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
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.manager.SmartValidateTagHandler;
import com.jsmart5.framework.tag.html.Tag;

public final class ValidateTagHandler extends SmartValidateTagHandler {

	@Override
	public void validateTag() throws JspException {
		if (look != null && !look.equalsIgnoreCase(ERROR) && !look.equalsIgnoreCase(WARNING) && !look.equalsIgnoreCase(SUCCESS)) {
			throw new JspException("Invalid look value for validate tag. Valid values are " + ERROR + ", " + WARNING 
					+ ", " + SUCCESS);
		}
	}

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof SmartTagHandler) {

			setValidator((SmartTagHandler) parent, this);
		}
		return true;
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
		// DO NOTHING
		return null;
	}

}
