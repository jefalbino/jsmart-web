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
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Align;
import com.jsmart5.framework.tag.type.Look;

public final class IconTagHandler extends TagHandler {

	private String name;

	private String side = Align.LEFT.name().toLowerCase();
	
	private String look;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();

		if (parent instanceof ButtonTagHandler) {
			((ButtonTagHandler) parent).addIconTag(this);
			return false;

		} else if (parent instanceof LinkTagHandler) {
			((LinkTagHandler) parent).addIconTag(this);
			return false;

		} else if (parent instanceof DropDownTagHandler) {
			((DropDownTagHandler) parent).addIconTag(this);
			return false;

		} else if (parent instanceof DropActionTagHandler) {
			((DropActionTagHandler) parent).addIconTag(this);
			return false;

		} else if (parent instanceof HeaderTagHandler) {
			((HeaderTagHandler) parent).addIconTag(this);
			return false;

		} else if (parent instanceof OutputTagHandler) {
			((OutputTagHandler) parent).addIconTag(this);
			return false;

		} else if (parent instanceof TabPaneTagHandler) {
			((TabPaneTagHandler) parent).addIconTag(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (side != null && !Align.validateLeftRight(side)) {
			throw InvalidAttributeException.fromPossibleValues("icon", "side", Align.getLeftRightValues());
		}
		if (look != null && !Look.validateLook(look) && !isEL(look)) {
			throw InvalidAttributeException.fromPossibleValues("icon", "look", Look.getLookValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
		
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		setRandomId("icon");

		Span span = new Span();
		span.addAttribute("style", style)
			.addAttribute("class", Bootstrap.GLYPHICON)
			.addAttribute("class", getTagValue(name))
			.addAttribute("aria-hidden", "true")
			.addAttribute("side", side);
		
		appendRefId(span, id);
		
		String lookVal = (String) getTagValue(look);

		if (Look.PRIMARY.equalsIgnoreCase(lookVal)) {
			span.addAttribute("class", Bootstrap.TEXT_PRIMARY);
		} else if (Look.SUCCESS.equalsIgnoreCase(lookVal)) {
			span.addAttribute("class", Bootstrap.TEXT_SUCCESS);
		} else if (Look.INFO.equalsIgnoreCase(lookVal)) {
			span.addAttribute("class", Bootstrap.TEXT_INFO);
		} else if (Look.WARNING.equalsIgnoreCase(lookVal)) {
			span.addAttribute("class", Bootstrap.TEXT_WARNING);
		} else if (Look.DANGER.equalsIgnoreCase(lookVal)) {
			span.addAttribute("class", Bootstrap.TEXT_DANGER);
		}
		
		// At last place the style class
		span.addAttribute("class", styleClass);

		appendAjax(id);
		appendBind(id);

		return span;
	}

	public void setName(String name) {
		this.name = name;
	}

	String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public void setLook(String look) {
		this.look = look;
	}
}
