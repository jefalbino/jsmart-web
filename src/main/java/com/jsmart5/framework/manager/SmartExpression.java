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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import com.jsmart5.framework.annotation.ScopeType;
import com.jsmart5.framework.annotation.SmartBean;
import com.jsmart5.framework.tag.SmartTableAdapter;

import static com.jsmart5.framework.manager.SmartConfig.*;
import static com.jsmart5.framework.manager.SmartHandler.*;
import static com.jsmart5.framework.manager.SmartText.*;
import static com.jsmart5.framework.manager.SmartTableTagHandler.*;

/*package*/ enum SmartExpression {

	EXPRESSIONS();

	/*package*/ Map<String, String> getRequestExpressions() {
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
		if (param.length() >= SmartTagHandler.J_TAG_LENGTH) {
			String jTag = param.substring(0, SmartTagHandler.J_TAG_LENGTH);

			if (jTag.startsWith(SmartTagHandler.J_TAG_INIT)) {
				return SmartTagEncrypter.complexDecrypt(jTag, param.replace("[]", ""));
			}
		}
		return null;
	}

	/*package*/ String handleRequestExpression(String param, String expr) throws ServletException, IOException {
		String submitExpression = null;
		String jTag = param.substring(0, SmartTagHandler.J_TAG_LENGTH);

		if (jTag.equals(SmartTagHandler.J_TAG)) {
			setExpressionValue(expr, param, false);

		} else if (jTag.equals(SmartTagHandler.J_ARRAY)) {
			setExpressionValues(expr, param);

		} else if (jTag.equals(SmartTagHandler.J_SEL)) {
			setSelectionValue(expr, param);

		} else if (jTag.equals(SmartTagHandler.J_TBL_SEL)) {
			setTableSelectionValue(expr, param);

		} else if (jTag.equals(SmartTagHandler.J_TBL_EDT)) {
			setTableEditionValue(expr, param);

		} else if (jTag.equals(SmartTagHandler.J_FILE)) {
			setExpressionFilePart(expr, param);

		} else if (jTag.equals(SmartTagHandler.J_DATE)) {
			setExpressionDate(expr, param);

		} else if (jTag.equals(SmartTagHandler.J_CAPTCHA)) {
			setExpressionCaptcha(expr, param);

		} else if (jTag.equals(SmartTagHandler.J_SBMT)) {
			submitExpression = expr;
		}

		return submitExpression;
	}

	/*package*/ String submitExpression(String expr) {
		String retExpr = null;
		if (expr != null && expr.startsWith(SmartConstants.START_EL) && expr.endsWith(SmartConstants.END_EL)) {

			expr = expr.replace(SmartConstants.START_EL, SmartConstants.JSP_EL);

			Set<Object> objs = getExpressionBeans(expr);

			for (Object obj : objs) {

				// Check authorization to execute method
				if (!HANDLER.checkExecuteAuthorization(obj, expr)) {
					break;
				}

				// Call mapped method with @PreSubmit annotation
				HANDLER.executePreSubmit(obj);

				// Call submit method
				ELContext context = SmartContext.getPageContext().getELContext();
				MethodExpression methodExpr = SmartContext.getExpressionFactory().createMethodExpression(context, expr, null, new Class<?>[]{});
				retExpr = (String) methodExpr.invoke(context, null);

				// Call mapped method with @PostSubmit annotation
				HANDLER.executePostSubmit(obj);

				break;
			}
		}
		return retExpr;
	}

	@SuppressWarnings("all")
	/*package*/ void setSelectionValue(String expr, String param) {
		if (expr != null && expr.startsWith(SmartConstants.START_EL) && expr.endsWith(SmartConstants.END_EL)) {

			String[] names = expr.replace(SmartConstants.START_EL, "").replace(SmartConstants.END_EL, "").split(SmartConstants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				List<Object> list = (List<Object>) getExpressionValue(SmartTagEncrypter.complexDecrypt(SmartTagHandler.J_SEL, SmartContext.getParameter(param)));
				if (list != null && !list.isEmpty()) {

					expr = expr.replace(SmartConstants.START_EL, SmartConstants.JSP_EL);
					ELContext context = SmartContext.getPageContext().getELContext();

					ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);
					String index = SmartContext.getParameter(param.replaceFirst(SmartTagHandler.J_SEL, SmartTagHandler.J_SEL_VAL));
					
					valueExpr.setValue(context, list.get(Integer.parseInt(index)));
				}
			}
		}
	}

	/*
	 * It receives a json structure
	 * {"edit": "", "index": "", "varname": "", "values": [{"name": "", "value": ""}, ...], "first": "", "size": "", "sort": {"name": "", "order": ""}, "filters": [{"name": "", "field": "", "value": ""}, ...]}
	 */
	@SuppressWarnings("all")
	/*package*/ void setTableEditionValue(String expr, String param) throws ServletException, IOException {
		if (expr != null && expr.startsWith(SmartConstants.START_EL) && expr.endsWith(SmartConstants.END_EL)) {
			try {
				expr = expr.replace(SmartConstants.START_EL, SmartConstants.JSP_EL);

				// Get json value to retrieve object value from collection or adapter
				JSONObject jsonAction = new JSONObject(SmartContext.getRequest().getParameter(param));

				Object object = getExpressionValue(SmartTagEncrypter.complexDecrypt(SmartTagHandler.J_TBL_EDT, getActionEdit(jsonAction)));

				Long first = getActionFirst(jsonAction);
				Long size = getActionSize(jsonAction);
				Integer index = getActionIndex(jsonAction);

				Object value = null;
				
				if (object instanceof SmartTableAdapter) {
					List<Object> list = null;
					Set<Object> objs = getExpressionBeans(expr);
					SmartTableAdapter adapter = (SmartTableAdapter<Object>) object;

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

			} catch (JSONException ex) {
				throw new ServletException(ex);
			}
		}
	}

	/*
	 * It receives a json structure
	 * {"action": "", "type": "SINGLE/MULTI", "indexes": [], "first": "", "size": "", "sort": {"name": "", "order": ""}, "filters": [{"name": "", "field": "", "value": ""}, ...]}
	 */
	@SuppressWarnings("all")
	/*package*/ void setTableSelectionValue(String expr, String param) throws ServletException {
		if (expr != null && expr.startsWith(SmartConstants.START_EL) && expr.endsWith(SmartConstants.END_EL)) {
			try {
				expr = expr.replace(SmartConstants.START_EL, SmartConstants.JSP_EL);
	
				// Get json value to retrieve object value from collection or adapter
				JSONObject jsonAction = new JSONObject(SmartContext.getRequest().getParameter(param));
	
				Object object = getExpressionValue(SmartTagEncrypter.complexDecrypt(SmartTagHandler.J_TBL_SEL, getActionSelect(jsonAction)));
	
				Long first = getActionFirst(jsonAction);
				Long size = getActionSize(jsonAction);
				SELECT_TYPE selectType = getActionType(jsonAction);
				Integer[] indexes = getActionIndexes(jsonAction);

				List values = new ArrayList(indexes.length);
	
				if (object instanceof SmartTableAdapter) {
					List<Object> list = null;
					Set<Object> objs = getExpressionBeans(expr);
					SmartTableAdapter adapter = (SmartTableAdapter<Object>) object;
	
					for (Object obj : objs) {
						if (obj.getClass().getAnnotation(SmartBean.class).scope() == ScopeType.REQUEST_SCOPE) {
							list = adapter.loadData(first, size.intValue(), getActionSortBy(jsonAction), 
									getActionSortOrder(jsonAction), getActionFilters(jsonAction));
						} else {
							list = ((SmartTableAdapter<Object>) object).getLoaded();
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

				SmartContext.setSelectIndexes(indexes);
				ELContext context = SmartContext.getPageContext().getELContext();

				if (selectType == SELECT_TYPE.MULTI) {
					ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, List.class);
					valueExpr.setValue(context, values);
	
				} else if (!values.isEmpty()) {				
					ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, values.get(0).getClass());
					valueExpr.setValue(context, values.get(0));
				}

			} catch (JSONException ex) {
				throw new ServletException(ex);
			}
		}
	}

	/*package*/ void setExpressionValue(String expr, String param, boolean isUrl) {
		if (expr != null && expr.startsWith(SmartConstants.START_EL) && expr.endsWith(SmartConstants.END_EL)) {

			if (isReadOnlyParameter(param)) {
				return;
			}

			String[] names = expr.replace(SmartConstants.START_EL, "").replace(SmartConstants.END_EL, "").split(SmartConstants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				expr = expr.replace(SmartConstants.START_EL, SmartConstants.JSP_EL);
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

	/*package*/ void setAttributeValue(String expr, Object value) {
		if (expr != null && expr.startsWith(SmartConstants.START_EL) && expr.endsWith(SmartConstants.END_EL)) {

			String[] names = expr.replace(SmartConstants.START_EL, "").replace(SmartConstants.END_EL, "").split(SmartConstants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				expr = expr.replace(SmartConstants.START_EL, SmartConstants.JSP_EL);
				ELContext context = SmartContext.getPageContext().getELContext();

				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);
				valueExpr.setValue(context, value);
			}
		}
	}

	/*package*/ void setExpressionValues(String expr, String param) {
		if (expr != null && expr.startsWith(SmartConstants.START_EL) && expr.endsWith(SmartConstants.END_EL)) {

			if (isReadOnlyParameter(param)) {
				return;
			}

			String[] names = expr.replace(SmartConstants.START_EL, "").replace(SmartConstants.END_EL, "").split(SmartConstants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				expr = expr.replace(SmartConstants.START_EL, SmartConstants.JSP_EL);

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

	/*package*/ void setExpressionFilePart(String expr, String file) throws ServletException, IOException {
		if (expr != null && expr.startsWith(SmartConstants.START_EL) && expr.endsWith(SmartConstants.END_EL)) {

			String[] names = expr.replace(SmartConstants.START_EL, "").replace(SmartConstants.END_EL, "").split(SmartConstants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				expr = expr.replace(SmartConstants.START_EL, SmartConstants.JSP_EL);
				ELContext context = SmartContext.getPageContext().getELContext();

				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);
				Object val = SmartContext.getRequest().getPart(file.replaceFirst(SmartTagHandler.J_FILE, SmartTagHandler.J_PART));
				valueExpr.setValue(context, val);
			}
		}
	}

	/*package*/ void setExpressionDate(String expr, String date) throws ServletException {
		if (expr != null && expr.startsWith(SmartConstants.START_EL) && expr.endsWith(SmartConstants.END_EL)) {

			String[] names = expr.replace(SmartConstants.START_EL, "").replace(SmartConstants.END_EL, "").split(SmartConstants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				expr = expr.replace(SmartConstants.START_EL, SmartConstants.JSP_EL);
				ELContext context = SmartContext.getPageContext().getELContext();

				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);

				String val = SmartContext.getParameter(date);

				if (!val.trim().isEmpty()) {
					String format = SmartContext.getParameter(date.replaceFirst(SmartTagHandler.J_DATE, SmartTagHandler.J_FRMT));

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

	/*package*/ void setExpressionCaptcha(String expr, String param) throws ServletException {
		if (expr != null && expr.startsWith(SmartConstants.START_EL) && expr.endsWith(SmartConstants.END_EL)) {

			String[] names = expr.replace(SmartConstants.START_EL, "").replace(SmartConstants.END_EL, "").split(SmartConstants.EL_SEPARATOR);
			if (names.length > 0 && SmartContext.containsAttribute(names[0])) {

				expr = expr.replace(SmartConstants.START_EL, SmartConstants.JSP_EL);
				ELContext context = SmartContext.getPageContext().getELContext();

				ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);

				String value = SmartContext.getParameter(param);
				String hashValue = SmartContext.getParameter(param.replaceFirst(SmartTagHandler.J_CAPTCHA, SmartTagHandler.J_CAPTCHA_HASH));

				valueExpr.setValue(context, computeCaptchaHash(value).equals(hashValue));
			}
		}
	}

	/*package*/ Object getExpressionValue(Object expr) {
		if (expr != null) {
			String union = expr.toString();
			Matcher matcher = Pattern.compile(SmartConstants.EL_PATTERN).matcher(expr.toString());
			List<Object> list = new ArrayList<Object>();

			while (matcher.find()) {
				String group = matcher.group();
				list.add(evaluateExpression(group));
				union = union.replace(group, "%s");
			}

			if (list.isEmpty()) {
				return expr;
			} else {
				if (union.equals("%s")) {
					return list.get(0);
				} else {
					return String.format(union, list.toArray());
				}
			}
		}
		return null;
	}

	private boolean isReadOnlyParameter(String param) {
		if (param != null) {
			return param.endsWith(SmartConstants.EL_PARAM_READ_ONLY);
		}
		return false;
	}

	private Object evaluateExpression(String expr) {
		if (expr != null && expr.startsWith(SmartConstants.START_EL) && expr.endsWith(SmartConstants.END_EL)) {

			expr = expr.replace(SmartConstants.START_EL, SmartConstants.JSP_EL);
			ELContext context = SmartContext.getPageContext().getELContext();

			ValueExpression valueExpr = SmartContext.getExpressionFactory().createValueExpression(context, expr, Object.class);
			Object obj = valueExpr.getValue(context);

			if (obj instanceof String) {
				String[] objs = obj.toString().split(SmartConstants.EL_SEPARATOR, 2);
				if (objs.length == 2 && TEXTS.containsResource(objs[0])) {
					return TEXTS.getString(objs[0], objs[1]);
				}
			}

			if (obj != null) {
				return obj;
			}

			String[] exprs = expr.replace(SmartConstants.JSP_EL, "").replace(SmartConstants.END_EL, "").split(SmartConstants.EL_SEPARATOR, 2);
			if (exprs.length == 2 && TEXTS.containsResource(exprs[0])) {
				return TEXTS.getString(exprs[0], exprs[1]);
			}

			return null;
		}
		return expr;
	}

	/*package*/ Object getResourceValue(String expr) {
		if (expr != null && expr.startsWith(SmartConstants.START_EL) && expr.endsWith(SmartConstants.END_EL)) {

			String[] exprs = expr.replace(SmartConstants.START_EL, "").replace(SmartConstants.END_EL, "").split(SmartConstants.EL_SEPARATOR, 2);

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
				if (expr.contains(name + SmartConstants.POINT)) {
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
			value = StringUtils.replaceEach(value, new String[]{"&", "\"", "<", ">", "$", "#"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;", "&#36;", "&#35;"});
		}
    	return value;
    }

	private Object decodeUrl(Object value) {
		if (value instanceof String) {
			try {
				return URLDecoder.decode(String.valueOf(value), SmartWebFilter.ENCODING);
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
