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

package com.jsmartframework.web.tag;

import com.jsmartframework.web.exception.InvalidAttributeException;
import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Side;
import com.jsmartframework.web.tag.type.TipEvent;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class PopOverTagHandler extends TagHandler {

    private String title;

    private String content;

    private String side = Side.RIGHT.name().toLowerCase();

    private String event;

    private String template;

    private String selector;

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();
        if (parent instanceof TagHandler) {
            ((TagHandler) parent).setPopOverTag(this);
        }
        return true;
    }

    @Override
    public void validateTag() throws JspException {
        if (side != null && !Side.validate(side)) {
            throw InvalidAttributeException.fromPossibleValues("popover", "side", Side.getValues());
        }
        if (event != null && !TipEvent.validate(event)) {
            throw InvalidAttributeException.fromPossibleValues("popover", "event", TipEvent.getValues());
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {
        StringWriter sw = new StringWriter();
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(sw);
        }

        // Place the tag content as template
        template = sw.toString();

        if (StringUtils.isNotBlank(template)) {
            setRandomId("popover");

            // The HTML template must overwrite the content
            content = null;
        }
        return null;
    }

    public void printTemplate(TagHandler tag) throws IOException {
        if (StringUtils.isNotBlank(template)) {
            Div div = new Div();
            div.addAttribute("id", id)
                .addAttribute("style", "display: none;")
                .addText(template);

            printOutput(tag, div.getHtml());
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTemplate() {
        return template;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }
}
