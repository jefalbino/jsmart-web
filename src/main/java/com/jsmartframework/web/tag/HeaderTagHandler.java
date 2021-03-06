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
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Align;
import com.jsmartframework.web.tag.type.Output;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class HeaderTagHandler extends TagHandler {

    private String title;

    private String type;

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();

        if (parent instanceof ModalTagHandler) {
            ((ModalTagHandler) parent).setHeader(this);
            return false;

        } else if (parent instanceof RowTagHandler) {
            ((RowTagHandler) parent).setHeader(this);
            return false;

        } else if (parent instanceof PanelTagHandler) {
            ((PanelTagHandler) parent).setHeader(this);
            return false;

        } else if (parent instanceof AlertTagHandler) {
            ((AlertTagHandler) parent).setHeader(this);
            return false;

        } else if (parent instanceof SlideTagHandler) {
            ((SlideTagHandler) parent).setHeader(this);
            return false;
        }
        return super.beforeTag();
    }

    @Override
    public void validateTag() throws JspException {
        if (type != null && !Output.validateHeader(type)) {
            throw InvalidAttributeException.fromPossibleValues("header", "type", Output.getHeaderValues());
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        JspTag parent = getParent();

        StringWriter sw = new StringWriter();
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(sw);
        }

        setRandomId("header");

        Tag header = null;
        if (type != null) {
            header = new Tag(type);
        }

        if (parent instanceof RowTagHandler) {
            if (header == null) {
                header = new Tag(Output.H4.name().toLowerCase());
            }
            header.addAttribute("class", Bootstrap.LIST_GROUP_ITEM_HEADING);

        } else if (parent instanceof PanelTagHandler) {
            if (header == null) {
                header = new Tag(Output.H3.name().toLowerCase());
            }
            header.addAttribute("class", Bootstrap.PANEL_TITLE);

        } else if (parent instanceof ModalTagHandler) {
            if (header == null) {
                header = new Tag(Output.H4.name().toLowerCase());
            }
            header.addAttribute("class", Bootstrap.MODAL_TITLE);

        } else if (parent instanceof AlertTagHandler) {
            if (header == null) {
                header = new Tag(Output.H4.name().toLowerCase());
            }
        } else if (header == null) {
            header = new Tag(Output.H3.name().toLowerCase());
        }

        header.addAttribute("style", getTagValue(style))
            .addAttribute("class", getTagValue(styleClass));

        appendRefId(header, id);

        for (IconTagHandler iconTag : iconTags) {
            if (Align.LEFT.equalsIgnoreCase(iconTag.getSide())) {
                header.addTag(iconTag.executeTag());
                header.addText(" ");
            }
        }

        for (ImageTagHandler imageTag : imageTags) {
            if (Align.LEFT.equalsIgnoreCase(imageTag.getSide())) {
                header.addTag(imageTag.executeTag());
                header.addText(" ");
            }
        }

        header.addText(getTagValue(title));

        for (IconTagHandler iconTag : iconTags) {
            if (Align.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
                header.addText(" ");
                header.addTag(iconTag.executeTag());
            }
        }

        for (ImageTagHandler imageTag : imageTags) {
            if (Align.RIGHT.equalsIgnoreCase(imageTag.getSide())) {
                header.addText(" ");
                header.addTag(imageTag.executeTag());
            }
        }

        // Add any other content inside header tag
        header.addText(sw);

        if (parent instanceof TagHandler && getMappedValue(DELEGATE_TAG_PARENT) == null) {
            String tagId = ((TagHandler) parent).getId();
            appendAjax(tagId);
            appendBind(tagId);
        } else {
            appendAjax(id);
            appendBind(id);
        }

        return header;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

}
