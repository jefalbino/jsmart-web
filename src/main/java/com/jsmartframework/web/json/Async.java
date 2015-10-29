/*
 * JSmart Framework - Java Web Development Framework
 * Copyright (c) 2014, Jeferson Albino da Silva, All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

public final class Async {

    private String id;

    private String path;

    private Boolean credentials;

    private String start;

    private List<AsyncEvent> events = new ArrayList<AsyncEvent>(3);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getCredentials() {
        return credentials;
    }

    public void setCredentials(Boolean credentials) {
        this.credentials = credentials;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public List<AsyncEvent> getEvents() {
        return events;
    }

    public void addEvent(AsyncEvent event) {
        this.events.add(event);
    }

    public void setEvents(List<AsyncEvent> events) {
        this.events = events;
    }
}
