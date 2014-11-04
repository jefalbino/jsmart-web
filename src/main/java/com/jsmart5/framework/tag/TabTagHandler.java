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

import com.jsmart5.framework.json.JsonTab;
import com.jsmart5.framework.manager.SmartTagHandler;

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.CssConstants.*;
import static com.jsmart5.framework.tag.JsConstants.*;

public final class TabTagHandler extends SmartTagHandler {

	private static final String TAB_INDEX = "_tab_index";

	private static final String TOP = "top";

	private static final String BOTTOM = "bottom";

	private static final String LEFT = "left";

	private static final String RIGHT = "right";

	private String align;

	private boolean ajax;

	private String value;

	private boolean hover;

	private String position = TOP;

	private boolean collapsible;

	private List<TabItemTagHandler> tabItems;

	public TabTagHandler() {
		tabItems = new ArrayList<TabItemTagHandler>();
	}

	@Override
	public void validateTag() throws JspException {
		if (!TOP.equals(position) && !BOTTOM.equals(position) && !LEFT.equals(position) && !RIGHT.equals(position)) {
			throw new JspException("Invalid position value for tab tag. Valid values are "
					+ TOP + ", " + BOTTOM + ", " + LEFT + " and " + RIGHT);
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		StringBuilder builder = new StringBuilder(OPEN_DIV_TAG);

		builder.append("id=\"" + id + "\" ");

		builder.append("tab=\"tab\" ");

		if (align != null) {
			builder.append("align=\"" + align + "\" ");
		}
		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CSS_TABS);
		}

		JsonTab jsonTab = new JsonTab();
		jsonTab.setAjax(String.valueOf(ajax));
		jsonTab.setCollapsible(String.valueOf(collapsible));
		jsonTab.setPosition(position);
		jsonTab.setHover(String.valueOf(hover));

		builder.append("ajax=\"" + getJsonValue(jsonTab) + "\" ");

		builder.append(">");

		String defaultValue = null;

		if (TOP.equals(position) || LEFT.equals(position)) {
			defaultValue = appendTabNavigator(builder);
		}

		for (TabItemTagHandler tabItem : tabItems) {

			builder.append(OPEN_DIV_TAG);

			if (tabItem.getId() != null) {
				builder.append("id=\"" + tabItem.getId() + "\" ");
			}
			if (tabItem.getStyle() != null) {
				builder.append("style=\"" + tabItem.getStyle() + "\" ");
			}
			if (tabItem.getStyleClass() != null) {
				builder.append("class=\"" + tabItem.getStyleClass() + "\" ");
			} else {
				if (TOP.equals(position)) { 
					appendClass(builder, CSS_TAB_CONTENT_TOP);
				} else if (LEFT.equals(position)) {
					appendClass(builder, CSS_TAB_CONTENT_LEFT);
				} else if (BOTTOM.equals(position)) {
					appendClass(builder, CSS_TAB_CONTENT_BOTTOM);
				} else if (RIGHT.equals(position)) {
					appendClass(builder, CSS_TAB_CONTENT_RIGHT);
				}
			}

			builder.append(">");

			StringWriter sw = new StringWriter();
			tabItem.setOutputWriter(sw);
			tabItem.executeTag();
			builder.append(sw.toString());

			builder.append(CLOSE_DIV_TAG);
		}

		if (BOTTOM.equals(position) || RIGHT.equals(position)) {
			defaultValue = appendTabNavigator(builder);
		}

		builder.append(CLOSE_DIV_TAG);
		
		Object tabValue = "";

		if (value != null) {
			tabValue = getTagValue(value);
			if (tabValue == null) {
				tabValue = defaultValue;
			}
		}

		if (value != null) {
			builder.append(INPUT_TAG);

			builder.append("id=\"" + id + TAB_INDEX + "\" type=\"hidden\" ");

			String name = getTagName(J_TAG, value);
			if (name != null) {
				builder.append("name=\"" + name + "\" ");
			}
			if (tabValue != null) {
				builder.append("value=\"" + tabValue + "\" ");
			}

			builder.append(" />");
		}

		appendScript(new StringBuilder(JSMART_TAB.format(id)), true);
		
		printOutput(builder);
	}

	private String appendTabNavigator(StringBuilder builder) throws JspException, IOException {
		String defaultValue = "";

		builder.append(OPEN_UNORDERED_LIST_TAG);

		if (TOP.equals(position) || BOTTOM.equals(position)) {
			appendClass(builder, CSS_TABS_UL_HORIZONTAL);
		} else {
			appendClass(builder, CSS_TABS_UL_VERTICAL);
		}

		appendEvent(builder);

		builder.append(">");

		for (TabItemTagHandler tabItem : tabItems) {
			builder.append(OPEN_LIST_ITEM_TAG);

			if (tabItem.getTabStyle() != null) {
				builder.append("style=\"" + tabItem.getTabStyle() + "\" ");
			}
			if (tabItem.getTabClass() != null) {
				builder.append("class=\"" + tabItem.getTabClass() + "\" ");
			} else {
				if (TOP.equals(position)) {
					appendClass(builder, CSS_TABS_LI_TOP);
				} else if (LEFT.equals(position)) {
					appendClass(builder, CSS_TABS_LI_LEFT);
				} else if (BOTTOM.equals(position)) {
					appendClass(builder, CSS_TABS_LI_BOTTOM);
				} else if (RIGHT.equals(position)) {
					appendClass(builder, CSS_TABS_LI_RIGHT);
				}
			}

			if (ajaxCommand != null) {
				builder.append(ajaxCommand);
			}

			StringBuilder eventBuilder = new StringBuilder();

			tabItem.appendEvent(eventBuilder);

			builder.append(eventBuilder);

			builder.append("tab=\"" + tabItem.getId() + "\" ");

			String tabValue = (String) getTagValue(tabItem.getValue());
			
			if (defaultValue == null) {
				defaultValue = tabValue != null ? tabValue : tabItem.getId();
			}

			builder.append("val=\"" + (tabValue != null ? tabValue : tabItem.getId()) + "\" >");

			builder.append(getTagValue(tabItem.getLabel()));

			builder.append(CLOSE_LIST_ITEM_TAG);
		}

		builder.append(CLOSE_UNORDERED_LIST_TAG);

		return defaultValue;
	}

	/*package*/ void addTabItem(TabItemTagHandler tabItem) {
		tabItems.add(tabItem);
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setHover(boolean hover) {
		this.hover = hover;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setTabItems(List<TabItemTagHandler> tabItems) {
		this.tabItems = tabItems;
	}

}
