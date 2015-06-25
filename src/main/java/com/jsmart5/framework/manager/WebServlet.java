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

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jsmart5.framework.config.Constants;
import com.jsmart5.framework.listener.WebContextListener;
import com.jsmart5.framework.listener.WebAsyncListener;
import com.jsmart5.framework.listener.WebAsyncListener.Reason;
import com.jsmart5.framework.util.WebUtils;

import static com.jsmart5.framework.manager.BeanHandler.*;

public final class WebServlet extends HttpServlet {

    private static final long serialVersionUID = -4462762772195421585L;

    private static final Logger LOGGER = Logger.getLogger(WebServlet.class.getPackage().getName());

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        WebContext.setServlet(this);

        // Call registered WebContextListeners
        for (WebContextListener contextListener : HANDLER.contextListeners) {
            HANDLER.executeInjection(contextListener);
            contextListener.contextInitialized(servletConfig.getServletContext());
        }
    }

    @Override
    public void destroy() {
        // Call registered WebContextListeners
        for (WebContextListener contextListener : HANDLER.contextListeners) {
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

        // If path is secure, check if user was logged case @AuthenticationBean annotation was provided
        if (checkAuthentication(path, request, response)) {
            return;
        }

        // Check if user is authorized to access the page. Send HTTP 403 response case they did not have
        Integer httpStatus = HANDLER.checkAuthorization(path);
        if (httpStatus != null) {
            LOGGER.log(Level.INFO, "WebBean access not authorized on page [" + path + "]");
            response.sendError(httpStatus);
            return;
        }

        // Decrypt expressions if needed
        Map<String, String> expressions = HANDLER.getRequestExpressions();

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
            HANDLER.finalizeWebBean(path, request.getSession());
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
            HANDLER.finalizeWebBean(path, request.getSession());
            responsePath = redirectPath;
        }

        // Case is Ajax post action and submit method returned a path, let JavaScript redirect page
        if (responsePath != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            redirectAjax = true;
        }

        if (responsePath == null) {
            responsePath = path;
        } else {

            // Case is Ajax post action, let JavaScript redirect page
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                if (redirectAjax) {
                    request.setAttribute(Constants.REQUEST_REDIRECT_PATH_AJAX_ATTR,
                            (responsePath.startsWith("/") ? request.getContextPath() : "") + responsePath);
                }
                responsePath = path;
            }
        }
        sendRedirect(responsePath, request, response);
    }

    private boolean checkAuthentication(String path, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String authPath = HANDLER.checkAuthentication(path);
        if (authPath != null && !authPath.equals(path)) {
            sendRedirect(authPath, request, response);
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
            HANDLER.finalizeWebBean(path, request.getSession());
            sendRedirect(redirectPath, request, response);
            return;
        }

        // Case response was written directly, just return
        if (WebContext.isResponseWritten()) {
            return;
        }

        // Case is Ajax post action, let JavaScript redirect page
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            request.setAttribute(Constants.REQUEST_REDIRECT_PATH_AJAX_ATTR,
                    (path.startsWith("/") ? request.getContextPath() : "") + path);
        }

        // Use Forward request internally case is the same page
        String url = HANDLER.getForwardPath(path);
        if (url == null) {
            LOGGER.log(Level.SEVERE, "Could not find JSP page for path [" + path + "]");
            return;
        }
        request.getRequestDispatcher(url).forward(request, response);
    }

    private void sendRedirect(String path, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (request.getServletPath().equals(path)) {

            String url = HANDLER.getForwardPath(path);
            if (url == null) {
                LOGGER.log(Level.SEVERE, "Could not find JSP page for path [" + path + "]");
                return;
            }

            // Use Forward request internally case is the same page
            request.getRequestDispatcher(url).forward(request, response);

        } else {
            // Use Redirect response internally case page had changed
            response.sendRedirect((path.startsWith("/") ? request.getContextPath() : "") + path);
        }
    }

    private class WebServletAsyncListener implements AsyncListener {

        private String path;

        private WebAsyncListener bean;

        public WebServletAsyncListener(final String path, final WebAsyncListener bean) {
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
