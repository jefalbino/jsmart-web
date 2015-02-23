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

package com.jsmart5.framework.tag.type;

public enum Look {

	DEFAULT,
	PRIMARY,
	SUCCESS,
	INFO,
	WARNING,
	DANGER,
	MUTED,
	LINK,
	ERROR;
	
	public static boolean validate(String look) {
		try {
			Look.valueOf(look.toUpperCase());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean validateValidate(String look) {
		return SUCCESS.name().equalsIgnoreCase(look) || WARNING.name().equalsIgnoreCase(look)
				|| ERROR.name().equalsIgnoreCase(look);
	}
	
	public static boolean validateBasic(String look) {
		return SUCCESS.name().equalsIgnoreCase(look) || INFO.name().equalsIgnoreCase(look)
				|| WARNING.name().equalsIgnoreCase(look) || DANGER.name().equalsIgnoreCase(look);
	}
	
	public static boolean validateLook(String look) {
		return DEFAULT.name().equalsIgnoreCase(look) || PRIMARY.name().equalsIgnoreCase(look)
				|| SUCCESS.name().equalsIgnoreCase(look) || INFO.name().equalsIgnoreCase(look)
				|| WARNING.name().equalsIgnoreCase(look) || DANGER.name().equalsIgnoreCase(look);
	}
	
	public static boolean validateButton(String look) {
		return validateLook(look) || LINK.name().equalsIgnoreCase(look);
	}
	
	public static boolean validateText(String look) {
		return validateLook(look) || MUTED.name().equalsIgnoreCase(look);
	}

	public static String[] getValues() {
		int index = 0;
		Look[] looks = values();
		String[] values = new String[looks.length];

		for (Look look : looks) {
			values[index++] = look.name().toLowerCase();
		}
		return values;
	}
	
	public static String[] getButtonValues() {
		String[] values = new String[7];
		values[0] = DEFAULT.name().toLowerCase();
		values[1] = PRIMARY.name().toLowerCase();
		values[2] = SUCCESS.name().toLowerCase();
		values[3] = INFO.name().toLowerCase();
		values[4] = WARNING.name().toLowerCase();
		values[5] = DANGER.name().toLowerCase();
		values[6] = LINK.name().toLowerCase();
		return values;
	}
	
	public static String[] getBasicValues() {
		String[] values = new String[4];
		values[0] = SUCCESS.name().toLowerCase();
		values[1] = INFO.name().toLowerCase();
		values[2] = WARNING.name().toLowerCase();
		values[3] = DANGER.name().toLowerCase();
		return values;
	}
	
	public static String[] getValidateValues() {
		String[] values = new String[3];
		values[0] = SUCCESS.name().toLowerCase();
		values[1] = WARNING.name().toLowerCase();
		values[2] = ERROR.name().toLowerCase();
		return values;
	}
	
	public static String[] getLookValues() {
		String[] values = new String[6];
		values[0] = DEFAULT.name().toLowerCase();
		values[1] = PRIMARY.name().toLowerCase();
		values[2] = SUCCESS.name().toLowerCase();
		values[3] = INFO.name().toLowerCase();
		values[4] = WARNING.name().toLowerCase();
		values[5] = DANGER.name().toLowerCase();
		return values;
	}
	
	public static String[] getTextValues() {
		String[] values = new String[7];
		values[0] = DEFAULT.name().toLowerCase();
		values[1] = PRIMARY.name().toLowerCase();
		values[2] = SUCCESS.name().toLowerCase();
		values[3] = INFO.name().toLowerCase();
		values[4] = WARNING.name().toLowerCase();
		values[5] = DANGER.name().toLowerCase();
		values[5] = MUTED.name().toLowerCase();
		return values;
	}
}
