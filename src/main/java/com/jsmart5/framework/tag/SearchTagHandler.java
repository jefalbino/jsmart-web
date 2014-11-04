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

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.CssConstants.*;
import static com.jsmart5.framework.tag.JsConstants.*;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.json.JsonParam;
import com.jsmart5.framework.json.JsonSearch;
import com.jsmart5.framework.manager.SmartTagHandler;

public class SearchTagHandler extends SmartTagHandler {

	private boolean async = true;

	private boolean trackSearch;

	private boolean disabled;

	private String placeHolder;
	
	private String label;

	private String buttonLabel;

	private String value;

	private String action;

	private String afterAjax;

	private String beforeAjax;

	private String update;

	private Integer tabIndex;

	private boolean autoFocus;

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
		// DO NOTHING
	}

	@Override
	public void executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		StringBuilder builder = new StringBuilder();
		builder.append(OPEN_DIV_TAG);
		appendClass(builder, CSS_INPUT_GROUP);
		builder.append(">");

		if (label != null) {
			builder.append(OPEN_SPAN_TAG);
			appendClass(builder, CSS_INPUT_LABEL);
			builder.append(">");

			String labelVal = (String) getTagValue(label);
			if (labelVal != null) {
				builder.append(labelVal);
			}
			builder.append(CLOSE_SPAN_TAG);
		}

		builder.append(OPEN_DIV_TAG);
		appendClass(builder, label != null ? CSS_SEARCH_GROUP : CSS_SEARCH_GROUP_NO_LABEL);
		builder.append(">");

		builder.append(OPEN_DIV_TAG);
		appendClass(builder, CSS_SEARCH_CONTENT);
		builder.append(">");
		
		builder.append(OPEN_DIV_TAG);
		appendClass(builder, CSS_SEARCH_INPUT);
		builder.append(">");

		builder.append(INPUT_TAG);

		builder.append("id=\"" + id + "\" type=\"text\" search=\"search\" ");

		String name = getTagName(J_TAG, value);
		if (name != null) {
			builder.append("name=\"" + name + "\" ");
		}
		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CSS_SEARCH);
		}

		appendFormValidator(builder);

		appendRest(builder);

		if (tabIndex != null) {
			builder.append("tabindex=\"" + tabIndex + "\" ");
		}
		if (placeHolder != null) {
			builder.append("placeholder=\"" + getResourceString(placeHolder) + "\" datatype=\"text\" ");
		}
		if (autoFocus) {
			builder.append("autofocus=\"autofocus\" ");
		}
		if (disabled || isEditRowTagEnabled()) {
			builder.append("disabled=\"disabled\" ");
		}

		JsonSearch jsonSearch = new JsonSearch();

		jsonSearch.setMethod("post");
		jsonSearch.setAction(getTagName(J_SBMT, action));
		jsonSearch.setTrack(trackSearch);

		for (String paramName : params.keySet()) {						
			jsonSearch.getParams().add(new JsonParam(paramName, params.get(paramName)));
		}

		if (update != null) {
			jsonSearch.setUpdate(update.trim());
		}
		if (beforeAjax != null) {
			jsonSearch.setBefore(beforeAjax.trim());
		}
		if (afterAjax != null) {
			jsonSearch.setExec(afterAjax.trim());
		}

		builder.append("ajax=\"" + getJsonValue(jsonSearch) + "\" ");

		if (ajaxCommand != null) {
			if (ajaxCommand.startsWith(ON_KEY_PRESS)) {
				builder.append(ajaxCommand.replace(ON_KEY_PRESS, ON_KEY_PRESS + JSMART_SEARCH.format(async, id)));
			} else {
				builder.append(ON_KEY_PRESS + "return " + JSMART_SEARCH.format(async, id) + "\" ");
				builder.append(ajaxCommand);
			}
		} else {
			builder.append(ON_KEY_PRESS + "return " + JSMART_SEARCH.format(async, id) + "\" ");
		}

		appendEvent(builder);

		builder.append("/>");
		builder.append(CLOSE_DIV_TAG);
		builder.append(CLOSE_DIV_TAG);

		builder.append(OPEN_DIV_TAG);
		appendClass(builder, CSS_SEARCH_IMAGE);
		builder.append(">");
		builder.append(CLOSE_DIV_TAG);

		builder.append(CLOSE_DIV_TAG);

		// Append Search Button
		builder.append(OPEN_BUTTON_TAG);
		appendClass(builder, CSS_SEARCH_BUTTON);

		builder.append("type=\"button\" ");
		builder.append(ON_CLICK + JSMART_SEARCH.format(async, id) + "return false;\" ");
		builder.append("ajax=\"" + getJsonValue(jsonSearch) + "\" ");

		appendEvent(builder);

		builder.append(">");

		String buttonLabelVal = (String) getTagValue(buttonLabel);
		builder.append(buttonLabelVal != null ? buttonLabelVal : "");
		builder.append(CLOSE_BUTTON_TAG);
		
		builder.append(CLOSE_DIV_TAG);

		appendScript(new StringBuilder(JSMART_SEARCH_RESET.format(id)));

		printOutput(builder);
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public void setTrackSearch(boolean trackSearch) {
		this.trackSearch = trackSearch;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setButtonLabel(String buttonLabel) {
		this.buttonLabel = buttonLabel;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setAfterAjax(String afterAjax) {
		this.afterAjax = afterAjax;
	}

	public void setBeforeAjax(String beforeAjax) {
		this.beforeAjax = beforeAjax;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setAutoFocus(boolean autoFocus) {
		this.autoFocus = autoFocus;
	}

}
