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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.jsmart5.framework.json.JSONEdit;
import com.jsmart5.framework.json.JSONScroll;
import com.jsmart5.framework.json.JSONSelect;
import com.jsmart5.framework.json.JSONTable;
import com.jsmart5.framework.manager.SmartTableTagHandler;

import static com.jsmart5.framework.tag.CssConstants.*;
import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.JSConstants.*;

/*
 * Table uses a json structure to pass data over post request
 * 
 * Action
 * {
 *   "action": "FIRST/PREV/NEXT/LAST/NUMBER/SORT/FILTER",
 *   "first": "",
 *   "sort": {"name": "", "order": ""},
 *   "filters": [{"name": "", "field": "", "value": ""}, ...]
 * }
 * 
 * Select
 * {
 *   "select": "",
 *   "type": "SINGLE/MULTI",
 *   "indexes": [],
 *   "expand": true/false,
 *   "first": "",
 *   "size": "",
 *   "sort": {"name": "", "order": ""},
 *   "filters": [{"name": "", "field": "", "value": ""}, ...]
 * }
 * 
 * Edit
 * {
 * 	  "edit": "",
 *    "index": "",
 *    "varname": "",
 *    "values": [{"name": "", "value": ""}, ...],
 *    "first": "",
 *    "size": "",
 *    "sort": {"name": "", "order": ""},
 *    "filters": [{"name": "", "field": "", "value": ""}, ...]
 * }
 */
public final class TableTagHandler extends SmartTableTagHandler {

	private static final String MULTI_SELECT_ALL_ID = "_multi_select_all";

	private static final String MULTI_SELECT_ITEM_ID = "_multi_select_item_";

	private static final String SELECT_ROW_ITEM_ID = "_select_row_item_";

	private static final String EDIT_CELL_ROW_ITEM_ID = "_edit_cell_row_item_";

	private static final String EDIT_CELL_START_ITEM_ID = "_edit_cell_start_item_";

	private static final String EDIT_CELL_CONFIRM_ITEM_ID = "_edit_cell_confirm_item_";

	private static final String EDIT_CELL_CANCEL_ITEM_ID = "_edit_cell_cancel_item_";

	private static final String MULTI_SELECT_ALL_INDEX = "all";

	private static final String INPUT_FILTER_ID = "_filter";

	private static final String PAGINATOR_BOTH = "both";

	private static final String PAGINATOR_BOTTOM = "bottom";

	private static final String PAGINATOR_TOP = "top";

	private static final String SORT_ASCENDENT = "asc";

	private static final String SORT_DESCENDENT = "desc";

	private static final int MAXIMUM_PAGES = 10;

	private static final int DEFAULT_PAGE_SIZE = 10;

	private static final String TOP_POSITION = "top";

	private static final String BOTTOM_POSITION = "bottom";

	private static final String ID_WRAPPER = "_wrapper";

	private String align;

	private String position = TOP_POSITION;

	private String var;

	private String value;

	private String summary;

	private String emptyMessage;

	private String emptyStyle;

	private String emptyClass;

	private String cellPadding;

	private String cellSpacing;

	private boolean multiSelect = false;

	private String select;

	private String edition;

	private String paginator;

	private boolean scrollable;

	private int pageSize = DEFAULT_PAGE_SIZE;

	private boolean async = true;

	private ExpandTagHandler itemExpand;

	private final List<ColumnTagHandler> items;

	private Collection<Object> collectionItems;

	private SmartTableAdapter<Object> tableAdapter;

	private long collectionIndex = DEFAULT_VALUE;

	private JSONObject jsonAction;

	public TableTagHandler() {
		items = new ArrayList<ColumnTagHandler>();
	}

	@Override
	public void validateTag() throws JspException {
		if (paginator != null && scrollable) {
			throw new JspException("Attribute scrollable and paginator cannot coexist for table tag");
		}

		if (scrollable && multiSelect) {
			throw new JspException("Attribute scrollable and multiSelect cannot coexist for table tag");
		}

		if (!TOP_POSITION.equals(position) && !BOTTOM_POSITION.equals(position)) {
			throw new JspException("Invalid position value for table tag. Valid position values are top or bottom");
		}

		if (scrollable && BOTTOM_POSITION.equals(position)) {
			throw new JspException("Attribute scrollable and position bottom cannot coexist for table tag");
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {
		try {
			JspFragment body = getJspBody();
			if (body != null) {
				body.invoke(null);
			}

			if (scrollable && itemExpand != null) {
				throw new JspException("Attribute scrollable and internal tag itemexpand cannot coexist for table tag");
			}

			HttpServletRequest request = getRequest();
	
		 	StringBuilder builder = new StringBuilder();
	
		 	builder.append(OPEN_DIV_TAG + "id=\"" + id + ID_WRAPPER + "\" ");
	
		 	if (align != null) {
		 		builder.append("align=\"" + align + "\" ");
			}
	
			builder.append(">");

			String tagParamName = getTagName(J_TBL, "@{" + id + "}");
			if (request.getParameter(tagParamName) != null) {
				jsonAction = new JSONObject(request.getParameter(tagParamName));
			} else {
				String selectParamName = getTagName(J_TBL_SEL, select);
				if (request.getParameter(selectParamName) != null) {
					jsonAction = new JSONObject(request.getParameter(selectParamName));
				} else {
					String editParamName = getTagName(J_TBL_EDT, edition);
					if (request.getParameter(editParamName) != null) {
						jsonAction = new JSONObject(request.getParameter(editParamName));
					}
				}
			}

			// The content must be placed here to get size value to create paginator pages
	 	 	List<Object> list = getTableContent(getTagValue(value), (pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE));

			// Pagination data for header and footer
	 	 	StringBuilder pgtr = getPaginatorWrapper();

			// Add header pagination
			if (paginator != null && (paginator.equals(PAGINATOR_BOTH) || paginator.equals(PAGINATOR_TOP))) {
				builder.append(OPEN_DIV_TAG + CSS_TABLE_PAGE_HEADER + ">");
				builder.append(pgtr);
				builder.append(CLOSE_DIV_TAG);
			}

			// Start building table
			builder.append(OPEN_TABLE_TAG + "id=\"" + id + "\" ");

			if (summary != null) {
				builder.append("summary=\"" + getResourceString(summary) + "\" ");
			}
			if (style != null) {
				builder.append("style=\"" + style + "\" ");
			}

			builder.append(styleClass != null ? "class=\"" + styleClass + "\" " : CSS_TABLE);

			if (cellPadding != null) {
				builder.append("cellpadding=\""+ cellPadding + "\" ");
 	 		}
 	 		if (cellSpacing != null) {
 	 			builder.append("cellspacing=\"" + cellSpacing + "\" ");
 	 		} else {
 	 			builder.append("cellspacing=\"0\" ");
 	 		}

 	 		// Include ITEM_EXPAND_MARKER to be changed by indexes later and to allow refresh by js on client after any update
 	 		if (itemExpand != null) {
 	 			builder.append("expandtable=\"expandtable\" expandstatus=\"" + getActionExpand(jsonAction) + "\" ");
 	 		}

 	 		// Save all indexes previously selected to keep track of them
 	 		builder.append("indexes=\"" + getSelectIndexes() + "\" ");

 	 		// Check if table is scrollable to include script to perform ajax updates on scroll
 	 		if (scrollable) {
 	 			appendScriptBuilder(new StringBuilder(JSMART_TABLE_SCROLL.format(id)));

 	 			JSONScroll jsonScroll = new JSONScroll();
 	 			jsonScroll.setName(getTagName(J_TBL, "@{" + id + "}"));
 	 			jsonScroll.setAction(ACTION.NEXT.toString());
 	 			jsonScroll.setFirst(String.valueOf(getPageFirst()));
 	 			jsonScroll.setEnd(String.valueOf((getTotalSize() - pageSize) <= getPageFirst()));
 	 			jsonScroll.setFilter(getActionFilter(jsonAction));
 	 			jsonScroll.setSort(getActionSort(jsonAction));

 	 			builder.append("ajaxscroll=\"" + getJSONValue(jsonScroll) + "\" ");
			}

			builder.append(">");

			// Check if all columns are selectable to avoid placing script in every column
			boolean allColumnsSelectable = true;

			if (TOP_POSITION.equals(position)) {
				StringBuilder header = new StringBuilder(OPEN_TABLE_HEAD_TAG + CSS_TABLE_HEAD + ">");
				header.append(OPEN_TABLE_ROW_TAG + CSS_TABLE_HEAD_ROW + ">");

				if (multiSelect) {
					header.append(OPEN_TABLE_HEAD_COLUMN_TAG + CSS_TABLE_HEAD_MULTI_SELECT_COLUMN + ">");
					appendHeaderMultiSelect(header);
					header.append(CLOSE_TABLE_HEAD_COLUMN_TAG);
				}

				for (ColumnTagHandler item : items) {
					allColumnsSelectable &= item.isSelectable();
		 			header.append(OPEN_TABLE_HEAD_COLUMN_TAG + CSS_TABLE_HEAD_ROW_COLUMN + ">");
		 			appendHeaderContent(header, item);
		 			header.append(CLOSE_TABLE_HEAD_COLUMN_TAG);
			 	}

				if (edition != null) {
					header.append(OPEN_TABLE_HEAD_COLUMN_TAG + CSS_TABLE_HEAD_EDIT_CELL_COLUMN + ">");
					header.append(CLOSE_TABLE_HEAD_COLUMN_TAG);
				}

				header.append(CLOSE_TABLE_ROW_TAG + CLOSE_TABLE_HEAD_TAG);

				// Add header into table
			 	builder.append(header);

			} else if (BOTTOM_POSITION.equals(position)) {
				StringBuilder footer = new StringBuilder(OPEN_TABLE_FOOT_TAG + CSS_TABLE_FOOT + ">");
				footer.append(OPEN_TABLE_ROW_TAG + CSS_TABLE_FOOT_ROW + ">");

				if (multiSelect) {
					footer.append(OPEN_TABLE_COLUMN_TAG + CSS_TABLE_FOOT_MULTI_SELECT_COLUMN + ">");
					appendHeaderMultiSelect(footer);
					footer.append(CLOSE_TABLE_COLUMN_TAG);
				}

				for (ColumnTagHandler item : items) {
					allColumnsSelectable &= item.isSelectable();
					footer.append(OPEN_TABLE_COLUMN_TAG + CSS_TABLE_FOOT_ROW_COLUMN + ">");
					appendHeaderContent(footer, item);
		 			footer.append(CLOSE_TABLE_COLUMN_TAG);
			 	}

				if (edition != null) {
					footer.append(OPEN_TABLE_COLUMN_TAG + CSS_TABLE_FOOT_EDIT_CELL_COLUMN + ">");
					footer.append(CLOSE_TABLE_COLUMN_TAG);
				}

				footer.append(CLOSE_TABLE_ROW_TAG + CLOSE_TABLE_FOOT_TAG);

				// Add header into table
			 	builder.append(footer);
			}

			// Iteration on body content
 	 		builder.append(OPEN_TABLE_BODY_TAG + CSS_TABLE_BODY + ">");

	 	 	if (list != null && !list.isEmpty()) {

	 	 		String selectCommand = getSelectCommand("%s", scrollable ? id + SELECT_ROW_ITEM_ID + "#s#" : id + ID_WRAPPER);
	 	 		int size = pageSize > 0 && pageSize < list.size() ? pageSize : list.size();

 	 			if (itemExpand != null) {
 	 				appendScriptBuilder(new StringBuilder(JSMART_TABLE_ROW_EXPAND.format(id)));
 	 			}

 	 			String rowStyle = select != null || itemExpand != null ? CSS_TABLE_BODY_ROW_SELECTION : CSS_TABLE_BODY_ROW;
 	 			String rowStyleNth = select != null || itemExpand != null ? CSS_TABLE_BODY_ROW_SELECTION_NTH : CSS_TABLE_BODY_ROW_NTH;

 	 			for (int i = 0; i < size; i++) {
 	 				request.setAttribute(var, list.get(i));
 	 				builder.append(OPEN_TABLE_ROW_TAG);

 	 				if (edition != null) {
 	 					builder.append("id=\"" + id + EDIT_CELL_ROW_ITEM_ID + (i + getIndex()) + "_" + getPageFirst() + "\" ");
 	 				}

 	 				if (select != null && scrollable) {
 	 					builder.append("select=\"" + id + SELECT_ROW_ITEM_ID + (i + getIndex()) + "_" + getPageFirst() + "\" ");
 	 				}

 	 				if (allColumnsSelectable && edition == null) {
 	 					appendSelectCommand(builder, selectCommand, i, true);
 	 				}

 	 				appendEventBuilder(builder);

 	 				if (paginator == null && scrollable && pageSize % 2 != 0 && getPageFirst() % 2 != 0) {
 	 					builder.append((i % 2 == 0 ? rowStyleNth : rowStyle) + ">");
 	 				} else {
 	 					builder.append((i % 2 == 0 ? rowStyle : rowStyleNth) + ">");
 	 				}

 	 				if (multiSelect) {
 	 					builder.append(OPEN_TABLE_COLUMN_TAG + CSS_TABLE_BODY_MULTI_SELECT_COLUMN);
 	 					if (!allColumnsSelectable || edition != null) {
 	 						appendSelectCommand(builder, selectCommand, i, true);
 	 					}
 	 					builder.append(">" + HtmlConstants.CHECKBOX_TAG + "id=\"" + id + MULTI_SELECT_ITEM_ID + (i + getIndex()) + "\" />");
 	 					builder.append(CLOSE_TABLE_COLUMN_TAG);
					}

 	 				for (ColumnTagHandler item : items) {
 	 					builder.append(OPEN_TABLE_COLUMN_TAG);

 	 					if (item.style != null) {
							builder.append("style=\"" + item.style + "\" ");
						}
						if (item.styleClass != null) {
							builder.append("class=\"" + item.styleClass + "\" ");
						} else {
							builder.append(CSS_TABLE_BODY_COLUMN);
						}

 	 					if (!allColumnsSelectable || edition != null) {
 	 						appendSelectCommand(builder, selectCommand, i, item.isSelectable());
 	 					}
 	 					builder.append(">");

 	 					StringWriter sw = new StringWriter();
 	 					item.setOutputWriter(sw);
 	 					item.executeTag();
 	 					builder.append(sw.toString());

 	 					builder.append(CLOSE_TABLE_COLUMN_TAG);
 	 				}

 	 				if (edition != null) {
 	 					builder.append(OPEN_TABLE_COLUMN_TAG + CSS_TABLE_BODY_CELL_EDIT_COLUMN + ">");
 	 					appendCellEditContent(builder, i);
 	 					builder.append(CLOSE_TABLE_COLUMN_TAG);
 	 				}

 	 				builder.append(CLOSE_TABLE_ROW_TAG);

 	 				if (itemExpand != null) {
 	 					builder.append(OPEN_TABLE_ROW_TAG + ">");
 	 					builder.append(OPEN_TABLE_COLUMN_TAG);
 	 	 				builder.append(CSS_TABLE_BODY_ROW_COLUMN_EXPANDED + "colspan=\"" + 
 	 	 									(items.size() + (multiSelect ? 1 : 0) + (edition != null ? 1 : 0)) + "\">");

 	 	 				StringWriter sw = new StringWriter();
 	 	 				itemExpand.setOutputWriter(sw);
 	 	 				itemExpand.executeTag();

 	 	 				builder.append(sw.toString());
 	 	 				builder.append(CLOSE_TABLE_COLUMN_TAG);
 	 	 				builder.append(CLOSE_TABLE_ROW_TAG);
 	 				}

 	 				request.removeAttribute(var);
 	 			}
	 	 	} else {
	 	 		// Add body with message for empty data
	 	 		builder.append(OPEN_TABLE_ROW_TAG + ">");
	 	 		builder.append(OPEN_TABLE_COLUMN_TAG);

	 	 		builder.append(emptyStyle != null ? "style=\"" + emptyStyle + "\" " : "");
	 	 		builder.append(emptyClass != null ? "class=\"" + emptyClass + "\" " : CSS_TABLE_BODY_EMPTY);
	 	 		builder.append("colspan=\"" + (items.size() + (multiSelect ? 1 : 0) + (edition != null ? 1 : 0)) + "\" valign=\"middle\" >");
	 	 		builder.append(emptyMessage != null ? getTagValue(emptyMessage) : "");

	 	 		builder.append(CLOSE_TABLE_COLUMN_TAG);
	 	 		builder.append(CLOSE_TABLE_ROW_TAG);
	 	 	}

	 	 	builder.append(CLOSE_TABLE_BODY_TAG);
	 	 	builder.append(CLOSE_TABLE_TAG);

	 	 	// Add footer pagination
			if (paginator != null && (paginator.equals(PAGINATOR_BOTH) || paginator.equals(PAGINATOR_BOTTOM))) {
				builder.append(OPEN_DIV_TAG + CSS_TABLE_PAGE_FOOTER + ">");
				builder.append(pgtr);
				builder.append(CLOSE_DIV_TAG);
			}
	
	 		printOutput(builder.append(CLOSE_DIV_TAG));

		} catch (JSONException ex) {
			throw new JspException(ex);
		}
	}

	private void appendCellEditContent(StringBuilder builder, int index) throws JSONException {
		builder.append(OPEN_DIV_TAG + "id=\"" + id + EDIT_CELL_START_ITEM_ID + (index + getIndex()) + "_" + getPageFirst() + "\" " + CSS_TABLE_EDIT_CELL_START);
		builder.append(ON_CLICK + JSMART_TABLE_EDIT_START.format(id, "$(this)", (index + getIndex()), getPageFirst()) + "\">");
		builder.append(CLOSE_DIV_TAG);

		builder.append(OPEN_DIV_TAG + "id=\"" + id + EDIT_CELL_CONFIRM_ITEM_ID + (index + getIndex()) + "_" + getPageFirst() + "\" " + CSS_TABLE_EDIT_CELL_CONFIRM);
		builder.append(ON_CLICK + JSMART_TABLE_EDIT.format(async, id, "$(this)") + "\" ");

		JSONEdit jsonEdit = new JSONEdit();
		jsonEdit.setVarname(var);
		jsonEdit.setName(getTagName(J_TBL_EDT, edition));
		jsonEdit.setAction(getTagName(J_TBL_EDT, value));
		jsonEdit.setIndex(String.valueOf(index + getIndex()));
		jsonEdit.setFirst(String.valueOf(getPageFirst()));
		jsonEdit.setSize(String.valueOf(pageSize));
		jsonEdit.setSort(getActionSort(jsonAction));
		jsonEdit.setFilter(getActionFilter(jsonAction));
		jsonEdit.setUpdate(id + EDIT_CELL_ROW_ITEM_ID + index + "_" + getPageFirst());
		
		builder.append("ajax=\"" + getJSONValue(jsonEdit) + "\" >");
		builder.append(CLOSE_DIV_TAG);

		builder.append(OPEN_DIV_TAG + "id=\"" + id + EDIT_CELL_CANCEL_ITEM_ID + (index + getIndex()) + "_" + getPageFirst() + "\" " + CSS_TABLE_EDIT_CELL_CANCEL);
		builder.append(ON_CLICK + JSMART_TABLE_EDIT_CANCEL.format(id, "$(this)", (index + getIndex()), getPageFirst()) + "\">");
		builder.append(CLOSE_DIV_TAG);
	}

	private void appendHeaderMultiSelect(StringBuilder builder) throws JSONException {
		builder.append(HtmlConstants.CHECKBOX_TAG + "id=\"" + id + MULTI_SELECT_ALL_ID + "\" ");
		builder.append(getSelectCommand(MULTI_SELECT_ALL_INDEX, id + ID_WRAPPER) + "/>");
	}

	private void appendHeaderContent(StringBuilder builder, ColumnTagHandler item) throws JSONException {
		if (item.getFilterBy() != null) {
			builder.append(INPUT_TAG + "id=\"" + id + "_" + item.getId() + INPUT_FILTER_ID + "\" ");
			builder.append(CSS_TABLE_HEAD_INPUT);
			builder.append(getActionCommand(ACTION.FILTER, item.getFilterBy() + "," + id + "_" + item.getId() + "_filter"));
			builder.append("placeholder=\"" + getTagValue(item.getHeader()) + "\" datatype=\"text\" />");
		} else {
			builder.append(item.getHeader() != null ? getTagValue(item.getHeader()) : "");
		}

		if (item.getSortBy() != null) {
			String sortBy = getActionSortBy(jsonAction);
			SmartTableAdapter.SortOrder sortOrder = getActionSortOrder(jsonAction);

			builder.append(OPEN_DIV_TAG + "style=\"float: right;\">");

			builder.append(OPEN_DIV_TAG);
			if (item.getSortBy().equals(sortBy) && sortOrder == SmartTableAdapter.SortOrder.ASC) {
				builder.append(item.getFilterBy() != null ? CSS_TABLE_HEAD_SELECTED_SORT_FILTER_UP : CSS_TABLE_HEAD_SELECTED_SORT_UP);
			} else {
				builder.append(item.getFilterBy() != null ? CSS_TABLE_HEAD_SORT_FILTER_UP : CSS_TABLE_HEAD_SORT_UP);
			}
			builder.append(getActionCommand(ACTION.SORT, item.getSortBy() + "," + SORT_ASCENDENT));
			builder.append(">" + CLOSE_DIV_TAG);

			builder.append(OPEN_DIV_TAG);
			if (item.getSortBy().equals(sortBy) && sortOrder == SmartTableAdapter.SortOrder.DESC) {
				builder.append(item.getFilterBy() != null ? CSS_TABLE_HEAD_SELECTED_SORT_FILTER_DOWN : CSS_TABLE_HEAD_SELECTED_SORT_DOWN);
			} else {
				builder.append(item.getFilterBy() != null ? CSS_TABLE_HEAD_SORT_FILTER_DOWN : CSS_TABLE_HEAD_SORT_DOWN);
			}
			builder.append(getActionCommand(ACTION.SORT, item.getSortBy() + "," + SORT_DESCENDENT));
			builder.append(">" + CLOSE_DIV_TAG);
			builder.append(CLOSE_DIV_TAG);
		}
	}

	private void appendSelectCommand(StringBuilder builder, String selectCommand, int rowIndex, boolean isSelectable) {
		if (selectCommand != null) {
			if (select != null) {
				if (multiSelect) {
					builder.append(isSelectable ? String.format(selectCommand, getSelectIndexes(rowIndex + getIndex())) : "");
				} else {
					if (scrollable) {
						builder.append(isSelectable ? String.format(selectCommand, rowIndex + getIndex()).replace("#s#", (rowIndex + getIndex()) + "_" + getPageFirst()) : "");
					} else {
						builder.append(isSelectable ? String.format(selectCommand, rowIndex + getIndex()) : "");
					}
				}
			} else {
				builder.append(selectCommand);
			}
		}
	}

	private String getSelectCommand(String indexes, String update) throws JSONException {
		String command = ajaxCommand; 
		if (select != null) {
			
			JSONSelect jsonSelect = new JSONSelect();
			jsonSelect.setId(id);
			jsonSelect.setName(getTagName(J_TBL_SEL, select));
			jsonSelect.setAction(getTagName(J_TBL_SEL, value));
			jsonSelect.setMulti(String.valueOf(multiSelect));
			jsonSelect.setIndexes(indexes);
			jsonSelect.setFirst(String.valueOf(getPageFirst()));
			jsonSelect.setSize(String.valueOf(pageSize));
			jsonSelect.setSort(getActionSort(jsonAction));
			jsonSelect.setFilter(getActionFilter(jsonAction));
			jsonSelect.setUpdate(update);

			String parameters = "ajaxeval=\"" + getJSONValue(jsonSelect) + "\" ";

			if (command != null) {
				if (command.startsWith(ON_CLICK)) {
					if (command.contains(JSMART_AJAX.toString())) {
						command += parameters;
					} else {
						command = command.replace(ON_CLICK, ON_CLICK + JSMART_TABLE_SELECT.format(async, id, "$(this)") + "\" " + parameters);
					}
				} else {
					command += ON_CLICK + JSMART_TABLE_SELECT.format(async, id, "$(this)") + "\" " + parameters;
				}
			} else {
				command = ON_CLICK + JSMART_TABLE_SELECT.format(async, id, "$(this)") + "\" " + parameters;
			}
		}
		return command;
	}

	private StringBuilder getActionCommand(ACTION action, Object value) throws JSONException {
		StringBuilder first = new StringBuilder();
		StringBuilder sort = new StringBuilder();
		StringBuilder filter = new StringBuilder();
		StringBuilder builder = new StringBuilder(" ");

		if (action == ACTION.SORT) {
			first.append(getPageFirst());
			sort.append(value);
			if (jsonAction != null) {
				filter.append(getActionFilter(jsonAction));
			}

		} else if (action == ACTION.FILTER) {
			first.append(getPageFirst());
			if (jsonAction != null) {
				sort.append(getActionSort(jsonAction));
				String filterAction = getActionFilter(jsonAction);
				filter.append(filterAction);
				if (filterAction != null) {
					if (!filterAction.contains(value.toString())) {
						filter.append((filter.length() > 0 ? "," : "") + value);
					} else {
						String inputValue = getActionFilterValue(jsonAction, ((String) value).split(",")[0]);
						if (inputValue != null) {
							builder.append("value=\"" + inputValue + "\" ");
						}
					}
				}
			} else {
				filter.append(value);
			}

		} else {
			// Case FIRST, PREV, NEXT, LAST, NUMBER
			first.append(value);
			if (jsonAction != null) {
				sort.append(getActionSort(jsonAction));
				filter.append(getActionFilter(jsonAction));
			}
		}

		builder.append(action == ACTION.FILTER ? ON_KEY_UP : ON_CLICK);
		builder.append(JSMART_TABLE.format(async, id, "$(this)") + "return false;\" ");

		JSONTable jsonTable = new JSONTable();
		jsonTable.setName(getTagName(J_TBL, "@{" + id + "}"));
		jsonTable.setAction(action.toString());
		jsonTable.setFirst(String.valueOf(first));
		jsonTable.setSort(String.valueOf(sort));
		jsonTable.setFilter(String.valueOf(filter));
		jsonTable.setUpdate(id + ID_WRAPPER);

		builder.append("ajax=\"" + getJSONValue(jsonTable) + "\" ");

		return builder;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Object> getTableContent(Object value, int pageSize) throws JspException, JSONException {

		long first = getActionFirst(jsonAction);
		ACTION action = getAction(jsonAction);
		String sortBy = getActionSortBy(jsonAction);
		SmartTableAdapter.SortOrder sortOrder = getActionSortOrder(jsonAction);
		Map<String, String> filterBy = getActionFilters(jsonAction);

		if (value instanceof SmartTableAdapter) {
			tableAdapter = (SmartTableAdapter) value;
			boolean shallReload = tableAdapter.shallReload(first, sortBy, sortOrder, filterBy);

			if (shallReload) {
				tableAdapter.setSize(tableAdapter.loadSize(sortBy, filterBy));
			}

			if (action == ACTION.FIRST) {
				tableAdapter.setFirst(0);

			} else if (action == ACTION.LAST) {
				if (tableAdapter.getSize() - pageSize >= 0) {
					long mod = tableAdapter.getSize() % pageSize;
					tableAdapter.setFirst(tableAdapter.getSize() - (mod != 0 ? mod : pageSize));
				}
			} else if (action == ACTION.NEXT) {
				tableAdapter.setFirst(first);
				if (tableAdapter.getFirst() + pageSize < tableAdapter.getSize()) {
					tableAdapter.setFirst(tableAdapter.getFirst() + pageSize);
				}
			} else if (action == ACTION.PREV) {
				tableAdapter.setFirst(first);
				if (tableAdapter.getFirst() - pageSize >= 0) {
					tableAdapter.setFirst(tableAdapter.getFirst() - pageSize);
				}
			} else if (action == ACTION.NUMBER) {
				tableAdapter.setFirst((first - 1) * pageSize);

			} else if (action == ACTION.SORT) {
				tableAdapter.setSortBy(sortBy);
				tableAdapter.setSortOrder(sortOrder);

			} else if (action == ACTION.FILTER) {
				tableAdapter.setFilterBy(filterBy);

			} else {
				tableAdapter.setFirst(first);
			}

			if (shallReload) {
				tableAdapter.resetReload();
				tableAdapter.setLoaded(tableAdapter.loadData(tableAdapter.getFirst(), pageSize, sortBy, sortOrder, filterBy));
				tableAdapter.postLoad(tableAdapter.getLoaded());
			}
			return tableAdapter.getLoaded();

 	 	} else if (value instanceof Collection) {
 	 		collectionIndex = 0;
 	 		collectionItems = (Collection<Object>) value;

			if (action == ACTION.FIRST) {
				collectionIndex = 0;

			} else if (action == ACTION.LAST) {
				if (collectionItems.size() - pageSize >= 0) {
					long mod = collectionItems.size() % pageSize;
					collectionIndex = (int) (collectionItems.size() - (mod != 0 ? mod : pageSize));
				}
			} else if (action == ACTION.NEXT) {
				if (first + pageSize < collectionItems.size()) {
					collectionIndex = (int) (first + pageSize);
				} else {
					collectionIndex = first;
				}
			} else if (action == ACTION.PREV) {
				if (first - pageSize >= 0) {
					collectionIndex = first - pageSize;
				}
			} else if (action == ACTION.NUMBER) {
				collectionIndex = (first - 1) * pageSize;

			} else {
				collectionIndex = first;
			}

 	 		List<Object> list = new ArrayList<Object>();
 	 		Object[] collectionArray = collectionItems.toArray();

 	 		for (int i = (int) collectionIndex; i < (collectionIndex + pageSize >= collectionItems.size() ? collectionItems.size() : (int) (collectionIndex + pageSize)); i++) {
 	 			list.add(collectionArray[i]);
 	 		}

 	 		return list;

 	 	} else if (value != null) {
 	 		throw new JspException("The value of attribute named 'value' for table tag must be instance of an Collection or SmartTableAdapter!");
 	 	}
		return Collections.EMPTY_LIST;
	}

	private StringBuilder getPaginatorWrapper() throws JSONException {
		if (paginator != null) {
			StringBuilder wrapper = new StringBuilder(OPEN_SPAN_TAG + CSS_TABLE_PAGE_SPAN + ">");
	
			wrapper.append(OPEN_SPAN_TAG + CSS_TABLE_PAGE_FIRST + getActionCommand(ACTION.FIRST, getPageFirst()) + ">" + CLOSE_SPAN_TAG + "&nbsp;");
			wrapper.append(OPEN_SPAN_TAG + CSS_TABLE_PAGE_PREVIOUS + getActionCommand(ACTION.PREV, getPageFirst()) + ">" + CLOSE_SPAN_TAG + "&nbsp;");
	
			wrapper.append(getPaginatiorPages() + "&nbsp;");
	
			wrapper.append(OPEN_SPAN_TAG + CSS_TABLE_PAGE_NEXT + getActionCommand(ACTION.NEXT, getPageFirst()) + ">" + CLOSE_SPAN_TAG + "&nbsp;");
			wrapper.append(OPEN_SPAN_TAG + CSS_TABLE_PAGE_LAST + getActionCommand(ACTION.LAST, getPageFirst()) + ">" + CLOSE_SPAN_TAG);
	
			return wrapper.append(CLOSE_SPAN_TAG);
		}
		return null;
	}

	private StringBuilder getPaginatiorPages() throws JSONException {

		StringBuilder pages = new StringBuilder("&nbsp;");

		if (paginator != null && pageSize > 0) {
			int amount = DEFAULT_VALUE;

			if (getTotalSize() != DEFAULT_VALUE) {
				amount = (int) Math.ceil(((float) getTotalSize()) / pageSize);
			}

			int start = 0;
			int finish = amount < MAXIMUM_PAGES ? amount : MAXIMUM_PAGES;
			int index = (int) ((getPageFirst() / pageSize) + 1);
			int current = index % MAXIMUM_PAGES;

			if (amount > MAXIMUM_PAGES) {
				if (current > (MAXIMUM_PAGES / 2) + 1 || index >= MAXIMUM_PAGES) {
					finish = index + ((MAXIMUM_PAGES / 2) - 1);
				} else if (current <= (MAXIMUM_PAGES / 2) && index >= ((MAXIMUM_PAGES / 2) + 1)) {
					finish = index + (MAXIMUM_PAGES / 2);
				}

				if (finish > amount) {
					finish = amount;
				}
				start = finish - MAXIMUM_PAGES;
			}

			for (int i = start; i < finish; i++) {
				String style = (i + 1 == index ? CSS_TABLE_PAGE_INDEX_ACTIVE : CSS_TABLE_PAGE_INDEX);
				pages.append(OPEN_SPAN_TAG + style + getActionCommand(ACTION.NUMBER, i + 1) + ">" + (i + 1) + CLOSE_SPAN_TAG + "&nbsp;");
			}
		}
		
		return pages;
	}

	private long getIndex() {
		return collectionItems != null ? collectionIndex : 0;
	}

	private long getPageFirst() {
		return tableAdapter != null ? tableAdapter.getFirst() : collectionItems != null ? collectionIndex : 0;
	}

	private long getTotalSize() {
		return tableAdapter != null ? tableAdapter.getSize() : collectionItems != null ? collectionItems.size() : DEFAULT_VALUE;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setCellPadding(String cellPadding) {
		this.cellPadding = cellPadding;
	}

	public void setCellSpacing(String cellSpacing) {
		this.cellSpacing = cellSpacing;
	}

	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setEmptyMessage(String emptyMessage) {
		this.emptyMessage = emptyMessage;
	}

	public void setEmptyStyle(String emptyStyle) {
		this.emptyStyle = emptyStyle;
	}

	public void setEmptyClass(String emptyClass) {
		this.emptyClass = emptyClass;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public void setPaginator(String paginator) {
		this.paginator = paginator;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}

	public void setItemExpand(ExpandTagHandler itemExpand) {
		this.itemExpand = itemExpand;
	}

	/*package*/ void addItem(ColumnTagHandler item) {
		item.setId(String.valueOf(items.size() + 1));
		this.items.add(item);
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

}
