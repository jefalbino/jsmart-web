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

package com.jsmartframework.web.util;

import org.apache.commons.lang.StringUtils;

import static com.jsmartframework.web.config.Config.CONFIG;

import java.util.regex.Pattern;

import javax.servlet.ServletContext;

/**
 * This class represents the container of image paths packed inside the project.
 * <br>
 * The images are separated by libraries that is mapped according to the 
 * folder containing the images.
 */
public enum WebImage {

    IMAGES();

    private static final Pattern DOT_PATTERN = Pattern.compile("\\.");

    private static String contextPath;

    public void init(ServletContext servletContext) {
        if (contextPath == null) {
            contextPath = "";
            String assetsUrl = CONFIG.getContent().getAssetsUrl();

            if (StringUtils.isNotBlank(assetsUrl)) {
                contextPath = assetsUrl.endsWith("/") ? assetsUrl.substring(0, assetsUrl.length() - 1) : assetsUrl;
            } else if (StringUtils.isNotBlank(servletContext.getContextPath())) {
                contextPath = servletContext.getContextPath();
            }
        }
    }

    /**
     * Returns the image path according to its library and image name.
     * Case not found internally, this image is maybe stored on assetsUrl link,
     * so point to there.
     *
     * @param lib folder path name containing the desired image.
     * @param name name of the image including its extension.
     * @return the path to get the image resource.
     */
    public static String getImage(String lib, String name) {
        if (lib != null) {
            StringBuilder imagePath = new StringBuilder(contextPath);

            // Library used to create url for internal and external images
            String library = DOT_PATTERN.matcher(lib).replaceAll("/");
            if (!library.startsWith("/")) {
                imagePath.append("/");
            }

            if (!library.endsWith("/")) {
                imagePath.append(library).append("/");
            } else {
                imagePath.append(library);
            }
            return imagePath.append(name.replace("/", "")).toString();
        }
        return null;
    }
}
