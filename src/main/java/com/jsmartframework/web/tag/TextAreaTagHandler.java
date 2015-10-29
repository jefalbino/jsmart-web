/*
 * JSmart Framework - Java Web Development Framework
 * Copyright (c) 2014, Jeferson Albino da Silva, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
*/

package com.jsmartframework.web.tag;

import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.css.Bootstrap;
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Label;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.html.TextArea;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class TextAreaTagHandler extends TagHandler {

	private Integer rows;

	private Integer cols;
	
	private String label;

	private Integer length;

	private String value;

	private boolean readOnly;

	private String placeholder;

	private Integer tabIndex;

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}
		
		setRandomId("textarea");

		Div formGroup = null;

		JspTag parent = getParent();
		if (label != null || parent instanceof FormTagHandler || parent instanceof RestTagHandler) {
			formGroup = new Div();
			formGroup.addAttribute("class", Bootstrap.FORM_GROUP);
		}

		if (label != null) {
			Label labelTag = new Label();
			labelTag.addAttribute("for", id)
					.addAttribute("class", Bootstrap.LABEL_CONTROL)
					.addText(getTagValue(label));
			formGroup.addTag(labelTag);
		}

        String name = getTagName(J_TAG, value) + (readOnly ? EL_PARAM_READ_ONLY : "");

		TextArea textArea = new TextArea();
		textArea.addAttribute("name", name)
			 .addAttribute("style", getTagValue(style))
			 .addAttribute("class", Bootstrap.FORM_CONTROL)
			 .addAttribute("rows", rows)
			 .addAttribute("cols", cols)
			 .addAttribute("tabindex", tabIndex)
			 .addAttribute("maxlength", length)
			 .addAttribute("readonly", readOnly ? readOnly : null)
			 .addAttribute("disabled", isDisabled() ? "disabled" : null)
			 .addAttribute("placeholder", getTagValue(placeholder))
			 .addText(getTagValue(value));
		
		appendRefId(textArea, id);
		
		// Add the style class at last
		textArea.addAttribute("class", getTagValue(styleClass));

		appendValidator(textArea);
		appendRest(textArea, name);
		appendEvent(textArea);
		
		appendTooltip(textArea);
		appendPopOver(textArea);

		if (formGroup != null) {
			formGroup.addTag(textArea);
		}

		appendAjax(id);
		appendBind(id);

		return formGroup != null ? formGroup : textArea;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public void setCols(Integer cols) {
		this.cols = cols;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

}
