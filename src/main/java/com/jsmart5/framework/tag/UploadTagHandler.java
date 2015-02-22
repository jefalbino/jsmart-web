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
import com.jsmart5.framework.tag.html.Set;
import com.jsmart5.framework.tag.html.Tag;

public final class UploadTagHandler extends SmartTagHandler {
	
	private static final String SMALL = "small";

	private static final String LARGE = "large";

	private static final String FILE_TYPE = "file";

	private String value;

	private String label;
	
	private String size;

	private Integer tabIndex;
	
	private boolean disabled;
	
	private String leftAddOn;
	
	private String rightAddOn;

	private String placeHolder;
	
	private boolean readOnly;

	private boolean autoFocus;
	
	private SmartTagHandler childAddOn;

	@Override
	public void validateTag() throws JspException {
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
		
		final String name = getTagName(J_FILE, value);
		
		// Hidden input must be included to be captured on request parameters	
		Input hidden = new Input();
		hidden.addAttribute("type", "hidden")
			.addAttribute("name", name);

		Input input = new Input();
		input.addAttribute("id", id)
			 .addAttribute("name", name.replace(J_FILE, J_PART))
			 .addAttribute("type", FILE_TYPE)
			 .addAttribute("style", style)
			 .addAttribute("class", Bootstrap.FORM_CONTROL)
			 .addAttribute("tabindex", tabIndex)
			 .addAttribute("readonly", readOnly ? readOnly : null)
			 .addAttribute("disabled", disabled ? "disabled" : null)
			 .addAttribute("placeholder", getTagValue(placeHolder))
			 .addAttribute("datatype", FILE_TYPE)
			 .addAttribute("autofocus", autoFocus ? autoFocus : null);
		
		input.addAttribute("value", getTagValue(value));
		
		if (SMALL.equals(size)) {
			input.addAttribute("class", Bootstrap.INPUT_SMALL);
		} else if (LARGE.equals(size)) {
			input.addAttribute("class", Bootstrap.INPUT_LARGE);
		}

		// Add the style class at last
		input.addAttribute("class", styleClass);
		
		appendValidator(input);
		appendRest(input);
		appendEvent(input);
		
		if (inputGroup != null) {
			inputGroup.addTag(input);
			inputGroup.addTag(hidden);
		} else if (formGroup != null) {
			formGroup.addTag(input);
			formGroup.addTag(hidden);
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

		Set set = new Set();
		set.addTag(input);
		set.addTag(hidden);

		return formGroup != null ? formGroup : inputGroup != null ? inputGroup : set;
	}

	void setChildAddOn(SmartTagHandler childAddOn) {
		this.childAddOn = childAddOn;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setLeftAddOn(String leftAddOn) {
		this.leftAddOn = leftAddOn;
	}

	public void setRightAddOn(String rightAddOn) {
		this.rightAddOn = rightAddOn;
	}

	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setAutoFocus(boolean autoFocus) {
		this.autoFocus = autoFocus;
	}
}
