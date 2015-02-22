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
import com.jsmart5.framework.tag.html.A;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Tag;

public final class PanelTagHandler extends SmartTagHandler {
	
	private String look;
	
	private HeaderTagHandler header;
	
	private FooterTagHandler footer;

	public void validateTag() throws JspException {
		if (look != null && !look.equalsIgnoreCase(DEFAULT) && !look.equalsIgnoreCase(PRIMARY) && !look.equalsIgnoreCase(SUCCESS)
				&& !look.equalsIgnoreCase(INFO) && !look.equalsIgnoreCase(WARNING) && !look.equalsIgnoreCase(DANGER)) {
			throw new JspException("Invalid look value for panel tag. Valid values are " + DEFAULT + ", " + PRIMARY 
					+ ", " + SUCCESS + ", " + INFO + ", " + WARNING + ", " + DANGER);
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
		String parentId = null;
		String contentId = null;

		JspTag parent = getParent();
		if (parent instanceof AccordionTagHandler) {
			parentId = ((AccordionTagHandler) parent).getId();
			contentId = getRandonId();
		}

		StringWriter sw = new StringWriter();
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}
		
		if (id == null) {
			id = getRandonId();
		}

		Div panel = new Div();
		panel.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.PANEL);

		String lookVal = Bootstrap.PANEL_DEFAULT;
		
		if (PRIMARY.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.PANEL_PRIMARY;
		} else if (SUCCESS.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.PANEL_SUCCESS;
		} else if (INFO.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.PANEL_INFO;
		} else if (WARNING.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.PANEL_WARNING;
		} else if (DANGER.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.PANEL_DANGER;
		}

		panel.addAttribute("class", lookVal);
		
		// Add the style class at last
		panel.addAttribute("class", styleClass);

		appendEvent(panel);

		if (!ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : ajaxTags) {
				appendScript(ajax.getFunction(id));
			}
		}
		
		if (header != null || parentId != null) {
			Div head = new Div();
			head.addAttribute("class", Bootstrap.PANEL_HEADING);

			for (IconTagHandler iconTag : iconTags) {
				if (IconTagHandler.LEFT.equalsIgnoreCase(iconTag.getSide())) {
					head.addTag(iconTag.executeTag());
				}
			}

			if (parentId != null) {
				A a = new A();
				a.addAttribute("data-toggle", "collapse")
					.addAttribute("data-parent", "#" + parentId)
					.addAttribute("href", "#" + contentId)
					.addAttribute("aria-expanded", "false")
					.addAttribute("aria-controls", contentId);
				
				if (header != null) {
					a.addTag(header.executeTag());
				}
				head.addTag(a);

			} else {
				head.addTag(header.executeTag());
			}
			
			for (IconTagHandler iconTag : iconTags) {
				if (IconTagHandler.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
					head.addTag(iconTag.executeTag());
				}
			}

			panel.addTag(head);
		}

		if (contentId != null) {
			Div content = new Div();
			content.addAttribute("id", contentId)
				.addAttribute("class", Bootstrap.PANEL_COLLPASE)
				.addAttribute("class", Bootstrap.COLLAPSE)
				.addAttribute("role", "tabpanel");
			
			content.addText(sw.toString());
			panel.addTag(content);
		} else {
			panel.addText(sw.toString());
		}

	    if (footer != null) {
			panel.addTag(footer.executeTag());
	    }

		return panel;
	}
	
	void setHeader(HeaderTagHandler header) {
		this.header = header;
	}
	
	void setFooter(FooterTagHandler footer) {
		this.footer = footer;
	}
	
	public void setLook(String look) {
		this.look = look;
	}

}
