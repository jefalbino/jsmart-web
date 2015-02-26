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

import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.A;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Li;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;

public final class DropDownTagHandler extends TagHandler {

	private String label;
	
	private boolean navbar;
	
	private boolean disabled;
	
	private DropMenuTagHandler dropMenu;

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

		setRandomId("dropdown");

		Tag dropDown;
		if (navbar) {
			dropDown = new Li();
		} else {
			dropDown = new Div();
		}

		dropDown.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.DROPDOWN)
			.addAttribute("class", disabled ? Bootstrap.DISABLED : null)
			.addAttribute("class", styleClass);
		
		appendEvent(dropDown);
		
		A a = new A();
		a.addAttribute("href", "#")
			.addAttribute("data-toggle", "dropdown")
			.addAttribute("aria-expanded", "false")
			.addAttribute("class", Bootstrap.DROPDOWN_TOGGLE)
			.addText(getTagValue(label));
		
		Span caret = new Span();
		caret.addAttribute("class", Bootstrap.CARET);
		a.addTag(caret);

		dropDown.addTag(a);
		
		if (dropMenu != null) {
			Tag ul = dropMenu.executeTag();
			ul.addAttribute("class", disabled ? Bootstrap.DISABLED : null);
			dropDown.addTag(ul);
		}
		
		appendAjax(id);
		appendBind(id);

		return dropDown;
	}

	void setDropMenu(DropMenuTagHandler dropMenu) {
		this.dropMenu = dropMenu;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setNavbar(boolean navbar) {
		this.navbar = navbar;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}