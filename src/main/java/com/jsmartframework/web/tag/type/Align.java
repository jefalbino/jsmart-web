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

package com.jsmartframework.web.tag.type;

public enum Align {

    LEFT,
    RIGHT,
    CENTER,
    JUSTIFY;

    public static boolean validate(String align) {
        try {
            Align.valueOf(align.toUpperCase());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validateLeftRight(String align) {
        return LEFT.equalsIgnoreCase(align) || RIGHT.equalsIgnoreCase(align);
    }

    public static boolean validateLeftRightCenter(String align) {
        return validateLeftRight(align) || CENTER.equalsIgnoreCase(align);
    }

    public static String[] getValues() {
        int index = 0;
        Align[] aligns = values();
        String[] values = new String[aligns.length];

        for (Align align : aligns) {
            values[index++] = align.name().toLowerCase();
        }
        return values;
    }

    public static String[] getLeftRightValues() {
        String[] values = new String[2];
        values[0] = LEFT.name().toLowerCase();
        values[1] = RIGHT.name().toLowerCase();
        return values;
    }

    public static String[] getLeftRightCenterValues() {
        String[] values = new String[3];
        values[0] = LEFT.name().toLowerCase();
        values[1] = RIGHT.name().toLowerCase();
        values[2] = CENTER.name().toLowerCase();
        return values;
    }

    public boolean equalsIgnoreCase(String string) {
        return this.name().equalsIgnoreCase(string);
    }

}
