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

package com.jsmart5.framework.exception;

import java.text.MessageFormat;

import javax.servlet.jsp.JspException;

public class ConstraintTagException extends JspException {

	private static final long serialVersionUID = -7139156469829986156L;

	public ConstraintTagException(String message) {
		super(message);
	}

	public static ConstraintTagException fromConstraint(String tag, String constraint) {
		return new ConstraintTagException(MessageFormat.format("Invalid [{0}] constraint. {1}", tag, constraint));
	}

}
