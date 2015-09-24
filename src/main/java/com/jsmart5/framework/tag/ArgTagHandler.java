/*
 * JSmart5 - Java Web Development Framework
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

package com.jsmart5.framework.tag;

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;
import java.io.IOException;

public final class ArgTagHandler extends TagHandler {

    private String name;

	private Object value;

    private String bindTo;

	@Override
	public void validateTag() throws JspException {
		if (value == null && (bindTo == null || bindTo.trim().isEmpty())) {
            if (getParent() instanceof FunctionTagHandler) {
                if (name == null || name.trim().isEmpty()) {
                    throw InvalidAttributeException.fromConflict("arg", "name", "Attribute [name] must be specified for function arguments");
                }
            } else {
                throw InvalidAttributeException.fromConflict("arg", "value", "Attribute [value] must be specified");
            }
        }
	}

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof FunctionTagHandler && value == null && (bindTo == null || bindTo.trim().isEmpty())) {
            FunctionTagHandler funcTagHandler = ((FunctionTagHandler) parent);
            String nameVal = (String) getTagValue(name);
            funcTagHandler.addArg(nameVal, null);
            funcTagHandler.appendFunctionArg(nameVal);
		} else {
            ((TagHandler) parent).addArg(getTagValue(value), (String) getTagValue(bindTo));
        }
		return false;
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
		// DO NOTHING
		return null;
	}

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Object value) {
		this.value = value;
	}

    public void setBindTo(String bindTo) {
        this.bindTo = bindTo;
    }
}
