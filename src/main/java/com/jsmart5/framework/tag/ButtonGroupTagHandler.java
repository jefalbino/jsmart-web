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
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Size;

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
		
		appendAjax(id);
		appendBind(id);

		return buttonGroup;
	}

	public void setInline(boolean inline) {
		this.inline = inline;
	}

	public void setSize(String size) {
		this.size = size;
	}

}
