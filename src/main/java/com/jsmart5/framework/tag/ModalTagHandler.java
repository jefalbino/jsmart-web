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

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Button;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;

public final class ModalTagHandler extends SmartTagHandler {
	
	private static final String SMALL = "small";

	private static final String LARGE = "large";

	private String title;

	private boolean opened;

	private String size;

	private boolean backdrop;

	private boolean fade = true;

	private String onShow;

	private String onHide;
		
	private FooterTagHandler footer;

	@Override
	public void validateTag() throws JspException {
		if (size != null && !size.equalsIgnoreCase(SMALL) && !size.equalsIgnoreCase(LARGE)) {
			throw new JspException("Invalid size value for modal tag. Valid values are " + SMALL + ", " + LARGE);
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		StringWriter sw = new StringWriter();
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}

		Div modal = new Div();
		modal.addAttribute("id", id)
			.addAttribute("class", Bootstrap.MODAL)
			.addAttribute("class", fade ? Bootstrap.FADE : null)
			.addAttribute("tabindex", "-1")
			.addAttribute("role", "dialog")
			.addAttribute("aria-hidden", "true")
			.addAttribute("data-backdrop", backdrop ? "static" : null)
			.addAttribute("data-show", opened ? "true" : null);
		
		Div modalDialog = new Div();
		modalDialog.addAttribute("class", Bootstrap.MODAL_DIALOG)
			.addAttribute("style", style);
		
		if (SMALL.equalsIgnoreCase(size)) {
			modalDialog.addAttribute("class", Bootstrap.MODAL_SMALL);
		} else if (LARGE.equalsIgnoreCase(size)) {
			modalDialog.addAttribute("class", Bootstrap.MODAL_LARGE);
		}

		// At last place the custom style
		modalDialog.addAttribute("class", styleClass);
		modal.addTag(modalDialog);
		
		Div modalContent = new Div();
		modalContent.addAttribute("class", Bootstrap.MODAL_CONTENT)
			.addAttribute("style", style)
			.addAttribute("class", styleClass);
		modalDialog.addTag(modalContent);

		if (title != null) {
			Div modalHeader = new Div();
			modalHeader.addAttribute("class", Bootstrap.MODAL_HEADER);
			
			Button button = new Button();
			button.addAttribute("type", "button")
				.addAttribute("class", Bootstrap.CLOSE)
				.addAttribute("data-dismiss", "modal")
				.addAttribute("aria-label", "Close");
			
			Span span = new Span();
			span.addAttribute("aria-hidden", "true")
				.addText("&times;");
			button.addTag(span);
			
			Tag h4 = new Tag("h4");
			h4.addAttribute("class", Bootstrap.MODAL_TITLE)
				.addText((String) getTagValue(title));
			
			modalHeader.addTag(button);
			modalHeader.addTag(h4);
			modalContent.addTag(modalHeader);
		}
		
		Div modalBody = new Div();
		modalBody.addAttribute("class", Bootstrap.MODAL_BODY)
			.addText(sw.toString());
		modalContent.addTag(modalBody);
		
		if (footer != null) {
			
			StringWriter swFooter = new StringWriter();
			footer.setOutputWriter(swFooter);
			footer.executeTag();
			
			modalContent.addText(swFooter.toString());
		}
		
		if (onShow != null) {
			appendScript(getFunction(id, "shown.bs.modal", new StringBuilder(onShow)));
		}
		if (onHide != null) {
			appendScript(getFunction(id, "hidden.bs.modal", new StringBuilder(onHide)));
		}

		printOutput(modal.getHtml());
	}

	void setFooter(FooterTagHandler footer) {
		this.footer = footer;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setBackdrop(boolean backdrop) {
		this.backdrop = backdrop;
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
