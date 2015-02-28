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

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Align;
import com.jsmart5.framework.tag.type.Output;

public final class HeaderTagHandler extends TagHandler {
	
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
		if (type != null && !Output.validateHeader(type)) {
			throw InvalidAttributeException.fromPossibleValues("header", "type", Output.getHeaderValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		TagHandler parent = (TagHandler) getParent();

		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}
		
		setRandomId("header");

		Tag header = null;
		if (type != null) {
			header = new Tag(type);
		}

		if (parent instanceof RowTagHandler) {
			if (header == null) {
				header = new Tag(Output.H4.name().toLowerCase());
			}
			header.addAttribute("class", Bootstrap.LIST_GROUP_ITEM_HEADING);
			 
		} else if (parent instanceof PanelTagHandler) {
			if (header == null) {
				header = new Tag(Output.H3.name().toLowerCase());
			}
			header.addAttribute("class", Bootstrap.PANEL_TITLE);

		} else if (parent instanceof ModalTagHandler) {
			if (header == null) {
				header = new Tag(Output.H4.name().toLowerCase());
			}
			header.addAttribute("class", Bootstrap.MODAL_TITLE);

		} else if (header == null) {
			header = new Tag(Output.H3.name().toLowerCase());
		}
		
		header.addAttribute("style", style)
			.addAttribute("class", styleClass);
		
		appendId(header, id);

		for (IconTagHandler iconTag : iconTags) {
			if (Align.LEFT.equalsIgnoreCase(iconTag.getSide())) {
				header.addTag(iconTag.executeTag());
				header.addText(" ");
			}
		}

		header.addText(getTagValue(title));

		for (IconTagHandler iconTag : iconTags) {
			if (Align.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
				header.addText(" ");
				header.addTag(iconTag.executeTag());
			}
		}
		
		if (parent instanceof TagHandler && getSharedValue(ITERATOR_TAG_PARENT) == null) {
			appendAjax(parent.getId());
			appendBind(parent.getId());
		} else {
			appendAjax(id);
			appendBind(id);
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
