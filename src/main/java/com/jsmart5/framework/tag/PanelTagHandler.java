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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.manager.SmartTagHandler;

public final class PanelTagHandler extends SmartTagHandler {

	private static final String PANEL_COLLAPSE = "_panel_collpase";

	private static final String PANEL_CONTENT = "_panel_content";

	private static final String FIELDSET_TYPE = "fieldset";

	private static final String SECTION_TYPE = "section";

	private String align;

	private String type;

	private String label;

	private boolean closed;

	private boolean collapsible;

	public void validateTag() throws JspException {
		if (type != null && !FIELDSET_TYPE.equals(type) && !SECTION_TYPE.equals(type)) {
			throw new JspException("Invalid type value for panel tag. Valid values are "
					+ FIELDSET_TYPE + " and " + SECTION_TYPE);
		}
		if (closed && !collapsible) {
			throw new JspException("Invalid attributes for panel tag. The attribute closed can only be used if collapsible attribute is true");
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		StringWriter sw = new StringWriter();
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}

		StringBuilder builder = new StringBuilder(HtmlConstants.OPEN_DIV_TAG);

		builder.append("id=\"" + id + "\" type=\"panel\" ");

		if (align != null) {
			builder.append("align=\"" + align + "\" ");
		} else {
			builder.append("align=\"left\" ");
		}
		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			builder.append(CssConstants.CSS_PANEL);
		}

		if (ajaxCommand != null) {
			builder.append(ajaxCommand);
		}

		appendEventBuilder(builder);
		
		builder.append(">");
		
		if (collapsible || SECTION_TYPE.equals(type) || label != null) {
			builder.append(HtmlConstants.OPEN_DIV_TAG + "id=\"" + id + PANEL_COLLAPSE + "\" ");
			builder.append(CssConstants.CSS_PANEL_HEADER + ">");

			// Triangle to represent if panel is opened or closed
			if (collapsible) {
				builder.append(HtmlConstants.OPEN_DIV_TAG + (closed ? "closed=\"true\"" : ""));
				builder.append(">" + HtmlConstants.CLOSE_DIV_TAG);
			}

			if (label != null) {
				builder.append(getTagValue(label));
			}
			builder.append(HtmlConstants.CLOSE_DIV_TAG);
		}

		if (FIELDSET_TYPE.equals(type)) {
			builder.append(HtmlConstants.OPEN_FIELDSET_TAG);
		} else if (SECTION_TYPE.equals(type)) {
			builder.append(HtmlConstants.OPEN_SECTION_TAG);
		} else {
			builder.append(HtmlConstants.OPEN_DIV_TAG);
		}

		builder.append("id=\"" + id + PANEL_CONTENT + "\" ");

		if (closed) {
			builder.append("closed=\"true\" ");
		}

		builder.append(CssConstants.CSS_PANEL_CONTENT + ">");

	    builder.append(sw.toString());

	    if (FIELDSET_TYPE.equals(type)) {
			builder.append(HtmlConstants.CLOSE_FIELDSET_TAG);
		} else if (SECTION_TYPE.equals(type)) {
			builder.append(HtmlConstants.CLOSE_SECTION_TAG);
		} else {
			builder.append(HtmlConstants.CLOSE_DIV_TAG);
		}

	    builder.append(HtmlConstants.CLOSE_DIV_TAG);

	    if (collapsible) {
	    	appendScriptBuilder(new StringBuilder(JSConstants.JSMART_PANEL.format(id)));
	    }

		printOutput(builder);
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
	}

}
