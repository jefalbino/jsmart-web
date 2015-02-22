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
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Tag;

public class ButtonGroupTagHandler extends SmartTagHandler {

	private boolean inline = true;

	private String size;

	@Override
	public void validateTag() throws JspException {
		if (size != null && !size.equals(ButtonTagHandler.XSMALL) && !size.equals(ButtonTagHandler.SMALL) 
				&& !size.equals(ButtonTagHandler.LARGE) && !size.equals(ButtonTagHandler.JUSTIFIED)) {
			throw new JspException("Invalid size value for buttongroup tag. Valid values are " + ButtonTagHandler.XSMALL + ", " 
				+ ButtonTagHandler.SMALL + ", " + ButtonTagHandler.LARGE + ", " + ButtonTagHandler.JUSTIFIED);
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

		Div buttonGroup = new Div();
		buttonGroup.addAttribute("id", id)
				.addAttribute("role", "group")
				.addAttribute("style", style);

		if (inline) {
			buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP);
		} else {
			buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_VERTICAL);
		}

		if (ButtonTagHandler.XSMALL.equals(size)) {
			buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_XSMALL);
		} else if (ButtonTagHandler.SMALL.equals(size)) {
			buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_SMALL);
		} else if (ButtonTagHandler.LARGE.equals(size)) {
			buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_LARGE);
		} else if (ButtonTagHandler.JUSTIFIED.equals(size)) {
			buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_JUSTIFIED);
		}

		buttonGroup.addText(sw.toString());

		return buttonGroup;
	}

	public void setInline(boolean inline) {
		this.inline = inline;
	}

	public void setSize(String size) {
		this.size = size;
	}

}
