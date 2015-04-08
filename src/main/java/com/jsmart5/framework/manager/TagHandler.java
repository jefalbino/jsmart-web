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
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.google.gson.Gson;
import com.jsmart5.framework.config.Constants;
import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.json.Ajax;
import com.jsmart5.framework.json.Bind;
import com.jsmart5.framework.tag.AjaxTagHandler;
import com.jsmart5.framework.tag.BindTagHandler;
import com.jsmart5.framework.tag.IconTagHandler;
import com.jsmart5.framework.tag.LoadTagHandler;
import com.jsmart5.framework.tag.PopOverTagHandler;
import com.jsmart5.framework.tag.TooltipTagHandler;
import com.jsmart5.framework.tag.ValidateTagHandler;
import com.jsmart5.framework.tag.html.DocScript;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.util.EventAction;
import com.jsmart5.framework.tag.util.RefAction;
import com.jsmart5.framework.util.SmartAlert;
import com.jsmart5.framework.util.SmartUtils;

import com.jsmart5.framework.util.SmartText;
import static com.jsmart5.framework.manager.ExpressionHandler.*;
import static com.jsmart5.framework.manager.BeanHandler.*;
import static com.jsmart5.framework.tag.js.JsConstants.JSMART_AJAX;
import static com.jsmart5.framework.tag.js.JsConstants.JSMART_BIND;

public abstract class TagHandler extends SimpleTagSupport {

	protected static final Logger LOGGER = Logger.getLogger(TagHandler.class.getPackage().getName());
	
	protected static final Gson GSON = new Gson();

	protected static final String DELEGATE_TAG_PARENT = "delegate_tag_parent";
	
	protected static final Pattern J_TAG_PATTERN = Pattern.compile("^(j0\\d{3}_)(.*)");

	private static final String J_TAG_INIT = "j0";

	protected static final int DEFAULT_VALUE = -1;

	protected static final String J_TAG = J_TAG_INIT + "001_";

	protected static final String J_SBMT = J_TAG_INIT + "002_";
	
	protected static final String J_SBMT_ARGS = J_TAG_INIT + "003_";
	
	protected static final String J_VALUES = J_TAG_INIT + "004_";

	protected static final String J_SEL = J_TAG_INIT + "005_";

	protected static final String J_SEL_VAL = J_TAG_INIT + "006_";

	protected static final String J_ARRAY = J_TAG_INIT + "007_";

	protected static final String J_FILE = J_TAG_INIT + "008_";

	protected static final String J_PART = J_TAG_INIT + "009_";
	
	protected static final String J_SCROLL = J_TAG_INIT + "010_";

	protected static final String J_DATE = J_TAG_INIT + "011_";

	protected static final String J_CAPTCHA = J_TAG_INIT + "012_";

	protected static final String EL_PARAM_READ_ONLY = Constants.EL_PARAM_READ_ONLY;

	protected final Map<String, Object> params;
	
	protected List<Object> args;

	protected ValidateTagHandler validatorTag;

	protected LoadTagHandler loadTag;
	
	protected PopOverTagHandler popOverTag;
	
	protected TooltipTagHandler tooltipTag;

	protected List<AjaxTagHandler> ajaxTags;
	
	protected List<IconTagHandler> iconTags;

	protected List<BindTagHandler> bindTags;

	protected StringWriter outputWriter;

	public String id;

	public String rest;
	
	public boolean ajax;

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

	public TagHandler() {
		ajaxTags = new ArrayList<AjaxTagHandler>(2);
		iconTags = new ArrayList<IconTagHandler>(2);
		bindTags = new ArrayList<BindTagHandler>(2);
		params = new LinkedHashMap<String, Object>(3);
		args = new ArrayList<Object>(3);
	}

	protected void clearTagParameters() {
		ajaxTags.clear();
		iconTags.clear();
		bindTags.clear();
		params.clear();
		args.clear();
	}

	@Override
	public final void doTag() throws JspException, IOException {
		// long start = System.currentTimeMillis();
		try {
			if (checkTagExecution()) {
				validateTag();
				clearTagParameters();
	
				if (beforeTag()) {
					Tag tag = executeTag();
					if (tag != null) {
						printOutput(tag.getHtml());
					}
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

	// Only execute this tag if it is not Ajax request or if this tag id is present on update component request
	private boolean checkTagExecution() {
		if (id == null) {
			return true;
		}
		HttpServletRequest request = getRequest();

		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			String updateVals = request.getHeader("Update-Ajax");
			if (updateVals == null) {
				return true;
			}
			return updateVals.contains(id);
		}
		return true;
	}

	public void addParam(String key, Object value) throws JspException {
		if (params.containsKey(key)) {
			throw new JspException("Duplicated tag parameter " + key);
		}
		params.put(key, value);
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void addArg(Object arg) {
		this.args.add(arg);
	}
	
	public List<Object> getArgs() {
		return args;
	}

	public void setValidatorTag(ValidateTagHandler validatorTag) {
		this.validatorTag = validatorTag;
	}
	
	public void setLoadTag(LoadTagHandler loadTag) {
		this.loadTag = loadTag;
	}
	
	public void setPopOverTag(PopOverTagHandler popOverTag) {
		this.popOverTag = popOverTag;
	}

	public void setTooltipTag(TooltipTagHandler tooltipTag) {
		this.tooltipTag = tooltipTag;
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

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
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

	protected void setEvents(TagHandler tag) {
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

	protected Object getMappedValue(final String name) {
		return SmartContext.getMappedValue(name);
	}
	
	protected Object removeMappedValue(final String name) {
		return SmartContext.removeMappedValue(name);
	}

	protected void addMappedValue(final String name, final Object value) {
		SmartContext.addMappedValue(name, value);
	}

	protected String getRequestPath() {
		HttpServletRequest request = getRequest();
		String[] paths = request.getServletPath().split("/");
		return request.getContextPath() + "/" + paths[paths.length -1].substring(0, paths[paths.length -1].indexOf("."));
	}

	protected InputStream getResourceStream(String name) {
		return getRequest().getServletContext().getResourceAsStream(Constants.WEB_INF + name);
	}

	protected String fakeTagName(String name) {
		return String.format(Constants.TAG_EL, name);
	}

	protected String getTagName(String prefix, String name) {
		if (name != null) {
			Matcher matcher = ExpressionHandler.EL_PATTERN.matcher(name);
			if (matcher.find()) {
				return prefix + TagEncrypter.complexEncrypt(name);
			}
		}
		return name;
	}
	
	protected boolean isEL(String name) {
		return ExpressionHandler.EL_PATTERN.matcher(name).find();
	}

	protected Object getTagValue(Object name) {
		return EXPRESSIONS.getExpressionValue(name);
	}

	protected void setTagValue(String name, Object value) {
		EXPRESSIONS.setAttributeValue(name, value);
	}

	protected String getResourceString(String resource, String key) {
		return SmartText.getString(resource, key);
	}

	protected Collection<String> getUserAuthorizationAccess() {
		return HANDLER.getUserAuthorizationAccess();
	}

	protected List<SmartAlert> getAlerts(String id) {
		return SmartContext.getAlerts(id);
	}

	protected String getJsonValue(Object object) {
		return GSON.toJson(object).replace("\u0027", "'");
	}
	
	protected String getJsonHtmlValue(Object object) {
		return GSON.toJson(object).replace("\"", "'");
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

	protected void appendScript(StringBuilder builder) {
		if (builder != null) {
			HttpServletRequest httpRequest = getRequest();
			DocScript script = (DocScript) httpRequest.getAttribute(Constants.REQUEST_PAGE_SCRIPT_ATTR);
	
			if (script == null) {
				script = new DocScript();
				script.addAttribute("type", "text/javascript");
				httpRequest.setAttribute(Constants.REQUEST_PAGE_SCRIPT_ATTR, script);
			}
			script.addText(builder.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void pushDelegateTagParent() {
		Stack<RefAction> actionStack = (Stack<RefAction>) getMappedValue(DELEGATE_TAG_PARENT);
		if (actionStack == null) {
			actionStack = new Stack<RefAction>();
			actionStack.push(new RefAction());
			addMappedValue(DELEGATE_TAG_PARENT, actionStack);
		} else {
			actionStack.push(new RefAction());
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void popDelegateTagParent() {
		Stack<RefAction> actionStack = (Stack<RefAction>) getMappedValue(DELEGATE_TAG_PARENT);
		
		RefAction refAction = actionStack.pop();
		if (actionStack.empty()) {
			removeMappedValue(DELEGATE_TAG_PARENT);
		}

		Map<String, EventAction> refs = refAction.getRefs();
		if (refs != null) {

			for (String refId : refs.keySet()) {			
				EventAction eventAction = refs.get(refId);
				if (eventAction == null) {
					continue;
				}

				Map<String, Set<Ajax>> refAjaxs = eventAction.getAjaxs();
				if (refAjaxs != null) {
					for (String event : refAjaxs.keySet()) {
						for (Ajax jsonAjax : refAjaxs.get(event)) {
							StringBuilder builder = new StringBuilder();
							builder.append(JSMART_AJAX.format(getJsonValue(jsonAjax)));
							builder = getDelegateFunction(id, "*[role-delegate=\"" + refId + "\"]", event.toLowerCase(), builder);
							appendScript(builder);
						}
					}
				}

				Map<String, Set<Bind>> refBinds = eventAction.getBinds();
				if (refBinds != null) {
					for (String event : refBinds.keySet()) {
						for (Bind jsonBind : refBinds.get(event)) {
							StringBuilder builder = new StringBuilder();
							builder.append(JSMART_BIND.format(getJsonValue(jsonBind)));
							builder = getDelegateFunction(id, "*[role-delegate=\"" + refId + "\"]", event.toLowerCase(), builder);
							appendScript(builder);
						}
					}
				}
			}
		}
	}

	private boolean isDelegate() {
		return getMappedValue(DELEGATE_TAG_PARENT) != null;
	}

	protected void appendRefId(Tag tag, String id) {

		if (ajaxTags.isEmpty() && bindTags.isEmpty()) {
			tag.addAttribute("id", id);
			return;
		}

		boolean isDelegate = isDelegate();
		if (isDelegate || SmartContext.isAjaxRequest()) {

			if (isDelegate) {
				tag.addAttribute("role-delegate", id);
			} else {
				tag.addAttribute("id", id);
			}

			// Place the arguments and parameters to this tag which is holding ajax tags
			for (AjaxTagHandler ajaxTag : ajaxTags) {
				for (String param : ajaxTag.params.keySet()) {
					tag.addUniqueAttribute(param, ajaxTag.params.get(param));
				}
				
				if (!ajaxTag.args.isEmpty()) {
					String actionName = getTagName(J_SBMT_ARGS, ajaxTag.getAction());
					tag.addUniqueAttribute(actionName, getJsonHtmlValue(ajaxTag.args));
				}
			}
		} else {
			tag.addAttribute("id", id);
		}
	}

	protected String getRandomId() {
		return SmartUtils.randomId();
	}

	protected void setRandomId(String tag) throws JspException {
		setRandomId(this, tag);
	}	

	protected void setRandomId(TagHandler tagHandler, String tag) throws JspException {
		if (tagHandler.id == null) {
			if (tagHandler.ajax) {
				throw InvalidAttributeException.fromConstraint(tag, "id", "specified because [ajax=true]");
			}
			if (!tagHandler.ajaxTags.isEmpty()) {
				throw InvalidAttributeException.fromConstraint(tag, "id", "specified because tag contains [ajax] tags");
			}
			if (!tagHandler.bindTags.isEmpty()) {
				throw InvalidAttributeException.fromConstraint(tag, "id", "specified because tag contains [bind] tags");
			}
			tagHandler.id = SmartUtils.randomId();
		}
	}

	protected void appendAjax(String id) {
		appendAjax(this, id);
	}

	protected void appendAjax(TagHandler tagHandler, String id) {
		if (!tagHandler.ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : tagHandler.ajaxTags) {
				appendScript(ajax.getBindFunction(id));
			}
		}
	}

	protected void appendDelegateAjax(String id, String child) {
		appendDelegateAjax(this, id, child);
	}

	protected void appendDelegateAjax(TagHandler tagHandler, String id, String child) {
		if (!tagHandler.ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : tagHandler.ajaxTags) {
				appendScript(ajax.getDelegateFunction(id, child));
			}
		}
	}

	protected void appendBind(String id) {
		appendBind(this, id);
	}
	
	protected void appendBind(TagHandler tagHandler, String id) {
		if (!tagHandler.bindTags.isEmpty()) {
			for (BindTagHandler bind : tagHandler.bindTags) {
				appendScript(bind.getBindFunction(id));
			}
		}
	}

	protected void appendDelegateBind(String id, String child) {
		appendDelegateBind(this, id, child);
	}

	protected void appendDelegateBind(TagHandler tagHandler, String id, String child) {
		if (!tagHandler.bindTags.isEmpty()) {
			for (BindTagHandler bind : tagHandler.bindTags) {
				appendScript(bind.getDelegateFunction(id, child));
			}
		}
	}

	protected void appendValidator(Tag tag) throws JspException, IOException {
		if (validatorTag != null) {
			tag.addAttribute("vldt-req", "true")
				.addAttribute("vldt-min-l", validatorTag.getMinLength())
				.addAttribute("vldt-max-l", validatorTag.getMaxLength())
				.addAttribute("vldt-regex", validatorTag.getRegex())
				.addAttribute("vldt-text", getTagValue(validatorTag.getText()))
				.addAttribute("vldt-look", getTagValue(validatorTag.getLook()));
		}
	}
	
	protected void appendPopOver(Tag tag) throws JspException, IOException {
		if (popOverTag != null) {
			tag.addAttribute("data-container", "body")
				.addAttribute("data-toggle", "popover")
				.addAttribute("title", getTagValue(popOverTag.getTitle()))
				.addAttribute("data-placement", popOverTag.getSide())
				.addAttribute("data-content", getTagValue(popOverTag.getContent()));
		}
	}
	
	protected void appendTooltip(Tag tag) throws JspException, IOException {
		if (tooltipTag != null) {
			tag.addAttribute("data-container", "body")
				.addAttribute("data-toggle", "tooltip")
				.addAttribute("title", getTagValue(tooltipTag.getTitle()))
				.addAttribute("data-placement", tooltipTag.getSide());	
		}
	}

	protected void appendRest(Tag tag) throws JspException, IOException {
		tag.addAttribute("rest", rest);
	}
	
	protected void appendEvent(Tag tag) {
		appendEvent(tag, this);
	}

	protected void appendEvent(Tag tag, TagHandler tagHandler) {
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
