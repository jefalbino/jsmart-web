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
import java.util.Arrays;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;


public final class WhenTagHandler extends TagHandler {

    private String access;

    private String deny;

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();

        if (parent instanceof AuthorizeTagHandler) {
            ((AuthorizeTagHandler) parent).addWhen(this);
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
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }
        return null;
    }

    List<String> getAccess() {
        return getAsList(access);
    }

    public void setAccess(String access) {
        this.access = access;
    }

    List<String> getDeny() {
        return getAsList(deny);
    }

    public void setDeny(String deny) {
        this.deny = deny;
    }

    private List<String> getAsList(String value) {
        Object object = getTagValue(value);
        if (object instanceof List) {
            return (List) object;

        } else if (object instanceof String) {
            String string = (String) object;

            if (!string.trim().isEmpty()) {
                return Arrays.asList(string.split(","));
            }
        }
        return null;
    }

}
