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
import java.util.Collection;

import javax.servlet.jsp.JspException;

import com.jsmart5.framework.manager.SmartTagHandler;


public final class OutputsTagHandler extends SmartTagHandler {

	private Object value;

	private boolean breakline = true;

	private String marker;

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@SuppressWarnings("unchecked")
	@Override
	public void executeTag() throws JspException, IOException {

		StringBuilder builder = new StringBuilder();
	
		builder.append(HtmlConstants.OPEN_SPAN_TAG);

		if (id != null) {
			builder.append("id=\"" + id + "\" ");
		}
		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			builder.append(CssConstants.CSS_OUTPUTS);
		}

		builder.append(">");

		Object obj = getTagValue(value);
		
		if (obj != null) {
			if (obj instanceof Collection) {
				for (Object o : (Collection<Object>) obj) {
					builder.append(breakline ? HtmlConstants.OPEN_PARAGRAPH_TAG : "");
					builder.append((marker != null ? marker + "&nbsp" : "") + (o != null ? o.toString() : o) + "&nbsp");
					builder.append(breakline ? HtmlConstants.CLOSE_PARAGRAPH_TAG : "");
				}
			} else if (obj.getClass().isArray()) {
				for (Object o : (Object[]) obj) {
					builder.append(breakline ? HtmlConstants.OPEN_PARAGRAPH_TAG : "");
					builder.append((marker != null ? marker + "&nbsp" : "") + (o != null ? o.toString() : o) + "&nbsp");
					builder.append(breakline ? HtmlConstants.CLOSE_PARAGRAPH_TAG : "");
				}
			}
		}

		builder.append(HtmlConstants.CLOSE_SPAN_TAG);

		printOutput(builder);
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setBreakline(Boolean breakline) {
		this.breakline = breakline;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}

}
