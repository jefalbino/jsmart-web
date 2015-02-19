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
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.SmartTagHandler;

public class DropTagHandler extends SmartTagHandler {
	
	private String header;

	private String label;
	
	private boolean disabled;

	private String action;
	
	private boolean divider;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof ButtonTagHandler || parent instanceof LinkTagHandler) {
			
			// Just to call nested tags for parameters
			JspFragment body = getJspBody();
			if (body != null) {
				body.invoke(null);
			}

			if (parent instanceof ButtonTagHandler) {
				((ButtonTagHandler) parent).addDrop(this);
			} else {
				((LinkTagHandler) parent).addDrop(this);
			}
			return false;

		} else if (parent instanceof TabPaneTagHandler) {
			((TabPaneTagHandler) parent).addDrop(this);
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
		JspTag parent = getParent();
		if (parent instanceof TabPaneTagHandler) {

			JspFragment body = getJspBody();
			if (body != null) {
				body.invoke(outputWriter);
			}
		}
	}

	String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	boolean hasDivider() {
		return divider;
	}

	public void setDivider(boolean divider) {
		this.divider = divider;
	}

	Map<String, Object> getParams() {
		return params;
	}

}
