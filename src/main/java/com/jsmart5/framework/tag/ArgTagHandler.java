/*
 * JSmart5 - Java Web Development Framework
 * Copyright (c) 2014, Jeferson Albino da Silva, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
*/

package com.jsmart5.framework.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Tag;

public final class ArgTagHandler extends TagHandler {

	private Object value;

    private String bindTo;

	@Override
	public void validateTag() throws JspException {
		if (value == null && (bindTo == null || bindTo.trim().isEmpty())) {
            throw InvalidAttributeException.fromConflict("arg", "value", "Attribute [value] must be specified");
        }
	}

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof TagHandler) {
			((TagHandler) parent).addArg(getTagValue(value), (String) getTagValue(bindTo));
		}
		return false;
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
		// DO NOTHING
		return null;
	}

	public void setValue(Object value) {
		this.value = value;
	}

    public void setBindTo(String bindTo) {
        this.bindTo = bindTo;
    }
}
