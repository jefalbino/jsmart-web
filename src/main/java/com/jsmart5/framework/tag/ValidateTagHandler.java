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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Look;

public final class ValidateTagHandler extends TagHandler {

	private String text;

	private String maxLength;

	private Integer minLength;
	
	private String regex;

	private String look = Look.ERROR.name().toLowerCase();

	@Override
	public void validateTag() throws JspException {
		if (look != null && !Look.validateValidate(look) && !isEL(look)) {
			throw InvalidAttributeException.fromPossibleValues("validate", "look", Look.getValidateValues());
		}
	}

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof TagHandler) {

			((TagHandler) parent).setValidatorTag(this);
			return false;
		}
		return true;
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
		// DO NOTHING
		return null;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLook() {
		return look;
	}

	public void setLook(String look) {
		this.look = look;
	}

	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
}
