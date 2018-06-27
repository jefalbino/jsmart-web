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

import static com.jsmartframework.web.config.Config.CONFIG;

import com.jsmartframework.web.manager.WebContext;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This class represents the container of text resources mapped on configuration file
 * based on the different {@link Locale} mapped for those resources.
 * <br>
 * Each text properties file is mapped as resource in this container according to what
 * was mapped on configuration via {@code webConfig.xml}.
 */
public enum WebText {

    TEXTS();

    public static final String NOT_FOUND = "???";

    private static final Logger LOGGER = Logger.getLogger(WebText.class.getPackage().getName());

    private static final Pattern BRACKETS = Pattern.compile(".*\\{[0-9]*\\}.*");

    private static Map<String, ResourceBundle> resourceBundles = new ConcurrentHashMap<>();

    private final WebTextControl control = new WebTextControl();

    private static Locale defaultLocale;

    private Set<String> resources = new HashSet<>();

    public void init() {
        String[] messageFiles = CONFIG.getContent().getMessageFiles();
        if (messageFiles != null) {
            for (String msg : messageFiles) {
                resources.add(msg);
            }
        }
        String defaultLanguage = CONFIG.getContent().getDefaultLanguage();
        if (defaultLanguage != null) {
            defaultLocale = new Locale(defaultLanguage);
        }
    }

    public static WebTextSet getStrings(String res, String keyPrefix, String language) {
        if (!containsResource(res)) {
            LOGGER.log(Level.SEVERE, "Resource [" + res + "] not found to return set of messages!");
            return null;
        }

        Locale locale;
        if (language != null) {
            locale = new Locale(language);
        } else {
            locale = defaultLocale != null ? defaultLocale : Locale.getDefault();
        }

        WebTextSet webTextSet = new WebTextSet(locale);
        ResourceBundle bundle = getBundle(res, locale);

        for (String key : bundle.keySet()) {
            if (key.startsWith(keyPrefix)) {
                webTextSet.putValue(key, bundle.getString(key));
            }
        }
        return webTextSet;
    }

    /**
     * Return <code>true</code> if some resource mapped on configuration file is
     * presented on this container, <code>false</code> otherwise.
     *
     * @param res resource name mapped on configuration file.
     * @return boolean indicating the presence of specified resource.
     */
    public static boolean containsResource(String res) {
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
    public static String getString(String res, String key) {
        try {
            if (!containsResource(res)) {
                LOGGER.log(Level.INFO, "Resource [" + res + "] not found!");
                return NOT_FOUND;
            }
            Locale locale = WebContext.getLocale();
            if (locale == null) {
                locale = defaultLocale != null ? defaultLocale : Locale.getDefault();
            }
            return getBundle(res, locale).getString(key);

        } catch (MissingResourceException ex) {
            LOGGER.log(Level.INFO, "Message for [" + key + "] not found: " + ex.getMessage());
        }
        return NOT_FOUND;
    }

    private static ResourceBundle getBundle(String res, String language) {
        return getBundle(res, new Locale(language));
    }

    private static ResourceBundle getBundle(String res, Locale locale) {
        String resourceBundleKey = res + locale.getLanguage();
        ResourceBundle resourceBundle = resourceBundles.get(resourceBundleKey);
        if (resourceBundle == null) {
            synchronized (resourceBundles) {
                if (!resourceBundles.containsKey(resourceBundleKey)) {
                    resourceBundles.put(resourceBundleKey, resourceBundle = ResourceBundle.getBundle(res, locale, TEXTS.control));
                } else {
                    resourceBundle = resourceBundles.get(resourceBundleKey);
                }
            }
        }
        return resourceBundle;
    }

    public static String getString(String res, String key, Object ... params) {
        String string = getString(res, key);
        string = formatString(string, params);
        return string;
    }

    public static String formatString(String string, Object ... params) {
        if (string != null && params != null && params.length > 0) {
            if (BRACKETS.matcher(string).find()) {
                string = MessageFormat.format(string, params);
            } else if (string.contains("%s")) {
                string = String.format(string, params);
            }
        }
        return string;
    }

    private class WebTextControl extends ResourceBundle.Control {

        @Override
        public List<Locale> getCandidateLocales(String baseName, Locale locale) {
            List<Locale> locales = super.getCandidateLocales(baseName, locale);
            if (defaultLocale != null) {
                locales.add(locales.indexOf(Locale.ROOT), defaultLocale);
            }
            return locales;
        }
    }

    public static class WebTextSet {

        private final String language;

        private final Map<String, Object> values = new HashMap<>();

        private WebTextSet(Locale locale) {
            this.language = locale.getLanguage();
        }

        public String getLanguage() {
            return language;
        }

        private void putValue(String key, Object value) {
            values.put(key, value);
        }

        public Map<String, Object> getValues() {
            return values;
        }
    }
}
