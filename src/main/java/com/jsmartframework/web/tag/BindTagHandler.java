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

import static com.jsmartframework.web.tag.js.JsConstants.JSMART_BIND;

import com.jsmartframework.web.exception.InvalidAttributeException;
import com.jsmartframework.web.json.Bind;
import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Event;
import com.jsmartframework.web.tag.util.RefAction;

import java.io.IOException;
import java.util.Stack;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

public final class BindTagHandler extends TagHandler {

    private String event;

    private String execute;

    private Integer timeout;

    @Override
    public void validateTag() throws JspException {
        if (event != null && !Event.validate(event)) {
            throw InvalidAttributeException.fromPossibleValues("ajax", "event", Event.getValues());
        }
        if (timeout != null && timeout < 0) {
            throw InvalidAttributeException.fromConstraint("ajax", "timeout", "greater or equal to 0");
        }
    }

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();
        if (parent instanceof TagHandler) {

            ((TagHandler) parent).addBindTag(this);
        }
        return false;
    }

    @Override
    public Tag executeTag() throws JspException, IOException {
        // DO NOTHING
        return null;
    }

    private Bind getJsonBind(String id) {
        Bind jsonBind = new Bind();
        jsonBind.setId(id);
        jsonBind.setTimeout((Integer) getTagValue(timeout));

        String exec = (String) getTagValue(execute);
        if (exec != null && !exec.trim().endsWith(";")) {
            exec += ";";
        }
        jsonBind.setExecute(exec);
        return jsonBind;
    }

    @SuppressWarnings("unchecked")
    public StringBuilder getBindFunction(String id) {
        Bind jsonBind = getJsonBind(id);

        // It means that the ajax is inside some iterator tag, so the
        // bind actions will be set by iterator tag and the event bind
        // will use the id as tag attribute
        Stack<RefAction> actionStack = (Stack<RefAction>) getMappedValue(DELEGATE_TAG_PARENT);
        if (actionStack != null) {
            actionStack.peek().addRef(id, event, jsonBind);

        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(JSMART_BIND.format(getJsonValue(jsonBind)));
            return getBindFunction(id, event, builder);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public StringBuilder getDelegateFunction(String id, String child) {
        Bind jsonBind = getJsonBind(id);

        // It means that the ajax is inside some iterator tag, so the
        // bind actions will be set by iterator tag and the event bind
        // will use the id as tag attribute
        Stack<RefAction> actionStack = (Stack<RefAction>) getMappedValue(DELEGATE_TAG_PARENT);
        if (actionStack != null) {
            actionStack.peek().addRef(id, event, jsonBind);

        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(JSMART_BIND.format(getJsonValue(jsonBind)));
            return getDelegateFunction(id, child, event, builder);
        }
        return null;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setExecute(String execute) {
        this.execute = execute;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

}
