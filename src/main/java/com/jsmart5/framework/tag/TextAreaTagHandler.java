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

import com.jsmart5.framework.manager.SmartTagHandler;


public final class TextAreaTagHandler extends SmartTagHandler {

	private Integer rows;

	private Integer cols;

	private Integer length;

	private String value;

	private boolean readOnly;

	private boolean disabled;

	private String placeHolder;

	private Integer tabIndex;

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public void executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		StringBuilder builder = new StringBuilder(HtmlConstants.OPEN_TEXT_AREA_TAG);

		if (id != null) {
			builder.append("id=\"" + id + "\" ");
		}
		String name = getTagName(J_TAG, value) + (readOnly ? EL_PARAM_READ_ONLY : "");
		if (name != null) {
			builder.append("name=\"" + name + "\" ");
		}

		appendFormValidator(builder);
		
		appendRest(builder);

		if (rows != null) {
			builder.append("rows=\"" + rows + "\" ");
		}
		if (cols != null) {
			builder.append("cols=\"" + cols + "\" ");
		}
		if (length != null) {
			builder.append("maxlength=\"" + length + "\" ");
		}
		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CssConstants.CSS_TEXTAREA);
		}
		if (tabIndex != null) {
			builder.append("tabindex=\"" + tabIndex + "\" ");
		}
		if (readOnly) {
			builder.append("readonly=\"true\" ");
		}
		if (disabled || isEditRowTagEnabled()) {
			builder.append("disabled=\"disabled\" ");
		}
		if (placeHolder != null) {
			builder.append("placeholder=\"" + getResourceString(placeHolder) + "\" "); 
		}

		appendEvent(builder);

		builder.append(">");

		Object object = getTagValue(value);

		if (object != null) {
			builder.append(object.toString());
		}

		printOutput(builder.append(HtmlConstants.CLOSE_TEXT_AREA_TAG));
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public void setCols(Integer cols) {
		this.cols = cols;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

}
