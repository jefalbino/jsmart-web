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

package com.jsmartframework.web.filter;

import static com.jsmartframework.web.config.Config.CONFIG;

import com.jsmartframework.web.config.CachePattern;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class CacheFilter implements Filter {

    private static final String HEADER_CACHE_CONTROL = "Cache-Control";

    private static final String HEADER_EXPIRES = "Expires";

    private static final String HEADER_X_FRAME_OPTIONS = "X-Frame-Options";

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
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Dispatcher Forward not supposed to get in this Filter
        if (httpRequest.getDispatcherType() == DispatcherType.FORWARD) {
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }

        CachePattern cachePattern = CONFIG.getContent().getCachePattern(httpRequest.getRequestURI());
        if (cachePattern != null && cachePattern.getCacheControl() != null) {
            httpResponse.setHeader(HEADER_CACHE_CONTROL, cachePattern.getCacheControlHeader());
            httpResponse.setHeader(HEADER_EXPIRES, cachePattern.getExpiresHeader());
        }

        // Add header for click jacking protection
        httpResponse.setHeader(HEADER_X_FRAME_OPTIONS, "DENY");
        filterChain.doFilter(httpRequest, httpResponse);
    }
}
