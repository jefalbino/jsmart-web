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

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.json.Ajax;
import com.jsmart5.framework.json.Param;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.A;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Align;
import com.jsmart5.framework.tag.type.Event;
import com.jsmart5.framework.tag.type.Size;
import com.jsmart5.framework.util.SmartUtils;

import static com.jsmart5.framework.tag.js.JsConstants.*;

public final class LinkTagHandler extends TagHandler {

	private String label;

	private Integer length;

	private boolean ellipsize;

	private String outcome;

	private String action;

	private String update;

	private String beforeSend;
	
	private String onError;
	
	private String onSuccess;

	private String onComplete;

	private Integer tabIndex;
	
	private String size;
	
	private boolean disabled;

	private DropMenuTagHandler dropMenu;

	@Override
	public void validateTag() throws JspException {
		if (size != null && !Size.validate(size)) {
			throw InvalidAttributeException.fromPossibleValues("link", "size", Size.getValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		StringWriter sw = new StringWriter();

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}
		
		setRandomId("link");

		Div linkGroup = null;

		if (dropMenu != null) {
			linkGroup = new Div();
			linkGroup.addAttribute("class", Bootstrap.BUTTON_GROUP)
				.addAttribute("role", "group");
			
			if (Size.XSMALL.equalsIgnoreCase(size)) {
				linkGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_XSMALL);
			} else if (Size.SMALL.equalsIgnoreCase(size)) {
				linkGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_SMALL);
			} else if (Size.LARGE.equalsIgnoreCase(size)) {
				linkGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_LARGE);
			} else if (Size.JUSTIFIED.equalsIgnoreCase(size)) {
				linkGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_JUSTIFIED);
			}
			
			if (dropMenu.isDropUp()) {
				linkGroup.addAttribute("class", Bootstrap.DROPUP);
			}
		}
		
		A link = new A();
		link.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.BUTTON)
			.addAttribute("tabindex", tabIndex)
			.addAttribute("class", Bootstrap.BUTTON_LINK)
			.addAttribute("class", disabled ? Bootstrap.DISABLED : null);

		StringBuilder urlParams = new StringBuilder("?");
		for (String key : params.keySet()) {
			urlParams.append(key + "=" + params.get(key) + "&");
		}

		String url = "";
		String href = "#";
		String outcomeVal = SmartUtils.decodePath((String) getTagValue(outcome)); 

		if (outcomeVal != null) {
			url = (outcomeVal.startsWith("/") ? outcomeVal.replaceFirst("/", "") : outcomeVal) + urlParams.substring(0, urlParams.length() -1);
			href = (!url.startsWith("http") && !url.startsWith("mailto") ? getRequest().getContextPath() + "/" : "") + url;
		}

		link.addAttribute("href", href);
		
		for (IconTagHandler iconTag : iconTags) {
			if (Align.LEFT.equalsIgnoreCase(iconTag.getSide())) {
				link.addTag(iconTag.executeTag());
				link.addText(" ");
			}
		}

		Object val = getTagValue(label);
		if (val != null && val instanceof String) {
			if (length != null && length > 0 && val.toString().length() >= length) {
				if (ellipsize && length > 4) {
					val = val.toString().substring(0, length - 4) + " ...";
				} else {
					val = val.toString().substring(0, length);
				}
			}
			link.addText((String) val);

		} else if (!sw.toString().isEmpty()) {
			link.addText(sw.toString());
		} else {
			link.addText(href);
		}
		
		for (IconTagHandler iconTag : iconTags) {
			if (Align.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
				link.addText(" ");
				link.addTag(iconTag.executeTag());
			}
		}

		if (Size.XSMALL.equalsIgnoreCase(size)) {
			link.addAttribute("class", Bootstrap.BUTTON_XSMALL);
		} else if (Size.SMALL.equalsIgnoreCase(size)) {
			link.addAttribute("class", Bootstrap.BUTTON_SMALL);
		} else if (Size.LARGE.equalsIgnoreCase(size)) {
			link.addAttribute("class", Bootstrap.BUTTON_LARGE);
		} else if (Size.JUSTIFIED.equalsIgnoreCase(size)) {
			link.addAttribute("class", Bootstrap.BUTTON_JUSTIFIED);
		}

		appendEvent(link);
		
		if (linkGroup != null) {
			link.addAttribute("data-toggle", "dropdown")
				.addAttribute("class", Bootstrap.DROPDOWN_TOGGLE)
				.addAttribute("class", disabled ? Bootstrap.DISABLED : null)
				.addAttribute("role", "button")
				.addAttribute("aria-expanded", false);
			
			Span caret = new Span();
			caret.addAttribute("class", Bootstrap.CARET);

			link.addText(" ");
			link.addTag(caret);

			linkGroup.addTag(link);
		}

		// Add the style class at last
		link.addAttribute("class", styleClass);
		
		if (action != null) {		
			appendScript(getFunction(url));
		} else {
			if (beforeSend != null) {
				appendScript(getExecFunction());
			}
		}

		if (dropMenu != null) {
			Tag ul = dropMenu.executeTag();
			ul.addAttribute("class", disabled ? Bootstrap.DISABLED : null);
			linkGroup.addTag(ul);
		}

		appendBind(id);

		return linkGroup != null ? linkGroup : link;
	}
	
	private StringBuilder getExecFunction() {
		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_EXEC.format(beforeSend.trim()));	
		return getBindFunction(id, Event.CLICK.name(), builder);
	}
	
	private StringBuilder getFunction(String url) {
		Ajax jsonAjax = new Ajax();
		jsonAjax.setId(id);

		if (action != null) {
			jsonAjax.setMethod("post");
			jsonAjax.setAction(getTagName(J_SBMT, action));

			for (String name : params.keySet()) {						
				jsonAjax.addParam(new Param(name, params.get(name)));
			}

			if (update == null && onError == null && onSuccess == null && onComplete == null) {
				jsonAjax.setUrl(url);
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

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public void setEllipsize(boolean ellipsize) {
		this.ellipsize = ellipsize;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
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

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
