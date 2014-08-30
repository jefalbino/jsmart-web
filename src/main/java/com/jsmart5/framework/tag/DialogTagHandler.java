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

import static com.jsmart5.framework.tag.HtmlConstants.*;

public final class DialogTagHandler extends SmartTagHandler {

	private static final String DIALOG_SCRIPT_START = "$('#%s').dialog({";

	private static final String DIALOG_SCRIPT_CLOSE = "});";

	private String title;

	private Boolean opened;

	private Integer height;

	private Integer width;

	private Boolean modal;

	private Boolean resizable;

	private String showEffect;

	private String hideEffect;

	private String position;

	private Boolean draggable;

	private Integer zIndex;

	private String onOpen;

	private String onClose;

	private String listenEnter;

	private Boolean hideHeader;

	@Override
	public void validateTag() throws JspException {
		if (showEffect != null && !showEffect.equals("blind") && !showEffect.equals("bounce") && !showEffect.equals("clip") 
				&& !showEffect.equals("drop") && !showEffect.equals("explode") && !showEffect.equals("fade") && !showEffect.equals("fold") 
				&& !showEffect.equals("puff") && !showEffect.equals("pulsate") && !showEffect.equals("shake") && !showEffect.equals("slide")) {
			throw new JspException("Invalid showEffect value for dialog tag. Valid values are blind, bounce, clip, drop, explode, fade, fold, puff, " +
					"pulsate, shake and slide");
		}
		if (hideEffect != null && !hideEffect.equals("blind") && !hideEffect.equals("bounce") && !hideEffect.equals("clip") 
				&& !hideEffect.equals("drop") && !hideEffect.equals("explode") && !hideEffect.equals("fade") && !hideEffect.equals("fold") 
				&& !hideEffect.equals("puff") && !hideEffect.equals("pulsate") && !hideEffect.equals("shake") && !hideEffect.equals("slide")) {
			throw new JspException("Invalid hideEffect value for dialog tag. Valid values are blind, bounce, clip, drop, explode, fade, fold, puff, " +
					"pulsate, shake and slide");
		}
		if (position != null && !position.equals("center") && !position.equals("left") && !position.equals("right") 
				&& !position.equals("top") && !position.equals("bottom") && !position.equals("left top") && !position.equals("left bottom")
				&& !position.equals("right top") && !position.equals("right bottom")) {
			throw new JspException("Invalid position value for dialog tag. Valid values are center, left, right, top, bottom, left top, left bottom, " +
					"right top, right bottom");
		}
		if (hideHeader != null && hideHeader == true && draggable != null && draggable == true) {
			throw new JspException("Invalid attributes for dialog tag. The attribute draggable can only be used if hideHeader attribute is false");
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		StringWriter sw = new StringWriter();
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}

		StringBuilder builder = new StringBuilder(OPEN_DIV_TAG);

		builder.append("id=\"" + id + "\" ");

		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}

		builder.append(">");

		builder.append(sw);
		builder.append(CLOSE_DIV_TAG);

	    StringBuilder scriptBuilder = new StringBuilder(String.format(DIALOG_SCRIPT_START, id) + "closeOnEscape: true,");
	    
		// Append theme options to dialog
		appendThemeOption(scriptBuilder);

		if (title != null) {
			scriptBuilder.append("title:'" + getTagValue(title) + "',");
		}
		
		if (hideHeader != null) {
			scriptBuilder.append("hideHeader:" + hideHeader + ",");
		}

		if (opened != null) {
			scriptBuilder.append("autoOpen:" + opened + ",");
		}

		if (modal != null) {
			scriptBuilder.append("modal:" + modal + ",");
		}

		if (width != null) {
			scriptBuilder.append("width:" + width + ",");
		}

		if (height != null) {
			scriptBuilder.append("height:" + height + ",");
		}

		if (zIndex != null) {
			scriptBuilder.append("zIndex:" + zIndex + ",");
		}

		if (showEffect != null) {
			scriptBuilder.append("show:'" + showEffect + "',");
		}

		if (hideEffect != null) {
			scriptBuilder.append("hide:'" + hideEffect + "',");
		}

		if (resizable != null) {
			scriptBuilder.append("resizable:" + resizable + ",");
		}

		if (draggable != null) {
			scriptBuilder.append("draggable:" + draggable + ",");
		}

		if (position != null) {
			scriptBuilder.append("position:'" + position + "',");
		}

		if (onOpen != null) {
			scriptBuilder.append("open: function() {" + onOpen + ";},");
		}

		if (onClose != null) {
			scriptBuilder.append("close: function() {" + onClose + ";},");
		}

		if (listenEnter != null) {
			scriptBuilder.append("listenEnter:'" + listenEnter + "',");
		}

		scriptBuilder.append(DIALOG_SCRIPT_CLOSE);

		appendScript(scriptBuilder);

		printOutput(builder);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setOpened(Boolean opened) {
		this.opened = opened;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setModal(Boolean modal) {
		this.modal = modal;
	}

	public void setResizable(Boolean resizable) {
		this.resizable = resizable;
	}

	public void setShowEffect(String showEffect) {
		this.showEffect = showEffect;
	}

	public void setHideEffect(String hideEffect) {
		this.hideEffect = hideEffect;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setDraggable(Boolean draggable) {
		this.draggable = draggable;
	}

	public void setzIndex(Integer zIndex) {
		this.zIndex = zIndex;
	}

	public void setOnOpen(String onOpen) {
		this.onOpen = onOpen;
	}

	public void setOnClose(String onClose) {
		this.onClose = onClose;
	}

	public void setListenEnter(String listenEnter) {
		this.listenEnter = listenEnter;
	}

	public Boolean getHideHeader() {
		return hideHeader;
	}

	public void setHideHeader(Boolean hideHeader) {
		this.hideHeader = hideHeader;
	}

}
