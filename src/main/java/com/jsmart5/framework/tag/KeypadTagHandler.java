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

import static com.jsmart5.framework.tag.HtmlConstants.*;

public final class KeypadTagHandler extends SmartTagHandler {

	private static final String KEYPAD_HOLDER = "_keypad_holder";

	private static final String KEYPAD = "_keypad";

	private static final String KEYPAD_SCRIPT_START = "$('#%s').keypad({";

	private static final String KEYPAD_SCRIPT_CLOSE = "});";

	private static final String QWERTY = "qwerty";

	private static final String NUMERIC = "numeric";

	private static final String FULL = "full";

	private String target;

	private String layout = FULL;

	private boolean opened;

	private String onKeyPress;

	private boolean showButton;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof GridTagHandler) {

			((GridTagHandler) parent).addTag(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (layout != null && !layout.equals(QWERTY) && !layout.equals(NUMERIC) && !layout.equals(FULL)) {
    			throw new JspException("Invalid layout value for keyboard tag. Valid values are " + QWERTY + ", " + NUMERIC + " and " + FULL);
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		StringBuilder scriptBuilder = new StringBuilder();

		if (opened) {
			scriptBuilder.append(String.format(KEYPAD_SCRIPT_START, target + KEYPAD));
			scriptBuilder.append("target:" + target + ",");
		} else {
			scriptBuilder.append(String.format(KEYPAD_SCRIPT_START, target));
		}
		
		// Append theme options to keypad
		appendThemeOption(scriptBuilder);
		
		if (onKeyPress != null && !onKeyPress.isEmpty()) {
			scriptBuilder.append("onKeypress:" + onKeyPress + ",");
		}
		if (showButton) {
			scriptBuilder.append("showOn:'button',");
		}
		if (layout != null) {
			if (layout.equals(QWERTY)) {
				scriptBuilder.append("layout:$.keypad.qwertyLayout,");

			} else if (layout.equals(NUMERIC)) {
				scriptBuilder.append("layout:$.keypad.numericLayout,");

			} else if (layout.equals(FULL)){
				scriptBuilder.append("layout:$.keypad.fullLayout,");
			}
		}

		scriptBuilder.append(KEYPAD_SCRIPT_CLOSE);

		appendScript(scriptBuilder);

		StringBuilder builder = new StringBuilder(INPUT_TAG);

		builder.append("id=\"" + target + KEYPAD_HOLDER + "\" ");
		builder.append("value=\"" + scriptBuilder + "\" ");
		builder.append("type=\"hidden\" keypad=\"keypad\" />");
		printOutput(builder);

		if (opened) {
			StringBuilder divBuilder = new StringBuilder(OPEN_DIV_TAG);
			divBuilder.append("id=\"" + target + KEYPAD + "\" >");
			divBuilder.append(CLOSE_DIV_TAG);
			printOutput(divBuilder);
		}
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public void setOnKeyPress(String onKeyPress) {
		this.onKeyPress = onKeyPress;
	}

	public void setShowButton(boolean showButton) {
		this.showButton = showButton;
	}

}
