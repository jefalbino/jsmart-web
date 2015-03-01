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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jsmart5.framework.adapter.ListAdapter;
import com.jsmart5.framework.annotation.ScopeType;
import com.jsmart5.framework.annotation.SmartBean;
import com.jsmart5.framework.config.Constants;
import com.jsmart5.framework.filter.WebFilter;
import com.jsmart5.framework.json.Scroll;
import com.jsmart5.framework.tag.TableAdapterHandler;

import static com.jsmart5.framework.config.Config.*;
import static com.jsmart5.framework.manager.BeanHandler.*;
import static com.jsmart5.framework.manager.TableExpressionHandler.*;
import static com.jsmart5.framework.util.SmartText.*;

public enum ExpressionHandler {

	EXPRESSIONS();
	
	private static final Logger LOGGER = Logger.getLogger(ExpressionHandler.class.getPackage().getName());

	private static final Gson GSON = new Gson();

	Map<String, String> getRequestExpressions() {
		Map<String, String> expressions = new LinkedHashMap<String, String>();
		for (String param : SmartContext.getRequest().getParameterMap().keySet()) {
			String expr = extractExpression(param);
			if (expr != null) {
				expressions.put(param, expr);
			}
		}
		return expressions;
	}

	private String extractExpression(String param) {
		if (param.length() >= TagHandler.J_TAG_LENGTH) {
			String jTag = param.substring(0, TagHandler.J_TAG_LENGTH);

			if (jTag.startsWith(TagHandler.J_TAG_INIT)) {
				return TagEncrypter.complexDecrypt(jTag, param.replace("[]", ""));
			}
		}
		return null;
	}

	String handleRequestExpression(String param, String expr) throws ServletException, IOException {
		try {
			String responsePath = null;
			String jTag = param.substring(0, TagHandler.J_TAG_LENGTH);
	
			if (jTag.equals(TagHandler.J_TAG)) {
				setExpressionValue(expr, param, false);
	
			} else if (jTag.equals(TagHandler.J_ARRAY)) {
				setExpressionValues(expr, param);
	
			} else if (jTag.equals(TagHandler.J_SEL)) {
				setSelectionValue(expr, param);
	
			} else if (jTag.equals(TagHandler.J_TBL_SEL)) {
				setTableSelectionValue(expr, param);
	
			} else if (jTag.equals(TagHandler.J_TBL_EDT)) {
				setTableEditionValue(expr, param);
	
			} else if (jTag.equals(TagHandler.J_FILE)) {
				setExpressionFilePart(expr, param);
	
			} else if (jTag.equals(TagHandler.J_DATE)) {
				setExpressionDate(expr, param);
	
			} else if (jTag.equals(TagHandler.J_CAPTCHA)) {
				setExpressionCaptcha(expr, param);
	
			} else if (jTag.equals(TagHandler.J_SBMT)) {
				responsePath = submitExpression(expr, param);
			}
			return responsePath;

		} catch (PropertyNotWritableException e) {
			LOGGER.log(Level.SEVERE, "Property " + expr + " is not writable");
			throw e;
		}
	}

	String submitExpression(String expr, String param) {
		String responsePath = null;
		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {

			String[] names = expr.replace(Constants.START_EL, "").replace(Constants.END_EL, "").split(Constants.EL_SEPARATOR);
			expr = expr.replace(Constants.START_EL, Constants.JSP_EL);

			Set<Object> objs = getExpressionBeans(expr);
			for (Object obj : objs) {

				// Check authorization to execute method
				if (!HANDLER.checkExecuteAuthorization(obj, expr)) {
					break;
				}

				// Call mapped method with @PreSubmit annotation
				HANDLER.executePreSubmit(obj);

				Object[] arguments = null;
				String[] args = SmartContext.getRequest().getParameterValues(param.replaceFirst(TagHandler.J_SBMT, TagHandler.J_SBMT_ARGS));

				if (args != null) {
					boolean unescape = HANDLER.containsUnescapeMethod(names);
					arguments = new Object[args.length];

					for (int i = 0; i < args.length; i++) {
						arguments[i] = unescape ? args[i] : escapeValue(args[i]);
					}
				}

				// Call submit method
				ELContext context = SmartContext.getPageContext().getELContext();
				MethodExpression methodExpr = SmartContext.getExpressionFactory().createMethodExpression(context, expr, null, 
						arguments != null ? new Class<?>[arguments.length] : new Class<?>[]{});

				responsePath = (String) methodExpr.invoke(context, arguments);

				// Call mapped method with @PostSubmit annotation
				HANDLER.executePostSubmit(obj);

				break;
			}
		}
		return responsePath;
	}

	@SuppressWarnings("all")
	void setSelectionValue(String expr, String param) {
		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {

			String[] names = expr.replace(Constants.START_EL, "").replace(Constants.END_EL, "").split(Constants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				Object object = getExpressionValue(TagEncrypter.complexDecrypt(TagHandler.J_VALUES, SmartContext.getParameter(param)));

				List<Object> list = null;
				Scroll jsonScroll = null;

				if (object instanceof ListAdapter) {
					String scrollParam = SmartContext.getParameter(param.replaceFirst(TagHandler.J_SEL, TagHandler.J_SCROLL));
					jsonScroll = GSON.fromJson(scrollParam, Scroll.class);

					list = ((ListAdapter) object).load(jsonScroll.getIndex(), jsonScroll.getSize());

					// Save the loaded content on request to avoid calling load twice when loading the page
					SmartContext.getRequest().setAttribute(Constants.REQUEST_LIST_ADAPTER, list);

				} else if (object instanceof List<?>) {
					list = (List<Object>) object;
				}

				if (list != null && !list.isEmpty()) {
					expr = expr.replace(Constants.START_EL, Constants.JSP_EL);
					ELContext context = SmartContext.getPageContext().getELContext();

					ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);
					Integer index = Integer.parseInt(SmartContext.getParameter(param.replaceFirst(TagHandler.J_SEL, TagHandler.J_SEL_VAL)));

					// Case scroll list with adapter need to calculate the difference between
					// the first index of the loaded content with the clicked list item index 
					if (jsonScroll != null) {
						index -= jsonScroll.getIndex();
					}
					valueExpr.setValue(context, list.get(index));
				}
			}
		}
	}

	/*
	 * It receives a json structure
	 * {"edit": "", "index": "", "varname": "", "values": [{"name": "", "value": ""}, ...], "first": "", "size": "", "sort": {"name": "", "order": ""}, "filters": [{"name": "", "field": "", "value": ""}, ...]}
	 */
	@SuppressWarnings("all")
	void setTableEditionValue(String expr, String param) throws ServletException, IOException {
		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {
			expr = expr.replace(Constants.START_EL, Constants.JSP_EL);

			// Get json value to retrieve object value from collection or adapter
			JsonObject jsonAction = GSON.toJsonTree(SmartContext.getRequest().getParameter(param)).getAsJsonObject();

			Object object = getExpressionValue(TagEncrypter.complexDecrypt(TagHandler.J_TBL_EDT, getActionEdit(jsonAction)));

			Long first = getActionFirst(jsonAction);
			Long size = getActionSize(jsonAction);
			Integer index = getActionIndex(jsonAction);

			Object value = null;
			
			if (object instanceof TableAdapterHandler) {
				List<Object> list = null;
				Set<Object> objs = getExpressionBeans(expr);
				TableAdapterHandler adapter = (TableAdapterHandler<Object>) object;

				for (Object obj : objs) {
					if (obj.getClass().getAnnotation(SmartBean.class).scope() == ScopeType.REQUEST_SCOPE) {
						list = adapter.loadData(first, size.intValue(), getActionSortBy(jsonAction), 
								getActionSortOrder(jsonAction), getActionFilters(jsonAction));
					} else {
						list = adapter.getLoaded();
					}
					break;
				}

				if (list != null && !list.isEmpty()) {
					value = list.get(index);
				}

	 	 	} else if (object instanceof Collection) {
	 	 		Collection collection = (Collection) object;

	 	 		if (collection != null && !collection.isEmpty()) {
	 	 			Object[] collectionArray = collection.toArray();
	 	 			value = collectionArray[index];
				}
	 	 	}

			if (value != null) {
				String var = getActionVar(jsonAction);
				SmartContext.getRequest().setAttribute(var, value);

				Map<String, String> editValues = getActionEditValues(jsonAction);
				SmartContext.setParameters(editValues);

				for (String editParam : editValues.keySet()) {
					String editExpr = extractExpression(editParam);
					if (editExpr != null) {
						handleRequestExpression(editParam, editExpr);
					}
				}

				ELContext context = SmartContext.getPageContext().getELContext();
				MethodExpression methodExpr = SmartContext.getExpressionFactory().createMethodExpression(context, expr, null, new Class<?>[]{value.getClass()});
				methodExpr.invoke(context, new Object[]{value});

				SmartContext.getRequest().removeAttribute(var);
			}
		}
	}

	/*
	 * It receives a json structure
	 * {"action": "", "type": "SINGLE/MULTI", "indexes": [], "first": "", "size": "", "sort": {"name": "", "order": ""}, "filters": [{"name": "", "field": "", "value": ""}, ...]}
	 */
	@SuppressWarnings("all")
	void setTableSelectionValue(String expr, String param) throws ServletException {
		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {
			expr = expr.replace(Constants.START_EL, Constants.JSP_EL);

			// Get json value to retrieve object value from collection or adapter
			JsonObject jsonAction = GSON.toJsonTree(SmartContext.getRequest().getParameter(param)).getAsJsonObject();

			Object object = getExpressionValue(TagEncrypter.complexDecrypt(TagHandler.J_TBL_SEL, getActionSelect(jsonAction)));

			Long first = getActionFirst(jsonAction);
			Long size = getActionSize(jsonAction);
			SELECT_TYPE selectType = getActionType(jsonAction);
			Integer[] indexes = getActionIndexes(jsonAction);

			List values = new ArrayList(indexes.length);

			if (object instanceof TableAdapterHandler) {
				List<Object> list = null;
				Set<Object> objs = getExpressionBeans(expr);
				TableAdapterHandler adapter = (TableAdapterHandler<Object>) object;

				for (Object obj : objs) {
					if (obj.getClass().getAnnotation(SmartBean.class).scope() == ScopeType.REQUEST_SCOPE) {
						list = adapter.loadData(first, size.intValue(), getActionSortBy(jsonAction), 
								getActionSortOrder(jsonAction), getActionFilters(jsonAction));
					} else {
						list = ((TableAdapterHandler<Object>) object).getLoaded();
					}
					break;
				}

				if (list != null && !list.isEmpty()) {
					for (int i = 0; i < indexes.length; i++) {
						values.add(list.get(indexes[i]));
					}
				}

	 	 	} else if (object instanceof Collection) {
	 	 		Collection collection = (Collection) object;

	 	 		if (collection != null && !collection.isEmpty()) {
	 	 			Object[] collectionArray = collection.toArray();

	 	 			for (int i = 0; i < indexes.length; i++) {
	 	 				values.add(collectionArray[indexes[i]]);
	 	 			}
				}
	 	 	}

			// SmartContext.setSelectIndexes(indexes);
			ELContext context = SmartContext.getPageContext().getELContext();

			if (selectType == SELECT_TYPE.MULTI) {
				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, List.class);
				valueExpr.setValue(context, values);

			} else if (!values.isEmpty()) {				
				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, values.get(0).getClass());
				valueExpr.setValue(context, values.get(0));
			}
		}
	}

	void setExpressionValue(String expr, String param, boolean isUrl) {
		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {

			if (isReadOnlyParameter(param)) {
				return;
			}

			String[] names = expr.replace(Constants.START_EL, "").replace(Constants.END_EL, "").split(Constants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				expr = expr.replace(Constants.START_EL, Constants.JSP_EL);
				ELContext context = SmartContext.getPageContext().getELContext();

				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);

				Object val = SmartContext.getParameter(param);

				if (!HANDLER.containsUnescapeMethod(names)) {
					val = escapeValue((String) val);
				}

				if (isUrl) {
					val = decodeUrl(val);
				}

				valueExpr.setValue(context, val);
			}
		}
	}

	void setAttributeValue(String expr, Object value) {
		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {

			String[] names = expr.replace(Constants.START_EL, "").replace(Constants.END_EL, "").split(Constants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				expr = expr.replace(Constants.START_EL, Constants.JSP_EL);
				ELContext context = SmartContext.getPageContext().getELContext();

				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);
				valueExpr.setValue(context, value);
			}
		}
	}

	void setExpressionValues(String expr, String param) {
		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {

			if (isReadOnlyParameter(param)) {
				return;
			}

			String[] names = expr.replace(Constants.START_EL, "").replace(Constants.END_EL, "").split(Constants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				expr = expr.replace(Constants.START_EL, Constants.JSP_EL);

				List<Object> list = new ArrayList<Object>();
				String[] values = SmartContext.getRequest().getParameterValues(param);

				boolean unescape = HANDLER.containsUnescapeMethod(names);

				if (values != null) {
					for (String val : values) {
						list.add(unescape ? val : escapeValue(val));
					}
				}

				// Check for empty value sent on array [false]
				if (list.size() == 1 && list.get(0) != null && list.get(0).equals("false")) {
					list.clear();
				}

				ELContext context = SmartContext.getPageContext().getELContext();
				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);
				valueExpr.setValue(context, list);
			}
		}
	}

	void setExpressionFilePart(String expr, String file) throws ServletException, IOException {
		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {

			String[] names = expr.replace(Constants.START_EL, "").replace(Constants.END_EL, "").split(Constants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				expr = expr.replace(Constants.START_EL, Constants.JSP_EL);
				ELContext context = SmartContext.getPageContext().getELContext();

				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);
				Object val = SmartContext.getRequest().getPart(file.replaceFirst(TagHandler.J_FILE, TagHandler.J_PART));
				valueExpr.setValue(context, val);
			}
		}
	}

	void setExpressionDate(String expr, String date) throws ServletException {
		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {

			String[] names = expr.replace(Constants.START_EL, "").replace(Constants.END_EL, "").split(Constants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				expr = expr.replace(Constants.START_EL, Constants.JSP_EL);
				ELContext context = SmartContext.getPageContext().getELContext();

				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);

				String val = SmartContext.getParameter(date);

				if (!val.trim().isEmpty()) {
					String format = SmartContext.getParameter(date.replaceFirst(TagHandler.J_DATE, TagHandler.J_FRMT));

					try {
						// First try with jdk Date
						SimpleDateFormat sdf = new SimpleDateFormat(format, SmartContext.getLocale());
						valueExpr.setValue(context, sdf.parse(val));

					} catch (Exception ex) {
						try {
							// Second try with jodatime DateTime
							DateTimeFormatter dtf = DateTimeFormat.forPattern(format).withLocale(SmartContext.getLocale());
							valueExpr.setValue(context, DateTime.parse(val, dtf));

						} catch (Exception ex1) {
							throw new ServletException(ex1.getMessage());
						}
					}
				} else {
					valueExpr.setValue(context, null);
				}
			}
		}
	}

	void setExpressionCaptcha(String expr, String param) throws ServletException {
		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {

			String[] names = expr.replace(Constants.START_EL, "").replace(Constants.END_EL, "").split(Constants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				expr = expr.replace(Constants.START_EL, Constants.JSP_EL);
				ELContext context = SmartContext.getPageContext().getELContext();

				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);

				String value = SmartContext.getParameter(param);
				String hashValue = SmartContext.getParameter(param.replaceFirst(TagHandler.J_CAPTCHA, TagHandler.J_CAPTCHA_HASH));

				valueExpr.setValue(context, computeCaptchaHash(value).equals(hashValue));
			}
		}
	}

	public Object getExpressionValue(Object expr) {
		if (expr != null) {
			String exprString = expr.toString();
			Matcher matcher = Constants.EL_PATTERN.matcher(exprString);
			List<Object> list = new ArrayList<Object>();

			while (matcher.find()) {
				String group = matcher.group();
				list.add(evaluateExpression(group));
				exprString = exprString.replace(group, "%s");
			}

			if (list.isEmpty()) {
				return expr;
			} else {
				if (exprString.equals("%s")) {
					return list.get(0);
				} else {
					return String.format(exprString, list.toArray());
				}
			}
		}
		return null;
	}

	private boolean isReadOnlyParameter(String param) {
		if (param != null) {
			return param.endsWith(Constants.EL_PARAM_READ_ONLY);
		}
		return false;
	}

	private Object evaluateExpression(String expr) {
		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {

			expr = expr.replace(Constants.START_EL, Constants.JSP_EL);
			ELContext context = SmartContext.getPageContext().getELContext();

			ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);
			Object obj = valueExpr.getValue(context);

			if (obj instanceof String) {
				String[] objs = obj.toString().split(Constants.EL_SEPARATOR, 2);
				if (objs.length == 2 && TEXTS.containsResource(objs[0])) {
					return TEXTS.getString(objs[0], objs[1]);
				}
			}

			if (obj != null) {
				return obj;
			}

			String[] exprs = expr.replace(Constants.JSP_EL, "").replace(Constants.END_EL, "").split(Constants.EL_SEPARATOR, 2);
			if (exprs.length == 2 && TEXTS.containsResource(exprs[0])) {
				return TEXTS.getString(exprs[0], exprs[1]);
			}

			return null;
		}
		return expr;
	}

	Object getResourceValue(String expr) {
		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {

			String[] exprs = expr.replace(Constants.START_EL, "").replace(Constants.END_EL, "").split(Constants.EL_SEPARATOR, 2);

			if (exprs.length == 2 && TEXTS.containsResource(exprs[0])) {
				return TEXTS.getString(exprs[0], exprs[1]);
			}
		}
		return expr;
	}

	private Set<Object> getExpressionBeans(String expr) {
		Set<Object> objs = new HashSet<Object>();

		if (expr != null && !expr.trim().isEmpty()) {

			Set<String> names = new HashSet<String>(HANDLER.smartBeans.keySet());
			names.addAll(HANDLER.authBeans.keySet());

			for (String name : names) {
				if (expr.contains(name + Constants.POINT)) {
					Object attribute = SmartContext.getAttribute(name);
					if (attribute != null) {
						objs.add(attribute);
					}
				}
			}

		}
		return objs;
	}

	private Object escapeValue(String value) {
		if (value != null && CONFIG.getContent().isEscapeRequest()) {
			value = StringEscapeUtils.escapeJavaScript(value);
			value = StringEscapeUtils.escapeHtml(value);
			//value = StringUtils.replaceEach(value, new String[]{"&", "\"", "<", ">", "$", "#"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;", "&#36;", "&#35;"});
		}
    	return value;
    }

	private Object decodeUrl(Object value) {
		if (value instanceof String) {
			try {
				return URLDecoder.decode(String.valueOf(value), WebFilter.ENCODING);
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
		}
		return value;
	}

	private String computeCaptchaHash(String value) {
		int hash = 5381;
		value = value.toUpperCase();
		for(int i = 0; i < value.length(); i++) {
			hash = ((hash << 5) + hash) + value.charAt(i);
		}
		return String.valueOf(hash);
	}

}
