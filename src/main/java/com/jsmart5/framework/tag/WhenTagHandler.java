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

import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public final class WhenTagHandler extends TagHandler {

	private String access;

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

	public void setAccess(String roles) {
		this.access = roles;
	}

	List<String> getAccess() {
		if (!access.trim().isEmpty()) {
			return Arrays.asList(access.split(","));
		}
		return null;
	}

}
