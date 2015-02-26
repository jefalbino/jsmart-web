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

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Li;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.html.Ul;
import com.jsmart5.framework.tag.type.Look;

public final class OutputListTagHandler extends TagHandler {

	private Object values;
	
	private String look;

	private boolean inline;

	@Override
	public void validateTag() throws JspException {
		if (look != null && !Look.validateText(look)) {
			throw InvalidAttributeException.fromPossibleValues("outputlist", "look", Look.getTextValues());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Tag executeTag() throws JspException, IOException {

		Ul ul = new Ul();
		ul.addAttribute("id", id)
			.addAttribute("style", style);
		
		String lookVal = null;

		if (Look.PRIMARY.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.TEXT_PRIMARY;
		} else if (Look.SUCCESS.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.TEXT_SUCCESS;
		} else if (Look.INFO.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.TEXT_INFO;
		} else if (Look.WARNING.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.TEXT_WARNING;
		} else if (Look.DANGER.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.TEXT_DANGER;
		} else if (Look.MUTED.equalsIgnoreCase(look)) {
			lookVal = Bootstrap.TEXT_MUTED;
		}

		ul.addAttribute("class", lookVal)
			.addAttribute("class", inline ? Bootstrap.LIST_INLINE : null);

		// Add the style class at last
		ul.addAttribute("class", styleClass);

		Object obj = getTagValue(values);
		if (obj != null) {
			if (obj instanceof Collection) {
				for (Object o : (Collection<Object>) obj) {
					if (o != null) {
						Li li = new Li();
						li.addText(o.toString());
						ul.addTag(li);
					}
				}
			} else if (obj.getClass().isArray()) {
				for (Object o : (Object[]) obj) {
					if (o != null) {
						Li li = new Li();
						li.addText(o.toString());
						ul.addTag(li);
					}
				}
			}
		}

		return ul;
	}

	public void setValues(Object values) {
		this.values = values;
	}

	public void setInline(boolean inline) {
		this.inline = inline;
	}

	public void setLook(String look) {
		this.look = look;
	}
}
