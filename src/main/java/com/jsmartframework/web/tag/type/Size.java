/*
 * JSmart Framework - Java Web Development Framework
 * Copyright (c) 2015, Jeferson Albino da Silva, All rights reserved.
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

package com.jsmartframework.web.tag.type;

public enum Size {

    JUSTIFIED,
    LARGE,
    SMALL,
    XSMALL;

    public static boolean validate(String size) {
        try {
            Size.valueOf(size.toUpperCase());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validateSmallLarge(String size) {
        return LARGE.equalsIgnoreCase(size) || SMALL.equalsIgnoreCase(size);
    }

    public static String[] getValues() {
        int index = 0;
        Size[] sizes = values();
        String[] values = new String[sizes.length];

        for (Size size : sizes) {
            values[index++] = size.name().toLowerCase();
        }
        return values;
    }

    public static String[] getSmallLargeValues() {
        String[] values = new String[2];
        values[0] = SMALL.name().toLowerCase();
        values[1] = LARGE.name().toLowerCase();
        return values;
    }

    public boolean equalsIgnoreCase(String string) {
        return this.name().equalsIgnoreCase(string);
    }
}
