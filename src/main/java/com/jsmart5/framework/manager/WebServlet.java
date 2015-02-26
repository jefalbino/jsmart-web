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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jsmart5.framework.config.Constants;
import com.jsmart5.framework.listener.SmartContextListener;
import com.jsmart5.framework.util.SmartUtils;

import static com.jsmart5.framework.manager.ExpressionHandler.*;
import static com.jsmart5.framework.manager.BeanHandler.*;

public final class WebServlet extends HttpServlet {

	private static final long serialVersionUID = -4462762772195421585L;

	private static final Logger LOGGER = Logger.getLogger(WebServlet.class.getPackage().getName());

	@Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        SmartContext.setServlet(this);

        // Call registered SmartContextListeners
        for (SmartContextListener contextListener : HANDLER.contextListeners) {
        	HANDLER.executeInjection(contextListener);
        	contextListener.contextInitialized(servletConfig.getServletContext());
        }
    }

	@Override
    public void destroy() {
        // Call registered SmartContextListeners
        for (SmartContextListener contextListener : HANDLER.contextListeners) {
        	contextListener.contextDestroyed(getServletContext());
        }
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String path = request.getServletPath();

    	// Clear related PageScope beans case needed
    	HANDLER.finalizeBeans(path, request.getSession());

    	// If path is secure, check if user was logged case @AuthenticationBean annotation was provided
    	String authpath = HANDLER.checkAuthentication(path);

    	if (authpath != null && !authpath.equals(path)) {
			sendRedirect(authpath, request, response);
			return;
		}

    	sendForward(path, request, response);
    }

    @Override
    @SuppressWarnings("all")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String path = request.getServletPath();

		// If path is secure, check if user was logged case @AuthenticationBean annotation was provided
		String authpath = HANDLER.checkAuthentication(path);
		if (authpath != null && !authpath.equals(path)) {
			sendRedirect(authpath, request, response);
			return;
		}

		// Check if user is authorized to access the page. Send HTTP 403 response case they did not have
		Integer httpStatus = HANDLER.checkAuthorization(path);
		if (httpStatus != null) {
			LOGGER.log(Level.INFO, "SmartBean access not authorized on page [" + path + "]");
			response.sendError(httpStatus);
			return;
		}

		// Decrypt expressions if needed
		Map<String, String> expressions = HANDLER.getRequestExpressions();

		// Initiate beans mentioned on jsp page (Case request scope beans)
    	try {
    		HANDLER.instantiateBeans(path, expressions);
    	} catch (Exception ex) {
    		LOGGER.log(Level.INFO, "SmartBeans on page [" + path + "] could not be instantiated: " + ex.getMessage());
    		throw new ServletException(ex);
    	}

    	// Case user had ordered redirect to specific path in postConstruct method
    	String redirectPath = SmartContext.getRedirectTo();
    	if (redirectPath != null && !redirectPath.equals(path)) {
    		HANDLER.finalizeBean(path, request.getSession());
    		sendRedirect(redirectPath, request, response);
    		return;
    	}

    	boolean redirectAjax = false;
    	String responsePath = null;
		String submitExpression = HANDLER.handleRequestExpressions(expressions);

		// Check authorization roles on submit expression and after execute it
		if (submitExpression != null) {
			responsePath = SmartUtils.decodePath(EXPRESSIONS.submitExpression(submitExpression));
		}

		// Case user had ordered redirect to specific path in submitted method
    	redirectPath = SmartContext.getRedirectTo();
    	if (redirectPath != null && !redirectPath.equals(path)) {
    		HANDLER.finalizeBean(path, request.getSession());
    		responsePath = redirectPath;
    	}

    	// Case is ajax post action and submit method returned a path, let JavaScript redirect page
		if (responsePath != null && SmartContext.isAjaxRequest()) {
			redirectAjax = true;
		}

		if (responsePath == null) {
			responsePath = path;
		} else {

			// Case is ajax post action, let JavaScript redirect page
			if (SmartContext.isAjaxRequest()) {
				if (redirectAjax) {
					request.setAttribute(Constants.REQUEST_REDIRECT_PATH_AJAX_ATTR, 
							(responsePath.startsWith("/") ? request.getContextPath() : "") + responsePath);
				}
				responsePath = path;
			}
		}

		sendRedirect(responsePath, request, response);
	}

	private void sendForward(String path, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Check if user is authorized to access the page. Send HTTP 403 response case they did not have
		Integer httpStatus = HANDLER.checkAuthorization(path);
		if (httpStatus != null) {
			LOGGER.log(Level.INFO, "SmartBean access not authorized on page [" + path + "]");
			response.sendError(httpStatus);
			return;
		}

		// Initiate beans mentioned on jsp page
    	try {
    		HANDLER.instantiateBeans(path, null);
    	} catch (Exception ex) {
    		LOGGER.log(Level.SEVERE, "SmartBeans on page [" + path + "] could not be instantiated: " + ex.getMessage());
    		throw new ServletException(ex);
    	}

    	// Case user had ordered redirect to specific path in postConstruct method
    	String redirectPath = SmartContext.getRedirectTo();
    	if (redirectPath != null && !redirectPath.equals(path)) {
    		HANDLER.finalizeBean(path, request.getSession());
    		sendRedirect(redirectPath, request, response);
    		return;
    	}

    	// Case is ajax post action, let JavaScript redirect page
		if (SmartContext.isAjaxRequest()) {
			request.setAttribute(Constants.REQUEST_REDIRECT_PATH_AJAX_ATTR, 
					(path.startsWith("/") ? request.getContextPath() : "") + path);
		}

        // Use Forward request internally case is the same page
        request.getRequestDispatcher(HANDLER.getForwardPath(path)).forward(request, response);
	}

	private void sendRedirect(String path, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (request.getServletPath().equals(path)) {
			String url = HANDLER.getForwardPath(path);

			// Use Forward request internally case is the same page
	        request.getRequestDispatcher(url).forward(request, response);

		} else {

			// Use Redirect response internally case page had changed
			response.sendRedirect((path.startsWith("/") ? request.getContextPath() : "") + path);
		}
	}

}
