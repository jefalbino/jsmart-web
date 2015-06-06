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
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.exception.ConstraintTagException;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.css.JSmart5;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.P;
import com.jsmart5.framework.tag.html.Tag;

public final class SlideTagHandler extends TagHandler {

	private boolean active;
	
	private String label;

    private String imageLib;

    private String imageName;

    private String imageAlt;

    private String imageWidth;

    private String imageHeight;

	private HeaderTagHandler header;

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();

		if (parent instanceof CarouselTagHandler) {
			((CarouselTagHandler) parent).addSlide(this);
		}
		return false;
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
		CarouselTagHandler parent = (CarouselTagHandler) getParent();

		// Just to call nested tags
		StringWriter sw = new StringWriter();
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}

		if (imageName == null && (parent.getWidth() == null || parent.getHeight() == null)) {
			throw ConstraintTagException.fromConstraint("carousel", "Tag must have attributes [width] and [height] " +
                    "case [slide] tags does not have [imageName] attribute");
		}

		Div div = new Div();
		div.addAttribute("id", id)
			.addAttribute("style", style)
			.addAttribute("class", Bootstrap.ITEM)
			.addAttribute("class", styleClass);

		if (active) {
			div.addAttribute("class", Bootstrap.ACTIVE);
		}

        ImageTagHandler image = null;

        if (imageName != null) {
            image = new ImageTagHandler();
            image.setParent(this);
            image.setLib(imageLib);
            image.setName(imageName);
            image.setAlt(imageAlt);
            image.setWidth(imageWidth);
            image.setHeight(imageHeight);
        }

		if (image != null) {
			div.addTag(image.executeTag());
		}

		Div caption = new Div();
		caption.addAttribute("class", Bootstrap.CAROUSEL_CAPTION)
			.addAttribute("class", image == null ? JSmart5.CAROUSEL_CAPTION : null);

		if (header != null) {
			caption.addTag(header.executeTag());
		}

		Object labelVal = getTagValue(label);
		if (labelVal != null) {
			P p = new P();
			p.addText(labelVal);
			caption.addTag(p);
		}

		caption.addText(sw);
		div.addTag(caption);

		return div;
	}

	void setHeader(HeaderTagHandler header) {
		this.header = header;
	}

    public void setImageLib(String imageLib) {
        this.imageLib = imageLib;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    String getImageName() {
        return imageName;
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }

    boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
