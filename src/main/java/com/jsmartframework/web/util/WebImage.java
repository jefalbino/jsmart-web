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

package com.jsmartframework.web.util;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static com.jsmartframework.web.config.Config.CONFIG;

/**
 * This class represents the container of image paths packed inside the project.
 * <br>
 * The images are separated by libraries that is mapped according to the 
 * folder containing the images. The image extensions supported are png, jpg,
 * jpeg, gif, bmp and ico. 
 */
public enum WebImage {

	IMAGES();

    private static final Pattern DOT_PATTERN = Pattern.compile("\\.");

    private static String assetsUrl = CONFIG.getContent().getAssetsUrl();

    private Map<String, Map<String, String>> libraries =  new ConcurrentHashMap<>();

	public void init(ServletContext servletContext) {
        if (assetsUrl != null && assetsUrl.endsWith("/")) {
            assetsUrl = assetsUrl.substring(0, assetsUrl.length() - 1);
        }
		lookupInResourcePath(servletContext, "/");
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
            // This is for image url pointing to assetsUrl case
            // image is not found internally
            String externalImg = assetsUrl != null ? assetsUrl : "";

            // Library used to create url for internal and external images
			String library = DOT_PATTERN.matcher(lib).replaceAll("/");

			if (!library.startsWith("/")) {
				library = "/" + library;
			}
            externalImg += library;

			if (library.endsWith("/")) {
				library = library.substring(0, library.length() - 1);

			} else if (!name.startsWith("/")) {
                externalImg += "/";
            }
            externalImg += name;

            String internalImg = null;
			Map<String, String> internalImgs = IMAGES.libraries.get(library);

			if (internalImgs != null) {
                if (name.startsWith("/")) {
                    name = name.substring(1);
                }
                internalImg = internalImgs.get(name);
			}

            if (assetsUrl != null) {
                return externalImg;

            } else if (internalImg != null) {
                return internalImg;
            }
		}
		return null;
	}

	private void lookupInResourcePath(ServletContext servletContext, String path) {
		Set<String> resources = servletContext.getResourcePaths(path);
		if (resources != null) {
			Map<String, String> library = null;

			for (String res : resources) {
				if (res.endsWith(".png") || res.endsWith(".jpg") || res.endsWith(".jpeg") || res.endsWith(".gif") || res.endsWith(".bmp") || res.endsWith(".ico")) {
					if (library == null) {
						library = new HashMap<String, String>();
					}
					String[] bars = res.split("/");
					library.put(bars[bars.length -1], res.replace("WEB-INF/classes/", "/"));
				} else {
					lookupInResourcePath(servletContext, res);
				}
			}

			// Add library to libraries
			if (library != null) {
				IMAGES.libraries.put(path.substring(0, path.length() -1), library);
			}
		}
	}

}
