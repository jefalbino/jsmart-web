/*
 * JSmart Framework - Java Web Development Framework
 * Copyright (c) 2015, Jeferson Albino da Silva, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
*/

package com.jsmartframework.web.adapter;

import java.util.List;
import java.util.Map;

/**
 * Adapter to load content to populate {@code table} component when
 * response as HTML page is being generated to client via JSP.
 * <br>
 * @param <T> - Object class to return a list of it
 */
public abstract class TableAdapter<T> {

    /**
     * Method to load content based on following criteria
     * <br>
     * @param offsetIndex - Index of last object on table when scroll is performed on component.
     *                    It is 0 in case table is empty.
     * @param offset - Last object on table when scroll is performed on component. It is null in case
     *               table is empty. The object content can be specified by using {@code scrollOffset} attribute
     *               on component declaration.
     * @param size - Quantity of objects on list to be loaded. The size is specified via {@code scrollSize} attribute.
     * @param sort - Sort object field name to sort the loaded list with.
     * @param order - Sort order of the loaded list. Possible values are -1, 0 or 1.
     * @param filters - Map of table filters to load content. The key contains the object field name to filter with
     *                and the value is the value to be filtered for the specified object field name.
     * <br>
     * @return List of objects to append on table component.
     */
    public abstract List<T> load(int offsetIndex, Object offset, int size, String sort,
                                 int order, Map<String, String> filters);

}
