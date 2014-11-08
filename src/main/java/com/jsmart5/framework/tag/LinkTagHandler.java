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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.json.JsonLink;
import com.jsmart5.framework.json.JsonParam;
import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.manager.SmartUtils;

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.CssConstants.*;
import static com.jsmart5.framework.tag.JsConstants.*;

/*
 * Link uses a json structure
 * 
 * {
 * 	  'method': '',
 *    'action': '',
 *    'url': '',
 *    'update': '',
 *    'before': '',
 *    'exec': ''
 *  }
 */
public final class LinkTagHandler extends SmartTagHandler {

	private String label;

	private Integer length;

	private boolean ellipsize;

	private String outcome;

	private String action;

	private String update;

	private String beforeAjax;

	private String afterAjax;

	private Integer tabIndex;

	private boolean async = true;

	private List<SmartTagHandler> actionItems;

	public LinkTagHandler() {
		actionItems = new ArrayList<SmartTagHandler>();
	}

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

		StringWriter sw = new StringWriter();

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}

		HttpServletRequest request = getRequest();
		StringBuilder builder = new StringBuilder();

		if (!actionItems.isEmpty()) {
			builder.append(OPEN_DIV_TAG);
			appendClass(builder, CSS_LINK_GROUP);
			builder.append(CLOSE_TAG);
		}

		builder.append(OPEN_LINK_TAG);

		StringBuilder urlParams = new StringBuilder("?");
		for (String key : params.keySet()) {
			urlParams.append(key + "=" + params.get(key) + "&");
		}

		if (id != null) {
			builder.append("id=\"" + id + "\" ");
		}
		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CSS_LINK);
		}
		if (tabIndex != null) {
			builder.append("tabindex=\"" + tabIndex + "\" ");
		}

		String outcomeVal = null; 
		if (outcome != null) {
			outcomeVal = SmartUtils.decodePath((String) getTagValue(outcome));
		}

		String url = "";
		String href = "#";
		if (outcomeVal != null) {
			url = (outcomeVal.startsWith("/") ? outcomeVal.replaceFirst("/", "") : outcomeVal) + urlParams.substring(0, urlParams.length() -1);
			href = (!url.startsWith("http") && !url.startsWith("mailto") ? request.getContextPath() + "/" : "") + url;
		}

		builder.append("href=\"" + href + "\" ");

		if (action != null || update != null || beforeAjax != null || afterAjax != null) {

			builder.append(ON_CLICK + JSMART_LINK.format(async, "$(this)") + "return false;\" ");

			JsonLink jsonLink = new JsonLink();
			if (action != null) {
				jsonLink.setMethod("post");
				jsonLink.setAction(getTagName(J_SBMT, action));

				for (String name : params.keySet()) {						
					jsonLink.getParams().add(new JsonParam(name, params.get(name)));
				}
				if (update == null && afterAjax == null) {
					jsonLink.setUrl(url);
				}
			} else if (update != null) {
				jsonLink.setMethod("get");
			}
			jsonLink.setUpdate(update);
			jsonLink.setBefore(beforeAjax);
			jsonLink.setExec(afterAjax);

			builder.append("ajax=\"" + getJsonValue(jsonLink) + "\" ");
		}

		appendEvent(builder);

		builder.append(">");

		Object labelVal = getTagValue(label);

		if (labelVal != null && labelVal instanceof String) {
			if (length != null && length > 0 && labelVal.toString().length() >= length) {
				if (ellipsize && length > 4) {
					labelVal = labelVal.toString().substring(0, length - 4) + " ...";
				} else {
					labelVal = labelVal.toString().substring(0, length);
				}
			}
			builder.append(labelVal);

		} else if (!sw.toString().isEmpty()) {
			builder.append(sw.toString());

		} else {
			builder.append(href);
		}

		builder.append(CLOSE_LINK_TAG);

		if (!actionItems.isEmpty()) {
			
			builder.append(OPEN_DIV_TAG);
			appendClass(builder, CSS_LINK_DROPDOWN_ARROW);
			builder.append(ON_CLICK + JSMART_LINK_DROPDOWN.format("$(this)") + "return false;\" ");
			builder.append(CLOSE_TAG + CLOSE_DIV_TAG);

			builder.append(OPEN_UNORDERED_LIST_TAG);
			appendClass(builder, CSS_LINK_DROPDOWN_LIST);
			builder.append(CLOSE_TAG);
	
			for (SmartTagHandler actionItem : actionItems) {
				
				if (actionItem instanceof SeparatorTagHandler) {
					builder.append(OPEN_LIST_ITEM_TAG);
					appendClass(builder, CSS_LINK_DROPDOWN_SEPARATOR);
					builder.append(CLOSE_TAG);
					builder.append(CLOSE_LIST_ITEM_TAG);

				} else if (actionItem instanceof LinkActionTagHandler) {

					LinkActionTagHandler linkActionItem = (LinkActionTagHandler) actionItem;

					builder.append(OPEN_LIST_ITEM_TAG + CLOSE_TAG);
					builder.append(OPEN_LINK_TAG);
	
					builder.append(ON_CLICK + JSMART_LINK.format(async, "$(this)") + "return false;\" ");
	
					JsonLink jsonLink = new JsonLink();
					jsonLink.setMethod("post");
					jsonLink.setAction(getTagName(J_SBMT, linkActionItem.getAction()));
	
					for (String name : linkActionItem.getParams().keySet()) {						
						jsonLink.getParams().add(new JsonParam(name, linkActionItem.getParams().get(name)));
					}
	
					jsonLink.setUpdate(update);
					jsonLink.setBefore(beforeAjax);
					jsonLink.setExec(afterAjax);
					builder.append("ajax=\"" + getJsonValue(jsonLink) + "\" >");
	
					builder.append(getTagValue(linkActionItem.getLabel()));
	
					builder.append(CLOSE_LINK_TAG);
					builder.append(CLOSE_LIST_ITEM_TAG);
				}
			}

			builder.append(CLOSE_UNORDERED_LIST_TAG);
			builder.append(CLOSE_DIV_TAG);
		}

		printOutput(builder);
	}

	/*package*/ void addActionItem(SmartTagHandler actionItem) {
		this.actionItems.add(actionItem);
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public void setEllipsize(boolean ellipsize) {
		this.ellipsize = ellipsize;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public void setBeforeAjax(String beforeAjax) {
		this.beforeAjax = beforeAjax;
	}

	public void setAfterAjax(String afterAjax) {
		this.afterAjax = afterAjax;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

}
