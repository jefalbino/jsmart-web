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

package com.jsmart5.framework.tag.js;

public enum JsConstants {

	JSMART_VALIDATE("Jsmart5.validate", "('%s');"),
	JSMART_AJAX("Jsmart5.ajax", "(%s,$(this));"),
	JSMART_BIND("Jsmart5.bind", "(%s);"),
	JSMART_MODAL("Jsmart5.modal", "('%s');"),
	JSMART_LIST("Jsmart5.list", "($(this),%s);"),
	JSMART_LISTSCROLL("Jsmart5.listscroll", "(%s);"),
	JSMART_TAB("Jsmart5.tab", "(%s);"),
	JSMART_TABPANE("Jsmart5.tabpane", "($(this),%s);"),
	

	JSMART_REST("Jsmart5.rest", "(%s,%s,%s);"),
	JSMART_TABLE("Jsmart5.table", "(%s,'%s',%s);"),
	JSMART_TABLE_SELECT("Jsmart5.tableSelect", "(%s,'%s',%s);"),
	JSMART_TABLE_EDIT("Jsmart5.tableEdit", "(%s,'%s',%s);"),
	JSMART_TABLE_EDIT_START("Jsmart5.tableEditStart", "('%s',%s,%s,%s);"),
	JSMART_TABLE_EDIT_CANCEL("Jsmart5.tableEditCancel", "('%s',%s,%s,%s);"),
	JSMART_TABLE_ROW_EXPAND("Jsmart5.tableRowExpand", "('%s');"),
	JSMART_TABLE_SCROLL("Jsmart5.tableScroll", "('%s');"),
	JSMART_SWITCH("Jsmart5.xswitch", "('%s',%s,%s);"),
	JSMART_SWITCH_RESET("Jsmart5.resetSwitch", "('%s');"),
	JSMART_BALLOON("Jsmart5.balloon", "('%s','%s',%s,%s,'%s');"),
	JSMART_CAROUSEL("Jsmart5.carousel", "('%s');"),
	JSMART_PROGRESS("Jsmart5.progress", "('%s');"),
	JSMART_RANGE("Jsmart5.range", "('%s');"),

	JSMART_DATE("Jsmart5.date", "(%s);"),
	JSMART_BACKUP_DATE("Jsmart5.backupDate", "(%s);"),

	JSMART_LOAD("Jsmart5.load", "('%s');"),
	JSMART_TREE("Jsmart5.tree", "('%s');"),

	JSMART_BUTTON_RESTARRAY("Jsmart5.buttonRestArray", "('%s','%s');"),

	JSMART_AUTOCOMPLETE_RESET("Jsmart5.resetAutocomplete", "('%s');"),
	JSMART_AUTOCOMPLETE("Jsmart5.autocomplete", "(%s,'%s',event);");

	private String name;

	private String parameters;

	private JsConstants(String name, String parameters) {
		this.name = name;
		this.parameters = parameters;
	}

	public String format(Object ... values) {
		return String.format(name + parameters, values);
	}

	@Override
	public String toString() {
		return name;
	}

}
