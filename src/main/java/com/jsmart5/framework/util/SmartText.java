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

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.jsmart5.framework.manager.SmartContext;

/**
 * This class represents the container of text resources mapped on configuration file
 * based on the different {@link Locale} mapped for those resources.
 * <br>
 * Each text properties file is mapped as resource in this container according to what
 * was mapped on configuration. 
 */
public enum SmartText {

	TEXTS();

	private static final Logger LOGGER = Logger.getLogger(SmartText.class.getPackage().getName());
	
	private static final Pattern BRACKETS = Pattern.compile(".*\\{[0-9]*\\}.*");

	private final SmartTextControl control = new SmartTextControl();

	private Set<String> resources = new HashSet<String>();

	private Locale defaultLocale;

	public void init(String[] messageFiles, String defaultLocale) {
		if (messageFiles != null) {
			for (String msg : messageFiles) {
				this.resources.add(msg);
			}
		}
		if (defaultLocale != null) {
			this.defaultLocale = new Locale(defaultLocale);
		}
	}

	/**
	 * Return <code>true</code> if some resource mapped on configuration file is
	 * presented on this container, <code>false</code> otherwise.
	 * 
	 * @param res resource name mapped on configuration file.
	 * @return boolean indicating the presence of specified resource.
	 */
	public static boolean containsResource(final String res) {
		return TEXTS.resources.contains(res);
	}

	/**
	 * Returns the string mapped by specified resource and key inside the file according
	 * to the standard of properties file. 
	 * <br>
	 * The string returned considers the {@link Locale} of the current request being processed.
	 * 
	 * @param res resource name mapped on configuration file.
	 * @param key key of the string inside the properties file
	 * @return the string on resource file according to the {@link Locale} of the request.
	 */
	public static String getString(final String res, final String key) {
		try {
			if (containsResource(res)) {
				return ResourceBundle.getBundle(res, SmartContext.getLocale(), TEXTS.control).getString(key);
			} else {
				LOGGER.log(Level.INFO, "Resource " + res + " not found!");
			}
		} catch (MissingResourceException ex) {
			LOGGER.log(Level.INFO, "Message for " + key + " not found: " + ex.getMessage());
		}
		return "???";
	}
	
	public static String getString(final String res, final String key, final Object ... params) {
		String string = getString(res, key);
		string = formatString(string, params);
		return string;
	}

	public static String formatString(String string, final Object ... params) {
		if (string != null && params != null && params.length > 0) {

			if (BRACKETS.matcher(string).find()) {
				string = MessageFormat.format(string, params);

			} else if (string.contains("%s")) {
				string = String.format(string, params);
			}
		}
		return string;
	}

	private class SmartTextControl extends ResourceBundle.Control {

		@Override
		public List<Locale> getCandidateLocales(String baseName, Locale locale) {
			List<Locale> locales = super.getCandidateLocales(baseName, locale);
			if (defaultLocale != null) {
				locales.add(locales.indexOf(Locale.ROOT), defaultLocale);
			}
			return locales;
		}
	}

}
