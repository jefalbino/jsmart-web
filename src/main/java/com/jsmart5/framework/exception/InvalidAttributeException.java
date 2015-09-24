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

package com.jsmart5.framework.exception;

import com.google.common.collect.Lists;

import javax.servlet.jsp.JspException;
import java.text.MessageFormat;

public class InvalidAttributeException extends JspException {

	private static final long serialVersionUID = -4064886707211938557L;
	
	public InvalidAttributeException(String message) {
		super(message);
	}
	
	public static InvalidAttributeException fromPossibleValues(String tag, String attr, String ... values) {
		int index = 0;
		Object[] args = new Object[3];
		args[index++] = attr;
		args[index++] = tag;
		args[index++] = Lists.newArrayList(values);
		return new InvalidAttributeException(MessageFormat.format("Invalid [{0}] value for [{1}] tag. Possible values are {2}", args));
	}
	
	public static InvalidAttributeException fromConstraint(String tag, String attr, String constraint) {
		return new InvalidAttributeException(MessageFormat.format("Invalid [{0}] value for [{1}] tag. Value must be {2}", attr, tag, constraint));
	}

	public static InvalidAttributeException fromConflict(String tag, String attr, String conflict) {
		return new InvalidAttributeException(MessageFormat.format("Invalid [{0}] value for [{1}] tag. {2}", attr, tag, conflict));
	}
}
