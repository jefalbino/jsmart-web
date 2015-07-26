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

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.html.Button;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Size;

import static com.jsmart5.framework.tag.js.JsConstants.*;

public final class ModalTagHandler extends TagHandler {

	private String opened;

	private String size;

	private boolean backdrop = true;

	private boolean fade = true;

	private String onShow;

	private String onHide;

	private HeaderTagHandler header;

	private FooterTagHandler footer;

	@Override
	public void validateTag() throws JspException {
		if (size != null && !Size.validateSmallLarge(size)) {
			throw InvalidAttributeException.fromPossibleValues("modal", "size", Size.getSmallLargeValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

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
			.addAttribute("data-backdrop", !backdrop ? "static" : null);
		
		Div modalDialog = new Div();
		modalDialog.addAttribute("class", Bootstrap.MODAL_DIALOG)
			.addAttribute("style", getTagValue(style));
		
		if (Size.SMALL.equalsIgnoreCase(size)) {
			modalDialog.addAttribute("class", Bootstrap.MODAL_SMALL);
		} else if (Size.LARGE.equalsIgnoreCase(size)) {
			modalDialog.addAttribute("class", Bootstrap.MODAL_LARGE);
		}

		// At last place the custom style
		modalDialog.addAttribute("class", getTagValue(styleClass));
		modal.addTag(modalDialog);
		
		Div modalContent = new Div();
		modalContent.addAttribute("class", Bootstrap.MODAL_CONTENT)
			.addAttribute("style", getTagValue(style))
			.addAttribute("class", getTagValue(styleClass));
		modalDialog.addTag(modalContent);

		if (header != null) {
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
			modalHeader.addTag(button);

			modalHeader.addTag(header.executeTag());
			modalContent.addTag(modalHeader);
		}
		
		Div modalBody = new Div();
		modalBody.addAttribute("class", Bootstrap.MODAL_BODY)
			.addText(sw.toString());
		modalContent.addTag(modalBody);
		
		if (footer != null) {
			modalContent.addTag(footer.executeTag());
		}
		
		if (onShow != null) {
			appendDocScript(getBindFunction(id, "show.bs.modal", new StringBuilder(onShow)));
		}
		if (onHide != null) {
			appendDocScript(getBindFunction(id, "hide.bs.modal", new StringBuilder(onHide)));
		}

		Object openedVal = getTagValue(opened);
		if (openedVal != null && Boolean.parseBoolean(openedVal.toString())) {
			appendDocScript(new StringBuilder(JSMART_MODAL.format(id)));
		}
		return modal;
	}
	
	void setHeader(HeaderTagHandler header) {
		this.header = header;
	}

	void setFooter(FooterTagHandler footer) {
		this.footer = footer;
	}

	public void setOpened(String opened) {
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
