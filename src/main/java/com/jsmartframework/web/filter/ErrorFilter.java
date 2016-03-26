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

import com.jsmartframework.web.config.ErrorPage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public final class ErrorFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(ErrorFilter.class.getPackage().getName());

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
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        final String requestPath = httpRequest.getServletPath();

        // It is necessary to avoid HTTP error codes to be set by container,
        // so we allow error code page customization by framework settings
        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(httpResponse) {

            @Override
            public void setHeader(String name, String value) {
                httpResponse.setHeader(name, value);
            }

            @Override
            public void sendError(int error) throws IOException {
                LOGGER.log(Level.INFO, "Request error [" + error + "] on path [" + requestPath + "]");

                if (CONFIG.getContent().getErrorPage(error) != null) {
                    httpResponse.setStatus(error);
                } else {
                    httpResponse.sendError(error);
                }
            }

            @Override
            public void sendError(int error, String message) throws IOException {
                LOGGER.log(Level.INFO, "Request error [" + error + "] on path [" + requestPath + "] caused by: [" + message + "]");

                if (CONFIG.getContent().getErrorPage(error) != null) {
                    httpResponse.setStatus(error);
                } else {
                    httpResponse.sendError(error, message);
                }
            }
        };

        try {
            filterChain.doFilter(httpRequest, responseWrapper);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }

        ErrorPage errorPage = CONFIG.getContent().getErrorPage(responseWrapper.getStatus());

        if (errorPage != null) {
            String path = errorPage.getPage();

            // Use Redirect response internally to change to error page
            responseWrapper.sendRedirect((path.startsWith("/") ? httpRequest.getContextPath() : "") + path);
        }
    }

}