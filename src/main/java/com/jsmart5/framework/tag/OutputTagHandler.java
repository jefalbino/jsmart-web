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

import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.jsmart5.framework.manager.SmartTagHandler;

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.CssConstants.*;

public final class OutputTagHandler extends SmartTagHandler {

	private static final String LEGEND_TYPE = "legend";

	private static final String STRONG_TYPE = "strong";

	private static final String MARK_TYPE = "mark";

	private static final String EM_TYPE = "em";

	private static final String SMALL_TYPE = "small";

	private static final String LABEL_TYPE = "label";

	private static final String OUTPUT_TYPE = "output";

	private Object value;

	private Integer length;

	private boolean ellipsize;

	private boolean uppercase;

	private boolean lowercase;

	private boolean capitalize;

	private String type;

	private String target;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof GridTagHandler) {

			((GridTagHandler) parent).addTag(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		if (type != null && !LEGEND_TYPE.equals(type) && !STRONG_TYPE.equals(type) && !MARK_TYPE.equals(type) 
				&& !EM_TYPE.equals(type) && !SMALL_TYPE.equals(type) && !LABEL_TYPE.equals(type) && !OUTPUT_TYPE.equals(type)) {
			throw new JspException("Invalid type value for output tag. Valid values are "
					+ LEGEND_TYPE + ", " + STRONG_TYPE + ", " + MARK_TYPE + ", " + EM_TYPE + ", " + SMALL_TYPE + ", " + LABEL_TYPE + " and " + OUTPUT_TYPE);
		}
	}

	@Override
	@SuppressWarnings("all")
	public void executeTag() throws JspException, IOException {

		StringWriter sw = new StringWriter();

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}

		StringBuilder builder = new StringBuilder();

		if (LEGEND_TYPE.equals(type)) {
			builder.append(OPEN_LEGEND_TAG);

		} else if (STRONG_TYPE.equals(type)) {
			builder.append(OPEN_STRONG_TAG);

		} else if (MARK_TYPE.equals(type)) {
			builder.append(OPEN_MARK_TAG);

		} else if (EM_TYPE.equals(type)) {
			builder.append(OPEN_EM_TAG);

		} else if (SMALL_TYPE.equals(type)) {
			builder.append(OPEN_SMALL_TAG);

		} else if (LABEL_TYPE.equals(type)) {
			builder.append(OPEN_LABEL_TAG);

		} else if (OUTPUT_TYPE.equals(type)) {
			builder.append(OPEN_OUTPUT_TAG);

		} else {
			builder.append(OPEN_SPAN_TAG);
		}

		if (id != null) {
			builder.append("id=\"" + id + "\" ");
		}

		String outputTransform = "";

		if (capitalize) {
			outputTransform += (style != null ? "; " : "") + "text-transform: capitalize";
		} else if (uppercase) {
			outputTransform += (style != null ? "; " : "") + "text-transform: uppercase";
		} else if (lowercase) {
			outputTransform += (style != null ? "; " : "") + "text-transform: lowercase";
		}

		if (style != null) {
			builder.append("style=\"" + style + outputTransform + "\" ");
		} else if (!outputTransform.isEmpty()) {
			builder.append("style=\"" + outputTransform + "\" ");
		}

		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CSS_OUTPUT);
		}

		if (target != null && (LABEL_TYPE.equals(type) || OUTPUT_TYPE.equals(type))) {
			builder.append("for=\"" + target + "\" ");
		}

		builder.append(">");

		String val = "";
		Object obj = getTagValue(value);

		if (obj != null) {
			Locale locale = getRequest().getLocale();

			if (dateFormatRegex != null) {
				if (obj instanceof Date) {
					val = new SimpleDateFormat(dateFormatRegex, locale).format(obj) + sw.toString();

				} else if (obj instanceof DateTime) {
					val = ((DateTime)obj).toString(DateTimeFormat.forPattern(dateFormatRegex).withLocale(locale)) + sw.toString();
				}

			} else if (numberFormatRegex != null) {
				DecimalFormat decimalFormat = new DecimalFormat(numberFormatRegex);
				val = decimalFormat.format(obj) + sw.toString();

			} else {
				val = obj.toString() + sw.toString();
			}
		} else {
			val = sw.toString();
		}

		if (length != null && length > 0 && val != null && val.length() >= length) {
			if (ellipsize && length > 4) {
				val = val.substring(0, length - 4) + " ...";
			} else {
				val = val.substring(0, length);
			}
		}

		builder.append(val);

		if (LEGEND_TYPE.equals(type)) {
			builder.append(CLOSE_LEGEND_TAG);
		} else if (STRONG_TYPE.equals(type)) {
			builder.append(CLOSE_STRONG_TAG);
		} else if (MARK_TYPE.equals(type)) {
			builder.append(CLOSE_MARK_TAG);
		} else if (EM_TYPE.equals(type)) {
			builder.append(CLOSE_EM_TAG);
		} else if (SMALL_TYPE.equals(type)) {
			builder.append(CLOSE_SMALL_TAG);
		} else if (LABEL_TYPE.equals(type)) {
			builder.append(CLOSE_LABEL_TAG);
		} else if (OUTPUT_TYPE.equals(type)) {
			builder.append(CLOSE_OUTPUT_TAG);
		} else {
			builder.append(CLOSE_SPAN_TAG);
		}

		printOutput(builder);
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public void setEllipsize(boolean ellipsize) {
		this.ellipsize = ellipsize;
	}

	public void setUppercase(boolean uppercase) {
		this.uppercase = uppercase;
	}

	public void setLowercase(boolean lowercase) {
		this.lowercase = lowercase;
	}

	public void setCapitalize(boolean capitalize) {
		this.capitalize = capitalize;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}
