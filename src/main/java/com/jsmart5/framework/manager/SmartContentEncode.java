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

package com.jsmart5.framework.manager;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/*package*/ final class SmartContentEncode {

	private String encode;

	private String[] mimeTypes;

	@XmlAttribute
	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		if (encode != null) {
			this.encode = encode.toLowerCase();
		}
	}

	@XmlAttribute
	@XmlJavaTypeAdapter(value = SmartAttributeAdapter.class)
	public String[] getMimeTypes() {
		return mimeTypes;
	}

	public void setMimeTypes(String[] mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

}