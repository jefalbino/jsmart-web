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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Set;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.ContentType;
import com.jsmart5.framework.tag.type.Method;
import com.jsmart5.framework.tag.type.Position;
import com.jsmart5.framework.tag.type.Size;

public final class RestTagHandler extends TagHandler {

	private String endpoint;

    private String method;

	private String contentType;

	private Boolean cors;

	private String jsonpCallback;

    private String bodyRoot;

    private String position;

    private String size;

    private List<Tag> beforeRest;

    public RestTagHandler() {
        beforeRest = new ArrayList<Tag>();
    }

	@Override
	public void validateTag() throws JspException {
        if (method != null && !Method.validate(method)) {
            throw InvalidAttributeException.fromPossibleValues("rest", "method", Method.getValues());
        }
		if (contentType != null && !ContentType.validate(contentType)) {
            throw InvalidAttributeException.fromPossibleValues("rest", "contentType", ContentType.getValues());
		}
        if (position != null && !Position.validate(position)) {
            throw InvalidAttributeException.fromPossibleValues("rest", "position", Position.getValues());
        }
        if (size != null && !Size.validateSmallLarge(size)) {
            throw InvalidAttributeException.fromPossibleValues("rest", "size", Size.getSmallLargeValues());
        }
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

        StringWriter sw = new StringWriter();
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(sw);
        }

        setRandomId("rest");

        Div rest = new Div();
        rest.addAttribute("id", id)
            .addAttribute("role", "restrequest")
            .addAttribute("endpoint", endpoint)
            .addAttribute("content-type", contentType != null ? contentType.toLowerCase() : ContentType.JSON.toString().toLowerCase())
            .addAttribute("method", method)
            .addAttribute("cors", cors)
            .addAttribute("callback", jsonpCallback)
            .addAttribute("body-root", bodyRoot);

        if (Position.HORIZONTAL.equalsIgnoreCase(position)) {
            rest.addAttribute("class", Bootstrap.FORM_HORIZONTAL);
        } else if (Position.INLINE.equalsIgnoreCase(position)) {
            rest.addAttribute("class", Bootstrap.FORM_INLINE);
        }

        rest.addAttribute("class", styleClass)
            .addAttribute("style", style)
            .addText(sw.toString());

        if (!beforeRest.isEmpty()) {
            Set set = new Set();
            for (Tag tag : beforeRest) {
                set.addTag(tag);
            }
            set.addTag(rest);
            return set;
        }
		return rest;
	}

    void addBeforeRestTag(Tag tag) {
        this.beforeRest.add(tag);
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setCors(Boolean cors) {
        this.cors = cors;
    }

    public void setJsonpCallback(String jsonpCallback) {
        this.jsonpCallback = jsonpCallback;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setBodyRoot(String bodyRoot) {
        this.bodyRoot = bodyRoot;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}