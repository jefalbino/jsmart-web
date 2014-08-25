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


public final class CheckboxTagHandler extends SmartTagHandler {

	private String value;

	private String label;

	private boolean disabled;

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

		StringBuilder builder = new StringBuilder(HtmlConstants.OPEN_DIV_TAG);

		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CssConstants.CSS_CHECKBOX);
		}

		builder.append(">" + HtmlConstants.CHECKBOX_TAG);

		builder.append("id=\"" + id + "\" ");

		String name = getTagName(J_TAG, value);
		if (name != null) {
			builder.append("name=\"" + name + "\" ");
		}

		appendFormValidator(builder);

		appendRest(builder);

		if (tabIndex != null) {
			builder.append("tabindex=\"" + tabIndex + "\" ");
		}
		if (disabled || isEditRowTagEnabled()) {
			builder.append("disabled=\"disabled\" ");
		}

		Boolean object = (Boolean) getTagValue(value);
		if (object != null) {
			builder.append("value=\"" + object + "\"" + (object ? " checked=\"true\"" : "") + " ");
		} else {
			builder.append("value=\"false\" ");
		}

		if (ajaxCommand != null) {
			if (ajaxCommand.startsWith(ON_CLICK)) {
				builder.append(ajaxCommand.replace(ON_CLICK, ON_CLICK + JSConstants.JSMART_CHECKBOX.format("$(this)")));
			} else {
				builder.append(ajaxCommand + ON_CLICK + JSConstants.JSMART_CHECKBOX.format("$(this)") + "\" ");
			}
		} else {
			builder.append(ON_CLICK + JSConstants.JSMART_CHECKBOX.format("$(this)") + "\" ");
		}

		appendEvent(builder);

		builder.append("/>" + HtmlConstants.OPEN_LABEL_TAG);

		builder.append("for=\"" + id + "\" ");

		builder.append(">" + HtmlConstants.CLOSE_LABEL_TAG);

		builder.append(HtmlConstants.CLOSE_DIV_TAG);

		builder.append(HtmlConstants.OPEN_SPAN_TAG + ">");

		builder.append(label != null ? "&nbsp; " + getTagValue(label) : "");

		builder.append(HtmlConstants.CLOSE_SPAN_TAG);

		printOutput(builder); 
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

}
