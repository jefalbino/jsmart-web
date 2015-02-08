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

package com.jsmart5.framework.config;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class SmartAttributeAdapter extends XmlAdapter<String, String[]> {

	@Override
	public String marshal(String[] values) throws Exception {
		String string = "";
		for (String value : values) {
			string += value + ",";
		}
		return string;
	}

	@Override
	public String[] unmarshal(String value) throws Exception {
		String[] values = value.split(",");
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i].trim();
		}
		return values;
	}

}
