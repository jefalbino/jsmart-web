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
import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.json.Ajax;
import com.jsmart5.framework.json.Param;
import com.jsmart5.framework.json.Scroll;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
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

    private String scrollOffset;
	
	private String maxHeight;
	
	private String update;
	
	private String beforeSend;
	
	private String onError;
	
	private String onSuccess;

	private String onComplete;

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
        if (values != null && var == null) {
            throw InvalidAttributeException.fromConflict("list", "var", "Attribute [var] must be specified case [values] is specified");
        }
	}

	@SuppressWarnings("unchecked")
	@Override
	public Tag executeTag() throws JspException, IOException {

		// Need to indicate that it is a list parent tag for deep inner tags
		// so the ajax and bind actions can be set by this class
		pushDelegateTagParent();

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		HttpServletRequest request = getRequest();

		Ul ul = new Ul();
		ul.addAttribute("id", id)
			.addAttribute("style", getTagValue(style))
			.addAttribute("style", maxHeight != null ? "max-height: " + maxHeight + ";" : null)
			.addAttribute("class", Bootstrap.LIST_GROUP)
			.addAttribute("class", getTagValue(styleClass));

        if (scrollSize != null || values == null) {
            ul.addAttribute("style", "overflow: auto;")
                    .addAttribute("scroll-size", scrollSize);
        }

        appendEvent(ul);

		if (loadTag != null) {
			Li li = new Li();
			li.addAttribute("role-load", "")
                .addAttribute("class", Bootstrap.LIST_GROUP_ITEM)
				.addAttribute("style", "display: none;")
				.addAttribute("style", "text-align: center;")
                .addAttribute("style", "border: 1px solid transparent;");

			li.addTag(loadTag.executeTag());
			ul.addTag(li);
		}

        if (emptyTag != null) {
            Li li = new Li();
            li.addAttribute("id", emptyTag.id)
                .addAttribute("role-empty", "")
                .addAttribute("class", Bootstrap.LIST_GROUP_ITEM)
                .addAttribute("style", "display: none; text-align: center;")
                .addAttribute("style", "border: 1px solid transparent;")
                .addAttribute("style", getTagValue(emptyTag.style))
                .addAttribute("class", getTagValue(emptyTag.styleClass));

            li.addText(emptyTag.getContent());
            ul.addTag(li);
        }

        // Append row template to be used by js functions to add, update and remove rows
        appendRowTemplate(ul);

		// Get the scroll parameters case requested by scroll list
		Scroll scroll = null;

        // It means that a scroll maybe happened
        String scrollParam = request.getParameter(getTagName(J_SCROLL, fakeTagName(id)));
        if (scrollParam != null) {
            scroll = GSON.fromJson(scrollParam, Scroll.class);

        } else {
            // It means that the select on list was performed
            scrollParam = request.getParameter(getTagName(J_SCROLL, selectValue));

            if (scrollParam != null) {
                scroll = GSON.fromJson(scrollParam, Scroll.class);
            }
        }

        Object object = getListContent(getTagValue(values), scroll);

		if (object instanceof List<?>) {
			Iterator<Object> iterator = ((List<Object>) object).iterator();

			int scrollIndex = scroll != null && scroll.getIndex() != null ? scroll.getIndex() : 0;
			int selectIndex = scrollIndex;

			while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj == null) {
                    continue;
                }
				request.setAttribute(var, obj);

				for (RowTagHandler row : rows) {
					row.setSelectable(selectValue != null);
					row.setSelectIndex(selectIndex);
					row.setScrollIndex(scrollIndex);
 					setEvents(row);

                    Tag rowTag = row.executeTag();
                    Object scrollOffsetVal = getTagValue(scrollOffset);
                    if (scrollOffsetVal != null) {
                        rowTag.addAttribute("scroll-offset", scrollOffsetVal);
                    }
 					ul.addTag(rowTag);
 				}
				selectIndex++;
				request.removeAttribute(var);
			}
		}

		// Needs to pop the iterator action so this class set the 
		// ajax and bind actions carried via RefAction
		popDelegateTagParent();

		if (selectValue != null) {
			appendDocScript(getAjaxFunction());
		}
		if (scrollSize != null) {
			appendDocScript(getScrollFunction());
		}
		return ul;
	}
	
	@SuppressWarnings("unchecked")
	private List<?> getListContent(Object object, Scroll scroll) throws JspException {
		int index = scroll != null && scroll.getIndex() != null ? scroll.getIndex() : 0;
        Object offset = scroll != null ? scroll.getOffset() : null;

		if (object instanceof ListAdapter) {
			if (scrollSize == null) {
				throw InvalidAttributeException.fromConflict("list", "scrollSize",
						"Attribute [scrollSize] must be specified to use ListAdapter");
			}
			
			ListAdapter<Object> listAdapter = (ListAdapter<Object>) object;
			return listAdapter.load(index, offset, scrollSize);

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

    private void appendRowTemplate(Tag ul) throws JspException, IOException {
        for (int i = 0; i < rows.size(); i++) {
            RowTagHandler row = rows.get(i);
            row.setSelectable(selectValue != null);
            setEvents(row);

            Tag rowTag = row.executeTag();
            rowTag.addAttribute("style", "display: none;")
                .addAttribute("role-template", i);
            ul.addTag(rowTag);
        }
    }

	private StringBuilder getAjaxFunction() {
		Ajax jsonAjax = new Ajax();

		jsonAjax.setId(id);
		jsonAjax.setMethod("post");
		jsonAjax.setTag("list");

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
		builder.append(JSMART_LIST.format(getJsonValue(jsonAjax)));
		return getDelegateFunction(id, "a", Event.CLICK.name(), builder);
	}
	
	private StringBuilder getScrollFunction() {
		Ajax jsonAjax = new Ajax();
		jsonAjax.setId(id);
		jsonAjax.setMethod("post");
		jsonAjax.setTag("listscroll");

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

    public void setScrollOffset(String scrollOffset) {
        this.scrollOffset = scrollOffset;
    }

    public void setMaxHeight(String maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public void setBeforeSend(String beforeSend) {
		this.beforeSend = beforeSend;
	}

	public void setOnError(String onError) {
		this.onError = onError;
	}

	public void setOnSuccess(String onSuccess) {
		this.onSuccess = onSuccess;
	}

	public void setOnComplete(String onComplete) {
		this.onComplete = onComplete;
	}

}
