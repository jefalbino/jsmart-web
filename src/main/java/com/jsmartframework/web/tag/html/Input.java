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

package com.jsmartframework.web.tag.html;

public class Input extends Tag {

    public Input() {
        super("input");
    }

    public StringBuilder getHtml() {
        StringBuilder builder = new StringBuilder("<");
        builder.append(name);

        for (String attr : attributes.keySet()) {
            builder.append(" ").append(attr).append("=\"").append(attributes.get(attr)).append("\"");
        }
        builder.append(" />");
        return builder;
    }

}
