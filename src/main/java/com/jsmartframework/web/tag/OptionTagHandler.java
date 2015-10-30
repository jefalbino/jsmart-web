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

import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.html.Option;
import com.jsmartframework.web.tag.html.Tag;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

public final class OptionTagHandler extends TagHandler {

    private String name;

    private String label;

    private Object value;

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();
        if (parent instanceof SelectTagHandler) {

            ((SelectTagHandler) parent).addOption(this);
            return false;
        }
        return true;
    }

    @Override
    public void validateTag() throws JspException {
        // DO NOTHING
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        Option option = new Option();
        option.addAttribute("id", id)
            .addAttribute("style", getTagValue(style))
            .addAttribute("class", getTagValue(styleClass))
            .addAttribute("disabled", isDisabled() ? "disabled" : null)
                .addText(getTagValue(label));

        Object object = getTagValue(value);
        option.addAttribute("value", object)
            .addAttribute("selected", verifySelection(object) ? "selected" : null);

        return option;
    }

    @SuppressWarnings("rawtypes")
    private boolean verifySelection(Object value) {
        // Get selected values
        Object values = getTagValue(name);

        if (values != null && value != null) {
            if (values instanceof Collection) {
                for (Object obj : (Collection) values) {
                    if (obj != null && obj.toString().equals(value.toString())) {
                        return true;
                    }
                }
            } else {
                return values.equals(value);
            }
        }

        return false;
    }

    void setName(String name) {
        this.name = name;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
