/*
 * JSmart Framework - Java Web Development Framework
 * Copyright (c) 2015, Jeferson Albino da Silva, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
*/

package com.jsmartframework.web.tag;

import com.jsmartframework.web.exception.InvalidAttributeException;
import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.css.Bootstrap;
import com.jsmartframework.web.tag.html.FigCaption;
import com.jsmartframework.web.tag.html.Figure;
import com.jsmartframework.web.tag.html.Image;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Align;
import com.jsmartframework.web.tag.type.Type;
import com.jsmartframework.web.util.WebImage;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class ImageTagHandler extends TagHandler {

    private String lib;

    private String name;

    private String alt;

    private String width;

    private String height;

    private String side = Align.LEFT.name().toLowerCase();

    private String caption;

    private boolean figure;

    private String type;

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();

        if (parent instanceof ButtonTagHandler) {
            ((ButtonTagHandler) parent).addImageTag(this);
            return false;

        } else if (parent instanceof LinkTagHandler) {
            ((LinkTagHandler) parent).addImageTag(this);
            return false;

        } else if (parent instanceof DropDownTagHandler) {
            ((DropDownTagHandler) parent).addImageTag(this);
            return false;

        } else if (parent instanceof DropActionTagHandler) {
            ((DropActionTagHandler) parent).addImageTag(this);
            return false;

        } else if (parent instanceof HeaderTagHandler) {
            ((HeaderTagHandler) parent).addImageTag(this);
            return false;

        } else if (parent instanceof TabPaneTagHandler) {
            TabPaneTagHandler tabPaneTag = (TabPaneTagHandler) parent;

            // Consider only the first child image tag as tabpane image,
            // otherwise let it be executed as any other tabpane content
            if (tabPaneTag.getImageTags().isEmpty()) {
                tabPaneTag.addImageTag(this);
                return false;
            }
        }
        return super.beforeTag();
    }

    @Override
    public void validateTag() throws JspException {
        if (type != null && !Type.validateImage(type)) {
            throw InvalidAttributeException.fromPossibleValues("image", "type", Type.getImageValues());
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        setRandomId("image");

        String libValue = (String) getTagValue(lib);
        String nameValue = (String) getTagValue(name);

        Image image = new Image();
        image.addAttribute("style", getTagValue(style))
            .addAttribute("width", width)
            .addAttribute("height", height);

        appendRefId(image, id);

        if (libValue != null) {
            image.addAttribute("src", WebImage.getImage(libValue, nameValue));
        } else {
            image.addAttribute("src", nameValue);
        }

        if (alt != null) {
            image.addAttribute("alt", getTagValue(alt));
        } else {
            image.addAttribute("alt", nameValue);
        }

        if (Type.RESPONSIVE.equalsIgnoreCase(type)) {
            image.addAttribute("class", Bootstrap.IMAGE_RESPONSIVE);
        } else if (Type.ROUND.equalsIgnoreCase(type)) {
            image.addAttribute("class", Bootstrap.IMAGE_ROUNDED);
        } else if (Type.CIRCLE.equalsIgnoreCase(type)) {
            image.addAttribute("class", Bootstrap.IMAGE_CIRCLE);
        } else if (Type.THUMBNAIL.equalsIgnoreCase(type)) {
            image.addAttribute("class", Bootstrap.IMAGE_THUMBNAIL);
        }

        // Add the style class at last
        image.addAttribute("class", getTagValue(styleClass));

        appendEvent(image);

        appendAjax(id);
        appendBind(id);

        appendTooltip(image);
        appendPopOver(image);

        if (figure) {
            Figure fig = new Figure();
            fig.addTag(image);

            if (caption != null) {
                FigCaption figCaption = new FigCaption();
                figCaption.addText((String) getTagValue(caption));
                fig.addTag(figCaption);
            }
            return fig;
        }

        return image;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setFigure(boolean figure) {
        this.figure = figure;
    }

    public void setType(String type) {
        this.type = type;
    }

}
