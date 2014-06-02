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
import javax.xml.bind.annotation.XmlValue;

/*package*/ final class SmartHtmlCompress {

	private boolean compressHtml = true;

	private boolean skipComments = false;

	@XmlValue
	public boolean isCompressHtml() {
		return compressHtml;
	}

	public void setCompressHtml(boolean compressHtml) {
		this.compressHtml = compressHtml;
	}

	@XmlAttribute
	public boolean isSkipComments() {
		return skipComments;
	}

	public void setSkipComments(boolean skipComments) {
		this.skipComments = skipComments;
	}

}
