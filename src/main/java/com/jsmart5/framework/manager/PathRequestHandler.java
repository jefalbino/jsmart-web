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

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathRequestHandler {

    private static final Pattern PATH_PATTERN = Pattern.compile("(/[^?#/]*)");

    private static final Gson gson = new Gson();

    private final String pathPattern;

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private String bodyContent;

    private Map<String, String> queryParams;

    PathRequestHandler(final String pathPattern, final HttpServletRequest request, final HttpServletResponse response) {
        this.pathPattern = pathPattern;
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getRequestPath() {
        return request.getPathInfo();
    }

    public String getPathParam(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }

        int iteration = 0;
        String normPath = pathPattern.replace(request.getServletPath(), "");

        Matcher matcher = PATH_PATTERN.matcher(normPath);
        while (matcher.find()) {
            iteration++;
            if (matcher.group(1).contains("{" + param + "}")) {
                break;
            }
        }
        matcher = PATH_PATTERN.matcher(getRequestPath());
        while (matcher.find()) {
            if ((--iteration) == 0) {
                return matcher.group(1).replace("/", "");
            }
        }
        return null;
    }

    public Map<String, String> getQueryParams() {
        if (queryParams == null) {
            queryParams = new HashMap<String, String>();

            final String queryParam = request.getQueryString();
            if (queryParam == null || queryParam.trim().isEmpty()) {
                return queryParams;
            }

            for (String param : request.getParameterMap().keySet()) {
                if (queryParam.contains(param + "=")) {
                    queryParams.put(param, request.getParameter(param));
                }
            }
        }
        return queryParams;
    }

    public String getContentAsString() throws IOException {
        if (bodyContent == null) {
            String line = null;
            final StringBuffer buffer = new StringBuffer();

            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            bodyContent = buffer.toString();
        }
        return bodyContent;
    }

    public <T> T getContentFromJson(Class<T> clazz) throws IOException {
        return gson.fromJson(getContentAsString(), clazz);
    }

    public <T> T getContentFromXml(Class<T> clazz) throws IOException, JAXBException {
        final JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(getContentAsString());
        return (T) unmarshaller.unmarshal(reader);
    }

    public void writeResponseAsString(String responseVal) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.write(responseVal);
        writer.flush();
    }

    public void writeResponseAsJson(Object object) throws IOException {
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(gson.toJson(object));
        writer.flush();
    }

    public void writeResponseAsXml(Object object) throws IOException, JAXBException  {
        response.setContentType("application/xml");
        PrintWriter writer = response.getWriter();

        final JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        final Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(object, writer);
        writer.flush();
    }
}