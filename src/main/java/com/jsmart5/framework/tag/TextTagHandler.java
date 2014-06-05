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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.manager.SmartTagHandler;


public final class TextTagHandler extends SmartTagHandler {

	private String res;

	private String key;

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public void executeTag() throws JspException, IOException {

		// Look for parameters
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		String message = getResourceString(res, key);

		if (!params.isEmpty() && message.contains("%s")) {
			message = String.format(message, params.values().toArray());
		}

		printOutput(new StringBuilder(message));
	}

	public void setRes(String res) {
		this.res = res;
	}

	public void setKey(String key) {
		this.key = key;
	}

}