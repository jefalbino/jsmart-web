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
import java.util.Map;

import javax.servlet.jsp.JspException;

import com.jsmart5.framework.manager.SmartContext;
import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.util.SmartMessage;

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.JsConstants.*;
import static com.jsmart5.framework.util.SmartMessage.*;

public final class MessageTagHandler extends SmartTagHandler {

	private static final String FIXED = "fixed";

	private static final String CENTER = "center";

	private static final String LEFT = "left";

	private static final String RIGHT = "right";

	private static final String TOP = "top";

	private static final String BOTTOM = "bottom";

	private static final String LEFT_TOP = LEFT + " " + TOP;

	private static final String LEFT_BOTTOM = LEFT + " " + BOTTOM;

	private static final String RIGHT_TOP = RIGHT + " " + TOP;

	private static final String RIGHT_BOTTOM = RIGHT + " " + BOTTOM;

	private Boolean autoHide = true;

	private Integer width;

	private Long duration;

	private String position;

	private Boolean modal;

	private String onShow;

	private String onClose;

	@Override
	public void validateTag() throws JspException {
		if (position != null && !FIXED.equals(position) && !CENTER.equals(position) && !LEFT.equals(position) && !RIGHT.equals(position) 
				&& !TOP.equals(position) && !BOTTOM.equals(position) && !LEFT_TOP.equals(position) && !LEFT_BOTTOM.equals(position)
				&& !RIGHT_TOP.equals(position) && !RIGHT_BOTTOM.equals(position)) {
			throw new JspException("Invalid position value for message tag. Valid values are " + FIXED + ", " + CENTER + ", " + LEFT 
					+ ", " + RIGHT + ", " + TOP + ", " + BOTTOM + ", " + LEFT_TOP + ", " + LEFT_BOTTOM + ", " + RIGHT_TOP + ", " + RIGHT_BOTTOM);
		}
		if (FIXED.equals(position) && id == null) {
			throw new JspException("Invalid attribute value for message tag. For position " + FIXED + " the id must be provided");
		}
		if (FIXED.equals(position) && Boolean.TRUE.equals(modal)) {
			throw new JspException("Invalid attribute value for message tag. For position " + FIXED + " the modal attribute cannot be used");
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {
		Map<String, SmartMessage> messages = null;

		if (FIXED.equals(position)) {
			messages = getMessages(id);
			printOutput(new StringBuilder(OPEN_DIV_TAG + "id=\"" + id + "\" fixed=\"fixed\" >" + CLOSE_DIV_TAG));
		} else {
			messages = getMessages();
		}

		if (messages != null && !messages.isEmpty()) {

			StringBuilder infoBuilder = new StringBuilder("info:[");
			StringBuilder warningBuilder = new StringBuilder("warning:[");
			StringBuilder errorBuilder = new StringBuilder("error:[");
			StringBuilder successBuilder = new StringBuilder("success:[");

			for (String key : messages.keySet()) {
				if (messages.get(key) == INFO) {
					infoBuilder.append("'" + clearMessage(key) + "',");				
				} else if (messages.get(key) == WARNING) {
					warningBuilder.append("'" + clearMessage(key) + "',");
				} else if (messages.get(key) == ERROR) {
					errorBuilder.append("'" + clearMessage(key) + "',");
				} else if (messages.get(key) == SUCCESS) {
					successBuilder.append("'" + clearMessage(key) + "',");
				}
			}

			infoBuilder.append("]");
			warningBuilder.append("]");
			errorBuilder.append("]");
			successBuilder.append("]");

			StringBuilder messagesBuilder = new StringBuilder();
			messagesBuilder.append("{" + infoBuilder + "," + warningBuilder + "," + errorBuilder + "," + successBuilder + "}");

			StringBuilder optionsBuilder = new StringBuilder("{");

			if (id != null) {
				optionsBuilder.append("id:'" + id + "',");
			}
			if (autoHide != null) {
				optionsBuilder.append("autoHide:" + autoHide + ",");
			}
			if (width != null && width > 0) {
				optionsBuilder.append("width:" + width + ",");
			}
			if (duration != null) {
				optionsBuilder.append("duration:" + duration + ",");
				if (autoHide == null) {
					optionsBuilder.append("autoHide:" + true + ",");
				}
			}
			if (position != null) {
				optionsBuilder.append("position:'" + position + "',");
			}
			if (modal != null) {
				optionsBuilder.append("modal:" + modal + ",");
			}
			if (onShow != null) {
				optionsBuilder.append("onShow:'" + onShow + "',");
			}
			if (onClose != null) {
				optionsBuilder.append("onClose:'" + onClose + "',");
			}

			optionsBuilder.append("}");

			StringBuilder scriptBuilder = new StringBuilder(JSMART_MESSAGE.format(messagesBuilder, optionsBuilder));

			if (SmartContext.isAjaxRequest()) {
				StringBuilder inputBuilder = new StringBuilder(INPUT_TAG);
				inputBuilder.append("id=\"jsmart_messages" + (id != null ? id : "") + "\" type=\"hidden\" ");
				inputBuilder.append("value=\"" + scriptBuilder + "\" />");
				printOutput(inputBuilder);
			} else {
				appendScriptDeprecated(scriptBuilder);
			}
		}
	}

	private String clearMessage(String message) {
		return message.replaceAll("(\\n)+", "\\\\n").replaceAll("(\\r)+", "\\\\r").replace("\"", "'").replace("'", "\\'");
	}

	public void setAutoHide(Boolean autoHide) {
		this.autoHide = autoHide;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setModal(Boolean modal) {
		this.modal = modal;
	}

	public void setOnShow(String onShow) {
		this.onShow = onShow != null ? onShow.replace("'", "\\'") : onShow;
	}

	public void setOnClose(String onClose) {
		this.onClose = onClose != null ? onClose.replace("'", "\\'") : onClose;
	}

}
