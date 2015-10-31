/*
 * JSmart Framework - Java Web Development Framework
 * Copyright (c) 2015, Jeferson Albino da Silva, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
*/

package com.jsmartframework.web.tag.js;

public enum JsConstants {

    JSMART_VALIDATE("JSmart.validate", "('%s');"),
    JSMART_AJAX("JSmart.ajax", "(%s,$(this));"),
    JSMART_AJAX_ATTACH("JSmart.ajaxattach", "('%s');"),
    JSMART_BIND("JSmart.bind", "(%s);"),
    JSMART_MODAL("JSmart.modal", "('%s');"),
    JSMART_LIST("JSmart.list", "($(this),%s);"),
    JSMART_LISTSCROLL("JSmart.listscroll", "(%s);"),
    JSMART_TAB("JSmart.tab", "(%s);"),
    JSMART_TABPANE("JSmart.tabpane", "($(this),%s);"),
    JSMART_CAROUSEL("JSmart.carousel", "('%s');"),
    JSMART_DATE("JSmart.date", "(%s);"),
    JSMART_PROGRESSBAR("JSmart.progressbar", "(%s);"),
    JSMART_PROGRESSGROUP("JSmart.progressgroup", "(%s);"),
    JSMART_TABLE("JSmart.table", "($(this),%s);"),
    JSMART_TABLESCROLL("JSmart.tablescroll", "(%s);"),
    JSMART_TABLEHEADER("JSmart.tableheader", "(%s);"),
    JSMART_AUTOCOMPLETE("JSmart.autocplt", "(%s,e);"),
    JSMART_AUTOCPLTSCROLL("JSmart.autocpltscroll", "(%s);"),
    JSMART_ASYNCEVENT("JSmart.asyncevent", "(%s);"),
    JSMART_FUNCTION_VAR("JSmart.fnvar", "('%s',%s);");

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
