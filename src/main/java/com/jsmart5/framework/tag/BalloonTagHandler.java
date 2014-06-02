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

import com.jsmart5.framework.manager.SmartTagHandler;


public final class BalloonTagHandler extends SmartTagHandler {

	private static final String BALLOON_HOLDER = "_balloon_holder";

	private static final Integer DEFAULT_LENGTH = 200;

	private static final String POSITION_TOP = "top";

	private static final String POSITION_LEFT = "left";

	private static final String POSITION_RIGHT = "right";

	private static final String POSITION_BOTTOM = "bottom";

	private String target;

	private String message;

	private String position;

	private boolean opened;

	private Integer length;

	@Override
	public void validateTag() throws JspException {
		if (position != null && !position.equals(POSITION_TOP) && !position.equals(POSITION_LEFT) 
				&& !position.equals(POSITION_RIGHT) && !position.equals(POSITION_BOTTOM)) {
			throw new JspException("Invalid position value for balloon tag. Valid values are top, bottom, right and left");
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {
		Object messageVal = getTagValue(message);
		if (length == null) {
			length = DEFAULT_LENGTH;
		}

		String balloonScript = JSConstants.JSMART_BALLOON.format(target, position != null ? position : "top",
				opened, length, messageVal != null ? messageVal.toString().replace("'", "\\'") : "");

		appendScriptBuilder(new StringBuilder(balloonScript), true);

		StringBuilder builder = new StringBuilder(HtmlConstants.OPEN_SPAN_TAG);
		builder.append("id=\"" + target + BALLOON_HOLDER + "\" ");
		builder.append("style=\"display: none;\" ");
		builder.append("type=\"balloon\" ");

		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			builder.append(CssConstants.CSS_BALLOON);
		}

		builder.append("target=\"" + target + "\" ");
		builder.append("position=\"" + (position != null ? position : "top") + "\" ");
		builder.append("opened=\"" + opened + "\" ");
		builder.append("length=\"" + length + "\" ");
		builder.append("message=\"" + (messageVal != null ? messageVal.toString() : "") + "\">");
		builder.append(HtmlConstants.CLOSE_SPAN_TAG);

		printOutput(builder);		
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

}
