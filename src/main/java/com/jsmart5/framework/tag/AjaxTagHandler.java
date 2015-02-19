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
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.json.JsonAjax;
import com.jsmart5.framework.json.JsonParam;
import com.jsmart5.framework.manager.SmartTagHandler;
import static com.jsmart5.framework.tag.JsConstants.*;

public final class AjaxTagHandler extends SmartTagHandler {

	private String event;

	private String action;

	private Integer timeout;

	private String update;

	private String beforeAjax;

	private String afterAjax;

	private boolean async = true;

	@Override
	public void validateTag() throws JspException {
		switch (event) {
			case EVENT_SELECT:
			case EVENT_CLICK:
			case EVENT_DBL_CLICK:
			case EVENT_MOUSE_DOWN:
			case EVENT_MOUSE_MOVE:
			case EVENT_MOUSE_OVER:
			case EVENT_MOUSE_OUT:
			case EVENT_MOUSE_UP:
			case EVENT_KEY_DOWN:
			case EVENT_KEY_PRESS:
			case EVENT_KEY_UP:
			case EVENT_FOCUS:
			case EVENT_CHANGE:
			case EVENT_BLUR:
				break;
			default:
				throw new JspException("Invalid event value for ajax tag. The valid values are " 
						+ EVENT_SELECT + ", "	+ EVENT_CLICK + ", " + EVENT_DBL_CLICK + ", "
						+ EVENT_MOUSE_DOWN + ", "	+ EVENT_MOUSE_MOVE + ", "	+ EVENT_MOUSE_OVER + ", "
						+ EVENT_MOUSE_OUT + ", " + EVENT_MOUSE_UP + ", " + EVENT_KEY_DOWN + ", "
						+ EVENT_KEY_PRESS + ", " + EVENT_KEY_UP + ", " + EVENT_FOCUS + ", "
						+ EVENT_CHANGE + " and "	+ EVENT_BLUR);
		}
		if (timeout != null && timeout < 0) {
			throw new JspException("Invalid timeout value for ajax tag. The valid value must be greater or equal to 0"); 
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof SmartTagHandler) {

			((SmartTagHandler) parent).addAjaxTag(this);
		}
	}

	StringBuilder getFunction(String id) {
		StringBuilder builder = new StringBuilder();
		builder.append("$('#").append(id).append("').bind('").append(event).append("', function(){");
		
		JsonAjax jsonAjax = new JsonAjax();
		jsonAjax.setId(id);
		jsonAjax.setAsync(async);
		jsonAjax.setTimeout(timeout);

		if (action != null) {
			jsonAjax.setMethod("post");
			jsonAjax.setAction(getTagName(J_SBMT, action));

			for (String name : params.keySet()) {						
				jsonAjax.getParams().add(new JsonParam(name, params.get(name)));
			}
		} else if (update != null) {
			jsonAjax.setMethod("get");
		}
		if (update != null) {
			jsonAjax.setUpdate(update.trim());
		}
		if (beforeAjax != null) {
			jsonAjax.setBefore(beforeAjax.trim());
		}
		if (afterAjax != null) {
			jsonAjax.setExec(afterAjax.trim());
		}

		builder.append(JSMART_AJAX_NEW.format(getNewJsonValue(jsonAjax)));

		builder.append("});");
		return builder;
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

	public void setBeforeAjax(String beforeAjax) {
		this.beforeAjax = beforeAjax;
	}

	public void setAfterAjax(String afterAjax) {
		this.afterAjax = afterAjax;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

}
