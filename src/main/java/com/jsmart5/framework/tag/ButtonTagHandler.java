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
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.json.JsonAjax;
import com.jsmart5.framework.json.JsonParam;
import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Button;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Event;
import com.jsmart5.framework.tag.type.Look;
import com.jsmart5.framework.tag.type.Size;

public final class ButtonTagHandler extends SmartTagHandler {

	private String look;

	private String label;

	private Integer length;

	private boolean ellipsize;

	private String size;

	private String action;
	
	private boolean ajax;

	private String update;

	private String beforeSend;

	private String onError;
	
	private String onSuccess;
	
	private String onComplete;

	private Integer tabIndex;

	private boolean reset;

	private boolean disabled;

	private DropMenuTagHandler dropMenu;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof InputTagHandler) {
			((InputTagHandler) parent).setChildAddOn(this);
			return false;
			
		} else if (parent instanceof UploadTagHandler) {
			((UploadTagHandler) parent).setChildAddOn(this);
			return false;

		} else if (parent instanceof SelectTagHandler) {
			((SelectTagHandler) parent).setChildAddOn(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (size != null && !Size.validate(size)) {
			throw InvalidAttributeException.fromPossibleValues("button", "size", Size.getValues());
		}
		if (look != null && !Look.validateButton(look)) {
			throw InvalidAttributeException.fromPossibleValues("button", "look", Look.getButtonValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

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

		if (dropMenu != null || inputAddOn) {
			buttonGroup = new Div();
			
			if (inputAddOn) {
				buttonGroup.addAttribute("class", Bootstrap.INPUT_GROUP_BUTTON);
			} else {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP);
			}
			
			if (Size.XSMALL.name().equalsIgnoreCase(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_XSMALL);
			} else if (Size.SMALL.name().equalsIgnoreCase(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_SMALL);
			} else if (Size.LARGE.name().equalsIgnoreCase(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_LARGE);
			} else if (Size.JUSTIFIED.name().equalsIgnoreCase(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_JUSTIFIED);
			}
			
			if (dropMenu != null && dropMenu.isDropUp()) {
				buttonGroup.addAttribute("class", Bootstrap.DROPUP);
			}
		}

		Button button = new Button();
		button.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.BUTTON)
			.addAttribute("tabindex", tabIndex)
			.addAttribute("disabled", disabled ? "disabled" : null);
		
		String lookVal = Bootstrap.BUTTON_DEFAULT;
		
		if (Look.PRIMARY.name().equalsIgnoreCase(look)) {
			lookVal = Bootstrap.BUTTON_PRIMARY;
		} else if (Look.SUCCESS.name().equalsIgnoreCase(look)) {
			lookVal = Bootstrap.BUTTON_SUCCESS;
		} else if (Look.INFO.name().equalsIgnoreCase(look)) {
			lookVal = Bootstrap.BUTTON_INFO;
		} else if (Look.WARNING.name().equalsIgnoreCase(look)) {
			lookVal = Bootstrap.BUTTON_WARNING;
		} else if (Look.DANGER.name().equalsIgnoreCase(look)) {
			lookVal = Bootstrap.BUTTON_DANGER;
		} else if (Look.LINK.name().equalsIgnoreCase(look)) {
			lookVal = Bootstrap.BUTTON_LINK;
		}

		button.addAttribute("class", lookVal);
			
		if (Size.XSMALL.name().equalsIgnoreCase(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_XSMALL);
		} else if (Size.SMALL.name().equalsIgnoreCase(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_SMALL);
		} else if (Size.LARGE.name().equalsIgnoreCase(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_LARGE);
		} else if (Size.JUSTIFIED.name().equalsIgnoreCase(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_JUSTIFIED);
		}

		for (IconTagHandler iconTag : iconTags) {
			if (IconTagHandler.LEFT.equalsIgnoreCase(iconTag.getSide())) {
				button.addTag(iconTag.executeTag());
				button.addText(" ");
			}
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

		for (IconTagHandler iconTag : iconTags) {
			if (IconTagHandler.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
				button.addText(" ");
				button.addTag(iconTag.executeTag());
			}
		}

		if (buttonGroup != null) {
			buttonGroup.addTag(button);
		}

		if (dropMenu != null) {
			Span caret = new Span();
			caret.addAttribute("class", Bootstrap.CARET);

			if (dropMenu.isSegmented()) {
				Button dropDown = new Button();
				dropDown.addAttribute("type", "button")
					.addAttribute("class", Bootstrap.BUTTON)
					.addAttribute("class", lookVal)
					.addAttribute("role", "button")
					.addAttribute("class", Bootstrap.DROPDOWN_TOGGLE)
					.addAttribute("class", disabled ? Bootstrap.DISABLED : null)
					.addAttribute("data-toggle", "dropdown")
					.addAttribute("aria-expanded", false);

				dropDown.addTag(caret);
				buttonGroup.addTag(dropDown);
			} else {
				button.addAttribute("role", "button")
					.addAttribute("class", Bootstrap.DROPDOWN_TOGGLE)
					.addAttribute("data-toggle", "dropdown")
					.addAttribute("aria-expanded", false);

				button.addText(" ");
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
			appendScript(id, getFunction(id, action, params));
		} else {
			if (action != null) {
				button.addAttribute("name", getTagName(J_SBMT, action));
			}
			if (beforeSend != null) {
				appendScript(id, getExecFunction());
			}
		}

		if (dropMenu != null) {
			Tag ul = dropMenu.executeTag();
			ul.addAttribute("class", disabled ? Bootstrap.DISABLED : null);
			buttonGroup.addTag(ul);
		}

		appendBind(id);

		return buttonGroup != null ? buttonGroup : button;
	}
	
	private StringBuilder getExecFunction() {
		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_EXEC.format(beforeSend.trim()));	
		return getBindFunction(id, Event.CLICK.name(), builder);
	}
	
	private StringBuilder getFunction(String id, String action, Map<String, Object> params) {
		JsonAjax jsonAjax = new JsonAjax();
		jsonAjax.setId(id);

		if (action != null) {
			jsonAjax.setMethod("post");
			jsonAjax.setAction(getTagName(J_SBMT, action));

			for (String name : params.keySet()) {						
				jsonAjax.addParam(new JsonParam(name, params.get(name)));
			}
		} else if (update != null) {
			jsonAjax.setMethod("get");
		}
		if (update != null) {
			jsonAjax.setUpdate(update.trim());
		}
		if (beforeSend != null) {
			jsonAjax.setBefore((String) getTagValue(beforeSend.trim()));
		}
		if (onError != null) {
			jsonAjax.setError((String) getTagValue(onError.trim()));
		}
		if (onSuccess != null) {
			jsonAjax.setSuccess((String) getTagValue(onSuccess.trim()));
		}
		if (onComplete != null) {
			jsonAjax.setComplete((String) getTagValue(onComplete.trim()));
		}

		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_BUTTON.format(getJsonValue(jsonAjax)) + "return false;");
		return getBindFunction(id, Event.CLICK.name(), builder);
	}

	void setDropMenu(DropMenuTagHandler dropMenu) {
		this.dropMenu = dropMenu;
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

	public void setBeforeSend(String beforeSend) {
		this.beforeSend = beforeSend;
	}

	public void setOnError(String onError) {
		this.onError = onError;
	}

	public void setOnSuccess(String onSuccess) {
		this.onSuccess = onSuccess;
	}

	public void setOnComplete(String onComplete) {
		this.onComplete = onComplete;
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

}