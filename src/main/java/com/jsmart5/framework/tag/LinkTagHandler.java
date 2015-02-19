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
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Li;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Ul;
import com.jsmart5.framework.util.SmartUtils;

import static com.jsmart5.framework.tag.JsConstants.*;

public final class LinkTagHandler extends SmartTagHandler {
	
	static final String JUSTIFIED = "justified";
	
	static final String LARGE = "large";

	static final String SMALL = "small";

	static final String XSMALL = "xsmall";

	private String label;

	private Integer length;

	private boolean ellipsize;

	private String outcome;

	private String action;

	private String update;

	private String beforeAjax;

	private String afterAjax;

	private Integer tabIndex;
	
	private String size;

	private boolean async = true;
	
	private boolean dropUp;

	private List<DropTagHandler> drops;

	public LinkTagHandler() {
		drops = new ArrayList<DropTagHandler>();
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
		if (size != null && !size.equalsIgnoreCase(XSMALL) && !size.equalsIgnoreCase(SMALL) 
				&& !size.equalsIgnoreCase(LARGE) && !size.equalsIgnoreCase(JUSTIFIED)) {
			throw new JspException("Invalid size value for link tag. Valid values are " + XSMALL + ", " + SMALL + ", " + LARGE + ", " + JUSTIFIED);
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		StringWriter sw = new StringWriter();

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}
		
		if (id == null) {
			id = getRandonId();
		}

		Div linkGroup = null;

		if (!drops.isEmpty()) {
			linkGroup = new Div();
			linkGroup.addAttribute("class", Bootstrap.BUTTON_GROUP)
				.addAttribute("role", "group");
			
			if (XSMALL.equalsIgnoreCase(size)) {
				linkGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_XSMALL);
			} else if (SMALL.equalsIgnoreCase(size)) {
				linkGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_SMALL);
			} else if (LARGE.equalsIgnoreCase(size)) {
				linkGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_LARGE);
			} else if (JUSTIFIED.equalsIgnoreCase(size)) {
				linkGroup.addAttribute("class", Bootstrap.BUTTON_GROUP_JUSTIFIED);
			}
			
			if (dropUp) {
				linkGroup.addAttribute("class", Bootstrap.DROPUP);
			}
		}
		
		A link = new A();
		link.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.BUTTON)
			.addAttribute("tabindex", tabIndex)
			.addAttribute("class", Bootstrap.BUTTON_LINK);

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
		
		if (iconTag != null && IconTagHandler.LEFT.equalsIgnoreCase(iconTag.getSide())) {
			link.addText(getIconTag());
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
		
		if (iconTag != null && IconTagHandler.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
			link.addText(getIconTag());
		}
		
		if (XSMALL.equalsIgnoreCase(size)) {
			link.addAttribute("class", Bootstrap.BUTTON_XSMALL);
		} else if (SMALL.equalsIgnoreCase(size)) {
			link.addAttribute("class", Bootstrap.BUTTON_SMALL);
		} else if (LARGE.equalsIgnoreCase(size)) {
			link.addAttribute("class", Bootstrap.BUTTON_LARGE);
		} else if (JUSTIFIED.equalsIgnoreCase(size)) {
			link.addAttribute("class", Bootstrap.BUTTON_JUSTIFIED);
		}

		appendEvent(link);
		
		if (linkGroup != null) {
			link.addAttribute("data-toggle", "dropdown")
				.addAttribute("class", Bootstrap.DROPDOWN_TOGGLE)
				.addAttribute("role", "button")
				.addAttribute("aria-expanded", false);
			
			Span caret = new Span();
			caret.addAttribute("class", Bootstrap.CARET);
			link.addTag(caret);

			linkGroup.addTag(link);
		}

		// Add the style class at last
		link.addAttribute("class", styleClass);
		
		if (action != null || update != null || beforeAjax != null || afterAjax != null) {
			appendScript(getFunction(id, action, url, params));
		}

		if (linkGroup != null) {

			Ul ul = new Ul();
			ul.addAttribute("role", "menu")
				.addAttribute("class", Bootstrap.DROPDOWN_MENU);
			linkGroup.addTag(ul);

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
				
				appendScript(getFunction(drop.getId(), drop.getAction(), url, drop.getParams()));
			}
		}

		printOutput(linkGroup != null ? linkGroup.getHtml() : link.getHtml());
	}
	
	private StringBuilder getFunction(String id, String action, String url, Map<String, Object> params) {
		JsonAjax jsonAjax = new JsonAjax();
		jsonAjax.setId(id);
		jsonAjax.setAsync(async);

		if (action != null) {
			jsonAjax.setMethod("post");
			jsonAjax.setAction(getTagName(J_SBMT, action));

			for (String name : params.keySet()) {						
				jsonAjax.getParams().add(new JsonParam(name, params.get(name)));
			}

			if (update == null && afterAjax == null) {
				jsonAjax.setUrl(url);
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
		builder.append(JSMART_BUTTON_NEW.format(getNewJsonValue(jsonAjax)) + "return false;");
		return getFunction(id, EVENT_CLICK, builder);
	}

	void addDrop(DropTagHandler drop) {
		this.drops.add(drop);
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

	public void setBeforeAjax(String beforeAjax) {
		this.beforeAjax = beforeAjax;
	}

	public void setAfterAjax(String afterAjax) {
		this.afterAjax = afterAjax;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public void setDropUp(boolean dropUp) {
		this.dropUp = dropUp;
	}

}
