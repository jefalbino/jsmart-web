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
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;

public final class IconTagHandler extends SmartTagHandler {

	static final String LEFT = "left";
	
	static final String RIGHT = "right";

	private String name;

	private String side = LEFT;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof SmartTagHandler) {

			((SmartTagHandler) parent).addIconTag(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (side != null && !side.equalsIgnoreCase(LEFT) && !side.equalsIgnoreCase(RIGHT)) {
			throw new JspException("Invalid side value for icon tag. Valid values are " + LEFT + ", " + RIGHT);
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
		Span span = new Span();
		span.addAttribute("class", Bootstrap.GLYPHICON)
			.addAttribute("class", getTagValue(name))
			.addAttribute("aria-hidden", "true");
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

}
