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

import static com.jsmart5.framework.tag.HtmlConstants.*;

public final class FormTagHandler extends SmartTagHandler {

	private String method;

	@Override
	public void validateTag() throws JspException {
		if (method != null && !method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("POST")) {
			throw new JspException("Invalid method for form tag. Valid value are get or post");
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		StringWriter sw = new StringWriter();
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}

		StringBuilder builder = new StringBuilder(OPEN_FORM_TAG);

		builder.append("id=\"" + id + "\" ");

		if (method != null) {
			builder.append("method=\"" + method + "\" ");
		} else {
			builder.append("method=\"GET\" ");
		}

		builder.append(ON_SUBMIT + "return " + JSConstants.JSMART_VALIDATE.format(id) + "\" >");
		builder.append(sw.toString());
		builder.append(CLOSE_FORM_TAG);

		printOutput(builder);
	}

	public void setMethod(String method) {
		this.method = method;
	}

}