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
import com.jsmartframework.web.tag.html.A;
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Look;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class PanelTagHandler extends TagHandler {

    private String look;

    private HeaderTagHandler header;

    private FooterTagHandler footer;

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();
        if (parent instanceof AccordionTagHandler && ((AccordionTagHandler) parent).hasValues()) {
            ((AccordionTagHandler) parent).addPanel(this);
            return false;
        }
        return true;
    }

    public void validateTag() throws JspException {
        if (look != null && !Look.validateLook(look) && !isEL(look)) {
            throw InvalidAttributeException.fromPossibleValues("panel", "look", Look.getLookValues());
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {
        String parentId = null;
        String contentId = null;

        JspTag parent = getParent();
        if (parent instanceof AccordionTagHandler) {
            parentId = ((AccordionTagHandler) parent).getId();
            contentId = getRandomId();
        }

        StringWriter sw = new StringWriter();
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(sw);
        }

        setRandomId("panel");

        Div panel = new Div();
        panel.addAttribute("id", id)
            .addAttribute("style", getTagValue(style))
            .addAttribute("class", Bootstrap.PANEL);

        String lookVal = (String) getTagValue(look);

        if (Look.PRIMARY.equalsIgnoreCase(lookVal)) {
            panel.addAttribute("class", Bootstrap.PANEL_PRIMARY);
        } else if (Look.SUCCESS.equalsIgnoreCase(lookVal)) {
            panel.addAttribute("class", Bootstrap.PANEL_SUCCESS);
        } else if (Look.INFO.equalsIgnoreCase(lookVal)) {
            panel.addAttribute("class", Bootstrap.PANEL_INFO);
        } else if (Look.WARNING.equalsIgnoreCase(lookVal)) {
            panel.addAttribute("class", Bootstrap.PANEL_WARNING);
        } else if (Look.DANGER.equalsIgnoreCase(lookVal)) {
            panel.addAttribute("class", Bootstrap.PANEL_DANGER);
        } else {
            panel.addAttribute("class", Bootstrap.PANEL_DEFAULT);
        }

        // Add the style class at last
        panel.addAttribute("class", getTagValue(styleClass));

        appendEvent(panel);

        if (header != null || parentId != null) {
            Div head = new Div();
            head.addAttribute("class", Bootstrap.PANEL_HEADING);

            if (parentId != null) {
                A a = new A();
                a.addAttribute("data-toggle", "collapse")
                    .addAttribute("data-parent", "#" + parentId)
                    .addAttribute("href", "#" + contentId)
                    .addAttribute("aria-expanded", "false")
                    .addAttribute("aria-controls", contentId);

                if (header != null) {
                    a.addTag(header.executeTag());
                }
                head.addTag(a);
            } else {
                head.addTag(header.executeTag());
            }
            panel.addTag(head);
        }

        if (contentId != null) {
            Div content = new Div();
            content.addAttribute("id", contentId)
                .addAttribute("class", Bootstrap.PANEL_COLLPASE)
                .addAttribute("class", Bootstrap.COLLAPSE)
                .addAttribute("role", "tabpanel");

            content.addText(sw.toString());
            panel.addTag(content);
        } else {
            panel.addText(sw.toString());
        }

        if (footer != null) {
            panel.addTag(footer.executeTag());
        }
        return panel;
    }

    void setHeader(HeaderTagHandler header) {
        this.header = header;
    }

    void setFooter(FooterTagHandler footer) {
        this.footer = footer;
    }

    public void setLook(String look) {
        this.look = look;
    }

}
