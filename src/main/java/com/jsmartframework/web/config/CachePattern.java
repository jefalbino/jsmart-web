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

package com.jsmartframework.web.config;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public final class CachePattern {

    private String cacheControl;

    private String[] files;

    @XmlAttribute
    public String getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(String cacheControl) {
        if (StringUtils.isNotBlank(cacheControl)) {
            this.cacheControl = cacheControl;
        }
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(value = AttributeAdapter.class)
    public String[] getFiles() {
        return files;
    }

    public boolean isEndedIn(String file) {
        if (files != null && file != null) {
            for (String fl : files) {
                if (file.endsWith("." + fl)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setFiles(String[] files) {
        this.files = files;
    }

}
