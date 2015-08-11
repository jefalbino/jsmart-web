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

public enum Mode {

	YEARS,
	MONTHS,
	DAYS,
	TIMEONLY;

	public static boolean validate(String mode) {
		try {
			Mode.valueOf(mode.toUpperCase());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String[] getValues() {
		int index = 0;
		Mode[] modes = values();
		String[] values = new String[modes.length];

		for (Mode mode : modes) {
			values[index++] = mode.name().toLowerCase();
		}
		return values;
	}

	public boolean equalsIgnoreCase(String string) {
		return this.name().equalsIgnoreCase(string);
	}

}
