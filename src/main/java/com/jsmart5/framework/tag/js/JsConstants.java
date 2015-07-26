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
    JSMART_AJAX_ATTACH("Jsmart5.ajaxattach", "('%s');"),
	JSMART_BIND("Jsmart5.bind", "(%s);"),
	JSMART_MODAL("Jsmart5.modal", "('%s');"),
	JSMART_LIST("Jsmart5.list", "($(this),%s);"),
	JSMART_LISTSCROLL("Jsmart5.listscroll", "(%s);"),
	JSMART_TAB("Jsmart5.tab", "(%s);"),
	JSMART_TABPANE("Jsmart5.tabpane", "($(this),%s);"),
	JSMART_CAROUSEL("Jsmart5.carousel", "('%s');"),
	JSMART_DATE("Jsmart5.date", "(%s);"),
	JSMART_PROGRESSBAR("Jsmart5.progressbar", "(%s);"),
	JSMART_PROGRESSGROUP("Jsmart5.progressgroup", "(%s);"),
	JSMART_TABLE("Jsmart5.table", "($(this),%s);"),
	JSMART_TABLESCROLL("Jsmart5.tablescroll", "(%s);"),
	JSMART_TABLEHEADER("Jsmart5.tableheader", "(%s);"),
	JSMART_AUTOCOMPLETE("Jsmart5.autocplt", "(%s,e);"),
	JSMART_AUTOCPLTSCROLL("Jsmart5.autocpltscroll", "(%s);"),
    JSMART_ASYNCEVENT("Jsmart5.asyncevent", "(%s);");

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
