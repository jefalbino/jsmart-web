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

package com.jsmart5.framework.tag.type;

public enum Event {

	SELECT,
	CHANGE,
	BLUR,
	CLICK,
	DBLCLICK,
	MOUSEDOWN,
	MOUSEMOVE,
	MOUSEOVER,
	MOUSEOUT,
	MOUSEUP,
	KEYDOWN,
	KEYPRESS,
	KEYUP,
	FOCUS,
	SUBMIT;

	public static boolean validate(String event) {
		try {
			Event.valueOf(event.toUpperCase());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String[] getValues() {
		int index = 0;
		Event[] events = values();
		String[] values = new String[events.length];

		for (Event event : events) {
			values[index++] = event.name().toLowerCase();
		}
		return values;
	}

	public boolean equalsIgnoreCase(String string) {
		return this.name().equalsIgnoreCase(string);
	}
}
