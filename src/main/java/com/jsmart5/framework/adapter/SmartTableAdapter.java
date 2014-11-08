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

package com.jsmart5.framework.adapter;

import java.util.List;

import com.jsmart5.framework.tag.TableAdapterHandler;

/**
 * This abstract class must be extended to allow table component mapped on the returned page 
 * to be paginated or scrolled dynamically based on action triggered by table component.  
 *
 * @param <T> the type of the element being loaded inside the {@link List} of elements.
 */
public abstract class SmartTableAdapter<T> extends TableAdapterHandler<T> {

	/**
	 * This enumerator represents the sort order triggered per column by table component
	 * to specify the order in which the elements must be presented on table.
	 * <br>
	 * Values are ASC (ascendant) or DESC (descendant). Default value is ASC.
	 */
	public static enum SortOrder {

		ASC, DESC;

		public static SortOrder valueBy(String string) {
			if (string != null) {
				for (SortOrder sortOrder : values()) {
					if (sortOrder.toString().equalsIgnoreCase(string)) {
						return sortOrder;
					}
				}
			}
			return null;
		}
	}
}
