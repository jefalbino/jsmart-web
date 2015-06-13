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

import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Tag;

public final class RestTagHandler extends TagHandler {

	private static final String CONTENT_TYPE_JSON = "json";

	private static final String CONTENT_TYPE_XML = "xml";

	private static final String POST = "post";

	private static final String GET = "get";

	private static final String DELETE = "delete";

	private static final String PUT = "put";

	private static final String HEAD = "head";

	private static final String OPTIONS = "options";

	private String label;

	private Integer length;

	private boolean ellipsize;

	private String image;

	private String endpoint;

	private String contentType;

	private String rootContent;

	private Boolean crossDomain;

	private String jsonp;

	private String jsonpCallback;

	private String method;

	private Integer timeout;

	private String beforeAjax;

	private String onSuccess;

	private String onError;

	private Integer tabIndex;

	private boolean disabled;

	@Override
	public void validateTag() throws JspException {
		if (!method.equalsIgnoreCase(POST) && !method.equalsIgnoreCase(GET) && !method.equalsIgnoreCase(DELETE) && !method.equalsIgnoreCase(PUT)
				&& !method.equalsIgnoreCase(HEAD) && !method.equalsIgnoreCase(OPTIONS)) {
			throw new JspException("Invalid method for rest tag. Valid values are " + POST + ", " + GET + ", " + DELETE + ", " 
				+ PUT  + ", " + HEAD  + ", " + OPTIONS);
		}
		if (contentType != null && !CONTENT_TYPE_JSON.equalsIgnoreCase(contentType) && !CONTENT_TYPE_XML.equalsIgnoreCase(contentType)) {
			throw new JspException("Invalid contentType for rest tag. Valid values are " + CONTENT_TYPE_JSON + ", " + CONTENT_TYPE_XML);
		}
		if (timeout != null && timeout < 0) {
			throw new JspException("Invalid timeout value for rest tag. The valid value must be greater or equal to 0"); 
		}
		if ((jsonp != null || jsonpCallback != null) && contentType != null && CONTENT_TYPE_XML.equalsIgnoreCase(contentType)) {
			throw new JspException("Invalid contentType value for rest tag. The xml value for contentType attribute cannot be used together jsonp or jsonpCallback attributes");
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {
//		StringBuilder builder = new StringBuilder();
//
//		// Look for parameters
//		JspFragment body = getJspBody();
//		if (body != null) {
//			body.invoke(null);
//		}
//
//		if (image != null) {
//			builder.append(INPUT_TAG);
//		} else {
//			builder.append(OPEN_BUTTON_TAG);
//		}
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
//			if (image != null) {
//				appendClass(builder, CSS_BUTTON_IMAGE);
//			} else {
//				appendClass(builder, CSS_BUTTON);
//			}
//		}
//
//		if (tabIndex != null) {
//			builder.append("tabindex=\"" + tabIndex + "\" ");
//		}
//
//		if (disabled || isEditRowTagEnabled()) {
//			builder.append("disabled=\"disabled\" ");
//		}
//
//		if (image != null) {
//			builder.append("type=\"image\" src=\"" + image + "\" ");
//		} else {
//			builder.append("type=\"button\" ");
//		}
//
//		builder.append(ON_CLICK + JSMART_REST.format(async, "$(this)", timeout != null ? timeout : 0) + "return false;\" ");
//
//		JsonRest jsonRest = new JsonRest();
//		jsonRest.setMethod(getTagValue(method));
//		jsonRest.setEndpoint(WebUtils.decodePath((String) getTagValue(endpoint)));
//		jsonRest.setContent(contentType != null ? contentType.toLowerCase() : CONTENT_TYPE_JSON);
//
//		if (!params.isEmpty()) {
//			for (String name : params.keySet()) {						
//				jsonRest.getParams().add(new JsonParam(name, params.get(name)));
//			}
//		}
//		jsonRest.setBodyRoot(rootContent != null ? rootContent.trim() : null);
//		jsonRest.setCrossdomain(crossDomain);
//		jsonRest.setJsonp(jsonp != null ? jsonp.trim() : null);
//		jsonRest.setJsonpcallback(jsonpCallback != null ? jsonpCallback.trim() : null);
//		jsonRest.setBefore(beforeAjax);
//		jsonRest.setSuccess(onSuccess);
//		jsonRest.setError(onError);
//
//		builder.append("ajax=\"" + getJsonValue(jsonRest) + "\" ");
//
//		String val = (String) getTagValue(label);
//
//		if (val != null && length != null && length > 0 && val.length() >= length) {
//			if (ellipsize && length > 4) {
//				val = val.substring(0, length - 4) + " ...";
//			} else {
//				val = val.substring(0, length);
//			}
//		}
//
//		if (image != null) {
//			builder.append((val != null ? "value=\"" + val + "\"" : "") + " />");
//		} else {
//			builder.append(">" + (val != null ? val : "") + CLOSE_BUTTON_TAG);
//		}
//
//		printOutput(builder);
		return null;
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

	public void setImage(String image) {
		this.image = image;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setRootContent(String rootContent) {
		this.rootContent = rootContent;
	}

	public void setCrossDomain(Boolean crossDomain) {
		this.crossDomain = crossDomain;
	}

	public void setJsonp(String jsonp) {
		this.jsonp = jsonp;
	}

	public void setJsonpCallback(String jsonpCallback) {
		this.jsonpCallback = jsonpCallback;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public void setBeforeAjax(String beforeAjax) {
		this.beforeAjax = beforeAjax;
	}

	public void setOnSuccess(String onSuccess) {
		this.onSuccess = onSuccess;
	}

	public void setOnError(String onError) {
		this.onError = onError;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}