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
import com.jsmart5.framework.util.SmartImage;

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.CssConstants.*;

public final class ImageTagHandler extends SmartTagHandler {

	private String lib;

	private String name;

	private String alt;

	private String width;

	private String height;

	private String caption;

	private boolean figure;

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
		// DO NOTHING
	}

	@Override
	public void executeTag() throws JspException, IOException {

		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		String libValue = (String) getTagValue(lib);
		String nameValue = (String) getTagValue(name);

		StringBuilder builder = new StringBuilder();

		if (figure) {
			builder.append(OPEN_FIGURE_TAG);
		}
		
		builder.append(IMG_TAG);

		if (libValue != null) {
			builder.append("src=\"" + SmartImage.IMAGES.getImage(libValue, nameValue) + "\" ");
		} else {
			builder.append("src=\"" + nameValue + "\" ");
		}

		if (alt != null) {
			builder.append("alt=\"" + getTagValue(alt) + "\" ");
		} else {
			builder.append("alt=\"" + nameValue + "\" ");
		}

		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}

		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CSS_IMAGE);
		}

		if (width != null) {
			builder.append("width=\"" + width + "\" ");
		}

		if (height != null) {
			builder.append("height=\"" + height + "\" ");
		}

		if (ajaxCommand != null) {
			builder.append(ajaxCommand);
		}

		appendEvent(builder);

		builder.append("/>");

		if (figure && caption != null) {
			builder.append(OPEN_FIGCAPTION_TAG + getTagValue(caption) + CLOSE_FIGCAPTION_TAG);
		}

		if (figure) {
			builder.append(CLOSE_FIGURE_TAG);
		}

		printOutput(builder);
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

}
