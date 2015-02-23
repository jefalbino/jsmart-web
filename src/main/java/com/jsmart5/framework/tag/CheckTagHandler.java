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

import com.jsmart5.framework.json.JsonAjax;
import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.manager.SmartValidateTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Input;
import com.jsmart5.framework.tag.html.Label;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Event;

import static com.jsmart5.framework.tag.js.JsConstants.*;

public final class CheckTagHandler extends SmartTagHandler {

	static final String CHECKBOX = "checkbox";

	static final String RADIO = "radio";

	private Object value;

	private String label;
	
	private boolean disabled;

	private String type;

	private String name;

	private boolean ajax;
	
	private boolean inline;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();

		if (parent instanceof RadioGroupTagHandler) {
			((RadioGroupTagHandler) parent).addCheck(this);
			return false;

		} else if (parent instanceof CheckGroupTagHandler) {
			((CheckGroupTagHandler) parent).addCheck(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING - type is internal
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		Div div = null;

		Label lb = new Label();
		lb.addAttribute("style", style);
		
		if (inline) {
			lb.addAttribute("class", CHECKBOX.equals(type) ? Bootstrap.CHECKBOX_INLINE : Bootstrap.RADION_INLINE);
		} else {
			div = new Div();
			div.addAttribute("class", CHECKBOX.equals(type) ? Bootstrap.CHECKBOX : Bootstrap.RADIO)
				.addAttribute("disabled", disabled ? "disabled" : null)
				.addTag(lb);
		}

		lb.addAttribute("class", styleClass);

		Input input = new Input();
		input.addAttribute("id", id)
			.addAttribute("type", type)
			.addAttribute("disabled", disabled ? "disabled" : null);
		
		if (CHECKBOX.equals(type)) {
			input.addAttribute("checkgroup", "checkgroup");
		} else if (RADIO.equals(type)) {
			input.addAttribute("radiogroup", "radiogroup");
		}

		String name = getTagName((type == null || type.equals(RADIO) ? J_TAG : J_ARRAY), 
				this.name != null ? this.name : (value != null ? value.toString() : null));
		if (name != null) {
			input.addAttribute("name", name);
		}

		appendValidator(input);
		appendRest(input);
		appendEvent(input);

		Object object = getTagValue(value);
		input.addAttribute("value", object)
			.addAttribute("checked", verifyCheck(object) ? "checked" : null);
		
		lb.addTag(input).addText(getTagValue(label));

		appendAjax(id);
		appendBind(id);

		if (ajax) {
			appendScript(getFunction());
		}

		return div != null ? div : lb;
	}

	private StringBuilder getFunction() {
		JsonAjax jsonAjax = new JsonAjax();
		jsonAjax.setId(id);
		jsonAjax.setMethod("post");

		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_CHECK.format(getJsonValue(jsonAjax)));
		return getBindFunction(id, Event.CLICK.name(), builder);
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

	void setType(String type) {
		this.type = type;
	}

	void setName(String name) {
		this.name = name;
	}
	
	void setInline(boolean inline) {
		this.inline = inline;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	void setValidator(SmartValidateTagHandler validator) {
		this.validator = validator;
	}

}
