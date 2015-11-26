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

package com.jsmartframework.web.util;

import com.google.common.html.HtmlEscapers;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang.StringEscapeUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Utility class to help working with URL pah and Cookies.
 */
public final class WebUtils {

    private static final int DEFAULT_RANDOM_BYTES = 8;

    private static final Random random = new SecureRandom();

    private static final Base32 base32 = new Base32();

    private WebUtils() {
        // DO NOTHING
    }

    public static String decodePath(String path) {
        if (path != null && !path.startsWith("/")) {
            try {
                new URL(path);
            } catch (MalformedURLException ex) {
                path = "/" + path;
            }
        }
        return path;
    }

    public static String randomId() {
        final byte[] bytes = new byte[DEFAULT_RANDOM_BYTES];
        random.nextBytes(bytes);
        String base32String = base32.encodeAsString(bytes);
        return base32String.toLowerCase().replace("=", "");
    }

    public static String getCookie(HttpServletRequest request, String name) {
        if (name == null) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equalsIgnoreCase(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static String escapeString(String value) {
        if (value != null) {
            value = HtmlEscapers.htmlEscaper().escape(value);
        }
        return value;
    }

    public static String unescapeString(String value) {
        if (value != null) {
            value = StringEscapeUtils.unescapeHtml(value);
        }
        return value;
    }
}
