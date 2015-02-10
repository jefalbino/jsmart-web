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

package com.jsmart5.framework.tag;

import static com.jsmart5.framework.tag.JsConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.json.JsonAjax;
import com.jsmart5.framework.json.JsonParam;
import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.A;
import com.jsmart5.framework.tag.html.Button;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Li;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Ul;

public final class ButtonTagHandler extends SmartTagHandler {

	static final String JUSTIFIED = "justified";
	
	static final String LARGE = "large";

	static final String SMALL = "small";

	static final String XSMALL = "xsmall";
	
	private static final String DEFAULT = "default";

	private static final String PRIMARY = "primary";

	private static final String SUCCESS = "success";
	
	private static final String INFO = "info";

	private static final String WARNING = "warning";

	private static final String DANGER = "danger";
	
	private static final String LINK = "link";

	private String render;

	private String label;

	private Integer length;

	private boolean ellipsize;

	private String size;

	private String icon;

	private String action;
	
	private boolean ajax;

	private String update;

	private String beforeAjax;

	private String afterAjax;

	private Integer tabIndex;

	private boolean reset;

	private boolean disabled;

	private boolean async = true;

	private List<SmartTagHandler> actionItems;

	public ButtonTagHandler() {
		actionItems = new ArrayList<SmartTagHandler>();
	}

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof GridTagHandler) {

			((GridTagHandler) parent).addTag(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (size != null && !size.equals(XSMALL) && !size.equals(SMALL) && !size.equals(LARGE) && !size.equals(JUSTIFIED)) {
			throw new JspException("Invalid size value for button tag. Valid values are " + XSMALL + ", " + SMALL + ", " + LARGE + ", " + JUSTIFIED);
		}
		if (render != null && !render.equals(DEFAULT) && !render.equals(PRIMARY) && !render.equals(SUCCESS) && !render.equals(INFO) 
				&& !render.equals(WARNING) && !render.equals(DANGER) && !render.equals(LINK)) {
			throw new JspException("Invalid render value for button tag. Valid values are " + DEFAULT + ", " + PRIMARY + ", " + SUCCESS + ", " + INFO
					+ ", " + WARNING + ", " + DANGER + ", " + LINK);
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		if (id == null) {
			id = getRandonId();
		}

		Div buttonGroup = null;

		if (!actionItems.isEmpty()) {
			buttonGroup = new Div();
			buttonGroup.addAttribute("role", "group")
				.addAttribute("class", Bootstrap.BUTTON_GROUP);
			
			if (XSMALL.equals(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_XSMALL);
			} else if (SMALL.equals(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_SMALL);
			} else if (LARGE.equals(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_LARGE);
			} else if (JUSTIFIED.equals(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_JUSTIFIED);
			}
		}

		Button button = new Button();
		button.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.BUTTON)
			.addAttribute("tabindex", tabIndex)
			.addAttribute("disabled", disabled || isEditRowTagEnabled() ? "disabled" : null);
		
		String renderVal = Bootstrap.BUTTON_DEFAULT;
		
		if (PRIMARY.equals(render)) {
			renderVal = Bootstrap.BUTTON_PRIMARY;
		} else if (SUCCESS.equals(render)) {
			renderVal = Bootstrap.BUTTON_SUCCESS;
		} else if (INFO.equals(render)) {
			renderVal = Bootstrap.BUTTON_INFO;
		} else if (WARNING.equals(render)) {
			renderVal = Bootstrap.BUTTON_WARNING;
		} else if (DANGER.equals(render)) {
			renderVal = Bootstrap.BUTTON_DANGER;
		} else if (LINK.equals(render)) {
			renderVal = Bootstrap.BUTTON_LINK;
		}

		button.addAttribute("class", renderVal);
			
		if (XSMALL.equals(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_XSMALL);
		} else if (SMALL.equals(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_SMALL);
		} else if (LARGE.equals(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_LARGE);
		} else if (JUSTIFIED.equals(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_JUSTIFIED);
		}
		
		if (icon != null) {
			Span span = new Span();
			span.addAttribute("class", Bootstrap.GLYPHICON)
					.addAttribute("class", icon)
					.addAttribute("aria-hidden", "true");
			button.addTag(span);
		}

		String val = (String) getTagValue(label);
		if (val != null && length != null && length > 0 && val.length() >= length) {
			if (ellipsize && length > 4) {
				val = val.substring(0, length - 4) + " ...";
			} else {
				val = val.substring(0, length);
			}
		}
		button.addText(val);
		
		
//		 <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
//	        <span class="caret"></span>
//	        <span class="sr-only">Toggle Dropdown</span>
//	      </button>
	      
		if (buttonGroup != null) {
			Button dropDown = new Button();
			dropDown.addAttribute("type", "button")
				.addAttribute("class", Bootstrap.BUTTON)
				.addAttribute("class", renderVal)
				.addAttribute("class", Bootstrap.DROPDOWN_TOGGLE)
				.addAttribute("data-toggle", "dropdown")
				.addAttribute("aria-expanded", false);
			
			Span caret = new Span();
			caret.addAttribute("class", Bootstrap.DROPDOWN_CARET);
			dropDown.addTag(caret);
			
			Span srOnly = new Span();
			srOnly.addAttribute("class", Bootstrap.SR_ONLY)
				.addText("Toggle Dropdown");
			dropDown.addTag(srOnly);

			buttonGroup.addTag(button);
			buttonGroup.addTag(dropDown);
		}

		// Add the style class at last
		button.addAttribute("class", styleClass);

		appendEvent(button);

		if (ajax) {
			button.addAttribute("type", "button");
		} else if (action != null) {
			button.addAttribute("type", "submit");
		} else if (reset) {
			button.addAttribute("type", "reset");
		} else {
			button.addAttribute("type", "button");
		}

		if (ajax) {		
			appendScript(getFunction(id, action, params));
		} else {
			if (action != null) {
				button.addAttribute("name", getTagName(J_SBMT, action));
			}
			if (beforeAjax != null) {
				appendScript(getFunction(id, beforeAjax));
			}
		}

		if (buttonGroup != null) {
			
			Ul ul = new Ul();
			ul.addAttribute("role", "menu")
				.addAttribute("class", Bootstrap.DROPDOWN_MENU);
			buttonGroup.addTag(ul);

			for (SmartTagHandler actionItem : actionItems) {
				
				if (actionItem instanceof SeparatorTagHandler) {
					Li li = new Li();
					li.addAttribute("class", Bootstrap.DIVIDER);
					ul.addTag(li);
					
				} else if (actionItem instanceof ButtonActionTagHandler) {

					ButtonActionTagHandler buttonActionItem = (ButtonActionTagHandler) actionItem;

					if (buttonActionItem.getId() == null) {
						buttonActionItem.setId(getRandonId());
					}

					Li li = new Li();
					li.addAttribute("id", buttonActionItem.getId());
					ul.addTag(li);

					A a = new A();
					a.addAttribute("href", "#")
						.addText((String) getTagValue(buttonActionItem.getLabel()));
					li.addTag(a);
					
					appendScript(getFunction(buttonActionItem.getId(), buttonActionItem.getAction(), buttonActionItem.getParams()));
				}
			}
		}

		printOutput(buttonGroup != null ? buttonGroup.getHtml() : button.getHtml());
	}
	
	private StringBuilder getFunction(String id, String exec) {
		StringBuilder builder = new StringBuilder();
		builder.append("$('#").append(id).append("').bind('").append(EVENT_CLICK).append("', function(){");

		builder.append(JSMART_EXEC.format(exec) + "return false;");

		builder.append("});");
		return builder;
	}
	
	private StringBuilder getFunction(String id, String action, Map<String, Object> params) {
		StringBuilder builder = new StringBuilder();
		builder.append("$('#").append(id).append("').bind('").append(EVENT_CLICK).append("', function(){");
		
		JsonAjax jsonAjax = new JsonAjax();
		jsonAjax.setId(id);
		jsonAjax.setAsync(async);

		if (action != null) {
			jsonAjax.setMethod("post");
			jsonAjax.setAction(getTagName(J_SBMT, action));

			for (String name : params.keySet()) {						
				jsonAjax.getParams().add(new JsonParam(name, params.get(name)));
			}
		} else if (update != null) {
			jsonAjax.setMethod("get");
		}
		if (update != null) {
			jsonAjax.setUpdate(update.trim());
		}
		if (beforeAjax != null) {
			jsonAjax.setBefore(beforeAjax.trim());
		}
		if (afterAjax != null) {
			jsonAjax.setExec(afterAjax.trim());
		}

		builder.append(JSMART_BUTTON_NEW.format(getNewJsonValue(jsonAjax)) + "return false;");

		builder.append("});");
		return builder;
	}

	void addActionItem(SmartTagHandler actionItem) {
		this.actionItems.add(actionItem);
	}

	public void setRender(String render) {
		this.render = render;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public void setEllipsize(boolean ellipsize) {
		this.ellipsize = ellipsize;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public void setBeforeAjax(String beforeAjax) {
		this.beforeAjax = beforeAjax;
	}

	public void setAfterAjax(String afterAjax) {
		this.afterAjax = afterAjax;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

}