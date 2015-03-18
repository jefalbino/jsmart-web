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
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.json.Ajax;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Event;
import com.jsmart5.framework.tag.util.RefAction;

public final class CheckGroupTagHandler extends TagHandler {

	protected final List<CheckTagHandler> checks;

	private String align;

	private String selectValues;

	private boolean inline;

	private String update;
	
	private String beforeSend;
	
	private String onError;
	
	private String onSuccess;

	private String onComplete;

	public CheckGroupTagHandler() {
		checks = new ArrayList<CheckTagHandler>();
	}

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

		setRandomId("checkgroup");
		
		Div div = new Div();
		div.addAttribute("align", align)
			.addAttribute("checkgroup", "");
		
		appendRefId(div, id);
		
		appendTooltip(div);
		appendPopOver(div);

 		long checkIndex = 0;
		for (CheckTagHandler check : checks) {

			check.setCheckIndex(checkIndex++);
			check.setStyle(style);
			check.setStyleClass(styleClass);
			check.setInline(inline);
			check.setValidatorTag(validatorTag);
			check.setRest(rest);
			check.setName(selectValues);
			check.setType(CheckTagHandler.CHECKBOX);
			setEvents(check);

			div.addTag(check.executeTag());
		}
		
		appendDelegateAjax(id, "input");
		appendDelegateBind(id, "input");

		if (ajax) {
			appendScript(getFunction());
		}

		return div;
	}

	@SuppressWarnings("unchecked")
	private StringBuilder getFunction() {
		Ajax jsonAjax = new Ajax();
		jsonAjax.setId(id);
		jsonAjax.setMethod("post");
		jsonAjax.setTag("checkgroup");

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
		Stack<RefAction> actionStack = (Stack<RefAction>) getMappedValue(ITERATOR_TAG_PARENT);
		if (actionStack != null) {
			actionStack.peek().addRef(id, Event.CLICK.name(), jsonAjax);

		} else {
			StringBuilder builder = new StringBuilder();
			builder.append(JSMART_AJAX.format(getJsonValue(jsonAjax)));
			return getDelegateFunction(id, "input", Event.CLICK.name(), builder);
		}

		return null;
	}

	void addCheck(CheckTagHandler check) {
		this.checks.add(check);
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setSelectValues(String selectValues) {
		this.selectValues = selectValues;
	}

	public void setInline(boolean inline) {
		this.inline = inline;
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
