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
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.html.Tag;

public final class EditTagHandler extends SmartTagHandler {

	private static final String INPUT = "input";

	private static final String OUTPUT = "output";

	private String type;

	@Override
	public void validateTag() throws JspException {
		if (!type.equalsIgnoreCase(OUTPUT) && !type.equalsIgnoreCase(INPUT)) {
			throw new JspException("Invalid type for edititem tag. Valid values are input or output");
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
//		JspFragment body = getJspBody();
//		if (body != null) {
//			setEditRowTagEnable(true);
//
//			StringWriter sw = new StringWriter();
//			body.invoke(sw);
//
//			StringBuilder builder = new StringBuilder();
//
//			if (type.equalsIgnoreCase(OUTPUT)) {
//				builder.append(OPEN_DIV_TAG + "outputwrapper=\"\" >");
//				builder.append(sw);
//				builder.append(CLOSE_DIV_TAG);
//
//			} else if (type.equalsIgnoreCase(INPUT)) {
//				builder.append(OPEN_DIV_TAG + "inputwrapper=\"\" style=\"display: none;\" >");
//				builder.append(sw);
//				builder.append(CLOSE_DIV_TAG);
//			}
//
//			setEditRowTagEnable(false);
//			printOutput(builder);
//		}
		return null;
	}

	public void setType(String type) {
		this.type = type;
	}

}
