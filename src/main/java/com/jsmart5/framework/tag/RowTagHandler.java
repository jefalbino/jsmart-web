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

import com.jsmart5.framework.manager.SmartTagHandler;


public final class RowTagHandler extends SmartTagHandler {

	private String header;

	private String value;

	private String type;

	private String badge;

	@Override
	public void doTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof ListTagHandler) {
			((ListTagHandler) parent).addItem(this);
		}
	}

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING - type is internal
	}

	@Override
	public void executeTag() throws JspException, IOException {

		StringBuilder builder = new StringBuilder();

		if (type != null && type.equals(ListTagHandler.DEFINITION)) {
			builder.append(HtmlConstants.OPEN_DEFINITION_TITLE_TAG + (header != null ? getTagValue(header) : "") + HtmlConstants.CLOSE_DEFINITION_TITLE_TAG + HtmlConstants.OPEN_DEFINITION_DATA_TAG);
		} else {
			builder.append(HtmlConstants.OPEN_LIST_ITEM_TAG);
		}

		if (style != null) {
			builder.append("style=\"" + (ajaxCommand != null ? "cursor: pointer; " : "") + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CssConstants.CSS_LIST_ROW);
		}
		if (ajaxCommand != null) {
			builder.append((style == null ? "style=\"cursor: pointer;\" " : "") + ajaxCommand + "select=\"true\" ");
		}

		appendEvent(builder);

		builder.append(">");

		Object object = getTagValue(value);
		if (object != null) {
			builder.append(object);
		}

		if (badge != null) {
			object = getTagValue(badge);
			builder.append(HtmlConstants.OPEN_SPAN_TAG);
			appendClass(builder, CssConstants.CSS_LIST_ROW_BADGE);
			builder.append(">");
			builder.append(object != null ? object : "");
			builder.append(HtmlConstants.CLOSE_SPAN_TAG);
		}

		if (type != null && type.equals(ListTagHandler.DEFINITION)) {
			builder.append(HtmlConstants.CLOSE_DEFINITION_DATA_TAG);
		} else {
			builder.append(HtmlConstants.CLOSE_LIST_ITEM_TAG);
		}

		printOutput(builder);
	}

	/*package*/ void setType(String type) {
		this.type = type;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setBadge(String badge) {
		this.badge = badge;
	}

}
