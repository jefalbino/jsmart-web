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

package com.jsmartframework.web.config;

import com.jsmartframework.web.util.WebUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public final class UrlPattern {

    private String url;

    private String jsp;

    private String[] access; // *, roles allowed with comma separated

    private boolean loggedAccess = true;

    @XmlValue
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = WebUtils.decodePath(url);
    }

    @XmlAttribute
    public String getJsp() {
        return jsp;
    }

    public void setJsp(String jsp) {
        this.jsp = jsp;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(value = AttributeAdapter.class)
    public String[] getAccess() {
        return access;
    }

    public boolean containsAccess(String value) {
        if (access != null) {
            for (String acs : access) {
                if (acs.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setAccess(String[] access) {
        if (access != null && access.length > 0) {
            this.access = access;
        }
    }

    @XmlAttribute
    public boolean isLoggedAccess() {
        return loggedAccess;
    }

    public void setLoggedAccess(boolean loggedAccess) {
        this.loggedAccess = loggedAccess;
    }

}
