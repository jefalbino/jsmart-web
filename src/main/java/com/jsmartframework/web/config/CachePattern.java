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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CachePattern {

    private static Pattern filesPattern;

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
        if (filesPattern != null && file != null) {
            Matcher matcher = filesPattern.matcher(file);
            return matcher.find();
        }
        return false;
    }

    public void setFiles(String[] files) {
        this.files = files;
        if (files == null || files.length == 0) {
            return;
        }

        StringBuilder patternBuilder = new StringBuilder("[^\\s]+(\\.(");
        for (int index = 0; index < files.length; index++) {
            if (files[index].contains(".")) {
                files[index] = files[index].replace(".", "");
            }
            if (index != 0) {
                patternBuilder.append("|");
            }
            patternBuilder.append(files[index]);
        }
        patternBuilder.append("))$");
        filesPattern = Pattern.compile(patternBuilder.toString());
    }

}
