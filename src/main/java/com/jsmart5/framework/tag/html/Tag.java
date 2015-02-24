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

package com.jsmart5.framework.tag.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tag {
	
	protected String name;

	protected Map<String, Object> attributes;

	protected List<Object> objects;

	public Tag(String name) {
		this.name = name;
		this.attributes = new HashMap<String, Object>();
		this.objects = new ArrayList<Object>();
	}

	public Tag addAttribute(String name, Object value) {
		if (value != null) {
			Object oldValue = attributes.get(name);
			if (oldValue == null) {
				attributes.put(name, value);
			} else {
				attributes.put(name, oldValue + " " + value);
			}
		}
		return this;
	}

	public Tag addText(Object text) {
		if (text != null) {
			this.objects.add(text.toString());
		}
		return this;
	}

	public Tag addTag(Tag tag) {
		this.objects.add(tag);
		return this;
	}

	public StringBuilder getHtml() {
		StringBuilder builder = new StringBuilder();
		builder.append("<").append(name);

		for (String attr : attributes.keySet()) {
			builder.append(" ").append(attr).append("=\"").append(attributes.get(attr)).append("\"");
		}
		builder.append(">");

		for (Object obj : objects) {
			if (obj instanceof Tag) {
				builder.append(((Tag) obj).getHtml());
			} else {
				builder.append(obj);
			}
		}

		builder.append("</").append(name).append(">");
		return builder;
	}

}
