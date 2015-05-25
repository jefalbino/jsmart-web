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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.manager.SmartContext;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.html.A;
import com.jsmart5.framework.tag.html.Button;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.P;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.util.SmartAlert;

import static com.jsmart5.framework.util.SmartAlert.*;

public final class AlertTagHandler extends TagHandler {

	private boolean dismissible = true;

	private String onHide;
	
	private HeaderTagHandler header;

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

		Div wrap = new Div();
		wrap.addAttribute("id", id + "-wrap")
			.addAttribute("role", "alert-wrap");

		if (onHide != null) {
			appendDocScript(getBindFunction(id, "close.bs.alert", new StringBuilder(onHide)));
		}

		List<SmartAlert> alerts = getAlerts(id);
		if (alerts == null || alerts.isEmpty()) {
			wrap.addAttribute("style", "display: none;");
			return wrap;
		}

		Div div = new Div();
		div.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.ALERT)
			.addAttribute("class", Bootstrap.FADE)
			.addAttribute("class", Bootstrap.IN)
			.addAttribute("role", "alert");

		if (dismissible) {
			div.addAttribute("class", Bootstrap.ALERT_DISMISSIBLE);
			
			Button button = new Button();
			button.addAttribute("type", "button")
				.addAttribute("class", Bootstrap.CLOSE)
				.addAttribute("data-dismiss", "alert")
				.addAttribute("aria-label", "close");
			
			Span span = new Span();
			span.addAttribute("aria-hidden", "true").addText("x");
			
			button.addTag(span);
			div.addTag(button);
		}

		// Add type, title and icon for the first fixed alert
		SmartAlert firstAlert = alerts.get(0);
		
		if (AlertType.INFO.equals(firstAlert.getType())) {
			div.addAttribute("class", Bootstrap.ALERT_INFO);

		} else if (AlertType.SUCCESS.equals(firstAlert.getType())) {
			div.addAttribute("class", Bootstrap.ALERT_SUCCESS);
			
		} else if (AlertType.WARNING.equals(firstAlert.getType())) {
			div.addAttribute("class", Bootstrap.ALERT_WARNING);

		} else if (AlertType.DANGER.equals(firstAlert.getType())) {
			div.addAttribute("class", Bootstrap.ALERT_DANGER);
		}

		// At last add the custom style
		div.addAttribute("class", styleClass);

		if (firstAlert.getTitleIcon() != null) {
			header = new HeaderTagHandler();
			header.setParent(this);

			IconTagHandler iconTag = new IconTagHandler();
			iconTag.setName(firstAlert.getTitleIcon());
			header.addIconTag(iconTag);
		}

		if (firstAlert.getTitle() != null) {
			if (header == null) {
				header = new HeaderTagHandler();
				header.setParent(this);
			}
			header.setTitle(firstAlert.getTitle()); 
		}

		if (header != null) {
			div.addTag(header.executeTag());
		}

		// Add messages to the alert
		for (SmartAlert alert : alerts) {
			P p = new P();

			if (alert.getMessageUrl() != null) {
				A a = new A();
				a.addAttribute("href", alert.getMessageUrl())
					.addAttribute("class", Bootstrap.ALERT_LINK)
					.addText(alert.getMessage());
				p.addTag(a);
			} else {
				p.addText(alert.getMessage());
			}
			div.addTag(p);
		}

		if (!sw.toString().isEmpty()) {
			P p = new P();
			p.addText(sw);
			div.addTag(p);
		}

		if (SmartContext.isAjaxRequest()) {
			wrap.addAttribute("alert-show", "true");
		}

		wrap.addTag(div);
		return wrap;
	}

	void setHeader(HeaderTagHandler header) {
		this.header = header;
	}

	public void setDismissible(boolean dismissible) {
		this.dismissible = dismissible;
	}

	public void setOnHide(String onHide) {
		this.onHide = onHide;
	}

}
