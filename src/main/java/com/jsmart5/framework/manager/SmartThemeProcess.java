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

package com.jsmart5.framework.manager;

import static com.jsmart5.framework.config.SmartConstants.FILTER_STYLES;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.jsmart5.framework.config.SmartTheme;

public final class SmartThemeProcess {

	private static final Logger LOGGER = Logger.getLogger(SmartThemeProcess.class.getPackage().getName());

	private static final String DEFAULT_THEME = "default";

	private static final int STREAM_BUFFER = 10000;

	private static final Pattern CSS_MARK_PATTERN = Pattern.compile("@.{1,20}?@");

	private static final String BASE_CSS = "css/jsmart5.base.css";

	private static final String THEME_CSS = "/css/jsmart5-theme.css";

	public static void main(String[] args) {
		try {
			JSONObject jsonStyles = new JSONObject(convertResourceToString(FILTER_STYLES));

        	// Always create new file
        	URL resourceURL = Thread.currentThread().getContextClassLoader().getResource(".");
        	File outputCss = new File(resourceURL.getPath().replace("/target/classes", "/src/main/resources") + THEME_CSS);
        	if (outputCss.exists()) {
        		outputCss.delete();
        	}
        	outputCss.createNewFile();

        	FileOutputStream fos = new FileOutputStream(outputCss);
            BufferedOutputStream bos = new BufferedOutputStream(fos, STREAM_BUFFER);

            for (SmartTheme theme : SmartTheme.values()) {
            	int count = 0;
            	byte data[] = new byte[STREAM_BUFFER];
            	BufferedInputStream bis = new BufferedInputStream(getResource(BASE_CSS));
            	
            	JSONObject defaultStyle = jsonStyles.getJSONObject(DEFAULT_THEME);
            	JSONObject jsonStyle = jsonStyles.getJSONObject(theme.toString());
            	
            	LOGGER.log(Level.INFO, "Generating css for theme: " + theme);

                while ((count = bis.read(data, 0, STREAM_BUFFER)) != -1) {
                	String cssData = new String(data, 0, count).replace("%s", theme.toString());
            		Matcher cssMatcher = CSS_MARK_PATTERN.matcher(cssData);

    				while (cssMatcher.find()) {
    					String match = cssMatcher.group();
    					if (jsonStyle.has(match)) {
    						cssData = cssData.replace(match, jsonStyle.getString(match));
    					} else if (defaultStyle.has(match)) {
    						cssData = cssData.replace(match, defaultStyle.getString(match));
    					}
    				}
    				bos.write(cssData.getBytes(), 0, cssData.length());
                }

                bos.flush();
                bis.close();
            }
            bos.close();

		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, "Output css file not found: " + ex.getMessage(), ex);

		} catch (JSONException ex) {
			LOGGER.log(Level.SEVERE, "Failure to load JSON resources: " + ex.getMessage(), ex);
		}
	}

	@SuppressWarnings("resource")
	private static String convertResourceToString(String resource) {
		InputStream is = getResource(resource);
		Scanner scanner = new Scanner(is).useDelimiter("\\A");
		return scanner.hasNext() ? scanner.next() : "";
	}

	private static InputStream getResource(String resource) {
		return SmartThemeProcess.class.getClassLoader().getResourceAsStream(resource);
	}

}
