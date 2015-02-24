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

import com.jsmart5.framework.json.JsonAjax;
import com.jsmart5.framework.json.JsonParam;
import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.html.Ul;
import com.jsmart5.framework.tag.type.Event;

import static com.jsmart5.framework.tag.js.JsConstants.*;

public final class ListTagHandler extends SmartTagHandler {

	private String var;

	private String values;

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

		Object object = getTagValue(values);
		if (object instanceof Collection<?>) {
			Iterator<Object> iterator = ((Collection<Object>) object).iterator();

			long selectIndex = 0;
			while (iterator.hasNext()) {
				request.setAttribute(var, iterator.next());
				for (RowTagHandler row : rows) {
					if (selectValue != null) {
	 					row.setSelectValue(selectValue);
	 					row.setSelectIndex(selectIndex);
					}
 					setEvents(row);
 					ul.addTag(row.executeTag());
 				}
				selectIndex++;
				request.removeAttribute(var);
			}
		}
		
		appendDelegateAjax(id, selectValue != null ? "a" : "li");
		appendDelegateBind(id, selectValue != null ? "a" : "li");

		if (selectValue != null) {
			appendScript(id, getAjaxFunction());
		}

		return ul;
	}

	private StringBuilder getAjaxFunction() {
		JsonAjax jsonAjax = new JsonAjax();
		jsonAjax.setId(id);
		jsonAjax.setMethod("post");
		jsonAjax.addParam(new JsonParam(getTagName(J_SEL, selectValue), getTagName(J_SEL, values)));
		jsonAjax.addParam(new JsonParam(getTagName(J_SEL_VAL, selectValue), ""));

		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_LIST.format(getJsonValue(jsonAjax)));
		return getDelegateFunction(id, "a", Event.CLICK.name(), builder);
	}

	void addRow(RowTagHandler row) {
		rows.add(row);
	}

	public void setValues(String values) {
		this.values = values;
	}

	public void setSelectValue(String selectValue) {
		this.selectValue = selectValue;
	}

	public void setVar(String var) {
		this.var = var;
	}

}
