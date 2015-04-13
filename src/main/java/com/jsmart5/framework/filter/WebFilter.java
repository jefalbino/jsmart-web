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

package com.jsmart5.framework.filter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
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

import org.reflections.vfs.Vfs;
import org.reflections.vfs.Vfs.Dir;

import com.google.gson.Gson;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.jsmart5.framework.config.HtmlCompress;
import com.jsmart5.framework.json.Headers;
import com.jsmart5.framework.json.Resources;
import com.jsmart5.framework.manager.SmartContext;
import com.jsmart5.framework.tag.html.Head;
import com.jsmart5.framework.tag.html.DocScript;

import static com.jsmart5.framework.config.Config.*;
import static com.jsmart5.framework.config.Constants.*;
import static com.jsmart5.framework.manager.BeanHandler.*;

public final class WebFilter implements Filter {

	public static final String ENCODING = "UTF-8";

	private static final int STREAM_BUFFER = 2048;

	private static final Gson GSON = new Gson();

	private static final Logger LOGGER = Logger.getLogger(WebFilter.class.getPackage().getName());

	private static final Pattern HTML_PATTERN = Pattern.compile("(<html.*?>)");

	private static final Pattern START_HEAD_PATTERN = Pattern.compile("(<head.*?>)");

	private static final Pattern CLOSE_BODY_PATTERN = Pattern.compile("(</body.*?>)");

	private static final Pattern SCRIPT_BODY_PATTERN = Pattern.compile("(<body.*?>\\s*)(<script.*?>)", Pattern.DOTALL);

	private static final Pattern JAR_FILE_PATTERN = Pattern.compile(LIB_JAR_FILE_PATTERN);

	private static final StringBuilder headerScripts = new StringBuilder();;

	private static final StringBuilder headerStyles = new StringBuilder();

	@Override
	public void init(FilterConfig config) throws ServletException {
		initHeaders();
		initResources(config);
	}

	@Override
	public void destroy() {
		// DO NOTHING
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final HttpServletResponse httpResponse = (HttpServletResponse) response;

		httpRequest.setCharacterEncoding(ENCODING);
		httpResponse.setCharacterEncoding(ENCODING);

		// Initiate bean context based on current thread instance
		SmartContext.initCurrentInstance(httpRequest, httpResponse);

		// Anonymous subclass to wrap HTTP response to print output
		WebFilterResponseWrapper responseWrapper = new WebFilterResponseWrapper(httpResponse);

        Throwable throwable = null;

        try {
        	filterChain.doFilter(request, responseWrapper);
        } catch (Throwable thrown) {
        	throwable = thrown;
        	thrown.printStackTrace();
        }

        // Finalize request scoped beans
        HANDLER.finalizeBeans(httpRequest);
        
        // Add Ajax headers to control redirect and reset
        addAjaxHeaders(httpRequest, responseWrapper);

        // Include head data to html
        String html = null;
        responseWrapper.flushBuffer();

        try {
        	html = getCompleteHtml(httpRequest, responseWrapper);

        } finally {
        	// Remove session reset attribute
        	HttpSession session = httpRequest.getSession();
        	synchronized (session) {
            	session.removeAttribute(SESSION_RESET_ATTR);
        	}
        }

        // Close bean context based on current thread instance
        SmartContext.closeCurrentInstance();

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

		if (html == null || html.trim().isEmpty()) {
			responseWrapper.setStatus(HttpServletResponse.SC_NO_CONTENT);
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
			response.addHeader("Redirect-Ajax", ajaxPath);
		}

		// Case session reset, place tag to force java script reset the page
	    HttpSession session = httpRequest.getSession();
	    synchronized (session) {

			if (session.getAttribute(SESSION_RESET_ATTR) != null) {
				if (ajaxPath == null && SmartContext.isAjaxRequest()) {
					response.addHeader("Reset-Ajax", "Session");
				}
	        }
	    }
	}

	private String getCompleteHtml(HttpServletRequest httpRequest, HttpServletResponseWrapper response) {
		String html = response.toString();
        Matcher htmlMatcher = HTML_PATTERN.matcher(html);

		// Check if it is a valid html
        if (htmlMatcher.find()) {

			// Try to place the css as the first link in the head tag
        	Matcher startHeadMatcher = START_HEAD_PATTERN.matcher(html);
		    if (startHeadMatcher.find()) {
		    	html = startHeadMatcher.replaceFirst("$1" + Matcher.quoteReplacement(headerStyles.toString()));

		    } else {
		    	Head head = new Head();
		    	head.addText(headerStyles);
		    	html = htmlMatcher.replaceFirst("$1" + Matcher.quoteReplacement(head.getHtml().toString()));
		    }

		    DocScript script = (DocScript) httpRequest.getAttribute(REQUEST_PAGE_SCRIPT_ATTR);

		    // Place the scripts before the last script tag inside body
		    Matcher scriptMatcher = SCRIPT_BODY_PATTERN.matcher(html);
		    if (scriptMatcher.find()) {
		    	String scripts = Matcher.quoteReplacement(headerScripts.toString() + (script != null ? script.getHtml() : ""));
		    	return scriptMatcher.replaceFirst("$1" + scripts + "$2");
		    }

		    // Place the scripts before the end body tag
		    Matcher bodyMatcher = CLOSE_BODY_PATTERN.matcher(html);
		    if (!bodyMatcher.find()) {
		    	throw new RuntimeException("HTML tag [body] could not be find. Please insert the body tag in your JSP");
		    }

		    String scripts = Matcher.quoteReplacement(headerScripts.toString() + (script != null ? script.getHtml() : ""));
			return bodyMatcher.replaceFirst(scripts + "$1");
        }
		return html;
	}

	private void initHeaders() {
		Headers jsonHeaders = GSON.fromJson(convertResourceToString(FILTER_HEADERS), Headers.class);

		for (String style : jsonHeaders.getStyles()) {
			headerStyles.append(style);
		}

		for (String script : jsonHeaders.getScripts()) {
			headerScripts.append(script);
		}
	}

	@SuppressWarnings("resource")
	private String convertResourceToString(String resource) {
		InputStream is = WebFilter.class.getClassLoader().getResourceAsStream(resource);
		Scanner scanner = new Scanner(is).useDelimiter("\\A");
		return scanner.hasNext() ? scanner.next() : "";
	}

	private void initResources(FilterConfig config) {
		try {
			ServletContext context = config.getServletContext();
			Set<String> libs = context.getResourcePaths(LIB_FILE_PATH);

			if (libs == null || libs.isEmpty()) {
				LOGGER.log(Level.SEVERE, "Could not find the JSmart5 library JAR file. Empty " + LIB_FILE_PATH + " resource folder.");
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
				LOGGER.log(Level.SEVERE, "Could not find the JSmart5 library JAR file inside " + LIB_FILE_PATH);
				return;
			}

			Resources jsonResources = GSON.fromJson(convertResourceToString(FILTER_RESOURCES), Resources.class);

			File libFile = new File(context.getRealPath(libFilePath));
			Dir content = Vfs.fromURL(libFile.toURI().toURL());

			Iterator<Vfs.File> files = content.getFiles().iterator();
			while (files.hasNext()) {
				Vfs.File file = files.next();

				for (String resource : jsonResources.getResources()) {

					String resourcePath = resource.replace("*", "");

					if (file.getRelativePath().startsWith(resourcePath)) {
						initDirResources(context.getRealPath(PATH_SEPARATOR), file.getRelativePath());

						int count = 0;
	                	BufferedInputStream bis = new BufferedInputStream(file.openInputStream());
	                	String realFilePath = new File(context.getRealPath(PATH_SEPARATOR)).getPath() + PATH_SEPARATOR + file.getRelativePath();

	                    FileOutputStream fos = new FileOutputStream(realFilePath);
	                    BufferedOutputStream bos = new BufferedOutputStream(fos, STREAM_BUFFER);

	                    byte data[] = new byte[STREAM_BUFFER];
	                    while ((count = bis.read(data, 0, STREAM_BUFFER)) != -1) {
	                    	bos.write(data, 0, count);
	                    }

	                    bos.close();
	                    bis.close();
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
