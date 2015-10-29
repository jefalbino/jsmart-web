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

package com.jsmartframework.web.manager;

import static com.jsmartframework.web.manager.ExpressionHandler.EXPRESSIONS;

import java.io.IOException;
import java.util.regex.Matcher;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;

public final class OutputFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
        // DO NOTHING
    }

    @Override
    public void destroy() {
        // DO NOTHING
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponseWrapper responseWrapper = (HttpServletResponseWrapper) response;

        filterChain.doFilter(request, responseWrapper);

        // Case AsyncBean process was started or response was written directly, it cannot proceed
        // because it will not provide HTML via framework
        if (httpRequest.isAsyncStarted() || WebContext.isResponseWritten()) {
            return;
        }

        boolean foundRegex = false;
        String html = responseWrapper.toString();

        Matcher outputMatcher = ExpressionHandler.EL_PATTERN.matcher(html);
        while (outputMatcher.find()) {
            foundRegex = true;
            String expression = outputMatcher.group();
            Object value = EXPRESSIONS.getExpressionValue(expression);
            html = html.replace(expression, value != null ? value.toString() : "");
        }

        // Write our modified text to the real response
        if (foundRegex) {
            responseWrapper.reset();
            responseWrapper.setContentLength(html.getBytes().length);
            responseWrapper.getWriter().write(html);
        }
    }

}
