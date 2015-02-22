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
import com.jsmart5.framework.tag.html.FieldSet;
import com.jsmart5.framework.tag.html.Section;
import com.jsmart5.framework.tag.html.Tag;

public final class PanelBodyTagHandler extends SmartTagHandler {
	
	private static final String LEFT = "left";
	
	private static final String RIGHT = "right";
	
	private static final String CENTER = "center";

	private static final String FIELDSET_TYPE = "fieldset";

	private static final String SECTION_TYPE = "section";

	private String align;

	private String type;

	public void validateTag() throws JspException {
		if (type != null && !FIELDSET_TYPE.equalsIgnoreCase(type) && !SECTION_TYPE.equalsIgnoreCase(type)) {
			throw new JspException("Invalid type value for panel tag. Valid values are "
					+ FIELDSET_TYPE + " and " + SECTION_TYPE);
		}
		if (align != null && !LEFT.equalsIgnoreCase(align) && !RIGHT.equalsIgnoreCase(align) && !CENTER.equalsIgnoreCase(align)) {
			throw new JspException("Invalid align attribute for panel tag. Valid values are "
					+ LEFT + ", " + RIGHT + ", " + CENTER);
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

		Tag content = null;

		if (FIELDSET_TYPE.equals(type)) {
			content = new FieldSet();
		} else if (SECTION_TYPE.equals(type)) {
			content = new Section();
		} else {
			content = new Div();
		}

		content.addAttribute("id", id)
			.addAttribute("align", align)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.PANEL_BODY)
			.addAttribute("class", styleClass)
			.addText(sw.toString());

		appendEvent(content);

		if (!ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : ajaxTags) {
				appendScript(ajax.getFunction(id));
			}
		}

		return content;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setType(String type) {
		this.type = type;
	}

}
