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
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.json.JSONObject;


import static com.jsmart5.framework.manager.SmartExpression.*;
import static com.jsmart5.framework.manager.SmartHandler.*;
import static com.jsmart5.framework.manager.SmartText.*;

public abstract class SmartTagHandler extends SimpleTagSupport {

	protected static final Logger LOGGER = Logger.getLogger(SmartTagHandler.class.getPackage().getName());

	/*package*/ static final int J_TAG_LENGTH = 6;

	/*package*/ static final String J_TAG_INIT = "j0";

	protected static final int DEFAULT_VALUE = -1;

	protected static final String J_TAG = J_TAG_INIT + "001_";

	protected static final String J_SBMT = J_TAG_INIT + "002_";

	protected static final String J_TBL = J_TAG_INIT + "003_";

	protected static final String J_TBL_SEL = J_TAG_INIT + "004_";

	protected static final String J_TBL_EDT = J_TAG_INIT + "005_";

	protected static final String J_SEL = J_TAG_INIT + "006_";

	protected static final String J_SEL_VAL = J_TAG_INIT + "007_";

	protected static final String J_ARRAY = J_TAG_INIT + "008_";

	protected static final String J_FILE = J_TAG_INIT + "009_";

	protected static final String J_PART = J_TAG_INIT + "010_";

	protected static final String J_DATE = J_TAG_INIT + "011_";

	protected static final String J_FRMT = J_TAG_INIT + "012_";

	protected static final String J_CAPTCHA = J_TAG_INIT + "013_";

	protected static final String J_CAPTCHA_HASH = J_TAG_INIT + "014_";

	protected static final String EL_PARAM_READ_ONLY = SmartConstants.EL_PARAM_READ_ONLY;

	protected static final String ON_SELECT = "onselect=\"";

	protected static final String ON_CLICK = "onclick=\"";

	protected static final String ON_DBL_CLICK = "ondblclick=\"";

	protected static final String ON_CHANGE = "onchange=\"";

	protected static final String ON_BLUR = "onblur=\"";

	protected static final String ON_MOUSE_DOWN = "onmousedown=\"";

	protected static final String ON_MOUSE_MOVE = "onmousemove=\"";

	protected static final String ON_MOUSE_OVER = "onmouseover=\"";

	protected static final String ON_MOUSE_OUT = "onmouseout=\"";

	protected static final String ON_MOUSE_UP = "onmouseup=\"";

	protected static final String ON_KEY_DOWN = "onkeydown=\"";

	protected static final String ON_KEY_PRESS = "onkeypress=\"";

	protected static final String ON_KEY_UP = "onkeyup=\"";

	protected static final String ON_FOCUS = "onfocus=\"";

	protected static final String ON_SUBMIT = "onSubmit=\"";

	protected final Map<String, Object> params;

	protected SmartValidateTagHandler validator;

	protected String ajaxCommand;

	protected String dateFormatRegex;

	protected String numberFormatRegex;

	protected StringWriter outputWriter;

	public String id;

	public String rest;

	public String style;

	public String styleClass;

	public String onClick;

	public String onDblClick;

	public String onMouseDown;

	public String onMouseMove;

	public String onMouseOver;

	public String onMouseOut;

	public String onMouseUp;

	public String onKeyDown;

	public String onKeyPress;

	public String onKeyUp;

	public String onBlur;

	public String onChange;

	public String onFocus;

	public String onSelect;

	@Override
	public void doTag() throws JspException, IOException {
		try {
			validateTag();
			executeTag();
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
			throw ex;
		}
	}

	public abstract void validateTag() throws JspException;

	public abstract void executeTag() throws JspException, IOException;

	public SmartTagHandler() {
		params = new LinkedHashMap<String, Object>();
	}

	protected void putParam(SmartTagHandler parent, String key, Object value) {
		parent.params.put(key, value);
	}

	protected void setValidator(SmartTagHandler parent, SmartValidateTagHandler validator) {
		parent.validator = validator;
	}

	protected void setDateFormatRegex(SmartTagHandler parent, String dateFormatRegex) {
		parent.dateFormatRegex = dateFormatRegex;
	}

	protected void setNumberFormatRegex(SmartTagHandler parent, String numberFormatRegex) {
		parent.numberFormatRegex = numberFormatRegex;
	}

	public void setOutputWriter(StringWriter outputWriter) {
		this.outputWriter = outputWriter;
	}

	public void setAjaxCommand(String ajaxCommand) {
		this.ajaxCommand = ajaxCommand;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRest(String rest) {
		this.rest = rest;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public void setOnDblClick(String onDblClick) {
		this.onDblClick = onDblClick;
	}

	public void setOnMouseDown(String onMouseDown) {
		this.onMouseDown = onMouseDown;
	}

	public void setOnMouseMove(String onMouseMove) {
		this.onMouseMove = onMouseMove;
	}

	public void setOnMouseOver(String onMouseOver) {
		this.onMouseOver = onMouseOver;
	}

	public void setOnMouseOut(String onMouseOut) {
		this.onMouseOut = onMouseOut;
	}

	public void setOnMouseUp(String onMouseUp) {
		this.onMouseUp = onMouseUp;
	}

	public void setOnKeyDown(String onKeyDown) {
		this.onKeyDown = onKeyDown;
	}

	public void setOnKeyPress(String onKeyPress) {
		this.onKeyPress = onKeyPress;
	}

	public void setOnKeyUp(String onKeyUp) {
		this.onKeyUp = onKeyUp;
	}

	public void setOnBlur(String onBlur) {
		this.onBlur = onBlur;
	}

	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	public void setOnFocus(String onFocus) {
		this.onFocus = onFocus;
	}

	public void setOnSelect(String onSelect) {
		this.onSelect = onSelect;
	}

	protected void appendScriptBuilder(StringBuilder script) {
		appendScriptBuilder(script, false);
	}

	protected void appendScriptBuilder(StringBuilder script, boolean appendToEnd) {
		StringBuilder[] scriptBuilders = (StringBuilder[]) SmartContext.getSession().getAttribute(SmartConstants.SCRIPT_BUILDER_ATTR);
		if (scriptBuilders == null) {
			scriptBuilders = new StringBuilder[] {new StringBuilder(), new StringBuilder()};
			SmartContext.getSession().setAttribute(SmartConstants.SCRIPT_BUILDER_ATTR, scriptBuilders);
		}
		if (appendToEnd) {
			scriptBuilders[1].append(script.toString());
		} else {
			scriptBuilders[0].append(script.toString());
		}
	}

	protected void prependScriptBuilder(StringBuilder script) {
		StringBuilder[] scriptBuilders = (StringBuilder[]) SmartContext.getSession().getAttribute(SmartConstants.SCRIPT_BUILDER_ATTR);
		if (scriptBuilders == null) {
			scriptBuilders = new StringBuilder[] {new StringBuilder(), new StringBuilder()};
			SmartContext.getSession().setAttribute(SmartConstants.SCRIPT_BUILDER_ATTR, scriptBuilders);
		}
		scriptBuilders[0].insert(0, script.toString());
	}

	protected void printOutput(StringBuilder builder) throws IOException {
		if (outputWriter != null) {
			outputWriter.write(builder.toString());
		} else {
			getJspContext().getOut().print(builder.toString());
		}
	}

	protected HttpServletRequest getRequest() {
		return SmartContext.getRequest();
	}

	protected HttpServletResponse getResponse() {
		return SmartContext.getResponse();
	}

	protected String getRequestPath() {
		HttpServletRequest request = getRequest();
		String[] paths = request.getServletPath().split("/");
		return request.getContextPath() + "/" + paths[paths.length -1].substring(0, paths[paths.length -1].indexOf("."));
	}

	protected InputStream getResourceStream(String name) {
		return getRequest().getServletContext().getResourceAsStream(SmartConstants.WEB_INF + name);
	}

	protected String getTagName(String prefix, String name) {
		if (name != null && name.startsWith(SmartConstants.START_EL) && name.endsWith(SmartConstants.END_EL)) {
			return SmartTagEncrypter.complexEncrypt(prefix, name);
		}
		return name;
	}

	protected Object getTagValue(Object name) {
		return EXPRESSIONS.getExpressionValue(name);
	}

	protected void setTagValue(String name, Object value) {
		EXPRESSIONS.setAttributeValue(name, value);
	}

	protected void setQueryParamValue(String name, String param) {
		EXPRESSIONS.setExpressionValue(name, param, true);
	}

	protected String getResourceString(String resource) {
		return (String) EXPRESSIONS.getResourceValue(resource);
	}

	protected String getResourceString(String resource, String key) {
		return TEXTS.getString(resource, key);
	}

	protected boolean isEditRowTagEnabled() {
		return SmartContext.isEditItemTagEnabled();
	}

	protected void setEditRowTagEnable(boolean enabled) {
		SmartContext.setEditItemTagEnabled(enabled);
	}

	protected Collection<String> getUserAuthorizationAccess() {
		return HANDLER.getUserAuthorizationAccess();
	}

	protected Map<String, SmartMessage> getMessages() {
		return SmartContext.getMessages();
	}

	protected Map<String, SmartMessage> getMessages(String id) {
		return SmartContext.getMessages(id);
	}

	protected String getJSONValue(Object object) {
		return new JSONObject(object).toString().replace("\"", "&quot;");
	}

	protected void appendFormValidator(StringBuilder builder) throws JspException, IOException {
		if (validator != null) {
			builder.append("validatedrequired=\"true\" ");
			if (validator.getMinLength() != null) {
				builder.append("validateminlength=\"" + validator.getMinLength() + "\" ");
			}
			if (validator.getMaxLength() != null) {
				builder.append("validatemaxlength=\"" + validator.getMaxLength() + "\" ");
			}
			if (validator.getMessage() != null) {
				builder.append("validatemessage=\"" + getTagValue(validator.getMessage()) + "\" ");
			}
		}
	}

	protected void appendRestBuilder(StringBuilder builder) throws JspException, IOException {
		if (rest != null) {
			builder.append("rest=\"" + rest + "\" ");
		}
	}

	protected void appendEventBuilder(StringBuilder builder) {
		if (onClick != null) {
			appendEventBuilder(builder, ON_CLICK, onClick);
		}
		if (onDblClick != null) {
			appendEventBuilder(builder, ON_DBL_CLICK, onDblClick);
		}
		if (onMouseDown != null) {
			appendEventBuilder(builder, ON_MOUSE_DOWN, onMouseDown);
		}
		if (onMouseMove != null) {
			appendEventBuilder(builder, ON_MOUSE_MOVE, onMouseMove);
		}
		if (onMouseOver != null) {
			appendEventBuilder(builder, ON_MOUSE_OVER, onMouseOver);
		}
		if (onMouseOut != null) {
			appendEventBuilder(builder, ON_MOUSE_OUT, onMouseOut);
		}
		if (onMouseUp != null) {
			appendEventBuilder(builder, ON_MOUSE_UP, onMouseUp);
		}
		if (onKeyDown != null) {
			appendEventBuilder(builder, ON_KEY_DOWN, onKeyDown);
		}
		if (onKeyPress != null) {
			appendEventBuilder(builder, ON_KEY_PRESS, onKeyPress);
		}
		if (onKeyUp != null) {
			appendEventBuilder(builder, ON_KEY_UP, onKeyUp);
		}
		if (onBlur != null) {
			appendEventBuilder(builder, ON_BLUR, onBlur);
		}
		if (onChange != null) {
			appendEventBuilder(builder, ON_CHANGE, onChange);
		}
		if (onFocus != null) {
			appendEventBuilder(builder, ON_FOCUS, onFocus);
		}
		if (onSelect != null) {
			appendEventBuilder(builder, ON_SELECT, onSelect);
		}
	}

	private void appendEventBuilder(StringBuilder builder, String attr, String exec) {
		int index = builder.lastIndexOf(attr);
		if (index >= 0) {
			builder.replace(index, index + attr.length(), attr + exec + ";");
		} else {
			builder.append(attr + exec + "\" ");	
		}
	}

}
