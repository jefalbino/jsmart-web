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

import com.jsmart5.framework.json.JsonRange;
import com.jsmart5.framework.manager.SmartTagHandler;

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.CssConstants.*;
import static com.jsmart5.framework.tag.JsConstants.*;

public final class RangeTagHandler extends SmartTagHandler {

	private static final String RANGE_FRAME = "_range_frame";

	private static final String RANGE_INPUT = "_range_input";

	private static final String RANGE_VALUE = "_range_value";

	private static final Integer DEFAULT_MAX = 100;

	private static final Integer DEFAULT_MIN = 0;

	private boolean ajax;

	private boolean disabled;

	private String value;

	private Integer maxValue = DEFAULT_MAX;

	private Integer minValue = DEFAULT_MIN;

	private Integer stepValue;

	private String onValueChange;

    private boolean showValue = true;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof GridTagHandler) {

			((GridTagHandler) parent).addTag(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (maxValue != null && minValue != null && minValue >= maxValue) {
			throw new JspException("Attribute minValue must be less than maxValue attribute for range tag");
		}
		if (stepValue != null && stepValue <= 0) {
			throw new JspException("Attribute stepValue must be greater than zero for range tag");
		}
		if (stepValue != null && stepValue > (maxValue - minValue)) {
			throw new JspException("Attribute stepValue must be less than the difference of maxValue and minValue for range tag");
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		// Container to hold range
		StringBuilder builder = new StringBuilder(OPEN_DIV_TAG);

		builder.append("id=\"" + id + "\" ");

		builder.append("range=\"range\" ");

		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CSS_RANGE_CONTAINER);
		}

		appendEvent(builder);

		builder.append(">");

		builder.append(OPEN_DIV_TAG);

		builder.append("id=\"" + id + RANGE_FRAME + "\" ");

		appendClass(builder, CSS_RANGE_FRAME);

		JsonRange jsonRange = new JsonRange();

		jsonRange.setAjax(String.valueOf(ajax));
		jsonRange.setMax(String.valueOf(maxValue));
		jsonRange.setMin(String.valueOf(minValue));
		if (stepValue != null) {
			jsonRange.setStep(String.valueOf(stepValue));
		}
		if (onValueChange != null) {
			jsonRange.setCallback(onValueChange.trim());
		}

		builder.append("ajax=\"" + getJsonValue(jsonRange) + "\" ");

		if (disabled || isEditRowTagEnabled()) {
			builder.append("disabled=\"disabled\" ");
		}

		builder.append(">");

		// Trail for the range
		builder.append(OPEN_SPAN_TAG);
		appendClass(builder, CSS_RANGE_TRAIL);
		builder.append(">");
		builder.append(CLOSE_SPAN_TAG);

		// Hidden input to send value to server
		builder.append(INPUT_TAG);

		builder.append("id=\"" + id + RANGE_INPUT + "\" ");

		String name = getTagName(J_TAG, value);
		if (name != null) {
			builder.append("name=\"" + name + "\" ");
		}

		Number number = (Number) getTagValue(value);
		if (number != null) {
			builder.append("value=\"" + number + "\" ");
		}

		if (disabled || isEditRowTagEnabled()) {
			builder.append("disabled=\"disabled\" ");
		}

		builder.append("type=\"hidden\" ");
		
		appendRest(builder);

		builder.append(" />");

		// Bar to handle range
		builder.append(OPEN_DIV_TAG);

		if (disabled) {
			appendClass(builder, CSS_RANGE_BAR_DISABLED);
		} else {
			appendClass(builder, CSS_RANGE_BAR);
		}

		builder.append(">" + CLOSE_DIV_TAG);

		builder.append(CLOSE_DIV_TAG);

		if (showValue) {
			builder.append(OPEN_SPAN_TAG);
			builder.append("id=\"" + id + RANGE_VALUE + "\" ");
			appendClass(builder, CSS_RANGE_VALUE);
			builder.append(">");
			builder.append(number != null ? number : 0);
			builder.append(CLOSE_SPAN_TAG);
		}

		builder.append(CLOSE_DIV_TAG);

		printOutput(builder);

		appendScriptDeprecated(new StringBuilder(JSMART_RANGE.format(id)));
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}

	public void setStepValue(Integer stepValue) {
		this.stepValue = stepValue;
	}

	public void setOnValueChange(String onValueChange) {
		this.onValueChange = onValueChange;
	}

	public void setShowValue(boolean showValue) {
		this.showValue = showValue;
	}

}
