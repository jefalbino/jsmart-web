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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.SmartTagHandler;

public final class ColumnTagHandler extends SmartTagHandler {

	// User for columns inside row tag for grid
	private Integer colspan;

	private Integer rowspan;

	private String header;

	private boolean selectable = true;

	private String sortBy;

	private String filterBy;

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();

		if (parent instanceof TableTagHandler) {
			((TableTagHandler) parent).addColumn(this);
			return false;

		} else if (parent instanceof RowTagHandler) {
			((RowTagHandler) parent).addColumn(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		JspTag parent = getParent();
		if (parent instanceof RowTagHandler) {

			if (header != null) {
				throw new JspException("Attribute header cannot be used on column tag inside grid tag");
			}
			if (sortBy != null) {
				throw new JspException("Attribute sortBy cannot be used on column tag inside grid tag");
			}
			if (filterBy != null) {
				throw new JspException("Attribute filterBy cannot be used on column tag inside grid tag");
			}
			if (colspan != null && colspan < 0) {
				throw new JspException("Attribute colspan must be greater than zero for column tag inside grid tag");
			}
			if (rowspan != null && rowspan < 0) {
				throw new JspException("Attribute rowspan must be greater than zero for column tag inside grid tag");
			}
		}
	}

	@Override
	public void executeTag() throws JspException, IOException {
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(outputWriter);
		}
	}

	/*package*/ String getId() {
		return id;
	}

	/*package*/ Integer getColspan() {
		return colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	/*package*/ Integer getRowspan() {
		return rowspan;
	}

	public void setRowspan(Integer rowspan) {
		this.rowspan = rowspan;
	}

	/*package*/ String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	/*package*/ boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	/*package*/ String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	/*package*/ String getFilterBy() {
		return filterBy;
	}

	public void setFilterBy(String filterBy) {
		this.filterBy = filterBy;
	}

}
