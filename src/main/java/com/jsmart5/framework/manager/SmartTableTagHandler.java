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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jsmart5.framework.tag.SmartTableAdapter;

public abstract class SmartTableTagHandler extends SmartTagHandler {

	protected static enum ACTION {
		FIRST, PREV, NEXT, LAST, NUMBER, SORT, FILTER;
	}

	protected static enum SELECT_TYPE {
		SINGLE, MULTI;
	}

	protected static String getSelectIndexes() {
		return SmartContext.getSelectIndexes();
	}

	protected static String getSelectIndexes(long index) {
		String multiIndexes = SmartContext.getSelectIndexes();
		return !multiIndexes.isEmpty() ? multiIndexes + "," + index : String.valueOf(index);
	}

	protected static boolean getActionExpand(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("expand")) {
			return new Boolean(jsonAction.getString("expand"));
		}
		return false;
	}

	protected static ACTION getAction(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("action")) {
			return ACTION.valueOf(jsonAction.getString("action"));
		}
		return null;
	}

	protected static String getActionSelect(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("select")) {
			return jsonAction.getString("select");
		}
		return null;
	}

	protected static String getActionEdit(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("edit")) {
			return jsonAction.getString("edit");
		}
		return null;
	}

	protected static String getActionVar(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("varname")) {
			return jsonAction.getString("varname");
		}
		return null;
	}

	protected static SELECT_TYPE getActionType(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("type")) {
			return SELECT_TYPE.valueOf(jsonAction.getString("type"));
		}
		return null;
	}

	protected static Integer getActionIndex(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("index")) {
			return jsonAction.getInt("index");
		}
		return null;
	}

	protected static Integer[] getActionIndexes(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("indexes") && !jsonAction.isNull("indexes")) {
			JSONArray jsonArray = jsonAction.getJSONArray("indexes");

			if (jsonArray != null) {
				Integer[] indexes = new Integer[jsonArray.length()];

				for (int i = 0; i < indexes.length; i++) {
					indexes[i] = jsonArray.getInt(i);
				}
				return indexes;
			}
		}
		return null;
	}

	protected static long getActionFirst(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("first")) {
			return jsonAction.getLong("first");
		}
		return 0;
	}

	protected static long getActionSize(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("size")) {
			return jsonAction.getLong("size");
		}
		return 0;
	}

	protected static String getActionSort(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("sort") && !jsonAction.isNull("sort")) {
			JSONObject sortObject = (JSONObject) jsonAction.get("sort");
			return sortObject.getString("name") + "," + sortObject.getString("order"); 
		}
		return null;
	}

	protected static String getActionSortBy(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("sort") && !jsonAction.isNull("sort")) {
			JSONObject sortObject = (JSONObject) jsonAction.get("sort");
			return sortObject.getString("name");
		}
		return null;
	}

	protected static SmartTableAdapter.SortOrder getActionSortOrder(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("sort") && !jsonAction.isNull("sort")) {
			JSONObject sortObject = (JSONObject) jsonAction.get("sort");
			return SmartTableAdapter.SortOrder.valueBy(sortObject.getString("order"));
		}
		return SmartTableAdapter.SortOrder.ASC;
	}

	protected static String getActionFilter(JSONObject jsonAction) throws JSONException {
		String actionFilter = "";
		if (jsonAction != null && jsonAction.has("filters") && !jsonAction.isNull("filters")) {
			JSONArray filtersArray = (JSONArray) jsonAction.get("filters");

			for (int i = 0; i < filtersArray.length(); i++) {
				JSONObject filter = (JSONObject) filtersArray.get(i);
				actionFilter += filter.getString("name") + "," + filter.getString("field") + ",";
			}

			actionFilter = actionFilter.substring(0, actionFilter.length() -1);
		}
		return actionFilter;
	}

	protected static String getActionFilterValue(JSONObject jsonAction, String name) throws JSONException {
		Map<String, String> values = getActionFilters(jsonAction);
		return values.get(name);
	}

	protected static Map<String, String> getActionFilters(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("filters") && !jsonAction.isNull("filters")) {
			JSONArray filtersArray = jsonAction.getJSONArray("filters");
			Map<String, String> filters = new HashMap<String, String>();

			for (int i = 0; i < filtersArray.length(); i++) {
				JSONObject filter = (JSONObject) filtersArray.get(i);
				if (!filter.isNull("value")) {
					filters.put(filter.getString("name"), filter.getString("value"));
				}
			}
			return filters;
		}
		return Collections.emptyMap();
	}

	protected static Map<String, String> getActionEditValues(JSONObject jsonAction) throws JSONException {
		if (jsonAction != null && jsonAction.has("values") && !jsonAction.isNull("values")) {
			JSONArray valuesArray = jsonAction.getJSONArray("values");
			Map<String, String> values = new HashMap<String, String>();

			for (int i = 0; i < valuesArray.length(); i++) {
				JSONObject value = (JSONObject) valuesArray.get(i);
				if (!value.isNull("value")) {
					values.put(value.getString("name"), value.getString("value"));
				}
			}
			return values;
		}
		return Collections.emptyMap();
	}

}
