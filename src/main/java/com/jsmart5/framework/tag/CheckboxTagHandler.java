/*
 * JSmart5 - Java Web Development Framework
 * Copyright (c) 2014, Jeferson Albino da Silva, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
*/

package com.jsmart5.framework.tag;

import com.jsmart5.framework.json.Ajax;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Input;
import com.jsmart5.framework.tag.html.Label;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Event;
import com.jsmart5.framework.tag.util.RefAction;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.util.Stack;

import static com.jsmart5.framework.tag.js.JsConstants.JSMART_AJAX;

public final class CheckboxTagHandler extends TagHandler {

	private String value;

	private String label;

	private Integer tabIndex;
	
	private String update;
	
	private String beforeSend;
	
	private String onError;
	
	private String onSuccess;

	private String onComplete;

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}
		
		setRandomId("checkbox");

        String name = getTagName(J_TAG, value);
        boolean disabled = isDisabled();

		Input input = new Input();
		input.addAttribute("type", "checkbox")
			.addAttribute("name", name)
			.addAttribute("tabindex", tabIndex)
			.addAttribute("disabled", disabled ? "disabled" : null);
		
		appendRefId(input, id);

		Label lb = new Label();
		lb.addTag(input)
			.addAttribute("style", getTagValue(style))
			.addAttribute("class", getTagValue(styleClass))
                .addText(getTagValue(label));

		Div div = new Div();
		div.addAttribute("class", Bootstrap.CHECKBOX)
			.addAttribute("disabled", disabled ? "disabled" : null)
			.addTag(lb);

		appendValidator(input);
		appendRest(input, name);
		appendEvent(input);

		appendTooltip(div);
		appendPopOver(div);

		Boolean object = (Boolean) getTagValue(value);
		if (object != null) {
			input.addAttribute("value", object)
				.addAttribute("checked", object ? "true" : null);
		} else {
			input.addAttribute("value", "false");
		}

		if (ajax) {
			appendDocScript(getAjaxFunction());
		}

		appendAjax(id);
		appendBind(id);

		return div; 
	}

	@SuppressWarnings("unchecked")
	private StringBuilder getAjaxFunction() {
		Ajax jsonAjax = new Ajax();
		jsonAjax.setId(id);
		jsonAjax.setMethod("post");
		jsonAjax.setTag("checkbox");
		
		if (update != null) {
			jsonAjax.setUpdate(update.trim());
		}
		if (beforeSend != null) {
			jsonAjax.setBefore((String) getTagValue(beforeSend.trim()));
		}
		if (onError != null) {
			jsonAjax.setError((String) getTagValue(onError.trim()));
		}
		if (onSuccess != null) {
			jsonAjax.setSuccess((String) getTagValue(onSuccess.trim()));
		}
		if (onComplete != null) {
			jsonAjax.setComplete((String) getTagValue(onComplete.trim()));
		}
		
		// It means that the ajax is inside some iterator tag, so the
		// ajax actions will be set by iterator tag and the event bind
		// will use the id as tag attribute
		Stack<RefAction> actionStack = (Stack<RefAction>) getMappedValue(DELEGATE_TAG_PARENT);
		if (actionStack != null) {
			actionStack.peek().addRef(id, Event.CLICK.name(), jsonAjax);

		} else {
			StringBuilder builder = new StringBuilder();
			builder.append(JSMART_AJAX.format(getJsonValue(jsonAjax)));
			return getBindFunction(id, Event.CLICK.name(), builder);
		}
		
		return null;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public void setBeforeSend(String beforeSend) {
		this.beforeSend = beforeSend;
	}

	public void setOnError(String onError) {
		this.onError = onError;
	}

	public void setOnSuccess(String onSuccess) {
		this.onSuccess = onSuccess;
	}

	public void setOnComplete(String onComplete) {
		this.onComplete = onComplete;
	}

}
