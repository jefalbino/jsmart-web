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
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.adapter.SlideAdapter;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Tag;

public final class SlidesTagHandler extends TagHandler {

	private String values;

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		Object object = getTagValue(values);

		if (object instanceof Collection && parent instanceof CarouselTagHandler) {
			
			for (SlideAdapter adapter : (Collection<SlideAdapter>) object) {
				SlideTagHandler slideTag = new SlideTagHandler();
				slideTag.setParent(parent);
				slideTag.setActive(adapter.isActive());
				slideTag.setLabel(adapter.getLabel());

				if (adapter.getHeader() != null) {
					HeaderTagHandler headerTag = new HeaderTagHandler();
					headerTag.setParent(slideTag);
					headerTag.setTitle(adapter.getHeader().getTitle());
					headerTag.setType(adapter.getHeader().getType());
					
					if (adapter.getHeader().getIcon() != null) {
						IconTagHandler iconTag = new IconTagHandler();
						iconTag.setParent(headerTag);
						iconTag.setName(adapter.getHeader().getIcon());
						headerTag.addIconTag(iconTag);
					}
					slideTag.setHeader(headerTag);
				}
				
				if (adapter.getImage() != null) {
					ImageTagHandler imageTag = new ImageTagHandler();
					imageTag.setParent(slideTag);
					imageTag.setLib(adapter.getImage().getLib());
					imageTag.setName(adapter.getImage().getName());
					imageTag.setAlt(adapter.getImage().getAlt());
					imageTag.setWidth(adapter.getImage().getWidth());
					imageTag.setHeight(adapter.getImage().getHeight());
					slideTag.setImage(imageTag);
				}

				((CarouselTagHandler) parent).addSlide(slideTag);
			}
		}
		return false;
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
		// DO NOTHING
		return null;
	}

	public void setValues(String values) {
		this.values = values;
	}

}