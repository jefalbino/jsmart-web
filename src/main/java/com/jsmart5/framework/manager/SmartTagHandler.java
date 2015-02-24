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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.google.gson.Gson;
import com.jsmart5.framework.config.SmartConstants;
import com.jsmart5.framework.tag.AjaxTagHandler;
import com.jsmart5.framework.tag.BindTagHandler;
import com.jsmart5.framework.tag.IconTagHandler;
import com.jsmart5.framework.tag.html.Script;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.util.SmartMessage;
import com.jsmart5.framework.util.SmartUtils;

import static com.jsmart5.framework.manager.SmartExpression.*;
import static com.jsmart5.framework.manager.SmartHandler.*;
import static com.jsmart5.framework.util.SmartText.*;

public abstract class SmartTagHandler extends SimpleTagSupport {

	protected static final Logger LOGGER = Logger.getLogger(SmartTagHandler.class.getPackage().getName());
	
	protected static final Gson GSON = new Gson();

	static final int J_TAG_LENGTH = 6;

	static final String J_TAG_INIT = "j0";

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

	protected static final String J_COMPLETE = J_TAG_INIT + "015_";

	protected static final String EL_PARAM_READ_ONLY = SmartConstants.EL_PARAM_READ_ONLY;


	protected final Map<String, Object> params;

	protected SmartValidateTagHandler validator;

	protected List<AjaxTagHandler> ajaxTags;
	
	protected List<IconTagHandler> iconTags;

	protected List<BindTagHandler> bindTags;

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

	public SmartTagHandler() {
		params = new LinkedHashMap<String, Object>(3);
		ajaxTags = new ArrayList<AjaxTagHandler>(2);
		iconTags = new ArrayList<IconTagHandler>(2);
		bindTags = new ArrayList<BindTagHandler>(2);
	}

	@Override
	public final void doTag() throws JspException, IOException {
		// long start = System.currentTimeMillis();
		try {
			validateTag();
			if (beforeTag()) {
				Tag tag = executeTag();
				if (tag != null) {
					printOutput(tag.getHtml());
				}
			}
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
			throw ex;
		}
		// LOGGER.log(Level.INFO, this.getClass().getName() + ": " + (System.currentTimeMillis() - start) + "ms");
	}

	// Available for overriding to stop tag being processed
	public boolean beforeTag() throws JspException, IOException {
		return true;
	}

	public abstract void validateTag() throws JspException;

	public abstract Tag executeTag() throws JspException, IOException;

	protected String getRandonId() {
		return SmartUtils.randomId();
	}

	protected void putParam(SmartTagHandler parent, String key, Object value) throws JspException {
		if (parent.params.containsKey(key)) {
			throw new JspException("Duplicated tag parameter " + key);
		}
		parent.params.put(key, value);
	}

	protected void setValidator(SmartTagHandler parent, SmartValidateTagHandler validator) {
		parent.validator = validator;
	}

	public void setOutputWriter(StringWriter outputWriter) {
		this.outputWriter = outputWriter;
	}

	public void addIconTag(IconTagHandler iconTag) {
		this.iconTags.add(iconTag);
	}
	
	public List<IconTagHandler> getIconTags() {
		return iconTags;
	}

	public List<BindTagHandler> getBindTags() {
		return bindTags;
	}
	
	public void addBindTag(BindTagHandler bindTag) {
		this.bindTags.add(bindTag);
	}
	
	public void addAllBindTag(List<BindTagHandler> list) {
		this.bindTags.addAll(list);
	}
	
	public List<AjaxTagHandler> getAjaxTags() {
		return ajaxTags;
	}
	
	public void addAjaxTag(AjaxTagHandler ajax) {
		this.ajaxTags.add(ajax);
	}
	
	public void addAllAjaxTag(List<AjaxTagHandler> list) {
		this.ajaxTags.addAll(list);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRest(String rest) {
		this.rest = rest;
	}
	
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
	
	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	protected void setEvents(SmartTagHandler tag) {
		if (tag.onClick == null) {
			tag.setOnClick(onClick);
		}
		if (tag.onDblClick == null) {
			tag.setOnDblClick(onDblClick);
		}
		if (tag.onMouseDown == null) {
			tag.setOnMouseDown(onMouseDown);
		}
		if (tag.onMouseMove == null) {
			tag.setOnMouseMove(onMouseMove);
		}
		if (tag.onMouseOver == null) {
			tag.setOnMouseOver(onMouseOver);
		}
		if (tag.onMouseOut == null) {
			tag.setOnMouseOut(onMouseOut);
		}
		if (tag.onMouseUp == null) {
			tag.setOnMouseUp(onMouseUp);
		}
		if (tag.onKeyDown == null) {
			tag.setOnKeyDown(onKeyDown);
		}
		if (tag.onKeyPress == null) {
			tag.setOnKeyPress(onKeyPress);
		}
		if (tag.onKeyUp == null) {
			tag.setOnKeyUp(onKeyUp);
		}
		if (tag.onBlur == null) {
			tag.setOnBlur(onBlur);
		}
		if (tag.onChange == null) {
			tag.setOnChange(onChange);
		}
		if (tag.onFocus == null) {
			tag.setOnFocus(onFocus);
		}
		if (tag.onSelect == null) {
			tag.setOnSelect(onSelect);
		}
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

	protected void printOutput(StringBuilder builder) throws IOException {
		printOutput(builder.toString());
	}
	
	protected void printOutput(String string) throws IOException {
		if (outputWriter != null) {
			outputWriter.write(string);
		} else {
			getJspContext().getOut().print(string);
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

	protected String getResourceString(String resource, String key) {
		return TEXTS.getString(resource, key);
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

	protected String getJsonValue(Object object) {
		return GSON.toJson(object).replace("\u0027", "'");
	}
	
	protected StringBuilder getBindFunction(String id, String event, StringBuilder script) {
		StringBuilder builder = new StringBuilder();
		builder.append("$(document).on('").append(event.toLowerCase()).append("','#").append(id).append("',function(e){");
		builder.append(script);
		builder.append("});");
		return builder;
	}

	protected StringBuilder getDelegateFunction(String id, String child, String event, StringBuilder script) {
		StringBuilder builder = new StringBuilder();
		builder.append("$(document).on('").append(event.toLowerCase()).append("','#");
		builder.append(id).append(" ").append(child).append("',function(e){");
		builder.append(script);
		builder.append("});");
		return builder;
	}

	protected void appendScript(String id, StringBuilder builder) {
		HttpServletRequest httpRequest = getRequest();
		Script script = (Script) httpRequest.getAttribute(SmartConstants.REQUEST_PAGE_SCRIPT_ATTR);

		if (script == null) {
			script = new Script();
			script.addAttribute("type", "text/javascript");
			httpRequest.setAttribute(SmartConstants.REQUEST_PAGE_SCRIPT_ATTR, script);
		}
		script.addText(builder.toString());
	}

	protected void appendAjax(String id) {
		appendAjax(this, id);
	}

	protected void appendAjax(SmartTagHandler tagHandler, String id) {
		if (!tagHandler.ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : tagHandler.ajaxTags) {
				appendScript(id, ajax.getBindFunction(id));
			}
		}
	}

	protected void appendDelegateAjax(String id, String child) {
		appendDelegateAjax(this, id, child);
	}

	protected void appendDelegateAjax(SmartTagHandler tagHandler, String id, String child) {
		if (!tagHandler.ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : tagHandler.ajaxTags) {
				appendScript(id, ajax.getDelegateFunction(id, child));
			}
		}
	}

	protected void appendBind(String id) {
		appendBind(this, id);
	}
	
	protected void appendBind(SmartTagHandler tagHandler, String id) {
		if (!tagHandler.bindTags.isEmpty()) {
			for (BindTagHandler bind : tagHandler.bindTags) {
				appendScript(id, bind.getBindFunction(id));
			}
		}
	}

	protected void appendDelegateBind(String id, String child) {
		appendDelegateBind(this, id, child);
	}

	protected void appendDelegateBind(SmartTagHandler tagHandler, String id, String child) {
		if (!tagHandler.bindTags.isEmpty()) {
			for (BindTagHandler bind : tagHandler.bindTags) {
				appendScript(id, bind.getDelegateFunction(id, child));
			}
		}
	}

	protected void appendValidator(Tag tag) throws JspException, IOException {
		if (validator != null) {
			tag.addAttribute("vldt-req", "true")
				.addAttribute("vldt-min-l", validator.getMinLength())
				.addAttribute("vldt-max-l", validator.getMaxLength())
				.addAttribute("vldt-text", getTagValue(validator.getText()))
				.addAttribute("vldt-look", validator.getLook());
		}
	}

	protected void appendRest(Tag tag) throws JspException, IOException {
		tag.addAttribute("rest", rest);
	}
	
	protected void appendEvent(Tag tag) {
		appendEvent(tag, this);
	}

	protected void appendEvent(Tag tag, SmartTagHandler tagHandler) {
		tag.addAttribute("onclick", tagHandler.onClick)
			.addAttribute("ondblclick", tagHandler.onDblClick)
			.addAttribute("onmousedown", tagHandler.onMouseDown)
			.addAttribute("onmousemove", tagHandler.onMouseMove)
			.addAttribute("onmouseover", tagHandler.onMouseOver)
			.addAttribute("onmouseout", tagHandler.onMouseOut)
			.addAttribute("onmouseup", tagHandler.onMouseUp)
			.addAttribute("onkeydown", tagHandler.onKeyDown)
			.addAttribute("onkeypress", tagHandler.onKeyPress)
			.addAttribute("onkeyup", tagHandler.onKeyUp)
			.addAttribute("onblur", tagHandler.onBlur)
			.addAttribute("onchange", tagHandler.onChange)
			.addAttribute("onfocus", tagHandler.onFocus)
			.addAttribute("onselect", tagHandler.onSelect);
	}
}
