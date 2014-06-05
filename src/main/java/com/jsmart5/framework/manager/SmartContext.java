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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

import com.jsmart5.framework.annotation.PostConstruct;
import com.jsmart5.framework.annotation.SmartBean;

/**
 * This class represents the context of the request being currently processed and it allows {@link SmartBean}
 * to get an instance of {@link ServletContext}, {@link HttpSession}, {@link HttpServletRequest} or 
 * {@link HttpServletResponse}.
 * <br>
 * This class also include methods to add message to client side, check if request is Ajax request or 
 * retrieve attributes from the request, session or application.
 */
public final class SmartContext {

	private static final JspFactory JSP_FACTORY = JspFactory.getDefaultFactory();

	private static final Map<Thread, SmartContext> THREADS = new HashMap<Thread, SmartContext>();

	private static Servlet smartServlet;

	private static JspApplicationContext jspContext;

	private HttpServletRequest request;

	private HttpServletResponse response;

	private String redirectTo;

	private boolean invalidate;

	private PageContext pageContext;

	private Map<String, SmartMessage> messages = new LinkedHashMap<String, SmartMessage>();

	private Map<String, Map<String, SmartMessage>> fixedMessages = new LinkedHashMap<String, Map<String, SmartMessage>>();

	private Map<String, String> parameters = new HashMap<String, String>();

	private String selectIndexes = new String();

	private boolean editItemTagEnabled; 

	private SmartContext(final HttpServletRequest request, final HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	/*package*/ static final void setServlet(final Servlet servlet) {
		smartServlet = servlet;
		jspContext = JSP_FACTORY.getJspApplicationContext(servlet.getServletConfig().getServletContext());
	}

	private static final SmartContext getCurrentInstance() {
		return THREADS.get(Thread.currentThread());
	}

	/*package*/ static final void initCurrentInstance(final HttpServletRequest request, final HttpServletResponse response) {
		THREADS.put(Thread.currentThread(), new SmartContext(request, response));
	}

	/*package*/ static final void closeCurrentInstance() {
		THREADS.remove(Thread.currentThread()).close();
	}

	private void close() {
		if (invalidate) {
			request.getSession().invalidate();
		}
		invalidate = false;
		request = null;
		response = null;
		redirectTo = null;
		messages.clear();
		messages = null;
		fixedMessages.clear();
		fixedMessages = null;
		parameters.clear();
		parameters = null;
		selectIndexes = null;
		editItemTagEnabled = false;
		JSP_FACTORY.releasePageContext(pageContext);
		pageContext = null;
	}

	/*package*/ static PageContext getPageContext() {
		SmartContext context = getCurrentInstance();
		return context != null ? context.getPage() : null;
	}

	private PageContext getPage() {
		if (pageContext == null) {
			pageContext = JSP_FACTORY.getPageContext(smartServlet, request, response, null, true, 8192, true);
		}
		return pageContext;
	}

	/*package*/ static ExpressionFactory getExpressionFactory() {
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
		SmartContext context = getCurrentInstance();
		return context != null ? context.request.getSession() : null;
	}

	/**
	 * Returns the current {@link HttpServletRequest} instance associated to the request being
	 * processed.
	 * 
	 * @return a instance of {@link HttpServletRequest}.
	 */
	public static HttpServletRequest getRequest() {
		SmartContext context = getCurrentInstance();
		return context != null ? context.request : null;
	}

	/**
	 * Returns the current {@link HttpServletResponse} instance associated to the request 
	 * being processed.
	 * 
	 * @return a instance of {@link HttpServletResponse}
	 */
	public static HttpServletResponse getResponse() {
		SmartContext context = getCurrentInstance();
		return context != null ? context.response : null;
	}

	/*package*/ static String getRedirectTo() {
		SmartContext context = getCurrentInstance();
		return context != null ? context.redirectTo : null;
	}

	/**
	 * Redirect the request to the specified link path after the current request is processed.
	 * <br>
	 * Case this method is called on {@link PostConstruct} annotated method, the redirect is done after the
	 * {@link PostConstruct} annotated method execution.
	 * 
	 * @param path path mapped on configuration file or general valid URL link.
	 */
	public static void redirectTo(final String path) {
		SmartContext context = getCurrentInstance();
		if (context != null) {
			context.redirectTo = SmartUtils.decodePath(path);
		}
	}

	/**
	 * Calling this method will cause the current {@link HttpSession} to be invalidated after the request
	 * processing is done. It means that the session will be invalidated after {@link SmartBean} life cycle
	 * is completed.
	 * <br>
	 * Case there is a need to invalidate the session at the moment of the execution, use {@link HttpSession}
	 * invalidate method instead.  
	 */
	public static void invalidate() {
		SmartContext context = getCurrentInstance();
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
	 * Returns <code>true</code> if the request being process was triggered by Ajax on client side,
	 * <code>false</code> otherwise.
	 * 
	 * @return boolean value indicating if request was done using Ajax.
	 */
	public static boolean isAjaxRequest() {
		HttpServletRequest request = getRequest();
		return request != null ? "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) : false;
	}

	/*package*/ static Map<String, SmartMessage> getMessages() {
		SmartContext context = getCurrentInstance();
		return context != null ? context.messages : null;
	}

	/**
	 * Add message by type category to be presented on client side after the response is returned. 
	 * <br>
	 * This method only take effect if the message tag is mapped on the page returned to the client.
	 * 
	 * @param type category type of the message.
	 * @param message message to be presented on the client side.
	 */
	public static void addMessage(final SmartMessage type, final String message) {
		SmartContext context = getCurrentInstance();
		if (context != null && message != null && type != null) {
			context.messages.put(message, type);
		}
	}

	/*package*/ static Map<String, SmartMessage> getMessages(final String id) {
		SmartContext context = getCurrentInstance();
		return context != null ? context.fixedMessages.get(id) : null;
	}

	/**
	 * Add message by type category to be presented on client side after the response is returned. 
	 * <br>
	 * This method only take effect if the message tag is mapped with specified id and with position fixed
	 * on the page returned to the client.
	 * <br>
	 * The message is placed on the same position where the the message tag is mapped.
	 * 
	 * @param id message tag id to receive the message.
	 * @param type category type of the message.
	 * @param message message to be presented on the client side.
	 */
	public static void addMessage(final String id, final SmartMessage type, final String message) {
		SmartContext context = getCurrentInstance();
		if (context != null && id != null && message != null && type != null) {
			Map<String, SmartMessage> messages = context.fixedMessages.get(id);
			if (messages == null) {
				context.fixedMessages.put(id, messages = new LinkedHashMap<String, SmartMessage>());
			}
			messages.put(message, type);
		}
	}

	/*package*/ static String getParameter(final String name) {
		SmartContext context = getCurrentInstance();
		if (context != null) {
			String object = context.parameters.get(name);
			if (object == null) {
				object = context.request.getParameter(name);
			}
			return object;
		}
		return null;
	}

	/*package*/ static Map<String, String> getParameters() {
		SmartContext context = getCurrentInstance();
		return context != null ? context.parameters : null;
	}

	/*package*/ static void setParameters(final Map<String, String> parameters) {
		SmartContext context = getCurrentInstance();
		if (context != null) {
			context.parameters.putAll(parameters);
		}
	}

	/*package*/ static String getSelectIndexes() {
		SmartContext context = getCurrentInstance();
		return context != null ? context.selectIndexes : null;
	}

	/*package*/ static void setSelectIndexes(final Integer[] selectIndexes) {
		SmartContext context = getCurrentInstance();
		if (context != null) {
			for (int i = 0; i < selectIndexes.length; i++) {
				context.selectIndexes += selectIndexes[i] + (i < selectIndexes.length -1 ? "," : "");
			}
		}
	}

	/*package*/ static boolean isEditItemTagEnabled() {
		SmartContext context = getCurrentInstance();
		return context != null ? context.editItemTagEnabled : false;
	}

	/*package*/ static void setEditItemTagEnabled(final boolean editItemTagEnabled) {
		SmartContext context = getCurrentInstance();
		if (context != null) {
			context.editItemTagEnabled = editItemTagEnabled;
		}
	}

	/**
	 * Returns the attribute carried on {@link HttpServletRequest}, {@link HttpSession} or {@link ServletContext}
	 * instances associated with current request being processed.
	 * 
	 * @param name name of the attribute.
	 * @return the {@link Object} mapped by attribute name on the current request.
	 */
	public static Object getAttribute(final String name) {
		if (name != null) {
			HttpServletRequest request = getRequest();
			if (request != null && request.getAttribute(name) != null) {
				return request.getAttribute(name);

			} else {
				HttpSession session = getSession();
				if (session != null && session.getAttribute(name) != null) {
					return session.getAttribute(name);

				} else {
					ServletContext application = getApplication();
					if (application.getAttribute(name) != null) {
						return application.getAttribute(name);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Check if attribute is carried on {@link HttpServletRequest}, {@link HttpSession} or {@link ServletContext}
	 * instances associated with current request being processed.
	 * 
	 * @param name name of the attribute.
	 * @return <code>true</code> if the attribute is contained in one of the instances {@link HttpServletRequest}, 
	 * {@link HttpSession} or {@link ServletContext}, <code>false</code> otherwise.
	 */
	public static boolean containsAttribute(final String name) {
		if (name != null) {
			HttpServletRequest request = getRequest();
			if (request != null && request.getAttribute(name) != null) {
				return true;
			}
			HttpSession session = getSession();
			if (session != null && session.getAttribute(name) != null) {
				return true;
			}
			return getApplication().getAttribute(name) != null;
		}
		return false;
	}

}