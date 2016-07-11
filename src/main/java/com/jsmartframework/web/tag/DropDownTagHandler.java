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

import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.css.Bootstrap;
import com.jsmartframework.web.tag.html.A;
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Li;
import com.jsmartframework.web.tag.html.Span;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Align;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

public final class DropDownTagHandler extends TagHandler {

    private String label;

    private boolean navbar;

    private String caretClass;

    private DropMenuTagHandler dropMenu;

    @Override
    public void validateTag() throws JspException {
        // DO NOTHING
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        // Just to call nested tags
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        setRandomId("dropdown");

        Tag dropDown;
        if (navbar) {
            dropDown = new Li();
        } else {
            dropDown = new Div();
        }

        boolean disabled = isDisabled();

        dropDown.addAttribute("style", getTagValue(style))
            .addAttribute("class", Bootstrap.DROPDOWN)
            .addAttribute("class", disabled ? Bootstrap.DISABLED : null)
            .addAttribute("class", getTagValue(styleClass));

        appendRefId(dropDown, id);
        appendEvent(dropDown);

        A a = new A();

        for (IconTagHandler iconTag : iconTags) {
            if (Align.LEFT.equalsIgnoreCase(iconTag.getSide())) {
                a.addTag(iconTag.executeTag());
                a.addText(" ");
            }
        }

        String labelVal = (String) getTagValue(label);

        a.addAttribute("href", "#")
            .addAttribute("data-toggle", "dropdown")
            .addAttribute("aria-expanded", "false")
            .addAttribute("class", Bootstrap.DROPDOWN_TOGGLE)
            .addText(StringUtils.isNotBlank(labelVal) ? labelVal : "&zwnj;");

        for (IconTagHandler iconTag : iconTags) {
            if (Align.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
                a.addText(" ");
                a.addTag(iconTag.executeTag());
            }
        }

        if (dropMenu != null) {
            Span span = new Span();
            String caretName = (String) getTagValue(caretClass);
            if (StringUtils.isNotBlank(caretName)) {
                if (caretName.startsWith(Bootstrap.GLYPHICON)) {
                    span.addAttribute("class", Bootstrap.GLYPHICON);
                }
                span.addAttribute("class", caretName);
            } else {
                span.addAttribute("class", Bootstrap.CARET);
            }
            a.addText(" ");
            a.addTag(span);
        }

        dropDown.addTag(a);

        if (dropMenu != null) {
            Tag ul = dropMenu.executeTag();
            ul.addAttribute("class", disabled ? Bootstrap.DISABLED : null);
            dropDown.addTag(ul);
        }

        appendAjax(id);
        appendBind(id);

        return dropDown;
    }

    void setDropMenu(DropMenuTagHandler dropMenu) {
        this.dropMenu = dropMenu;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setNavbar(boolean navbar) {
        this.navbar = navbar;
    }

    public void setCaretClass(String caretClass) {
        this.caretClass = caretClass;
    }
}