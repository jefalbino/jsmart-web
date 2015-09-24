/*
 * JSmart5 - Java Web Development Framework
 * Copyright (c) 2014, Jeferson Albino da Silva, All rights reserved.
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

package com.jsmart5.framework.filter;

import com.jsmart5.framework.config.ContentEncode;

import javax.servlet.DispatcherType;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import static com.jsmart5.framework.config.Config.CONFIG;
import static com.jsmart5.framework.manager.BeanHandler.HANDLER;

public final class EncodeFilter implements Filter {

	private static final Logger LOGGER = Logger.getLogger(EncodeFilter.class.getPackage().getName());

	private static final String ACCEPT_ENCODING = "Accept-Encoding";

	private static final String CONTENT_ENCODING = "Content-Encoding";

	private static final String VARY = "Vary";

	private static final String GZIP = "gzip";

	private static final String DEFLATE = "deflate";

	private static final int BUFFER_SIZE = 8192;

	private static final int DEFLATE_COMPRESSION_LEVEL = Deflater.DEFAULT_COMPRESSION;

	private ServletContext servetContext;

	@Override
	public void init(FilterConfig config) throws ServletException {
		servetContext = config.getServletContext();
	}

	@Override
	public void destroy() {
		// DO NOTHING
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		// Dispatcher Forward not supposed to get in this Filter
		if (httpRequest.getDispatcherType() == DispatcherType.FORWARD) {
			filterChain.doFilter(httpRequest, httpResponse);
        	return;
		}

		ContentEncode contentEncode = CONFIG.getContent().getContentEncode();

		if (!httpResponse.isCommitted() && contentEncode != null && isMimeTypeAccepted(httpRequest, contentEncode)) {
			String accepted = getAcceptedEncoding(httpRequest, contentEncode);

			DeflaterOutputStream gzipOutputStream = null;
            ByteArrayOutputStream compressedByteArray = new ByteArrayOutputStream();

            if (GZIP.equals(accepted)) {
            	gzipOutputStream = new GZIPOutputStream(compressedByteArray, BUFFER_SIZE);
            } else if (DEFLATE.equals(accepted)) {
            	gzipOutputStream = new DeflaterOutputStream(compressedByteArray, new Deflater(DEFLATE_COMPRESSION_LEVEL, true), BUFFER_SIZE);
            }

            if (gzipOutputStream == null) {
            	LOGGER.log(Level.SEVERE, "Encoding type [" + contentEncode.getEncode() + "] not supported for compression");
            	filterChain.doFilter(httpRequest, httpResponse);
            	return;
            }

        	SmartEncodingResponseWrapper responseWrapper = new SmartEncodingResponseWrapper(httpResponse, gzipOutputStream);

            filterChain.doFilter(request, responseWrapper);

            // Flush and close current outputStream on responseWrapper
            responseWrapper.flushBuffer();
            gzipOutputStream.close();

            // If server throws any error mapped by customer just return
            if (CONFIG.getContent().getErrorPage(responseWrapper.getStatus()) != null) {
            	return;
            }

            if (responseWrapper.getStatus() == HttpServletResponse.SC_NO_CONTENT 
            		|| responseWrapper.getStatus() ==  HttpServletResponse.SC_RESET_CONTENT
            		|| responseWrapper.getStatus() == HttpServletResponse.SC_NOT_MODIFIED) {
            	return;
            }

            if (!httpResponse.isCommitted()) {
            	try {
	                byte[] compressedBytes = compressedByteArray.toByteArray();
	                httpResponse.setHeader(CONTENT_ENCODING, accepted);
	                httpResponse.addHeader(VARY, "Accept-Encoding, User-Agent");
	                httpResponse.setContentLength(compressedBytes.length);
	                httpResponse.getOutputStream().write(compressedBytes);
            	} catch (IOException ex) {
            		LOGGER.log(Level.WARNING, "Exception on write the compressed response: " + ex.getMessage());
            	}
            }
		} else {
			filterChain.doFilter(httpRequest, httpResponse);
		}
	}

	private boolean isMimeTypeAccepted(HttpServletRequest httpRequest, ContentEncode contentEncode) {
		String requestMimeType = servetContext.getMimeType(httpRequest.getRequestURI());
		if (contentEncode.getMimeTypes() != null) {

			if (contentEncode.getMimeTypes().length == 1 && contentEncode.getMimeTypes()[0].equals("*")) {
				return true;
			}

			if (requestMimeType != null) {
				for (String mimeType : contentEncode.getMimeTypes()) {
					if (mimeType.endsWith("/*")) {
						if (requestMimeType.startsWith(mimeType.substring(0, mimeType.lastIndexOf("/*")))) {
							return true;
						}
					} else if (mimeType.equalsIgnoreCase(requestMimeType)) {
						return true;
					}
				}
			} else {
				String path = httpRequest.getServletPath();
				if (HANDLER.getForwardPath(path) != null) {
					for (String mimeType : contentEncode.getMimeTypes()) {
						if (mimeType.equalsIgnoreCase("text/html") || mimeType.equalsIgnoreCase("text/*")) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private String getAcceptedEncoding(HttpServletRequest httpRequest, ContentEncode contentEncode) {
		if (contentEncode.getEncode() != null) {
			Enumeration<String> accepted = httpRequest.getHeaders(ACCEPT_ENCODING);

			while (accepted.hasMoreElements()) {
				String headerValue = (String) accepted.nextElement();
				if (headerValue.contains(GZIP) && contentEncode.getEncode().contains(GZIP)) {
					return GZIP;
				} else if (headerValue.contains(DEFLATE) && contentEncode.getEncode().contains(DEFLATE)) {
					return DEFLATE;
				}
			}
		}
		return null;
	}

	private class SmartEncodingResponseWrapper extends HttpServletResponseWrapper {

		private PrintWriter writer;

		private final ServletOutputStream outputStream;

		public SmartEncodingResponseWrapper(HttpServletResponse servletResponse, DeflaterOutputStream outputStream) {
			super(servletResponse);
			this.outputStream = new SmartEncodingServletOutputStream(outputStream);
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return outputStream;
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			if (writer == null) {
	            writer = new PrintWriter(new OutputStreamWriter(outputStream, getCharacterEncoding()), true);
	        }
	        return writer;
		}

		@Override
		public void flushBuffer() throws IOException {
			if (writer != null) {
	            writer.flush();
	        }
	        outputStream.flush();
	    }
	}

	private class SmartEncodingServletOutputStream extends ServletOutputStream {

	    private final OutputStream outputStream;
	    
	    private WriteListener writeListener;

	    public SmartEncodingServletOutputStream(final OutputStream outputStream) {
	        this.outputStream = outputStream;
	    }

	    public void write(final int b) throws IOException {
	    	try {
		    	outputStream.write(b);
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

	    public void write(final byte[] b) throws IOException {
	    	try {
		    	outputStream.write(b);
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

	    public void write(final byte[] b, final int off, final int len) throws IOException {
	    	try {
	    		outputStream.write(b, off, len);
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
			outputStream.flush();
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
