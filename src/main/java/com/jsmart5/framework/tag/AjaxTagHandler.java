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

import com.jsmart5.framework.json.JSONAjax;
import com.jsmart5.framework.json.JSONParam;
import com.jsmart5.framework.manager.SmartTagHandler;
import static com.jsmart5.framework.tag.JSConstants.*;

/*
 * Ajax uses a json structure
 * 
 * {
 * 	  'method': '',
 *    'action': '',
 *    'params': [{'name': '', 'value': ''}],
 *    'update': '',
 *    'before': '',
 *    'exec': ''
 *  }
 */
public final class AjaxTagHandler extends SmartTagHandler {

	private static final String EVENT_SELECT = "select";

	private static final String EVENT_CHANGE = "change";

	private static final String EVENT_BLUR = "blur";

	private static final String EVENT_CLICK = "click";

	private static final String EVENT_DBL_CLICK = "dblclick";

	private static final String EVENT_MOUSE_DOWN = "mousedown";

	private static final String EVENT_MOUSE_MOVE = "mousemove";

	private static final String EVENT_MOUSE_OVER = "mouseover";

	private static final String EVENT_MOUSE_OUT = "mouseout";

	private static final String EVENT_MOUSE_UP = "mouseup";

	private static final String EVENT_KEY_DOWN = "keydown";

	private static final String EVENT_KEY_PRESS = "keypress";

	private static final String EVENT_KEY_UP = "keyup";

	private static final String EVENT_FOCUS = "focus";

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

		StringBuilder builder = new StringBuilder();

		// Look for parameters
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		if (event.equals(EVENT_SELECT)) {
			builder.append(ON_SELECT);

		} else if (event.equals(EVENT_CLICK)) {
			builder.append(ON_CLICK);
			
		} else if (event.equals(EVENT_DBL_CLICK)) {
			builder.append(ON_DBL_CLICK);
			
		} else if (event.equals(EVENT_MOUSE_DOWN)) {
			builder.append(ON_MOUSE_DOWN);
			
		} else if (event.equals(EVENT_MOUSE_MOVE)) {
			builder.append(ON_MOUSE_MOVE);
			
		} else if (event.equals(EVENT_MOUSE_OVER)) {
			builder.append(ON_MOUSE_OVER);
			
		} else if (event.equals(EVENT_MOUSE_OUT)) {
			builder.append(ON_MOUSE_OUT);
			
		} else if (event.equals(EVENT_MOUSE_UP)) {
			builder.append(ON_KEY_UP);
			
		} else if (event.equals(EVENT_KEY_DOWN)) {
			builder.append(ON_KEY_DOWN);
			
		} else if (event.equals(EVENT_KEY_PRESS)) {
			builder.append(ON_KEY_PRESS);
			
		} else if (event.equals(EVENT_KEY_UP)) {
			builder.append(ON_KEY_UP);
			
		} else if (event.equals(EVENT_FOCUS)) {
			builder.append(ON_FOCUS);

		} else if (event.equals(EVENT_CHANGE)) {
			builder.append(ON_CHANGE);

		} else if (event.equals(EVENT_BLUR)) {
			builder.append(ON_BLUR);
		}

		if (builder.length() > 0) {
			builder.append(JSMART_AJAX.format(async, "$(this)", timeout != null ? timeout : 0) + "\" ");

			JSONAjax jsonAjax = new JSONAjax();
			if (action != null) {
				jsonAjax.setMethod("post");
				jsonAjax.setAction(getTagName(J_SBMT, action));
				if (!params.isEmpty()) {
					for (String name : params.keySet()) {						
						jsonAjax.getParams().add(new JSONParam(name, params.get(name)));
					}
				}
			} else if (update != null) {
				jsonAjax.setMethod("get");
			}
			jsonAjax.setUpdate(update);
			jsonAjax.setBefore(beforeAjax);
			jsonAjax.setExec(afterAjax);

			builder.append("ajax=\"" + getJSONValue(jsonAjax) + "\" ");

			JspTag parent = getParent();
			if (parent instanceof SmartTagHandler) {
				((SmartTagHandler) parent).setAjaxCommand(builder.toString());
			}
		}
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
