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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.SmartTagHandler;

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.CssConstants.*;


public final class CarouselTagHandler extends SmartTagHandler {

	private static final String BASIC = "basic";

	private static final String NUMBERED = "numbered";

	private static final String TRANSITION_FADE = "fade";

	private static final String TRANSITION_SLIDE = "slide";

	protected final List<CarouselItemTagHandler> items;

	private String type;

	private String title;

	private Integer width;

	private Integer height;

	private Integer transitionTime;

	private String transitionType;

	private Integer timer;

	public CarouselTagHandler() {
		items = new ArrayList<CarouselItemTagHandler>();
	}

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
		if (type != null && !type.equals(BASIC) && !type.equals(NUMBERED)) {
			throw new JspException("Invalid type value for carousel tag. Valid values are " + BASIC + " and " + NUMBERED);
		}
		if (transitionType != null && !transitionType.equals(TRANSITION_FADE) && !transitionType.equals(TRANSITION_SLIDE)) {
			throw new JspException("Invalid transitionType value for carousel tag. Valid values are "
					+ TRANSITION_FADE + " and " + TRANSITION_SLIDE);
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		StringBuilder builder = new StringBuilder(OPEN_DIV_TAG);
		
		builder.append("id=\"" + id + "\" carousel=\"carousel\" ");

		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CSS_CAROUSEL);
		}

		builder.append("width=\"" + width + "\" ");
		builder.append("height=\"" + height + "\" ");
		builder.append("timer=\"" + timer + "\" ");

		if (transitionType != null) {
			builder.append("transitionType=\"" + transitionType + "\" ");
		}		
		if (transitionTime != null) {
			builder.append("transitionTime=\"" + transitionTime + "\" ");
		}

		builder.append(CLOSE_TAG);

		if (title != null) {
			builder.append(OPEN_PARAGRAPH_TAG + getTagValue(title) + CLOSE_PARAGRAPH_TAG);
		}

		builder.append(OPEN_DIV_TAG);
		appendClass(builder, CSS_CAROUSEL_SLIDES);
		builder.append(">");

		for (int i = 0; i < items.size(); i++) {
			StringWriter sw = new StringWriter();
			items.get(i).setOutputWriter(sw);
			items.get(i).setId(id + "_slide_" + (i + 1));
			items.get(i).executeTag();
			builder.append(sw.toString());
		}

		if (NUMBERED.equals(type)) {
			// Numbers
			builder.append(OPEN_DIV_TAG);
			appendClass(builder, CSS_CAROUSEL_CONTROL);
			builder.append(" align=\"center\" >");
			builder.append(OPEN_DIV_TAG + "align=\"right\" >");
			for (int i = 0; i < items.size(); i++) {
				builder.append(OPEN_SPAN_TAG + CLOSE_TAG + (i + 1) + CLOSE_SPAN_TAG);
			}
			builder.append(CLOSE_DIV_TAG + CLOSE_DIV_TAG);

			// Arrows Previous and Next
			builder.append(OPEN_DIV_TAG);
			appendClass(builder, CSS_CAROUSEL_CONTROL_ARROW);
			builder.append(CLOSE_TAG);
			builder.append(OPEN_SPAN_TAG + "direction=\"prev\" >");
			builder.append(OPEN_DIV_TAG + ">" + CLOSE_DIV_TAG);
			builder.append(CLOSE_SPAN_TAG);
			builder.append(OPEN_SPAN_TAG + "direction=\"next\" >");
			builder.append(OPEN_DIV_TAG + ">" + CLOSE_DIV_TAG);
			builder.append(CLOSE_SPAN_TAG);
			builder.append(CLOSE_DIV_TAG);
		}

		builder.append(CLOSE_DIV_TAG + CLOSE_DIV_TAG);

		StringBuilder scriptBuilder = new StringBuilder(JsConstants.JSMART_CAROUSEL.format(id));

		appendScriptDeprecated(scriptBuilder);

		printOutput(builder);
	}

	void addItem(CarouselItemTagHandler item) {
		this.items.add(item);
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public void setTransitionTime(Integer transitionTime) {
		this.transitionTime = transitionTime;
	}

	public void setTransitionType(String transitionType) {
		this.transitionType = transitionType;
	}

	public void setTimer(Integer timer) {
		this.timer = timer;
	}

}
