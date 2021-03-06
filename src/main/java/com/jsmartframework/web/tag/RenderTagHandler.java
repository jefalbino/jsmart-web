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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;

public final class RenderTagHandler extends TagHandler {

    private String test;

    @Override
    public void validateTag() throws JspException {
        // DO NOTHING
    }

    @Override
    public Tag executeTag() throws JspException, IOException {
        Boolean testValue;

        Object tagValue = getTagValue(test);
        if (tagValue instanceof Boolean) {
            testValue = (Boolean) tagValue;
        } else {
            testValue = Boolean.valueOf((String) tagValue);
        }

        if (testValue != null && testValue) {
            JspFragment body = getJspBody();
            if (body != null) {
                body.invoke(null);
            }
        }
        return null;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
