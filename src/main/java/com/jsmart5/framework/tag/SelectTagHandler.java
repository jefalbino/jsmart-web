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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.json.Ajax;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Label;
import com.jsmart5.framework.tag.html.Select;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Event;
import com.jsmart5.framework.tag.type.Size;

import static com.jsmart5.framework.tag.js.JsConstants.*;

public final class SelectTagHandler extends TagHandler {

	private String selectValues;

	private boolean multiple;

	private boolean disabled;

	private Integer tabIndex;
	
	private String label;
	
	private String leftAddOn;
	
	private String rightAddOn;
	
	private String size;
	
	private String update;
	
	private String beforeSend;
	
	private String onError;
	
	private String onSuccess;

	private String onComplete;

	private List<OptionTagHandler> options;

	private TagHandler childAddOn;

	public SelectTagHandler() {
		options = new ArrayList<OptionTagHandler>();
	}

	@Override
	public void validateTag() throws JspException {
		if (size != null && !Size.validateSmallLarge(size)) {
			throw InvalidAttributeException.fromPossibleValues("select", "size", Size.getSmallLargeValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}
		
		setRandomId("select");

		Div formGroup = null;
		Div inputGroup = null;
		
		JspTag parent = getParent();
		if (label != null || parent instanceof FormTagHandler) {
			formGroup = new Div();
			formGroup.addAttribute("class", Bootstrap.FORM_GROUP);
			
			if (parent instanceof FormTagHandler) {
				String size = ((FormTagHandler) parent).getSize();

				if (Size.LARGE.equalsIgnoreCase(size)) {
					formGroup.addAttribute("class", Bootstrap.FORM_GROUP_LARGE);

				} else if (Size.SMALL.equalsIgnoreCase(size)) {
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

			if (Size.SMALL.equalsIgnoreCase(size)) {
				inputGroup.addAttribute("class", Bootstrap.INPUT_GROUP_SMALL);
			} else if (Size.LARGE.equalsIgnoreCase(size)) {
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

		Select select = new Select();
		select.addAttribute("id", id)
			 .addAttribute("class", Bootstrap.FORM_CONTROL)
			 .addAttribute("name", getTagName((multiple ? J_ARRAY : J_TAG), selectValues))
			 .addAttribute("tabindex", tabIndex)
			 .addAttribute("disabled", disabled ? "disabled" : null)
			 .addAttribute("multiple", multiple ? "multiple" : null);
		
		if (Size.SMALL.equalsIgnoreCase(size)) {
			select.addAttribute("class", Bootstrap.INPUT_SMALL);
		} else if (Size.LARGE.equalsIgnoreCase(size)) {
			select.addAttribute("class", Bootstrap.INPUT_LARGE);
		}

		// Add the style class at last
		if (inputGroup != null) {
			inputGroup.addAttribute("style", style)
				.addAttribute("class", styleClass);
		} else {
			select.addAttribute("style", style)
				.addAttribute("class", styleClass);
		}

		appendValidator(select);
		appendRest(select);
		appendEvent(select);

		if (inputGroup != null) {
			inputGroup.addTag(select);
		} else if (formGroup != null) {
			formGroup.addTag(select);
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

		if (ajax) {
			appendDocScript(getFunction());
		}

		for (OptionTagHandler option : options) {
			option.setName(selectValues);
			select.addTag(option.executeTag());
		}

		appendAjax(id);
		appendBind(id);
		
		if (formGroup != null) {
			appendTooltip(formGroup);
			appendPopOver(formGroup);

		} else if (inputGroup != null) {
			appendTooltip(inputGroup);
			appendPopOver(inputGroup);
		} else {
			appendTooltip(select);
			appendPopOver(select);
		}

		return formGroup != null ? formGroup : inputGroup != null ? inputGroup : select;
	}

	private StringBuilder getFunction() {
		Ajax jsonAjax = new Ajax();
		jsonAjax.setId(id);
		jsonAjax.setMethod("post");
		jsonAjax.setTag("select");

		if (update != null) {
			jsonAjax.setUpdate(update.trim());
		}
		if (beforeSend != null) {
			jsonAjax.setBefore((String) getTagValue(beforeSend.trim()));
		}
		if (onError != null) {
			jsonAjax.setError((String) getTagValue(onError.trim()));
		}
		if (onSuccess != null) {
			jsonAjax.setSuccess((String) getTagValue(onSuccess.trim()));
		}
		if (onComplete != null) {
			jsonAjax.setComplete((String) getTagValue(onComplete.trim()));
		}

		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_AJAX.format(getJsonValue(jsonAjax)));
		return getBindFunction(id, Event.CHANGE.name(), builder);
	}
	
	void setChildAddOn(TagHandler childAddOn) {
		this.childAddOn = childAddOn;
	}

	void addOption(OptionTagHandler option) {
		this.options.add(option);
	}

	public void setSelectValues(String selectValues) {
		this.selectValues = selectValues;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
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

	public void setSize(String size) {
		this.size = size;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public void setBeforeSend(String beforeSend) {
		this.beforeSend = beforeSend;
	}

	public void setOnError(String onError) {
		this.onError = onError;
	}

	public void setOnSuccess(String onSuccess) {
		this.onSuccess = onSuccess;
	}

	public void setOnComplete(String onComplete) {
		this.onComplete = onComplete;
	}

}
