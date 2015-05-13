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

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Look;

public final class ProgressBarTagHandler extends TagHandler {

	private static final Integer DEFAULT_MAX = 100;

	private static final Integer DEFAULT_MIN = 0;

	private String value;

	private Integer maxValue = DEFAULT_MAX;

	private Integer minValue = DEFAULT_MIN;

    private Integer interval;

    private String onInterval;

    private String onComplete;
    
    private boolean withLabel;

    private boolean striped;

    private boolean animated;
    
    private String look;

	@Override
	public void validateTag() throws JspException {
		if (maxValue != null && maxValue <= 0) {
			throw InvalidAttributeException.fromConstraint("progressbar", "maxValue", "greater than 0");
		}
		if (minValue != null && minValue < 0) {
			throw InvalidAttributeException.fromConstraint("progressbar", "minValue", "greater or equal to 0");
		}
		if (maxValue != null && minValue != null && minValue >= maxValue) {
			throw InvalidAttributeException.fromConstraint("progressbar", "minValue", "less than [maxValue] attribute value");
		}
		if (interval != null && interval < 0) {
			throw InvalidAttributeException.fromConstraint("progressbar", "interval", "greater than 0");
		}
		if (onInterval != null && interval == null) {
			throw InvalidAttributeException.fromConflict("progressbar", "interval", "Attribute must be specified case [onInterval] attribute is used");
		}
		if (look != null && !Look.validateButton(look) && !isEL(look)) {
			throw InvalidAttributeException.fromPossibleValues("progressbar", "look", Look.getBasicValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		JspTag parent = getParent();

		setRandomId("progressbar");

		Div progress = null;

		if (!(parent instanceof ProgressGroupTagHandler)) {
			progress = new Div();
			progress.addAttribute("class", Bootstrap.PROGRESS);
		}

		Object tagVal = getTagValue(value);

		Div progressBar = new Div();
		progressBar.addAttribute("id", id)
			.addAttribute("class", Bootstrap.PROGRESS_BAR)
			.addAttribute("role", "progressbar")
			.addAttribute("aria-valuenow", tagVal)
			.addAttribute("aria-valuemin", minValue)
			.addAttribute("aria-valuemax", maxValue)
			.addAttribute("style", "width:" + tagVal + "%;")
			.addAttribute("style", "min-width: 2em;"); // TODO

		String lookVal = (String) getTagValue(look);
		progressBar.addAttribute("class", getProgressBarLook(lookVal));

		if (striped || animated) {
			progressBar.addAttribute("class", Bootstrap.PROGRESS_BAR_STRIPED);
		}
		if (animated) {
			progressBar.addAttribute("class", Bootstrap.ACTIVE);
		}
		
		if (progress != null) {
			progress.addAttribute("class", styleClass)
				.addAttribute("style", style);
		} else {
			progressBar.addAttribute("class", styleClass)
				.addAttribute("style", style);
		}
		
		if (withLabel) {
			progressBar.addText("60%");
		}

		if (progress != null) {
			progress.addTag(progressBar);
		}
		
		return progress != null ? progress.addTag(progressBar) : progressBar;
//
//		// Container to hold progress
//		StringBuilder builder = new StringBuilder(OPEN_DIV_TAG);
//
//		builder.append("id=\"" + id + "\" ");
//
//		builder.append("progress=\"progress\" ");
//
//		if (style != null) {
//			builder.append("style=\"" + style + "\" ");
//		}
//		if (styleClass != null) {
//			builder.append("class=\"" + styleClass + "\" ");
//		} else {
//			appendClass(builder, CSS_PROGRESS_CONTAINER);
//		}
//
//		appendEvent(builder);
//
//		builder.append(">");
//
//		builder.append(OPEN_DIV_TAG);
//
//		builder.append("id=\"" + id + PROGRESS_FRAME + "\" ");
//
//		appendClass(builder, CSS_PROGRESS_FRAME);
//
//		JsonProgress jsonProgress = new JsonProgress();
//
//		jsonProgress.setAjax(String.valueOf(ajax));
//		jsonProgress.setMax(String.valueOf(maxValue));
//		jsonProgress.setMin(String.valueOf(minValue));
//
//		if (interval != null) {
//			jsonProgress.setInterval(String.valueOf(interval));
//		}
//		if (onInterval != null) {
//			jsonProgress.setCallback(onInterval);
//		}
//		if (onComplete != null) {
//			jsonProgress.setComplete(onComplete);
//		}
//
//		builder.append("ajax=\"" + getJsonValue(jsonProgress) + "\" ");
//
//		if (disabled || isEditRowTagEnabled()) {
//			builder.append("disabled=\"disabled\" ");
//		}
//
//		builder.append(">");
//
//
//		// Hidden input to send value to server
//		builder.append(INPUT_TAG);
//
//		builder.append("id=\"" + id + PROGRESS_INPUT + "\" ");
//
//		String name = getTagName(J_TAG, value);
//		if (name != null) {
//			builder.append("name=\"" + name + "\" ");
//		}
//
//		Number number = (Number) getTagValue(value);
//		if (number != null) {
//			builder.append("value=\"" + number + "\" ");
//		}
//
//		if (disabled || isEditRowTagEnabled()) {
//			builder.append("disabled=\"disabled\" ");
//		}
//
//		builder.append("type=\"hidden\" ");
//		
//		appendRest(builder);
//
//		builder.append(" />");
//
//
//		builder.append(OPEN_DIV_TAG);
//
//		if (disabled) {
//			appendClass(builder, CSS_PROGRESS_BAR_DISABLED);
//		} else {
//			appendClass(builder, CSS_PROGRESS_BAR);
//		}
//
//		builder.append(">" + CLOSE_DIV_TAG);
//
//		Object labelVal = getTagValue(label);
//		if (labelVal != null) {
//			builder.append(OPEN_SPAN_TAG);
//			appendClass(builder, CSS_PROGRESS_LABEL);
//			builder.append(">");
//			builder.append(labelVal);
//			builder.append(CLOSE_SPAN_TAG);
//		}
//
//		builder.append(CLOSE_DIV_TAG);
//
//		if (showPercentage) {
//			builder.append(OPEN_SPAN_TAG);
//			builder.append("id=\"" + id + PROGRESS_PERCENT + "\" ");
//			appendClass(builder, CSS_PROGRESS_PERCENT);
//			builder.append(">");
//			builder.append(CLOSE_SPAN_TAG);
//		}
//
//		builder.append(CLOSE_DIV_TAG);
//
//		printOutput(builder);
//
//		appendScriptDeprecated(new StringBuilder(JSMART_PROGRESS.format(id)));
	}

	private String getProgressBarLook(String lookVal) {
		String progressBarLook = null;

		if (Look.SUCCESS.equalsIgnoreCase(lookVal)) {
			progressBarLook = Bootstrap.PROGRESS_BAR_SUCCESS;
		} else if (Look.INFO.equalsIgnoreCase(lookVal)) {
			progressBarLook = Bootstrap.PROGRESS_BAR_INFO;
		} else if (Look.WARNING.equalsIgnoreCase(lookVal)) {
			progressBarLook = Bootstrap.PROGRESS_BAR_WARNING;
		} else if (Look.DANGER.equalsIgnoreCase(lookVal)) {
			progressBarLook = Bootstrap.PROGRESS_BAR_DANGER;
		}
		return progressBarLook;
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

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public void setOnInterval(String onInterval) {
		this.onInterval = onInterval;
	}

	public void setOnComplete(String onComplete) {
		this.onComplete = onComplete;
	}

	public void setWithLabel(boolean withLabel) {
		this.withLabel = withLabel;
	}

	public void setStriped(boolean striped) {
		this.striped = striped;
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
	}

	public void setLook(String look) {
		this.look = look;
	}

}
