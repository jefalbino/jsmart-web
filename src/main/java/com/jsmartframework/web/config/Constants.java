/*
 * JSmart Framework - Java Web Development Framework
 * Copyright (c) 2014, Jeferson Albino da Silva, All rights reserved.
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

package com.jsmartframework.web.config;

public final class Constants {

    public static final String REQUEST_USER_ACCESS = "jsmart_request_user_access";

    public static final String REQUEST_PAGE_DOC_SCRIPT_ATTR = "jsmart_page_doc_script_attr";

    public static final String REQUEST_PAGE_SCRIPT_ATTR = "jsmart_page_script_attr";

    public static final String REQUEST_REDIRECT_PATH_AJAX_ATTR = "jsmart_request_redirect_path_ajax_attr";

    public static final String REQUEST_REDIRECT_WINDOW_PATH_AJAX_ATTR = "jsmart_request_redirect_window_path_ajax_attr";

    public static final String SESSION_RESET_ATTR = "jsmart_session_reset_attr";


    public static final String JSP_EL = "${%s}";

    public static final String TAG_EL = "@{%s}";

    public static final String POINT = ".";

    public static final String EL_SEPARATOR = "\\.";

    public static final String EL_PARAM_READ_ONLY = "#";


    public static final String WEB_INF = "/WEB-INF";

    public static final String INDEX_JSP = "index.jsp";

    public static final String PATH_SEPARATOR = "/";

    public static final String LIB_FILE_PATH = WEB_INF + PATH_SEPARATOR + "lib";

    public static final String LIB_JAR_FILE_PATTERN = ".*jsmart.*\\.jar";

    public static final String WEB_CONFIG_XML = "webConfig.xml";

    public static final String WEB_CONFIG_XML_PATH = WEB_INF + PATH_SEPARATOR + WEB_CONFIG_XML;

    public static final String FILTER_RESOURCES = "jsmart_resources.json";

    public static final String FILTER_HEADERS = "jsmart_headers.json";


    private Constants() {
        // DO NOTHING
    }

}
