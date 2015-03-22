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

package com.jsmart5.framework.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

/**
 * This class represents the container of image paths packed inside the project.
 * <br>
 * The images are separated by libraries that is mapped according to the 
 * folder containing the images. The image extensions supported are png, jpg,
 * jpeg, gif, bmp and ico. 
 */
public enum SmartImage {

	IMAGES();

	private Map<String, Map<String, String>> libraries =  new HashMap<String, Map<String,String>>();

	public void init(ServletContext servletContext) {
		lookupInResourcePath(servletContext, "/");
	}

	/**
	 * Returns the image path according to its library and image name.
	 * 
	 * @param library folder path name containing the desired image.
	 * @param name name of the image including its extension.
	 * @return the path to get the image resource.
	 */
	public static String getImage(String library, String name) {
		if (library != null) {
			library = library.replaceAll("\\.", "/");

			if (!library.startsWith("/")) {
				library = "/" + library;
			}
			if (library.endsWith("/")) {
				library = library.substring(0, library.length() - 1);
			}
			Map<String, String> names = IMAGES.libraries.get(library);
			if (names != null) {
				return names.get(name);
			}
		}
		return null;
	}

	private static void lookupInResourcePath(ServletContext servletContext, String path) {
		Set<String> resources = servletContext.getResourcePaths(path);
		if (resources != null) {
			Map<String, String> library = null;

			for (String res : resources) {
				if (res.endsWith(".png") || res.endsWith(".jpg") || res.endsWith(".jpeg") || res.endsWith(".gif") || res.endsWith(".bmp") || res.endsWith(".ico")) {
					if (library == null) {
						library = new HashMap<String, String>();
					}
					String[] bars = res.split("/");
					library.put(bars[bars.length -1], res.replaceFirst("/", "").replace("WEB-INF/classes/", ""));
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
