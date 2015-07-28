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

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.json.Progress;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Input;
import com.jsmart5.framework.tag.html.Set;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Look;

import static com.jsmart5.framework.tag.js.JsConstants.JSMART_PROGRESSBAR;

public final class ProgressBarTagHandler extends TagHandler {

	private static final Integer DEFAULT_MAX = 100;

	private static final Integer DEFAULT_MIN = 0;

	private String value;

	private Integer intValue;

	private String maxValue;

	private Integer intMaxValue;

	private String minValue;

	private Integer intMinValue;

    private Integer interval;

    private String onInterval;
    
    private boolean withLabel;

    private boolean striped;

    private boolean animated;
    
    private String look;

    private String minWidth;

    private Integer relation;

	@Override
	public void validateTag() throws JspException {
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
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();

		if (parent instanceof ProgressGroupTagHandler) {
			((ProgressGroupTagHandler) parent).addBar(this);
			return false;
		}
		return true;
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		JspTag parent = getParent();
		ProgressGroupTagHandler group = null; 

		// Just call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		setRandomId("progressbar");

		Div progress = null;

		if (!(parent instanceof ProgressGroupTagHandler)) {
			progress = new Div();
			progress.addAttribute("class", Bootstrap.PROGRESS);
			
			// Initiate integer values because parent will not call it
			initIntValues(false);
		} else {
			// In this case the group parent will call initIntValues before calling executeTag
			group = (ProgressGroupTagHandler) parent;
		}

		int percent = ((100 * (intValue - intMinValue) / (intMaxValue - intMinValue)) | 0);
		
		if (relation != null) {
			percent = ((percent * relation / 100) | 0);
		}

		String name = getTagName(J_TAG, value);

		Div bar = new Div();
		bar.addAttribute("id", id)
			.addAttribute("name", name)
			.addAttribute("class", Bootstrap.PROGRESS_BAR)
			.addAttribute("role", "progressbar")
			.addAttribute("aria-valuenow", intValue)
			.addAttribute("aria-valuemin", intMinValue)
			.addAttribute("aria-valuemax", intMaxValue)
            .addAttribute("role-relation", relation)
			.addAttribute("style", "width:" + percent + "%;");

		if (minWidth != null) {
			bar.addAttribute("style", "min-width:" + minWidth + ";");
		}

		String lookVal = (String) getTagValue(look);
		bar.addAttribute("class", getProgressBarLook(lookVal));

		if (striped || animated) {
			bar.addAttribute("class", Bootstrap.PROGRESS_BAR_STRIPED);
		}
		if (animated) {
			bar.addAttribute("class", Bootstrap.ACTIVE);
		}
		if (withLabel) {
			bar.addText(percent + "%");
		}

		if (progress != null) {
			progress.addAttribute("class", getTagValue(styleClass))
				.addAttribute("style", getTagValue(style));
		} else {
			bar.addAttribute("class", getTagValue(styleClass))
				.addAttribute("style", getTagValue(style));
		}

		appendEvent(bar);

		// Hidden input must be included to be captured on request parameters
		Input hidden = null;

		if ((isEL(value) || rest != null) && (group == null || !group.containsBarValue(value))) {

			// Control to avoid duplicated hidden input tags per value on group
			if (group != null) {
				group.addBarValue(value);
			}

			hidden = new Input();
			hidden.addAttribute("type", "hidden")
				.addAttribute("name", name)
				.addAttribute("value", intValue);
			
			appendRest(hidden, name);
		}

		appendAjax(id);
		appendBind(id);

		appendTooltip(bar);
		appendPopOver(bar);
		
		if (onInterval != null) {
			appendDocScript(getIntervalScript());
		}

		if (progress != null) {
			return progress.addTag(bar).addTag(hidden); 
		} else {
			Set set = new Set();
			return set.addTag(bar).addTag(hidden);
		}
	}

	Integer initIntValues(boolean onGroup) throws JspException {
		intMinValue = getValue(minValue, DEFAULT_MIN);
		intValue = getValue(value, intMinValue);
		intMaxValue = getValue(maxValue, DEFAULT_MAX);
		
		if (!onGroup && intMinValue >= intMaxValue) {
			throw InvalidAttributeException.fromConstraint("progressbar", "minValue", "less than [maxValue] attribute value");
		}
		if (!onGroup && intValue < intMinValue) {
			throw InvalidAttributeException.fromConstraint("progressbar", "value", "greater or equal to [minValue] attribute value");
		}
		if (!onGroup && intValue > intMaxValue) {
			throw InvalidAttributeException.fromConstraint("progressbar", "value", "less or equal to [maxValue] attribute value");
		}
		return Math.abs(intMaxValue - intMinValue);
	}

	private StringBuilder getIntervalScript() {
		Progress jsonProgress = new Progress();
		jsonProgress.setId(id);
		jsonProgress.setMethod("get");
		jsonProgress.setRequest(ajax);
		jsonProgress.setInterval(interval);
		jsonProgress.setOnInterval(onInterval);
		return new StringBuilder(JSMART_PROGRESSBAR.format(getJsonValue(jsonProgress)));
	}

	private Integer getValue(String value, Integer defaultValue) {
		Object obj = getTagValue(value);

		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		} else if (obj instanceof String) {
			return Integer.parseInt((String) obj);
		} else {
			return defaultValue;
		}
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

	void setRelation(Integer relation) {
		this.relation = relation;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public void setOnInterval(String onInterval) {
		this.onInterval = onInterval;
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

	public void setMinWidth(String minWidth) {
		this.minWidth = minWidth;
	}

}
