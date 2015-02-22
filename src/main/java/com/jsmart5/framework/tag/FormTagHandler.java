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

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.FieldSet;
import com.jsmart5.framework.tag.html.Form;
import com.jsmart5.framework.tag.html.Tag;

import static com.jsmart5.framework.tag.js.JsConstants.*;

public final class FormTagHandler extends SmartTagHandler {
	
	private static final String POST = "post";
	
	private static final String GET = "get";
	
	static final String SMALL = "small";
	
	static final String LARGE = "large";

	private static final String VERTICAL = "vertical";
	
	private static final String HORIZONTAL = "horizontal";
	
	private static final String INLINE = "inline";

	private String method;
	
	private String enctype;
	
	private boolean disabled;
	
	private String position;
	
	private String size;

	@Override
	public void validateTag() throws JspException {
		if (method != null && !method.equalsIgnoreCase(GET) && !method.equalsIgnoreCase(POST)) {
			throw new JspException("Invalid method for form tag. Valid values are get or post");
		}
		if (position != null && !position.equalsIgnoreCase(VERTICAL) && !position.equalsIgnoreCase(HORIZONTAL) 
				&& !position.equalsIgnoreCase(INLINE)) {
			throw new JspException("Invalid position for form tag. Valid values are " + VERTICAL + ", "
				+ HORIZONTAL + ", " + INLINE);
		}
		if (size != null && !size.equalsIgnoreCase(SMALL) && !size.equalsIgnoreCase(LARGE)) {
			throw new JspException("Invalid size for form tag. Valid values are " + SMALL + ", " + LARGE);
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
			.addAttribute("method", method != null ? method : POST)
			.addAttribute("enctype", enctype)
			.addAttribute("style", style);

		if (HORIZONTAL.equalsIgnoreCase(position)) {
			form.addAttribute("class", Bootstrap.FORM_HORIZONTAL);
		} else if (INLINE.equalsIgnoreCase(position)) {
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

		appendScript(getFunction());

		return form;
	}
	
	private StringBuilder getFunction() {
		StringBuilder builder = new StringBuilder();
		builder.append("$('#").append(id).append("').bind('").append(EVENT_SUBMIT).append("', function(){");

		builder.append("return " + JSMART_VALIDATE.format(id));

		builder.append("});");
		return builder;
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