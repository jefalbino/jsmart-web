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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.jsmart5.framework.manager.SmartTagHandler;

import static com.jsmart5.framework.tag.HtmlConstants.*;

public final class DateTagHandler extends SmartTagHandler {

	private static final String DATE_SCRIPT = "$('#%s').datepicker({%s});";

	private static final String REGIONAL_SCRIPT = "$.datepicker.setDefaults($.datepicker.regional['%s']);";

	private boolean opened;

	private String format;
	
	private String label;
	
	private boolean changeMonth;

	private boolean changeYear;
	
	private boolean showButton;

	private boolean showWeek;

	private Integer months;

	private boolean fullMonth;

	private Integer size;

	private String value;

	private boolean readOnly;

	private Integer tabIndex;

	private boolean disabled;

	@Override
	public void validateTag() throws JspException {
		if (months != null && (months <= 0 || months > 4)) {
			throw new JspException("Invalid months value for date tag. Value must be greater than 0 and less or equal to 4");
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		if (label != null && opened) {
			throw new JspException("Attribute label and opened cannot coexist for date tag");
		}

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		StringBuilder builder = new StringBuilder();
		StringBuilder options = new StringBuilder();
		Locale locale = getRequest().getLocale();

		String name = getTagName(J_FRMT, value) + (readOnly ? EL_PARAM_READ_ONLY : "");

		String dateFormat = format != null ? format.replace("yy", "yyyy").replace("MM", "MMMM") : "dd/MM/yyyy";
		
		// Hidden input to carry format content
		builder.append(INPUT_TAG + "id=\"" + id + "_date_format\" name=\"" + name + "\" value=\"" + dateFormat + "\" type=\"hidden\" />");

		if (label != null) {
			builder.append(OPEN_DIV_TAG + CssConstants.CSS_INPUT_GROUP + ">");
			builder.append(OPEN_SPAN_TAG + CssConstants.CSS_INPUT_LABEL + ">");

			String labelVal = (String) getTagValue(label);
			if (labelVal != null) {
				builder.append(labelVal);
			}
			builder.append(CLOSE_SPAN_TAG);
		}

		// Input to carry date content
		builder.append(INPUT_TAG + "name=\"" + name.replace(J_FRMT, J_DATE) + "\" ");

		Object val = getTagValue(value);
		if (val != null) {
			if (val instanceof Date) {
				builder.append("value=\"" + new SimpleDateFormat(dateFormat, locale).format(val) + "\" ");

			} else if (val instanceof DateTime) {
				builder.append("value=\"" + ((DateTime) val).toString(DateTimeFormat.forPattern(dateFormat).withLocale(locale)) + "\" ");
			}
		}

		builder.append(ON_BLUR + JSConstants.JSMART_DATE.format("$(this)") + "\" ");
		builder.append(ON_FOCUS + JSConstants.JSMART_BACKUP_DATE.format("$(this)") + " return false;\" ");

		appendFormValidator(builder);

		appendRestBuilder(builder);

		if (opened) {
			builder.append("id=\"" + id + "_date_hidden\" type=\"hidden\" />");
			builder.append(OPEN_DIV_TAG);
		}

		builder.append("id=\"" + id + "\" ");

		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			if (!opened) {
				builder.append(CssConstants.CSS_INPUT);
			}
		}
		if (tabIndex != null) {
			builder.append("tabindex=\"" + tabIndex + "\" ");
		}
		if (size != null) {
			builder.append("size=\"" + size + "\" ");
		}
		if (readOnly) {
			builder.append("readonly=\"true\" ");
		}
		if (disabled || isEditRowTagEnabled()) {
			builder.append("disabled=\"disabled\" ");
			options.append("disabled: " + disabled + ",");
		}

		if (showButton) {
			options.append("showOn: 'button',");
		}
		if (format != null) {
			options.append("dateFormat: '" + format + "',");
		}
		if (changeMonth) {
			options.append("changeMonth: " + changeMonth + ",");
		}
		if (changeYear) {
			options.append("changeYear: " + changeYear + ",");
		}
		if (months != null) {
			options.append("numberOfMonths: " + months + ",");
		}
		if (fullMonth) {
			options.append("showOtherMonths: true, selectOtherMonths: true,");
		}
		if (showWeek) {
			options.append("showWeek: " + showWeek + ",");
		}

		StringBuilder dateScript = new StringBuilder(String.format(REGIONAL_SCRIPT, locale.getLanguage()) + String.format(DATE_SCRIPT, id, options));

		appendScriptBuilder(dateScript);

		builder.append("date=\"" + dateScript + "\" ");

		appendEventBuilder(builder);

		if (opened) {
			printOutput(builder.append(">" + CLOSE_DIV_TAG));
		} else {			
			builder.append("/>");

			if (label != null) {
				builder.append(CLOSE_DIV_TAG);
			}
			printOutput(builder);
		}
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setChangeMonth(boolean changeMonth) {
		this.changeMonth = changeMonth;
	}

	public void setChangeYear(boolean changeYear) {
		this.changeYear = changeYear;
	}

	public void setMonths(Integer months) {
		this.months = months;
	}

	public void setFullMonth(boolean fullMonth) {
		this.fullMonth = fullMonth;
	}

	public void setShowWeek(boolean showWeek) {
		this.showWeek = showWeek;
	}

	public void setShowButton(boolean showButton) {
		this.showButton = showButton;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
