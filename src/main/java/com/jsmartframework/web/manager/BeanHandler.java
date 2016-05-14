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

import static com.jsmartframework.web.config.Config.CONFIG;
import static com.jsmartframework.web.config.Constants.REQUEST_EXPOSE_VARS_ATTR;
import static com.jsmartframework.web.manager.ExpressionHandler.EXPRESSIONS;
import static com.jsmartframework.web.manager.ExpressionHandler.EL_PATTERN;
import static com.jsmartframework.web.manager.ExpressionHandler.ID_PATTERN;
import static com.jsmartframework.web.manager.ExpressionHandler.EL_PATTERN_FORMAT;
import static com.jsmartframework.web.manager.ExpressionHandler.BEAN_METHOD_NAME_FORMAT;
import static com.jsmartframework.web.manager.TagHandler.J_TAG_PATTERN;
import static com.jsmartframework.web.manager.BeanHelper.HELPER;

import com.jsmartframework.web.adapter.CsrfAdapter;
import com.jsmartframework.web.annotation.Arg;
import com.jsmartframework.web.annotation.AsyncBean;
import com.jsmartframework.web.annotation.AuthBean;
import com.jsmartframework.web.annotation.AuthField;
import com.jsmartframework.web.annotation.AuthType;
import com.jsmartframework.web.annotation.ExecuteAccess;
import com.jsmartframework.web.annotation.Function;
import com.jsmartframework.web.annotation.PostAction;
import com.jsmartframework.web.annotation.PostSubmit;
import com.jsmartframework.web.annotation.PreAction;
import com.jsmartframework.web.annotation.PreSubmit;
import com.jsmartframework.web.annotation.QueryParam;
import com.jsmartframework.web.annotation.RequestPath;
import com.jsmartframework.web.annotation.ScopeType;
import com.jsmartframework.web.annotation.Action;
import com.jsmartframework.web.annotation.WebBean;
import com.jsmartframework.web.annotation.WebFilter;
import com.jsmartframework.web.annotation.WebListener;
import com.jsmartframework.web.annotation.WebSecurity;
import com.jsmartframework.web.annotation.WebServlet;
import com.jsmartframework.web.config.Constants;
import com.jsmartframework.web.config.UrlPattern;
import com.jsmartframework.web.listener.CsrfRequestListener;
import com.jsmartframework.web.listener.WebAsyncListener;
import com.jsmartframework.web.util.WebUtils;

import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionListener;

public enum BeanHandler {

    HANDLER();

    private static final Logger LOGGER = Logger.getLogger(BeanHandler.class.getPackage().getName());

    private static final Pattern INCLUDE_PATTERN = Pattern.compile("<%@*.include.*file=\"(.*)\".*%>");

    private static final Pattern HANDLER_EL_PATTERN = Pattern.compile(EL_PATTERN.pattern() + "|" + INCLUDE_PATTERN.pattern() + "|" + ID_PATTERN.pattern());

    private static final Pattern SPRING_VALUE_PATTERN = Pattern.compile("[\\$,\\{,\\}]*");

    private static final Pattern PATH_BEAN_ALL_PATTERN = Pattern.compile("(.*)/\\*");

    Map<String, Class<?>> webBeans = new ConcurrentHashMap<>();

    Map<String, Class<?>> authBeans = new ConcurrentHashMap<>();

    Map<String, Class<?>> asyncBeans = new ConcurrentHashMap<>();

    Map<String, Class<?>> webServlets = new ConcurrentHashMap<>();

    Map<String, Class<?>> webFilters = new ConcurrentHashMap<>();

    Map<String, Class<?>> requestPaths = new ConcurrentHashMap<>();

    Map<String, Class<?>> securityListeners = new ConcurrentHashMap<>();

    Set<ServletContextListener> contextListeners = new HashSet<>();

    Set<HttpSessionListener> sessionListeners = new HashSet<>();

    Set<ServletRequestListener> requestListeners = new HashSet<>();

    Map<String, AnnotatedFunction> beanMethodFunctions = new ConcurrentHashMap<>();

    private Map<String, List<AnnotatedFunction>> annotatedFunctions = new ConcurrentHashMap<>();

    private Map<String, AnnotatedAction> annotatedActions = new ConcurrentHashMap<>();

    private Map<String, String> forwardPaths = new ConcurrentHashMap<>();

    private Map<Class<?>, String> jndiMapping = new ConcurrentHashMap<>();

    private Map<String, JspPageBean> jspPageBeans = new ConcurrentHashMap<>();

    private InitialContext initialContext;

    private ApplicationContext springContext;

    void init(ServletContext context) {
        checkWebXmlPath(context);
        initJndiMapping();
        initAnnotatedBeans();
        initForwardPaths(context);
        initJspPageBeans(context);
    }

    void destroy(ServletContext context) {
        try {
            finalizeWebBeans(context);
            authBeans.clear();
            webBeans.clear();
            asyncBeans.clear();
            webServlets.clear();
            webFilters.clear();
            requestPaths.clear();
            securityListeners.clear();
            contextListeners.clear();
            sessionListeners.clear();
            requestListeners.clear();
            forwardPaths.clear();
            jspPageBeans.clear();
            annotatedFunctions.clear();
            beanMethodFunctions.clear();
            annotatedActions.clear();
            jndiMapping.clear();
            initialContext = null;
            springContext = null;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to destroy BeanHandler: " + ex.getMessage());
        }
    }

    void setSpringContext(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    @Deprecated
    boolean executePreSubmit(Object bean, String action) {
        for (Method method : HELPER.getPreSubmitMethods(bean.getClass())) {
            for (String onAction : method.getAnnotation(PreSubmit.class).onActions()) {
                try {
                    if (action.equalsIgnoreCase(onAction)) {
                        Boolean result = (Boolean) method.invoke(bean, null);
                        return result != null ? result : true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }

    boolean executePreAction(Object bean, String action) {
        for (Method method : HELPER.getPreActionMethods(bean.getClass())) {
            for (String onAction : method.getAnnotation(PreAction.class).onActions()) {
                try {
                    if (action.equalsIgnoreCase(onAction)) {
                        Boolean result = (Boolean) method.invoke(bean, null);
                        return result != null ? result : true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }

    @Deprecated
    void executePostSubmit(Object bean, String action) {
        for (Method method : HELPER.getPostSubmitMethods(bean.getClass())) {
            for (String onAction : method.getAnnotation(PostSubmit.class).onActions()) {
                try {
                    if (action.equalsIgnoreCase(onAction)) {
                        method.invoke(bean, null);
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return;
        }
    }

    void executePostAction(Object bean, String action) {
        for (Method method : HELPER.getPostActionMethods(bean.getClass())) {
            for (String onAction : method.getAnnotation(PostAction.class).onActions()) {
                try {
                    if (action.equalsIgnoreCase(onAction)) {
                        method.invoke(bean, null);
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return;
        }
    }

    void executePreDestroy(Object bean) {
        for (Method method : HELPER.getPreDestroyMethods(bean.getClass())) {
            if (method.isAnnotationPresent(PreDestroy.class)) {
                try {
                    method.invoke(bean, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }
        }
    }

    void executePostConstruct(Object bean) {
        for (Method method : HELPER.getPostConstructMethods(bean.getClass())) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                try {
                    method.invoke(bean, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }
        }
    }

    void executePreSet(String name, Object bean, Map<String, String> expressions) {
        if (expressions == null || expressions.isEmpty()) {
            return;
        }
        try {
            for (Field field : HELPER.getPreSetFields(bean.getClass())) {
                for (Entry<String, String> expr : expressions.entrySet()) {
                    Matcher elMatcher = EL_PATTERN.matcher(expr.getValue());
                    if (elMatcher.find() && elMatcher.group(1).contains(name + "." + field.getName())) {

                        Matcher tagMatcher = J_TAG_PATTERN.matcher(expr.getKey());
                        if (tagMatcher.find()) {
                            String jTag = tagMatcher.group(1);
                            String jParam = tagMatcher.group(2);

                            EXPRESSIONS.handleRequestExpression(jTag, expr.getValue(), jParam);
                            expressions.remove(expr.getKey());
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Execution of PreSet on WebBean " + bean + " failed: " + ex.getMessage());
        }
    }

    boolean containsUnescapeMethod(String[] names) {
        if (names != null && names.length > 1) {
            Class<?> clazz = webBeans.get(names[0]);
            if (clazz == null) {
                return false;
            }
            for (String methodName : HELPER.getUnescapeMethods(clazz)) {
                if (names[1].equalsIgnoreCase(methodName)) {
                    return true;
                }
            }
        }
        return false;
    }

    Map<String, String> getRequestExpressions(HttpServletRequest request) {
        return EXPRESSIONS.getRequestExpressions(request);
    }

    String handleRequestExpressions(Map<String, String> expressions) throws ServletException, IOException {
        String submitParam = null;
        String submitExpr = null;

        for (Entry<String, String> expr : expressions.entrySet()) {
            Matcher matcher = J_TAG_PATTERN.matcher(expr.getKey());
            if (matcher.find()) {

                String jTag = matcher.group(1);
                String jParam = matcher.group(2);
                EXPRESSIONS.handleRequestExpression(jTag, expr.getValue(), jParam);

                if (jTag.equals(TagHandler.J_SBMT)) {
                    submitExpr = expr.getValue();
                    submitParam = jParam;
                }
            }
        }

        String responsePath = null;
        if (submitExpr != null) {
            responsePath = EXPRESSIONS.handleSubmitExpression(submitExpr, submitParam);
        }
        return responsePath;
    }

    void instantiateBeans(String path, Map<String, String> expressions) throws Exception {
        JspPageBean jspPageBean = jspPageBeans.get(path);
        if (jspPageBean != null) {
            for (String name : jspPageBean.getBeanNames()) {
                instantiateBean(name, expressions);
            }
        }
    }

    private Object instantiateBean(String name, Map<String, String> expressions) throws Exception {
        Object bean = null;
        ServletContext context = WebContext.getApplication();
        HttpSession session = WebContext.getSession();
        HttpServletRequest request = WebContext.getRequest();

        if (request.getAttribute(name) != null) {
            bean = request.getAttribute(name);
            executeInjection(bean);
            return bean;
        }

        synchronized (session) {
            if (session.getAttribute(name) != null) {
                bean = session.getAttribute(name);
                executeInjection(bean);
                return bean;
            }
        }

        if (context.getAttribute(name) != null) {
            bean = context.getAttribute(name);
            executeInjection(bean);
            return bean;
        }

        if (webBeans.containsKey(name)) {
            Class<?> clazz = webBeans.get(name);
            bean = clazz.newInstance();

            WebBean webBean = clazz.getAnnotation(WebBean.class);
            if (webBean.scope().equals(ScopeType.REQUEST)) {
                request.setAttribute(name, bean);

            } else if (webBean.scope().equals(ScopeType.SESSION)) {
                synchronized (session) {
                    session.setAttribute(name, bean);
                }
            } else if (webBean.scope().equals(ScopeType.APPLICATION)) {
                context.setAttribute(name, bean);

            } else {
                return null;
            }

            executeInjection(bean);
            executePreSet(name, bean, expressions);
            executePostConstruct(bean);
        }
        return bean;
    }

    Object instantiateAsyncBean(String path) throws Exception {
        Class<?> clazz = asyncBeans.get(path);
        if (clazz != null) {
            Object bean = clazz.newInstance();
            executeInjection(bean);
            return bean;
        }
        return null;
    }

    void executeInjection(Object object) {
        try {
            HttpSession session = WebContext.getSession();
            HttpServletRequest request = WebContext.getRequest();

            for (Field field : HELPER.getBeanFields(object.getClass())) {
                if (field.getAnnotations().length == 0) {
                    continue;
                }

                if (field.isAnnotationPresent(Inject.class)) {
                    WebBean webBean = field.getType().getAnnotation(WebBean.class);
                    if (webBean != null) {
                        field.setAccessible(true);
                        field.set(object, instantiateBean(HELPER.getClassName(webBean, field.getType()), null));
                        continue;
                    }

                    AuthBean authBean = field.getType().getAnnotation(AuthBean.class);
                    if (authBean != null) {
                        field.setAccessible(true);
                        if (authBean.type() == AuthType.SESSION) {
                            field.set(object, instantiateAuthBean(HELPER.getClassName(authBean, field.getType()), session));

                        } else if (authBean.type() == AuthType.REQUEST) {
                            field.set(object, instantiateAuthBean(HELPER.getClassName(authBean, field.getType()), request));
                        }
                        continue;
                    }
                }

                // Inject URL Parameters
                if (field.isAnnotationPresent(QueryParam.class)) {
                    QueryParam queryParam = field.getAnnotation(QueryParam.class);
                    String paramValue = request.getParameter(queryParam.value());

                    if (paramValue != null) {
                        field.setAccessible(true);
                        field.set(object, ExpressionHandler.EXPRESSIONS.decodeUrl(paramValue));
                    }
                    continue;
                }

                // Inject dependencies
                if (initialContext != null && jndiMapping.containsKey(field.getType())) {
                    field.setAccessible(true);
                    field.set(object, initialContext.lookup(jndiMapping.get(field.getType())));
                    continue;
                }

                if (springContext != null) {
                    String beanName = HELPER.getClassName(field.getType().getSimpleName());
                    if (springContext.containsBean(beanName) || field.isAnnotationPresent(Qualifier.class)) {
                        field.setAccessible(true);
                        field.set(object, springContext.getBean(field.getType()));

                    } else if (field.isAnnotationPresent(Value.class)) {
                        String propertyName = field.getAnnotation(Value.class).value();
                        propertyName = SPRING_VALUE_PATTERN.matcher(propertyName).replaceAll("");
                        field.setAccessible(true);
                        field.set(object, springContext.getEnvironment().getProperty(propertyName, field.getType()));
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Injection on object " + object + " failed", ex);
        }
    }

    void finalizeAsyncBean(Object bean, HttpServletRequest request) {
        if (bean != null) {
            finalizeInjection(bean, request);
        }
    }

    void finalizeWebBeans(ServletContext servletContext) {
        List<String> names = Collections.list(servletContext.getAttributeNames());
        for (String name : names) {
            Object bean = servletContext.getAttribute(name);
            if (bean == null) {
                continue;
            }
            if (bean.getClass().isAnnotationPresent(WebBean.class)) {
                finalizeWebBean(bean, servletContext);
            }
        }
    }

    private void finalizeWebBean(Object bean, ServletContext servletContext) {
        if (bean != null) {
            executePreDestroy(bean);
            finalizeInjection(bean, servletContext);
            WebBean webBean = bean.getClass().getAnnotation(WebBean.class);
            servletContext.removeAttribute(HELPER.getClassName(webBean, bean.getClass()));
        }
    }

    void finalizeBeans(HttpSession session) {
        synchronized (session) {
            List<String> names = Collections.list(session.getAttributeNames());

            for (String name : names) {
                Object bean = session.getAttribute(name);
                if (bean == null) {
                    continue;
                }
                if (bean.getClass().isAnnotationPresent(WebBean.class)) {
                    finalizeWebBean(bean, session);

                } else if (bean.getClass().isAnnotationPresent(AuthBean.class)) {
                    finalizeAuthBean(bean, session);
                }
            }
        }
    }

    private void finalizeWebBean(Object bean, HttpSession session) {
        if (bean != null) {
            executePreDestroy(bean);
            finalizeInjection(bean, session);
            WebBean webBean = bean.getClass().getAnnotation(WebBean.class);
            session.removeAttribute(HELPER.getClassName(webBean, bean.getClass()));
        }
    }

    public void finalizeBeans(HttpServletRequest request, HttpServletResponse response) {
        List<String> names = Collections.list(request.getAttributeNames());
        for (String name : names) {
            Object bean = request.getAttribute(name);
            if (bean == null) {
                continue;
            }

            Field[] exposeVars = HELPER.getExposeVarFields(bean.getClass());
            for (int i = 0; i < exposeVars.length; i++) {
                try {
                    setExposeVarAttribute(request, exposeVars[i].getName(), exposeVars[i].get(bean));
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Could not expose var " + exposeVars[i], ex);
                }
            }

            if (bean.getClass().isAnnotationPresent(WebBean.class)) {
                finalizeWebBean(bean, request);

            } else if (bean.getClass().isAnnotationPresent(AuthBean.class)) {
                finalizeAuthBean(bean, request, response);

            } else if (bean.getClass().isAnnotationPresent(WebSecurity.class)) {
                finalizeWebSecurity(bean, request);
            }
        }
    }

    private void setExposeVarAttribute(HttpServletRequest request, String name, Object value) {
        Map<String, Object> exposeVars = (Map) request.getAttribute(REQUEST_EXPOSE_VARS_ATTR);
        if (exposeVars == null) {
            request.setAttribute(REQUEST_EXPOSE_VARS_ATTR, (exposeVars = new ConcurrentHashMap<>()));
        }
        exposeVars.put(name, value);
    }

    private void finalizeWebBean(Object bean, HttpServletRequest request) {
        if (bean != null) {
            executePreDestroy(bean);
            finalizeInjection(bean, request);
            WebBean webBean = bean.getClass().getAnnotation(WebBean.class);
            request.removeAttribute(HELPER.getClassName(webBean, bean.getClass()));
        }
    }

    private void finalizeInjection(Object bean, Object servletObject) {
        try {
            for (Field field : HELPER.getBeanFields(bean.getClass())) {
                if (field.isAnnotationPresent(Inject.class)) {

                    if (field.getType().isAnnotationPresent(WebBean.class)) {
                        field.setAccessible(true);

                        if (servletObject instanceof HttpServletRequest) {
                            finalizeWebBean(field.get(bean), (HttpServletRequest) servletObject);

                        } else if (servletObject instanceof HttpSession) {
                            finalizeWebBean(field.get(bean), (HttpSession) servletObject);

                        } else if (servletObject instanceof ServletContext) {
                            finalizeWebBean(field.get(bean), (ServletContext) servletObject);
                        }
                        field.set(bean, null);
                        continue;
                    }

                    if (field.getType().isAnnotationPresent(AuthBean.class)) {
                        field.setAccessible(true);
                        field.set(bean, null);
                        continue;
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Finalize injection on WebBean " + bean + " failed: " + ex.getMessage());
        }
    }

    void instantiateAuthBean(HttpSession session) {
        for (String name : authBeans.keySet()) {
            AuthBean authBean = authBeans.get(name).getAnnotation(AuthBean.class);

            if (authBean.type() == AuthType.SESSION) {
                instantiateAuthBean(name, session);
            }
            // We must have only one @AuthBean mapped
            break;
        }
    }

    private Object instantiateAuthBean(String name, HttpSession session) {
        synchronized (session) {
            Object bean = session.getAttribute(name);
            if (bean != null) {
                return bean;
            }
            bean = initializeAuthBean(name, null);
            session.setAttribute(name, bean);
            return bean;
        }
    }

    void instantiateAuthBean(HttpServletRequest request) {
        for (String name : authBeans.keySet()) {
            AuthBean authBean = authBeans.get(name).getAnnotation(AuthBean.class);

            if (authBean.type() == AuthType.REQUEST) {
                instantiateAuthBean(name, request);
            }
            // We must have only one @AuthBean mapped
            break;
        }
    }

    private Object instantiateAuthBean(String name, HttpServletRequest request) {
        Object bean = request.getAttribute(name);
        if (bean != null) {
            return bean;
        }
        bean = initializeAuthBean(name, request);
        request.setAttribute(name, bean);
        return bean;
    }

    private Object initializeAuthBean(String name, HttpServletRequest request) {
        Object bean = null;
        try {
            int index = 0;
            bean = authBeans.get(name).newInstance();
            AuthBean authBean = authBeans.get(name).getAnnotation(AuthBean.class);

            for (Field field : HELPER.getBeanFields(bean.getClass())) {
                if (field.getAnnotations().length == 0) {
                    continue;
                }

                // Set authentication cookies when initializing @AuthBean
                if (request != null && field.isAnnotationPresent(AuthField.class)) {
                    AuthField authField = field.getAnnotation(AuthField.class);
                    field.setAccessible(true);
                    String fieldValue = WebUtils.getCookie(request, authField.value());

                    if (fieldValue != null) {
                        fieldValue = AuthEncrypter.decrypt(request, authBean.secretKey(), fieldValue);
                    }
                    field.set(bean, fieldValue);
                    continue;
                }

                if (initialContext != null && jndiMapping.containsKey(field.getType())) {
                    field.setAccessible(true);
                    field.set(bean, initialContext.lookup(jndiMapping.get(field.getType())));
                    continue;
                }

                if (springContext != null) {
                    String beanName = HELPER.getClassName(field.getType().getSimpleName());
                    if (springContext.containsBean(beanName) || field.isAnnotationPresent(Qualifier.class)) {
                        field.setAccessible(true);
                        field.set(bean, springContext.getBean(field.getType()));

                    } else if (field.isAnnotationPresent(Value.class)) {
                        String propertyName = field.getAnnotation(Value.class).value();
                        propertyName = SPRING_VALUE_PATTERN.matcher(propertyName).replaceAll("");
                        field.setAccessible(true);
                        field.set(bean, springContext.getEnvironment().getProperty(propertyName, field.getType()));
                    }
                }
            }
            executePostConstruct(bean);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Injection on AuthBean " + bean + " failed: " + ex.getMessage());
        }
        return bean;
    }

    private void finalizeAuthBean(Object bean, HttpSession session) {
        executePreDestroy(bean);
        AuthBean authBean = bean.getClass().getAnnotation(AuthBean.class);
        session.removeAttribute(HELPER.getClassName(authBean, bean.getClass()));
    }

    private void finalizeAuthBean(Object bean, HttpServletRequest request, HttpServletResponse response) {
        executePreDestroy(bean);
        AuthBean authBean = bean.getClass().getAnnotation(AuthBean.class);
        try {
            for (Field field : HELPER.getBeanFields(bean.getClass())) {
                if (field.getAnnotations().length > 0) {
                    field.setAccessible(true);

                    if (field.isAnnotationPresent(AuthField.class)) {
                        AuthField authField = field.getAnnotation(AuthField.class);

                        Object value = field.get(bean);
                        if (value != null) {
                            // Return encrypted auth fields as cookies to check if customer is still
                            // logged on next request
                            String cookieValue = AuthEncrypter.encrypt(request, authBean.secretKey(), value);

                            Cookie cookie = getAuthenticationCookie(request, authField.value(), cookieValue, -1);
                            response.addCookie(cookie);
                        } else {
                            // Case value is null we force Cookie deletion on client side
                            Cookie cookie = getAuthenticationCookie(request, authField.value(), null, 0);
                            response.addCookie(cookie);
                        }
                    }
                    field.set(bean, null);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Finalize injection on AuthBean " + bean + " failed: " + ex.getMessage());
        }
        request.removeAttribute(HELPER.getClassName(authBean, bean.getClass()));
    }

    private Cookie getAuthenticationCookie(HttpServletRequest request, String name, String value, int age) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(age);
        return cookie;
    }

    void instantiateWebSecurity(HttpServletRequest request) {
        for (String name : securityListeners.keySet()) {
            WebSecurity webSecurity = securityListeners.get(name).getAnnotation(WebSecurity.class);

            Object listener = request.getAttribute(name);
            if (listener == null) {
                try {
                    listener = securityListeners.get(name).newInstance();
                    executeInjection(listener);
                    executePostConstruct(listener);
                    request.setAttribute(name, listener);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Injection on WebSecurity " + name + " failed: " + ex.getMessage());
                }
            }
            // We must have only one @WebSecurity mapped
            break;
        }
    }

    private void finalizeWebSecurity(Object listener, HttpServletRequest request) {
        executePreDestroy(listener);
        WebSecurity webSecurity = listener.getClass().getAnnotation(WebSecurity.class);
        request.removeAttribute(HELPER.getClassName(webSecurity, listener.getClass()));
    }

    String checkAuthentication(String path) throws ServletException {
        if (authBeans.isEmpty() && !CONFIG.getContent().getSecureUrls().isEmpty()) {
            throw new ServletException("Not found AuthBean mapped in your system. Once your system " +
                    "has secure urls, please map a bean with @AuthBean!");
        }

        AuthBean authBean = null;
        boolean authenticated = true;

        for (String name : authBeans.keySet()) {
            authBean = authBeans.get(name).getAnnotation(AuthBean.class);

            if (authBean.type() == AuthType.SESSION) {
                HttpSession session = WebContext.getSession();
                synchronized (session) {
                    authenticated = checkAuthentication(session.getAttribute(name));
                }
            } else if (authBean.type() == AuthType.REQUEST) {
                HttpServletRequest request = WebContext.getRequest();
                authenticated = checkAuthentication(request.getAttribute(name));
            }
            // We must have only one @AuthBean mapped
            break;
        }

        // Access secure url
        //  - User authenticated ===>>> ok redirect to path
        //  - User not authenticated ===>>> redirect to login
        if (CONFIG.getContent().containsSecureUrl(path)) {
            if (authenticated) {
                return path;
            } else {
                return WebUtils.decodePath(authBean.loginPath());
            }
        }
        // Access non secure url
        //   - User authenticated
        //         - access login page or except page ===>>> redirect to home
        //         - other cases ===>>> ok redirect to path
        //   - User not authenticated ===>>> ok redirect to path
        else {
            if (authenticated) {
                if (authBean != null && (path.equals(WebUtils.decodePath(authBean.loginPath()))
                        || CONFIG.getContent().containsNonSecureUrlOnly(path))) {
                    return WebUtils.decodePath(authBean.homePath());
                } else {
                    return path;
                }
            } else {
                return path;
            }
        }
    }

    private boolean checkAuthentication(Object bean) throws ServletException {
        boolean authenticated = true;
        if (bean == null) {
            return authenticated;
        }

        // Look for fields in order to check if the request is already authenticated
        for (Field field : HELPER.getAuthFields(bean.getClass())) {
            try {
                field.setAccessible(true);
                // If field is not set it means that user is not authenticated
                if (field.get(bean) == null) {
                    authenticated = false;
                    break;
                }
            } catch (Exception ex) {
                throw new ServletException("AuthField not accessible: " + ex.getMessage(), ex);
            }
        }

        // Check authenticate methods to validate credentials
        for (Method method : HELPER.getAuthMethods(bean.getClass())) {
            try {
                Boolean auth = (Boolean) method.invoke(bean);
                authenticated &= auth != null ? auth : false;
            } catch (Exception ex) {
                throw new ServletException("AuthMethod not accessible: " + ex.getMessage(), ex);
            }
        }
        return authenticated;
    }

    @SuppressWarnings("all")
    Integer checkAuthorization(String path) {
        if (CONFIG.getContent().containsSecureUrl(path) && !authBeans.isEmpty()) {
            AuthBean authBean = null;
            Collection<String> userAccess = getUserAuthorizationAccess();

            // Check mapped urls
            UrlPattern urlPattern = CONFIG.getContent().getUrlPattern(path);
            if (urlPattern != null && urlPattern.getAccess() != null) {

                for (String access : urlPattern.getAccess()) {
                    if (userAccess.contains(access) || access.equals("*")) {
                        return null; // It means, authorized user
                    }
                }
                return HttpServletResponse.SC_FORBIDDEN;
            }
        }
        return null; // It means, authorized user
    }

    Collection<String> getUserAuthorizationAccess() {
        HttpServletRequest request = WebContext.getRequest();

        for (String name : authBeans.keySet()) {
            AuthBean authBean = authBeans.get(name).getAnnotation(AuthBean.class);

            if (authBean.type() == AuthType.SESSION) {
                HttpSession session = WebContext.getSession();
                synchronized (session) {
                    return getUserAuthorizationAccess(session.getAttribute(name), request);
                }
            } else if (authBean.type() == AuthType.REQUEST) {
                return getUserAuthorizationAccess(request.getAttribute(name), request);
            }
            // We must have only one @AuthBean mapped
            break;
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    Collection<String> getUserAuthorizationAccess(Object bean, HttpServletRequest request) {
        if (request.getAttribute(Constants.REQUEST_USER_ACCESS) == null) {
            Collection<String> userAccess = new HashSet<>();

            for (Field field : HELPER.getAuthAccess(bean.getClass())) {
                try {
                    field.setAccessible(true);
                    Object object = field.get(bean);
                    if (object != null) {
                        userAccess.addAll((Collection<String>) object);
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "AuthAccess mapped on WebBean [" + bean + "] could " +
                                             "not be cast to Collection<String>: " + ex.getMessage());
                }
                break;
            }
            request.setAttribute(Constants.REQUEST_USER_ACCESS, userAccess);
        }
        return (Collection<String>) request.getAttribute(Constants.REQUEST_USER_ACCESS);
    }

    boolean checkExecuteAuthorization(Object bean, String expression, HttpServletRequest request) {
        for (Method method : HELPER.getExecuteAccessMethods(bean.getClass())) {
            ExecuteAccess execAccess = method.getAnnotation(ExecuteAccess.class);

            if (execAccess.access().length > 0 && expression.contains(method.getName())) {
                Collection<String> userAccess = getUserAuthorizationAccess(bean, request);

                if (!userAccess.isEmpty()) {
                    for (String access : execAccess.access()) {
                        if (userAccess.contains(access)) {
                            return true;
                        }
                    }
                    return false;
                }
                break;
            }
        }
        return true;
    }

    Integer checkWebSecurityToken(HttpServletRequest request) {
        if (securityListeners.isEmpty()) {
            return null; // It means, valid user access
        }

        for (String name : securityListeners.keySet()) {
            WebSecurity webSecurity = securityListeners.get(name).getAnnotation(WebSecurity.class);
            CsrfRequestListener listener = (CsrfRequestListener) request.getAttribute(name);

            String csrfName = request.getHeader(Constants.CSRF_TOKEN_NAME);
            if (StringUtils.isBlank(csrfName)) {
                csrfName = request.getParameter(Constants.CSRF_TOKEN_NAME);
            }
            String csrfToken = request.getHeader(Constants.CSRF_TOKEN_VALUE);
            if (StringUtils.isBlank(csrfToken)) {
                csrfToken = request.getParameter(Constants.CSRF_TOKEN_VALUE);
            }

            if (StringUtils.isBlank(csrfName) && StringUtils.isBlank(csrfToken)) {
                return HttpServletResponse.SC_FORBIDDEN;
            }

            if (!webSecurity.disableEncrypt()) {
                csrfName = CsrfEncrypter.decrypt(request, webSecurity.secretKey(), csrfName);
                csrfToken = CsrfEncrypter.decrypt(request, webSecurity.secretKey(), csrfToken);
            }
            if (!listener.isValidToken(new CsrfAdapter(csrfName, csrfToken))) {
                return HttpServletResponse.SC_FORBIDDEN;
            }
            // We must have only one @webSecurity mapped
            break;
        }
        return null; // It means, valid user access
    }

    void generateWebSecurityToken(HttpServletRequest request, HttpServletResponse response) {
        if (request.getAttribute(Constants.REQUEST_META_DATA_CSRF_TOKEN_NAME) == null) {
            for (String name : securityListeners.keySet()) {

                WebSecurity webSecurity = securityListeners.get(name).getAnnotation(WebSecurity.class);
                CsrfRequestListener listener = (CsrfRequestListener) request.getAttribute(name);

                CsrfAdapter csrfAdapter = listener.generateToken();
                if (csrfAdapter == null || StringUtils.isBlank(csrfAdapter.getName()) || StringUtils.isBlank(csrfAdapter.getToken())) {
                    LOGGER.warning("Class " + name + " returned invalid token from generateToken method");
                    return;
                }

                String csrfName = csrfAdapter.getName();
                String csrfToken = csrfAdapter.getToken();

                if (!webSecurity.disableEncrypt()) {
                    csrfName = CsrfEncrypter.encrypt(request, webSecurity.secretKey(), csrfAdapter.getName());
                    csrfToken = CsrfEncrypter.encrypt(request, webSecurity.secretKey(), csrfAdapter.getToken());
                }
                request.setAttribute(Constants.REQUEST_META_DATA_CSRF_TOKEN_NAME, csrfName);
                request.setAttribute(Constants.REQUEST_META_DATA_CSRF_TOKEN_VALUE, csrfToken);
                // We must have only one @webSecurity mapped
                break;
            }
        }
    }

    private void initAnnotatedBeans() {
        if (CONFIG.getContent().getPackageScan() == null) {
            LOGGER.log(Level.SEVERE, "None [package-scan] tag was found on " + Constants.WEB_CONFIG_XML +
                                     " file! Skipping package scanning.");
            return;
        }

        Object[] packages = CONFIG.getContent().getPackageScan().split(",");
        Reflections reflections = new Reflections(packages);

        initAnnotatedWebBeans(reflections);
        initAnnotatedAuthBeans(reflections);
        initAnnotatedRequestPaths(reflections);
        initAnnotatedAsyncBeans(reflections);
        initAnnotatedWebServlets(reflections);
        initAnnotatedWebFilters(reflections);
        initAnnotatedWebListeners(reflections);
        initAnnotatedWebSecurities(reflections);
    }

    private void initAnnotatedWebBeans(Reflections reflections) {
        Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(WebBean.class);

        for (Class<?> clazz : annotations) {
            WebBean bean = clazz.getAnnotation(WebBean.class);
            LOGGER.log(Level.INFO, "Mapping WebBean class: " + clazz);

            if (bean.scope() == ScopeType.SESSION && !Serializable.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("Mapped WebBean class [" + clazz + "] with scope [" + bean.scope() + "] " +
                                           "must implement java.io.Serializable interface");
            }

            HELPER.setBeanFields(clazz);
            HELPER.setBeanMethods(clazz);
            String className = HELPER.getClassName(bean, clazz);
            webBeans.put(className, clazz);

            initAnnotatedActionMethods(className, clazz);
        }

        if (webBeans.isEmpty()) {
            LOGGER.log(Level.INFO, "WebBeans were not mapped!");
        }
    }

    private void initAnnotatedActionMethods(String className, Class<?> clazz) {
        for (Method method : HELPER.getBeanMethods(clazz)) {
            List<Arg> arguments = new ArrayList<>();

            for (Annotation[] annotations : method.getParameterAnnotations()) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Arg) {
                        arguments.add((Arg) annotation);
                    }
                }
            }

            if (method.isAnnotationPresent(Function.class)) {
                AnnotatedFunction annotatedFunction = new AnnotatedFunction(method, className, arguments);

                // Keep track of functions per bean method
                beanMethodFunctions.put(String.format(BEAN_METHOD_NAME_FORMAT, className, method.getName()),
                        annotatedFunction);

                String[] urlPaths = annotatedFunction.getFunction().forPaths();
                if (urlPaths.length == 1 && "/*".equals(urlPaths[0].trim())) {
                    urlPaths = CONFIG.getContent().getUrlPatternsArray();
                }

                for (String urlPattern : urlPaths) {
                    // Functions are created per Url-Pattern
                    String path = getCleanPath(urlPattern);
                    path = matchUrlPattern(path);

                    List<AnnotatedFunction> pathFunctions = annotatedFunctions.get(path);
                    if (pathFunctions == null) {
                        pathFunctions = new ArrayList<>();
                        annotatedFunctions.put(path, pathFunctions);
                    }
                    pathFunctions.add(annotatedFunction);
                }
            }

            if (method.isAnnotationPresent(Action.class)) {
                AnnotatedAction annotatedAction = new AnnotatedAction(method, className, arguments);
                for (String id : annotatedAction.getAction().forIds()) {
                    annotatedActions.put(id, annotatedAction);
                }
            }
        }
    }

    private void initAnnotatedAuthBeans(Reflections reflections) {
        Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(AuthBean.class);

        for (Class<?> clazz : annotations) {
            AuthBean authBean = clazz.getAnnotation(AuthBean.class);
            if (authBeans.isEmpty()) {
                LOGGER.log(Level.INFO, "Mapping AuthBean class: " + clazz);

                if (authBean.type() == AuthType.SESSION && !Serializable.class.isAssignableFrom(clazz)) {
                    throw new RuntimeException("Mapped AuthBean class [" + clazz + "] of type AuthType.SESSION " +
                                               "must implement java.io.Serializable interface");
                }
                if (authBean.type() == AuthType.REQUEST && authBean.secretKey().length() != AuthEncrypter.CYPHER_KEY_LENGTH) {
                    throw new RuntimeException("Mapped AuthBean annotation for class [" + clazz + "] must " +
                                               "have its secretKey value with [" + AuthEncrypter.CYPHER_KEY_LENGTH +
                                               "] characters");
                }

                HELPER.setBeanFields(clazz);
                HELPER.setBeanMethods(clazz);
                HELPER.setAuthFields(clazz);
                HELPER.setAuthAccess(clazz);
                HELPER.setAuthMethods(clazz);

                if (HELPER.getAuthFields(clazz).length == 0) {
                    throw new RuntimeException("Mapped AuthBean class [" + clazz + "] must contain at " +
                                               "least one field annotated with @AuthField");
                }
                if (HELPER.hasPrimitiveAuthFields(clazz)) {
                    throw new RuntimeException("Mapped AuthBean class [" + clazz + "] must have all fields " +
                            "annotated with @AuthField as non primitive type");
                }
                if (HELPER.getAuthMethods(clazz).length == 0) {
                    throw new RuntimeException("Mapped AuthBean class [" + clazz + "] must contain at " +
                                               "least one method that is annotated with @AuthMethod " +
                                               "and returns boolean value to return authentication status");
                }

                String className = HELPER.getClassName(authBean, clazz);
                authBeans.put(className, clazz);

                initAnnotatedActionMethods(className, clazz);
            } else {
                LOGGER.log(Level.SEVERE, "Only one AuthBean can be declared! Skipping class " + clazz);
            }
        }

        if (authBeans.isEmpty()) {
            LOGGER.log(Level.INFO, "AuthBean was not mapped!");
        }
        checkWebBeanConstraint(annotations, "@AuthBean");
    }

    private void initAnnotatedRequestPaths(Reflections reflections) {
        Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(RequestPath.class);

        for (Class<?> clazz : annotations) {
            RequestPath requestPath = clazz.getAnnotation(RequestPath.class);
            LOGGER.log(Level.INFO, "Mapping RequestPath class: " + clazz);

            if (!clazz.isAnnotationPresent(Controller.class)) {
                throw new RuntimeException("Mapped RequestPath class [" + clazz + "] must be annotated with " +
                                           "org.springframework.stereotype.Controller from Spring");
            }
            if (!requestPath.value().endsWith("*")) {
                throw new RuntimeException("Mapped class [" + clazz + "] annotated with @RequestPath must have its " +
                                           "path annotation attribute ending with * character");
            }

            HELPER.setBeanFields(clazz);
            HELPER.setBeanMethods(clazz);
            requestPaths.put(requestPath.value(), clazz);
        }

        if (requestPaths.isEmpty()) {
            LOGGER.log(Level.INFO, "RequestPaths were not mapped!");
        }

        for (Class<?> pathClazz : requestPaths.values()) {
            for (Class<?> webClazz : webBeans.values()) {
                if (webClazz == pathClazz) {
                    LOGGER.log(Level.SEVERE, "@WebBean class [" + webClazz + "] cannot be annotated with @RequestPath");
                }
            }
        }
        checkWebBeanConstraint(annotations, "@RequestPath");
        checkAuthBeanConstraint(annotations, "@RequestPath");
    }

    private void initAnnotatedAsyncBeans(Reflections reflections) {
        Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(AsyncBean.class);

        for (Class<?> clazz : annotations) {
            AsyncBean asyncBean = clazz.getAnnotation(AsyncBean.class);
            LOGGER.log(Level.INFO, "Mapping AsyncBean class: " + clazz);

            if (!WebAsyncListener.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("Mapped AsyncBean class [" + clazz + "] must implement " +
                                           "WebAsyncListener interface");
            }

            HELPER.setBeanFields(clazz);
            HELPER.setBeanMethods(clazz);
            String path = getCleanPath(asyncBean.value());
            path = matchUrlPattern(path);
            asyncBeans.put(path, clazz);
        }

        if (asyncBeans.isEmpty()) {
            LOGGER.log(Level.INFO, "AsyncBeans were not mapped!");
        }
        checkWebBeanConstraint(annotations, "@AsyncBean");
        checkAuthBeanConstraint(annotations, "@AsyncBean");
        checkRequestPathConstraint(annotations, "@AsyncBean");
    }

    private void initAnnotatedWebServlets(Reflections reflections) {
        Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(WebServlet.class);

        for (Class<?> clazz : annotations) {
            WebServlet servlet = clazz.getAnnotation(WebServlet.class);
            LOGGER.log(Level.INFO, "Mapping WebServlet class: " + clazz);

            if (!HttpServlet.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("Mapped WebServlet class " +
                                           "[" + clazz + "] must extends javax.servlet.http.HttpServlet class");
            }

            HELPER.setBeanFields(clazz);
            HELPER.setBeanMethods(clazz);
            webServlets.put(HELPER.getClassName(servlet, clazz), clazz);
        }

        if (webServlets.isEmpty()) {
            LOGGER.log(Level.INFO, "WebServlets were not mapped!");
        }
        checkWebBeanConstraint(annotations, "@WebServlet");
        checkAuthBeanConstraint(annotations, "@WebServlet");
        checkRequestPathConstraint(annotations, "@WebServlet");
        checkAsyncBeanConstraint(annotations, "@WebServlet");
    }

    private void initAnnotatedWebFilters(Reflections reflections) {
        Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(WebFilter.class);

        for (Class<?> clazz : annotations) {
            WebFilter filter = clazz.getAnnotation(WebFilter.class);
            LOGGER.log(Level.INFO, "Mapping WebFilter class: " + clazz);

            if (!Filter.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("Mapped WebFilter class [" + clazz + "] " +
                                           "must implement javax.servlet.Filter interface");
            }

            HELPER.setBeanFields(clazz);
            HELPER.setBeanMethods(clazz);
            webFilters.put(HELPER.getClassName(filter, clazz), clazz);
        }

        if (webFilters.isEmpty()) {
            LOGGER.log(Level.INFO, "WebFilters were not mapped!");
        }
        checkWebBeanConstraint(annotations, "@WebFilter");
        checkAuthBeanConstraint(annotations, "@WebFilter");
        checkRequestPathConstraint(annotations, "@WebFilter");
        checkAsyncBeanConstraint(annotations, "@WebFilter");
    }

    private void initAnnotatedWebListeners(Reflections reflections) {
        Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(WebListener.class);

        for (Class<?> clazz : annotations) {
            try {
                Object listenerObj = clazz.newInstance();
                if (ServletContextListener.class.isInstance(listenerObj)) {
                    LOGGER.log(Level.INFO, "Mapping ServletContextListener class [" + clazz + "]");
                    HELPER.setBeanFields(clazz);
                    HELPER.setBeanMethods(clazz);
                    contextListeners.add((ServletContextListener) listenerObj);

                } else if (HttpSessionListener.class.isInstance(listenerObj)) {
                    LOGGER.log(Level.INFO, "Mapping HttpSessionListener class [" + clazz + "]");
                    HELPER.setBeanFields(clazz);
                    HELPER.setBeanMethods(clazz);
                    sessionListeners.add((HttpSessionListener) listenerObj);

                } else if (ServletRequestListener.class.isInstance(listenerObj)) {
                    LOGGER.log(Level.INFO, "Mapping ServletRequestListener class [" + clazz + "]");
                    HELPER.setBeanFields(clazz);
                    HELPER.setBeanMethods(clazz);
                    requestListeners.add((ServletRequestListener) listenerObj);

                } else {
                    throw new RuntimeException("Mapped WebListener class [" + clazz + "] " +
                                               "must implement javax.servlet.ServletContextListener, " +
                                               "javax.servlet.http.HttpSessionListener or javax.servlet.ServletRequestListener interface");
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "WebListener class [" + clazz.getName() + "] " +
                                         "could not be instantiated!");
            }
        }

        if (contextListeners.isEmpty() && sessionListeners.isEmpty() && requestListeners.isEmpty()) {
            LOGGER.log(Level.INFO, "WebListeners were not mapped!");
        }
        checkWebBeanConstraint(annotations, "@WebListener");
        checkAuthBeanConstraint(annotations, "@WebListener");
        checkRequestPathConstraint(annotations, "@WebListener");
        checkAsyncBeanConstraint(annotations, "@WebListener");
    }

    private void initAnnotatedWebSecurities(Reflections reflections) {
        Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(WebSecurity.class);

        for (Class<?> clazz : annotations) {
            try {
                WebSecurity webSecurity = clazz.getAnnotation(WebSecurity.class);
                if (securityListeners.isEmpty()) {
                    LOGGER.log(Level.INFO, "Mapping WebSecurity class [" + clazz + "]");

                    if (!CsrfRequestListener.class.isAssignableFrom(clazz)) {
                        throw new RuntimeException("Mapped WebSecurity class [" + clazz + "] " +
                                                   "must implement CsrfRequestListener interface");
                    }
                    if (webSecurity.secretKey().length() != CsrfEncrypter.CYPHER_KEY_LENGTH) {
                        throw new RuntimeException("Mapped WebSecurity annotation for class [" + clazz + "] must " +
                                                   "have its secretKey value with [" + CsrfEncrypter.CYPHER_KEY_LENGTH +
                                                   "] characters");
                    }

                    HELPER.setBeanFields(clazz);
                    HELPER.setBeanMethods(clazz);
                    securityListeners.put(HELPER.getClassName(webSecurity, clazz), clazz);
                } else {
                    throw new RuntimeException("Only one WebSecurity can be declared! Skipping class " + clazz);
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "WebSecurity class [" + clazz.getName() + "] " +
                                         "could not be instantiated!");
            }
        }

        if (securityListeners.isEmpty()) {
            LOGGER.log(Level.INFO, "WebSecurities were not mapped!");
        }
        checkRequestPathConstraint(annotations, "@WebSecurity");
        checkAsyncBeanConstraint(annotations, "@WebSecurity");
    }

    private void checkWebBeanConstraint(Set<Class<?>> resources, String resourceName) {
        for (Class<?> clazz : resources) {
            for (Class<?> webClazz : webBeans.values()) {
                if (webClazz == clazz) {
                    LOGGER.log(Level.SEVERE, "@WebBean class [" + webClazz + "] cannot be annotated with " +
                                             resourceName);
                }
            }
        }
    }

    private void checkAuthBeanConstraint(Set<Class<?>> resources, String resourceName) {
        for (Class<?> clazz : resources) {
            for (Class<?> authClazz : authBeans.values()) {
                if (authClazz == clazz) {
                    LOGGER.log(Level.SEVERE, "@AuthBean class [" + authClazz + "] cannot be annotated with " +
                                             resourceName);
                }
            }
        }
    }

    private void checkRequestPathConstraint(Set<Class<?>> resources, String resourceName) {
        for (Class<?> clazz : resources) {
            for (Class<?> pathClazz : requestPaths.values()) {
                if (pathClazz == clazz) {
                    LOGGER.log(Level.SEVERE, "@RequestPath class [" + pathClazz + "] cannot be annotated with " +
                            resourceName);
                }
            }
        }
    }

    private void checkAsyncBeanConstraint(Set<Class<?>> resources, String resourceName) {
        for (Class<?> clazz : resources) {
            for (Class<?> asyncClazz : asyncBeans.values()) {
                if (asyncClazz == clazz) {
                    LOGGER.log(Level.SEVERE, "@AsyncBean class [" + asyncClazz + "] cannot be annotated with " +
                            resourceName);
                }
            }
        }
    }

    private String matchUrlPattern(String path) {
        for (UrlPattern urlPattern : CONFIG.getContent().getUrlPatterns()) {
            if (urlPattern.getUrl().equals(path)) {
                return urlPattern.getUrl();
            }
        }

        for (UrlPattern urlPattern : CONFIG.getContent().getUrlPatterns()) {
            if (urlPattern.getUrl().contains("/*")) {
                String url = urlPattern.getUrl().replace("/*", "");
                if (path.equals(url)) {
                    return url;
                }
            }
        }
        return path;
    }

    public String getForwardPath(String path) {
        if (path != null) {
            return forwardPaths.get(path);
        }
        return path;
    }

    AnnotatedAction getAnnotatedAction(String id) {
        if (id != null) {
            return annotatedActions.get(id);
        }
        return null;
    }

    List<AnnotatedFunction> getAnnotatedFunctions(String path) {
        if (path != null) {
            List<AnnotatedFunction> functions = annotatedFunctions.get(path);
            return functions != null ? functions : Collections.EMPTY_LIST;
        }
        return Collections.EMPTY_LIST;
    }

    private String getCleanPath(String path) {
        Matcher matcher = PATH_BEAN_ALL_PATTERN.matcher(path);
        if (matcher.find()) {
            path = matcher.group(1);
        }
        return path;
    }

    private void initForwardPaths(ServletContext servletContext) {
        lookupInResourcePath(servletContext, Constants.PATH_SEPARATOR);
        overrideForwardPaths();
    }

    private void overrideForwardPaths() {
        for (UrlPattern urlPattern : CONFIG.getContent().getUrlPatterns()) {

            if (StringUtils.isNotBlank(urlPattern.getJsp())) {
                String prevJsp = forwardPaths.put(urlPattern.getUrl(), urlPattern.getJsp());

                if (prevJsp != null) {
                    LOGGER.log(Level.INFO, "Overriding path mapping [" + urlPattern.getUrl() + "] from [" + prevJsp + "] " +
                            "to [" + urlPattern.getJsp() + "]");
                } else {
                    LOGGER.log(Level.INFO, "Mapping path  [" + urlPattern.getUrl() + "] to [" + urlPattern.getJsp() + "]");
                }
            }
        }
    }

    private void lookupInResourcePath(ServletContext servletContext, String path) {
        Set<String> resources = servletContext.getResourcePaths(path);
        if (resources != null) {
            for (String res : resources) {
                if (res.endsWith(".jsp") || res.endsWith(".jspf") || res.endsWith(".html")) {
                    String[] bars = res.split(Constants.PATH_SEPARATOR);
                    if (res.endsWith(".jspf")) {

                        // Save the entire resource path to capture it later when reading JSP include tags
                        forwardPaths.put(res, res);
                    } else {
                        forwardPaths.put(Constants.PATH_SEPARATOR + bars[bars.length -1].replace(".jsp", "").replace(".html", ""), res);
                    }
                } else {
                    lookupInResourcePath(servletContext, res);
                }
            }
        }
    }

    private void checkWebXmlPath(ServletContext servletContext) {
        try {
            URL webXml = servletContext.getResource("/WEB-INF/web.xml");
            if (webXml != null) {
                throw new RuntimeException("JSmart framework is not compatible with [/WEB-INF/web.xml] file. " +
                        "Please remove the web.xml and compile your project with [failOnMissingWebXml=false]");
            }
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.WARNING, "/WEB-INF/web.xml malformed Url: " + ex.getMessage());
        }
    }

    private void initJndiMapping() {
        try {
            String lookupName = CONFIG.getContent().getEjbLookup();
            initialContext = new InitialContext();

            // For glassfish implementation
            NamingEnumeration<Binding> bindList = initialContext.listBindings("");
            while (bindList.hasMore()) {
                Binding bind = bindList.next();
                if (bind != null && ("java:" + lookupName).equals(bind.getName()) && bind.getObject() instanceof Context) {
                    lookupInContext((Context) bind.getObject(), "java:" + lookupName);
                }
            }

            // For Jboss implementation
            if (jndiMapping.isEmpty()) {
                lookupInContext((Context) initialContext.lookup("java:" + lookupName), "java:" + lookupName);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "JNDI for EJB mapping could not be initialized: " + ex.getMessage());
        }
    }

    private void lookupInContext(Context context, String prefix) {
        try {
            prefix += "/";
            NamingEnumeration<Binding> bindList = context.listBindings("");
            while (bindList.hasMore()) {
                Binding bind = bindList.next();
                if (bind != null) {
                    if (bind.getObject() instanceof Context) {
                        lookupInContext((Context) bind.getObject(), prefix + bind.getName());
                    }
                    String[] binds = bind.getName().split("!");
                    if (binds.length > 1) {
                        try {
                            jndiMapping.put(Class.forName(binds[1]), prefix + binds[0]);
                        } catch (Throwable ex) {
                            LOGGER.log(Level.WARNING, "Class could not be found for EJB mapping: " + ex.getMessage());
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            LOGGER.log(Level.WARNING, "Bindings could not be found for EJB context: " + ex.getMessage());
        }
    }

    private void initJspPageBeans(ServletContext context) {
        for (UrlPattern urlPattern : CONFIG.getContent().getUrlPatterns()) {

            String path = getCleanPath(urlPattern.getUrl());
            JspPageBean jspPageBean = new JspPageBean();

            readJspPageResource(context, path, jspPageBean);

            // Included the mapped bean containing function into jspPageBeans
            // so they can be initialized properly
            for(AnnotatedFunction annotatedFunction : getAnnotatedFunctions(path)) {
                jspPageBean.addBeanName(annotatedFunction.getClassName());
            }
            jspPageBeans.put(path, jspPageBean);
        }
    }

    private void readJspPageResource(ServletContext context, String path, JspPageBean jspPageBean) {
        InputStream is = context.getResourceAsStream(getForwardPath(path));

        if (is != null) {
            Scanner fileScanner = new Scanner(is);
            Set<String> includes = new LinkedHashSet<>();

            try {
                String lineScan;
                while ((lineScan = fileScanner.findWithinHorizon(HANDLER_EL_PATTERN, 0)) != null) {

                    boolean hasInclude = false;
                    Matcher matcher = INCLUDE_PATTERN.matcher(lineScan);

                    while (matcher.find()) {
                        hasInclude = true;
                        includes.add(matcher.group(1));
                    }

                    if (hasInclude) {
                        continue;
                    }

                    matcher = ExpressionHandler.EL_PATTERN.matcher(lineScan);
                    while (matcher.find()) {
                        for (String name : matcher.group(1).split(Constants.EL_SEPARATOR)) {
                            if (webBeans.containsKey(name.trim())) {
                                jspPageBean.addBeanName(name.trim());
                            }
                        }
                    }

                    matcher = ExpressionHandler.ID_PATTERN.matcher(lineScan);
                    while (matcher.find()) {
                        AnnotatedAction annotatedAction = getAnnotatedAction(matcher.group(1));
                        if (annotatedAction != null) {
                            jspPageBean.addBeanName(annotatedAction.getClassName());
                        }
                    }
                }
            } finally {
                fileScanner.close();
            }

            // Read include page resources
            for (String include : includes) {
                String includeOwner = getForwardPath(path);
                includeOwner = includeOwner.substring(0, includeOwner.lastIndexOf(Constants.PATH_SEPARATOR) + 1);

                include = getRelativeIncludePath(includeOwner, include);
                readJspPageResource(context, include, jspPageBean);
            }
        }
    }

    private String getRelativeIncludePath(String includeOwner, String include) {
        int index = 0;
        int pathSeparators = 0;

        while (index != -1) {
            index = include.indexOf(Constants.PREVIOUS_PATH, index);
            if (index != -1) {
                pathSeparators++;
                index += Constants.PREVIOUS_PATH.length();
            }
        }
        if (pathSeparators != 0) {
            String[] ownerPath = includeOwner.split(Constants.PATH_SEPARATOR);
            if (ownerPath.length > pathSeparators) {
                includeOwner = includeOwner.substring(0, includeOwner.lastIndexOf(ownerPath[ownerPath.length - pathSeparators]));
            } else {
                includeOwner = Constants.PATH_SEPARATOR;
            }
        }
        if (include.startsWith(Constants.PATH_SEPARATOR)) {
            include = include.replaceFirst(Constants.PATH_SEPARATOR, "");
        }
        return includeOwner + include.replace(Constants.PREVIOUS_PATH, "");
    }

    private class JspPageBean {

        private Set<String> beanNames;

        public JspPageBean() {
            this.beanNames = new LinkedHashSet<>();
        }

        public Set<String> getBeanNames() {
            return beanNames;
        }

        public void addBeanName(String beanName) {
            this.beanNames.add(beanName);
        }
    }

    public static class AnnotatedFunction {

        private Function function;

        private Method method;

        private String className;

        private List<Arg> arguments;

        private String beanMethod;

        private String beforeSend;

        private String onSuccess;

        private String onComplete;

        private String onError;

        private String update;

        public AnnotatedFunction(Method method, String className, List<Arg> arguments) {
            this.method = method;
            this.className = className;
            this.function = method.getAnnotation(Function.class);
            this.beanMethod = String.format(EL_PATTERN_FORMAT, className, method.getName());
            this.beforeSend = StringUtils.join(function.beforeSend(), ";");
            this.onSuccess = StringUtils.join(function.onSuccess(), ";");
            this.onComplete = StringUtils.join(function.onComplete(), ";");
            this.onError = StringUtils.join(function.onError(), ";");
            this.update = StringUtils.join(function.update(), ",");
            this.arguments = arguments;
        }

        public Function getFunction() {
            return function;
        }

        public String getFunctionName() {
            if (StringUtils.isBlank(function.name())) {
                return method.getName();
            }
            return function.name();
        }

        public List<Arg> getArguments() {
            return arguments;
        }

        public Class<?> getArgumentType(int index) {
            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length > index) {
                return parameters[index];
            }
            return null;
        }

        public String getClassName() {
            return className;
        }

        public String getBeanMethod() {
            return beanMethod;
        }

        public String getBeforeSend() {
            return beforeSend;
        }

        public String getOnSuccess() {
            return onSuccess;
        }

        public String getOnComplete() {
            return onComplete;
        }

        public String getOnError() {
            return onError;
        }

        public String getUpdate() {
            return update;
        }
    }

    public static class AnnotatedAction {

        private Action action;

        private Method method;

        private List<Arg> arguments;

        private String className;

        private String beanMethod;

        private String beforeSend;

        private String onSuccess;

        private String onComplete;

        private String onError;

        private String update;

        public AnnotatedAction(Method method, String className, List<Arg> arguments) {
            this.method = method;
            this.className = className;
            this.action = method.getAnnotation(Action.class);
            this.beanMethod = String.format(EL_PATTERN_FORMAT, className, method.getName());
            this.beforeSend = StringUtils.join(action.beforeSend(), ";");
            this.onSuccess = StringUtils.join(action.onSuccess(), ";");
            this.onComplete = StringUtils.join(action.onComplete(), ";");
            this.onError = StringUtils.join(action.onError(), ";");
            this.update = StringUtils.join(action.update(), ",");
            this.arguments = arguments;
        }

        public Action getAction() {
            return action;
        }

        public List<Arg> getArguments() {
            return arguments;
        }

        public String getClassName() {
            return className;
        }

        public String getBeanMethod() {
            return beanMethod;
        }

        public String getBeforeSend() {
            return beforeSend;
        }

        public String getOnSuccess() {
            return onSuccess;
        }

        public String getOnComplete() {
            return onComplete;
        }

        public String getOnError() {
            return onError;
        }

        public String getUpdate() {
            return update;
        }
    }
}
