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

package com.jsmartframework.web.tag.util;

import com.jsmartframework.web.json.Ajax;
import com.jsmartframework.web.json.Bind;

import java.util.HashMap;
import java.util.Map;

public class RefAction {

    private Map<String, EventAction> refs;

    public Map<String, EventAction> getRefs() {
        return refs;
    }

    public void setRefs(Map<String, EventAction> refs) {
        this.refs = refs;
    }

    public void addRef(String refId, String event, Ajax ajax) {
        if (refs == null) {
            refs = new HashMap<String, EventAction>();
        }

        EventAction eventAction = refs.get(refId);
        if (eventAction == null) {
            eventAction = new EventAction();
            refs.put(refId, eventAction);
        }
        eventAction.addAjax(event, ajax);
    }

    public void addRef(String refId, String event, Bind bind) {
        if (refs == null) {
            refs = new HashMap<String, EventAction>();
        }

        EventAction eventAction = refs.get(refId);
        if (eventAction == null) {
            eventAction = new EventAction();
            refs.put(refId, eventAction);
        }
        eventAction.addBind(event, bind);
    }

}
