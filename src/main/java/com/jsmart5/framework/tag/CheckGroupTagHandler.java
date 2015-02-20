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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.html.Div;

public final class CheckGroupTagHandler extends SmartTagHandler {

	protected final List<CheckTagHandler> checks;

	private String align;

	private String value;

	private boolean inline;

	private boolean ajax;

	private boolean async = false;

	public CheckGroupTagHandler() {
		checks = new ArrayList<CheckTagHandler>();
	}

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public void executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		if (id == null) {
			id = getRandonId();
		}
		
		Div div = new Div();
		div.addAttribute("id", id)
			.addAttribute("align", align);

 		int index = 0;
		for (CheckTagHandler check : checks) {
			StringWriter sw = new StringWriter();

			check.setId(id + "_" + index++);
			check.setStyle(style);
			check.setStyleClass(styleClass);
			check.setInline(inline);
			check.setValidator(validator);
			check.setRest(rest);
			check.setName(value);
			check.setAjax(ajax);
			check.setAsync(async);
			check.addAllAjaxTag(ajaxTags);
			check.setType(CheckTagHandler.CHECKBOX);
			setEvents(check);
			check.setOutputWriter(sw);
			check.executeTag();

			div.addText(sw.toString());
		}

		printOutput(div.getHtml());
	}

	void addCheck(CheckTagHandler check) {
		this.checks.add(check);
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setInline(boolean inline) {
		this.inline = inline;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

}
