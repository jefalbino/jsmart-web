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
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.manager.SmartValidateTagHandler;

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.CssConstants.*;
import static com.jsmart5.framework.tag.JsConstants.*;


public final class GroupItemTagHandler extends SmartTagHandler {

	static final String CHECKBOX = "checkbox";

	static final String RADIO = "radio";

	private String itemId;

	private Object value;

	private String label;

	private String type;

	private String name;

	private boolean ajax;

	private boolean async;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();

		if (parent instanceof RadioGroupTagHandler) {
			((RadioGroupTagHandler) parent).addItem(this);
			return false;

		} else if (parent instanceof CheckGroupTagHandler) {
			((CheckGroupTagHandler) parent).addItem(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING - type is internal
	}

	@Override
	public void executeTag() throws JspException, IOException {

		StringBuilder builder = new StringBuilder(OPEN_DIV_TAG);

		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			if (CHECKBOX.equals(type)) {
				appendClass(builder, CSS_CHECKGROUP_ITEM);

			} else if (RADIO.equals(type)) {
				appendClass(builder, CSS_RADIOGROUP_ITEM);
			}
		}

		builder.append(">" + INPUT_TAG);

		builder.append("id=\"" + itemId + "\" ");

		if (CHECKBOX.equals(type)) {
			builder.append("checkgroup=\"checkgroup\" ");

		} else if (RADIO.equals(type)) {
			builder.append("radiogroup=\"radiogroup\" ");
		}

		String name = getTagName((type == null || type.equals(RADIO) ? J_TAG : J_ARRAY), this.name != null ? this.name : (value != null ? value.toString() : null));
		if (name != null) {
			builder.append("name=\"" + name + "\" ");
		}

		appendFormValidator(builder);

		appendRest(builder);

		if (type != null) {
			builder.append("type=\"" + type +"\" ");
		}

		Object object = getTagValue(value);
		if (object != null) {
			builder.append("value=\"" + object + "\" ");
		}

		if (verifyCheck(object)) {
			builder.append("checked=\"checked\" ");
		}

		String command = ajaxCommand;

		if (ajax) {
			if (command != null) {
				if (command.startsWith(ON_CLICK)) {
					if (!command.contains(JSMART_AJAX.toString())) {
						command = command.replace(ON_CLICK, ON_CLICK + JSMART_GROUPITEM.format(async, name, "$(this)"));
					}
				} else {
					command += ON_CLICK + JSMART_GROUPITEM.format(async, name, "$(this)") + "\" ";
				}
			} else {
				command = ON_CLICK + JSMART_GROUPITEM.format(async, name, "$(this)") + "\" ";
			}
		}

		if (command != null) {
			builder.append(command);
		}

		appendEvent(builder);

		builder.append("/>" + OPEN_LABEL_TAG);

		builder.append("for=\"" + itemId + "\" ");

		builder.append(">" + CLOSE_LABEL_TAG);

		builder.append(CLOSE_DIV_TAG);

		printOutput(builder.append("&nbsp; " + getTagValue(label)));
	}

	@SuppressWarnings("rawtypes")
	private boolean verifyCheck(Object value) {
		// Get selected values
		Object values = getTagValue(name);

		if (values != null && value != null) {
			if (values instanceof Collection) {
				for (Object obj : (Collection) values) {
					if (obj != null && obj.toString().equals(value.toString())) {
						return true;
					}
				}
			} else {
				return values.equals(value);
			}
		}
		return false;
	}

	void setItemId(String itemId) {
		this.itemId = itemId;
	}

	void setType(String type) {
		this.type = type;
	}

	void setName(String name) {
		this.name = name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	void setAsync(boolean async) {
		this.async = async;
	}

	void setValidator(SmartValidateTagHandler validator) {
		this.validator = validator;
	}

}
