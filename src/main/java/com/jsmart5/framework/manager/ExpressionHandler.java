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

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.jsmart5.framework.adapter.ListAdapter;

import com.jsmart5.framework.config.Constants;
import com.jsmart5.framework.filter.WebFilter;
import com.jsmart5.framework.json.Scroll;

import com.jsmart5.framework.util.SmartText;
import static com.jsmart5.framework.config.Config.*;
import static com.jsmart5.framework.config.Constants.*;
import static com.jsmart5.framework.manager.BeanHandler.*;

public enum ExpressionHandler {

	EXPRESSIONS();
	
	private static final Logger LOGGER = Logger.getLogger(ExpressionHandler.class.getPackage().getName());

	public static final Pattern EL_PATTERN = Pattern.compile("@\\{(.[^@\\{\\}]*)\\}");

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
		Matcher matcher = TagHandler.J_TAG_PATTERN.matcher(param);
		if (matcher.find()) {
			return TagEncrypter.complexDecrypt(matcher.group(2).replace("[]", ""));
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
	
			} else if (jTag.equals(TagHandler.J_TBL_SEL)) {
				setTableSelectionValue(expr, jParam);
	
			} else if (jTag.equals(TagHandler.J_TBL_EDT)) {
				setTableEditionValue(expr, jParam);
	
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
		String responsePath = null;
		Matcher matcher = EL_PATTERN.matcher(expr);
		if (matcher.find()) {

			String beanMethod = matcher.group(1);
			String[] methodSign = beanMethod.split(EL_SEPARATOR);
			
			if (methodSign.length > 0 && SmartContext.containsAttribute(methodSign[0])) {
				Object bean = getExpressionBean(methodSign[0]);
				beanMethod = String.format(JSP_EL, beanMethod);

				// Check authorization to execute method
				if (!HANDLER.checkExecuteAuthorization(bean, beanMethod)) {
					return responsePath;
				}

				// Call mapped method with @PreSubmit annotation for specific action
				if (HANDLER.executePreSubmit(bean, methodSign[methodSign.length -1])) {

					Object[] arguments = null;
					String[] paramArgs = SmartContext.getRequest().getParameterValues(TagHandler.J_SBMT_ARGS + jParam);

					if (paramArgs != null) {
						boolean unescape = HANDLER.containsUnescapeMethod(methodSign);
						arguments = new Object[paramArgs.length];

						for (int i = 0; i < paramArgs.length; i++) {
							arguments[i] = unescape ? paramArgs[i] : escapeValue(paramArgs[i]);
						}
					}

					// Call submit method
					ELContext context = SmartContext.getPageContext().getELContext();

					MethodExpression methodExpr = SmartContext.getExpressionFactory().createMethodExpression(context, beanMethod,
							null, arguments != null ? new Class<?>[arguments.length] : new Class<?>[]{});

					responsePath = (String) methodExpr.invoke(context, arguments);

					// Call mapped method with @PostSubmit annotation for specific action
					HANDLER.executePostSubmit(bean, methodSign[methodSign.length -1]);
				}
			}
		}
		return responsePath;
	}

	@SuppressWarnings("all")
	private void setSelectionValue(String expr, String jParam) {
		Matcher matcher = EL_PATTERN.matcher(expr);
		if (matcher.find()) {

			String beanMethod = matcher.group(1);
			String[] methodSign = beanMethod.split(EL_SEPARATOR);
			
			if (methodSign.length > 0 && SmartContext.containsAttribute(methodSign[0])) {

				HttpServletRequest request = SmartContext.getRequest();
				beanMethod = String.format(JSP_EL, beanMethod);

				// Get parameter mapped by TagHandler.J_VALUES
				String valuesParam = request.getParameter(TagHandler.J_SEL + jParam);
				Matcher valuesMatcher = TagHandler.J_TAG_PATTERN.matcher(valuesParam);

				Object object = null;
				List<Object> list = null;
				Scroll jsonScroll = null;

				if (valuesMatcher.find()) {
					object = getExpressionValue(TagEncrypter.complexDecrypt(valuesMatcher.group(2)));
				}

				if (object instanceof ListAdapter) {
					String scrollParam = request.getParameter(TagHandler.J_SCROLL + jParam);

					jsonScroll = GSON.fromJson(scrollParam, Scroll.class);

					list = ((ListAdapter) object).load(jsonScroll.getIndex(), jsonScroll.getSize());

					// Save the loaded content on request to avoid calling load twice when loading the page
					request.setAttribute(REQUEST_LIST_ADAPTER, list);

				} else if (object instanceof List<?>) {
					list = (List<Object>) object;
				}

				if (list != null && !list.isEmpty()) {
					ELContext context = SmartContext.getPageContext().getELContext();
					ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, beanMethod, Object.class);

					Integer index = Integer.parseInt(request.getParameter(TagHandler.J_SEL_VAL + jParam));

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
	private void setTableEditionValue(String expr, String param) throws ServletException, IOException {
//		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {
//			expr = expr.replace(Constants.START_EL, Constants.JSP_EL);
//
//			// Get json value to retrieve object value from collection or adapter
//			JsonObject jsonAction = GSON.toJsonTree(SmartContext.getRequest().getParameter(param)).getAsJsonObject();
//
//			Object object = getExpressionValue(TagEncrypter.complexDecrypt(TagHandler.J_TBL_EDT, getActionEdit(jsonAction)));
//
//			Long first = getActionFirst(jsonAction);
//			Long size = getActionSize(jsonAction);
//			Integer index = getActionIndex(jsonAction);
//
//			Object value = null;
//			
//			if (object instanceof TableAdapterHandler) {
//				List<Object> list = null;
//				Set<Object> objs = getExpressionBeans(expr);
//				TableAdapterHandler adapter = (TableAdapterHandler<Object>) object;
//
//				for (Object obj : objs) {
//					if (obj.getClass().getAnnotation(SmartBean.class).scope() == ScopeType.REQUEST_SCOPE) {
//						list = adapter.loadData(first, size.intValue(), getActionSortBy(jsonAction), 
//								getActionSortOrder(jsonAction), getActionFilters(jsonAction));
//					} else {
//						list = adapter.getLoaded();
//					}
//					break;
//				}
//
//				if (list != null && !list.isEmpty()) {
//					value = list.get(index);
//				}
//
//	 	 	} else if (object instanceof Collection) {
//	 	 		Collection collection = (Collection) object;
//
//	 	 		if (collection != null && !collection.isEmpty()) {
//	 	 			Object[] collectionArray = collection.toArray();
//	 	 			value = collectionArray[index];
//				}
//	 	 	}
//
//			if (value != null) {
//				String var = getActionVar(jsonAction);
//				SmartContext.getRequest().setAttribute(var, value);
//
//				Map<String, String> editValues = getActionEditValues(jsonAction);
//				// SmartContext.addParameters(editValues);
//
//				for (String editParam : editValues.keySet()) {
//					String editExpr = extractExpression(editParam);
//					if (editExpr != null) {
//						String jTag = editParam.substring(0, TagHandler.J_TAG_LENGTH);
//						handleRequestExpression(jTag, editExpr, editParam);
//					}
//				}
//
//				ELContext context = SmartContext.getPageContext().getELContext();
//				MethodExpression methodExpr = SmartContext.getExpressionFactory().createMethodExpression(context, expr, null, new Class<?>[]{value.getClass()});
//				methodExpr.invoke(context, new Object[]{value});
//
//				SmartContext.getRequest().removeAttribute(var);
//			}
//		}
	}

	/*
	 * It receives a json structure
	 * {"action": "", "type": "SINGLE/MULTI", "indexes": [], "first": "", "size": "", "sort": {"name": "", "order": ""}, "filters": [{"name": "", "field": "", "value": ""}, ...]}
	 */
	@SuppressWarnings("all")
	private void setTableSelectionValue(String expr, String param) throws ServletException {
//		if (expr != null && expr.startsWith(Constants.START_EL) && expr.endsWith(Constants.END_EL)) {
//			expr = expr.replace(Constants.START_EL, Constants.JSP_EL);
//
//			// Get json value to retrieve object value from collection or adapter
//			JsonObject jsonAction = GSON.toJsonTree(SmartContext.getRequest().getParameter(param)).getAsJsonObject();
//
//			Object object = getExpressionValue(TagEncrypter.complexDecrypt(TagHandler.J_TBL_SEL, getActionSelect(jsonAction)));
//
//			Long first = getActionFirst(jsonAction);
//			Long size = getActionSize(jsonAction);
//			SELECT_TYPE selectType = getActionType(jsonAction);
//			Integer[] indexes = getActionIndexes(jsonAction);
//
//			List values = new ArrayList(indexes.length);
//
//			if (object instanceof TableAdapterHandler) {
//				List<Object> list = null;
//				Set<Object> objs = getExpressionBeans(expr);
//				TableAdapterHandler adapter = (TableAdapterHandler<Object>) object;
//
//				for (Object obj : objs) {
//					if (obj.getClass().getAnnotation(SmartBean.class).scope() == ScopeType.REQUEST_SCOPE) {
//						list = adapter.loadData(first, size.intValue(), getActionSortBy(jsonAction), 
//								getActionSortOrder(jsonAction), getActionFilters(jsonAction));
//					} else {
//						list = ((TableAdapterHandler<Object>) object).getLoaded();
//					}
//					break;
//				}
//
//				if (list != null && !list.isEmpty()) {
//					for (int i = 0; i < indexes.length; i++) {
//						values.add(list.get(indexes[i]));
//					}
//				}
//
//	 	 	} else if (object instanceof Collection) {
//	 	 		Collection collection = (Collection) object;
//
//	 	 		if (collection != null && !collection.isEmpty()) {
//	 	 			Object[] collectionArray = collection.toArray();
//
//	 	 			for (int i = 0; i < indexes.length; i++) {
//	 	 				values.add(collectionArray[indexes[i]]);
//	 	 			}
//				}
//	 	 	}
//
//			// SmartContext.setSelectIndexes(indexes);
//			ELContext context = SmartContext.getPageContext().getELContext();
//
//			if (selectType == SELECT_TYPE.MULTI) {
//				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, List.class);
//				valueExpr.setValue(context, values);
//
//			} else if (!values.isEmpty()) {				
//				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, values.get(0).getClass());
//				valueExpr.setValue(context, values.get(0));
//			}
//		}
	}

	void setExpressionValue(String expr, String jParam) {
		if (isReadOnlyParameter(jParam)) {
			return;
		}

		Matcher matcher = EL_PATTERN.matcher(expr);
		if (matcher.find()) {

			String beanMethod = matcher.group(1);	
			String[] methodSign = beanMethod.split(EL_SEPARATOR);

			if (methodSign.length > 0 && SmartContext.containsAttribute(methodSign[0])) {
				beanMethod = String.format(JSP_EL, beanMethod);

				ELContext context = SmartContext.getPageContext().getELContext();
				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, beanMethod, Object.class);

				Object value = SmartContext.getRequest().getParameter(TagHandler.J_TAG + jParam);

				if (!HANDLER.containsUnescapeMethod(methodSign)) {
					value = escapeValue((String) value);
				}
				valueExpr.setValue(context, value);
			}
		}
	}

	void setAttributeValue(String expr, Object value) {
		Matcher matcher = EL_PATTERN.matcher(expr);
		if (matcher.find()) {

			String beanMethod = matcher.group(1);	
			String[] methodSign = beanMethod.split(EL_SEPARATOR);
			
			if (methodSign.length > 0 && SmartContext.containsAttribute(methodSign[0])) {
				beanMethod = String.format(JSP_EL, beanMethod);
				
				ELContext context = SmartContext.getPageContext().getELContext();
				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, beanMethod, Object.class);
				valueExpr.setValue(context, value);
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
			String[] methodSign = beanMethod.split(EL_SEPARATOR);
			
			if (methodSign.length > 0 && SmartContext.containsAttribute(methodSign[0])) {
				beanMethod = String.format(JSP_EL, beanMethod);

				List<Object> list = new ArrayList<Object>();
				String[] values = SmartContext.getRequest().getParameterValues(TagHandler.J_ARRAY + jParam);

				boolean unescape = HANDLER.containsUnescapeMethod(methodSign);

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
				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, beanMethod, Object.class);
				valueExpr.setValue(context, list);
			}
		}
	}

	private void setExpressionFilePart(String expr, String jParam) throws ServletException, IOException {
		Matcher matcher = EL_PATTERN.matcher(expr);
		if (matcher.find()) {
			
			String beanMethod = matcher.group(1);	
			String[] methodSign = beanMethod.split(EL_SEPARATOR);
			
			if (methodSign.length > 0 && SmartContext.containsAttribute(methodSign[0])) {
				beanMethod = String.format(JSP_EL, beanMethod);

				ELContext context = SmartContext.getPageContext().getELContext();
				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, beanMethod, Object.class);

				Object value = SmartContext.getRequest().getPart(TagHandler.J_PART + jParam);
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
			String[] methodSign = beanMethod.split(EL_SEPARATOR);

			if (methodSign.length > 0 && SmartContext.containsAttribute(methodSign[0])) {
				beanMethod = String.format(JSP_EL, beanMethod);

				ELContext context = SmartContext.getPageContext().getELContext();
				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, beanMethod, Object.class);
				String value = SmartContext.getRequest().getParameter(TagHandler.J_DATE + jParam);

				if (value != null && !value.trim().isEmpty()) {
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
		HttpServletRequest request = SmartContext.getRequest();

		// Just add the ReCaptcha value to mapped values for further validation
		if (expr.contains(ReCaptchaHandler.RESPONSE_V1_FIELD_NAME)) {
			SmartContext.addMappedValue(ReCaptchaHandler.RESPONSE_V1_FIELD_NAME, 
					request.getParameter(TagHandler.J_CAPTCHA + jParam));
			
		} else {
			SmartContext.addMappedValue(ReCaptchaHandler.RESPONSE_V2_FIELD_NAME, 
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

		String jspExpr = String.format(JSP_EL, expr);

		ELContext context = SmartContext.getPageContext().getELContext();
		ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, jspExpr, Object.class);
		Object obj = valueExpr.getValue(context);

		if (obj instanceof String) {
			String[] objs = obj.toString().split(EL_SEPARATOR, 2);
			if (objs.length == 2 && SmartText.containsResource(objs[0])) {
				return SmartText.getString(objs[0], objs[1]);
			}
		}

		if (obj != null) {
			return obj;
		}

		String[] exprs = expr.split(EL_SEPARATOR, 2);
		if (exprs.length == 2 && SmartText.containsResource(exprs[0])) {
			return SmartText.getString(exprs[0], exprs[1]);
		}
		return null;
	}

	private Object getExpressionBean(String name) {
		return SmartContext.getAttribute(name);
	}

	private boolean isReadOnlyParameter(String jParam) {
		if (jParam != null) {
			return jParam.endsWith(Constants.EL_PARAM_READ_ONLY);
		}
		return false;
	}

	private Object escapeValue(String value) {
		if (value != null && CONFIG.getContent().isEscapeRequest()) {
			value = StringEscapeUtils.escapeJavaScript(value);
			value = StringEscapeUtils.escapeHtml(value);
		}
    	return value;
    }

	String decodeUrl(String value) {
		try {
			return URLDecoder.decode(value, WebFilter.ENCODING);
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return value;
	}

}
