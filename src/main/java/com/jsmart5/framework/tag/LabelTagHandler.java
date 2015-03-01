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

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Look;

public final class LabelTagHandler extends TagHandler {

	private String target;

	private String value;

	private String look;

	@Override
	public void validateTag() throws JspException {
		if (look != null && !Look.validateLook(look)) {
			throw InvalidAttributeException.fromPossibleValues("label", "look", Look.getLookValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		setRandomId("label");

		Span span = new Span();
		span.addAttribute("style", style)
			.addAttribute("for", getTagValue(target))
			.addAttribute("class", Bootstrap.LABEL)
			.addText(getTagValue(value));
		
		appendRefId(span, id);

		String lookVal = Bootstrap.LABEL_DEFAULT;

		if (Look.PRIMARY.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.LABEL_PRIMARY;
		} else if (Look.SUCCESS.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.LABEL_SUCCESS;
		} else if (Look.INFO.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.LABEL_INFO;
		} else if (Look.WARNING.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.LABEL_WARNING;
		} else if (Look.DANGER.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.LABEL_DANGER;
		}
		
		span.addAttribute("class", lookVal);

		// Add the style class at last
		span.addAttribute("class", styleClass);

		appendEvent(span);
		appendAjax(id);
		appendBind(id);

		return span;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setLook(String look) {
		this.look = look;
	}

}
