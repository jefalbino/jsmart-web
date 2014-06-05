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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.reflections.vfs.Vfs;
import org.reflections.vfs.Vfs.Dir;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

import static com.jsmart5.framework.manager.SmartHandler.*;
import static com.jsmart5.framework.manager.SmartConstants.*;

public final class SmartWebFilter implements Filter {

	/*package*/ static final String ENCODING = "UTF-8";

	private static final int STREAM_BUFFER = 2048;

	private static final Logger LOGGER = Logger.getLogger(SmartWebFilter.class.getPackage().getName());

	private static final Pattern HTML_PATTERN = Pattern.compile("<html.*?>");

	private static final Pattern START_HEAD_PATTERN = Pattern.compile("<head.*?>");

	private static final Pattern SCRIPT_HEAD_PATTERN = Pattern.compile("<script.*?>");

	private static final Pattern CLOSE_HEAD_PATTERN = Pattern.compile("</head.*?>");

	private static final Pattern CLOSE_BODY_PATTERN = Pattern.compile("</body.*?>");

	private static final Pattern HEAD_CONTENT_PATTERN = Pattern.compile("<head.*?</head.*?>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

	private static final Pattern FORM_PATTERN = Pattern.compile("<form.*?>");

	private static final Pattern CSS_MARK_PATTERN = Pattern.compile("@.{1,20}?@");

	private static JSONObject jsonResources;

	private static JSONObject jsonHeaders;

	private static JSONObject jsonStyles;

	@Override
	public void init(FilterConfig config) throws ServletException {
		initJsonResources();
		initBeanResources(config.getServletContext());
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

		// Remove script builder
		httpRequest.getSession().removeAttribute(SCRIPT_BUILDER_ATTR);

		// Anonymous subclass to wrap HTTP response to print output
		SmartHttpServletResponseWrapper responseWrapper = new SmartHttpServletResponseWrapper(httpResponse);

        Throwable throwable = null;

        try {
        	filterChain.doFilter(request, responseWrapper);
        } catch (Throwable thrown) {
        	throwable = thrown;
        	thrown.printStackTrace();
        }

        // Finalize request scoped beans
        HANDLER.finalizeBeans(httpRequest);

        // Include head data to html
        String html = null;

        try {
        	html = getCompleteHtml(httpRequest, responseWrapper);
        } catch (JSONException ex) {
        	throw new ServletException(ex);
        }

        // Close bean context based on current thread instance
        SmartContext.closeCurrentInstance();

        // Case internal server error
		if (throwable != null) {
			// Remove script builder
			httpRequest.getSession().removeAttribute(SCRIPT_BUILDER_ATTR);

			responseWrapper.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

			if (throwable instanceof IOException) {
				throw new IOException(throwable);
			}
			throw new ServletException(throwable);
		}

		if (SmartConfig.CONFIG.getContent().isPrintHtml()) {
        	LOGGER.log(Level.INFO, html);
        }

        // Compress html to better load performance
		SmartHtmlCompress compressHtml = SmartConfig.CONFIG.getContent().getCompressHtml();
		if (compressHtml.isCompressHtml()) {
			HtmlCompressor compressor = new HtmlCompressor();
	    	compressor.setRemoveComments(!compressHtml.isSkipComments());
	    	html = compressor.compress(html);
		}

		// Write our modified text to the real response
		if (!responseWrapper.isCommitted()) {
	        response.setContentLength(html.getBytes().length);
	        PrintWriter out = response.getWriter();
	        out.write(html);
	        out.close();
		}
	}

	private String getCompleteHtml(HttpServletRequest httpRequest, HttpServletResponseWrapper responseWrapper) throws JSONException {
		String html = responseWrapper.toString();
        Matcher htmlMatcher = HTML_PATTERN.matcher(html);

		// Check if it is a valid html
        if (htmlMatcher.find()) {

			// Try to place the css as the first link in the head tag
        	Matcher startHeadMatcher = START_HEAD_PATTERN.matcher(html);
		    if (startHeadMatcher.find()) {
		    	String startHeadMatch = startHeadMatcher.group();
		    	html = html.replaceFirst(startHeadMatch, startHeadMatch + jsonHeaders.getString("style"));

		    	// Try to place the javascript as the first script before no other inside head content
		    	Matcher headContentMatcher = HEAD_CONTENT_PATTERN.matcher(html);
		    	if (headContentMatcher.find()) {
		    		String headContentMatch = headContentMatcher.group();
		    		
		    		Matcher scriptHeadMatcher = SCRIPT_HEAD_PATTERN.matcher(headContentMatch);
					if (scriptHeadMatcher.find()) {
						String scriptHeadMatch = scriptHeadMatcher.group();
						html = html.replaceFirst(scriptHeadMatch, jsonHeaders.getString("script") + scriptHeadMatch);

					} else {
						Matcher closeHeadMatcher = CLOSE_HEAD_PATTERN.matcher(html);
						if (closeHeadMatcher.find()) {
							String closeHeadMatch = closeHeadMatcher.group();
							html = html.replaceFirst(closeHeadMatch, jsonHeaders.getString("script") + closeHeadMatch);
						}
					}
		    	}

		    } else {
		    	String htmlMatch = htmlMatcher.group();
		    	html = html.replaceFirst(htmlMatch, htmlMatch + START_HEAD_TAG + jsonHeaders.getString("style") + jsonHeaders.getString("script") + END_HEAD_TAG);
		    }

	        // Case redirect via ajax, place tag with path to be handled b javascript
			String ajaxPath = (String) httpRequest.getSession().getAttribute(AJAX_ATTR);
			if (ajaxPath != null) {

				Matcher formMatcher = FORM_PATTERN.matcher(html);
				if (formMatcher.find()) {
					String formMatch = formMatcher.group();
					html = html.replace(formMatch, formMatch + REDIRECT_AJAX_TAG + ajaxPath + END_AJAX_TAG);
				}

				httpRequest.getSession().removeAttribute(AJAX_ATTR);
			}

			// Case reset via ajax, place tag to force javascript reset the page
			if (httpRequest.getSession().getAttribute(AJAX_RESET_ATTR) != null) {
				if (ajaxPath == null && SmartContext.isAjaxRequest()) {

					Matcher formMatcher = FORM_PATTERN.matcher(html);
					if (formMatcher.find()) {
						String formMatch = formMatcher.group();
						html = html.replaceFirst(formMatch, formMatch + RESET_AJAX_TAG);
					}
				}

				httpRequest.getSession().removeAttribute(AJAX_RESET_ATTR);
	        }

			StringBuilder[] scriptBuilders = (StringBuilder[]) httpRequest.getSession().getAttribute(SCRIPT_BUILDER_ATTR);
			if (scriptBuilders != null) {
				Matcher closeBodyMatcher = CLOSE_BODY_PATTERN.matcher(html);

				if (closeBodyMatcher.find()) {
					String closeBodyMatch = closeBodyMatcher.group();
//					ClosureJavaScriptCompressor compressor = new ClosureJavaScriptCompressor();
					String compressedScript = String.format(SCRIPT_READY_AJAX_TAG, scriptBuilders[0].append(scriptBuilders[1]));
					html = html.replace(closeBodyMatch, compressedScript + closeBodyMatch);
				}
			}
        }

		// Remove script builder
        httpRequest.getSession().removeAttribute(SCRIPT_BUILDER_ATTR);
		return html;
	}

	private void initJsonResources() {
		try {
			jsonStyles = new JSONObject(convertResourceToString(FILTER_STYLES));
			jsonResources = new JSONObject(convertResourceToString(FILTER_RESOURCES));
			jsonHeaders = new JSONObject(convertResourceToString(FILTER_HEADERS));
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Failure to load resources: " + ex.getMessage(), ex);
		}
	}

	@SuppressWarnings("resource")
	private String convertResourceToString(String resource) {
		InputStream is = SmartWebFilter.class.getClassLoader().getResourceAsStream(resource);
		Scanner scanner = new Scanner(is).useDelimiter("\\A");
		return scanner.hasNext() ? scanner.next() : "";
	}

	private void initBeanResources(ServletContext context) {
		try {
			File rootFile = new File(context.getRealPath(JAR_FILE_PATH));
			Dir content = Vfs.fromURL(rootFile.toURI().toURL());

			Iterator<Vfs.File> files = content.getFiles().iterator();
			while (files.hasNext()) {
				Vfs.File file = files.next();

				JSONArray resources = jsonResources.getJSONArray("resources");
				for (int i = 0; i < resources.length(); i++) {

					if (resources.getString(i).equals(file.getRelativePath())) {
						initDirResources(context.getRealPath(SEPARATOR), file.getRelativePath());

						// Replace style marks on CSS file according to the theme defined on config file
						JSONObject cssMarks = null;

						if (JSMART5_CSS_PATH.equals(file.getRelativePath())) {
							if (jsonStyles.has(SmartConfig.CONFIG.getContent().getTheme())) {
								cssMarks = jsonStyles.getJSONObject(SmartConfig.CONFIG.getContent().getTheme());
							} else {
								cssMarks = jsonStyles.getJSONObject(SmartConfig.CONFIG.getContent().getDefaultTheme());
							}
						}

	                	BufferedInputStream bis = new BufferedInputStream(file.openInputStream());

	                	int count = 0;
	                	String realFilePath = new File(context.getRealPath(SEPARATOR)).getPath() + SEPARATOR + file.getRelativePath();

	                    FileOutputStream fos = new FileOutputStream(realFilePath);
	                    BufferedOutputStream bos = new BufferedOutputStream(fos, STREAM_BUFFER);

                    	// Replace style marks on CSS file
                    	if (cssMarks != null) {
                    		byte data[] = new byte[STREAM_BUFFER];

    	                    while ((count = bis.read(data, 0, STREAM_BUFFER)) != -1) {
    	                    	String cssData = new String(data, 0, count);
                        		Matcher cssMatcher = CSS_MARK_PATTERN.matcher(cssData);

    							while (cssMatcher.find()) {
    								String match = cssMatcher.group();
    								if (cssMarks.has(match)) {
    									cssData = cssData.replace(match, cssMarks.getString(match));
    								}
    							}
    							bos.write(cssData.getBytes(), 0, cssData.length());
    	                    }
                    	} else {
    	                    byte data[] = new byte[STREAM_BUFFER];
    	                    while ((count = bis.read(data, 0, STREAM_BUFFER)) != -1) {
    	                    	bos.write(data, 0, count);
    	                    }
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
		if (relativePath.contains(SEPARATOR)) {
			String[] paths = relativePath.split(SEPARATOR);

			for (int i = 0; i < paths.length - 1; i++) {
				currentPath += SEPARATOR + paths[i];

				File dir = new File(currentPath);
				if (!dir.exists()) {
					dir.mkdir();
				}
			}
		}
	}

	private class SmartHttpServletResponseWrapper extends HttpServletResponseWrapper {

		private StringWriter writer;

		private ServletOutputStreamWrapper outputWrapper;

		public SmartHttpServletResponseWrapper(HttpServletResponse httpResponse) {
			super(httpResponse);
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			if (writer != null) {
				throw new IllegalStateException("getWriter() has already been called on this response.");
			}
			if (outputWrapper == null) {
				outputWrapper = new ServletOutputStreamWrapper(getResponse().getOutputStream());
			}
			return outputWrapper;
		}

		@Override
        public PrintWriter getWriter() throws IOException {
			if (outputWrapper != null) {
	            throw new IllegalStateException("getOutputStream() has already been called on this response.");
	        }
	        if (writer == null) {
	            writer = new StringWriter();
	        }
            return new PrintWriter(writer, true);
        }

		@Override
		public void reset() {
			if (writer != null) {
                writer = new StringWriter();
            } else if (outputWrapper != null) {
            	try {
            		outputWrapper = new ServletOutputStreamWrapper(getResponse().getOutputStream());
            	} catch (IOException ex) {
            		LOGGER.log(Level.SEVERE, "Error while reseting outputStream wrapper.", ex);
            	}
            }
		}

		@Override
        public String toString() {
    		if (writer != null) {
                return writer.toString();
            } else if (outputWrapper != null) {
            	return outputWrapper.toString();
            }
    		return null;
        }
	}

	private class ServletOutputStreamWrapper extends ServletOutputStream {

		private ServletOutputStream outputStream;

		public ServletOutputStreamWrapper(ServletOutputStream outputStream) {
			this.outputStream = outputStream;
		}

		@Override
		public void write(int b) throws IOException {
			outputStream.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			outputStream.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			outputStream.write(b, off, len);
		}

		@Override
		public String toString() {
			return outputStream.toString();
		}
	}

}