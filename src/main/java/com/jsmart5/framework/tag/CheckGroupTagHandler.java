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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Tag;

public final class CheckGroupTagHandler extends SmartTagHandler {

	protected final List<CheckTagHandler> checks;

	private String align;

	private String selectValues;

	private boolean inline;

	private boolean ajax;

	public CheckGroupTagHandler() {
		checks = new ArrayList<CheckTagHandler>();
	}

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

		if (id == null) {
			id = getRandonId();
		}
		
		Div div = new Div();
		div.addAttribute("id", id)
			.addAttribute("align", align)
			.addAttribute("checkgroup", "");

 		int index = 0;
		for (CheckTagHandler check : checks) {

			check.setId(id + "_" + index++);
			check.setStyle(style);
			check.setStyleClass(styleClass);
			check.setInline(inline);
			check.setValidator(validator);
			check.setRest(rest);
			check.setName(selectValues);
			check.setAjax(ajax);
			check.addAllAjaxTag(ajaxTags);
			check.setType(CheckTagHandler.CHECKBOX);
			setEvents(check);

			div.addTag(check.executeTag());
		}

		return div;
	}

	void addCheck(CheckTagHandler check) {
		this.checks.add(check);
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setSelectValues(String selectValues) {
		this.selectValues = selectValues;
	}

	public void setInline(boolean inline) {
		this.inline = inline;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

}
