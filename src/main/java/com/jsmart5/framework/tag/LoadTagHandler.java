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

package com.jsmart5.framework.tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.jsp.JspException;

import com.jsmart5.framework.manager.SmartTagHandler;

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.CssConstants.*;
import static com.jsmart5.framework.tag.JsConstants.*;

public final class LoadTagHandler extends SmartTagHandler {

	private static final String SMALL = "small";

	private static final String MEDIUM = "medium";

	private static final String LARGE = "large";

	private String label;

	private String image;

	private String html;

	private String size;

	@Override
	public void validateTag() throws JspException {
		if (size != null && !size.equals(SMALL) && !size.equals(MEDIUM) && !size.equals(LARGE)) {
			throw new JspException("Invalid size value for load tag. Valid values are " + SMALL + ", " + MEDIUM + ", " + LARGE);
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		StringBuilder builder = new StringBuilder(OPEN_DIV_TAG + "id=\"" + id + "\" ");

		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		}

		builder.append(">");

		if (size != null) {
			builder.append(OPEN_SPAN_TAG);

			if (size.equals(LARGE)) {
				appendClass(builder, CSS_LOAD_LARGE);

			} else if (size.equals(MEDIUM)) {
				appendClass(builder, CSS_LOAD_MEDIUM);

			} else {
				appendClass(builder, CSS_LOAD_SMALL);
			}
			builder.append(">" + CLOSE_SPAN_TAG);
		}

		if (image != null) {
			builder.append(IMG_TAG + "src=\"" + getTagValue(image) + "\" />");

		} else if (html != null) {
			try {
				InputStream is = getResourceStream(html);
				if (is != null) {
					BufferedReader br = new BufferedReader(new InputStreamReader(is));

					String line;
			    	while ((line = br.readLine()) != null) {
			    		builder.append(line);
			    	}
			    	br.close();
				}
			} catch (IOException ex) {
				throw new JspException(ex);
			}
		}

		if (label != null) {
			builder.append(OPEN_LABEL_TAG + ">" + getTagValue(label) + CLOSE_LABEL_TAG); 
		}

		builder.append(CLOSE_DIV_TAG);		

		appendScriptDeprecated(new StringBuilder(JSMART_LOAD.format(id)));

		printOutput(builder);
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public void setSize(String size) {
		this.size = size;
	}

}
