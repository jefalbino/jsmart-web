/*
 * JSmart5 - Java Web Development Framework
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

package com.jsmart5.framework.manager;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AsyncFilter implements Filter {

    public static final String ENCODING = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // DO NOTHING
    }

    @Override
    public void destroy() {
        // DO NOTHING
    }

    // Filter used case AsyncContext is dispatched internally by AsyncBean implementation
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpRequest.setCharacterEncoding(ENCODING);
        httpResponse.setCharacterEncoding(ENCODING);

        // Initiate bean context based on current thread instance
        WebContext.initCurrentInstance(httpRequest, httpResponse);

        Throwable throwable = null;
        try {
            filterChain.doFilter(httpRequest, httpResponse);
        } catch (Throwable thrown) {
            throwable = thrown;
            thrown.printStackTrace();
        }

        // Close bean context based on current thread instance
        WebContext.closeCurrentInstance();

        // Case internal server error
        if (throwable != null) {
            if (throwable instanceof IOException) {
                throw new IOException(throwable);
            }
            throw new ServletException(throwable);
        }
    }

}
