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
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Tag;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

public final class AccordionTagHandler extends TagHandler {

    private String onShow;

    private String onShown;

    private String onHide;

    private String onHidden;

    @Override
    public void validateTag() throws JspException {
        // DO NOTHING
    }

    @Override
    public Tag executeTag() throws JspException, IOException {
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
                .addAttribute("aria-multiselectable", "true");

        if (repeatTag != null) {
            repeatTag.setId(id);
            div.addTag(repeatTag.executeTag());
        } else {
            div.addText(sw.toString());
        }

        appendEvent(div);

        if (onShow != null) {
            appendDocScript(getBindFunction(id, "show.bs.collapse", new StringBuilder(onShow)));
        }
        if (onShown != null) {
            appendDocScript(getBindFunction(id, "shown.bs.collapse", new StringBuilder(onShown)));
        }
        if (onHide != null) {
            appendDocScript(getBindFunction(id, "hide.bs.collapse", new StringBuilder(onHide)));
        }
        if (onHidden != null) {
            appendDocScript(getBindFunction(id, "hidden.bs.collapse", new StringBuilder(onHidden)));
        }
        return div;
    }

    public void setOnShow(String onShow) {
        this.onShow = onShow;
    }

    public void setOnShown(String onShown) {
        this.onShown = onShown;
    }

    public void setOnHide(String onHide) {
        this.onHide = onHide;
    }

    public void setOnHidden(String onHidden) {
        this.onHidden = onHidden;
    }
}
