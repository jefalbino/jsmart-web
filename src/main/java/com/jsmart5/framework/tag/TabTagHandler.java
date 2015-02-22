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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.A;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Input;
import com.jsmart5.framework.tag.html.Li;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.html.Ul;

import static com.jsmart5.framework.tag.js.JsConstants.*;

public final class TabTagHandler extends SmartTagHandler {

	private static final String STACKED = "stacked";
	
	private static final String REGULAR = "regular";
	
	private String tabStyle;

	private String tabClass;

	private boolean ajax;

	private String value;

	private String pills;

	private boolean justified;
	
	private boolean fade;
	
	private String onShow;
	
	private String onHide;

	private List<TabPaneTagHandler> tabPanes;

	public TabTagHandler() {
		tabPanes = new ArrayList<TabPaneTagHandler>();
	}

	@Override
	public void validateTag() throws JspException {
		if (pills != null && !pills.equalsIgnoreCase(STACKED) && !pills.equalsIgnoreCase(REGULAR)) {
			throw new JspException("Invalid pills value for tab tag. Valid values are " + REGULAR + ", " + STACKED);
		}
		if (STACKED.equalsIgnoreCase(pills) && justified) {
			throw new JspException("Stacked pills value and justified value for tab tag cannot coexist");
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		if (id == null) {
			id = getRandonId();
		}

		Div tab = new Div();
		tab.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", styleClass)
			.addAttribute("role", "tabpanel");

		appendEvent(tab);

		Input input = null;
		if (value != null) {
			input = new Input();
			input.addAttribute("type", "hidden")
				.addAttribute("name", getTagName(J_TAG, value));
			tab.addTag(input);
		}

		Ul ul = new Ul();
		ul.addAttribute("role", "tablist")
			.addAttribute("class", Bootstrap.NAV)
			.addAttribute("style", tabStyle);

		if (pills == null) {
			ul.addAttribute("class", Bootstrap.NAV_TABS);
		} else {
			ul.addAttribute("class", Bootstrap.NAV_PILLS);
			if (STACKED.equalsIgnoreCase(pills)) {
				ul.addAttribute("class", Bootstrap.NAV_STACKED);
			}
		}

		if (justified) {
			ul.addAttribute("class", Bootstrap.NAV_JUSTIFIED);
		}
		
		// At last the custom style class
		ul.addAttribute("class", tabClass);

		Div content = new Div();
		content.addAttribute("class", Bootstrap.TAB_CONTENT);

		tab.addTag(ul)
			.addTag(content);

		Object tabValue = getTagValue(value);

		for (TabPaneTagHandler tabPane : tabPanes) {

			if (tabPane.getId() == null) {
				tabPane.setId(getRandonId());
			}

			// The execute tag must be called first to decide if there are drop down children
			StringWriter swPane = new StringWriter();
			tabPane.setOutputWriter(swPane);
			tabPane.executeTag();

			String liId = getRandonId();

			Li li = new Li();
			li.addAttribute("id", liId)
				.addAttribute("role", "presentation")
				.addAttribute("class", tabPane.isDisabled() ? Bootstrap.DISABLED : null)
				.addAttribute("style", tabPane.getTabStyle())
				.addAttribute("class", tabPane.getTabClass());

			if (!tabPane.getAjaxTags().isEmpty()) {
				for (AjaxTagHandler ajax : tabPane.getAjaxTags()) {
					appendScript(ajax.getFunction(liId));
				}
			}

			A a = new A();

			for (IconTagHandler iconTag : tabPane.getIconTags()) {
				if (IconTagHandler.LEFT.equalsIgnoreCase(iconTag.getSide())) {
					a.addTag(iconTag.executeTag());
					a.addText(" ");
				}
			}

			a.addText(getTagValue(tabPane.getLabel()));

			for (IconTagHandler iconTag : tabPane.getIconTags()) {
				if (IconTagHandler.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
					a.addText(" ");
					a.addTag(iconTag.executeTag());
				}
			}

			// Case drop panes not empty we must include drop down style
			if (!tabPane.getDropPanes().isEmpty()) {
				li.addAttribute("class", Bootstrap.DROPDOWN);
				
				a.addAttribute("role", "button")
					.addAttribute("class", Bootstrap.DROPDOWN_TOGGLE)
					.addAttribute("class", tabPane.isDisabled() ? Bootstrap.DISABLED : null)
					.addAttribute("data-toggle", "dropdown")
					.addAttribute("href", "#")
					.addAttribute("aria-expanded", "false");
				
				Span span = new Span();
				span.addAttribute("class", Bootstrap.CARET);
				a.addTag(span);

			} else {
				a.addAttribute("href", "#" + tabPane.getId())
					.addAttribute("aria-controls", tabPane.getId())
					.addAttribute("class", tabPane.isDisabled() ? Bootstrap.DISABLED : null)
					.addAttribute("role", "tab")
					.addAttribute("data-toggle", "tab");
			}

			li.addTag(a);
			ul.addTag(li);

			// Case dropDowns not empty we must include on drop down li tags
			if (!tabPane.getDropPanes().isEmpty()) {
				
				Ul dropUl = new Ul();
				dropUl.addAttribute("role", "menu")
					.addAttribute("class", Bootstrap.DROPDOWN_MENU);
				li.addTag(dropUl);

				for (TabPaneTagHandler dropPane : tabPane.getDropPanes()) {
					
					if (dropPane.getId() == null) {
						dropPane.setId(getRandonId());
					}

					// The execute tag must be called first to decide if there are drop down children
					StringWriter swDropPane = new StringWriter();
					dropPane.setOutputWriter(swDropPane);
					dropPane.executeTag();
					
					if (dropPane.getHeader() != null) {
						Li headerLi = new Li();
						headerLi.addAttribute("role", "presentation")
							.addAttribute("class", Bootstrap.DROPDOWN_HEADER)
							.addText(getTagValue(dropPane.getHeader()));
						dropUl.addTag(headerLi);
					}
					
					String dropLiId = getRandonId();
					
					Li dropLi = new Li();
					dropLi.addAttribute("id", dropLiId)
						.addAttribute("role", "presentation")
						.addAttribute("class", dropPane.isDisabled() ? Bootstrap.DISABLED : null);

					if (!dropPane.getAjaxTags().isEmpty()) {
						for (AjaxTagHandler ajax : dropPane.getAjaxTags()) {
							appendScript(ajax.getFunction(dropLiId));
						}
					}
					
					A dropA = new A();
					dropA.addAttribute("href", "#" + dropPane.getId())
						.addAttribute("tabIndex", "-1")
						.addAttribute("role", "tab")
						.addAttribute("class", dropPane.isDisabled() ? Bootstrap.DISABLED : null)
						.addAttribute("data-toggle", "tab")
						.addAttribute("aria-controls", dropPane.getId())
						.addAttribute("aria-expanded", "false");
					
					for (IconTagHandler iconTag : dropPane.getIconTags()) {
						if (IconTagHandler.LEFT.equalsIgnoreCase(iconTag.getSide())) {
							dropA.addTag(iconTag.executeTag());
							dropA.addText(" ");
						}
					}
					
					dropA.addText(getTagValue(dropPane.getLabel()));
					
					for (IconTagHandler iconTag : dropPane.getIconTags()) {
						if (IconTagHandler.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
							dropA.addText(" ");
							dropA.addTag(iconTag.executeTag());
						}
					}

					dropLi.addTag(dropA);
					dropUl.addTag(dropLi);
					
					if (dropPane.hasDivider()) {
						Li dividerLi = new Li();
						dividerLi.addAttribute("class", Bootstrap.DIVIDER);
						dropUl.addTag(dividerLi);
					}
					
					Div tabContent = new Div();
					tabContent.addAttribute("id", dropPane.getId())
						.addAttribute("style", dropPane.getStyle())
						.addAttribute("role", "tabpanel")
						.addAttribute("class", Bootstrap.TAB_PANE)
						.addAttribute("class", fade ? Bootstrap.FADE : null)
						.addAttribute("class", dropPane.getStyleClass());

					tabContent.addText(swDropPane.toString());
					content.addTag(tabContent);
				}

			} else {
				String tabPaneValue = (String) getTagValue(tabPane.getValue());
				li.addAttribute("tab-value", tabPaneValue != null ? tabPaneValue : tabPane.getId());

				Div tabContent = new Div();
				tabContent.addAttribute("id", tabPane.getId())
					.addAttribute("style", tabPane.getStyle())
					.addAttribute("role", "tabpanel")
					.addAttribute("class", Bootstrap.TAB_PANE)
					.addAttribute("class", fade ? Bootstrap.FADE : null)
					.addAttribute("class", tabPane.getStyleClass());
				
				if (tabValue == null) {
					tabValue = tabPaneValue != null ? tabPaneValue : tabPane.getId();
					li.addAttribute("class", Bootstrap.ACTIVE);
					tabContent.addAttribute("class", Bootstrap.ACTIVE);
	
				} else if (tabValue.equals(tabPaneValue)) {
					li.addAttribute("class", Bootstrap.ACTIVE);
					tabContent.addAttribute("class", Bootstrap.ACTIVE);
				}
				
				if (ajax) {
					appendScript(getFunction(liId));
				}

				tabContent.addText(swPane.toString());
				content.addTag(tabContent);
			}
		}

		if (input != null) {
			input.addAttribute("value", tabValue);
		}

		if (ajax) {
			appendScript(getFunction());
		}

		if (onShow != null) {
			appendScript(getBindFunction(id, "show.bs.tab", new StringBuilder(onShow)));
		}
		if (onHide != null) {
			appendScript(getBindFunction(id, "hide.bs.tab", new StringBuilder(onHide)));
		}

		return tab;
	}

	private StringBuilder getFunction() {
		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_TAB.format(id));
		return builder;
	}

	private StringBuilder getFunction(String id) {
		StringBuilder builder = new StringBuilder();
		builder.append("$('#").append(id).append("').bind('").append(EVENT_CLICK).append("', function(){");

		builder.append(JSMART_TABPANE.format(id));

		builder.append("});");
		return builder;
	}

	void addTabPane(TabPaneTagHandler tabItem) {
		tabPanes.add(tabItem);
	}

	public void setTabStyle(String tabStyle) {
		this.tabStyle = tabStyle;
	}

	public void setTabClass(String tabClass) {
		this.tabClass = tabClass;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setPills(String pills) {
		this.pills = pills;
	}

	public void setJustified(boolean justified) {
		this.justified = justified;
	}

	public void setFade(boolean fade) {
		this.fade = fade;
	}

	public void setOnShow(String onShow) {
		this.onShow = onShow;
	}

	public void setOnHide(String onHide) {
		this.onHide = onHide;
	}

}
