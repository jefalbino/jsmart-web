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

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.CssConstants.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.SmartTagHandler;

public final class RowTagHandler extends SmartTagHandler {

	// Used as boolean for rows inside grid tag
	private String header;

	// Used as boolean for rows inside grid tag
	private String footer;

	private String value;

	private String type;

	private String badge;

	// Internally used, setup via GridTagHandler
	private boolean border;

	// Used for rows inside grid tag
	private List<ColumnTagHandler> columns;

	private int totalColumns;

	public RowTagHandler() {
		columns = new ArrayList<ColumnTagHandler>();
	}

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();

		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		if (parent instanceof ListTagHandler) {
			theme = ((ListTagHandler) parent).getTheme();
			((ListTagHandler) parent).addRow(this);
			return false;

		} else if (parent instanceof GridTagHandler) {
			GridTagHandler gridParent = (GridTagHandler) parent; 
			gridParent.addRow(this);
			gridParent.setTotalColumns(this.columns.size());
			theme = gridParent.getTheme();
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		JspTag parent = getParent();

		if (parent instanceof ListTagHandler) {
			if (value == null) {
				throw new JspException("Attribute value is required for row tag inside list tag");
			}
		} else if (parent instanceof GridTagHandler) {
			if (value != null) {
				throw new JspException("Attribute value cannot be used on row tag inside grid tag");
			}
			if (badge != null) {
				throw new JspException("Attribute badge cannot be used on row tag inside grid tag");
			}
			if (type != null) {
				throw new JspException("Attribute type cannot be used on row tag inside grid tag");
			}
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {
		JspTag parent = getParent();

		if (parent instanceof ListTagHandler) {
			executeListTag();

		} else if (parent instanceof GridTagHandler) {
			executeGridTag();
		}
	}

	private void executeListTag() throws JspException, IOException {
		StringBuilder builder = new StringBuilder();

		if (type != null && type.equals(ListTagHandler.DEFINITION)) {
			builder.append(OPEN_DEFINITION_TITLE_TAG + (header != null ? getTagValue(header) : "") + CLOSE_DEFINITION_TITLE_TAG + OPEN_DEFINITION_DATA_TAG);
		} else {
			builder.append(OPEN_LIST_ITEM_TAG);
		}

		if (style != null) {
			builder.append("style=\"" + (ajaxCommand != null ? "cursor: pointer; " : "") + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CSS_LIST_ROW);
		}
		if (ajaxCommand != null) {
			builder.append((style == null ? "style=\"cursor: pointer;\" " : "") + ajaxCommand + "select=\"true\" ");
		}

		appendEvent(builder);

		builder.append(">");

		Object object = getTagValue(value);
		if (object != null) {
			builder.append(object);
		}

		if (badge != null) {
			object = getTagValue(badge);
			builder.append(OPEN_SPAN_TAG);
			appendClass(builder, CSS_LIST_ROW_BADGE);
			builder.append(">");
			builder.append(object != null ? object : "");
			builder.append(CLOSE_SPAN_TAG);
		}

		if (type != null && type.equals(ListTagHandler.DEFINITION)) {
			builder.append(CLOSE_DEFINITION_DATA_TAG);
		} else {
			builder.append(CLOSE_LIST_ITEM_TAG);
		}

		printOutput(builder);
	}

	private void executeGridTag() throws JspException, IOException {

		boolean rowHeader = Boolean.parseBoolean(header);
		boolean rowFooter = Boolean.parseBoolean(footer);

		StringBuilder builder = new StringBuilder();

		if (rowHeader) {
			builder.append(OPEN_TABLE_HEAD_TAG + ">");
		} else if (rowFooter) {
			builder.append(OPEN_TABLE_FOOT_TAG + ">");
		}

		builder.append(OPEN_TABLE_ROW_TAG);
		appendClass(builder, rowHeader ? CSS_GRID_ROW_HEADER : rowFooter ? CSS_GRID_ROW_FOOTER : CSS_GRID_ROW);

		if (id != null) {
			builder.append("id=\"" + id + "\" ");
		}
		if (style != null) {
			builder.append("style=\"" + (ajaxCommand != null ? "cursor: pointer; " : "") + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		}
		if (ajaxCommand != null) {
			builder.append((style == null ? "style=\"cursor: pointer;\" " : "") + ajaxCommand);
		}

		appendEvent(builder);
		builder.append(">");

		for (ColumnTagHandler column : columns) {
			if (rowHeader) {
				builder.append(OPEN_TABLE_HEAD_COLUMN_TAG);
			} else {
				builder.append(OPEN_TABLE_COLUMN_TAG);
			}

			if (column.id != null) {
				builder.append("id=\"" + column.id + "\" ");
			}
			if (column.style != null) {
				builder.append("style=\"" + column.style + "\" ");
			}
			if (column.styleClass != null) {
				builder.append("class=\"" + column.styleClass + "\" ");
			} else {
				appendClass(builder,  rowHeader ? CSS_GRID_HEADER_COLUMN : rowFooter ? 
						CSS_GRID_FOOTER_COLUMN : border ? CSS_GRID_BORDER_COLUMN : CSS_GRID_COLUMN);
			}

			if (columns.size() < totalColumns) {
				builder.append("shrunken=\"true\" ");
			}

			if (column.getColspan() != null) {
				builder.append("colspan=\"" + column.getColspan() + "\" ");
			}
			if (column.getRowspan() != null) {
				builder.append("rowspan=\"" + column.getRowspan() + "\" ");
			}
			builder.append(">");

			StringWriter sw = new StringWriter();
			column.setOutputWriter(sw);
			column.executeTag();

			builder.append(sw.toString());
			
			if (rowHeader) {
				builder.append(CLOSE_TABLE_HEAD_COLUMN_TAG);
			} else {
				builder.append(CLOSE_TABLE_COLUMN_TAG);
			}
		}
		
		builder.append(CLOSE_TABLE_ROW_TAG);
		
		if (rowHeader) {
			builder.append(CLOSE_TABLE_HEAD_TAG);
		} else if (rowFooter) {
			builder.append(CLOSE_TABLE_FOOT_TAG);
		}

		printOutput(builder);
	}

	void setType(String type) {
		this.type = type;
	}

	void setTotalColumns(int totalColumns) {
		this.totalColumns = totalColumns;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setBadge(String badge) {
		this.badge = badge;
	}

	void addColumn(ColumnTagHandler column) {
		this.columns.add(column);
	}

	void setBorder(boolean border) {
		this.border = border;
	}

}
