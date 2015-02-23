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
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.FieldSet;
import com.jsmart5.framework.tag.html.Form;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Event;
import com.jsmart5.framework.tag.type.Method;
import com.jsmart5.framework.tag.type.Position;
import com.jsmart5.framework.tag.type.Size;

import static com.jsmart5.framework.tag.js.JsConstants.*;

public final class FormTagHandler extends SmartTagHandler {

	private String method;
	
	private String enctype;
	
	private boolean disabled;
	
	private String position;
	
	private String size;

	@Override
	public void validateTag() throws JspException {
		if (method != null && !Method.validatePostGet(method)) {
			throw InvalidAttributeException.fromPossibleValues("form", "method", Method.getPostGetValues());
		}
		if (position != null && !Position.validate(position)) {
			throw InvalidAttributeException.fromPossibleValues("form", "position", Position.getValues());
		}
		if (size != null && !Size.validateSmallLarge(size)) {
			throw InvalidAttributeException.fromPossibleValues("form", "size", Size.getSmallLargeValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		StringWriter sw = new StringWriter();
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}
		
		if (id == null) {
			id = getRandonId();
		}

		Form form = new Form();
		form.addAttribute("id", id)
			.addAttribute("method", method != null ? method : Method.POST.name().toLowerCase())
			.addAttribute("enctype", enctype)
			.addAttribute("style", style);

		if (Position.HORIZONTAL.name().equalsIgnoreCase(position)) {
			form.addAttribute("class", Bootstrap.FORM_HORIZONTAL);
		} else if (Position.INLINE.name().equalsIgnoreCase(position)) {
			form.addAttribute("class", Bootstrap.FORM_INLINE);
		}
		
		// Add the style class at last
		form.addAttribute("class", styleClass);

		FieldSet fieldSet = null;
		if (disabled) {
			fieldSet = new FieldSet();
			fieldSet.addAttribute("disabled", "disabled");
			form.addTag(fieldSet);
		}

		if (fieldSet != null) {
			fieldSet.addText(sw.toString());
		} else {
			form.addText(sw.toString());
		}

		appendBind(id);
		appendScript(getFunction());

		return form;
	}
	
	private StringBuilder getFunction() {
		StringBuilder builder = new StringBuilder();
		builder.append("return " + JSMART_VALIDATE.format(id));
		return getBindFunction(id, Event.SUBMIT.name(), builder);
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setEnctype(String enctype) {
		this.enctype = enctype;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

}