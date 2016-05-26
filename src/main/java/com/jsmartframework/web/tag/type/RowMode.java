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

public enum RowMode {

    SECTION,
    APPEND;

    public static boolean validate(String rowMode) {
        try {
            RowMode.valueOf(rowMode.toUpperCase());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String[] getValues() {
        int index = 0;
        RowMode[] rowModes = values();
        String[] values = new String[rowModes.length];

        for (RowMode rowMode : rowModes) {
            values[index++] = rowMode.name().toLowerCase();
        }
        return values;
    }

    public boolean equalsIgnoreCase(String string) {
        return this.name().equalsIgnoreCase(string);
    }
}
