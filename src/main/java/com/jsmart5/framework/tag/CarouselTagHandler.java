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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.exception.ConstraintTagException;
import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.css.JSmart5;
import com.jsmart5.framework.tag.html.A;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Li;
import com.jsmart5.framework.tag.html.Ol;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;
import static com.jsmart5.framework.tag.js.JsConstants.*;

public final class CarouselTagHandler extends TagHandler {

	private Integer timeout;
	
	private boolean continuous = true;
	
	private String onSlide;
	
	private String width;
	
	private String height;
	
	protected final List<SlideTagHandler> slides;

	public CarouselTagHandler() {
		slides = new ArrayList<SlideTagHandler>();
	}

	@Override
	public void validateTag() throws JspException {
		if (timeout != null && timeout < 0) {
			throw InvalidAttributeException.fromConstraint("carousel", "timeout", "greater than 0"); 
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		if (this.slides.isEmpty()) {
			throw ConstraintTagException.fromConstraint("carousel", "Tag must contain [slide] and or [slides] inner tags");
		}

		setRandomId("carousel");

		Div div = new Div();
		div.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.CAROUSEL)
			.addAttribute("class", Bootstrap.SLIDE)
			.addAttribute("data-ride", "carousel")
			.addAttribute("data-wrap", continuous);

		if (width != null) {
			div.addAttribute("style", "width: " + width + ";");
		}
		if (height != null) {
			div.addAttribute("style", "height: " + height + ";");
		}

		if (timeout != null) {
			div.addAttribute("data-interval", timeout == 0 ? "false" : timeout * 1000);
		}

		Ol ol = new Ol();
		ol.addAttribute("class", Bootstrap.CAROUSEL_INDICATORS);

		// Create indicators
		boolean slideActive = false;

		for (int i = 0; i < slides.size(); i++) {
			Li li = new Li();
			li.addAttribute("data-target", "#" + id)
				.addAttribute("data-slide-to", i);
			
			if (slides.get(i).getImageName() == null) {
				li.addAttribute("class", JSmart5.CAROUSEL_INDICATOR);
			}

			if (!slideActive && slides.get(i).isActive()) {
				li.addAttribute("class", Bootstrap.ACTIVE);
				slideActive = true;
			}
			ol.addTag(li);
		}

		// Case no active was found
		if (!slideActive) {
			slides.get(0).setActive(true);

			Tag li = (Tag) ol.getObject(0);
			li.addAttribute("class", Bootstrap.ACTIVE);
		}

		div.addTag(ol);

		// Slides wrapper
		Div inner = new Div();
		inner.addAttribute("class", Bootstrap.CAROUSEL_INNER)
			.addAttribute("class", JSmart5.CAROUSEL_INNER)
			.addAttribute("role", "listbox");
		
		// Slides
		slideActive = false;
		boolean noSlideImage = false;
		
		for (SlideTagHandler slide : slides) {
			if (slideActive) {
				slide.setActive(false);
			}
			if (!slideActive && slide.isActive()) {
				slideActive = true;
			}
			inner.addTag(slide.executeTag());

			noSlideImage |= slide.getImageName() == null;
		}
		div.addTag(inner);
		
		// Controls
		A left = new A();
		left.addAttribute("class", Bootstrap.LEFT)
			.addAttribute("class", Bootstrap.CAROUSEL_CONTROL)
			.addAttribute("class", noSlideImage ? JSmart5.CAROUSEL_CONTROL : null)
			.addAttribute("href", "#" + id)
			.addAttribute("role", "button")
			.addAttribute("data-slide", "prev");
		
		Span leftSpan = new Span();
		leftSpan.addAttribute("class", "glyphicon")
			.addAttribute("class", "glyphicon-chevron-left")
			.addAttribute("aria-hidden", "true");
		
		Span srLeftSpan = new Span();
		srLeftSpan.addAttribute("class", Bootstrap.SR_ONLY)
			.addText("Previous");
		left.addTag(leftSpan).addTag(srLeftSpan);

		A right = new A();
		right.addAttribute("class", Bootstrap.RIGHT)
			.addAttribute("class", Bootstrap.CAROUSEL_CONTROL)
			.addAttribute("class", noSlideImage ? JSmart5.CAROUSEL_CONTROL : null)
			.addAttribute("href", "#" + id)
			.addAttribute("role", "button")
			.addAttribute("data-slide", "next");
	
		Span rightSpan = new Span();
		rightSpan.addAttribute("class", "glyphicon")
			.addAttribute("class", "glyphicon-chevron-right")
			.addAttribute("aria-hidden", "true");

		Span srRightSpan = new Span();
		srRightSpan.addAttribute("class", Bootstrap.SR_ONLY)
			.addText("Next");
		right.addTag(rightSpan).addTag(srRightSpan);
	
		div.addTag(left).addTag(right);

		if (onSlide != null) {
			appendDocScript(getBindFunction(id, "slide.bs.carousel", new StringBuilder(onSlide)));
		}

		if (noSlideImage) {
			StringBuilder script = new StringBuilder(JSMART_CAROUSEL.format(id));
			appendDocScript(getBindFunction(id, "slid.bs.carousel", script));
			
			// This will be executed at document ready
			appendDocScript(script);
		}
		return div;
	}

	void addSlide(SlideTagHandler slide) {
		this.slides.add(slide);
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public void setOnSlide(String onSlide) {
		this.onSlide = onSlide;
	}

	public void setContinuous(boolean continuous) {
		this.continuous = continuous;
	}

	String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

}
