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

package com.jsmart5.framework.config;

public final class SmartConstants {

	public static final String WEB_INF = "/WEB-INF";

	public static final String SEPARATOR = "/"; /* DO NOT CHANGE - SOME STATEMENTS NEED "/" CHARACTER */
	
	public static final String REQUEST_USER_ACCESS = "jsmart5_user_access_request";
	
	public static final String SESSION_CREATED_FLAG = "jsmart5_session_created";

	// TODO: Pass via requesst not via session :(
	public static final String AJAX_ATTR = "jsmart_ajax_attr";

	public static final String AJAX_RESET_ATTR = "jsmart_reset_ajax_attr";

	public static final String SCRIPT_BUILDER_ATTR = "jsmart_script_builder_attr";

	
	@Deprecated
	public static final String START_HEAD_TAG = "\t<head>";

	@Deprecated
	public static final String END_HEAD_TAG = "\t</head>";

	@Deprecated
	public static final String RESET_AJAX_TAG = "<input id=\"" + AJAX_RESET_ATTR + "\" type=\"hidden\" />";

	@Deprecated
	public static final String REDIRECT_AJAX_TAG = "<input id=\"jsmart_redirect_ajax_path\" type=\"hidden\" value=\"";

	@Deprecated
	public static final String SCRIPT_READY_AJAX_TAG = "<script type=\"text/javascript\">$(document).ready(function(){jsmartRefresh();});function jsmartRefresh(){%s};</script>";

	@Deprecated
	public static final String END_AJAX_TAG = "\" />";


	public static final String URL_PARAM_TAG = ":urlparam";

	public static final String URL_PARAM_NAME_ATTR = "name=\"";

	public static final String URL_PARAM_PARAM_ATTR = "param=\"";

	public static final String INCLUDE_TAG = "include";

	public static final String INCLUDE_FILE_ATTR = "file=\"";

	public static final String START_JSP_TAG = "<%@";

	public static final String END_JSP_TAG = "%>";

	public static final String START_EL = "@{";

	public static final String END_EL = "}";

	public static final String JSP_EL = "${";

	public static final String POINT = ".";

	public static final String EL_SEPARATOR = "\\.";

	public static final String EL_PARAM_READ_ONLY = "#";

	public static final String EL_PATTERN = "@\\{(.[^@\\{\\}]*)\\}";

	public static final String URL_PARAM_PATTERN = "<.*" + URL_PARAM_TAG + "?.*";

	public static final String INCLUDE_JSPF_PATTERN = START_JSP_TAG + ".*" + INCLUDE_TAG + ".*" + INCLUDE_FILE_ATTR + "?.*";
	
	
	public static final String LIB_FILE_PATH = WEB_INF + SEPARATOR + "lib";

	public static final String LIB_JAR_FILE_PATTERN = ".*jsmart5.*\\.jar";

	public static final String JSMART5_XML_PATH = WEB_INF + SEPARATOR + "jsmart5.xml";

	public static final String FILTER_RESOURCES = "jsmart_resources.json";

	public static final String FILTER_HEADERS = "jsmart_headers.json";


	private SmartConstants() {
		// DO NOTHING
	}

}
