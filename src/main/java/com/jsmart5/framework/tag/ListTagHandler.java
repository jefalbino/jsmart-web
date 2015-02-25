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

import com.jsmart5.framework.exception.InvalidAttributeException;
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
	
	private Integer scrollSize;
	
	private Integer maxHeight;

	private final List<RowTagHandler> rows;

	public ListTagHandler() {
		rows = new ArrayList<RowTagHandler>();
	}

	@Override
	public void validateTag() throws JspException {
		if (scrollSize != null && scrollSize <= 0) {
			throw InvalidAttributeException.fromConstraint("list", "scrollSize", "greater than zero");
		}
		if (maxHeight != null && maxHeight <= 0) {
			throw InvalidAttributeException.fromConstraint("list", "maxHeight", "greater than zero");
		}
		if (scrollSize != null && maxHeight == null) {
			throw InvalidAttributeException.fromConflict("list", "maxHeight", "Attribute [maxHeight] must be specified");
		}
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
			.addAttribute("style", maxHeight != null ? "max-height: " + maxHeight + " px;" : null)
			.addAttribute("class", Bootstrap.LIST_GROUP)
			.addAttribute("class", styleClass);

		if (scrollSize != null) {
			ul.addAttribute("style", "overflow: auto;")
				.addAttribute("scroll-size", scrollSize);
		}

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
			appendScript(getAjaxFunction());
		}
		
		if (scrollSize != null) {
			appendScript(getScrollFunction());
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
	
	private StringBuilder getScrollFunction() {
		JsonAjax jsonAjax = new JsonAjax();
		jsonAjax.setId(id);
		jsonAjax.setMethod("post");
		
		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_LISTSCROLL.format(getJsonValue(jsonAjax)));
		return getBindFunction(id, Event.SCROLL.name(), builder);
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

	public void setScrollSize(Integer scrollSize) {
		this.scrollSize = scrollSize;
	}

	public void setMaxHeight(Integer maxHeight) {
		this.maxHeight = maxHeight;
	}

}
