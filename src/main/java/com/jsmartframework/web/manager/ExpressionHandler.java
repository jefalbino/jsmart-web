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
import static com.jsmartframework.web.manager.BeanHandler.HANDLER;
import static com.jsmartframework.web.config.Constants.ENCODING;
import static com.jsmartframework.web.manager.BeanHandler.AnnotatedFunction;

import com.google.common.html.HtmlEscapers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.Primitives;
import com.jsmartframework.web.adapter.ListAdapter;
import com.jsmartframework.web.adapter.TableAdapter;
import com.jsmartframework.web.annotation.ProduceType;
import com.jsmartframework.web.config.Constants;
import com.jsmartframework.web.json.Scroll;
import com.jsmartframework.web.util.WebText;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

enum ExpressionHandler {

    EXPRESSIONS();

    private static final Logger LOGGER = Logger.getLogger(ExpressionHandler.class.getPackage().getName());

    public static final Pattern EL_PATTERN = Pattern.compile("@\\{[!]*(.[^@\\{\\}]*)\\}");

    public static final Pattern JSP_PATTERN = Pattern.compile("\\$\\{[!]*(.[^\\$\\{\\}]*)\\}");

    public static final Pattern ID_PATTERN = Pattern.compile("id=\"(.[^\"]*)\"");

    public static final String EL_PATTERN_FORMAT = "@{%s.%s}";

    public static final String BEAN_METHOD_NAME_FORMAT = "%s.%s";

    static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new JsonConverter.LocalDateTimeTypeConverter())
            .registerTypeAdapter(DateTime.class, new JsonConverter.DateTimeTypeConverter())
            .registerTypeAdapter(Date.class, new JsonConverter.DateTypeConverter())
            .create();

    Map<String, String> getRequestExpressions(HttpServletRequest request) {
        Map<String, String> expressions = new LinkedHashMap<>();
        for (String param : request.getParameterMap().keySet()) {
            String expr = extractExpression(request, param);
            if (expr != null) {
                expressions.put(param, expr);
            }
        }
        return expressions;
    }

    private String extractExpression(HttpServletRequest request, String param) {
        Matcher matcher = TagHandler.J_TAG_PATTERN.matcher(param);
        if (matcher.find()) {
            return TagEncrypter.decrypt(request, matcher.group(2).replace("[]", ""));
        }
        return null;
    }

    void handleRequestExpression(String jTag, String expr, String jParam) throws ServletException, IOException {
        try {
            if (jTag.equals(TagHandler.J_TAG)) {
                setExpressionValue(expr, jParam);

            } else if (jTag.equals(TagHandler.J_ARRAY)) {
                setExpressionValues(expr, jParam);

            } else if (jTag.equals(TagHandler.J_SEL)) {
                setSelectionValue(expr, jParam);

            } else if (jTag.equals(TagHandler.J_FILE)) {
                setExpressionFilePart(expr, jParam);

            } else if (jTag.equals(TagHandler.J_DATE)) {
                setExpressionDate(expr, jParam);

            } else if (jTag.equals(TagHandler.J_CAPTCHA)) {
                setExpressionCaptcha(expr, jParam);
            }
        } catch (PropertyNotWritableException e) {
            LOGGER.log(Level.SEVERE, "Property " + expr + " is not writable");
            throw e;
        }
    }

    String handleSubmitExpression(String expr, String jParam) throws ServletException, IOException {
        Object response = null;
        Matcher matcher = EL_PATTERN.matcher(expr);
        if (matcher.find()) {

            String beanMethod = matcher.group(1);
            String[] methodSign = beanMethod.split(Constants.EL_SEPARATOR);
            HttpServletRequest request = WebContext.getRequest();

            if (methodSign.length > 0 && WebContext.containsAttribute(methodSign[0])) {
                Object bean = getExpressionBean(methodSign[0]);
                String elBeanMethod = String.format(Constants.JSP_EL, beanMethod);

                // Check authorization to execute method
                if (!HANDLER.checkExecuteAuthorization(bean, elBeanMethod, request)) {
                    return null;
                }

                // Call mapped method with @PreSubmit / @PreAction annotation for specific action
                boolean preActionValidated = HANDLER.executePreSubmit(bean, methodSign[methodSign.length -1]);
                preActionValidated &= HANDLER.executePreAction(bean, methodSign[methodSign.length -1]);

                if (preActionValidated) {
                    Object[] arguments = null;
                    String[] paramArgs = request.getParameterValues(TagHandler.J_SBMT_ARGS + jParam);

                    AnnotatedFunction function = HANDLER.beanMethodFunctions.get(beanMethod);

                    if (paramArgs != null) {
                        arguments = new Object[paramArgs.length];
                        boolean unescape = HANDLER.containsUnescapeMethod(methodSign);

                        for (int i = 0; i < paramArgs.length; i++) {
                            if (function != null) {
                                Class<?> argClass = function.getArgumentType(i);

                                if (argClass != null && !Primitives.isPrimitive(argClass) && !argClass.equals(String.class)) {
                                    arguments[i] = GSON.fromJson(paramArgs[i], argClass);
                                    continue;
                                }
                            }
                            arguments[i] = unescape ? paramArgs[i] : escapeValue(paramArgs[i]);
                        }
                    }

                    // Call submit method
                    ELContext context = WebContext.getPageContext().getELContext();

                    MethodExpression methodExpr = WebContext.getExpressionFactory().createMethodExpression(context, elBeanMethod,
                            null, arguments != null ? new Class<?>[arguments.length] : new Class<?>[]{});

                    response = methodExpr.invoke(context, arguments);

                    if (function != null && function.hasProduces()) {
                        switch (function.getProduces()) {
                            case JSON:
                                WebContext.writeResponseAsJson(response);
                                break;
                            case XML:
                                WebContext.writeResponseAsXmlQuietly(response);
                                break;
                            case STRING:
                                WebContext.writeResponseAsString(response.toString());
                                break;
                        }
                        // Reset response to be passed to path analysis
                        response = null;
                    }

                    // Call mapped method with @PostSubmit / @PostAction annotation for specific action
                    HANDLER.executePostSubmit(bean, methodSign[methodSign.length -1]);
                    HANDLER.executePostAction(bean, methodSign[methodSign.length -1]);
                }
            }
        }
        return response != null ? response.toString() : null;
    }

    @SuppressWarnings("all")
    private void setSelectionValue(String expr, String jParam) {
        Matcher matcher = EL_PATTERN.matcher(expr);
        if (matcher.find()) {

            String beanMethod = matcher.group(1);
            String[] methodSign = beanMethod.split(Constants.EL_SEPARATOR);

            if (methodSign.length > 0 && WebContext.containsAttribute(methodSign[0])) {

                HttpServletRequest request = WebContext.getRequest();
                beanMethod = String.format(Constants.JSP_EL, beanMethod);

                // Get parameter mapped by TagHandler.J_VALUES
                String valuesParam = request.getParameter(TagHandler.J_SEL + jParam);
                Matcher valuesMatcher = TagHandler.J_TAG_PATTERN.matcher(valuesParam);

                Object object = null;
                List<Object> list = null;
                Scroll scroll = null;

                if (valuesMatcher.find()) {
                    object = getExpressionValue(TagEncrypter.decrypt(request, valuesMatcher.group(2)));
                }

                if (object instanceof ListAdapter) {
                    String scrollParam = request.getParameter(TagHandler.J_SCROLL + jParam);
                    scroll = GSON.fromJson(scrollParam, Scroll.class);

                    list = ((ListAdapter) object).load(scroll.getIndex(), scroll.getOffset(), scroll.getSize());

                } else if (object instanceof TableAdapter) {
                    String scrollParam = request.getParameter(TagHandler.J_SCROLL + jParam);
                    scroll = GSON.fromJson(scrollParam, Scroll.class);

                    list = ((TableAdapter) object).load(scroll.getIndex(), scroll.getOffset(), scroll.getSize(),
                            scroll.getSort(), scroll.getOrder(), scroll.getFilters());

                } else if (object instanceof List<?>) {
                    list = (List<Object>) object;
                }

                if (list != null && !list.isEmpty()) {
                    ELContext context = WebContext.getPageContext().getELContext();
                    ValueExpression valueExpr = WebContext.getExpressionFactory().createValueExpression(context, beanMethod, Object.class);

                    Integer index = Integer.parseInt(request.getParameter(TagHandler.J_SEL_VAL + jParam));

                    // Case scroll list with adapter need to calculate the difference between
                    // the first index of the loaded content with the clicked list item index
                    if (scroll != null) {
                        index -= scroll.getIndex();
                    }
                    valueExpr.setValue(context, list.get(index));
                }
            }
        }
    }

    void setExpressionValue(String expr, String jParam) {
        if (isReadOnlyParameter(jParam)) {
            return;
        }

        Matcher matcher = EL_PATTERN.matcher(expr);
        if (matcher.find()) {

            String beanMethod = matcher.group(1);
            String[] methodSign = beanMethod.split(Constants.EL_SEPARATOR);

            if (methodSign.length > 0 && WebContext.containsAttribute(methodSign[0])) {
                beanMethod = String.format(Constants.JSP_EL, beanMethod);

                ELContext context = WebContext.getPageContext().getELContext();
                ValueExpression valueExpr = WebContext.getExpressionFactory().createValueExpression(context, beanMethod, Object.class);

                Object value = WebContext.getRequest().getParameter(TagHandler.J_TAG + jParam);

                if (!HANDLER.containsUnescapeMethod(methodSign)) {
                    value = escapeValue((String) value);
                }
                valueExpr.setValue(context, value);
            }
        }
    }

    void setAttributeValue(String expr, Object value) {
        if (expr != null) {
            Matcher matcher = EL_PATTERN.matcher(expr);
            if (matcher.find()) {

                String beanMethod = matcher.group(1);
                String[] methodSign = beanMethod.split(Constants.EL_SEPARATOR);

                if (methodSign.length > 0 && WebContext.containsAttribute(methodSign[0])) {
                    beanMethod = String.format(Constants.JSP_EL, beanMethod);

                    ELContext context = WebContext.getPageContext().getELContext();
                    ValueExpression valueExpr = WebContext.getExpressionFactory().createValueExpression(context, beanMethod, Object.class);
                    valueExpr.setValue(context, value);
                }
            }
        }
    }

    private void setExpressionValues(String expr, String jParam) {
        if (isReadOnlyParameter(jParam)) {
            return;
        }

        Matcher matcher = EL_PATTERN.matcher(expr);
        if (matcher.find()) {

            String beanMethod = matcher.group(1);
            String[] methodSign = beanMethod.split(Constants.EL_SEPARATOR);

            if (methodSign.length > 0 && WebContext.containsAttribute(methodSign[0])) {
                beanMethod = String.format(Constants.JSP_EL, beanMethod);

                List<Object> list = new ArrayList<Object>();
                String[] values = WebContext.getRequest().getParameterValues(TagHandler.J_ARRAY + jParam);

                boolean unescape = HANDLER.containsUnescapeMethod(methodSign);

                if (values != null) {
                    for (String val : values) {
                        try {
                            list.add(NumberUtils.createNumber(val));
                        } catch (NumberFormatException e) {
                            list.add(unescape ? val : escapeValue(val));
                        }
                    }
                }

                // Check for empty value sent on array [false]
                if (list.size() == 1 && list.get(0) != null && list.get(0).equals("false")) {
                    list.clear();
                }

                ELContext context = WebContext.getPageContext().getELContext();
                ValueExpression valueExpr = WebContext.getExpressionFactory().createValueExpression(context, beanMethod, Object.class);
                valueExpr.setValue(context, list);
            }
        }
    }

    private void setExpressionFilePart(String expr, String jParam) throws ServletException, IOException {
        Matcher matcher = EL_PATTERN.matcher(expr);
        if (matcher.find()) {

            String beanMethod = matcher.group(1);
            String[] methodSign = beanMethod.split(Constants.EL_SEPARATOR);

            if (methodSign.length > 0 && WebContext.containsAttribute(methodSign[0])) {
                beanMethod = String.format(Constants.JSP_EL, beanMethod);

                ELContext context = WebContext.getPageContext().getELContext();
                ValueExpression valueExpr = WebContext.getExpressionFactory().createValueExpression(context, beanMethod, Object.class);

                Object value = WebContext.getRequest().getPart(TagHandler.J_PART + jParam);
                valueExpr.setValue(context, value);
            }
        }
    }

    private void setExpressionDate(String expr, String jParam) throws ServletException {
        if (isReadOnlyParameter(jParam)) {
            return;
        }

        Matcher matcher = EL_PATTERN.matcher(expr);
        if (matcher.find()) {

            String beanMethod = matcher.group(1);
            String[] methodSign = beanMethod.split(Constants.EL_SEPARATOR);

            if (methodSign.length > 0 && WebContext.containsAttribute(methodSign[0])) {
                beanMethod = String.format(Constants.JSP_EL, beanMethod);

                ELContext context = WebContext.getPageContext().getELContext();
                ValueExpression valueExpr = WebContext.getExpressionFactory().createValueExpression(context, beanMethod, Object.class);
                String value = WebContext.getRequest().getParameter(TagHandler.J_DATE + jParam);

                if (StringUtils.isNotBlank(value)) {
                    Throwable throwable = null;
                    try {
                        valueExpr.setValue(context, value);
                        return;
                    } catch (Exception ex) {
                        throwable = ex;
                    }

                    Long timeMillis = Long.parseLong(value);
                    try {
                        valueExpr.setValue(context, new Date(timeMillis));
                        return;
                    } catch (Exception ex) {
                        throwable = ex;
                    }

                    try {
                        valueExpr.setValue(context, Instant.ofEpochMilli(timeMillis)
                                .atZone(ZoneId.systemDefault()).toLocalDateTime());
                        return;
                    } catch (Exception ex) {
                        throwable = ex;
                    }

                    try {
                        valueExpr.setValue(context, new DateTime(timeMillis));
                        return;
                    } catch (Exception ex) {
                        throwable = ex;
                    }

                    if (throwable != null) {
                        throw new ServletException(throwable.getMessage());
                    }
                } else {
                    valueExpr.setValue(context, null);
                }
            }
        }
    }

    private void setExpressionCaptcha(String expr, String jParam) throws ServletException {
        HttpServletRequest request = WebContext.getRequest();

        // Just add the ReCaptcha value to mapped values for further validation
        if (expr.contains(ReCaptchaHandler.RESPONSE_V1_FIELD_NAME)) {
            WebContext.addMappedValue(ReCaptchaHandler.RESPONSE_V1_FIELD_NAME,
                    request.getParameter(TagHandler.J_CAPTCHA + jParam));

        } else {
            WebContext.addMappedValue(ReCaptchaHandler.RESPONSE_V2_FIELD_NAME,
                    request.getParameter(ReCaptchaHandler.RESPONSE_V2_FIELD_NAME));
        }
    }

    public Object getExpressionValue(Object expr) {
        if (expr != null) {
            String evalExpr = expr.toString();

            Matcher matcher = EL_PATTERN.matcher(evalExpr);
            if (!matcher.find()) {
                return expr;
            }

            boolean hasMoreGroup = false;
            StringBuffer exprBuffer = new StringBuffer();

            Object result = evaluateExpression(evalExpr.substring(matcher.start() + 2, matcher.end() - 1));
            matcher.appendReplacement(exprBuffer, result != null ? Matcher.quoteReplacement(result.toString()) : "null");

            while (matcher.find()) {
                hasMoreGroup = true;
                Object object = evaluateExpression(evalExpr.substring(matcher.start() + 2, matcher.end() - 1));
                matcher.appendReplacement(exprBuffer, object != null ? Matcher.quoteReplacement(object.toString()) : "null");
            }

            if (hasMoreGroup || result instanceof String) {
                return matcher.appendTail(exprBuffer).toString();
            } else {
                return result;
            }
        }
        return null;
    }

    private Object evaluateExpression(String expr) {
        if (expr == null) {
            return expr;
        }

        String[] exprs = expr.split(Constants.EL_SEPARATOR, 2);
        if (exprs.length == 2 && WebText.containsResource(exprs[0])) {
            return WebText.getString(exprs[0], exprs[1]);
        }

        String jspExpr = String.format(Constants.JSP_EL, expr);

        ELContext context = WebContext.getPageContext().getELContext();
        ValueExpression valueExpr = WebContext.getExpressionFactory().createValueExpression(context, jspExpr, Object.class);
        Object obj = valueExpr.getValue(context);

        if (obj instanceof String) {
            String[] objs = obj.toString().split(Constants.EL_SEPARATOR, 2);
            if (objs.length == 2 && WebText.containsResource(objs[0])) {
                return WebText.getString(objs[0], objs[1]);
            }
        }
        return obj;
    }

    private Object getExpressionBean(String name) {
        return WebContext.getAttribute(name);
    }

    private boolean isReadOnlyParameter(String jParam) {
        if (jParam != null) {
            return jParam.endsWith(Constants.EL_PARAM_READ_ONLY);
        }
        return false;
    }

    private Object escapeValue(String value) {
        if (value != null && CONFIG.getContent().isEscapeRequest()) {
            value = HtmlEscapers.htmlEscaper().escape(value);
        }
        return value;
    }

    String decodeUrl(String value) {
        try {
            return URLDecoder.decode(value, ENCODING);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return value;
    }
}
