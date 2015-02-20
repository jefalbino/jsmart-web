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
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Input;
import com.jsmart5.framework.tag.html.Label;

import static com.jsmart5.framework.tag.js.JsConstants.*;

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
		
		if (id == null) {
			id = getRandonId();
		}

		Input input = new Input();
		input.addAttribute("id", id)
			.addAttribute("type", "checkbox")
			.addAttribute("name", getTagName(J_TAG, value))
			.addAttribute("tabindex", tabIndex)
			.addAttribute("disabled", disabled || isEditRowTagEnabled() ? "disabled" : null);

		Label lb = new Label();
		lb.addTag(input)
			.addAttribute("style", style)
			.addAttribute("class", styleClass)
			.addText((String) getTagValue(label));

		Div div = new Div();
		div.addAttribute("class", Bootstrap.CHECKBOX)
			.addAttribute("disabled", disabled || isEditRowTagEnabled() ? "disabled" : null)
			.addTag(lb);

		appendFormValidator(input);
		appendRest(input);
		appendEvent(input);

		Boolean object = (Boolean) getTagValue(value);
		if (object != null) {
			input.addAttribute("value", object)
				.addAttribute("checked", object ? "true" : "");
		} else {
			input.addAttribute("value", "false");
		}

		if (!ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : ajaxTags) {
				appendScript(ajax.getFunction(id));
			}
		}

		appendScript(getFunction());

		printOutput(div.getHtml()); 
	}
	
	private StringBuilder getFunction() {
		StringBuilder builder = new StringBuilder();
		builder.append("$('#").append(id).append("').bind('").append(EVENT_CLICK).append("', function(){");

		builder.append(JSMART_CHECKBOX.format(id));

		builder.append("});");
		return builder;
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
