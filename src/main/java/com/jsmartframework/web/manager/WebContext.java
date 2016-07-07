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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jsmartframework.web.util.WebAlert;
import com.jsmartframework.web.util.WebUtils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.el.ExpressionFactory;
import javax.servlet.AsyncContext;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * This class represents the context of the request being currently processed and it allows beans
 * to get an instance of {@link ServletContext}, {@link HttpSession}, {@link HttpServletRequest} or 
 * {@link HttpServletResponse}.
 * <br>
 * This class also include methods to add message to client side, check if request is Ajax request or 
 * retrieve attributes from the request, session or application among other utilities.
 */
public final class WebContext implements Serializable {

    private static final long serialVersionUID = -3910553204750683737L;

    private static final JspFactory JSP_FACTORY = JspFactory.getDefaultFactory();

    private static final Map<Thread, WebContext> THREADS = new ConcurrentHashMap<>();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new JsonConverter.DateTimeTypeConverter())
            .registerTypeAdapter(Date.class, new JsonConverter.DateTypeConverter())
            .create();

    private static Servlet smartServlet;

    private static JspApplicationContext jspContext;

    private HttpServletRequest request;

    private String bodyContent;

    private HttpServletResponse response;

    private boolean responseWritten;

    private String redirectTo;

    private boolean redirectToWindow;

    private boolean invalidate;

    private PageContext pageContext;

    private Map<String, List<WebAlert>> alerts = new LinkedHashMap<>();

    private Map<String, Object> mappedValues = new ConcurrentHashMap<>();

    private Map<String, String> queryParams;

    private WebContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    static final void setServlet(Servlet servlet) {
        smartServlet = servlet;
        jspContext = JSP_FACTORY.getJspApplicationContext(servlet.getServletConfig().getServletContext());
    }

    private static final WebContext getCurrentInstance() {
        return THREADS.get(Thread.currentThread());
    }

    static final void initCurrentInstance(HttpServletRequest request, HttpServletResponse response) {
        THREADS.put(Thread.currentThread(), new WebContext(request, response));
    }

    static final void closeCurrentInstance() {
        THREADS.remove(Thread.currentThread()).close();
    }

    private void close() {
        if (invalidate) {
            request.getSession().invalidate();
        }
        invalidate = false;
        request = null;
        bodyContent = null;
        response = null;
        responseWritten = false;
        redirectTo = null;
        alerts.clear();
        alerts = null;
        mappedValues.clear();
        mappedValues = null;
        JSP_FACTORY.releasePageContext(pageContext);
        pageContext = null;
    }

    static PageContext getPageContext() {
        WebContext context = getCurrentInstance();
        return context != null ? context.getPage() : null;
    }

    private PageContext getPage() {
        if (pageContext == null) {
            pageContext = JSP_FACTORY.getPageContext(smartServlet, request, response, null, true, 8192, true);
        }
        return pageContext;
    }

    static ExpressionFactory getExpressionFactory() {
        return jspContext.getExpressionFactory();
    }

    /**
     * Returns the current {@link ServletContext} instance associated to the request
     * being processed.
     *
     * @return a instance of {@link ServletContext}.
     */
    public static ServletContext getApplication() {
        return smartServlet.getServletConfig().getServletContext();
    }

    /**
     * Returns the current {@link HttpSession} instance associated to the request being
     * processed.
     *
     * @return a instance of {@link HttpSession}.
     */
    public static HttpSession getSession() {
        WebContext context = getCurrentInstance();
        return context != null ? context.request.getSession() : null;
    }

    /**
     * Returns the current {@link HttpServletRequest} instance associated to the request being
     * processed.
     *
     * @return a instance of {@link HttpServletRequest}.
     */
    public static HttpServletRequest getRequest() {
        WebContext context = getCurrentInstance();
        return context != null ? context.request : null;
    }

    /**
     * Returns the current query parameters associated to the request being processed.
     *
     * @return Map containing name and values of URL query parameters
     */
    public static Map<String, String> getQueryParams() {
        WebContext context = getCurrentInstance();
        if (context == null) {
            return null;
        }
        if (context.queryParams == null) {
            context.queryParams = new ConcurrentHashMap<String, String>();

            String queryParam = context.request.getQueryString();
            if (StringUtils.isBlank(queryParam)) {
                return context.queryParams;
            }

            for (String param : context.request.getParameterMap().keySet()) {
                if (queryParam.contains(param + "=")) {
                    context.queryParams.put(param, context.request.getParameter(param));
                }
            }
        }
        return context.queryParams;
    }

    /**
     * Returns the current {@link HttpServletResponse} instance associated to the request
     * being processed.
     *
     * @return a instance of {@link HttpServletResponse}
     */
    public static HttpServletResponse getResponse() {
        WebContext context = getCurrentInstance();
        return context != null ? context.response : null;
    }

    static String getRedirectTo() {
        WebContext context = getCurrentInstance();
        return context != null ? context.redirectTo : null;
    }

    static boolean isRedirectToWindow() {
        WebContext context = getCurrentInstance();
        return context != null ? context.redirectToWindow : false;
    }

    /**
     * Redirect the request to the specified link path after the current request is processed.
     * <br>
     * Case this method is called on {@link PostConstruct} annotated method, the redirect is done
     * after method execution.
     *
     * @param path mapped on configuration file {@code webConfig.xml} or general valid URL link.
     */
    public static void redirectTo(String path) {
        WebContext context = getCurrentInstance();
        if (context != null) {
            context.redirectTo = WebUtils.decodePath(path);
        }
    }

    /**
     * Redirect the request to the specified link path after the current request is processed on a
     * new window on client browser.
     * <br>
     * Case this method is called on {@link PostConstruct} annotated method, the redirect is done
     * after method execution.
     *
     * @param path mapped on configuration file {@code webConfig.xml} or general valid URL link.
     */
    public static void redirectToWindow(String path) {
        WebContext context = getCurrentInstance();
        if (context != null) {
            context.redirectTo = WebUtils.decodePath(path);
            context.redirectToWindow = true;
        }
    }

    /**
     * Calling this method will cause the current {@link HttpSession} to be invalidated after the request
     * processing is done. It means that the session will be invalidated after request is completed.
     * <br>
     * Case there is a need to invalidate the session at the moment of the execution, use {@link HttpSession}
     * invalidate method instead.
     */
    public static void invalidate() {
        WebContext context = getCurrentInstance();
        if (context != null) {
            context.invalidate = true;
        }
    }

    /**
     * Returns the {@link Locale} of the client associated to the request being processed.
     *
     * @return {@link Locale} instance.
     */
    public static Locale getLocale() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getLocale() : null;
    }

    /**
     * Returns true if the request being process was triggered by Ajax on client side,
     * false otherwise.
     *
     * @return boolean value indicating if request was done using Ajax.
     */
    public static boolean isAjaxRequest() {
        HttpServletRequest request = getRequest();
        return request != null ? "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) : false;
    }

    static List<WebAlert> getAlerts(String id) {
        WebContext context = getCurrentInstance();
        return context != null ? context.alerts.get(id) : null;
    }

    /**
     * Add info alert to be presented on client side after the response is returned.
     * <br>
     * This method only take effect if the alert tag is mapped with specified id on JSP page.
     * <br>
     * The message is placed on the same position where the {@code alert} is mapped.
     *
     * @param id of the alert to receive the message.
     * @param alert object containing alert details such as title, message, header and icon.
     */
    public static void addAlert(String id, WebAlert alert) {
        WebContext context = getCurrentInstance();
        if (context != null && id != null && alert != null) {
            List<WebAlert> alerts = context.alerts.get(id);
            if (alerts == null) {
                context.alerts.put(id, alerts = new ArrayList<WebAlert>());
            }
            alerts.add(alert);
        }
    }

    /**
     * Add info alert to be presented on client side after the response is returned.
     * <br>
     * This method only take effect if the alert tag is mapped with specified id on JSP page.
     * <br>
     * The message is placed on the same position where the {@code alert} tag is mapped.
     *
     * @param id of the alert to receive the message.
     * @param message to be presented on the client side.
     */
    public static void addInfo(String id, String message) {
        WebAlert alert = new WebAlert(WebAlert.AlertType.INFO);
        alert.setMessage(message);
        addAlert(id, alert);
    }

    /**
     * Add warning alert to be presented on client side after the response is returned.
     * <br>
     * This method only take effect if the alert tag is mapped with specified id on JSP page.
     * <br>
     * The message is placed on the same position where the {@code alert} tag is mapped.
     *
     * @param id of the alert to receive the message.
     * @param message to be presented on the client side.
     */
    public static void addWarning(String id, String message) {
        WebAlert alert = new WebAlert(WebAlert.AlertType.WARNING);
        alert.setMessage(message);
        addAlert(id, alert);
    }

    /**
     * Add success alert to be presented on client side after the response is returned.
     * <br>
     * This method only take effect if the alert tag is mapped with specified id on JSP page.
     * <br>
     * The message is placed on the same position where the {@code alert} tag is mapped.
     *
     * @param id of the alert to receive the message.
     * @param message to be presented on the client side.
     */
    public static void addSuccess(String id, String message) {
        WebAlert alert = new WebAlert(WebAlert.AlertType.SUCCESS);
        alert.setMessage(message);
        addAlert(id, alert);
    }

    /**
     * Add error alert to be presented on client side after the response is returned.
     * <br>
     * This method only take effect if the alert tag is mapped with specified id on JSP page.
     * <br>
     * The message is placed on the same position where the {@code alert} message tag is mapped.
     *
     * @param id of the alert to receive the message.
     * @param message to be presented on the client side.
     */
    public static void addError(String id, String message) {
        WebAlert alert = new WebAlert(WebAlert.AlertType.DANGER);
        alert.setMessage(message);
        addAlert(id, alert);
    }

    static Object getMappedValue(String name) {
        WebContext context = getCurrentInstance();
        if (context != null) {
            return context.mappedValues.get(name);
        }
        return null;
    }

    static Object removeMappedValue(String name) {
        WebContext context = getCurrentInstance();
        if (context != null) {
            return context.mappedValues.remove(name);
        }
        return null;
    }

    static void addMappedValue(String name, Object value) {
        WebContext context = getCurrentInstance();
        if (context != null) {
            context.mappedValues.put(name, value);
        }
    }

    /**
     * Returns the attribute carried on {@link HttpServletRequest}, {@link HttpSession} or {@link ServletContext}
     * instances associated with current request being processed.
     *
     * @param name name of the attribute.
     * @return the {@link Object} mapped by attribute name on the current request.
     */
    public static Object getAttribute(String name) {
        if (name != null) {
            HttpServletRequest request = getRequest();
            if (request != null && request.getAttribute(name) != null) {
                return request.getAttribute(name);
            }

            HttpSession session = getSession();
            if (session != null) {
                synchronized (session) {
                    if (session.getAttribute(name) != null) {
                        return session.getAttribute(name);
                    }
                }
            }

            ServletContext application = getApplication();
            if (application.getAttribute(name) != null) {
                return application.getAttribute(name);
            }
        }
        return null;
    }

    /**
     * Check if attribute is carried on {@link HttpServletRequest}, {@link HttpSession} or {@link ServletContext}
     * instances associated with current request being processed.
     *
     * @param name name of the attribute.
     * @return true if the attribute is contained in one of the instances {@link HttpServletRequest},
     * {@link HttpSession} or {@link ServletContext}, false otherwise.
     */
    public static boolean containsAttribute(String name) {
        if (name != null) {
            HttpServletRequest request = getRequest();
            if (request != null && request.getAttribute(name) != null) {
                return true;
            }

            HttpSession session = getSession();
            if (session != null) {
                synchronized (session) {
                    if (session.getAttribute(name) != null) {
                        return true;
                    }
                }
            }

            return getApplication().getAttribute(name) != null;
        }
        return false;
    }

    /**
     * Given that the current request has ReCaptcha content to be verified use this method
     * to check if the ReCaptcha input is valid on server side.
     *
     * @param secretKey for ReCaptcha based on your domain registered
     *
     * @return true if the ReCaptcha input is valid, false otherwise
     */
    public static boolean checkReCaptcha(String secretKey) {
        String responseField = (String) getMappedValue(ReCaptchaHandler.RESPONSE_V1_FIELD_NAME);
        if (responseField != null) {
            return ReCaptchaHandler.checkReCaptchaV1(secretKey, responseField);
        }

        responseField = (String) getMappedValue(ReCaptchaHandler.RESPONSE_V2_FIELD_NAME);
        if (responseField != null) {
            return ReCaptchaHandler.checkReCaptchaV2(secretKey, responseField);
        }
        throw new RuntimeException("ReCaptcha not found on this submit. Plase make sure the recaptcha tag is included on submitted form");
    }

    /**
     * Returns the request content as String
     *
     * @return request content as String
     * @throws IOException
     */
    public static String getContentAsString() throws IOException {
        WebContext context = getCurrentInstance();
        if (context == null) {
            return null;
        }
        if (context.bodyContent == null) {
            String line = null;
            StringBuffer buffer = new StringBuffer();

            BufferedReader reader = context.request.getReader();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            context.bodyContent = buffer.toString();
        }
        return context.bodyContent;
    }

    /**
     * Get request content from JSON and convert it to class mapping the content.
     *
     * @param clazz - Class mapping the request content.
     * @param <T> - type of class to convert JSON into class.
     *
     * @return content from JSON to object
     * @throws IOException
     */
    public static <T> T getContentFromJson(Class<T> clazz) throws IOException {
        return getContentFromJson(clazz, GSON);
    }

    /**
     * Get request content from JSON and convert it to class mapping the content.
     *
     * @param clazz - Class mapping the request content.
     * @param gson - Gson converter to convert JSON request into object.
     * @param <T> - type of class to convert JSON into class.
     *
     * @return content from JSON to object
     * @throws IOException
     */
    public static <T> T getContentFromJson(Class<T> clazz, Gson gson) throws IOException {
        return gson.fromJson(getContentAsString(), clazz);
    }

    /**
     * Get request content from XML and convert it to class mapping the content.
     *
     * @param clazz - Class mapping the request content.
     * @param <T> - type of class to convert XML into class.
     *
     * @return content from XML to object
     * @throws IOException
     */
    public static <T> T getContentFromXml(Class<T> clazz) throws IOException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        return getContentFromXml(jaxbContext.createUnmarshaller());
    }

    /**
     * Get request content from XML and convert it to class mapping the content.
     *
     * @param unmarshaller - JAXBContext unmarshaller to convert the request content into object.
     * @param <T> - type of class to convert XML into class.
     *
     * @return content from XML to object
     * @throws IOException
     */
    public static <T> T getContentFromXml(Unmarshaller unmarshaller) throws IOException, JAXBException {
        StringReader reader = new StringReader(getContentAsString());
        return (T) unmarshaller.unmarshal(reader);
    }

    static boolean isResponseWritten() {
        WebContext context = getCurrentInstance();
        return context != null ? context.responseWritten : false;
    }

    /**
     * Write response directly as String. Note that by using this method the response
     * as HTML will not be generated and the response will be what you have defined.
     *
     * @param response String to write in the response.
     * @throws IOException
     */
    public static void writeResponseAsString(String response) throws IOException {
        WebContext context = getCurrentInstance();
        if (context != null) {
            context.responseWritten = true;
            PrintWriter writer = context.response.getWriter();
            writer.write(response);
            writer.flush();
        }
    }

    /**
     * Write response directly as JSON from Object. Note that by using this method the response
     * as HTML will not be generated and the response will be what you have defined.
     *
     * @param object Object to convert into JSON to write in the response.
     * @throws IOException
     */
    public static void writeResponseAsJson(Object object) throws IOException {
        writeResponseAsJson(object, GSON);
    }

    /**
     * Write response directly as JSON from Object. Note that by using this method the response
     * as HTML will not be generated and the response will be what you have defined.
     *
     * @param object Object to convert into JSON to write in the response.
     * @param gson Gson instance to be used to convert object into JSON.
     * @throws IOException
     */
    public static void writeResponseAsJson(Object object, Gson gson) throws IOException {
        WebContext context = getCurrentInstance();
        if (context != null) {
            context.responseWritten = true;
            context.response.setContentType("application/json");
            PrintWriter writer = context.response.getWriter();
            writer.write(gson.toJson(object));
            writer.flush();
        }
    }

    /**
     * Write response directly as XML from Object. Note that by using this method the response
     * as HTML will not be generated and the response will be what you have defined.
     *
     * @param object Object to convert into XML to write in the response.
     * @throws IOException
     */
    public static void writeResponseAsXml(Object object) throws IOException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        writeResponseAsXml(object, jaxbContext.createMarshaller());
    }

    /**
     * Write response directly as XML from Object. Note that by using this method the response
     * as HTML will not be generated and the response will be what you have defined.
     *
     * @param object Object to convert into XML to write in the response.
     * @param marshaller JAXBContext marshaller to write object as XML.
     * @throws IOException
     */
    public static void writeResponseAsXml(Object object, Marshaller marshaller) throws IOException, JAXBException {
        WebContext context = getCurrentInstance();
        if (context != null) {
            context.responseWritten = true;
            context.response.setContentType("application/xml");
            PrintWriter writer = context.response.getWriter();
            marshaller.marshal(object, writer);
            writer.flush();
        }
    }

    /**
     * Write response directly as Event-Stream for Server Sent Events.
     *
     * @param asyncContext - Asynchronous Context.
     * @param event - Name of event to be written on response.
     * @param data - Content of event ot be written on response.
     * @throws IOException
     */
    public static void writeResponseAsEventStream(AsyncContext asyncContext, String event, Object data) throws IOException {
        writeResponseAsEventStream(asyncContext, event, data, null);
    }

    /**
     * Write response directly as Event-Stream for Server Sent Events.
     *
     * @param asyncContext - Asynchronous Context.
     * @param event - Name of event to be written on response.
     * @param data - Content of event ot be written on response.
     * @param retry - Time in (milliseconds) for client retry opening connection.
     *              after asynchronous context is closed.
     * @throws IOException
     */
    public static void writeResponseAsEventStream(AsyncContext asyncContext, String event, Object data, Long retry) throws IOException {
        if (asyncContext != null && event != null && data != null) {
            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
            response.setContentType("text/event-stream");
            PrintWriter printWriter = response.getWriter();

            if (retry != null) {
                printWriter.write("retry:" + retry + "\n");
            }
            printWriter.write("event:" + event + "\n");
            printWriter.write("data:" + data + "\n\n");
            printWriter.flush();
        }
    }

    /**
     * Write response as file stream when you want to provide download functionality.
     * Note that by using this method the response as HTML will not be generated and
     * the response will be what you have defined.
     *
     * @param file - File to be written on response.
     * @param bufferSize - Buffer size to write the response. Example 2048 bytes.
     * @throws IOException
     */
    public static void writeResponseAsFileStream(File file, int bufferSize) throws IOException {
        WebContext context = getCurrentInstance();
        if (context != null && file != null && bufferSize > 0) {
            context.responseWritten = true;
            context.response.setHeader("Content-Disposition", "attachment;filename=\"" + file.getName() + "\"");
            context.response.addHeader("Content-Length", Long.toString(file.length()));
            context.response.setContentLength((int) file.length());

            String mimetype = getApplication().getMimeType(file.getName());
            context.response.setContentType((mimetype != null) ? mimetype : "application/octet-stream");

            FileInputStream fileInputStream = new FileInputStream(file);
            ServletOutputStream outputStream = context.response.getOutputStream();

            try {
                int i;
                byte[] buffer = new byte[bufferSize];
                while ((i = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, i);
                }
            } finally {
                outputStream.flush();
                fileInputStream.close();
            }
        }
    }
}