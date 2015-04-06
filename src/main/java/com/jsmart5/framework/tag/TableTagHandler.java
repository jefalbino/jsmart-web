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

import com.jsmart5.framework.adapter.TableAdapter;
import com.jsmart5.framework.config.Constants;
import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.json.Ajax;
import com.jsmart5.framework.json.Param;
import com.jsmart5.framework.json.Scroll;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.css.JSmart5;
import com.jsmart5.framework.tag.html.Caption;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.TBody;
import com.jsmart5.framework.tag.html.THead;
import com.jsmart5.framework.tag.html.Table;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.html.Td;
import com.jsmart5.framework.tag.html.Tr;
import com.jsmart5.framework.tag.type.Event;
import com.jsmart5.framework.tag.type.Look;
import com.jsmart5.framework.tag.type.Size;

import static com.jsmart5.framework.tag.js.JsConstants.*;

public final class TableTagHandler extends TagHandler {

	private String var;

	private String values;

	private String selectValue;
	
	private String selectLook;

	private String caption;

	private Integer scrollSize;

	private String maxHeight;
	
	private String size;

	private boolean bordered;

	private boolean striped;
	
	private String update;
	
	private String beforeSend;
	
	private String onError;
	
	private String onSuccess;

	private String onComplete;

	private final List<ColumnTagHandler> columns;

	public TableTagHandler() {
		columns = new ArrayList<ColumnTagHandler>();
	}

	@Override
	public void validateTag() throws JspException {
		if (scrollSize != null && scrollSize <= 0) {
			throw InvalidAttributeException.fromConstraint("table", "scrollSize", "greater than zero");
		}
		if (scrollSize != null && maxHeight == null) {
			throw InvalidAttributeException.fromConflict("table", "maxHeight", "Attribute [maxHeight] must be specified");
		}
		if (size != null && !Size.validateSmallLarge(size)) {
			throw InvalidAttributeException.fromPossibleValues("table", "size", Size.getSmallLargeValues());
		}
		if (selectLook != null && !Look.validateBasic(selectLook) && !isEL(selectLook)) {
			throw InvalidAttributeException.fromPossibleValues("table", "selectLook", Look.getBasicValues());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Tag executeTag() throws JspException, IOException {
		
		// Need to indicate that it is a table parent tag for deep inner tags
		// so the ajax and bind actions can be set by this class
		pushDelegateTagParent();

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		setRandomId("table");

		HttpServletRequest request = getRequest();

		Div div = new Div();
		div.addAttribute("class", Bootstrap.TABLE_RESPONSIVE);

		Table table = new Table();
		table.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.TABLE)
			.addAttribute("class", bordered ? Bootstrap.TABLE_BORDERED : null)
			.addAttribute("class", striped ? Bootstrap.TABLE_STRIPED : null)
			.addAttribute("class", selectValue != null ? Bootstrap.TABLE_HOVER : null)
			.addAttribute("class", scrollSize != null ? JSmart5.TABLE_SCROLL : null)
			.addAttribute("style", maxHeight != null ? "height: " + maxHeight + ";" : null);

		if (selectValue != null) {
			Object lookVal = getTagValue(selectLook);
			table.addAttribute("select-look", lookVal != null ? lookVal : Bootstrap.ACTIVE);
		}

		if (Size.SMALL.equalsIgnoreCase(size)) {
			table.addAttribute("class", Bootstrap.TABLE_CONDENSED);
		}
		table.addAttribute("class", styleClass);

		if (caption != null) {
			Caption captionTag = new Caption();
			captionTag.addText(getTagValue(caption));
			table.addTag(captionTag);
		}

		THead thead = new THead();
		TBody tbody = new TBody();
		tbody.addAttribute("style", maxHeight != null ? "max-height: " + maxHeight + ";" : null);
		
		if (scrollSize != null) {
			tbody.addAttribute("scroll-size", scrollSize);
		}

		if (loadTag != null) {
			Tr tr = new Tr();
			Td td = new Td();
			td.addAttribute("style", "display: none;")
				.addAttribute("style", "text-align: center;")
				.addAttribute("colspan", columns.size())
				.addTag(loadTag.executeTag());

			tr.addTag(td);
			tbody.addTag(tr);
		}

		// Get the scroll parameters case requested by scroll table
		Scroll jsonScroll = null;
		
		Object object = request.getAttribute(Constants.REQUEST_TABLE_ADAPTER);
		if (object == null) {
			// It means that a scroll maybe happened
			String scrollParam = request.getParameter(getTagName(J_SCROLL, fakeTagName(id)));

			if (scrollParam != null) {
				jsonScroll = GSON.fromJson(scrollParam, Scroll.class);
			}
			object = getTableContent(getTagValue(values), jsonScroll);

		} else {
			// It means that the select on table was performed and the content was 
			// loaded via adapter
			String scrollParam = request.getParameter(getTagName(J_SCROLL, selectValue));

			if (scrollParam != null) {
				jsonScroll = GSON.fromJson(scrollParam, Scroll.class);
			}
		}

		if (object instanceof List<?>) {
			Iterator<Object> iterator = ((List<Object>) object).iterator();

			int scrollIndex = jsonScroll != null ? jsonScroll.getIndex() : 0;
			int selectIndex = scrollIndex;

			while (iterator.hasNext()) {
				request.setAttribute(var, iterator.next());

				Tr tr = new Tr();
				tr.addAttribute("scroll-index", scrollIndex);

				if (selectValue != null) {
					tr.addAttribute("style", "cursor: pointer;")
						.addAttribute("table-index", selectIndex);
				}

				appendEvent(tr);
				appendAjax(id);
				appendBind(id);

				for (ColumnTagHandler column : columns) {
					tr.addTag(column.executeTag());
				}

				tbody.addTag(tr);
				selectIndex++;
				request.removeAttribute(var);
			}
		}

		// Call after reading the column inner tag headers
		if (!columns.isEmpty()) {
			Tr tr = new Tr();
			thead.addTag(tr);
			for (ColumnTagHandler column : columns) {
				if (column.getHeader() != null) {
					tr.addTag(column.getHeader().executeTag());
				}
			}
		}

		// Needs to pop the iterator action so this class set the 
		// ajax and bind actions carried via RefAction
		popDelegateTagParent();

		if (selectValue != null) {
			appendScript(getAjaxFunction());
		}
		if (scrollSize != null) {
			appendScript(getScrollFunction());
		}

		div.addTag(table);
		table.addTag(thead)
			.addTag(tbody);

		return div;
	}

	@SuppressWarnings("unchecked")
	private List<?> getTableContent(Object object, Scroll jsonScroll) throws JspException {
		int index = jsonScroll != null ? jsonScroll.getIndex() : 0;

		if (object instanceof TableAdapter) {
			if (scrollSize == null) {
				throw InvalidAttributeException.fromConflict("table", "scrollSize",
						"Attribute [scrollSize] must be specified to use TableAdapter");
			}

			TableAdapter<Object> tableAdapter = (TableAdapter<Object>) object;
			return tableAdapter.load(index, scrollSize, null, 0, null); // TODO

		} else if (object instanceof List) {
			List<Object> list = (List<Object>) object;
			Object[] array = list.toArray();

			List<Object> retList = new ArrayList<Object>();

			int size = list.size();
			if (scrollSize != null) {
				size = index + scrollSize >= list.size() ? list.size() : (int) (index + scrollSize);
			}

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
		jsonAjax.setTag("table");

		jsonAjax.addParam(new Param(getTagName(J_SEL, selectValue), getTagName(J_VALUES, values)));
		jsonAjax.addParam(new Param(getTagName(J_SEL_VAL, selectValue), ""));

		if (scrollSize != null) {
			jsonAjax.addParam(new Param(getTagName(J_SCROLL, selectValue), ""));
		}
		if (update != null) {
			jsonAjax.setUpdate(update.trim());
		}
		if (beforeSend != null) {
			jsonAjax.setBefore((String) getTagValue(beforeSend.trim()));
		}
		if (onError != null) {
			jsonAjax.setError((String) getTagValue(onError.trim()));
		}
		if (onSuccess != null) {
			jsonAjax.setSuccess((String) getTagValue(onSuccess.trim()));
		}
		if (onComplete != null) {
			jsonAjax.setComplete((String) getTagValue(onComplete.trim()));
		}

		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_TABLE.format(getJsonValue(jsonAjax)));
		return getDelegateFunction(id, "tr", Event.CLICK.name(), builder);
	}

	private StringBuilder getScrollFunction() {
		Ajax jsonAjax = new Ajax();
		jsonAjax.setId(id);
		jsonAjax.setMethod("post");
		jsonAjax.setTag("tablescroll");

		jsonAjax.addParam(new Param(getTagName(J_SCROLL, fakeTagName(id)), ""));

		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_TABLESCROLL.format(getJsonValue(jsonAjax)));
		return builder;
	}

	void addColumn(ColumnTagHandler column) {
		this.columns.add(column);
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setValues(String values) {
		this.values = values;
	}

	public void setSelectValue(String selectValue) {
		this.selectValue = selectValue;
	}

	public void setSelectLook(String selectLook) {
		this.selectLook = selectLook;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setScrollSize(Integer scrollSize) {
		this.scrollSize = scrollSize;
	}

	public void setMaxHeight(String maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setBordered(boolean bordered) {
		this.bordered = bordered;
	}

	public void setStriped(boolean striped) {
		this.striped = striped;
	}

}
