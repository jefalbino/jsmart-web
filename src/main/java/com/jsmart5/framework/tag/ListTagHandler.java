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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.adapter.ListAdapter;
import com.jsmart5.framework.config.Constants;
import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.json.Ajax;
import com.jsmart5.framework.json.Param;
import com.jsmart5.framework.json.Scroll;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Li;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.html.Ul;
import com.jsmart5.framework.tag.type.Event;

import static com.jsmart5.framework.tag.js.JsConstants.*;

public final class ListTagHandler extends TagHandler {

	private String var;

	private String values;

	private String selectValue;
	
	private Integer scrollSize;
	
	private String maxHeight;

	private final List<RowTagHandler> rows;

	public ListTagHandler() {
		rows = new ArrayList<RowTagHandler>();
	}

	@Override
	public void validateTag() throws JspException {
		if (scrollSize != null && scrollSize <= 0) {
			throw InvalidAttributeException.fromConstraint("list", "scrollSize", "greater than zero");
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

		setRandomId("list");

		HttpServletRequest request = getRequest();

		Ul ul = new Ul();
		ul.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("style", maxHeight != null ? "max-height: " + maxHeight + ";" : null)
			.addAttribute("class", Bootstrap.LIST_GROUP)
			.addAttribute("class", styleClass);

		if (loadTag != null) {
			Li li = new Li();
			li.addAttribute("class", Bootstrap.LIST_GROUP_ITEM)
				.addAttribute("style", "display: none;")
				.addAttribute("style", "text-align: center;");

			li.addTag(loadTag.executeTag());
			ul.addTag(li);
		}

		if (scrollSize != null) {
			ul.addAttribute("style", "overflow: auto;")
				.addAttribute("scroll-size", scrollSize);
		}

		appendEvent(ul);

		// Get the scroll parameters case requested by scroll list
		Scroll jsonScroll = null;

		Object object = request.getAttribute(Constants.REQUEST_LIST_ADAPTER);
		if (object == null) {
			// It means that a scroll maybe happened
			String scrollParam = request.getParameter(getTagName(J_SCROLL, fakeTagName(id)));

			if (scrollParam != null) {
				jsonScroll =  GSON.fromJson(scrollParam, Scroll.class);
			}
			object = getListContent(getTagValue(values), jsonScroll);

		} else {
			// It means that the select on list was performed and the content was 
			// loaded via adapter
			String scrollParam = request.getParameter(getTagName(J_SCROLL, selectValue));

			if (scrollParam != null) {
				jsonScroll =  GSON.fromJson(scrollParam, Scroll.class);
			}
		}

		if (object instanceof List<?>) {
			Iterator<Object> iterator = ((List<Object>) object).iterator();

			int scrollIndex = jsonScroll != null ? jsonScroll.getIndex() : 0;
			int selectIndex = scrollIndex;
			
			while (iterator.hasNext()) {
				request.setAttribute(var, iterator.next());
				for (RowTagHandler row : rows) {
					if (selectValue != null) {
	 					row.setSelectValue(selectValue);
	 					row.setScrollIndex(scrollIndex);
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
	
	@SuppressWarnings("unchecked")
	private List<?> getListContent(Object object, Scroll jsonScroll) {
		int index = jsonScroll != null ? jsonScroll.getIndex() : 0;

		if (object instanceof ListAdapter) {
			ListAdapter<Object> listAdapter = (ListAdapter<Object>) object;

			return listAdapter.load(index, scrollSize);

		} else if (object instanceof List) {
			List<Object> list = (List<Object>) object;
			Object[] array = list.toArray();

			List<Object> retList = new ArrayList<Object>();

 	 		int size = index + scrollSize >= list.size() ? list.size() : (int) (index + scrollSize);

 	 		for (int i = index; i < size; i++) {
 	 			retList.add(array[i]);
 	 		}
 	 		return retList;
		}
		return Collections.EMPTY_LIST;
	}

	private StringBuilder getAjaxFunction() {
		Ajax jsonAjax = new Ajax();

		jsonAjax.setId(id);
		jsonAjax.setMethod("post");
		jsonAjax.addParam(new Param(getTagName(J_SEL, selectValue), getTagName(J_VALUES, values)));
		jsonAjax.addParam(new Param(getTagName(J_SEL_VAL, selectValue), ""));

		if (scrollSize != null) {
			jsonAjax.addParam(new Param(getTagName(J_SCROLL, selectValue), ""));
		}

		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_LIST.format(getJsonValue(jsonAjax)));
		return getDelegateFunction(id, "a", Event.CLICK.name(), builder);
	}
	
	private StringBuilder getScrollFunction() {
		Ajax jsonAjax = new Ajax();
		jsonAjax.setId(id);
		jsonAjax.setMethod("post");
		jsonAjax.addParam(new Param(getTagName(J_SCROLL, fakeTagName(id)), ""));

		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_LISTSCROLL.format(getJsonValue(jsonAjax)));
		return builder;
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

	public void setMaxHeight(String maxHeight) {
		this.maxHeight = maxHeight;
	}

}
