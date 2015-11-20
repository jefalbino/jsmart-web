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
import com.jsmartframework.web.tag.html.Tag;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class EmptyTagHandler extends TagHandler {

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();
        if (parent instanceof ListTagHandler) {
            ((ListTagHandler) parent).setEmptyTag(this);

        } else if (parent instanceof TableTagHandler) {
            ((TableTagHandler) parent).setEmptyTag(this);

        } else if (parent instanceof AccordionTagHandler) {
            ((AccordionTagHandler) parent).setEmptyTag(this);
        }
        return false;
    }

    @Override
    public void validateTag() throws JspException {
        // DO NOTHING
    }

    @Override
    public Tag executeTag() throws JspException, IOException {
        // DO NOTHING
        return null;
    }

    StringWriter getContent() throws JspException, IOException {
        StringWriter sw = new StringWriter();
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(sw);
        }
        return sw;
    }

}
