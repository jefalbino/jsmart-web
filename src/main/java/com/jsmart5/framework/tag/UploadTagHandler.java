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
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.SmartTagHandler;

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.CssConstants.*;
import static com.jsmart5.framework.tag.JsConstants.*;

public final class UploadTagHandler extends SmartTagHandler {

	private static final String FILE_TYPE = "file";

	private String value;

	private Long maxSize;

	private String label;

	private String maxSizeMessage;

	private Integer tabIndex;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof GridTagHandler) {

			((GridTagHandler) parent).addTag(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (maxSize != null && maxSize <= 0) {
			throw new JspException("Invalid maxSize value for upload tag. Value must be greater than zero");
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		// Hidden input must be included to be captured on request parameters		
		StringBuilder builder = new StringBuilder(INPUT_TAG);

		String name = getTagName(J_FILE, value);

		builder.append("name=\"" + name + "\" type=\"hidden\" />");

		if (label != null) {
			builder.append(OPEN_DIV_TAG);
			appendClass(builder, CSS_INPUT_GROUP);
			builder.append(">");
			builder.append(OPEN_SPAN_TAG);
			appendClass(builder, CSS_INPUT_LABEL_UPLOAD);
			builder.append(">");

			String labelVal = (String) getTagValue(label);
			if (labelVal != null) {
				builder.append(labelVal);
			}
			builder.append(CLOSE_SPAN_TAG);
		}

		builder.append(INPUT_TAG);

		builder.append("id=\"" + id + "\" ");

		if (name != null) {
			builder.append("name=\"" + name.replace(J_FILE, J_PART) + "\" ");
		}

		appendFormValidator(builder);

		if (maxSize != null) {
			builder.append(ON_CHANGE + JSMART_UPLOAD.format(id) + "\" maxFileSize=\"" + maxSize + "\" ");
		}

		if (maxSizeMessage != null) {
			builder.append("maxSizeMsg=\"" + getTagValue(maxSizeMessage) + "\" ");
		}

		builder.append("type=\"" + FILE_TYPE + "\" ");

		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CSS_UPLOAD);
		}
		if (tabIndex != null) {
			builder.append("tabindex=\"" + tabIndex + "\" ");
		}

		appendEvent(builder);

		builder.append("/>");

		if (label != null) {
			builder.append(CLOSE_DIV_TAG);
		}

		printOutput(builder);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setMaxSize(Long maxSize) {
		this.maxSize = maxSize;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public void setMaxSizeMessage(String maxSizeMessage) {
		this.maxSizeMessage = maxSizeMessage;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

}
