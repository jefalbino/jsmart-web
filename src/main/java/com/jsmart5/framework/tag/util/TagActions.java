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

package com.jsmart5.framework.tag.util;

import java.util.HashSet;
import java.util.Set;

import com.jsmart5.framework.json.Ajax;
import com.jsmart5.framework.json.Bind;

public class TagActions {

	private String ref;
	
	private Set<Ajax> ajaxs = new HashSet<Ajax>();
	
	private Set<Bind> binds = new HashSet<Bind>();
	
	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public Set<Ajax> getAjaxs() {
		return ajaxs;
	}
	
	public void addAjax(Ajax ajax) {
		this.ajaxs.add(ajax);
	}

	public void setAjaxs(Set<Ajax> ajaxs) {
		this.ajaxs = ajaxs;
	}

	public Set<Bind> getBinds() {
		return binds;
	}
	
	public void addBind(Bind bind) {
		this.binds.add(bind);
	}

	public void setBinds(Set<Bind> binds) {
		this.binds = binds;
	}
}
