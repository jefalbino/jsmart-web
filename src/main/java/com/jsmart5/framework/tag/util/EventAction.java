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

package com.jsmart5.framework.tag.util;

import com.jsmart5.framework.json.Ajax;
import com.jsmart5.framework.json.Bind;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class EventAction {

	private Map<String, Set<Ajax>> ajaxs;

	private Map<String, Set<Bind>> binds;

	public Map<String, Set<Ajax>> getAjaxs() {
		return ajaxs;
	}

	public Set<Ajax> getAjax(String event) {
		return ajaxs.get(event);
	}

	public void addAjax(String event, Ajax ajax) {
		if (ajaxs == null) {
			ajaxs = new HashMap<String, Set<Ajax>>();
		}
		Set<Ajax> set = ajaxs.get(event);
		if (set == null) {
			set = new LinkedHashSet<Ajax>(2);
			ajaxs.put(event, set);
		}
		set.add(ajax);
	}
	
	public Map<String, Set<Bind>> getBinds() {
		return binds;
	}

	public Set<Bind> getBind(String event) {
		return binds.get(event);
	}

	public void addBind(String event, Bind bind) {
		if (binds == null) {
			binds = new HashMap<String, Set<Bind>>();
		}
		Set<Bind> set = binds.get(event);
		if (set == null) {
			set = new LinkedHashSet<Bind>(2);
			binds.put(event, set);
		}
		set.add(bind);
	}

}
