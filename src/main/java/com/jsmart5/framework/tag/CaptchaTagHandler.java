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

import static com.jsmart5.framework.tag.HtmlConstants.*;

public final class CaptchaTagHandler extends SmartTagHandler {

	private static final String OPEN_CAPTCHA_SCRIPT = "$('#%s').realperson(";

	private static final String CLOSE_CAPTCHA_SCRIPT = ");";

	private static final String DEFAULT_LENGTH = "6";

	private String length = DEFAULT_LENGTH;

	private boolean numbers;

	private String message;

	private String value;

	private boolean disabled;

	private Integer tabIndex;

	private String placeHolder;

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public void executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		StringBuilder builder = new StringBuilder(INPUT_TAG);

		builder.append("id=\"" + id + "\" type=\"text\" ");

		String name = getTagName(J_CAPTCHA, value);
		if (name != null) {
			builder.append("name=\"" + name + "\" ");
		}

		appendFormValidator(builder);

		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			builder.append(CssConstants.CSS_CAPTCHA);
		}
		if (tabIndex != null) {
			builder.append("tabindex=\"" + tabIndex + "\" ");
		}
		if (placeHolder != null) {
			builder.append("placeholder=\"" + getResourceString(placeHolder) + "\" datatype=\"text\" ");
		}


		StringBuilder captchaScript = new StringBuilder(String.format(OPEN_CAPTCHA_SCRIPT, id) + CLOSE_CAPTCHA_SCRIPT);
		
		captchaScript.append(String.format(OPEN_CAPTCHA_SCRIPT, id) + "'option', {");

		if (name != null) {
			captchaScript.append("hashName: '" + name.replaceFirst(J_CAPTCHA, J_CAPTCHA_HASH) + "',");
		}
		if (length != null) {
			builder.append("maxlength=\"" + length + "\" ");
			captchaScript.append("length: " + length + ",");
		}
		if (numbers) {
			captchaScript.append("includeNumbers: " + numbers + ",");
		}
		if (message != null) {
			captchaScript.append("regenerate: '" + getTagValue(message) + "',");
		}

		captchaScript.deleteCharAt(captchaScript.length() -1).append("}" + CLOSE_CAPTCHA_SCRIPT);

		if (disabled || isEditRowTagEnabled()) {
			builder.append("disabled=\"disabled\" ");
			captchaScript.append(String.format(OPEN_CAPTCHA_SCRIPT, id) + "'disable'" + CLOSE_CAPTCHA_SCRIPT);
		}

		setTagValue(value, false);

		captchaScript.append("$('#" + id + "').keyup(function(){this.value=this.value.toUpperCase();});");

		appendScriptBuilder(captchaScript);

		appendEventBuilder(builder);

		builder.append("captcha=\"" + captchaScript + "\" ");

		printOutput(builder.append("/>"));
	}

	public void setLength(String length) {
		this.length = length;
	}

	public void setNumbers(boolean numbers) {
		this.numbers = numbers;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}

}
