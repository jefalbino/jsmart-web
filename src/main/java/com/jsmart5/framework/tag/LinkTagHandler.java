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

import com.jsmart5.framework.json.JSONLink;
import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.manager.SmartUtils;

import static com.jsmart5.framework.tag.JSConstants.*;

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
		StringBuilder builder = new StringBuilder(HtmlConstants.OPEN_LINK_TAG);

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
			appendClass(builder, CssConstants.CSS_LINK);
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

			JSONLink jsonAjax = new JSONLink();
			if (action != null) {
				jsonAjax.setMethod("post");
				jsonAjax.setAction(getTagName(J_SBMT, action));
				if (update == null && afterAjax == null) {
					jsonAjax.setUrl(url);
				}
			} else if (update != null) {
				jsonAjax.setMethod("get");
			}
			jsonAjax.setUpdate(update);
			jsonAjax.setBefore(beforeAjax);
			jsonAjax.setExec(afterAjax);

			builder.append("ajax=\"" + getJSONValue(jsonAjax) + "\" ");
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

		builder.append(HtmlConstants.CLOSE_LINK_TAG);

		printOutput(builder);
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
