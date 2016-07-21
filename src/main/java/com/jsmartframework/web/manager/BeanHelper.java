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
import static com.jsmartframework.web.util.WebText.TEXTS;

import com.google.gson.internal.Primitives;
import com.jsmartframework.web.annotation.AuthAccess;
import com.jsmartframework.web.annotation.AuthBean;
import com.jsmartframework.web.annotation.AuthField;
import com.jsmartframework.web.annotation.AuthMethod;
import com.jsmartframework.web.annotation.ExecuteAccess;
import com.jsmartframework.web.annotation.ExposeVar;
import com.jsmartframework.web.annotation.PostAction;
import com.jsmartframework.web.annotation.PostSubmit;
import com.jsmartframework.web.annotation.PreAction;
import com.jsmartframework.web.annotation.PreSet;
import com.jsmartframework.web.annotation.PreSubmit;
import com.jsmartframework.web.annotation.Unescape;
import com.jsmartframework.web.annotation.VarMapping;
import com.jsmartframework.web.annotation.WebBean;
import com.jsmartframework.web.annotation.WebFilter;
import com.jsmartframework.web.annotation.WebSecurity;
import com.jsmartframework.web.annotation.WebServlet;
import com.jsmartframework.web.config.UrlPattern;

import com.jsmartframework.web.util.WebText;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

enum BeanHelper {

    HELPER();

    private static final String ENCODING = "UTF-8";

    private static final Logger LOGGER = Logger.getLogger(BeanHelper.class.getPackage().getName());

    private static final Pattern SET_METHOD_PATTERN = Pattern.compile("^set(.*)");

    private static final Pattern PROPERTIES_NAME_PATTERN = Pattern.compile("\\s*_(.*)\\.properties");

    private static final Pattern PATH_BEAN_ALL_PATTERN = Pattern.compile("(.*)/\\*");

    private Map<Class<?>, String> beanNames = new ConcurrentHashMap<>();

    private Map<Class<?>, Field[]> beanFields = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> beanMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Field[]> preSetFields = new ConcurrentHashMap<>();

    private Map<Class<?>, Field[]> exposeVarFields = new ConcurrentHashMap<>();

    private Map<String, List<Class<?>>> exposeVarPaths = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> postConstructMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> preDestroyMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> preSubmitMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> preActionMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> postSubmitMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> postActionMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, String[]> unescapeMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> executeAccessMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Field[]> authFields = new ConcurrentHashMap<>();

    private Map<Class<?>, Field[]> authAccess = new ConcurrentHashMap<>();

    private Map<Class<?>, Method[]> authMethods = new ConcurrentHashMap<>();

    private Map<Field, Map<String, WebText.WebTextSet>> varMappingFields = new ConcurrentHashMap<>();

    String getBeanName(Class<?> clazz) {
        return beanNames.get(clazz);
    }

    String getClassName(String name) {
        return name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toLowerCase());
    }

    String getClassName(WebBean webBean, Class<?> beanClass) {
        String beanName = webBean.name();
        if (StringUtils.isBlank(beanName)) {
            beanName = getClassName(beanClass.getSimpleName());
        }
        beanNames.put(beanClass, beanName);
        return beanName;
    }

    String getClassName(AuthBean authBean, Class<?> authClass) {
        String beanName = authBean.name();
        if (StringUtils.isBlank(beanName)) {
            beanName = getClassName(authClass.getSimpleName());
        }
        beanNames.put(authClass, beanName);
        return beanName;
    }

    String getClassName(WebServlet servlet, Class<?> servletClass) {
        if (StringUtils.isBlank(servlet.name())) {
            String servletName = servletClass.getSimpleName();
            return getClassName(servletName);
        }
        return servlet.name();
    }

    String getClassName(WebFilter filter, Class<?> filterClass) {
        if (StringUtils.isBlank(filter.name())) {
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
            List<Field> exposeVars = new ArrayList<>();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(PreSet.class)) {
                    preSets.add(field);
                }

                if (field.isAnnotationPresent(ExposeVar.class)) {
                    exposeVars.add(field);
                    ExposeVar exposeVar = field.getAnnotation(ExposeVar.class);

                    if (StringUtils.isNotBlank(exposeVar.value().i18n())) {
                        if (!field.getType().equals(Map.class)) {
                            throw new RuntimeException("Field [" + field + "] annotated with ExposeVar containing " +
                                    "VarMapping attribute must be the type of Map<String, Object>");
                        }
                        setExposeVarMapping(field, exposeVar.value());
                    }

                    for (String varPath : cleanPaths(exposeVar.forPaths())) {
                        List<Class<?>> classes = exposeVarPaths.get(varPath);
                        if (classes == null) {
                            exposeVarPaths.put(varPath, classes = new ArrayList<>());
                        }
                        classes.add(clazz);
                    }
                }
            }

            beanFields.put(clazz, clazz.getDeclaredFields());
            preSetFields.put(clazz, preSets.toArray(new Field[preSets.size()]));
            exposeVarFields.put(clazz, exposeVars.toArray(new Field[exposeVars.size()]));
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

    @Deprecated
    Method[] getPostSubmitMethods(Class<?> clazz) {
        Method[] methods = postSubmitMethods.get(clazz);
        return methods != null ? methods : new Method[]{};
    }

    Method[] getPostActionMethods(Class<?> clazz) {
        Method[] methods = postActionMethods.get(clazz);
        return methods != null ? methods : new Method[]{};
    }

    @Deprecated
    Method[] getPreSubmitMethods(Class<?> clazz) {
        Method[] methods = preSubmitMethods.get(clazz);
        return methods != null ? methods : new Method[]{};
    }

    Method[] getPreActionMethods(Class<?> clazz) {
        Method[] methods = preActionMethods.get(clazz);
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
            List<Method> postActions = new ArrayList<>();
            List<Method> preSubmits = new ArrayList<>();
            List<Method> preActions = new ArrayList<>();
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
                if (method.isAnnotationPresent(PostAction.class)) {
                    postActions.add(method);
                }
                if (method.isAnnotationPresent(PreSubmit.class)) {
                    preSubmits.add(method);
                }
                if (method.isAnnotationPresent(PreAction.class)) {
                    preActions.add(method);
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
            postActionMethods.put(clazz, postActions.toArray(new Method[postActions.size()]));
            preSubmitMethods.put(clazz, preSubmits.toArray(new Method[preSubmits.size()]));
            preActionMethods.put(clazz, preActions.toArray(new Method[preActions.size()]));
            unescapeMethods.put(clazz, unescapes.toArray(new String[unescapes.size()]));
        }
    }

    Field[] getAuthFields(Class<?> clazz) {
        Field[] fields = authFields.get(clazz);
        return fields != null ? fields : new Field[]{};
    }

    boolean hasPrimitiveAuthFields(Class<?> clazz) {
        Field[] fields = getAuthFields(clazz);
        for (int i = 0; i < fields.length; i++) {
            if (Primitives.isPrimitive(fields[i].getGenericType())) {
                return true;
            }
        }
        return false;
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

    List<String> cleanPaths(String[] urlPaths) {
        List<String> cleanPaths = new ArrayList<>();
        if (urlPaths.length == 1 && "/*".equals(urlPaths[0].trim())) {
            urlPaths = CONFIG.getContent().getUrlPatternsArray();
        }

        for (String urlPattern : urlPaths) {
            String path = getCleanPath(urlPattern);
            cleanPaths.add(matchUrlPattern(path));
        }
        return cleanPaths;
    }

    String getCleanPath(String path) {
        Matcher matcher = PATH_BEAN_ALL_PATTERN.matcher(path);
        if (matcher.find()) {
            path = matcher.group(1);
        }
        return path;
    }

    String matchUrlPattern(String path) {
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

    Field[] getExposeVarFields(Class<?> clazz) {
        Field[] fields = exposeVarFields.get(clazz);
        return fields != null ? fields : new Field[]{};
    }

    List<Class<?>> getExposeVarByPath(String path) {
        List<Class<?>> classes = exposeVarPaths.get(path);
        return classes != null ? classes : Collections.EMPTY_LIST;
    }

    Map<String, Object> getExposeVarMapping(Field field) {
        Locale locale = WebContext.getLocale();
        String language = locale != null ? locale.getLanguage() : Locale.getDefault().getLanguage();

        Map<String, WebText.WebTextSet> localeTexts = varMappingFields.get(field);
        if (localeTexts != null) {
            if (localeTexts.containsKey(language)) {
                return localeTexts.get(language).getValues();
            }
            return localeTexts.get(Locale.getDefault().getLanguage()).getValues();
        }
        return null;
    }

    private void setExposeVarMapping(Field field, VarMapping varMapping) {
        try {
            List<String> properties = IOUtils.readLines(getClass().getClassLoader().getResourceAsStream("/"), ENCODING);
            for (String propertiesName : properties) {

                if (propertiesName.startsWith(varMapping.i18n()) && propertiesName.endsWith(".properties")) {
                    String language = CONFIG.getContent().getDefaultLocale();

                    Matcher matcher = PROPERTIES_NAME_PATTERN.matcher(propertiesName);
                    if (matcher.find()) {
                        language = matcher.group(1);
                    }
                    setExposeVarMapping(field, varMapping, language);
                }
            }
            setExposeVarMapping(field, varMapping, Locale.getDefault().getLanguage());

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error reading list of resource properties", ex);
        }
    }

    private void setExposeVarMapping(Field field, VarMapping varMapping, String language) {
        WebText.WebTextSet webTextSet = TEXTS.getStrings(varMapping.i18n(), varMapping.prefix(), language);

        if (webTextSet != null) {
            Map<String, WebText.WebTextSet> localeTexts = varMappingFields.get(field);
            if (localeTexts == null) {
                varMappingFields.put(field, localeTexts = new ConcurrentHashMap<>());
            }
            localeTexts.put(webTextSet.getLanguage(), webTextSet);
        }
    }
}
