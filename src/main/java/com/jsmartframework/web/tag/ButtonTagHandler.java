/*
 * JSmart Framework - Java Web Development Framework
 * Copyright (c) 2014, Jeferson Albino da Silva, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
*/

package com.jsmartframework.web.tag;

import com.jsmartframework.web.exception.InvalidAttributeException;
import com.jsmartframework.web.json.Ajax;
import com.jsmartframework.web.json.Param;
import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.css.Bootstrap;
import com.jsmartframework.web.tag.css.JSmart;
import com.jsmartframework.web.tag.html.Button;
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Span;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Align;
import com.jsmartframework.web.tag.type.Event;
import com.jsmartframework.web.tag.type.Look;
import com.jsmartframework.web.tag.type.Size;
import com.jsmartframework.web.tag.util.RefAction;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;

import static com.jsmartframework.web.tag.js.JsConstants.JSMART_AJAX;

public final class ButtonTagHandler extends TagHandler {

    private String onForm;

	private String look;

	private String label;

	private Integer length;

	private boolean ellipsize;

	private String size;

	private String action;

	private String update;

	private String beforeSend;

	private String onError;
	
	private String onSuccess;
	
	private String onComplete;

	private Integer tabIndex;

	private boolean reset;

	private DropMenuTagHandler dropMenu;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof InputTagHandler) {
            ((InputTagHandler) parent).addChildAddOn(this);
            return false;

        } else if (parent instanceof AutoCompleteTagHandler) {
            ((AutoCompleteTagHandler) parent).addChildAddOn(this);
            return false;

		} else if (parent instanceof DateTagHandler) {
			((DateTagHandler) parent).addChildAddOn(this);
			return false;

		} else if (parent instanceof UploadTagHandler) {
			((UploadTagHandler) parent).addChildAddOn(this);
			return false;

		} else if (parent instanceof SelectTagHandler) {
			((SelectTagHandler) parent).addChildAddOn(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (size != null && !Size.validate(size)) {
			throw InvalidAttributeException.fromPossibleValues("button", "size", Size.getValues());
		}
		if (look != null && !Look.validateButton(look) && !isEL(look)) {
			throw InvalidAttributeException.fromPossibleValues("button", "look", Look.getButtonValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		JspTag parent = getParent();
		boolean inputAddOn = parent instanceof InputTagHandler
                || parent instanceof AutoCompleteTagHandler
                || parent instanceof DateTagHandler || parent instanceof SelectTagHandler
                || parent instanceof UploadTagHandler;

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		setRandomId("button");

		Div buttonGroup = null;

		if (dropMenu != null || inputAddOn) {
			buttonGroup = new Div();
			
			if (inputAddOn) {
				buttonGroup.addAttribute("class", Bootstrap.INPUT_GROUP_BUTTON);
			} else {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP);
			}
			
			if (Size.XSMALL.equalsIgnoreCase(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_XSMALL);
			} else if (Size.SMALL.equalsIgnoreCase(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_SMALL);
			} else if (Size.LARGE.equalsIgnoreCase(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_LARGE);
			} else if (Size.JUSTIFIED.name().equalsIgnoreCase(size)) {
				buttonGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_JUSTIFIED);
			}
			
			if (dropMenu != null && dropMenu.isDropUp()) {
				buttonGroup.addAttribute("class", Bootstrap.DROPUP);
			}
		}

        boolean disabled = isDisabled();

		Button button = new Button();
		button.addAttribute("style", getTagValue(style))
			.addAttribute("class", Bootstrap.BUTTON)
			.addAttribute("tabindex", tabIndex)
			.addAttribute("disabled", disabled ? "disabled" : null);

		appendRefId(button, id);

		String lookVal = (String) getTagValue(look);
		button.addAttribute("class", getButtonLook(lookVal));
			
		if (Size.XSMALL.equalsIgnoreCase(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_XSMALL);
		} else if (Size.SMALL.equalsIgnoreCase(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_SMALL);
		} else if (Size.LARGE.equalsIgnoreCase(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_LARGE);
		} else if (Size.JUSTIFIED.equalsIgnoreCase(size)) {
			button.addAttribute("class", Bootstrap.BUTTON_JUSTIFIED);
		}

		for (IconTagHandler iconTag : iconTags) {
			if (Align.LEFT.equalsIgnoreCase(iconTag.getSide())) {
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
			if (Align.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
				button.addText(" ");
				button.addTag(iconTag.executeTag());
			}
		}

		if (buttonGroup != null) {
			buttonGroup.addTag(button);
		}

		if (loadTag != null) {
			Tag tag = loadTag.executeTag();
			tag.addAttribute("style", "display: none;");
			button.addTag(tag);
		}

		if (dropMenu != null) {
			Span caret = new Span();
			caret.addAttribute("class", Bootstrap.CARET);

			if (dropMenu.isSegmented()) {
				Button dropDown = new Button();
				dropDown.addAttribute("type", "button")
					.addAttribute("class", Bootstrap.BUTTON)
					.addAttribute("class", getButtonLook(lookVal))
					.addAttribute("role", "button")
					.addAttribute("class", Bootstrap.DROPDOWN_TOGGLE)
					.addAttribute("class", disabled ? Bootstrap.DISABLED : null)
                    .addAttribute("class", JSmart.BUTTON_DROPDOWN_TOGGLE)
					.addAttribute("data-toggle", "dropdown")
					.addAttribute("aria-expanded", false);

				dropDown.addText("&zwnj;").addTag(caret);
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
		button.addAttribute("class", getTagValue(styleClass));

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
			appendDocScript(getFunction(id, action, params));
		} else if (action != null) {
			button.addAttribute("name", getTagName(J_SBMT, action));
		}

		if (dropMenu != null) {
			Tag ul = dropMenu.executeTag();
			ul.addAttribute("class", disabled ? Bootstrap.DISABLED : null);
			buttonGroup.addTag(ul);
		}

		appendBind(id);
		appendAjax(id);
		
		if (buttonGroup != null) {
			appendTooltip(buttonGroup);
			appendPopOver(buttonGroup);
		} else {
			appendTooltip(button);
			appendPopOver(button);
		}

		return buttonGroup != null ? buttonGroup : button;
	}

	private String getButtonLook(String lookVal) {
		String buttonLook = Bootstrap.BUTTON_DEFAULT;

		if (Look.PRIMARY.equalsIgnoreCase(lookVal)) {
			buttonLook = Bootstrap.BUTTON_PRIMARY;
		} else if (Look.SUCCESS.equalsIgnoreCase(lookVal)) {
			buttonLook = Bootstrap.BUTTON_SUCCESS;
		} else if (Look.INFO.equalsIgnoreCase(lookVal)) {
			buttonLook = Bootstrap.BUTTON_INFO;
		} else if (Look.WARNING.equalsIgnoreCase(lookVal)) {
			buttonLook = Bootstrap.BUTTON_WARNING;
		} else if (Look.DANGER.equalsIgnoreCase(lookVal)) {
			buttonLook = Bootstrap.BUTTON_DANGER;
		} else if (Look.LINK.equalsIgnoreCase(lookVal)) {
			buttonLook = Bootstrap.BUTTON_LINK;
		}
		return buttonLook;
	}
	
	@SuppressWarnings("unchecked")
	private StringBuilder getFunction(String id, String action, Map<String, Object> params) {
		Ajax jsonAjax = new Ajax();
		jsonAjax.setId(id);
        jsonAjax.setForm(onForm);
		jsonAjax.setTag("button");

        // Params must be considered regardless the action for rest purpose
        for (String name : params.keySet()) {
            jsonAjax.addParam(new Param(name, params.get(name)));
        }

		if (action != null) {
			jsonAjax.setMethod("post");
			jsonAjax.setAction(getTagName(J_SBMT, action));

            if (!args.isEmpty()) {
                String argName = getTagName(J_SBMT_ARGS, action);
                for (Object arg : args.keySet()) {
                    jsonAjax.addArg(new Param(argName, arg, args.get(arg)));
                }
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
		
		// It means that the ajax is inside some iterator tag, so the
		// ajax actions will be set by iterator tag and the event bind
		// will use the id as tag attribute
		Stack<RefAction> actionStack = (Stack<RefAction>) getMappedValue(DELEGATE_TAG_PARENT);
		if (actionStack != null) {
			actionStack.peek().addRef(id, Event.CLICK.name(), jsonAjax);
			
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append(JSMART_AJAX.format(getJsonValue(jsonAjax)));
			return getBindFunction(id, Event.CLICK.name(), builder);
		}

		return null;
	}

	void setDropMenu(DropMenuTagHandler dropMenu) {
		this.dropMenu = dropMenu;
	}

    public void setOnForm(String onForm) {
        this.onForm = onForm;
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

	public void setReset(boolean reset) {
		this.reset = reset;
	}

}