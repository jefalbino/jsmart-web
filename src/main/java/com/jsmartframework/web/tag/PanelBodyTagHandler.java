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
import com.jsmartframework.web.tag.html.FieldSet;
import com.jsmartframework.web.tag.html.Section;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Align;
import com.jsmartframework.web.tag.type.Type;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.io.StringWriter;

public final class PanelBodyTagHandler extends TagHandler {

	private String align;

	private String type;

	public void validateTag() throws JspException {
		if (type != null && !Type.validatePanel(type)) {
			throw InvalidAttributeException.fromPossibleValues("panelbody", "type", Type.getPanelValues());
		}
		if (align != null && !Align.validateLeftRightCenter(align)) {
			throw InvalidAttributeException.fromPossibleValues("panelbody", "align", Align.getLeftRightCenterValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		StringWriter sw = new StringWriter();
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}
		
		setRandomId("panelbody");

		Tag content = null;

		if (Type.FIELDSET.equalsIgnoreCase(type)) {
			content = new FieldSet();
		} else if (Type.SECTION.equalsIgnoreCase(type)) {
			content = new Section();
		} else {
			content = new Div();
		}

		content.addAttribute("id", id)
			.addAttribute("align", align)
			.addAttribute("style", getTagValue(style))
			.addAttribute("class", Bootstrap.PANEL_BODY)
			.addAttribute("class", getTagValue(styleClass))
                .addText(sw.toString());

		appendEvent(content);

		return content;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setType(String type) {
		this.type = type;
	}

}
