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

import java.util.List;
import java.util.Map;

import com.jsmart5.framework.adapter.SmartTableAdapter.SortOrder;

public abstract class TableAdapterHandler<T> {

	private long first;

	private long size;

	private String sortBy;

	private SortOrder sortOrder = SortOrder.ASC;

	private Map<String, String> filterBy;

	private List<T> loaded;

	private boolean reload;

	/**
	 * Method must be implemented to load values on a {@link List} of elements of <T> type based on parameters
	 * passed to this method. The parameters are set based on the action triggered by table component.
	 * 
	 * @param pageFirst 
	 * 			zero based value representing the index of the first element of the table page or scroll block. 
	 * @param pageSize 
	 * 			value for the total quantity of elements presented on table page or scroll block.
	 * @param sortBy 
	 * 			{@link String} mapped on table component to specify the sort value to load {@link List}	of 
	 * 			elements of type <T>. Default is <code>null</code>.
	 * @param sortOrder 
	 * 			{@link SortOrder} object mapped on table component to specify the order of sort to load {@link List}
	 * 			of element of type <T>. Default value is SortOrder.ASC.
	 * @param filterBy 
	 * 			{@link Map} to specify the filter column names and its respective values inputed on table component
	 * 			to filter the load of elements of type <T> 
	 * @return {@link List} of elements of type <T>
	 */
	public abstract List<T> loadData(long pageFirst, int pageSize, String sortBy, SortOrder sortOrder, Map<String, String> filterBy);

	/**
	 * Method must be implemented to specify the total size of the elements of type <T> to be loaded. 
	 * <br>
	 * It is used to calculated the number of pages needed on table.
	 * 
	 * @param sortBy 
	 * 			{@link String} mapped on table component to specify the sort value to load {@link List}	of 
	 * 			elements of type <T>. Default is <code>null</code>.
	 * @param filterBy 
	 * 			{@link Map} to specify the filter column names and its respective values inputed on table component
	 * 			to filter the load of elements of type <T> 
	 * @return the total size of the {@link List} of elements of type <T>.
	 */
	public abstract long loadSize(String sortBy, Map<String, String> filterBy);

	/**
	 * Method called after loadData method is executed.  
	 * 
	 * @param loaded {@link List} of elements of type <T> loaded.
	 */
	public void postLoad(List<T> loaded) {
		// AVAILABLE TO BE OVERRIDEN
	}

	/**
	 * Method to indicate that the values of {@link List} of elements of type <T> must be reloaded. 
	 * 
	 * @param resetIndex value to indicate that the current index of table page selected must be reset.
	 */
	public void reloadData(boolean resetIndex) {
		if (resetIndex) {
			this.first = 0;
		}
		this.reload = true;
	}

	boolean isReload() {
		return this.reload;
	}

	void resetReload() {
		this.reload = false;
	}

	long getFirst() {
		return this.first;
	}

	void setFirst(long first) {
		this.first = first;
	}

	/**
	 * Return the size of the {@link List} of elements of <T> type current or
	 * previous loaded.
	 * 
	 * @return the size of the {@link List}.
	 */
	public long getSize() {
		return this.size;
	}

	void setSize(long size) {
		this.size = size;
	}

	/**
	 * Return current or previous loaded value, as {@link List} of elements of <T> type.
	 * 
	 * @return {@link List} of elements of <T> type.
	 */
	public List<T> getLoaded() {
		return this.loaded;
	}

	void setLoaded(List<T> loaded) {
		this.loaded = loaded;
	}

	void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	void setFilterBy(Map<String, String> filterBy) {
		this.filterBy = filterBy;
	}

	boolean shallReload(String sortBy, SortOrder sortOrder, Map<String, String> filterBy) {
		boolean shallReload = getSize() == 0l || isReload();
		shallReload |= sortBy != null ? !sortBy.equals(this.sortBy) : this.sortBy != null;
		shallReload |= sortOrder != this.sortOrder;
		shallReload |= filterBy != null ? compareFilterBy(filterBy) : this.filterBy != null;
		return shallReload;
	}

	boolean shallReload(long first, String sortBy, SortOrder sortOrder, Map<String, String> filterBy) {
		boolean shallReload = getLoaded() == null || first != getFirst() || isReload();
		shallReload |= sortBy != null ? !sortBy.equals(this.sortBy) : this.sortBy != null;
		shallReload |= sortOrder != this.sortOrder;
		shallReload |= filterBy != null ? compareFilterBy(filterBy) : this.filterBy != null;
		return shallReload;
	}

	private boolean compareFilterBy(Map<String, String> filterBy) {
		if (this.filterBy != null) {
			if (this.filterBy.size() != filterBy.size()) {
				return true;
			} else {
				for (String key : this.filterBy.keySet()) {
					String value = filterBy.get(key);
					if (value == null) {
						return true;
					} else {
						if (!value.equals(this.filterBy.get(key))) {
							return true;
						}
					}
				}
				return false;
			}
		}
		return true;
	}

}
