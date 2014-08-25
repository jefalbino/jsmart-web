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

import com.jsmart5.framework.json.JSONProgress;
import com.jsmart5.framework.manager.SmartTagHandler;
import static com.jsmart5.framework.tag.HtmlConstants.*;

public final class ProgressTagHandler extends SmartTagHandler {

	private static final String PROGRESS_FRAME = "_progress_frame";

	private static final String PROGRESS_PERCENT = "_progress_percent";

	private static final String PROGRESS_INPUT = "_progress_input";

	private static final Integer DEFAULT_MAX = 100;

	private static final Integer DEFAULT_MIN = 0;

	private boolean ajax;

	private boolean disabled;

	private String value;

	private Integer maxValue = DEFAULT_MAX;

	private Integer minValue = DEFAULT_MIN;

    private String label;

    private Integer interval;

    private String onInterval;

    private boolean showPercentage = true;

    private String onComplete;    

	@Override
	public void validateTag() throws JspException {
		if (maxValue != null && maxValue <= 0) {
			throw new JspException("Attribute maxValue must be greater than zero for progress tag");
		}
		if (minValue != null && minValue < 0) {
			throw new JspException("Attribute minValue must be greater or equal to zero for progress tag");
		}
		if (maxValue != null && minValue != null && minValue >= maxValue) {
			throw new JspException("Attribute minValue must be less than maxValue attribute for progress tag");
		}
		if (interval != null && interval < 0) {
			throw new JspException("Attribute interval must be greater than zero for progress tag");
		}
		if (onInterval != null && interval == null) {
			throw new JspException("Attribute interval must be specified case onInterval attribute is specified for progress tag");
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		// Container to hold progress
		StringBuilder builder = new StringBuilder(OPEN_DIV_TAG);

		builder.append("id=\"" + id + "\" ");

		builder.append("progress=\"progress\" ");

		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CssConstants.CSS_PROGRESS_CONTAINER);
		}

		appendEvent(builder);

		builder.append(">");

		builder.append(OPEN_DIV_TAG);

		builder.append("id=\"" + id + PROGRESS_FRAME + "\" ");

		appendClass(builder, CssConstants.CSS_PROGRESS_FRAME);

		JSONProgress jsonProgress = new JSONProgress();

		jsonProgress.setAjax(String.valueOf(ajax));
		jsonProgress.setMax(String.valueOf(maxValue));
		jsonProgress.setMin(String.valueOf(minValue));

		if (interval != null) {
			jsonProgress.setInterval(String.valueOf(interval));
		}
		if (onInterval != null) {
			jsonProgress.setCallback(onInterval);
		}
		if (onComplete != null) {
			jsonProgress.setComplete(onComplete);
		}

		builder.append("ajax=\"" + getJSONValue(jsonProgress) + "\" ");

		if (disabled || isEditRowTagEnabled()) {
			builder.append("disabled=\"disabled\" ");
		}

		builder.append(">");


		// Hidden input to send value to server
		builder.append(HtmlConstants.INPUT_TAG);

		builder.append("id=\"" + id + PROGRESS_INPUT + "\" ");

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


		builder.append(OPEN_DIV_TAG);

		if (disabled) {
			appendClass(builder, CssConstants.CSS_PROGRESS_BAR_DISABLED);
		} else {
			appendClass(builder, CssConstants.CSS_PROGRESS_BAR);
		}

		builder.append(">" + CLOSE_DIV_TAG);

		Object labelVal = getTagValue(label);
		if (labelVal != null) {
			builder.append(OPEN_SPAN_TAG);
			appendClass(builder, CssConstants.CSS_PROGRESS_LABEL);
			builder.append(">");
			builder.append(labelVal);
			builder.append(CLOSE_SPAN_TAG);
		}

		builder.append(CLOSE_DIV_TAG);

		if (showPercentage) {
			builder.append(OPEN_SPAN_TAG);
			builder.append("id=\"" + id + PROGRESS_PERCENT + "\" ");
			appendClass(builder, CssConstants.CSS_PROGRESS_PERCENT);
			builder.append(">");
			builder.append(CLOSE_SPAN_TAG);
		}

		builder.append(CLOSE_DIV_TAG);

		printOutput(builder);

		appendScript(new StringBuilder(JSConstants.JSMART_PROGRESS.format(id)));
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

	public void setLabel(String label) {
		this.label = label;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public void setOnInterval(String onInterval) {
		this.onInterval = onInterval;
	}

	public void setShowPercentage(boolean showPercentage) {
		this.showPercentage = showPercentage;
	}

	public void setOnComplete(String onComplete) {
		this.onComplete = onComplete;
	}

}
