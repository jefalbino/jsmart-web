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

package com.jsmartframework.web.manager;

import static com.jsmartframework.web.config.Constants.REQUEST_REDIRECT_PATH_AJAX_ATTR;
import static com.jsmartframework.web.config.Constants.REQUEST_REDIRECT_WINDOW_PATH_AJAX_ATTR;
import static com.jsmartframework.web.manager.BeanHandler.HANDLER;
import static com.jsmartframework.web.config.Constants.ENCODING;
import static com.jsmartframework.web.config.Constants.NEXT_URL;

import com.jsmartframework.web.listener.WebAsyncListener;
import com.jsmartframework.web.listener.WebAsyncListener.Reason;
import com.jsmartframework.web.util.WebUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ServletControl extends HttpServlet {

    private static final long serialVersionUID = -4462762772195421585L;

    private static final Logger LOGGER = Logger.getLogger(ServletControl.class.getPackage().getName());

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        WebContext.setServlet(this);

        // Call registered WebContextListeners
        for (ServletContextListener contextListener : HANDLER.contextListeners) {
            HANDLER.executeInjection(contextListener);
            contextListener.contextInitialized(new ServletContextEvent(servletConfig.getServletContext()));
        }
    }

    @Override
    public void destroy() {
        // Call registered WebContextListeners
        for (ServletContextListener contextListener : HANDLER.contextListeners) {
            contextListener.contextDestroyed(new ServletContextEvent(getServletContext()));
        }
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        // If path is secure, check if user was logged case @AuthBean annotation was provided
        if (checkAuthentication(path, request, response)) {
            return;
        }
        // Return if request is for async bean handling
        if (doAsync(path, request, response)) {
            return;
        }
        // If got here the request is for web bean handling
        sendForward(path, request, response);
    }

    @Override
    @SuppressWarnings("all")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        // If path is secure, check if user was logged case @AuthBean annotation was provided
        if (checkAuthentication(path, request, response)) {
            return;
        }

        // Check if user is authorized to access the page. Send HTTP 403 response case they did not have
        Integer httpStatus = HANDLER.checkAuthorization(path);
        if (httpStatus != null) {
            LOGGER.log(Level.INFO, "Access not authorized on page [" + path + "]");
            response.sendError(httpStatus);
            return;
        }

        // Check if user is truly valid by carrying CSRF token if implemented
        httpStatus = HANDLER.checkWebSecurityToken(request);
        if (httpStatus != null) {
            LOGGER.log(Level.INFO, "Possibly invalid access via CSRF attack on page [" + path + "]");
            response.sendError(httpStatus);
            return;
        }

        // Decrypt expressions if needed
        Map<String, String> expressions = HANDLER.getRequestExpressions(request);

        // Initiate beans mentioned on jsp page (Case request scope beans)
        try {
            HANDLER.instantiateBeans(path, expressions);
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "WebBeans on page [" + path + "] could not be instantiated: " + ex.getMessage());
            throw new ServletException(ex);
        }

        // Case user had ordered redirect to specific path in postConstruct method
        String redirectPath = WebContext.getRedirectTo();
        if (redirectPath != null && !redirectPath.equals(path)) {
            sendRedirect(redirectPath, request, response);
            return;
        }

        boolean redirectAjax = false;
        String responsePath = HANDLER.handleRequestExpressions(expressions);

        // Case response was written directly, just return
        if (WebContext.isResponseWritten()) {
            return;
        }

        // Decode the response path case returned from action method
        if (responsePath != null) {
            responsePath = WebUtils.decodePath(responsePath);
        }

        // Case user had ordered redirect to specific path in submitted method
        redirectPath = WebContext.getRedirectTo();
        if (redirectPath != null && !redirectPath.equals(path)) {
            responsePath = redirectPath;
        }

        // Case is Ajax post action and submit method returned a path, let JavaScript redirect page
        if (responsePath != null && WebContext.isAjaxRequest(request)) {
            redirectAjax = true;
        }

        if (responsePath == null) {
            responsePath = path;
        } else {

            // Case is Ajax post action, let JavaScript redirect page
            if (WebContext.isAjaxRequest(request)) {
                if (redirectAjax) {
                    request.setAttribute(REQUEST_REDIRECT_PATH_AJAX_ATTR, getRedirectPath(responsePath, request, false));
                    request.setAttribute(REQUEST_REDIRECT_WINDOW_PATH_AJAX_ATTR, WebContext.isRedirectToWindow());
                }
                responsePath = path;
            }
        }
        sendRedirect(responsePath, request, response);
    }

    private boolean checkAuthentication(String path, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AuthPath authPath = HANDLER.checkAuthentication(path);
        if (authPath.shouldRedirectFromPath(path)) {
            // Case is Ajax post action, let JavaScript redirect page
            if (WebContext.isAjaxRequest(request)) {
                request.setAttribute(REQUEST_REDIRECT_PATH_AJAX_ATTR, getRedirectPath(authPath.getPath(), request, true));
            } else {
                sendRedirect(authPath.getPath(), request, response, !authPath.isHomePath());
            }
            return true;
        }
        return false;
    }

    private boolean doAsync(String path, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            // Only proceed if the AsyncContext was not started to avoid looping whe dispatch is called
            if (!request.isAsyncStarted()) {
                WebAsyncListener bean = (WebAsyncListener) HANDLER.instantiateAsyncBean(path);

                if (bean != null) {
                    AsyncContext asyncContext = request.startAsync();
                    bean.asyncContextCreated(asyncContext);
                    asyncContext.addListener(new WebServletAsyncListener(path, bean));
                    return true;
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "AsyncBean on path [" + path + "] could not be instantiated: " + ex.getMessage());
            throw new ServletException(ex);
        }
        return false;
    }

    private void sendForward(String path, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // Check if user is authorized to access the page. Send HTTP 403 response case they did not have
        Integer httpStatus = HANDLER.checkAuthorization(path);
        if (httpStatus != null) {
            LOGGER.log(Level.INFO, "WebBean access not authorized on page [" + path + "]");
            response.sendError(httpStatus);
            return;
        }

        // Initiate beans mentioned on jsp page
        try {
            HANDLER.instantiateBeans(path, null);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "WebBeans on page [" + path + "] could not be instantiated: " + ex.getMessage());
            throw new ServletException(ex);
        }

        // Case user had ordered redirect to specific path in postConstruct method
        String redirectPath = WebContext.getRedirectTo();
        if (redirectPath != null && !redirectPath.equals(path)) {
            sendRedirect(redirectPath, request, response);
            return;
        }

        // Case response was written directly, just return
        if (WebContext.isResponseWritten()) {
            return;
        }

        // Case is Ajax post action, let JavaScript redirect page
        if (WebContext.isAjaxRequest(request)) {
            request.setAttribute(REQUEST_REDIRECT_PATH_AJAX_ATTR, getRedirectPath(path, request, false));
            request.setAttribute(REQUEST_REDIRECT_WINDOW_PATH_AJAX_ATTR, WebContext.isRedirectToWindow());
        }

        // Use Forward request internally case is the same page
        String url = HANDLER.getForwardPath(path);
        if (url == null) {
            LOGGER.log(Level.SEVERE, "Could not find JSP page for path [" + path + "]");
            return;
        }

        // Generate web security token to prevent CSRF attack
        HANDLER.generateWebSecurityToken(request, response);

        // Use Forward request internally case is the same page
        request.getRequestDispatcher(url).forward(request, response);
    }

    private void sendRedirect(String path, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        sendRedirect(path, request, response, false);
    }

    private void sendRedirect(String path, HttpServletRequest request, HttpServletResponse response, boolean authNeeded) throws IOException, ServletException {
        if (request.getServletPath().equals(path)) {
            String url = HANDLER.getForwardPath(path);
            if (url == null) {
                LOGGER.log(Level.SEVERE, "Could not find JSP page for path [" + path + "]");
                return;
            }

            // Generate web security token to prevent CSRF attack
            HANDLER.generateWebSecurityToken(request, response);

            // Use Forward request internally case is the same page
            request.getRequestDispatcher(url).forward(request, response);

        } else {
            // Use Redirect response case page had changed (Do not use status 302 once cookies are not set)
            response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
            response.setHeader("Location", getRedirectPath(path, request, authNeeded));
        }
    }

    private String getRedirectPath(String path, HttpServletRequest request, boolean authNeeded) {
        StringBuilder nextUrl = new StringBuilder();

        if (authNeeded) {
            nextUrl.append("?").append(NEXT_URL).append("=");

            if (!request.getContextPath().equals("/")) {
                nextUrl.append(request.getContextPath());
            }
            nextUrl.append(request.getServletPath());

            if (StringUtils.isNotBlank(request.getPathInfo())) {
                nextUrl.append(request.getPathInfo());
            }
            if (StringUtils.isNotBlank(request.getQueryString())) {
                nextUrl.append(encodeUrlQuietly("?" + request.getQueryString()));
            }
        } else {
            String nextPath = request.getParameter(NEXT_URL);
            if (StringUtils.isNotBlank(nextPath)) {
                return nextPath;
            }
        }
        return (path.startsWith("/") ? request.getContextPath() : "") + path + nextUrl;
    }

    private String encodeUrlQuietly(String url) {
        try {
            return URLEncoder.encode(url, ENCODING);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private class WebServletAsyncListener implements AsyncListener {

        private String path;

        private WebAsyncListener bean;

        public WebServletAsyncListener(String path, WebAsyncListener bean) {
            this.path = path;
            this.bean = bean;
        }

        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            finalizeAsyncContext(event, Reason.COMPLETE);
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            finalizeAsyncContext(event, Reason.TIMEOUT);
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
            finalizeAsyncContext(event, Reason.ERROR);
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
            try {
                bean = (WebAsyncListener) HANDLER.instantiateAsyncBean(path);
                bean.asyncContextCreated(event.getAsyncContext());
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "AsyncBean on path [" + path + "] could not be instantiated: " + ex.getMessage());
            }
        }

        private void finalizeAsyncContext(AsyncEvent event, Reason reason) throws IOException {
            AsyncContext asyncContext = event.getAsyncContext();
            bean.asyncContextDestroyed(asyncContext, reason);
            HANDLER.finalizeAsyncBean(bean, (HttpServletRequest) asyncContext.getRequest());
        }
    }
}
