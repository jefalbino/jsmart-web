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
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jsmart5.framework.annotation.*;
import com.jsmart5.framework.config.Constants;
import com.jsmart5.framework.listener.WebAsyncListener;
import com.jsmart5.framework.listener.WebContextListener;
import com.jsmart5.framework.listener.WebSessionListener;
import com.jsmart5.framework.util.WebUtils;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.jsmart5.framework.config.UrlPattern;

import static com.jsmart5.framework.config.Config.*;
import static com.jsmart5.framework.config.Constants.*;
import static com.jsmart5.framework.manager.ExpressionHandler.*;

public enum BeanHandler {

    HANDLER();

    private static final Logger LOGGER = Logger.getLogger(BeanHandler.class.getPackage().getName());

    private static final Pattern INCLUDE_PATTERN = Pattern.compile("<%@*.include.*file=\"(.*)\".*%>");

    private static final Pattern HANDLER_EL_PATTERN = Pattern.compile(EL_PATTERN.pattern() + "|" + INCLUDE_PATTERN.pattern());

    private static final Pattern SPRING_VALUE_PATTERN = Pattern.compile("[\\$,\\{,\\}]*");

    private static final Pattern SET_METHOD_PATTERN = Pattern.compile("^set(.*)");

    private static final Pattern PATH_BEAN_PARAM_PATTERN = Pattern.compile("\\{([^/]*)\\}");

    private static final Pattern PATH_BEAN_ALL_PATTERN = Pattern.compile("(.*)/\\*");

    Map<String, Class<?>> webBeans;

    Map<String, Class<?>> authBeans;

    Map<String, Class<?>> asyncBeans;

    Map<String, Class<?>> pathBeans;

    Map<String, Class<?>> smartServlets;

    Map<String, Class<?>> smartFilters;

    Set<WebContextListener> contextListeners;

    Set<WebSessionListener> sessionListeners;

    private Map<String, String> forwardPaths;

    private InitialContext initialContext;

    private ApplicationContext springContext;

    private Map<Class<?>, String> jndiMapping = new ConcurrentHashMap<Class<?>, String>();

    private Map<Class<?>, Field[]> mappedBeanFields = new ConcurrentHashMap<Class<?>, Field[]>();

    private Map<Class<?>, Method[]> mappedBeanMethods = new ConcurrentHashMap<Class<?>, Method[]>();

    private Map<String, JspPageBean> jspPageBeans = new ConcurrentHashMap<String, JspPageBean>();

    void init(ServletContext context) {
        checkWebXmlPath(context);
        initJndiMapping();
        initAnnotatedBeans(context);
        initForwardPaths(context);
        initJspPageBeans(context);
    }

    void destroy(ServletContext context) {
        try {
            finalizeWebBeans(context);
            authBeans.clear();
            webBeans.clear();
            asyncBeans.clear();
            pathBeans.clear();
            smartServlets.clear();
            smartFilters.clear();
            contextListeners.clear();
            sessionListeners.clear();
            forwardPaths.clear();
            jspPageBeans.clear();
            jndiMapping.clear();
            initialContext = null;
            springContext = null;
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Failure to destroy SmartHandler: " + ex.getMessage());
        }
    }

    void setSpringContext(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    @SuppressWarnings("all")
    boolean executePreSubmit(Object bean, String action) {
        for (Method method : getBeanMethods(bean.getClass())) {
            if (method.isAnnotationPresent(PreSubmit.class)) {
                try {
                    String forAction = method.getAnnotation(PreSubmit.class).forAction();

                    if (action.equalsIgnoreCase(forAction)) {
                        Boolean result = (Boolean) method.invoke(bean, null);
                        return result != null && result;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }

    @SuppressWarnings("all")
    void executePostSubmit(Object bean, String action) {
        for (Method method : getBeanMethods(bean.getClass())) {
            if (method.isAnnotationPresent(PostSubmit.class)) {
                try {
                    String forAction = method.getAnnotation(PostSubmit.class).forAction();

                    if (action.equalsIgnoreCase(forAction)) {
                        method.invoke(bean, null);
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }
        }
    }

    @SuppressWarnings("all")
    void executePreDestroy(Object bean) {
        for (Method method : getBeanMethods(bean.getClass())) {
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

    @SuppressWarnings("all")
    void executePostConstruct(Object bean) {
        for (Method method : getBeanMethods(bean.getClass())) {
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

    void executePostPreset(String name, Object bean, Map<String, String> expressions) {
        try {
            if (expressions != null) {
                for (Field field : getBeanFields(bean.getClass())) {

                    if (field.isAnnotationPresent(PostPreset.class)) {
                        for (Entry<String, String> expr : expressions.entrySet()) {

                            Matcher elMatcher = EL_PATTERN.matcher(expr.getValue());
                            if (elMatcher.find() && elMatcher.group(1).contains(name + "." + field.getName())) {

                                Matcher tagMatcher = TagHandler.J_TAG_PATTERN.matcher(expr.getKey());
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
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Execute PostPreset on WebBean " + bean + " failure: " + ex.getMessage());
        }
    }

    boolean containsUnescapeMethod(String[] names) {
        if (names != null && names.length > 1) {
            Class<?> clazz = webBeans.get(names[0]);
            if (clazz != null) {
                for (Method method : getBeanMethods(clazz)) {
                    Matcher matcher = SET_METHOD_PATTERN.matcher(method.getName());

                    if (matcher.find() && names[1].equalsIgnoreCase(matcher.group(1))) {
                        return method.isAnnotationPresent(Unescape.class);
                    }
                }
            }
        }
        return false;
    }

    Map<String, String> getRequestExpressions() {
        return EXPRESSIONS.getRequestExpressions();
    }

    String handleRequestExpressions(Map<String, String> expressions) throws ServletException, IOException {
        String submitParam = null;
        String submitExpr = null;

        for (Entry<String, String> expr : expressions.entrySet()) {
            Matcher matcher = TagHandler.J_TAG_PATTERN.matcher(expr.getKey());
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

            PageScope pageScope = new PageScope(path);

            for (String name : jspPageBean.getBeanNames()) {
                instantiateBean(name, expressions, pageScope);
            }

            // Include into session the path with respective page scoped bean names
            if (!pageScope.getNames().isEmpty()) {
                HttpSession session = WebContext.getSession();
                synchronized (session) {
                    session.setAttribute(path, pageScope);
                }
            }
        }
    }

    private Object instantiateBean(String name, Map<String, String> expressions, PageScope pageScope) throws Exception {
        Object bean = null;
        ServletContext context = WebContext.getApplication();
        HttpSession session = WebContext.getSession();
        HttpServletRequest request = WebContext.getRequest();

        if (request.getAttribute(name) != null) {
            bean = request.getAttribute(name);
            executeInjection(bean, pageScope);
            return bean;
        }

        synchronized (session) {
            if (session.getAttribute(name) != null) {
                bean = session.getAttribute(name);
                executeInjection(bean, pageScope);
                return bean;
            }
        }

        if (context.getAttribute(name) != null) {
            bean = context.getAttribute(name);
            executeInjection(bean, pageScope);
            return bean;
        }

        if (webBeans.containsKey(name)) {
            Class<?> clazz = webBeans.get(name);
            bean = clazz.newInstance();

            WebBean webBean = clazz.getAnnotation(WebBean.class);
            if (webBean.scope().equals(ScopeType.REQUEST_SCOPE)) {
                request.setAttribute(name, bean);

            } else if (webBean.scope().equals(ScopeType.PAGE_SCOPE)) {
                synchronized (session) {
                    pageScope.addName(name);
                    session.setAttribute(name, bean);
                }

            } else if (webBean.scope().equals(ScopeType.SESSION_SCOPE)) {
                synchronized (session) {
                    session.setAttribute(name, bean);
                }

            } else if (webBean.scope().equals(ScopeType.APPLICATION_SCOPE)) {
                context.setAttribute(name, bean);

            } else {
                return null;
            }

            executeInjection(bean, pageScope);
            executePostPreset(name, bean, expressions);
            executePostConstruct(bean);
        }
        return bean;
    }

    Object instantiatePathBean(String path) throws Exception {
        Class<?> clazz = pathBeans.get(path);
        if (clazz != null) {
            Object bean = clazz.newInstance();
            executeInjection(bean);
            return bean;
        }
        return null;
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

    void executeInjection(Object bean) {
        executeInjection(bean, null);
    }

    private String getClassName(String name) {
        return name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toLowerCase());
    }

    private String getClassName(WebBean webBean, Class<?> beanClass) {
        if (webBean.name().trim().isEmpty()) {
            String beanName = beanClass.getSimpleName();
            return getClassName(beanName);
        }
        return webBean.name();
    }

    private String getClassName(AuthenticateBean authBean, Class<?> authClass) {
        if (authBean.name().trim().isEmpty()) {
            String beanName = authClass.getSimpleName();
            return getClassName(beanName);
        }
        return authBean.name();
    }

    private String getClassName(com.jsmart5.framework.annotation.SmartServlet servlet, Class<?> servletClass) {
        if (servlet.name() == null || servlet.name().trim().isEmpty()) {
            String servletName = servletClass.getSimpleName();
            return getClassName(servletName);
        }
        return servlet.name();
    }

    private String getClassName(SmartFilter filter, Class<?> filterClass) {
        if (filter.name() == null || filter.name().trim().isEmpty()) {
            String filterName = filterClass.getSimpleName();
            return getClassName(filterName);
        }
        return filter.name();
    }

    private void executeInjection(Object bean, PageScope pageScope) {
        try {
            HttpSession session = WebContext.getSession();
            HttpServletRequest request = WebContext.getRequest();

            for (Field field : getBeanFields(bean.getClass())) {
                if (field.isAnnotationPresent(Inject.class)) {

                    WebBean sb = field.getType().getAnnotation(WebBean.class);
                    if (sb != null) {
                        field.setAccessible(true);
                        field.set(bean, instantiateBean(getClassName(sb, field.getType()), null, pageScope));
                        continue;
                    }

                    AuthenticateBean ab = field.getType().getAnnotation(AuthenticateBean.class);
                    if (ab != null) {
                        field.setAccessible(true);
                        field.set(bean, instantiateAuthBean(getClassName(ab, field.getType()), session));
                        continue;
                    }
                }

                // Inject URL Parameters
                if (field.isAnnotationPresent(QueryParam.class)) {
                    QueryParam queryParam = field.getAnnotation(QueryParam.class);
                    String paramValue = request.getParameter(queryParam.name());

                    if (paramValue != null) {
                        field.setAccessible(true);
                        field.set(bean, EXPRESSIONS.decodeUrl(paramValue));
                    }
                    continue;
                }

                // Inject dependencies
                if (field.getAnnotations().length > 0) {

                    if (initialContext != null && jndiMapping.containsKey(field.getType())) {
                        field.setAccessible(true);
                        field.set(bean, initialContext.lookup(jndiMapping.get(field.getType())));
                        continue;
                    }

                    if (springContext != null) {
                        if (springContext.containsBean(getClassName(field.getType().getSimpleName()))) {
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
            }
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Injection on WebBean " + bean + " failure: " + ex.getMessage());
        }
    }

    void finalizePathBean(Object bean, HttpServletRequest request) {
        if (bean != null) {
            finalizeInjection(bean, request);
            bean = null;
        }
    }

    void finalizeAsyncBean(Object bean, HttpServletRequest request) {
        if (bean != null) {
            finalizeInjection(bean, request);
            bean = null;
        }
    }

    void finalizeWebBeans(ServletContext servletContext) {
        List<String> names = Collections.list(servletContext.getAttributeNames());
        for (String name : names) {
            Object bean = servletContext.getAttribute(name);
            if (bean != null) {

                if (bean.getClass().isAnnotationPresent(WebBean.class)) {
                    finalizeWebBean(bean, servletContext);
                }
            }
        }
    }

    private void finalizeWebBean(Object bean, ServletContext servletContext) {
        if (bean != null) {
            executePreDestroy(bean);
            finalizeInjection(bean, servletContext);

            WebBean webBean = bean.getClass().getAnnotation(WebBean.class);
            servletContext.removeAttribute(getClassName(webBean, bean.getClass()));
            bean = null;
        }
    }

    void finalizeBeans(HttpSession session) {
        synchronized (session) {
            List<String> names = Collections.list(session.getAttributeNames());

            for (String name : names) {
                Object bean = session.getAttribute(name);
                if (bean != null) {

                    if (bean.getClass().isAnnotationPresent(WebBean.class)) {
                        finalizeWebBean(bean, session);

                    } else if (bean.getClass().isAnnotationPresent(AuthenticateBean.class)) {
                        finalizeAuthBean(bean, session);
                    }
                }
            }
        }
    }

    void finalizeBeans(String path, HttpSession session) {
        // Do not finalize beans case path was meant to be processed by
        // PathBean or AsyncBean, otherwise (WebBean) clear the
        // page scope beans
        if (pathBeans.containsKey(path) || asyncBeans.containsKey(path)) {
            return;
        }

        synchronized (session) {
            List<String> names = Collections.list(session.getAttributeNames());

            for (String attrname : names) {
                Object object = session.getAttribute(attrname);

                if (!attrname.equals(path) && object instanceof PageScope) {

                    for (String name : ((PageScope) object).getNames()) {
                        finalizeWebBean(session.getAttribute(name), session);
                    }
                    session.removeAttribute(attrname);
                }
            }
        }
    }

    void finalizeWebBean(String path, HttpSession session) {
        synchronized (session) {
            Object pageScope = session.getAttribute(path);
            if (pageScope instanceof PageScope) {

                for (String name : ((PageScope) pageScope).getNames()) {
                    finalizeWebBean(session.getAttribute(name), session);
                }
            }
            session.removeAttribute(path);
        }
    }

    private void finalizeWebBean(Object bean, HttpSession session) {
        if (bean != null) {
            executePreDestroy(bean);
            finalizeInjection(bean, session);

            WebBean webBean = bean.getClass().getAnnotation(WebBean.class);
            session.removeAttribute(getClassName(webBean, bean.getClass()));
            bean = null;
        }
    }

    public void finalizeWebBeans(HttpServletRequest request) {
        List<String> names = Collections.list(request.getAttributeNames());
        for (String name : names) {

            if (webBeans.containsKey(name)) {
                Object webBean = request.getAttribute(name);

                if (webBean != null && webBean.getClass().isAnnotationPresent(WebBean.class)) {
                    finalizeWebBean(webBean, request);
                }
            }
        }
    }

    private void finalizeWebBean(Object bean, HttpServletRequest request) {
        if (bean != null) {
            executePreDestroy(bean);
            finalizeInjection(bean, request);

            WebBean webBean = bean.getClass().getAnnotation(WebBean.class);
            request.removeAttribute(getClassName(webBean, bean.getClass()));
            bean = null;
        }
    }

    private void finalizeInjection(Object bean, Object servletObject) {
        try {
            for (Field field : getBeanFields(bean.getClass())) {
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

                    if (field.getType().isAnnotationPresent(AuthenticateBean.class)) {
                        field.setAccessible(true);
                        field.set(bean, null);
                        continue;
                    }
                }

                if (field.getAnnotations().length > 0) {
                    field.setAccessible(true);
                    field.set(bean, null);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Finalize injection on WebBean " + bean + " failure: " + ex.getMessage());
        }
    }

    void instantiateAuthBean(HttpSession session) {
        for (String name : authBeans.keySet()) {
            instantiateAuthBean(name, session);

            // We must have only one authentication bean mapped
            break;
        }
    }

    private Object instantiateAuthBean(String name, HttpSession session) {
        synchronized (session) {
            Object bean = session.getAttribute(name);

            if (bean == null) {
                try {
                    bean = authBeans.get(name).newInstance();
                    for (Field field : getBeanFields(bean.getClass())) {

                        if (field.getAnnotations().length > 0) {

                            if (initialContext != null && jndiMapping.containsKey(field.getType())) {
                                field.setAccessible(true);
                                field.set(bean, initialContext.lookup(jndiMapping.get(field.getType())));
                                continue;
                            }

                            if (springContext != null) {
                                if (springContext.containsBean(getClassName(field.getType().getSimpleName()))) {
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
                    }

                    executePostConstruct(bean);
                    session.setAttribute(name, bean);
                } catch (Exception ex) {
                    LOGGER.log(Level.INFO, "Injection on AuthenticationBean " + bean + " failure: " + ex.getMessage());
                }
            }
            return bean;
        }
    }

    private void finalizeAuthBean(Object bean, HttpSession session) {
        executePreDestroy(bean);

        try {
            for (Field field : getBeanFields(bean.getClass())) {
                if (field.getAnnotations().length > 0) {
                    field.setAccessible(true);
                    field.set(bean, null);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Finalize injection on AuthenticationBean " + bean + " failure: " + ex.getMessage());
        }

        AuthenticateBean authBean = bean.getClass().getAnnotation(AuthenticateBean.class);
        session.removeAttribute(getClassName(authBean, bean.getClass()));
        bean = null;
    }

    String checkAuthentication(String path) throws ServletException {

        if (authBeans.isEmpty() && !CONFIG.getContent().getSecureUrls().isEmpty()) {
            throw new ServletException("Not found AuthenticationBean mapped in your system. Once your system has secure urls, please use @AuthenticateBean!");
        }

        boolean authenticated = true;
        AuthenticateBean authBean = null;

        HttpSession session = WebContext.getSession();
        synchronized (session) {

            for (String name : authBeans.keySet()) {
                authBean = authBeans.get(name).getAnnotation(AuthenticateBean.class);
                Object bean = session.getAttribute(name);

                if (bean != null) {
                    boolean foundField = false;

                    for (Field field : getBeanFields(bean.getClass())) {

                        if (field.isAnnotationPresent(AuthenticateField.class)) {
                            try {
                                foundField = true;
                                field.setAccessible(true);
                                if (field.get(bean) == null) {
                                    authenticated = false;
                                    break;
                                }
                            } catch (Exception ex) {
                                throw new ServletException("AuthenticationField not accessible: " + ex.getMessage(), ex);
                            }
                        }
                    }

                    if (!foundField) {
                        throw new ServletException("None AuthenticateField found in AuthenticateBean!");
                    }
                }

                // We must have only one authentication bean mapped
                break;
            }
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

    @SuppressWarnings("all")
    Integer checkAuthorization(String path) {
        if (CONFIG.getContent().containsSecureUrl(path)) {

            Collection<String> userAccess = getUserAuthorizationAccess();

            AuthenticateBean authBean = null;
            for (String name : authBeans.keySet()) {
                authBean = authBeans.get(name).getAnnotation(AuthenticateBean.class);

                // We must have only one authentication bean mapped
                break;
            }

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

    @SuppressWarnings("unchecked")
    Collection<String> getUserAuthorizationAccess() {
        HttpServletRequest request = WebContext.getRequest();

        if (request.getAttribute(REQUEST_USER_ACCESS) == null) {

            Collection<String> userAccess = new HashSet<String>();

            HttpSession session = WebContext.getSession();
            synchronized (session) {

                for (String name : authBeans.keySet()) {
                    Object bean = session.getAttribute(name);

                    if (bean != null) {
                        for (Field field : getBeanFields(bean.getClass())) {

                            if (field.isAnnotationPresent(AuthorizeAccess.class)) {
                                try {
                                    field.setAccessible(true);
                                    Object object = field.get(bean);
                                    if (object != null) {
                                        userAccess.addAll((Collection<String>) object);
                                    }
                                } catch (Exception ex) {
                                    LOGGER.log(Level.INFO, "AuthorizeAccess mapped on WebBean [" + bean + "] could not be cast to Collection<String>: " + ex.getMessage());
                                }
                                break;
                            }
                        }
                    }

                    // We must have only one authentication bean mapped
                    break;
                }
            }
            request.setAttribute(REQUEST_USER_ACCESS, userAccess);
        }
        return (Collection<String>) request.getAttribute(REQUEST_USER_ACCESS);
    }

    boolean checkExecuteAuthorization(Object bean, String expression) {

        for (Method method : getBeanMethods(bean.getClass())) {

            ExecuteAccess execAccess = method.getAnnotation(ExecuteAccess.class);
            if (execAccess != null && execAccess.access().length > 0 && expression.contains(method.getName())) {

                Collection<String> userAccess = getUserAuthorizationAccess();
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

    private void initAnnotatedBeans(ServletContext context) {

        webBeans = new ConcurrentHashMap<String, Class<?>>();
        authBeans = new ConcurrentHashMap<String, Class<?>>();
        asyncBeans = new ConcurrentHashMap<String, Class<?>>();
        pathBeans = new ConcurrentHashMap<String, Class<?>>();
        smartServlets = new ConcurrentHashMap<String, Class<?>>();
        smartFilters = new ConcurrentHashMap<String, Class<?>>();
        contextListeners = new HashSet<WebContextListener>();
        sessionListeners = new HashSet<WebSessionListener>();

        if (CONFIG.getContent().getPackageScan() == null) {
            LOGGER.log(Level.SEVERE, "None [package-scan] tag was found on " + Constants.WEB_CONFIG_XML + " file! Skipping package scanning.");
            return;
        }

        Object[] packages = CONFIG.getContent().getPackageScan().split(",");
        Reflections reflections = new Reflections(packages);

        Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(WebBean.class);
        for (Class<?> clazz : annotations) {
            WebBean bean = clazz.getAnnotation(WebBean.class);
            LOGGER.log(Level.INFO, "Mapping WebBean class: " + clazz);

            if ((bean.scope() == ScopeType.PAGE_SCOPE || bean.scope() == ScopeType.SESSION_SCOPE)
                    && !Serializable.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("Mapped WebBean class [" + clazz + "] with scope [" + bean.scope() + "] " +
                        "must implement java.io.Serializable interface");
            }

            setBeanFields(clazz);
            setBeanMethods(clazz);

            String className = getClassName(bean, clazz);
            webBeans.put(className, clazz);
        }

        annotations = reflections.getTypesAnnotatedWith(AuthenticateBean.class);
        for (Class<?> clazz : annotations) {
            AuthenticateBean authBean = clazz.getAnnotation(AuthenticateBean.class);
            if (authBeans.isEmpty()) {
                LOGGER.log(Level.INFO, "Mapping AuthenticateBean class: " + clazz);

                if (!Serializable.class.isAssignableFrom(clazz)) {
                    throw new RuntimeException("Mapped AuthenticateBean class [" + clazz + "] must implement " +
                            "java.io.Serializable interface");
                }

                setBeanFields(clazz);
                setBeanMethods(clazz);
                String className = getClassName(authBean, clazz);
                authBeans.put(className, clazz);
                continue;
            } else {
                LOGGER.log(Level.SEVERE, "Only one AuthenticateBean must be declared! Skipping remained ones.");
            }
        }

        annotations = reflections.getTypesAnnotatedWith(PathBean.class);
        for (Class<?> clazz : annotations) {
            PathBean pathBean = clazz.getAnnotation(PathBean.class);
            LOGGER.log(Level.INFO, "Mapping PathBean class: " + clazz);

            if (!WebPathRequest.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("Mapped PathBean class [" + clazz + "] must extends " +
                        "com.jsmart5.framework.manager.WebPathRequest abstract class");
            }

            setBeanFields(clazz);
            setBeanMethods(clazz);

            String path = pathBean.path();

            Matcher matcher = PATH_BEAN_ALL_PATTERN.matcher(path);
            if (matcher.find()) {
                path = matcher.group(1);
            } else {
                matcher = PATH_BEAN_PARAM_PATTERN.matcher(path);
                if (matcher.find()) {
                    path = path.substring(0, matcher.start() -1);
                }
            }
            pathBeans.put(path, clazz);
        }

        annotations = reflections.getTypesAnnotatedWith(AsyncBean.class);
        for (Class<?> clazz : annotations) {
            AsyncBean asyncBean = clazz.getAnnotation(AsyncBean.class);
            LOGGER.log(Level.INFO, "Mapping AsyncBean class: " + clazz);

            if (!WebAsyncListener.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("Mapped AsyncBean class [" + clazz + "] must implement " +
                        "com.jsmart5.framework.listener.WebAsyncListener interface");
            }

            setBeanFields(clazz);
            setBeanMethods(clazz);

            String path = asyncBean.asyncPath();
            Matcher matcher = PATH_BEAN_ALL_PATTERN.matcher(path);
            if (matcher.find()) {
                path = matcher.group(1);
            }
            asyncBeans.put(path, clazz);
        }

        annotations = reflections.getTypesAnnotatedWith(SmartServlet.class);
        for (Class<?> clazz : annotations) {
            SmartServlet servlet = clazz.getAnnotation(SmartServlet.class);
            LOGGER.log(Level.INFO, "Mapping SmartServlet class: " + clazz);

            if (!HttpServlet.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("Mapped SmartServlet class [" + clazz + "] must extends " +
                        "javax.servlet.http.HttpServlet class");
            }

            setBeanFields(clazz);
            setBeanMethods(clazz);
            smartServlets.put(getClassName(servlet, clazz), clazz);
        }

        annotations = reflections.getTypesAnnotatedWith(SmartFilter.class);
        for (Class<?> clazz : annotations) {
            SmartFilter filter = clazz.getAnnotation(SmartFilter.class);
            LOGGER.log(Level.INFO, "Mapping SmartFilter class: " + clazz);

            if (!Filter.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("Mapped SmartFilter class [" + clazz + "] must implement " +
                        "javax.servlet.Filter interface");
            }

            setBeanFields(clazz);
            setBeanMethods(clazz);
            smartFilters.put(getClassName(filter, clazz), clazz);
        }

        annotations = reflections.getTypesAnnotatedWith(SmartListener.class);
        for (Class<?> clazz : annotations) {
            try {
                Object listenerObj = clazz.newInstance();
                if (WebContextListener.class.isInstance(listenerObj)) {
                    LOGGER.log(Level.INFO, "Mapping SmartListener class [" + clazz + "]");
                    setBeanFields(clazz);
                    setBeanMethods(clazz);
                    contextListeners.add((WebContextListener) listenerObj);

                } else if (WebSessionListener.class.isInstance(listenerObj)) {
                    LOGGER.log(Level.INFO, "Mapping SmartListener class [" + clazz + "]");
                    setBeanFields(clazz);
                    setBeanMethods(clazz);
                    sessionListeners.add((WebSessionListener) listenerObj);

                } else {
                    throw new RuntimeException("Mapped SmartListener class [" + clazz + "] must implement " +
                            "com.jsmart5.framework.listener.WebContextListener or " +
                            "com.jsmart5.framework.listener.WebSessionListener interface");
                }
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, "SmartListener class [" + clazz.getName() + "] could not be instantiated!");
            }
        }

        if (webBeans.isEmpty()) {
            LOGGER.log(Level.INFO, "WebBeans were not mapped!");
        }
        if (authBeans.isEmpty()) {
            LOGGER.log(Level.INFO, "AuthenticateBean was not mapped!");
        }
        if (pathBeans.isEmpty()) {
            LOGGER.log(Level.INFO, "PathBeans were not mapped!");
        }
        if (asyncBeans.isEmpty()) {
            LOGGER.log(Level.INFO, "AsyncBeans were not mapped!");
        }
        if (smartServlets.isEmpty()) {
            LOGGER.log(Level.INFO, "SmartServlets were not mapped!");
        }
        if (smartFilters.isEmpty()) {
            LOGGER.log(Level.INFO, "SmartFilters were not mapped!");
        }
        if (contextListeners.isEmpty() && sessionListeners.isEmpty()) {
            LOGGER.log(Level.INFO, "SmartListeners were not mapped!");
        }
    }

    public String getForwardPath(String path) {
        if (path != null) {
            return forwardPaths.get(path);
        }
        return path;
    }

    private void initForwardPaths(ServletContext servletContext) {
        forwardPaths = new HashMap<String, String>();
        lookupInResourcePath(servletContext, PATH_SEPARATOR);
        overrideForwardPaths();
    }

    private void overrideForwardPaths() {
        for (UrlPattern urlPattern : CONFIG.getContent().getUrlPatterns()) {

            if (urlPattern.getJsp() != null && !urlPattern.getJsp().trim().isEmpty()) {
                String prevJsp = forwardPaths.put(urlPattern.getUrl(), urlPattern.getJsp());

                if (prevJsp != null) {
                    LOGGER.log(Level.INFO, "Overriding path mapping [" + urlPattern.getUrl() + "] from [" + prevJsp + "] to [" + urlPattern.getJsp() + "]");
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
                    String[] bars = res.split(PATH_SEPARATOR);
                    if (res.endsWith(".jspf")) {

                        // Save the entire resource path to capture it later when reading JSP include tags
                        forwardPaths.put(res, res);
                    } else {
                        forwardPaths.put(PATH_SEPARATOR + bars[bars.length -1].replace(".jsp", "").replace(".html", ""), res);
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
                throw new RuntimeException("JSmart5 framework is not compatible with [/WEB-INF/web.xml] file. Please remove the web.xml and compile your project with [failOnMissingWebXml=false]");
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

    private Field[] getBeanFields(Class<?> clazz) {
        if (!mappedBeanFields.containsKey(clazz)) {
            mappedBeanFields.put(clazz, clazz.getDeclaredFields());
        }
        return mappedBeanFields.get(clazz);
    }
   
    private void setBeanFields(Class<?> clazz) {
        if (!mappedBeanFields.containsKey(clazz)) {
            mappedBeanFields.put(clazz, clazz.getDeclaredFields());
        }
    }

    private Method[] getBeanMethods(Class<?> clazz) {
        if (!mappedBeanMethods.containsKey(clazz)) {
            mappedBeanMethods.put(clazz, clazz.getMethods());
        }
        return mappedBeanMethods.get(clazz);
    }

    private void setBeanMethods(Class<?> clazz) {
        if (!mappedBeanMethods.containsKey(clazz)) {
            mappedBeanMethods.put(clazz, clazz.getMethods());
        }
    }

    private void initJspPageBeans(ServletContext context) {
        for (UrlPattern urlPattern : CONFIG.getContent().getUrlPatterns()) {
            JspPageBean jspPageBean = new JspPageBean();
            readJspPageResource(context, urlPattern.getUrl(), jspPageBean);
            jspPageBeans.put(urlPattern.getUrl(), jspPageBean);
        }
    }

    private void readJspPageResource(ServletContext context, String path, JspPageBean jspPageBean) {
        InputStream is = context.getResourceAsStream(getForwardPath(path));

        if (is != null) {
            Scanner fileScanner = new Scanner(is);
            Set<String> includes = new LinkedHashSet<String>();

            try {
                String lineScan = null;
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

                    matcher = EL_PATTERN.matcher(lineScan);
                    while (matcher.find()) {
                        for (String name : matcher.group(1).split(EL_SEPARATOR)) {
                            if (webBeans.containsKey(name.trim())) {
                                jspPageBean.addBeanName(name.trim());
                            }
                        }
                    }
                }
            } finally {
                fileScanner.close();
            }

            // Read include page resources
            for (String include : includes) {
                String includeOwner = getForwardPath(path);
                include = includeOwner.substring(0, includeOwner.lastIndexOf(PATH_SEPARATOR) + 1) + include;
                readJspPageResource(context, include, jspPageBean);
            }
        }
    }

    private class JspPageBean {

        private Set<String> beanNames;

        public JspPageBean() {
            this.beanNames = new LinkedHashSet<String>();
        }

        public Set<String> getBeanNames() {
            return beanNames;
        }

        public void addBeanName(String beanName) {
            this.beanNames.add(beanName);
        }
    }

}
