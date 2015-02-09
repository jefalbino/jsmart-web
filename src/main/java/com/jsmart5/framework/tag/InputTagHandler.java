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

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Input;
import com.jsmart5.framework.tag.html.Span;

public final class InputTagHandler extends SmartTagHandler {

	private static final String TEXT_TYPE = "text";

	private static final String PASSWORD_TYPE = "password";

	private static final String HIDDEN_TYPE = "hidden";

	private static final String NUMBER_TYPE = "number";

	private static final String SEARCH_TYPE = "search";

	private static final String RANGE_TYPE = "range";

	private static final String EMAIL_TYPE = "email";

	private static final String URL_TYPE = "url";
	
	private static final String DATE_TYPE = "date";

	private static final String MONTH_TYPE = "month";

	private static final String WEEK_TYPE = "week";

	private static final String TIME_TYPE = "time";

	private static final String DATETIME_TYPE = "datetime";

	private static final String DATETIME_LOCAL_TYPE = "datetime-local";

	private static final String COLOR_TYPE = "color";

	private static final String PHONE_TYPE = "tel";
	
	private String type;

	private Integer length;

	private Integer size;

	private String value;

	private boolean readOnly;

	private boolean autoFocus;

	private Integer tabIndex;

	private String mask;

	private String placeHolder;

	private String label;

	private String pattern;

	private Integer minValue;

	private Integer maxValue;

	private Integer stepValue;

	private boolean disabled;

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
		switch (type) {
			case TEXT_TYPE:
			case PASSWORD_TYPE:
			case HIDDEN_TYPE:
			case NUMBER_TYPE:
			case SEARCH_TYPE:
			case RANGE_TYPE:
			case EMAIL_TYPE:
			case URL_TYPE:
			case DATE_TYPE:
			case MONTH_TYPE:
			case WEEK_TYPE:
			case TIME_TYPE:
			case DATETIME_TYPE:
			case DATETIME_LOCAL_TYPE:
			case COLOR_TYPE:
			case PHONE_TYPE:
				break;
			default:
				throw new JspException("Invalid type value for input tag. Valid values are "
						+ TEXT_TYPE + ", " + PASSWORD_TYPE + ", " + HIDDEN_TYPE + ", " + NUMBER_TYPE + ", " 
						+ SEARCH_TYPE + ", " + RANGE_TYPE + ", " + EMAIL_TYPE + ", " + URL_TYPE + ", " 
						+ DATE_TYPE + ", " + MONTH_TYPE + ", " + WEEK_TYPE + ", " + TIME_TYPE + ", "
						+ DATETIME_TYPE + ", " + DATETIME_LOCAL_TYPE + ", " + COLOR_TYPE + ", " + PHONE_TYPE);
		}

		if (id == null && mask != null) {
			throw new JspException("Attribute id must be provided case mask is set for input tag");
		}
		
		if (maxValue != null && minValue == null) {
			throw new JspException("Attribute minValue must be specified case maxValue is specified for input tag");
		}
		if (minValue != null && maxValue == null) {
			throw new JspException("Attribute maxValue must be specified case minValue is specified for input tag");
		}
		if (stepValue != null && stepValue <= 0) {
			throw new JspException("Attribute stepValue must be greater than zero for input tag");
		}
		if (maxValue != null && minValue != null && minValue >= maxValue) {
			throw new JspException("Attribute minValue must be less than maxValue for input tag");
		}
		if (maxValue != null && minValue != null && stepValue != null && stepValue > (maxValue - minValue)) {
			throw new JspException("Attribute stepValue must be less than the difference of maxValue and minValue for input tag");
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		Div div = new Div();
		String addonId = getRandonId();

		if (label != null) {
			div.addAttribute("class", Bootstrap.INPUT_GROUP);

			Span span = new Span();
			span.addAttribute("id", "basic-addon-" + addonId)
				.addAttribute("class", Bootstrap.INPUT_GROUP_ADDON)
				.setText((String) getTagValue(label));

			div.addTag(span);
		}

		Input input = new Input();
		input.addAttribute("id", id)
			 .addAttribute("name", getTagName(J_TAG, value) + (readOnly ? EL_PARAM_READ_ONLY : ""))
			 .addAttribute("type", type)
			 .addAttribute("style", style)
			 .addAttribute("class", Bootstrap.FORM_CONTROL)
			 .addAttribute("class", styleClass)
			 .addAttribute("tabindex", tabIndex)
			 .addAttribute("maxlength", length)
			 .addAttribute("size", size)
			 .addAttribute("readonly", readOnly ? readOnly : null)
			 .addAttribute("disabled", disabled || isEditRowTagEnabled() ? "disabled" : null)
			 .addAttribute("placeholder", getResourceString(placeHolder))
			 .addAttribute("datatype", type)
			 .addAttribute("pattern", pattern)
			 .addAttribute("autofocus", autoFocus ? autoFocus : null);
		
		if (label != null) {
			input.addAttribute("aria-describedby", "basic-addon-" + addonId);
			div.addTag(input);
		}

		if (NUMBER_TYPE.equals(type) || RANGE_TYPE.equals(type)) {
			input.addAttribute("min", minValue)
				 .addAttribute("max", maxValue)
				 .addAttribute("step", stepValue);
		}
		
		if (!PASSWORD_TYPE.equals(type)) {
			input.addAttribute("value", getTagValue(value));
		} else {
			setTagValue(value, null);
		}

//		appendFormValidator(builder);

//		appendRest(builder);

//		if (ajaxCommand != null) {
//			if (ajaxCommand.startsWith(ON_BLUR) && NUMBER_TYPE.equals(type)) {
//				builder.append(ajaxCommand.replace(ON_BLUR, ON_BLUR + JSMART_NUMBER.format("$(this)")));
//				builder.append(ajaxCommand.replace(ON_FOCUS, ON_FOCUS + JSMART_BACKUP_NUMBER.format("$(this)")));
//			} else {
//				if (NUMBER_TYPE.equals(type)) {
//					builder.append(ON_BLUR + JSMART_NUMBER.format("$(this)") + "\" ");
//					builder.append(ON_FOCUS + JSMART_BACKUP_NUMBER.format("$(this)") + " return false;\" ");
//				}
//				builder.append(ajaxCommand);
//			}
//		} else {
//			if (NUMBER_TYPE.equals(type)) {
//				builder.append(ON_BLUR + JSMART_NUMBER.format("$(this)") + "\" ");
//				builder.append(ON_FOCUS + JSMART_BACKUP_NUMBER.format("$(this)") + " return false;\" ");
//			}
//		}
//
//		appendEvent(builder);

		printOutput(label != null ? div.getHtml() : input.getHtml());
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setAutoFocus(boolean autoFocus) {
		this.autoFocus = autoFocus;
	}

	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	public void setStepValue(Integer stepValue) {
		this.stepValue = stepValue;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
