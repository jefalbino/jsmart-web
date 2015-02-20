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

import static com.jsmart5.framework.tag.js.JsConstants.*;

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

public final class DropDownTagHandler extends SmartTagHandler {

	static final String JUSTIFIED = "justified";
	
	static final String LARGE = "large";

	static final String SMALL = "small";

	static final String XSMALL = "xsmall";

	private String look;

	private String label;

	private Integer length;

	private boolean ellipsize;

	private String size;

	private String action;
	
	private boolean ajax;

	private String update;

	private String beforeAjax;

	private String afterAjax;

	private Integer tabIndex;

	private boolean reset;

	private boolean disabled;

	private boolean async = true;

	private boolean dropSegmented;

	private boolean dropUp;

	private List<DropTagHandler> drops;

	public DropDownTagHandler() {
		drops = new ArrayList<DropTagHandler>();
	}

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof InputTagHandler) {
			((InputTagHandler) parent).setChildAddOn(this);
			return false;

		} else if (parent instanceof SelectTagHandler) {
			((SelectTagHandler) parent).setChildAddOn(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (size != null && !size.equalsIgnoreCase(XSMALL) && !size.equalsIgnoreCase(SMALL) 
				&& !size.equalsIgnoreCase(LARGE) && !size.equalsIgnoreCase(JUSTIFIED)) {
			throw new JspException("Invalid size value for button tag. Valid values are " + XSMALL + ", " + SMALL + ", " + LARGE + ", " + JUSTIFIED);
		}
		if (look != null && !look.equalsIgnoreCase(DEFAULT) && !look.equalsIgnoreCase(PRIMARY) 
				&& !look.equalsIgnoreCase(SUCCESS) && !look.equalsIgnoreCase(INFO) && !look.equalsIgnoreCase(WARNING)
				&& !look.equalsIgnoreCase(DANGER) && !look.equalsIgnoreCase(LINK)) {
			throw new JspException("Invalid look value for button tag. Valid values are " + DEFAULT + ", " + PRIMARY 
					+ ", " + SUCCESS + ", " + INFO + ", " + WARNING + ", " + DANGER + ", " + LINK);
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		JspTag parent = getParent();
		boolean inputAddOn = parent instanceof InputTagHandler || parent instanceof SelectTagHandler;

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		if (id == null) {
			id = getRandonId();
		}

		Div buttonGroup = null;

		if (!drops.isEmpty() || inputAddOn) {
			buttonGroup = new Div();
			
			if (inputAddOn) {
				buttonGroup.addAttribute("class", Bootstrap.INPUT_GROUP_BUTTON);
			} else {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP);
			}
			
			if (XSMALL.equalsIgnoreCase(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_XSMALL);
			} else if (SMALL.equalsIgnoreCase(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_SMALL);
			} else if (LARGE.equalsIgnoreCase(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_LARGE);
			} else if (JUSTIFIED.equalsIgnoreCase(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_JUSTIFIED);
			}
			
			if (dropUp) {
				buttonGroup.addAttribute("class", Bootstrap.DROPUP);
			}
		}

		Button button = new Button();
		button.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.BUTTON)
			.addAttribute("tabindex", tabIndex)
			.addAttribute("disabled", disabled || isEditRowTagEnabled() ? "disabled" : null);
		
		String lookVal = Bootstrap.BUTTON_DEFAULT;
		
		if (PRIMARY.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.BUTTON_PRIMARY;
		} else if (SUCCESS.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.BUTTON_SUCCESS;
		} else if (INFO.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.BUTTON_INFO;
		} else if (WARNING.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.BUTTON_WARNING;
		} else if (DANGER.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.BUTTON_DANGER;
		} else if (LINK.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.BUTTON_LINK;
		}

		button.addAttribute("class", lookVal);
			
		if (XSMALL.equalsIgnoreCase(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_XSMALL);
		} else if (SMALL.equalsIgnoreCase(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_SMALL);
		} else if (LARGE.equalsIgnoreCase(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_LARGE);
		} else if (JUSTIFIED.equalsIgnoreCase(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_JUSTIFIED);
		}
		
		if (iconTag != null && IconTagHandler.LEFT.equalsIgnoreCase(iconTag.getSide())) {
			button.addText(getIconTag());
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

		if (iconTag != null && IconTagHandler.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
			button.addText(getIconTag());
		}

		if (buttonGroup != null) {
			buttonGroup.addTag(button);
		}

		if (!drops.isEmpty()) {
			Span caret = new Span();
			caret.addAttribute("class", Bootstrap.CARET);

			if (dropSegmented) {
				Button dropDown = new Button();
				dropDown.addAttribute("type", "button")
					.addAttribute("class", Bootstrap.BUTTON)
					.addAttribute("class", lookVal)
					.addAttribute("role", "button")
					.addAttribute("class", Bootstrap.DROPDOWN_TOGGLE)
					.addAttribute("data-toggle", "dropdown")
					.addAttribute("aria-expanded", false);

				dropDown.addTag(caret);
				buttonGroup.addTag(dropDown);
			} else {
				button.addAttribute("role", "button")
					.addAttribute("class", Bootstrap.DROPDOWN_TOGGLE)
					.addAttribute("data-toggle", "dropdown")
					.addAttribute("aria-expanded", false);

				button.addTag(caret);
			}
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

		if (!drops.isEmpty()) {
			
			Ul ul = new Ul();
			ul.addAttribute("role", "menu")
				.addAttribute("class", Bootstrap.DROPDOWN_MENU);
			buttonGroup.addTag(ul);

			for (DropTagHandler drop : drops) {

				if (drop.getId() == null) {
					drop.setId(getRandonId());
				}
				
				if (drop.getHeader() != null) {
					Li headerLi = new Li();
					headerLi.addAttribute("role", "presentation")
						.addAttribute("class", Bootstrap.DROPDOWN_HEADER)
						.addText((String) getTagValue(drop.getHeader()));
					ul.addTag(headerLi);
				}

				Li li = new Li();
				li.addAttribute("id", drop.getId())
					.addAttribute("role", "presentation")
					.addAttribute("class", drop.isDisabled() ? Bootstrap.DISABLED : null);
				ul.addTag(li);

				A a = new A();
				a.addAttribute("href", "#")
					.addText((String) getTagValue(drop.getLabel()));
				li.addTag(a);
				
				if (drop.hasDivider()) {
					Li dividerLi = new Li();
					dividerLi.addAttribute("class", Bootstrap.DIVIDER);
					ul.addTag(li);
				}
				
				appendScript(getFunction(drop.getId(), drop.getAction(), drop.getParams()));
				
			}
		}

		printOutput(buttonGroup != null ? buttonGroup.getHtml() : button.getHtml());
	}
	
	private StringBuilder getFunction(String id, String exec) {
		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_EXEC.format(exec) + "return false;");	
		return getFunction(id, EVENT_CLICK, builder);
	}
	
	private StringBuilder getFunction(String id, String action, Map<String, Object> params) {
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

		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_BUTTON.format(getJsonValue(jsonAjax)) + "return false;");
		return getFunction(id, EVENT_CLICK, builder);
	}

	void addDrop(DropTagHandler drop) {
		this.drops.add(drop);
	}

	public void setLook(String look) {
		this.look = look;
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

	public void setDropSegmented(boolean dropSegmented) {
		this.dropSegmented = dropSegmented;
	}

	public void setDropUp(boolean dropUp) {
		this.dropUp = dropUp;
	}

}