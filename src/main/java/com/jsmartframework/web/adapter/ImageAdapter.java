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

public final class ImageAdapter {

	private String lib;
	
	private String name;
	
	private String alt;

	private String width;

	private String height;
	
	public ImageAdapter(String name) {
		this(name, null);
	}
	
	public ImageAdapter(String name, String lib) {
		this.name = name;
		this.lib = lib;
	}

	public String getLib() {
		return lib;
	}

	public ImageAdapter setLib(String lib) {
		this.lib = lib;
        return this;
	}

	public String getName() {
		return name;
	}

	public ImageAdapter setName(String name) {
		this.name = name;
        return this;
	}

	public String getAlt() {
		return alt;
	}

	public ImageAdapter setAlt(String alt) {
		this.alt = alt;
        return this;
	}

	public String getWidth() {
		return width;
	}

	public ImageAdapter setWidth(String width) {
		this.width = width;
        return this;
	}

	public String getHeight() {
		return height;
	}

	public ImageAdapter setHeight(String height) {
		this.height = height;
        return this;
	}

}
