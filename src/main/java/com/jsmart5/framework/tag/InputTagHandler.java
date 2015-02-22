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
import com.jsmart5.framework.tag.html.Label;
import com.jsmart5.framework.tag.html.Tag;

public final class InputTagHandler extends SmartTagHandler {
	
	private static final String SMALL = "small";

	private static final String LARGE = "large";

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

	private String size;

	private String value;

	private boolean readOnly;

	private boolean autoFocus;

	private Integer tabIndex;

	private String mask;

	private String placeHolder;

	private String label;
	
	private String leftAddOn;
	
	private String rightAddOn;

	private String pattern;

	private Integer minValue;

	private Integer maxValue;

	private Integer stepValue;

	private boolean disabled;

	private SmartTagHandler childAddOn;

	@Override
	public void validateTag() throws JspException {
		if (type != null)
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
		if (size != null && !size.equals(SMALL) && !size.equals(LARGE)) {
			throw new JspException("Invalid size value for input tag. Valid values are " + SMALL + ", " + LARGE);
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}
		
		if (id == null) {
			id = getRandonId();
		}

		Div formGroup = null;
		Div inputGroup = null;
		
		JspTag parent = getParent();
		if (label != null || parent instanceof FormTagHandler) {
			formGroup = new Div();
			formGroup.addAttribute("class", Bootstrap.FORM_GROUP);
			
			if (parent instanceof FormTagHandler) {
				String size = ((FormTagHandler) parent).getSize();

				if (FormTagHandler.LARGE.equalsIgnoreCase(size)) {
					formGroup.addAttribute("class", Bootstrap.FORM_GROUP_LARGE);

				} else if (FormTagHandler.SMALL.equalsIgnoreCase(size)) {
					formGroup.addAttribute("class", Bootstrap.FORM_GROUP_SMALL);
				}
			}
		}

		if (label != null) {
			Label labelTag = new Label();
			labelTag.addAttribute("for", id)
					.addAttribute("class", Bootstrap.LABEL_CONTROL)
					.addText(getTagValue(label));
			formGroup.addTag(labelTag);
		}

		if (leftAddOn != null || rightAddOn != null) {
			inputGroup = new Div();
			inputGroup.addAttribute("class", Bootstrap.INPUT_GROUP);

			if (SMALL.equals(size)) {
				inputGroup.addAttribute("class", Bootstrap.INPUT_GROUP_SMALL);
			} else if (LARGE.equals(size)) {
				inputGroup.addAttribute("class", Bootstrap.INPUT_GROUP_LARGE);
			}

			if (formGroup != null) {
				formGroup.addTag(inputGroup);
			}
		}
		
		if (leftAddOn != null) {
			if (childAddOn != null && leftAddOn.equalsIgnoreCase(childAddOn.getId())) {
				inputGroup.addTag(childAddOn.executeTag());
			} else {
				Div div = new Div();
				div.addAttribute("class", Bootstrap.INPUT_GROUP_ADDON)
					.addText(getTagValue(leftAddOn));
				inputGroup.addTag(div);
			}
		}

		Input input = new Input();
		input.addAttribute("id", id)
			 .addAttribute("name", getTagName(J_TAG, value) + (readOnly ? EL_PARAM_READ_ONLY : ""))
			 .addAttribute("type", type != null ? type : TEXT_TYPE)
			 .addAttribute("style", style)
			 .addAttribute("class", Bootstrap.FORM_CONTROL)
			 .addAttribute("tabindex", tabIndex)
			 .addAttribute("maxlength", length)
			 .addAttribute("readonly", readOnly ? readOnly : null)
			 .addAttribute("disabled", disabled ? "disabled" : null)
			 .addAttribute("placeholder", getTagValue(placeHolder))
			 .addAttribute("datatype", type != null ? type : TEXT_TYPE)
			 .addAttribute("pattern", pattern)
			 .addAttribute("autofocus", autoFocus ? autoFocus : null)
			 .addAttribute("data-mask", mask);
		
		if (SMALL.equals(size)) {
			input.addAttribute("class", Bootstrap.INPUT_SMALL);
		} else if (LARGE.equals(size)) {
			input.addAttribute("class", Bootstrap.INPUT_LARGE);
		}
		
		if (NUMBER_TYPE.equals(type) || RANGE_TYPE.equals(type)) {
			input.addAttribute("min", minValue)
				 .addAttribute("max", maxValue)
				 .addAttribute("step", stepValue);
		}
		
		// Add the style class at last
		input.addAttribute("class", styleClass);
		
		if (!PASSWORD_TYPE.equals(type)) {
			input.addAttribute("value", getTagValue(value));
		} else {
			setTagValue(value, null);
		}

		appendValidator(input);
		appendRest(input);
		appendEvent(input);

		if (inputGroup != null) {
			inputGroup.addTag(input);
		} else if (formGroup != null) {
			formGroup.addTag(input);
		}

		if (rightAddOn != null) {
			if (childAddOn != null && rightAddOn.equalsIgnoreCase(childAddOn.getId())) {
				inputGroup.addTag(childAddOn.executeTag());
			} else {
				Div div = new Div();
				div.addAttribute("class", Bootstrap.INPUT_GROUP_ADDON)
					.addText(getTagValue(rightAddOn));
				inputGroup.addTag(div);
			}
		}

		if (!ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : ajaxTags) {
				appendScript(ajax.getFunction(id));
			}
		}

		return formGroup != null ? formGroup : inputGroup != null ? inputGroup : input;
	}
	
	void setChildAddOn(SmartTagHandler childAddOn) {
		this.childAddOn = childAddOn;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public void setSize(String size) {
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

	public void setLeftAddOn(String leftAddOn) {
		this.leftAddOn = leftAddOn;
	}

	public void setRightAddOn(String rightAddOn) {
		this.rightAddOn = rightAddOn;
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
