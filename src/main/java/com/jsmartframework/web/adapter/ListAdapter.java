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

/**
 * Adapter to load content to populate {@code list} or {@code autocomplete} components when
 * response as HTML page is being generated to client via JSP.
 * <br>
 * @param <T> - Object class to return a list of it
 */
public abstract class ListAdapter<T> {

    /**
     * Method to load content based on following criteria
     * <br>
     * @param offsetIndex - Index of last object on list when scroll is performed on component.
     *                    It is {@value 0} in case list is empty.
     * @param offset - Last object on list when scroll is performed on component. It is {@value null} in case
     *               list is empty. The object content can be specified by using {@code scrollOffset} attribute
     *               on component declaration.
     * @param size - Quantity of objects on list to be loaded. The size is specified via {@code scrollSize} attribute.
     * <br>
     * @return List of objects to append on list or autocomplete components.
     */
    public abstract List<T> load(int offsetIndex, Object offset, int size);

}
