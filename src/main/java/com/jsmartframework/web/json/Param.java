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

package com.jsmartframework.web.json;

public final class Param {

	private String name;

	private Object value;

    // It does not work with delegate for any iterable component
    private String bind;

	public Param() {
	}

	public Param(String name, Object value) {
		this.name = name;
		this.value = value;
	}

    public Param(String name, Object value, String bind) {
        this.name = name;
        this.value = value;
        this.bind = bind;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

    public String getBind() {
        return bind;
    }

    public void setBind(String bind) {
        this.bind = bind;
    }
}
