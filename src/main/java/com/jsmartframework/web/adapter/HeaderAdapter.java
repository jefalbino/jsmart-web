/*
 * JSmart Framework - Java Web Development Framework
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

package com.jsmartframework.web.adapter;

public final class HeaderAdapter {

	private String title;
	
	private String icon;
	
	private String type;
	
	public HeaderAdapter(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public HeaderAdapter setTitle(String title) {
		this.title = title;
        return this;
	}

	public String getIcon() {
		return icon;
	}

	public HeaderAdapter setIcon(String icon) {
		this.icon = icon;
        return this;
	}

	public String getType() {
		return type;
	}

	public HeaderAdapter setType(String type) {
		this.type = type;
        return this;
	}

}
