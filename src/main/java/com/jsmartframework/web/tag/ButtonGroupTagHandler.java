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

import com.jsmartframework.web.exception.InvalidAttributeException;
import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.css.Bootstrap;
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Size;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

public class ButtonGroupTagHandler extends TagHandler {

	private boolean inline = true;

	private String size;

	@Override
	public void validateTag() throws JspException {
		if (size != null && !Size.validate(size)) {
			throw InvalidAttributeException.fromPossibleValues("buttongroup", "size", Size.getValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		StringWriter sw = new StringWriter();
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}

		setRandomId("buttongroup");

		Div buttonGroup = new Div();
		buttonGroup.addAttribute("id", id)
				.addAttribute("role", "group")
				.addAttribute("style", style);

		if (inline) {
			buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP);
		} else {
			buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_VERTICAL);
		}

		if (Size.XSMALL.equalsIgnoreCase(size)) {
			buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_XSMALL);
		} else if (Size.SMALL.equalsIgnoreCase(size)) {
			buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_SMALL);
		} else if (Size.LARGE.equalsIgnoreCase(size)) {
			buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_LARGE);
		} else if (Size.JUSTIFIED.equalsIgnoreCase(size)) {
			buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_JUSTIFIED);
		}

		buttonGroup.addText(sw.toString());
		
		appendTooltip(buttonGroup);
		appendPopOver(buttonGroup);

		return buttonGroup;
	}

	public void setInline(boolean inline) {
		this.inline = inline;
	}

	public void setSize(String size) {
		this.size = size;
	}

}
