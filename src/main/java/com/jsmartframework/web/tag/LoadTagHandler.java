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
import com.jsmartframework.web.tag.css.JSmart;
import com.jsmartframework.web.tag.html.Span;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Output;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class LoadTagHandler extends TagHandler {

    private String icon;

    private String label;

    private String type;

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();
        if (parent instanceof AutoCompleteTagHandler) {
            ((AutoCompleteTagHandler) parent).setLoadTag(this);
            return false;

        } else if (parent instanceof ButtonTagHandler) {
            ((ButtonTagHandler) parent).setLoadTag(this);
            return false;

        } else if (parent instanceof LinkTagHandler) {
            ((LinkTagHandler) parent).setLoadTag(this);
            return false;

        } else if (parent instanceof ListTagHandler) {
            ((ListTagHandler) parent).setLoadTag(this);
            return false;

        } else if (parent instanceof TableTagHandler) {
            ((TableTagHandler) parent).setLoadTag(this);
            return false;
        }
        return super.beforeTag();
    }

    @Override
    public void validateTag() throws JspException {
        if (type != null && !Output.validateHeader(type)) {
            throw InvalidAttributeException.fromPossibleValues("load", "type", Output.getHeaderValues());
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        setRandomId("load");

        Tag header = null;
        if (type != null) {
            header = new Tag(type);
        } else {
            header = new Tag(Output.H3.name().toLowerCase());
        }

        header.addAttribute("style", getTagValue(style))
            .addAttribute("class", getTagValue(styleClass));

        appendRefId(header, id);

        Span span = new Span();
        span.addAttribute("role-load-content", "")
            .addAttribute("class", JSmart.ICON)
            .addAttribute("class", Bootstrap.GLYPHICON)
            .addAttribute("class", Bootstrap.GLYPHICON_ANIMATE)
            .addAttribute("aria-hidden", "true");

        if (icon != null) {
            span.addAttribute("class", getTagValue(icon));
        } else {
            span.addAttribute("class", Bootstrap.GLYPHICON_REFRESH);
        }

        header.addTag(span);

        if (label != null) {
            header.addText(" ")
                .addText(getTagValue(label));
        }

        appendAjax(id);
        appendBind(id);

        return header;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setType(String type) {
        this.type = type;
    }

}
