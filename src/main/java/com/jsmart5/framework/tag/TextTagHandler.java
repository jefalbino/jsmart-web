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

package com.jsmart5.framework.tag;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Tag;

public final class TextTagHandler extends TagHandler {
	
	private static final Pattern BRACKETS = Pattern.compile(".*\\{[0-9]*\\}.*");

	private String res;

	private String key;

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}
	
	@Override
	public boolean beforeTag() throws JspException, IOException {
		// Look for parameters
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		String message = getResourceString(res, key);

		if (!params.isEmpty()) {
			message = formatText(message, params);
		}
		printOutput(message);
		return true;
	}

    static String formatText(final String message, final Map<String, Object> params) {
        if (BRACKETS.matcher(message).find()) {
            return MessageFormat.format(message, params.values().toArray());

        } else if (message.contains("%s")) {
            return String.format(message, params.values().toArray());
        }
        return message;
    }

	@Override
	public Tag executeTag() throws JspException, IOException {
		// DO NOTHING
		return null;
	}

	public void setRes(String res) {
		this.res = res;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
