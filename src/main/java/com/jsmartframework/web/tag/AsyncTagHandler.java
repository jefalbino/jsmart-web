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

package com.jsmartframework.web.tag;

import com.jsmartframework.web.exception.InvalidAttributeException;
import com.jsmartframework.web.json.Async;
import com.jsmartframework.web.json.AsyncEvent;
import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.html.Tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jsmartframework.web.tag.js.JsConstants.JSMART_ASYNCEVENT;

public final class AsyncTagHandler extends TagHandler {

	private String path;

    private Boolean withCredentials;

    private String onStart;

    private List<AsyncEventTagHandler> events;

    public AsyncTagHandler() {
        events = new ArrayList<AsyncEventTagHandler>(3);
    }

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

        if (events.isEmpty()) {
            throw InvalidAttributeException.fromConflict("async", "asyncevent",
                    "At least one inner tag [asyncevent] must be specified");
        }

        appendDocScript(getAsyncFunction());
        return null;
	}

	private StringBuilder getAsyncFunction() {
		Async jsonAsync = new Async();
        jsonAsync.setId(id);
		jsonAsync.setPath(path);
        jsonAsync.setStart(onStart);
        jsonAsync.setCredentials(withCredentials);

        for (AsyncEventTagHandler event : events) {
            AsyncEvent asyncEvent = new AsyncEvent();
            asyncEvent.setEvent(event.getEvent());
            asyncEvent.setCapture(event.getCapture());
            asyncEvent.setExecute(event.getExecute());
            jsonAsync.addEvent(asyncEvent);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(JSMART_ASYNCEVENT.format(getJsonValue(jsonAsync)));
        return builder;
	}

	void addEvent(AsyncEventTagHandler event) {
        this.events.add(event);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setWithCredentials(Boolean withCredentials) {
        this.withCredentials = withCredentials;
    }

    public void setOnStart(String onStart) {
        this.onStart = onStart;
    }
}