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

import static com.jsmart5.framework.tag.js.JsConstants.*;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Tag;

public final class RestArrayTagHandler extends TagHandler {

	private static final String ADD_ARRAY_ITEM = "add";

	private static final String REMOVE_ARRAY_ITEM = "remove";

	private String align;

	private boolean dynamic;

	private Integer maxItems;

	@Override
	public void validateTag() throws JspException {
		if (dynamic && id == null) {
			throw new JspException("Attribute id must be specified case dynamic attribute is true for restarray tag");
		}
		if (maxItems != null && maxItems <= 0) {
			throw new JspException("Attribute maxItems must be greater than zero for restarray tag");
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
//
//		StringWriter sw = new StringWriter();
//		JspFragment body = getJspBody();
//		if (body != null) {
//			body.invoke(sw);
//		}
//
//		StringBuilder builder = new StringBuilder(OPEN_DIV_TAG);
//
//		builder.append("type=\"restarray\" ");
//
//		if (id != null) {
//			builder.append("id=\"" + id + "\" ");
//		}
//		if (style != null) {
//			builder.append("style=\"" + style + "\" ");
//		}
//		if (styleClass != null) {
//			builder.append("class=\"" + styleClass + "\" ");
//		}
//
//		if (align != null) {
//			builder.append("align=\"" + align + "\" ");
//		} else {
//			builder.append("align=\"left\" ");
//		}
//		if (maxItems != null) {
//			builder.append("maxItems=\"" + maxItems + "\" ");
//		}
//
//		appendRest(builder);
//		builder.append(">");
//		builder.append(sw);
//		builder.append(CLOSE_DIV_TAG);
//
//		if (dynamic) {
//			builder.append(OPEN_DIV_TAG);
//			appendClass(builder, CSS_REST_ARRAY_GROUP);
//			builder.append(">");
//			appendButton(builder, "+", ADD_ARRAY_ITEM);
//			appendButton(builder, "-", REMOVE_ARRAY_ITEM);
//			builder.append(CLOSE_DIV_TAG);
//		}
//
//		printOutput(builder);
		return null;
	}

	private void appendButton(StringBuilder builder, String text, String operation) throws JspException, IOException  {
//		builder.append(OPEN_BUTTON_TAG);
//		builder.append("type=\"button\" ");
//		appendClass(builder, CSS_REST_ARRAY_BUTTON);
//		builder.append(ON_CLICK + JSMART_BUTTON_RESTARRAY.format(id, operation) + "return false;\" >");
//		builder.append(text);
//		builder.append(CLOSE_BUTTON_TAG);
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public void setMaxItems(Integer maxItems) {
		this.maxItems = maxItems;
	}

}