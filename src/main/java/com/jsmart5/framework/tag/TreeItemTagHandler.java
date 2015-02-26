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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.util.SmartUtils;

/*
 * TreeItem uses a json structure
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
public class TreeItemTagHandler extends TagHandler {

	private String action;

	private String update;

	private String beforeAjax;

	private String afterAjax;

	private String outcome;

	private String label;

	private Integer length;

	private boolean ellipsize;

	@Override
	public void validateTag() throws JspException {
		
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
//	
//		JspTag parent = getParent();
//		if (parent instanceof TreeTagHandler) {
//			theme = ((TreeTagHandler) parent).getTheme();
//
//		} else if (parent instanceof TreeItemTagHandler) {
//			theme = ((TreeItemTagHandler) parent).getTheme();
//		}
//
//		StringWriter sw = new StringWriter();
//		JspFragment body = getJspBody();
//		if (body != null) {
//			body.invoke(sw);
//		}
//
//		HttpServletRequest request = getRequest();
//		StringBuilder builder = new StringBuilder(OPEN_LIST_ITEM_TAG);
//
//		if (id != null) {
//			builder.append("id=\"" + id + "\" ");
//		}
//		if (style != null) {
//			builder.append("style=\"" + style + "\" ");
//		}
//		if (styleClass != null) {
//			builder.append("class=\"" + styleClass + "\" ");
//		} else {
//			appendClass(builder, CSS_TREE_ITEM);			
//		}
//
//		appendEvent(builder);
//
//		builder.append(CLOSE_TAG);
//
//		builder.append(OPEN_DIV_TAG);
//		
//		if (!sw.toString().isEmpty()) {
//			appendClass(builder, CSS_TREE_ITEM_MARK_CLOSED);
//		} else {
//			appendClass(builder, CSS_TREE_ITEM_MARK_EMPTY);
//		}
//
//		builder.append(CLOSE_TAG);
//		builder.append(CLOSE_DIV_TAG);
//
//		builder.append(OPEN_LINK_TAG);
//
//		String outcomeVal = null; 
//		if (outcome != null) {
//			outcomeVal = SmartUtils.decodePath((String) getTagValue(outcome));
//		}
//
//		String url = "";
//		String href = "#";
//		if (outcomeVal != null) {
//			url = outcomeVal.startsWith("/") ? outcomeVal.replaceFirst("/", "") : outcomeVal;
//			href = (!url.startsWith("http") && !url.startsWith("mailto") ? request.getContextPath() + "/" : "") + url;
//		}
//
//		builder.append("href=\"" + href + "\" ");
//
//		if (action != null || update != null || beforeAjax != null || afterAjax != null) {
//
//			builder.append(ON_CLICK + JSMART_LINK.format(async, "$(this)") + "return false;\" ");
//
//			JsonLink jsonAjax = new JsonLink();
//			if (action != null) {
//				jsonAjax.setMethod("post");
//				jsonAjax.setAction(getTagName(J_SBMT, action));
//				if (update == null && afterAjax == null) {
//					jsonAjax.setUrl(url);
//				}
//			} else if (update != null) {
//				jsonAjax.setMethod("get");
//			}
//
//			jsonAjax.setUpdate(update);
//			jsonAjax.setBefore(beforeAjax);
//			jsonAjax.setExec(afterAjax);
//
//			builder.append("ajax=\"" + getJsonValue(jsonAjax) + "\" ");
//		}
//
//		builder.append(CLOSE_TAG);
//		
//		String labelVal = null;
//		Object objectVal = getTagValue(label);
//
//		if (label != null && objectVal != null) {
//			labelVal = objectVal.toString();
//		} else if (!sw.toString().isEmpty()) {
//			labelVal = sw.toString();
//		} else {
//			labelVal = href;
//		}
//
//		if (length != null && length > 0 && labelVal.length() >= length) {
//			if (ellipsize && length > 4) {
//				labelVal = labelVal.substring(0, length - 4) + " ...";
//			} else {
//				labelVal = labelVal.substring(0, length);
//			}
//		}
//
//		builder.append(labelVal);
//
//		builder.append(CLOSE_LINK_TAG);
//
//		if (!sw.toString().isEmpty()) {
//			builder.append(OPEN_UNORDERED_LIST_TAG + CLOSE_TAG);
//			builder.append(sw);
//			builder.append(CLOSE_UNORDERED_LIST_TAG);
//		}
//
//		builder.append(CLOSE_LIST_ITEM_TAG);
//
//		printOutput(builder);
		return null;
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

	public void setOutcome(String outcome) {
		this.outcome = outcome;
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

}
