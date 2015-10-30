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

public enum Type {

    NUMBER,
    DATE,

    RESPONSIVE,
    ROUND,
    CIRCLE,
    THUMBNAIL,

    TEXT,
    PASSWORD,
    HIDDEN,
    SEARCH,
    RANGE,
    EMAIL,
    URL,
    MONTH,
    WEEK,
    TIME,
    DATETIME,
    DATETIME_LOCAL,
    COLOR,
    TEL,

    FILE,

    FIELDSET,
    SECTION,

    STACKED,
    REGULAR;

    public static boolean validate(String type) {
        try {
            Type.valueOf(type.toUpperCase());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validateFormat(String format) {
        return NUMBER.equalsIgnoreCase(format) || DATE.equalsIgnoreCase(format);
    }

    public static boolean validatePanel(String panel) {
        return FIELDSET.equalsIgnoreCase(panel) || SECTION.equalsIgnoreCase(panel);
    }

    public static boolean validateTab(String tab) {
        return STACKED.equalsIgnoreCase(tab) || REGULAR.equalsIgnoreCase(tab);
    }

    public static boolean validateImage(String image) {
        return RESPONSIVE.equalsIgnoreCase(image) || ROUND.equalsIgnoreCase(image)
                || CIRCLE.equalsIgnoreCase(image) || THUMBNAIL.equalsIgnoreCase(image);
    }

    public static boolean validateInput(String input) {
        return TEXT.equalsIgnoreCase(input) || PASSWORD.equalsIgnoreCase(input)
                || HIDDEN.equalsIgnoreCase(input) || NUMBER.equalsIgnoreCase(input)
                || SEARCH.equalsIgnoreCase(input) || RANGE.equalsIgnoreCase(input)
                || EMAIL.equalsIgnoreCase(input) || URL.equalsIgnoreCase(input)
                || DATE.equalsIgnoreCase(input) || MONTH.equalsIgnoreCase(input)
                || WEEK.equalsIgnoreCase(input) || TIME.equalsIgnoreCase(input)
                || DATETIME.equalsIgnoreCase(input) || DATETIME_LOCAL.name().replace("_", "-").equalsIgnoreCase(input)
                || COLOR.equalsIgnoreCase(input) || TEL.equalsIgnoreCase(input);
    }

    public static String[] getValues() {
        int index = 0;
        Type[] types = values();
        String[] values = new String[types.length];

        for (Type type : types) {
            values[index++] = type.name().toLowerCase();
        }
        return values;
    }

    public static String[] getFormatValues() {
        String[] values = new String[2];
        values[0] = NUMBER.name().toLowerCase();
        values[1] = DATE.name().toLowerCase();
        return values;
    }

    public static String[] getPanelValues() {
        String[] values = new String[2];
        values[0] = FIELDSET.name().toLowerCase();
        values[1] = SECTION.name().toLowerCase();
        return values;
    }

    public static String[] getTabValues() {
        String[] values = new String[2];
        values[0] = STACKED.name().toLowerCase();
        values[1] = REGULAR.name().toLowerCase();
        return values;
    }

    public static String[] getImageValues() {
        String[] values = new String[4];
        values[0] = RESPONSIVE.name().toLowerCase();
        values[1] = ROUND.name().toLowerCase();
        values[2] = CIRCLE.name().toLowerCase();
        values[3] = THUMBNAIL.name().toLowerCase();
        return values;
    }

    public static String[] getInputValues() {
        String[] values = new String[16];
        values[0] = TEXT.name().toLowerCase();
        values[1] = PASSWORD.name().toLowerCase();
        values[2] = HIDDEN.name().toLowerCase();
        values[3] = NUMBER.name().toLowerCase();
        values[4] = SEARCH.name().toLowerCase();
        values[5] = RANGE.name().toLowerCase();
        values[6] = EMAIL.name().toLowerCase();
        values[7] = URL.name().toLowerCase();
        values[8] = DATE.name().toLowerCase();
        values[9] = MONTH.name().toLowerCase();
        values[10] = WEEK.name().toLowerCase();
        values[11] = TIME.name().toLowerCase();
        values[12] = DATETIME.name().toLowerCase();
        values[13] = DATETIME_LOCAL.name().replace("_", "-").toLowerCase();
        values[14] = COLOR.name().toLowerCase();
        values[15] = TEL.name().toLowerCase();
        return values;
    }

    public boolean equalsIgnoreCase(String string) {
        return this.name().equalsIgnoreCase(string);
    }
}
