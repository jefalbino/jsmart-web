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
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.config.SmartConstants;
import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.A;
import com.jsmart5.framework.tag.html.Li;
import com.jsmart5.framework.tag.html.Tag;

public final class RowTagHandler extends SmartTagHandler {
	
	private String look;

	private boolean disabled;
	
	private String selectValue;
	
	private HeaderTagHandler header;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof ListTagHandler) {
			
			((ListTagHandler) parent).addRow(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (look != null && !look.startsWith(SmartConstants.START_EL) && !look.equalsIgnoreCase(SUCCESS) 
				&& !look.equalsIgnoreCase(INFO) && !look.equalsIgnoreCase(WARNING) && !look.equalsIgnoreCase(DANGER)) {
			throw new JspException("Invalid look value for row tag. Valid value is an expression or the values " + SUCCESS + ", " 
				+ INFO + ", " + WARNING + ", " + DANGER);
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		StringWriter sw = new StringWriter();
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}
		
		if (id == null) {
			id = getRandonId();
		}

		Tag tag = null;
		if (selectValue != null) {
			tag = new A();
			tag.addAttribute("href", "#");
		} else {
			tag = new Li();
		}

		tag.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.LIST_GROUP_ITEM)
			.addAttribute("class", disabled ? Bootstrap.DISABLED : null)
			.addAttribute("class", styleClass);
		
		String lookVal = (String) getTagValue(look);

		if (SUCCESS.equalsIgnoreCase(lookVal)) {
			tag.addAttribute("class", Bootstrap.LIST_GROUP_ITEM_SUCCESS);
		} else if (INFO.equalsIgnoreCase(lookVal)) {
			tag.addAttribute("class", Bootstrap.LIST_GROUP_ITEM_INFO);
		} else if (WARNING.equalsIgnoreCase(lookVal)) {
			tag.addAttribute("class", Bootstrap.LIST_GROUP_ITEM_WARNING);
		} else if (DANGER.equalsIgnoreCase(lookVal)) {
			tag.addAttribute("class", Bootstrap.LIST_GROUP_ITEM_DANGER);
		}

		// At last place the style class
		tag.addAttribute("class", styleClass);

		appendEvent(tag);
		
		if (!ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : ajaxTags) {
				appendScript(ajax.getFunction(id));
			}
		}

		if (header != null) {
			tag.addTag(header.executeTag());
		}
		tag.addText(sw.toString());

		return tag;
	}
	
	void setHeader(HeaderTagHandler header) {
		this.header = header;
	}
	
	void setSelectValue(String selectValue) {
		this.selectValue = selectValue;
	}

	public void setLook(String look) {
		this.look = look;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
