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
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathRequestHandler {

    private static final Pattern PATH_PARAM_PATTERN = Pattern.compile("\\{([^/]*)\\}");

    private static final Gson gson = new Gson();

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private String bodyContent;

    private Map<String, String> queryParams;

    PathRequestHandler(final HttpServletRequest request, final HttpServletResponse response) {
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
//        String requestPath = getRequestPath();
//        Matcher matcher = PATH_PARAM_PATTERN.matcher(requestPath);
//        while (matcher.find()) {
//            System.out.println("/home/{user}/test/{test1}".substring(matcher.start(), matcher.end()));
//        }
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

}