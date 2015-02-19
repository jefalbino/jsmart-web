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
import com.jsmart5.framework.tag.html.FigCaption;
import com.jsmart5.framework.tag.html.Figure;
import com.jsmart5.framework.tag.html.Image;
import com.jsmart5.framework.util.SmartImage;

public final class ImageTagHandler extends SmartTagHandler {

	private static final String RESPONSIVE = "responsive";

	private static final String ROUND = "round";

	private static final String CIRCLE = "circle";

	private static final String THUMBNAIL = "thumbnail";

	private String lib;

	private String name;

	private String alt;

	private String width;

	private String height;

	private String caption;

	private boolean figure;

	private String type;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof GridTagHandler) {

			((GridTagHandler) parent).addTag(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (type != null && !type.equalsIgnoreCase(RESPONSIVE) && !type.equalsIgnoreCase(ROUND) && !type.equalsIgnoreCase(CIRCLE)
				&& !type.equalsIgnoreCase(THUMBNAIL)) {
			throw new JspException("Invalid type value for image tag. Valid values are " + RESPONSIVE + ", " + ROUND 
					+ ", " + CIRCLE + ", " + THUMBNAIL);
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}
		
		if (id == null) {
			id = getRandonId();
		}

		String libValue = (String) getTagValue(lib);
		String nameValue = (String) getTagValue(name);

		Image image = new Image();
		image.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("width", width)
			.addAttribute("height", height);

		if (libValue != null) {
			image.addAttribute("src",  SmartImage.IMAGES.getImage(libValue, nameValue));
		} else {
			image.addAttribute("src", nameValue);
		}

		if (alt != null) {
			image.addAttribute("alt", getTagValue(alt));
		} else {
			image.addAttribute("alt", nameValue);
		}
		
		if (RESPONSIVE.equalsIgnoreCase(type)) {
			image.addAttribute("class", Bootstrap.IMAGE_RESPONSIVE);
		} else if (ROUND.equalsIgnoreCase(type)) {
			image.addAttribute("class", Bootstrap.IMAGE_ROUNDED);
		} else if (CIRCLE.equalsIgnoreCase(type)) {
			image.addAttribute("class", Bootstrap.IMAGE_CIRCLE);
		} else if (THUMBNAIL.equalsIgnoreCase(type)) {
			image.addAttribute("class", Bootstrap.IMAGE_THUMBNAIL);
		}
		
		// Add the style class at last
		image.addAttribute("class", styleClass);

		appendEvent(image);

		if (!ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : ajaxTags) {
				appendScript(ajax.getFunction(id));
			}
		}

		if (figure) {
			Figure fig = new Figure();
			fig.addTag(image);
			
			if (caption != null) {
				FigCaption figCaption = new FigCaption();
				figCaption.addText((String) getTagValue(caption));
				fig.addTag(figCaption);
			}
			printOutput(fig.getHtml());

		} else {
			printOutput(image.getHtml());
		}
	}

	public void setLib(String lib) {
		this.lib = lib;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setFigure(boolean figure) {
		this.figure = figure;
	}

	public void setType(String type) {
		this.type = type;
	}

}
