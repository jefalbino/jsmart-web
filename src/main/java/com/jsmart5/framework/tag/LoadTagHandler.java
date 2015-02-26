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
import com.jsmart5.framework.tag.type.Output;

public final class LoadTagHandler extends TagHandler {

	private String label;

	private String type;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof TagHandler) {

			((TagHandler) parent).setLoadTag(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (type != null && !Output.validateHeader(type)) {
			throw InvalidAttributeException.fromPossibleValues("load", "type", Output.getHeaderValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		setRandomId("load");
		
		Tag header = null;
		if (type != null) {
			header = new Tag(type);
		} else {
			header = new Tag(Output.H3.name().toLowerCase());
		}

		header.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", styleClass);

		Span span = new Span();
		span.addAttribute("refresh-icon", "")
			.addAttribute("class", Bootstrap.GLYPHICON)
			.addAttribute("class", Bootstrap.GLYPHICON_REFRESH)
			.addAttribute("class", Bootstrap.GLYPHICON_ANIMATE)
			.addAttribute("aria-hidden", "true");

		header.addTag(span);
		
		if (label != null) { 
			header.addText(" ")
				.addText(getTagValue(label));
		}
		
		appendAjax(id);
		appendBind(id);

		return header;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setType(String type) {
		this.type = type;
	}

}
