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
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Tag;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

public final class AccordionTagHandler extends TagHandler {

    private String var;

    private String values;

    private final List<PanelTagHandler> panels;

    public AccordionTagHandler() {
        panels = new ArrayList<PanelTagHandler>();
    }

    public void validateTag() throws JspException {
        if (values != null && id == null) {
            throw InvalidAttributeException.fromConflict("accordion", "id", "Attribute [id] must be specified case [values] is specified");
        }
        if (values != null && var == null) {
            throw InvalidAttributeException.fromConflict("accordion", "var", "Attribute [var] must be specified case [values] is specified");
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {
        if (values != null) {
            return iterableAccordion();
        } else {
            return basicAccordion();
        }
    }

    private Tag basicAccordion() throws JspException, IOException {
        setRandomId("accordion");

        StringWriter sw = new StringWriter();
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(sw);
        }

        Div div = new Div();
        div.addAttribute("id", id)
                .addAttribute("style", getTagValue(style))
                .addAttribute("class", Bootstrap.PANEL_GROUP)
                .addAttribute("class", getTagValue(styleClass))
                .addAttribute("role", "tablist")
                .addAttribute("aria-multiselectable", "true")
                .addText(sw.toString());

        appendEvent(div);
        return div;
    }

    private Tag iterableAccordion() throws JspException, IOException {
        // Need to indicate that it is a list parent tag for deep inner tags
        // so the ajax and bind actions can be set by this class
        pushDelegateTagParent();

        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        HttpServletRequest request = getRequest();

        Div div = new Div();
        div.addAttribute("id", id)
                .addAttribute("style", getTagValue(style))
                .addAttribute("class", Bootstrap.PANEL_GROUP)
                .addAttribute("class", getTagValue(styleClass))
                .addAttribute("role", "tablist")
                .addAttribute("aria-multiselectable", "true");

        appendEvent(div);

        Collection<?> collection = (Collection<?>) getTagValue(values);

        if (collection != null && !collection.isEmpty()) {
            Iterator<Object> iterator = (Iterator<Object>) collection.iterator();

            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj == null) {
                    continue;
                }
                request.setAttribute(var, obj);

                for (PanelTagHandler panel : panels) {
                    div.addTag(panel.executeTag());
                }
                request.removeAttribute(var);
            }

        } else if (emptyTag != null) {
            Div empty = new Div();
            empty.addAttribute("id", emptyTag.id)
                    .addAttribute("role-empty", "true")
                    .addAttribute("style", getTagValue(emptyTag.style))
                    .addAttribute("class", getTagValue(emptyTag.styleClass));

            empty.addText(emptyTag.getContent());
            div.addTag(empty);
        }

        // Needs to pop the iterator action so this class set the
        // ajax and bind actions carried via RefAction
        popDelegateTagParent();
        return div;
    }

    boolean hasValues() {
        return values != null;
    }

    void addPanel(PanelTagHandler panel) {
        panels.add(panel);
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setValues(String values) {
        this.values = values;
    }
}
