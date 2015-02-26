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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.adapter.SmartCarouselItem;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Tag;


public final class CarouselItemsTagHandler extends TagHandler {

	private String value;

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
//		JspTag parent = getParent();
//		Object object = getTagValue(value);
//
//		if (object instanceof List && parent instanceof CarouselTagHandler) {
//			for (SmartCarouselItem item : (List<SmartCarouselItem>) object) {
//				CarouselItemTagHandler itemHandler = new CarouselItemTagHandler();
//				itemHandler.setLib(item.getLib());
//				itemHandler.setName(item.getName());
//				itemHandler.setCaption(item.getCaption());
//				itemHandler.setLink(item.getLink());
//				((CarouselTagHandler) parent).addItem(itemHandler);
//			}
//		}
		return null;
	}

	public void setValue(String value) {
		this.value = value;
	}

}