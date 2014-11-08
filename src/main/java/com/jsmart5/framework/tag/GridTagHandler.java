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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.SmartTagHandler;

public class GridTagHandler extends SmartTagHandler {

	private static final float HUNDRED_PERCENTAGE = 100f;

	private static final String ID_WRAPPER = "_wrapper";

	private String align;

	private Integer columns;

	private String columnStyle;

	// Division follows the pattern of 30%,70%,..
	private String division;

	private boolean noLines;

	private List<SmartTagHandler> rows;

	private boolean foundRows;

	private boolean foundGeneralTags;

	private int totalColumns;

	public GridTagHandler() {
		rows = new ArrayList<SmartTagHandler>();
	}

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
		if (columns != null && columns <= 0) {
			throw new JspException("Attribute columns must be greater than zero for grid tag");
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {

		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		StringBuilder builder = new StringBuilder();

		if (foundRows && foundGeneralTags) {
			throw new JspException("Grid tag only support internal row tags or general tags. " +
					"Do not mix row tags with general tags on the same hierarchy.");
		}

		if (foundRows) {
			executeTableGrid(builder);
		} else {
			executeDivGrid(builder);
		}

 	 	printOutput(builder);
	}

	private void executeTableGrid(StringBuilder builder) throws JspException, IOException {
		builder.append(OPEN_DIV_TAG);
		
		if (id != null) {
			builder.append("id=\"" + id + ID_WRAPPER + "\" ");
		}
	 	if (align != null) {
	 		builder.append("align=\"" + align + "\" ");
		}
		builder.append(CLOSE_TAG);

		builder.append(OPEN_TABLE_TAG);

		if (id != null) {
			builder.append("id=\"" + id + "\" ");
		}
		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CSS_GRID_TABLE);
		}
		builder.append("cellspacing=\"0\" ");
		
		appendEvent(builder);
		builder.append(CLOSE_TAG);

 		for (SmartTagHandler row : rows) {
 			StringWriter sw = new StringWriter();
 			RowTagHandler rowTag = (RowTagHandler) row;

 			rowTag.setOutputWriter(sw);
 			rowTag.setAjaxCommand(ajaxCommand);
 			rowTag.setBorder(!noLines);
 			rowTag.setTotalColumns(totalColumns);
 			setEvents(rowTag);

 			rowTag.executeTag();
 			builder.append(sw.toString());
 		}
 
 	 	builder.append(CLOSE_TABLE_TAG);
 	 	builder.append(CLOSE_DIV_TAG);
	}

	private void executeDivGrid(StringBuilder builder) throws JspException, IOException {
		
		if (columns == null) {
			columns = 1;
		}

		String[] parts = null;
		if (division != null) {
			parts = division.split(",");

			if (parts.length != columns) {
				throw new JspException("Attribute division must contains the percentage of each column for grid tag");
			}
		}

		BigDecimal percentage = new BigDecimal(HUNDRED_PERCENTAGE / (float) columns).setScale(2, BigDecimal.ROUND_HALF_UP);

		builder.append(OPEN_DIV_TAG);

		if (id != null) {
			builder.append("id=\"" + id + "\" ");
		}
		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			appendClass(builder, CSS_GRID_DIV);
		}

		appendEvent(builder);
		builder.append(CLOSE_TAG);

		// Start first div row
		builder.append(OPEN_DIV_TAG);
		appendClass(builder, CSS_GRID_DIV_ROW);

		if (ajaxCommand != null) {
			builder.append(ajaxCommand);
		}
		builder.append(CLOSE_TAG);

		for (int i = 0; i < rows.size(); i++) {
			
			if (i != 0 && i % columns == 0) {
				// Close previous div row
				builder.append(CLOSE_DIV_TAG);
				
				// Open new div row
				builder.append(OPEN_DIV_TAG);
				appendClass(builder, CSS_GRID_DIV_ROW);
				
				if (ajaxCommand != null) {
					builder.append(ajaxCommand);
				}
				builder.append(CLOSE_TAG);
			}

			// Open tag related to div column
			builder.append(OPEN_DIV_TAG);
			if (align != null) {
		 		builder.append("align=\"" + align + "\" ");
			}

			if (parts != null) {
				builder.append("style=\"" + (columnStyle != null ? columnStyle : "") + "; width: " + parts[i % columns] + ";\" ");
			} else {
				builder.append("style=\"" + (columnStyle != null ? columnStyle : "") + ";width: " + percentage + "%;\" ");
			}

			appendClass(builder, CSS_GRID_DIV_COLUMN);
			builder.append(CLOSE_TAG);

			SmartTagHandler row = rows.get(i);
 			StringWriter sw = new StringWriter();
 			row.setOutputWriter(sw);
 			setEvents(row);
 			row.executeTag();
 			builder.append(sw.toString());

 			// Close tag related to div column
 			builder.append(CLOSE_DIV_TAG);
 		}
		// Close last div row
		builder.append(CLOSE_DIV_TAG);

		builder.append(CLOSE_DIV_TAG);
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setColumns(Integer columns) {
		this.columns = columns;
	}

	public void setColumnStyle(String columnStyle) {
		this.columnStyle = columnStyle;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public void setNoLines(boolean noLines) {
		this.noLines = noLines;
	}

	/*package*/ void addRow(RowTagHandler row) {
		this.foundRows = true;
		this.rows.add(row);
	}

	/*package*/ void addTag(SmartTagHandler tag) {
		this.foundGeneralTags = true;
		this.rows.add(tag);
	}

	/*package*/ void setTotalColumns(int totalColumns) {
		if (totalColumns > this.totalColumns) {
			this.totalColumns = totalColumns;
		}
	}

}
