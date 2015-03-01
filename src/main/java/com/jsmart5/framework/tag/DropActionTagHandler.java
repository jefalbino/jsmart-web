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
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Tag;

public class DropActionTagHandler extends TagHandler {
	
	private String header;

	private boolean divider;

	private String label;
	
	private boolean disabled;

	private String action;

	private String update;

	private String beforeSend;

	private String onError;
	
	private String onSuccess;
	
	private String onComplete;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof DropMenuTagHandler) {

			// Just to call nested tags for parameters
			JspFragment body = getJspBody();
			if (body != null) {
				body.invoke(null);
			}

			((DropMenuTagHandler) parent).addDropAction(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
		// DO NOTHING
		return null;
	}

	String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	boolean hasDivider() {
		return divider;
	}

	public void setDivider(boolean divider) {
		this.divider = divider;
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

	String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	String getBeforeSend() {
		return beforeSend;
	}

	public void setBeforeSend(String beforeSend) {
		this.beforeSend = beforeSend;
	}

	String getOnError() {
		return onError;
	}

	public void setOnError(String onError) {
		this.onError = onError;
	}

	String getOnSuccess() {
		return onSuccess;
	}

	public void setOnSuccess(String onSuccess) {
		this.onSuccess = onSuccess;
	}

	String getOnComplete() {
		return onComplete;
	}

	public void setOnComplete(String onComplete) {
		this.onComplete = onComplete;
	}

}
