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

/**
 * This abstract class must be extended to allow autocomplete component mapped on the 
 * returned page to dynamically search for results based on action triggered by the component.  
 *
 * @param <T> the type of the element being loaded inside the {@link List} of elements.
 */
public abstract class SmartSearchAdapter<T> {

	/**
	 * Method must be implemented to search values for autocomplete component.
	 * 
	 * @param value
	 * 			value inputed for searching criteria
	 * @param maxResults
	 * 			maximum search results to be returned
	 * 
	 * @return {@link List} of elements of type <T>
	 */
	public abstract List<T> search(String value, int maxResults);

}
