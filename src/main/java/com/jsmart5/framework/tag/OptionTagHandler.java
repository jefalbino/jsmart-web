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

import com.jsmart5.framework.manager.SmartTagHandler;

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.CssConstants.*;

public final class OptionTagHandler extends SmartTagHandler {

	private String name;

	private String label;

	private Object value;

	private boolean disabled;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof SelectTagHandler) {
			
			((SelectTagHandler) parent).addOption(this);
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

		StringBuilder builder = new StringBuilder(OPEN_OPTION_TAG);

		if (id != null) {
			builder.append("id=\"" + id + "\" ");
		}
		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CSS_SELECT_OPTION);
		}
		if (disabled || isEditRowTagEnabled()) {
			builder.append("disabled=\"disabled\" ");
		}

		Object object = getTagValue(value);
		if (object != null) {
			builder.append("value=\"" + object + "\" ");
		}

		if (verifySelection(object)) {
			builder.append("selected=\"selected\" ");
		}

		printOutput(builder.append(">" + getTagValue(label) + CLOSE_OPTION_TAG));
	}

	@SuppressWarnings("rawtypes")
	private boolean verifySelection(Object value) {
		// Get selected values
		Object values = getTagValue(name);

		if (values != null && value != null) {
			if (values instanceof Collection) {
				for (Object obj : (Collection) values) {
					if (obj != null && obj.toString().equals(value.toString())) {
						return true;
					}
				}
			} else {
				return values.equals(value);
			}
		}

		return false;
	}

	/*package*/ void setName(String name) {
		this.name = name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
