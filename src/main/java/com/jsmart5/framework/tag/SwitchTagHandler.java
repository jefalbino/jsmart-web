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

import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Tag;

import static com.jsmart5.framework.tag.js.JsConstants.*;

public final class SwitchTagHandler extends TagHandler {

	private String value;

	private String label;

	private String labelOn;

	private String labelOff;

	private boolean disabled;

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
//
//		// Just to call nested tags
//		JspFragment body = getJspBody();
//		if (body != null) {
//			body.invoke(null);
//		}
//
//		// Container to hold switch
//		StringBuilder builder = new StringBuilder();
//		
//		if (label != null) {
//			builder.append(OPEN_DIV_TAG);
//			appendClass(builder, CSS_INPUT_GROUP);
//			builder.append(CLOSE_TAG);
//
//			builder.append(OPEN_SPAN_TAG);
//			appendClass(builder, CSS_INPUT_LABEL);
//			builder.append(CLOSE_TAG);
//
//			String labelVal = (String) getTagValue(label);
//			if (labelVal != null) {
//				builder.append(labelVal);
//			}
//			builder.append(CLOSE_SPAN_TAG);
//		}
//
//		builder.append(OPEN_DIV_TAG);
//		builder.append("id=\"" + id + "\" ");
//		builder.append("switch=\"switch\" ");
//
//		if (style != null) {
//			builder.append("style=\"" + style + "\" ");
//		}
//		if (styleClass != null) {
//			builder.append("class=\"" + styleClass + "\" ");
//		} else {
//			appendClass(builder, CSS_SWITCH_CONTAINER);
//		}
//
//		appendEvent(builder);
//
//		builder.append(">");
//
//		// Hidden input to send value to server
//		builder.append(INPUT_TAG);
//
//		builder.append("id=\"" + id + SWITCH_INPUT + "\" ");
//
//		String name = getTagName(J_TAG, value);
//		if (name != null) {
//			builder.append("name=\"" + name + "\" ");
//		}
//
//		Boolean object = (Boolean) getTagValue(value);
//		if (object != null) {
//			builder.append("value=\"" + object + "\" ");
//		}
//
//		if (disabled || isEditRowTagEnabled()) {
//			builder.append("disabled=\"disabled\" ");
//		}
//
//		builder.append("type=\"hidden\" ");
//
//		builder.append("switch=\"switch\" ");
//
//		appendFormValidator(builder);
//		
//		appendRest(builder);
//
//		builder.append(" />");
//
//		// Span tag to represent On state
//		builder.append(OPEN_SPAN_TAG);
//
//		builder.append("id=\"" + id + SWITCH_SPAN_ON + "\" ");
//
//		if (disabled) {
//			appendClass(builder, CSS_SWITCH_SPAN_ON_DISABLED);
//		} else {
//			appendClass(builder, CSS_SWITCH_SPAN_ON);
//		}
//
//		builder.append(">");
//
//		builder.append(getTagValue(labelOn));
//
//		builder.append(CLOSE_SPAN_TAG);
//
//		// Span tag to represent Off state
//		builder.append(OPEN_SPAN_TAG);
//
//		builder.append("id=\"" + id + SWITCH_SPAN_OFF + "\" ");
//
//		if (disabled) {
//			appendClass(builder, CSS_SWITCH_SPAN_OFF_DISABLED);
//		} else {
//			appendClass(builder, CSS_SWITCH_SPAN_OFF);
//		}
//
//		builder.append(">");
//
//		builder.append(getTagValue(labelOff));
//
//		builder.append(CLOSE_SPAN_TAG);
//
//		// Switch button
//		builder.append(OPEN_DIV_TAG);
//
//		builder.append("id=\"" + id + SWITCH_BUTTON + "\" ");
//
//		if (disabled) {
//			appendClass(builder, CSS_SWITCH_BUTTON_DISABLED);
//		} else {
//			appendClass(builder, CSS_SWITCH_BUTTON);
//			builder.append(ON_CLICK + JSMART_SWITCH.format(id, async, ajax) + "\" ");
//			appendEvent(builder);
//		}
//
//		builder.append(CLOSE_TAG + CLOSE_DIV_TAG);
//		builder.append(CLOSE_DIV_TAG);
//
//		if (label != null) {
//			builder.append(CLOSE_DIV_TAG);
//		}
//
//		printOutput(builder);
//
//		appendScriptDeprecated(new StringBuilder(JSMART_SWITCH_RESET.format(id)));
		return null;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLabelOn(String labelOn) {
		this.labelOn = labelOn;
	}

	public void setLabelOff(String labelOff) {
		this.labelOff = labelOff;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
