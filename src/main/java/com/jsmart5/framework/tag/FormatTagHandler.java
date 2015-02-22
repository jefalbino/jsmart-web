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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.html.Tag;


public final class FormatTagHandler extends SmartTagHandler {

	private static final String NUMBER = "number";
	
	private static final String DATE = "date";
	
	private String type;

	private String regex;

	@Override
	public void validateTag() throws JspException {
		if (!type.equalsIgnoreCase(NUMBER) && !type.equalsIgnoreCase(DATE)) {
			throw new JspException("Invalid type attribute. Valid values are " + NUMBER + ", " + DATE);
		}
	}
	
	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof OutputTagHandler) {

			((OutputTagHandler) parent).setFormat(this);
			return false;
		}
		return true;
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
		// DO NOTHING
		return null;
	}

	String formatValue(final Object value) {
		if (value != null) {
			if (type.equalsIgnoreCase(NUMBER)) {
				return new DecimalFormat(regex).format(value);
						
			} else if (type.equalsIgnoreCase(DATE)) {
				if (value instanceof Date) {
					return new SimpleDateFormat(regex, getRequest().getLocale()).format(value);
	
				} else if (value instanceof DateTime) {
					return ((DateTime) value).toString(DateTimeFormat.forPattern(regex).withLocale(getRequest().getLocale()));
				}
			}
			return value.toString();
		}
		return null;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

}
