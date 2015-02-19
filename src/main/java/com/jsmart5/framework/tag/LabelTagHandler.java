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

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Span;

public final class LabelTagHandler extends SmartTagHandler {

	private String target;

	private String value;

	private String look;

	@Override
	public void validateTag() throws JspException {
		if (look != null && !look.equalsIgnoreCase(DEFAULT) && !look.equalsIgnoreCase(PRIMARY) && !look.equalsIgnoreCase(SUCCESS)
				&& !look.equalsIgnoreCase(INFO) && !look.equalsIgnoreCase(WARNING) && !look.equalsIgnoreCase(DANGER)) {
			throw new JspException("Invalid look value for label tag. Valid values are " + DEFAULT + ", " + PRIMARY 
					+ ", " + SUCCESS + ", " + INFO + ", " + WARNING + ", " + DANGER);
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		if (id == null) {
			id = getRandonId();
		}

		Span span = new Span();
		span.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("for", (String) getTagValue(target))
			.addAttribute("class", Bootstrap.LABEL)
			.addText((String) getTagValue(value));

		String lookVal = Bootstrap.LABEL_DEFAULT;

		if (PRIMARY.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.LABEL_PRIMARY;
		} else if (SUCCESS.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.LABEL_SUCCESS;
		} else if (INFO.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.LABEL_INFO;
		} else if (WARNING.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.LABEL_WARNING;
		} else if (DANGER.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.LABEL_DANGER;
		}
		
		span.addAttribute("class", lookVal);

		// Add the style class at last
		span.addAttribute("class", styleClass);

		appendEvent(span);

		if (!ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : ajaxTags) {
				appendScript(ajax.getFunction(id));
			}
		}

		printOutput(span.getHtml());
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
