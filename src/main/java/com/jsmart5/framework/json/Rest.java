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

package com.jsmart5.framework.json;

import java.util.ArrayList;
import java.util.List;

public final class Rest {

	private Object method;

	private Object endpoint;

	private String content;

	private String bodyRoot;

	private Boolean crossdomain;

	private String jsonp;

	private String jsonpcallback;

	private List<Param> params = new ArrayList<Param>();

	private String before;

	private String success;

	private String error;

	public Object getMethod() {
		return method;
	}

	public void setMethod(Object method) {
		this.method = method;
	}

	public Object getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(Object endpoint) {
		this.endpoint = endpoint;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBodyRoot() {
		return bodyRoot;
	}

	public void setBodyRoot(String bodyRoot) {
		this.bodyRoot = bodyRoot;
	}

	public Boolean getCrossdomain() {
		return crossdomain;
	}

	public void setCrossdomain(Boolean crossdomain) {
		this.crossdomain = crossdomain;
	}

	public String getJsonp() {
		return jsonp;
	}

	public void setJsonp(String jsonp) {
		this.jsonp = jsonp;
	}

	public String getJsonpcallback() {
		return jsonpcallback;
	}

	public void setJsonpcallback(String jsonpcallback) {
		this.jsonpcallback = jsonpcallback;
	}

	public List<Param> getParams() {
		return params;
	}

	public void setParams(List<Param> params) {
		this.params = params;
	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
