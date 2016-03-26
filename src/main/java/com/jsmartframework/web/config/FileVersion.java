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
import java.util.regex.Pattern;

public final class FileVersion {

    private static Pattern extensionsPattern;

    private static Pattern foldersPattern;

    private static Pattern minifiedPattern = Pattern.compile("[^\\s]+(\\.min\\.)+.*");

    private String version;

    private String[] extensions;

    private String[] excludeFolders;

    private boolean includeMinified;

    @XmlAttribute
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        if (StringUtils.isNotBlank(version)) {
            this.version = version;
        }
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(value = AttributeAdapter.class)
    public String[] getExtensions() {
        return extensions;
    }

    public boolean hasExtension(String file) {
        if (extensionsPattern != null && file != null) {
            return extensionsPattern.matcher(file).find();
        }
        return false;
    }

    public void setExtensions(String[] extensions) {
        this.extensions = extensions;
        if (extensions == null || extensions.length == 0) {
            return;
        }

        StringBuilder patternBuilder = new StringBuilder("[^\\s]+(\\.(");
        for (int index = 0; index < extensions.length; index++) {
            if (extensions[index].contains(".")) {
                extensions[index] = extensions[index].replace(".", "");
            }
            if (index != 0) {
                patternBuilder.append("|");
            }
            patternBuilder.append(extensions[index]);
        }
        patternBuilder.append("))$");
        extensionsPattern = Pattern.compile(patternBuilder.toString());
    }

    @XmlAttribute(name = "include-minified")
    public boolean isIncludeMinified() {
        return includeMinified;
    }

    public void setIncludeMinified(boolean includeMinified) {
        this.includeMinified = includeMinified;
    }

    public boolean isMinifiedFile(String file) {
        return minifiedPattern.matcher(file).find();
    }

    @XmlAttribute(name = "exclude-folders")
    @XmlJavaTypeAdapter(value = AttributeAdapter.class)
    public String[] getExcludeFolders() {
        return excludeFolders;
    }

    public boolean isOnExcludeFolders(String file) {
        if (foldersPattern != null && file != null) {
            return foldersPattern.matcher(file).find();
        }
        return false;
    }

    public void setExcludeFolders(String[] excludeFolders) {
        this.excludeFolders = excludeFolders;
        if (excludeFolders == null || excludeFolders.length == 0) {
            return;
        }

        StringBuilder patternBuilder = new StringBuilder("[^\\s]*(");
        for (int index = 0; index < excludeFolders.length; index++) {
            if (excludeFolders[index].contains("/")) {
                excludeFolders[index] = excludeFolders[index].replace("/", "");
            }
            if (index != 0) {
                patternBuilder.append("|");
            }
            patternBuilder.append(excludeFolders[index]);
        }
        patternBuilder.append(")+/.*");
        foldersPattern = Pattern.compile(patternBuilder.toString());
    }

}
