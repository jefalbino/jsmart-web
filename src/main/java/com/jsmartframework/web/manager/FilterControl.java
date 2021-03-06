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
import static com.jsmartframework.web.config.Constants.CSRF_TOKEN_NAME;
import static com.jsmartframework.web.config.Constants.CSRF_TOKEN_VALUE;
import static com.jsmartframework.web.config.Constants.ENCODING;
import static com.jsmartframework.web.config.Constants.FILTER_HEADERS;
import static com.jsmartframework.web.config.Constants.FILTER_RESOURCES;
import static com.jsmartframework.web.config.Constants.IMPLEMENTATION_VERSION;
import static com.jsmartframework.web.config.Constants.INDEX_JSP;
import static com.jsmartframework.web.config.Constants.LIB_FILE_PATH;
import static com.jsmartframework.web.config.Constants.LIB_JAR_FILE_PATTERN;
import static com.jsmartframework.web.config.Constants.MANIFEST;
import static com.jsmartframework.web.config.Constants.PATH_SEPARATOR;
import static com.jsmartframework.web.config.Constants.ROOT_PATH;
import static com.jsmartframework.web.config.Constants.REQUEST_META_DATA_CSRF_TOKEN_NAME;
import static com.jsmartframework.web.config.Constants.REQUEST_META_DATA_CSRF_TOKEN_VALUE;
import static com.jsmartframework.web.config.Constants.REQUEST_PAGE_DOC_SCRIPT_ATTR;
import static com.jsmartframework.web.config.Constants.REQUEST_PAGE_SCRIPT_ATTR;
import static com.jsmartframework.web.config.Constants.REQUEST_REDIRECT_PATH_AJAX_ATTR;
import static com.jsmartframework.web.config.Constants.REQUEST_REDIRECT_WINDOW_PATH_AJAX_ATTR;
import static com.jsmartframework.web.config.Constants.REQUEST_EXPOSE_VARS_ATTR;
import static com.jsmartframework.web.config.Constants.SESSION_RESET_ATTR;
import static com.jsmartframework.web.manager.BeanHandler.HANDLER;
import static com.jsmartframework.web.manager.BeanHandler.AnnotatedFunction;
import static com.jsmartframework.web.manager.ExpressionHandler.EXPRESSIONS;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.jsmartframework.web.config.FileVersion;
import com.jsmartframework.web.config.HtmlCompress;
import com.jsmartframework.web.json.Headers;
import com.jsmartframework.web.json.Resources;
import com.jsmartframework.web.tag.FunctionTagHandler;
import com.jsmartframework.web.tag.html.DocScript;
import com.jsmartframework.web.tag.html.Head;
import com.jsmartframework.web.tag.html.Meta;
import com.jsmartframework.web.tag.html.Script;

import com.jsmartframework.web.tag.html.Tag;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.reflections.vfs.Vfs;
import org.reflections.vfs.Vfs.Dir;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;

public final class FilterControl implements Filter {

    private static final int STREAM_BUFFER = 2048;

    private static final Logger LOGGER = Logger.getLogger(FilterControl.class.getPackage().getName());

    private static final Pattern HTML_PATTERN = Pattern.compile("(<html.*?>)");

    private static final Pattern START_HEAD_PATTERN = Pattern.compile("(<head.*?>)");

    private static final Pattern CLOSE_BODY_PATTERN = Pattern.compile("(</body.*?>)");

    private static final Pattern SCRIPT_BODY_PATTERN = Pattern.compile("(<body.*?>\\s*)(<script.*?>)", Pattern.DOTALL);

    private static final Pattern JAR_FILE_PATTERN = Pattern.compile(LIB_JAR_FILE_PATTERN);

    private static Map<String, Pattern> versionFilePatterns = new ConcurrentHashMap<>();

    private static StringBuilder headerScripts = new StringBuilder();

    private static StringBuilder headerStyles = new StringBuilder();

    @Override
    public void init(FilterConfig config) throws ServletException {
        initHeaders(config);
        initResources(config);
        versionResources(config);
    }

    @Override
    public void destroy() {
        // DO NOTHING
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpRequest.setCharacterEncoding(ENCODING);
        httpResponse.setCharacterEncoding(ENCODING);

        // Initiate bean context based on current thread instance
        WebContext.initCurrentInstance(httpRequest, httpResponse);

        // Instantiate request scoped authentication bean
        HANDLER.instantiateAuthBean(httpRequest);

        // Instantiate web security for request extra validation
        HANDLER.instantiateWebSecurity(httpRequest);

        // Anonymous subclass to wrap HTTP response to print output
        WebFilterResponseWrapper responseWrapper = new WebFilterResponseWrapper(httpResponse);

        Throwable throwable = null;
        try {
            filterChain.doFilter(request, responseWrapper);
        } catch (Throwable thrown) {
            throwable = thrown;
            thrown.printStackTrace();
        }

        // Finalize request scoped web and auth beans
        HANDLER.finalizeBeans(httpRequest, responseWrapper);

        // Check if response was written before closing the WebContext
        boolean responseWritten = WebContext.isResponseWritten();

        // Close bean context based on current thread instance
        WebContext.closeCurrentInstance();

        // Case AsyncBean or RequestPath process was started it cannot proceed because it will not provide HTML via framework
        if (httpRequest.isAsyncStarted() || responseWritten) {

            // Generate response value after flushing the response wrapper buffer
            responseWrapper.flushBuffer();
            String responseVal = responseWrapper.toString();

            // Close current outputStream on responseWrapper
            responseWrapper.close();

            // Write the response value on real response object
            if (!httpResponse.isCommitted()) {
                httpResponse.getWriter().write(responseVal);
            }

            // Case internal server error
            if (throwable != null) {
                if (throwable instanceof IOException) {
                    throw new IOException(throwable);
                }
                throw new ServletException(throwable);
            }
            return;
        }

        // Add Ajax headers to control redirect and reset
        addAjaxHeaders(httpRequest, responseWrapper);

        // Generate HTML after flushing the response wrapper buffer
        responseWrapper.flushBuffer();
        String html = completeHtml(httpRequest, responseWrapper);

        // Close current outputStream on responseWrapper
        responseWrapper.close();

        // Case internal server error
        if (throwable != null) {
            responseWrapper.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            if (throwable instanceof IOException) {
                throw new IOException(throwable);
            }
            throw new ServletException(throwable);
        }

        if (StringUtils.isBlank(html)) {
            return;
        }

        if (CONFIG.getContent().isPrintHtml()) {
            LOGGER.log(Level.INFO, html);
        }

        // Compress html to better load performance
        HtmlCompress compressHtml = CONFIG.getContent().getCompressHtml();
        if (compressHtml.isCompressHtml()) {
            HtmlCompressor compressor = new HtmlCompressor();
            compressor.setRemoveComments(!compressHtml.isSkipComments());
            html = compressor.compress(html);
        }

        // Write our modified text to the real response
        if (!httpResponse.isCommitted()) {
            httpResponse.setContentLength(html.getBytes().length);
            httpResponse.getWriter().write(html);
        }
    }

    private void addAjaxHeaders(HttpServletRequest httpRequest, HttpServletResponseWrapper response) {
        // Case redirect via ajax, place tag with path to be handled by java script
        String ajaxPath = (String) httpRequest.getAttribute(REQUEST_REDIRECT_PATH_AJAX_ATTR);
        if (ajaxPath != null) {
            Boolean newWindow = (Boolean) httpRequest.getAttribute(REQUEST_REDIRECT_WINDOW_PATH_AJAX_ATTR);

            if (Boolean.TRUE.equals(newWindow)) {
                response.addHeader("New-Window-Ajax", ajaxPath);
            } else {
                response.addHeader("Redirect-Ajax", ajaxPath);
            }
        }

        // Case session reset, place tag to force java script reset the page
        HttpSession session = httpRequest.getSession();
        synchronized (session) {
            if (session.getAttribute(SESSION_RESET_ATTR) != null) {
                if (ajaxPath == null && WebContext.isAjaxRequest()) {
                    response.addHeader("Reset-Ajax", "Session");
                }
                session.removeAttribute(SESSION_RESET_ATTR);
            }
        }
    }

    private String completeHtml(HttpServletRequest httpRequest, HttpServletResponseWrapper response) {
        String html = response.toString();

        // Ajax request do not use scripts returned on html body
        if ("XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With"))) {
            return html;
        }

        // Check if it is a valid html, if not just return the html
        Matcher htmlMatcher = HTML_PATTERN.matcher(html);
        if (!htmlMatcher.find()) {
            return html;
        }

        html = completeHeader(httpRequest, htmlMatcher, html);
        html = completeScripts(httpRequest, html);
        html = completeVersions(httpRequest, html);
        return html;
    }

    private String completeHeader(HttpServletRequest httpRequest, Matcher htmlMatcher, String html) {
        // Try to place the css as the first link in the head tag
        Matcher startHeadMatcher = START_HEAD_PATTERN.matcher(html);
        if (startHeadMatcher.find()) {
            html = startHeadMatcher.replaceFirst("$1" + Matcher.quoteReplacement(headerStyles.toString()));
        } else {
            Head head = new Head();
            head.addText(headerStyles);
            html = htmlMatcher.replaceFirst("$1" + Matcher.quoteReplacement(head.getHtml().toString()));
        }

        // Place the CSRF token as Meta tags
        String tokenName = (String) httpRequest.getAttribute(REQUEST_META_DATA_CSRF_TOKEN_NAME);
        if (tokenName != null) {
            String tokenValue = (String) httpRequest.getAttribute(REQUEST_META_DATA_CSRF_TOKEN_VALUE);
            startHeadMatcher = START_HEAD_PATTERN.matcher(html);

            Tag csrfName = new Meta().addAttribute("name", CSRF_TOKEN_NAME).addAttribute("content", tokenName);
            Tag csrfToken = new Meta().addAttribute("name", CSRF_TOKEN_VALUE).addAttribute("content", tokenValue);
            StringBuilder metaTags = csrfName.getHtml().append(csrfToken.getHtml());

            html = startHeadMatcher.replaceFirst("$1" + Matcher.quoteReplacement(metaTags.toString()));
        }
        return html;
    }

    private String completeScripts(HttpServletRequest httpRequest, String html) {
        // Stand alone script with mapped exposed variables
        Script varScript = getExposeVarScripts(httpRequest);

        // Stand alone functions mapped via function tag
        Script funcScript = getFunctionScripts(httpRequest);

        // General page scripts executed when document is ready
        DocScript docScript = (DocScript) httpRequest.getAttribute(REQUEST_PAGE_DOC_SCRIPT_ATTR);

        StringBuilder scriptBuilder = new StringBuilder(headerScripts);
        if (varScript != null) {
            scriptBuilder.append(varScript.getHtml());
        }
        if (funcScript != null) {
            scriptBuilder.append(funcScript.getHtml());
        }
        if (docScript != null) {
            scriptBuilder.append(docScript.getHtml());
        }

        // Place the scripts before the last script tag inside body
        Matcher scriptMatcher = SCRIPT_BODY_PATTERN.matcher(html);
        if (scriptMatcher.find()) {
            return scriptMatcher.replaceFirst("$1" + Matcher.quoteReplacement(scriptBuilder.toString()) + "$2");
        }

        // Place the scripts before the end body tag
        Matcher bodyMatcher = CLOSE_BODY_PATTERN.matcher(html);
        if (!bodyMatcher.find()) {
            throw new RuntimeException("HTML tag [body] could not be find. Please insert the body tag in your JSP");
        }
        return bodyMatcher.replaceFirst(Matcher.quoteReplacement(scriptBuilder.toString()) + "$1");
    }

    private String completeVersions(HttpServletRequest httpRequest, String html) {
        if (versionFilePatterns.isEmpty()) {
            return html;
        }
        for (String version : versionFilePatterns.keySet()) {
            Pattern filePattern = versionFilePatterns.get(version);
            Matcher fileMatcher = filePattern.matcher(html);
            html = fileMatcher.replaceAll("$1" + Matcher.quoteReplacement("?" + version));
        }
        return html;
    }

    private Script getFunctionScripts(HttpServletRequest httpRequest) {
        String requestPath = httpRequest.getServletPath();
        List<AnnotatedFunction> annotatedFunctions = HANDLER.getAnnotatedFunctions(requestPath);

        for (AnnotatedFunction annotatedFunction : annotatedFunctions) {
            try {
                new FunctionTagHandler().executeTag(httpRequest, annotatedFunction);
            } catch (JspException | IOException e) {
                LOGGER.log(Level.SEVERE, "Annotated Function could not be generated for path [" + requestPath + "]: " + e.getMessage());
            }
        }
        return (Script) httpRequest.getAttribute(REQUEST_PAGE_SCRIPT_ATTR);
    }

    private Script getExposeVarScripts(HttpServletRequest httpRequest) {
        Map<String, Object> exposeVars = (Map) httpRequest.getAttribute(REQUEST_EXPOSE_VARS_ATTR);
        if (exposeVars == null || exposeVars.isEmpty()) {
            return null;
        }

        StringBuilder varBuilder = new StringBuilder();
        for (String key : exposeVars.keySet()) {
            varBuilder.append("var ").append(key).append(" = ")
                    .append(EXPRESSIONS.GSON.toJson(exposeVars.get(key)))
                    .append(";");
        }
        Script script = new Script();
        script.addAttribute("type", "text/javascript");
        script.addText(varBuilder);
        return script;
    }

    private void initHeaders(FilterConfig config) {
        String assetsUrl = CONFIG.getContent().getAssetsUrl();
        String contextPath = config.getServletContext().getContextPath();

        String headerPath = "/";
        if (StringUtils.isNotBlank(assetsUrl)) {
            headerPath = assetsUrl + (assetsUrl.endsWith("/") ? "" : "/");
        } else if (StringUtils.isNotBlank(contextPath)) {
            headerPath = contextPath + "/";
        }

        Headers jsonHeaders = EXPRESSIONS.GSON.fromJson(convertResourceToString(FILTER_HEADERS), Headers.class);
        for (String style : jsonHeaders.getStyles()) {
            headerStyles.append(String.format(style, headerPath));
        }
        for (String script : jsonHeaders.getScripts()) {
            headerScripts.append(String.format(script, headerPath));
        }
    }

    @SuppressWarnings("resource")
    private String convertResourceToString(String resource) {
        InputStream is = FilterControl.class.getClassLoader().getResourceAsStream(resource);
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private void initResources(FilterConfig config) {
        try {
            if (CONFIG.getContent().getAssetsUrl() != null) {
                LOGGER.log(Level.INFO, "Using external assets, please provide the jsmart assets content at ["
                        + CONFIG.getContent().getAssetsUrl() + "]");
            }

            ServletContext context = config.getServletContext();
            Set<String> libs = context.getResourcePaths(LIB_FILE_PATH);

            if (libs == null || libs.isEmpty()) {
                LOGGER.log(Level.SEVERE, "Could not find the JSmart library JAR file. Empty [" + LIB_FILE_PATH + "] resource folder.");
                return;
            }

            String libFilePath = null;
            for (String lib : libs) {
                Matcher matcher = JAR_FILE_PATTERN.matcher(lib);
                if (matcher.find()) {
                    libFilePath = matcher.group();
                    break;
                }
            }

            if (libFilePath == null) {
                LOGGER.log(Level.SEVERE, "Could not find the JSmart library JAR file inside [" + LIB_FILE_PATH + "]");
                return;
            }

            Resources jsonResources = EXPRESSIONS.GSON.fromJson(convertResourceToString(FILTER_RESOURCES), Resources.class);

            File libFile = new File(context.getRealPath(libFilePath));
            Dir content = Vfs.fromURL(libFile.toURI().toURL());

            Iterator<Vfs.File> files = content.getFiles().iterator();
            while (files.hasNext()) {
                Vfs.File file = files.next();

                // Copy index.jsp and replace content to redirect to welcome-url case configured
                if (file.getRelativePath().startsWith(INDEX_JSP)) {
                    if (CONFIG.getContent().getWelcomeUrl() != null) {
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(file.openInputStream(), writer);
                        String index = writer.toString().replace("{0}", CONFIG.getContent().getWelcomeUrl());
                        copyFileResource(new ByteArrayInputStream(index.getBytes(ENCODING)), file.getRelativePath(), context);
                    }
                }

                // Do not copy anything if assets-url was provided
                if (CONFIG.getContent().getAssetsUrl() != null) {
                    continue;
                }

                // Copy js, css and font resources to specific location
                for (String resource : jsonResources.getResources()) {
                    String resourcePath = resource.replace("*", "");

                    if (file.getRelativePath().startsWith(resourcePath)) {
                        initDirResources(context.getRealPath(PATH_SEPARATOR), file.getRelativePath());
                        copyFileResource(file.openInputStream(), file.getRelativePath(), context);
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
        }
    }

    private void initDirResources(String currentPath, String relativePath) {
        if (relativePath.contains(PATH_SEPARATOR)) {
            String[] paths = relativePath.split(PATH_SEPARATOR);

            for (int i = 0; i < paths.length - 1; i++) {
                currentPath += PATH_SEPARATOR + paths[i];

                File dir = new File(currentPath);
                if (!dir.exists()) {
                    dir.mkdir();
                }
            }
        }
    }

    private void versionResources(FilterConfig config) {
        try {
            if (CONFIG.getContent().getFileVersions() == null) {
                LOGGER.log(Level.INFO, "Not using files version control.");
                return;
            }

            String automaticVersion = getAutomaticResourceVersion(config);
            if (automaticVersion != null) {
                LOGGER.log(Level.INFO, "Automatic versioning detected as " + automaticVersion);
            }

            ServletContext context = config.getServletContext();
            File rootFile = new File(context.getRealPath(ROOT_PATH));
            Dir content = Vfs.fromURL(rootFile.toURI().toURL());

            Map<String, StringBuilder> patternBuilders = new HashMap<>();
            Iterator<Vfs.File> files = content.getFiles().iterator();

            while (files.hasNext()) {
                Vfs.File file = files.next();
                String relativePath = file.getRelativePath();

                FileVersion fileVersion = CONFIG.getContent().getFileVersion(relativePath);
                if (fileVersion != null && !fileVersion.isOnExcludeFolders(relativePath)) {
                    if (!fileVersion.isIncludeMinified() && fileVersion.isMinifiedFile(relativePath)) {
                        continue;
                    }

                    String patternVersion = automaticVersion;
                    if (!fileVersion.isAuto()) {
                        patternVersion = fileVersion.getVersion();
                    }
                    if (patternVersion == null) {
                        continue;
                    }

                    StringBuilder patternBuilder = patternBuilders.get(patternVersion);
                    if (patternBuilder == null) {
                        patternBuilder = new StringBuilder("(");
                        patternBuilders.put(patternVersion, patternBuilder);
                    }
                    if (patternBuilder.length() > 1) {
                        patternBuilder.append("|");
                    }
                    patternBuilder.append(relativePath.replace(".", "\\."));
                }
            }

            for (String version : patternBuilders.keySet()) {
                StringBuilder patternBuilder = patternBuilders.get(version).append(")");
                versionFilePatterns.put(version, Pattern.compile(patternBuilder.toString(), Pattern.DOTALL));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
        }
    }

    private String getAutomaticResourceVersion(FilterConfig config) {
        try {
            Properties manifestProperties = new Properties();
            manifestProperties.load(config.getServletContext().getResourceAsStream(MANIFEST));
            return manifestProperties.getProperty(IMPLEMENTATION_VERSION);
        } catch (Exception e) {
            LOGGER.warning("Failed to retrieve [MANIFEST.MF] information for automatic versioning");
            return null;
        }
    }

    private void copyFileResource(InputStream is, String relativePath, ServletContext context) throws Exception {
        int count = 0;
        BufferedInputStream bis = new BufferedInputStream(is);
        String realFilePath = new File(context.getRealPath(PATH_SEPARATOR)).getPath() + PATH_SEPARATOR + relativePath;

        FileOutputStream fos = new FileOutputStream(realFilePath);
        BufferedOutputStream bos = new BufferedOutputStream(fos, STREAM_BUFFER);

        byte data[] = new byte[STREAM_BUFFER];
        while ((count = bis.read(data, 0, STREAM_BUFFER)) != -1) {
            bos.write(data, 0, count);
        }
        bos.close();
        bis.close();
    }

    private class WebFilterResponseWrapper extends HttpServletResponseWrapper {

        private WebFilterOutputStream outputStream = new WebFilterOutputStream();

        public WebFilterResponseWrapper(HttpServletResponse servletResponse) {
            super(servletResponse);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return outputStream;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(outputStream.getWriter(), true);
        }

        @Override
        public void reset() {
            outputStream.reset();
        }

        @Override
        public void flushBuffer() throws IOException {
            outputStream.flush();
        }

        public void close() throws IOException {
            outputStream.close();
        }

        @Override
        public String toString() {
            return outputStream.toString();
        }
    }

    private class WebFilterOutputStream extends ServletOutputStream {

        private StringWriter writer = new StringWriter();

        private WriteListener writeListener;

        public StringWriter getWriter() {
            return writer;
        }

        public void reset() {
            writer = new StringWriter();
        }

        @Override
        public void write(int b) throws IOException {
            try {
                writer.write(b);
                if (writeListener != null) {
                    writeListener.onWritePossible();
                }
            } catch (IOException ex) {
                if (writeListener != null) {
                    writeListener.onError(ex);
                }
                throw ex;
            }
        }

        @Override
        public void write(byte[] b) throws IOException {
            try {
                writer.write(new String(b));
                if (writeListener != null) {
                    writeListener.onWritePossible();
                }
            } catch (IOException ex) {
                if (writeListener != null) {
                    writeListener.onError(ex);
                }
                throw ex;
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            try {
                writer.write(new String(b), off, len);
                if (writeListener != null) {
                    writeListener.onWritePossible();
                }
            } catch (IOException ex) {
                if (writeListener != null) {
                    writeListener.onError(ex);
                }
                throw ex;
            }
        }

        @Override
        public void flush() throws IOException {
            writer.flush();
        }

        @Override
        public void close() throws IOException {
            writer.close();
        }

        @Override
        public String toString() {
            return writer.toString();
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            this.writeListener = writeListener;
        }
    }

}
