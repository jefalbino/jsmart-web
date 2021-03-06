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

package com.jsmartframework.web.json;

import java.util.Collections;
import java.util.Map;

public class Scroll {

    private Integer index;

    private Integer size;

    private String sort;

    private Integer order;

    private Object offset;

    private Map<String, String> filters;

    public Integer getIndex() {
        return index != null ? index : 0;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getOrder() {
        return order == null ? 0 : order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Object getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public Map<String, String> getFilters() {
        return filters != null ? filters : Collections.<String, String>emptyMap();
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }

}
