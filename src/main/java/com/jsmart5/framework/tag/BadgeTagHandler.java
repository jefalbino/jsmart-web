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
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;

public final class BadgeTagHandler extends TagHandler {

	private String label;

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
		
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		setRandomId("badge");

		Span span = new Span();
		span.addAttribute("style", getTagValue(style))
			.addAttribute("class", Bootstrap.BADGE)
			.addAttribute("class", getTagValue(styleClass))
                .addText(getTagValue(label));

		appendTooltip(span);
		appendPopOver(span);
		
		appendRefId(span, id);
		appendAjax(id);
		appendBind(id);

		return span;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
