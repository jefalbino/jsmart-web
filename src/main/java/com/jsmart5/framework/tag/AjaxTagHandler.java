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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.json.Ajax;
import com.jsmart5.framework.json.Param;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Event;
import com.jsmart5.framework.tag.util.EventActions;

import static com.jsmart5.framework.tag.js.JsConstants.*;

public final class AjaxTagHandler extends TagHandler {

	private String event;

	private String action;

	private Integer timeout;

	private String update;

	private String beforeSend;
	
	private String onError;
	
	private String onSuccess;

	private String onComplete;
	
	private List<Object> args;

	public AjaxTagHandler() {
		args = new ArrayList<Object>();
	}

	@Override
	public void validateTag() throws JspException {
		if (event != null && !Event.validate(event)) {
			throw InvalidAttributeException.fromPossibleValues("ajax", "event", Event.getValues());
		}
		if (timeout != null && timeout < 0) {
			throw InvalidAttributeException.fromConstraint("ajax", "timeout", "greater or equal to 0"); 
		}
	}
	
	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof TagHandler) {

			((TagHandler) parent).addAjaxTag(this);
		}
		return true;
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
		// DO NOTHING
		return null;
	}
	
	private Ajax getJsonAjax(String id) {
		Ajax jsonAjax = new Ajax();
		jsonAjax.setId(id);
		jsonAjax.setTimeout(timeout);

		if (action != null) {
			jsonAjax.setMethod("post");
			jsonAjax.setAction(getTagName(J_SBMT, action));

			for (String name : params.keySet()) {						
				jsonAjax.addParam(new Param(name, params.get(name)));
			}
		} else if (update != null) {
			jsonAjax.setMethod("get");
		}
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
		return jsonAjax;
	}

	public StringBuilder getBindFunction(String id) {
		Ajax jsonAjax = getJsonAjax(id);

		// It means that the ajax is inside some iterator tag, so the
		// ajax actions will be set by iterator tag
		EventActions eventActions = (EventActions) getPageValue(ITERATOR_TAG_PARENT);
		if (eventActions != null) {
			// actions.addAjax(jsonAjax);
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_AJAX.format(getJsonValue(jsonAjax)));
		return getBindFunction(id, event, builder);
	}

	public StringBuilder getDelegateFunction(String id, String child) {
		Ajax jsonAjax = getJsonAjax(id);
		
		// It means that the ajax is inside some iterator tag, so the
		// ajax actions will be set by iterator tag
		EventActions eventActions = (EventActions) getPageValue(ITERATOR_TAG_PARENT);
		if (eventActions != null) {
			// actions.addAjax(jsonAjax);
			return null;
		}

		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_AJAX.format(getJsonValue(jsonAjax)));
		return getDelegateFunction(id, child, event, builder);
	}

	void addArg(Object arg) {
		this.args.add(arg);
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
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
