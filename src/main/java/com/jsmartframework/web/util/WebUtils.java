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

import org.apache.commons.codec.binary.Base32;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Random;

public final class WebUtils {

	private static final int DEFAULT_RANDOM_BYTES = 8;

	private static final Random random = new SecureRandom();

	private static final Base32 base32 = new Base32();
	
	private WebUtils() {
		// DO NOTHING
	}

	public static String decodePath(String path) {
		if (path != null && !path.startsWith("/")) {
			try {
				new URL(path);
			} catch (MalformedURLException ex) {
				path = "/" + path;
			}
		}
		return path;
	}

	public static String randomId() {
		final byte[] bytes = new byte[DEFAULT_RANDOM_BYTES];
		random.nextBytes(bytes);
		String base32String = base32.encodeAsString(bytes);
		return base32String.toLowerCase().replace("=", "");
	}

}
