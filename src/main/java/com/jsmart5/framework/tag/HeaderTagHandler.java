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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Tag;

public final class HeaderTagHandler extends SmartTagHandler {
	
	private static final String H1 = "h1";
	
	private static final String H2 = "h2";
	
	private static final String H3 = "h3";
	
	private static final String H4 = "h4";
	
	private static final String H5 = "h5";
	
	private static final String H6 = "h6";
	
	private String title;
	
	private String type;
	
	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof ModalTagHandler) {
			((ModalTagHandler) parent).setHeader(this);
			return false;

		} else if (parent instanceof RowTagHandler) {
			((RowTagHandler) parent).setHeader(this);
			return false;

		} else if (parent instanceof PanelTagHandler) {
			((PanelTagHandler) parent).setHeader(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (type != null && !H1.equalsIgnoreCase(type) && !H2.equalsIgnoreCase(type) && !H3.equalsIgnoreCase(type)
				&& !H4.equalsIgnoreCase(type) && !H5.equalsIgnoreCase(type) && !H6.equalsIgnoreCase(type)) {
			throw new JspException("Invalid type value for output tag. Valid values are "
					+ H1 + ", " + H2 + ", " + H3 + ", " + H4 + ", " + H5 + ", " + H6);
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

		Tag header = null;
		if (type != null) {
			header = new Tag(type);
		}

		JspTag parent = getParent();
		if (parent instanceof RowTagHandler) {
			if (header == null) {
				header = new Tag(H4);
			}
			header.addAttribute("class", Bootstrap.LIST_GROUP_ITEM_HEADING);
			 
		} else if (parent instanceof PanelTagHandler) {
			if (header == null) {
				header = new Tag(H3);
			}
			header.addAttribute("class", Bootstrap.PANEL_TITLE);

		} else if (parent instanceof ModalTagHandler) {
			if (header == null) {
				header = new Tag(H4);
			}
			header.addAttribute("class", Bootstrap.MODAL_TITLE);

		} else if (header == null) {
			header = new Tag(H3);
		}
		
		header.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", styleClass);

		for (IconTagHandler iconTag : iconTags) {
			if (IconTagHandler.LEFT.equalsIgnoreCase(iconTag.getSide())) {
				header.addTag(iconTag.executeTag());
				header.addText(" ");
			}
		}

		header.addText(getTagValue(title));

		for (IconTagHandler iconTag : iconTags) {
			if (IconTagHandler.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
				header.addText(" ");
				header.addTag(iconTag.executeTag());
			}
		}
		
		if (!ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : ajaxTags) {
				appendScript(ajax.getFunction(id));
			}
		}

		return header;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(String type) {
		this.type = type;
	}

}
