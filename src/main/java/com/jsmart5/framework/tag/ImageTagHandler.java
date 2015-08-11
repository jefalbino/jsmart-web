/*
 * JSmart5 - Java Web Development Framework
 * Copyright (c) 2014, Jeferson Albino da Silva, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
*/

package com.jsmart5.framework.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.html.FigCaption;
import com.jsmart5.framework.tag.html.Figure;
import com.jsmart5.framework.tag.html.Image;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Type;
import com.jsmart5.framework.util.WebImage;

public final class ImageTagHandler extends TagHandler {

	private String lib;

	private String name;

	private String alt;

	private String width;

	private String height;

	private String caption;

	private boolean figure;

	private String type;

	@Override
	public void validateTag() throws JspException {
		if (type != null && !Type.validateImage(type)) {
			throw InvalidAttributeException.fromPossibleValues("image", "type", Type.getImageValues());
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}
		
		setRandomId("image");

		String libValue = (String) getTagValue(lib);
		String nameValue = (String) getTagValue(name);

		Image image = new Image();
		image.addAttribute("style", getTagValue(style))
			.addAttribute("width", width)
			.addAttribute("height", height);
		
		appendRefId(image, id);

		if (libValue != null) {
			image.addAttribute("src",  WebImage.getImage(libValue, nameValue));
		} else {
			image.addAttribute("src", nameValue);
		}

		if (alt != null) {
			image.addAttribute("alt", getTagValue(alt));
		} else {
			image.addAttribute("alt", nameValue);
		}
		
		if (Type.RESPONSIVE.equalsIgnoreCase(type)) {
			image.addAttribute("class", Bootstrap.IMAGE_RESPONSIVE);
		} else if (Type.ROUND.equalsIgnoreCase(type)) {
			image.addAttribute("class", Bootstrap.IMAGE_ROUNDED);
		} else if (Type.CIRCLE.equalsIgnoreCase(type)) {
			image.addAttribute("class", Bootstrap.IMAGE_CIRCLE);
		} else if (Type.THUMBNAIL.equalsIgnoreCase(type)) {
			image.addAttribute("class", Bootstrap.IMAGE_THUMBNAIL);
		}
		
		// Add the style class at last
		image.addAttribute("class", getTagValue(styleClass));

		appendEvent(image);

		appendAjax(id);
		appendBind(id);

		if (figure) {
			Figure fig = new Figure();
			fig.addTag(image);
			
			if (caption != null) {
				FigCaption figCaption = new FigCaption();
				figCaption.addText((String) getTagValue(caption));
				fig.addTag(figCaption);
			}
			return fig;
		}

		return image;
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
