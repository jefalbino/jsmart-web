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

package com.jsmart5.framework.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jsmart5.framework.adapter.TableAdapter.SortOrder;

public abstract class TableExpressionHandler extends TagHandler {

	protected static enum ACTION {
		FIRST, PREV, NEXT, LAST, NUMBER, SORT, FILTER;
	}

	protected static enum SELECT_TYPE {
		SINGLE, MULTI;
	}

	protected static String getSelectIndexes() {
		return null;
	}

	protected static String getSelectIndexes(long index) {
		String multiIndexes = "";
		return !multiIndexes.isEmpty() ? multiIndexes + "," + index : String.valueOf(index);
	}

	protected static boolean getActionExpand(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("expand")) {
			return jsonAction.get("expand").getAsBoolean();
		}
		return false;
	}

	protected static ACTION getAction(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("action")) {
			return ACTION.valueOf(jsonAction.get("action").getAsString());
		}
		return null;
	}

	protected static String getActionSelect(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("select")) {
			return jsonAction.get("select").getAsString();
		}
		return null;
	}

	protected static String getActionEdit(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("edit")) {
			return jsonAction.get("edit").getAsString();
		}
		return null;
	}

	protected static String getActionVar(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("varname")) {
			return jsonAction.get("varname").getAsString();
		}
		return null;
	}

	protected static SELECT_TYPE getActionType(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("type")) {
			return SELECT_TYPE.valueOf(jsonAction.get("type").getAsString());
		}
		return null;
	}

	protected static Integer getActionIndex(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("index")) {
			return jsonAction.get("index").getAsInt();
		}
		return null;
	}

	protected static Integer[] getActionIndexes(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("indexes") && !jsonAction.get("indexes").isJsonNull()) {
			JsonArray jsonArray = jsonAction.get("indexes").getAsJsonArray();

			if (jsonArray != null) {
				Integer[] indexes = new Integer[jsonArray.size()];

				for (int i = 0; i < indexes.length; i++) {
					indexes[i] = jsonArray.get(i).getAsInt();
				}
				return indexes;
			}
		}
		return null;
	}

	protected static long getActionFirst(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("first")) {
			return jsonAction.get("first").getAsLong();
		}
		return 0;
	}

	protected static long getActionSize(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("size")) {
			return jsonAction.get("size").getAsLong();
		}
		return 0;
	}

	protected static String getActionSort(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("sort") && !jsonAction.get("sort").isJsonNull()) {
			JsonObject sortObject = jsonAction.get("sort").getAsJsonObject();
			return sortObject.get("name").getAsString() + "," + sortObject.get("order").getAsString(); 
		}
		return null;
	}

	protected static String getActionSortBy(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("sort") && !jsonAction.get("sort").isJsonNull()) {
			JsonObject sortObject = jsonAction.get("sort").getAsJsonObject();
			return sortObject.get("name").getAsString();
		}
		return null;
	}

	protected static SortOrder getActionSortOrder(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("sort") && !jsonAction.get("sort").isJsonNull()) {
			JsonObject sortObject = jsonAction.get("sort").getAsJsonObject();
			return SortOrder.valueBy(sortObject.get("order").getAsString());
		}
		return SortOrder.ASC;
	}

	protected static String getActionFilter(JsonObject jsonAction) {
		String actionFilter = "";
		if (jsonAction != null && jsonAction.has("filters") && !jsonAction.get("filters").isJsonNull()) {
			JsonArray filtersArray = jsonAction.get("filters").getAsJsonArray();

			for (int i = 0; i < filtersArray.size(); i++) {
				JsonObject filter = filtersArray.get(i).getAsJsonObject();
				actionFilter += filter.get("name").getAsString() + "," + filter.get("field").getAsString() + ",";
			}

			actionFilter = actionFilter.substring(0, actionFilter.length() -1);
		}
		return actionFilter;
	}

	protected static String getActionFilterValue(JsonObject jsonAction, String name) {
		Map<String, String> values = getActionFilters(jsonAction);
		return values.get(name);
	}

	protected static Map<String, String> getActionFilters(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("filters") && !jsonAction.get("filters").isJsonNull()) {
			JsonArray filtersArray = jsonAction.get("filters").getAsJsonArray();
			Map<String, String> filters = new HashMap<String, String>();

			for (int i = 0; i < filtersArray.size(); i++) {
				JsonObject filter = filtersArray.get(i).getAsJsonObject();
				if (!filter.get("value").isJsonNull()) {
					filters.put(filter.get("name").getAsString(), filter.get("value").getAsString());
				}
			}
			return filters;
		}
		return Collections.emptyMap();
	}

	protected static Map<String, String> getActionEditValues(JsonObject jsonAction) {
		if (jsonAction != null && jsonAction.has("values") && !jsonAction.get("values").isJsonNull()) {
			JsonArray valuesArray = jsonAction.get("values").getAsJsonArray();
			Map<String, String> values = new HashMap<String, String>();

			for (int i = 0; i < valuesArray.size(); i++) {
				JsonObject value = (JsonObject) valuesArray.get(i);
				if (!value.get("value").isJsonNull()) {
					values.put(value.get("name").getAsString(), value.get("value").getAsString());
				}
			}
			return values;
		}
		return Collections.emptyMap();
	}

}
