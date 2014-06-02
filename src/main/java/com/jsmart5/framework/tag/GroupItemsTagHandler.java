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

package com.jsmart5.framework.tag;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.SmartTagHandler;


public final class GroupItemsTagHandler extends SmartTagHandler {

	private String value;

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	@SuppressWarnings("unchecked")
	public void executeTag() throws JspException, IOException {
		JspTag parent = getParent();
		Object object = getTagValue(value);

		if (object instanceof Map) {
			Map<Object, Object> map = (Map<Object, Object>) object;

			for (Entry<Object, Object> entry : map.entrySet()) {

				GroupItemTagHandler item = new GroupItemTagHandler();
				item.setValue(entry.getKey());
				item.setLabel(entry.getValue() != null ? entry.getValue().toString() : null);

				if (parent instanceof RadioGroupTagHandler) {
					((RadioGroupTagHandler) parent).addItem(item);

				} else if (parent instanceof CheckGroupTagHandler) {
					((CheckGroupTagHandler) parent).addItem(item);
				}
			}
		}
	}

	public void setValue(String value) {
		this.value = value;
	}

}
