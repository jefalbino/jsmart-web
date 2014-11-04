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


public final class CheckboxTagHandler extends SmartTagHandler {

	private String value;

	private String label;

	private boolean disabled;

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
		// DO NOTHING
	}

	@Override
	public void executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		StringBuilder builder = new StringBuilder(OPEN_DIV_TAG);

		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CSS_CHECKBOX);
		}

		builder.append(CLOSE_TAG + CHECKBOX_TAG);

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
				builder.append(ajaxCommand.replace(ON_CLICK, ON_CLICK + JSMART_CHECKBOX.format("$(this)")));
			} else {
				builder.append(ajaxCommand + ON_CLICK + JSMART_CHECKBOX.format("$(this)") + "\" ");
			}
		} else {
			builder.append(ON_CLICK + JSMART_CHECKBOX.format("$(this)") + "\" ");
		}

		appendEvent(builder);

		builder.append(CLOSE_INLINE_TAG + OPEN_LABEL_TAG);

		builder.append("for=\"" + id + "\" ");

		builder.append(CLOSE_TAG + CLOSE_LABEL_TAG);

		builder.append(CLOSE_DIV_TAG);

		builder.append(OPEN_SPAN_TAG + CLOSE_TAG);

		builder.append(label != null ? "&nbsp; " + getTagValue(label) : "");

		builder.append(CLOSE_SPAN_TAG);

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
