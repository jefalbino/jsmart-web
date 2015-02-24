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

import static com.jsmart5.framework.tag.js.JsConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.json.JsonAjax;
import com.jsmart5.framework.json.JsonParam;
import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.A;
import com.jsmart5.framework.tag.html.Li;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.html.Ul;
import com.jsmart5.framework.tag.type.Align;
import com.jsmart5.framework.tag.type.Event;

public final class DropMenuTagHandler extends SmartTagHandler {

	private String align;
	
	private boolean dropUp;
	
	private boolean segmented;

	private List<DropActionTagHandler> dropActions;

	public DropMenuTagHandler() {
		dropActions = new ArrayList<DropActionTagHandler>();
	}

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof ButtonTagHandler) {
			((ButtonTagHandler) parent).setDropMenu(this);
			return false;

		} else if (parent instanceof LinkTagHandler) {
			((LinkTagHandler) parent).setDropMenu(this);
			return false;
		
		} else if (parent instanceof DropDownTagHandler) {
			((DropDownTagHandler) parent).setDropMenu(this);
			return false;
		}
		return false;
	}

	@Override
	public void validateTag() throws JspException {
		if (align != null && !Align.validateLeftRight(align)) {
			throw InvalidAttributeException.fromPossibleValues("dropmenu", "align", Align.getLeftRightValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		SmartTagHandler parent = (SmartTagHandler) getParent();

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		if (id == null) {
			id = getRandonId();
		}

		Ul ul = new Ul();
		ul.addAttribute("id", id)
			.addAttribute("role", "menu")
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.DROPDOWN_MENU);
		
		if (Align.RIGHT.name().equalsIgnoreCase(align)) {
			ul.addAttribute("class", Bootstrap.DROPDOWN_MENU_RIGHT);
		}
		
		// At last place the style class
		ul.addAttribute("class", styleClass);

		for (DropActionTagHandler dropAction : dropActions) {

			if (dropAction.getId() == null) {
				dropAction.setId(getRandonId());
			}
			
			if (dropAction.getHeader() != null) {
				Li headerLi = new Li();
				headerLi.addAttribute("role", "presentation")
					.addAttribute("class", Bootstrap.DROPDOWN_HEADER)
					.addText(getTagValue(dropAction.getHeader()));
				ul.addTag(headerLi);
			}

			Li li = new Li();
			li.addAttribute("id", dropAction.getId())
				.addAttribute("role", "presentation")
				.addAttribute("class", dropAction.isDisabled() ? Bootstrap.DISABLED : null);
			ul.addTag(li);

			appendEvent(li, dropAction);

			A a = new A();
			a.addAttribute("href", "#");
			li.addTag(a);
			
			for (IconTagHandler iconTag : dropAction.getIconTags()) {
				if (IconTagHandler.LEFT.equalsIgnoreCase(iconTag.getSide())) {
					a.addTag(iconTag.executeTag());
					a.addText(" ");
				}
			}

			a.addText(getTagValue(dropAction.getLabel()));

			for (IconTagHandler iconTag : dropAction.getIconTags()) {
				if (IconTagHandler.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
					a.addText(" ");
					a.addTag(iconTag.executeTag());
				}
			}
			
			if (dropAction.hasDivider()) {
				Li dividerLi = new Li();
				dividerLi.addAttribute("class", Bootstrap.DIVIDER);
				ul.addTag(dividerLi);
			}
			
			appendScript(parent.getId(), getFunction(dropAction));
		}

		return ul;
	}
	
	private StringBuilder getFunction(DropActionTagHandler dropAction) {
		JsonAjax jsonAjax = new JsonAjax();
		jsonAjax.setId(dropAction.getId());

		if (dropAction.getAction() != null) {
			jsonAjax.setMethod("post");
			jsonAjax.setAction(getTagName(J_SBMT, dropAction.getAction()));

			for (String name : dropAction.getParams().keySet()) {						
				jsonAjax.addParam(new JsonParam(name, dropAction.getParams().get(name)));
			}
		} else if (dropAction.getUpdate() != null) {
			jsonAjax.setMethod("get");
		}
		if (dropAction.getUpdate() != null) {
			jsonAjax.setUpdate(dropAction.getUpdate().trim());
		}
		if (dropAction.getBeforeSend() != null) {
			jsonAjax.setBefore((String) getTagValue(dropAction.getBeforeSend().trim()));
		}
		if (dropAction.getOnError() != null) {
			jsonAjax.setError((String) getTagValue(dropAction.getOnError().trim()));
		}
		if (dropAction.getOnSuccess() != null) {
			jsonAjax.setSuccess((String) getTagValue(dropAction.getOnSuccess().trim()));
		}
		if (dropAction.getOnComplete() != null) {
			jsonAjax.setComplete((String) getTagValue(dropAction.getOnComplete().trim()));
		}

		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_BUTTON.format(getJsonValue(jsonAjax)) + "return false;");
		return getBindFunction(dropAction.getId(), Event.CLICK.name(), builder);
	}

	void addDropAction(DropActionTagHandler dropAction) {
		this.dropActions.add(dropAction);
	}

	public void setAlign(String align) {
		this.align = align;
	}

	boolean isDropUp() {
		return dropUp;
	}

	public void setDropUp(boolean dropUp) {
		this.dropUp = dropUp;
	}

	boolean isSegmented() {
		return segmented;
	}

	public void setSegmented(boolean segmented) {
		this.segmented = segmented;
	}

}