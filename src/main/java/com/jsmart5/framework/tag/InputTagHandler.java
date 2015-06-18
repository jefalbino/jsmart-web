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
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Input;
import com.jsmart5.framework.tag.html.Label;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Size;
import com.jsmart5.framework.tag.type.Type;

public final class InputTagHandler extends TagHandler {
	
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

	private TagHandler childAddOn;

	@Override
	public void validateTag() throws JspException {
		if (type != null && !Type.validateInput(type)) {
			throw InvalidAttributeException.fromPossibleValues("input", "type", Type.getInputValues());
		}
		if (maxValue != null && minValue == null) {
			throw InvalidAttributeException.fromConstraint("input", "minValue", "must be specified case maxValue is specified");
		}
		if (minValue != null && maxValue == null) {
			throw InvalidAttributeException.fromConstraint("input", "maxValue", "specified case minValue is specified");
		}
		if (stepValue != null && stepValue <= 0) {
			throw InvalidAttributeException.fromConstraint("input", "stepValue", "greater than 0");
		}
		if (maxValue != null && minValue != null && minValue >= maxValue) {
			throw InvalidAttributeException.fromConstraint("input", "minValue", "less than maxValue");
		}
		if (maxValue != null && minValue != null && stepValue != null && stepValue > (maxValue - minValue)) {
			throw InvalidAttributeException.fromConstraint("input", "stepValue", "less than the difference of maxValue and minValue");
		}
		if (size != null && !Size.validateSmallLarge(size)) {
			throw InvalidAttributeException.fromPossibleValues("input", "size", Size.getSmallLargeValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}
		
		setRandomId("input");

		Div formGroup = null;
		Div inputGroup = null;
		
		JspTag parent = getParent();
		if (!Type.HIDDEN.equalsIgnoreCase(type) && (label != null || parent instanceof FormTagHandler
                || parent instanceof RestTagHandler)) {
			formGroup = new Div();
			formGroup.addAttribute("class", Bootstrap.FORM_GROUP);

            String size = null;
            if (parent instanceof FormTagHandler) {
                size = ((FormTagHandler) parent).getSize();
            } else if (parent instanceof RestTagHandler) {
                size = ((RestTagHandler) parent).getSize();
            }
            if (Size.LARGE.equalsIgnoreCase(size)) {
                formGroup.addAttribute("class", Bootstrap.FORM_GROUP_LARGE);
            } else if (Size.SMALL.equalsIgnoreCase(size)) {
                formGroup.addAttribute("class", Bootstrap.FORM_GROUP_SMALL);
            }
		}

		if (!Type.HIDDEN.equalsIgnoreCase(type) && label != null) {
			Label labelTag = new Label();
			labelTag.addAttribute("for", id)
					.addAttribute("class", Bootstrap.LABEL_CONTROL)
					.addText(getTagValue(label));
			formGroup.addTag(labelTag);
		}

		if (!Type.HIDDEN.equalsIgnoreCase(type) && (leftAddOn != null || rightAddOn != null)) {
			inputGroup = new Div();
			inputGroup.addAttribute("class", Bootstrap.INPUT_GROUP);

			if (Size.SMALL.equalsIgnoreCase(size)) {
				inputGroup.addAttribute("class", Bootstrap.INPUT_GROUP_SMALL);
			} else if (Size.LARGE.equalsIgnoreCase(size)) {
				inputGroup.addAttribute("class", Bootstrap.INPUT_GROUP_LARGE);
			}

			if (formGroup != null) {
				formGroup.addTag(inputGroup);
			}
		}
		
		if (!Type.HIDDEN.equalsIgnoreCase(type) && leftAddOn != null) {
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
		input.addAttribute("name", getTagName(J_TAG, value) + (readOnly ? EL_PARAM_READ_ONLY : ""))
			 .addAttribute("type", type != null ? type : Type.TEXT.name().toLowerCase())
			 .addAttribute("class", Bootstrap.FORM_CONTROL)
			 .addAttribute("tabindex", tabIndex)
			 .addAttribute("maxlength", length)
			 .addAttribute("readonly", readOnly ? readOnly : null)
			 .addAttribute("disabled", disabled ? "disabled" : null)
			 .addAttribute("placeholder", getTagValue(placeHolder))
			 .addAttribute("datatype", type != null ? type : Type.TEXT.name().toLowerCase())
			 .addAttribute("pattern", pattern)
			 .addAttribute("autofocus", autoFocus ? autoFocus : null)
			 .addAttribute("data-mask", mask);
		
		appendRefId(input, id);
		
		if (Size.SMALL.equalsIgnoreCase(size)) {
			input.addAttribute("class", Bootstrap.INPUT_SMALL);
		} else if (Size.LARGE.equalsIgnoreCase(size)) {
			input.addAttribute("class", Bootstrap.INPUT_LARGE);
		}
		
		if (Type.NUMBER.equalsIgnoreCase(type) || Type.RANGE.equalsIgnoreCase(type)) {
			input.addAttribute("min", minValue)
				 .addAttribute("max", maxValue)
				 .addAttribute("step", stepValue);
		}
		
		// Add the style class at last
		if (inputGroup != null) {
			inputGroup.addAttribute("style", style)
				.addAttribute("class", styleClass);
		} else {
			input.addAttribute("style", style)
				.addAttribute("class", styleClass);
		}

		if (!Type.PASSWORD.equalsIgnoreCase(type)) {
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

		if (!Type.HIDDEN.equalsIgnoreCase(type) && rightAddOn != null) {
			if (childAddOn != null && rightAddOn.equalsIgnoreCase(childAddOn.getId())) {
				inputGroup.addTag(childAddOn.executeTag());
			} else {
				Div div = new Div();
				div.addAttribute("class", Bootstrap.INPUT_GROUP_ADDON)
					.addText(getTagValue(rightAddOn));
				inputGroup.addTag(div);
			}
		}

		appendAjax(id);
		appendBind(id);
		
		if (formGroup != null) {
			appendTooltip(formGroup);
			appendPopOver(formGroup);

		} else if (inputGroup != null) {
			appendTooltip(inputGroup);
			appendPopOver(inputGroup);

		} else if (!Type.HIDDEN.equalsIgnoreCase(type)) {
			appendTooltip(input);
			appendPopOver(input);
		}

		return formGroup != null ? formGroup : inputGroup != null ? inputGroup : input;
	}
	
	void setChildAddOn(TagHandler childAddOn) {
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
