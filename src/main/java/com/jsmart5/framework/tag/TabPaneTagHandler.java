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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.html.Tag;

public final class TabPaneTagHandler extends SmartTagHandler {

	private String title;

	private String value;

	private boolean disabled;

	private String tabStyle;

	private String tabClass;

	private List<DropTagHandler> drops;

	public TabPaneTagHandler() {
		drops = new ArrayList<DropTagHandler>();
	}

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof TabTagHandler) {

			((TabTagHandler) parent).addTabPane(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public void executeTag() throws JspException, IOException {
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(outputWriter);
		}
	}

	@Override
	protected void appendEvent(Tag tag) {
		super.appendEvent(tag);
	}

	String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	String getTabStyle() {
		return tabStyle;
	}

	public void setTabStyle(String tabStyle) {
		this.tabStyle = tabStyle;
	}

	String getTabClass() {
		return tabClass;
	}

	public void setTabClass(String tabClass) {
		this.tabClass = tabClass;
	}

	boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	void addDrop(DropTagHandler drop) {
		this.drops.add(drop);
	}

	List<DropTagHandler> getDrops() {
		return drops;
	}

}
