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

import com.jsmartframework.web.annotation.AuthAccess;
import com.jsmartframework.web.annotation.AuthBean;
import com.jsmartframework.web.annotation.AuthField;
import com.jsmartframework.web.annotation.AuthMethod;
import com.jsmartframework.web.annotation.ExecuteAccess;
import com.jsmartframework.web.annotation.PostSubmit;
import com.jsmartframework.web.annotation.PreSet;
import com.jsmartframework.web.annotation.PreSubmit;
import com.jsmartframework.web.annotation.Unescape;
import com.jsmartframework.web.annotation.WebBean;
import com.jsmartframework.web.annotation.WebFilter;
import com.jsmartframework.web.annotation.WebSecurity;
import com.jsmartframework.web.annotation.WebServlet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public enum BeanHelper {

    HELPER();

    private static final Logger LOGGER = Logger.getLogger(BeanHelper.class.getPackage().getName());

    private static final Pattern SET_METHOD_PATTERN = Pattern.compile("^set(.*)");

    private Map<Class<?>, Field[]> beanFields = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> beanMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Field[]> preSetFields = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> postConstructMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> preDestroyMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> preSubmitMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> postSubmitMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, String[]> unescapeMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> executeAccessMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Field[]> authFields = new ConcurrentHashMap<>();

    private Map<Class<?>, Field[]> authAccess = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> authMethods = new ConcurrentHashMap<>();

    String getClassName(String name) {
        return name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toLowerCase());
    }

    String getClassName(WebBean webBean, Class<?> beanClass) {
        if (webBean.name().trim().isEmpty()) {
            String beanName = beanClass.getSimpleName();
            return getClassName(beanName);
        }
        return webBean.name();
    }

    String getClassName(AuthBean authBean, Class<?> authClass) {
        if (authBean.name().trim().isEmpty()) {
            String beanName = authClass.getSimpleName();
            return getClassName(beanName);
        }
        return authBean.name();
    }

    String getClassName(WebServlet servlet, Class<?> servletClass) {
        if (servlet.name() == null || servlet.name().trim().isEmpty()) {
            String servletName = servletClass.getSimpleName();
            return getClassName(servletName);
        }
        return servlet.name();
    }

    String getClassName(WebFilter filter, Class<?> filterClass) {
        if (filter.name() == null || filter.name().trim().isEmpty()) {
            String filterName = filterClass.getSimpleName();
            return getClassName(filterName);
        }
        return filter.name();
    }

    String getClassName(WebSecurity security, Class<?> securityClass) {
        String securityName = securityClass.getSimpleName();
        return getClassName(securityName);
    }

    Field[] getBeanFields(Class<?> clazz) {
        if (!beanFields.containsKey(clazz)) {
            beanFields.put(clazz, clazz.getDeclaredFields());
        }
        return beanFields.get(clazz);
    }

    void setBeanFields(Class<?> clazz) {
        if (!beanFields.containsKey(clazz)) {

            List<Field> preSets = new ArrayList<>();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(PreSet.class)) {
                    preSets.add(field);
                }
            }
            beanFields.put(clazz, clazz.getDeclaredFields());
            preSetFields.put(clazz, preSets.toArray(new Field[preSets.size()]));
        }
    }

    Field[] getPreSetFields(Class<?> clazz) {
        Field[] fields = preSetFields.get(clazz);
        return fields != null ? fields : new Field[]{};
    }

    String[] getUnescapeMethods(Class<?> clazz) {
        String[] methods = unescapeMethods.get(clazz);
        return methods != null ? methods : new String[]{};
    }

    Method[] getPostConstructMethods(Class<?> clazz) {
        Method[] methods = postConstructMethods.get(clazz);
        return methods != null ? methods : new Method[]{};
    }

    Method[] getPreDestroyMethods(Class<?> clazz) {
        Method[] methods = preDestroyMethods.get(clazz);
        return methods != null ? methods : new Method[]{};
    }

    Method[] getPostSubmitMethods(Class<?> clazz) {
        Method[] methods = postSubmitMethods.get(clazz);
        return methods != null ? methods : new Method[]{};
    }

    Method[] getPreSubmitMethods(Class<?> clazz) {
        Method[] methods = preSubmitMethods.get(clazz);
        return methods != null ? methods : new Method[]{};
    }

    Method[] getExecuteAccessMethods(Class<?> clazz) {
        Method[] methods = executeAccessMethods.get(clazz);
        return methods != null ? methods : new Method[]{};
    }

    Method[] getBeanMethods(Class<?> clazz) {
        if (!beanMethods.containsKey(clazz)) {
            beanMethods.put(clazz, clazz.getMethods());
        }
        return beanMethods.get(clazz);
    }

    void setBeanMethods(Class<?> clazz) {
        if (!beanMethods.containsKey(clazz)) {
            List<Method> postConstructs = new ArrayList<>();
            List<Method> preDestroys = new ArrayList<>();
            List<Method> postSubmits = new ArrayList<>();
            List<Method> preSubmits = new ArrayList<>();
            List<String> unescapes = new ArrayList<>();
            List<Method> executeAccess = new ArrayList<>();

            for (Method method: clazz.getMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    postConstructs.add(method);
                }
                if (method.isAnnotationPresent(PreDestroy.class)) {
                    preDestroys.add(method);
                }
                if (method.isAnnotationPresent(PostSubmit.class)) {
                    postSubmits.add(method);
                }
                if (method.isAnnotationPresent(PreSubmit.class)) {
                    preSubmits.add(method);
                }
                if (method.isAnnotationPresent(Unescape.class)) {
                    Matcher matcher = SET_METHOD_PATTERN.matcher(method.getName());
                    if (matcher.find()) {
                        unescapes.add(matcher.group(1));
                    }
                }
                if (method.isAnnotationPresent(ExecuteAccess.class)) {
                    executeAccess.add(method);
                }
            }

            beanMethods.put(clazz, clazz.getMethods());
            executeAccessMethods.put(clazz, executeAccess.toArray(new Method[executeAccess.size()]));
            postConstructMethods.put(clazz, postConstructs.toArray(new Method[postConstructs.size()]));
            preDestroyMethods.put(clazz, preDestroys.toArray(new Method[preDestroys.size()]));
            postSubmitMethods.put(clazz, postSubmits.toArray(new Method[postSubmits.size()]));
            preSubmitMethods.put(clazz, preSubmits.toArray(new Method[preSubmits.size()]));
            unescapeMethods.put(clazz, unescapes.toArray(new String[unescapes.size()]));
        }
    }

    Field[] getAuthFields(Class<?> clazz) {
        Field[] fields = authFields.get(clazz);
        return fields != null ? fields : new Field[]{};
    }

    void setAuthFields(Class<?> clazz) {
        if (!authFields.containsKey(clazz)) {
            List<Field> fields = new ArrayList<>();

            for (Field field : getBeanFields(clazz)) {
                if (!field.isAnnotationPresent(AuthField.class)) {
                    continue;
                }
                fields.add(field);
            }
            authFields.put(clazz, fields.toArray(new Field[fields.size()]));
        }
    }

    Field[] getAuthAccess(Class<?> clazz) {
        Field[] fields = authAccess.get(clazz);
        return fields != null ? fields : new Field[]{};
    }

    void setAuthAccess(Class<?> clazz) {
        if (!authAccess.containsKey(clazz)) {
            List<Field> fields = new ArrayList<>();

            for (Field field : getBeanFields(clazz)) {
                if (!field.isAnnotationPresent(AuthAccess.class)) {
                    continue;
                }
                fields.add(field);
            }
            authAccess.put(clazz, fields.toArray(new Field[fields.size()]));
        }
    }

    Method[] getAuthMethods(Class<?> clazz) {
        Method[] methods = authMethods.get(clazz);
        return methods != null ? methods : new Method[]{};
    }

    void setAuthMethods(Class<?> clazz) {
        if (!authMethods.containsKey(clazz)) {
            List<Method> methods = new ArrayList<>();

            for (Method method : getBeanMethods(clazz)) {
                if (!method.isAnnotationPresent(AuthMethod.class)) {
                    continue;
                }
                Class<?> returnType = method.getReturnType();
                if (Boolean.class == returnType || boolean.class == returnType) {
                    methods.add(method);
                }
            }
            authMethods.put(clazz, methods.toArray(new Method[methods.size()]));
        }
    }
}
