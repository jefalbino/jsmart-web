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

package com.jsmart5.framework.manager;

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

import static com.jsmart5.framework.manager.SmartConfig.*;

public final class SmartErrorFilter implements Filter {

	private static final Logger LOGGER = Logger.getLogger(SmartErrorFilter.class.getPackage().getName());

	private static final String HEADER_E_TAG = "ETag";

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

		// Wrapper to change the default ETag-based validated caching to faster max-age-caching,
		// we need to prevent the ETag-header from being added to the response object.
		// Also it is necessary to avoid error 404 (not found) to be set by container, 
		// in order to allow 404 page customization by framework settings
		HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(httpResponse) {

			@Override 
			public void setHeader(String name, String value) {
				if (!HEADER_E_TAG.equals(name)) {
					httpResponse.setHeader(name, value);
				}
			}

			@Override
			public void sendError(int error) throws IOException {
				LOGGER.log(Level.INFO, "Request error " + error + " on path " + requestPath);

				if (CONFIG.getContent().getErrorPage(error) != null) {
					httpResponse.setStatus(error);
				} else {
					httpResponse.sendError(error);
				}
			}

			@Override
			public void sendError(int error, String message) throws IOException {
				LOGGER.log(Level.INFO, "Request error " + error + " on path " + requestPath + " caused by: " + message);

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

		SmartErrorPage errorPage = CONFIG.getContent().getErrorPage(responseWrapper.getStatus());

		if (errorPage != null) {
			String path = errorPage.getPage();

			// Use Redirect response internally to change to error page
			responseWrapper.sendRedirect((path.startsWith("/") ? httpRequest.getContextPath() : "") + path);
		}
	}

}