/*
 * JSmart Framework - Java Web Development Framework
 * Copyright (c) 2014, Jeferson Albino da Silva, All rights reserved.
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
import com.jsmartframework.web.tag.css.JSmart;
import com.jsmartframework.web.tag.html.Span;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Align;
import com.jsmartframework.web.tag.type.Look;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class IconTagHandler extends TagHandler {

    private String name;

    private String side = Align.LEFT.name().toLowerCase();

    private String look;

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();

        if (parent instanceof ButtonTagHandler) {
            ((ButtonTagHandler) parent).addIconTag(this);
            return false;

        } else if (parent instanceof LinkTagHandler) {
            ((LinkTagHandler) parent).addIconTag(this);
            return false;

        } else if (parent instanceof DropDownTagHandler) {
            ((DropDownTagHandler) parent).addIconTag(this);
            return false;

        } else if (parent instanceof DropActionTagHandler) {
            ((DropActionTagHandler) parent).addIconTag(this);
            return false;

        } else if (parent instanceof HeaderTagHandler) {
            ((HeaderTagHandler) parent).addIconTag(this);
            return false;

        } else if (parent instanceof TabPaneTagHandler) {
            TabPaneTagHandler tabPaneTag = (TabPaneTagHandler) parent;

            // Consider only the first child icon tag as tabpane icon,
            // otherwise let it be executed as any other tabpane content
            if (tabPaneTag.getIconTags().isEmpty()) {
                tabPaneTag.addIconTag(this);
                return false;
            }
        }
        return true;
    }

    @Override
    public void validateTag() throws JspException {
        if (side != null && !Align.validateLeftRight(side)) {
            throw InvalidAttributeException.fromPossibleValues("icon", "side", Align.getLeftRightValues());
        }
        if (look != null && !Look.validateLook(look) && !isEL(look)) {
            throw InvalidAttributeException.fromPossibleValues("icon", "look", Look.getLookValues());
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        setRandomId("icon");

        Span span = new Span();
        span.addAttribute("class", JSmart.ICON);

        String iconName = (String) getTagValue(name);
        if (iconName != null && iconName.startsWith(Bootstrap.GLYPHICON)) {
            span.addAttribute("class", Bootstrap.GLYPHICON);
        }

        span.addAttribute("class", iconName)
            .addAttribute("style", getTagValue(style))
            .addAttribute("aria-hidden", "true")
            .addAttribute("side", side);

        appendRefId(span, id);
        appendEvent(span);

        String lookVal = (String) getTagValue(look);

        if (Look.PRIMARY.equalsIgnoreCase(lookVal)) {
            span.addAttribute("class", Bootstrap.TEXT_PRIMARY);
        } else if (Look.SUCCESS.equalsIgnoreCase(lookVal)) {
            span.addAttribute("class", Bootstrap.TEXT_SUCCESS);
        } else if (Look.INFO.equalsIgnoreCase(lookVal)) {
            span.addAttribute("class", Bootstrap.TEXT_INFO);
        } else if (Look.WARNING.equalsIgnoreCase(lookVal)) {
            span.addAttribute("class", Bootstrap.TEXT_WARNING);
        } else if (Look.DANGER.equalsIgnoreCase(lookVal)) {
            span.addAttribute("class", Bootstrap.TEXT_DANGER);
        }

        // At last place the style class
        span.addAttribute("class", getTagValue(styleClass));

        appendAjax(id);
        appendBind(id);

        appendTooltip(span);
        appendPopOver(span);

        return span;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public void setLook(String look) {
        this.look = look;
    }
}
