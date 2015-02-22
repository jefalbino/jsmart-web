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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.html.Ul;

public final class ListTagHandler extends SmartTagHandler {

	private String var;

	private String value;

	private String selectValue;

	private final List<RowTagHandler> rows;

	public ListTagHandler() {
		rows = new ArrayList<RowTagHandler>();
	}

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@SuppressWarnings("unchecked")
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

		HttpServletRequest request = getRequest();

		Ul ul = new Ul();
		ul.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.LIST_GROUP)
			.addAttribute("class", styleClass);

		appendEvent(ul);

		Object object = getTagValue(value);
		if (object instanceof Collection<?>) {

			Collection<Object> collection = (Collection<Object>) object;

			if (!collection.isEmpty()) {

//				String command = ajaxCommand;
//
// 				if (select != null) {
// 					JsonParams jsonParams = new JsonParams();
// 					jsonParams.addParam(new JsonParam(getTagName(J_SEL, select), getTagName(J_SEL, value)));
// 					jsonParams.addParam(new JsonParam(getTagName(J_SEL_VAL, select), "%s"));
//
// 					String parameters = "ajaxoutside=\"" + getJsonValue(jsonParams) + "\" ";
//
// 					if (command != null) {
// 						if (command.startsWith(ON_CLICK)) {
// 							if (command.contains(JSMART_AJAX.toString())) {
// 								command += parameters;
// 							} else {
// 								command = command.replace(ON_CLICK, ON_CLICK + JSMART_LIST.format(async, "$(this)")) + parameters;
// 							}
// 						} else {
// 							command += ON_CLICK + JSMART_LIST.format(async, "$(this)") + "\" " + parameters;
// 						}
// 					} else {
// 						command = ON_CLICK + JSMART_LIST.format(async, "$(this)") + "\" " + parameters;
// 					}
// 				}

				Iterator<Object> iterator = collection.iterator();
				
				while (iterator.hasNext()) {
					request.setAttribute(var, iterator.next());

					for (RowTagHandler row : rows) {
	 					row.setSelectValue(selectValue);
	 					row.addAllAjaxTag(ajaxTags);
	 					setEvents(row);

//	 					if (command != null) {
//	 						if (select != null) {
//	 							row.setAjaxCommand(String.format(command, i));
//	 						} else {
//	 							row.setAjaxCommand(command);
//	 						}
//	 					}

	 					ul.addTag(row.executeTag());
	 				}

					request.removeAttribute(var);
				}
			}
		}
		
		return ul;
	}

	void addRow(RowTagHandler row) {
		rows.add(row);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setSelectValue(String selectValue) {
		this.selectValue = selectValue;
	}

	public void setVar(String var) {
		this.var = var;
	}

}
