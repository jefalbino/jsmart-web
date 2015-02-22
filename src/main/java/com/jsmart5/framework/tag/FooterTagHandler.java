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

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Tag;

public final class FooterTagHandler extends SmartTagHandler {
	
	private String title;
	
	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof ModalTagHandler) {
			((ModalTagHandler) parent).setFooter(this);
			return false;

		} else if (parent instanceof PanelTagHandler) {
			((PanelTagHandler) parent).setFooter(this);
			return false;
		} 
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
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

		Div footer = new Div();
		footer.addAttribute("id", id)
			.addAttribute("style", style);

		JspTag parent = getParent();
		if (parent instanceof ModalTagHandler) {
			footer.addAttribute("class", Bootstrap.MODAL_FOOTER);
			
		} else if (parent instanceof PanelTagHandler) {
			footer.addAttribute("class", Bootstrap.PANEL_FOOTER);
		}

		footer.addAttribute("class", styleClass)
			.addText(getTagValue(title))
			.addText(sw.toString());
		
		if (!ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : ajaxTags) {
				appendScript(ajax.getFunction(id));
			}
		}

		return footer;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
