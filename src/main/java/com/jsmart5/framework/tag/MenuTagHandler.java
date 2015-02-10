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
import static com.jsmart5.framework.tag.CssConstants.*;
import static com.jsmart5.framework.tag.JsConstants.*;

public final class MenuTagHandler extends SmartTagHandler {

	static final String MENU_TOP = "top";

	static final String MENU_LEFT = "left";

	static final String MENU_RIGHT = "right";

	static final String MENU_BOTTOM = "bottom";

	private String type;

	@Override
	public void validateTag() throws JspException {
		if (!type.equals(MENU_TOP) && !type.equals(MENU_LEFT) && !type.equals(MENU_BOTTOM) && !type.equals(MENU_RIGHT)) {
			throw new JspException("Invalid type value for menu tag. Valid values are " + MENU_TOP + ", " + MENU_LEFT 
					+ ", " + MENU_BOTTOM + ", " + MENU_RIGHT);
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		StringWriter sw = new StringWriter();
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}

		StringBuilder builder = new StringBuilder(OPEN_NAV_TAG + OPEN_UNORDERED_LIST_TAG + "id=\"" + id + "\" ");

		builder.append("menu=\"menu\" ");

		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			if (MENU_TOP.equals(type) || MENU_BOTTOM.equals(type)) {
				appendClass(builder, CSS_MENU_HORIZONTAL);
			} else {
				appendClass(builder, CSS_MENU_VERTICAL);
			}
		}

		appendEvent(builder);

		builder.append("type=\"" + type + "\" >");
		builder.append(sw);
		builder.append(CLOSE_UNORDERED_LIST_TAG + CLOSE_NAV_TAG);

		appendScriptDeprecated(new StringBuilder(JSMART_MENU.format(id)));

		printOutput(builder);
	}

	String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}