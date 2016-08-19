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
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Set;
import com.jsmartframework.web.tag.html.Tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class RepeatTagHandler extends TagHandler {

    private String var;

    private String values;

    private List<TagHandler> tags = new ArrayList<>();

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();
        if (parent instanceof AccordionTagHandler || parent instanceof DropMenuTagHandler) {
            ((TagHandler) parent).setRepeatTag(this);
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
        // Need to indicate that it is a list parent tag for deep inner tags
        // so the ajax and bind actions can be set by this class
        pushDelegateTagParent();

        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        Set set = new Set();
        HttpServletRequest request = getRequest();

        Collection<?> collection = (Collection<?>) getTagValue(values);

        if (collection != null && !collection.isEmpty()) {
            Iterator<Object> iterator = (Iterator<Object>) collection.iterator();

            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj == null) {
                    continue;
                }
                request.setAttribute(var, obj);

                for (TagHandler tag : tags) {
                    tag.clearTagParameters();
                    set.addTag(tag.executeTag());
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
            set.addTag(empty);
        }

        // Needs to pop the iterator action so this class set the
        // ajax and bind actions carried via RefAction
        popDelegateTagParent();
        return set;
    }

    public void addTag(TagHandler tag) {
        tags.add(tag);
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setValues(String values) {
        this.values = values;
    }
}
