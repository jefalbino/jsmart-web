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
import com.jsmartframework.web.tag.html.Li;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.html.Ul;
import com.jsmartframework.web.tag.type.Look;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;

public final class OutputListTagHandler extends TagHandler {

    private Object values;

    private String look;

    private boolean inline;

    @Override
    public void validateTag() throws JspException {
        if (look != null && !Look.validateText(look) && !isEL(look)) {
            throw InvalidAttributeException.fromPossibleValues("outputlist", "look", Look.getTextValues());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Tag executeTag() throws JspException, IOException {

        Ul ul = new Ul();
        ul.addAttribute("id", id)
            .addAttribute("style", getTagValue(style));

        String lookVal = (String) getTagValue(look);

        if (Look.PRIMARY.equalsIgnoreCase(lookVal)) {
            ul.addAttribute("class", Bootstrap.TEXT_PRIMARY);
        } else if (Look.SUCCESS.equalsIgnoreCase(lookVal)) {
            ul.addAttribute("class", Bootstrap.TEXT_SUCCESS);
        } else if (Look.INFO.equalsIgnoreCase(lookVal)) {
            ul.addAttribute("class", Bootstrap.TEXT_INFO);
        } else if (Look.WARNING.equalsIgnoreCase(lookVal)) {
            ul.addAttribute("class", Bootstrap.TEXT_WARNING);
        } else if (Look.DANGER.equalsIgnoreCase(lookVal)) {
            ul.addAttribute("class", Bootstrap.TEXT_DANGER);
        } else if (Look.MUTED.equalsIgnoreCase(lookVal)) {
            ul.addAttribute("class", Bootstrap.TEXT_MUTED);
        }

        ul.addAttribute("class", inline ? Bootstrap.LIST_INLINE : null);

        // Add the style class at last
        ul.addAttribute("class", getTagValue(styleClass));

        Object obj = getTagValue(values);
        if (obj != null) {
            if (obj instanceof Collection) {
                for (Object o : (Collection<Object>) obj) {
                    if (o != null) {
                        Li li = new Li();
                        li.addText(o.toString());
                        ul.addTag(li);
                    }
                }
            } else if (obj.getClass().isArray()) {
                for (Object o : (Object[]) obj) {
                    if (o != null) {
                        Li li = new Li();
                        li.addText(o.toString());
                        ul.addTag(li);
                    }
                }
            }
        }

        return ul;
    }

    public void setValues(Object values) {
        this.values = values;
    }

    public void setInline(boolean inline) {
        this.inline = inline;
    }

    public void setLook(String look) {
        this.look = look;
    }
}
