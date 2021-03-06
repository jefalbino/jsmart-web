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
import com.jsmartframework.web.tag.css.Bootstrap;
import com.jsmartframework.web.tag.html.A;
import com.jsmartframework.web.tag.html.Li;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Look;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class RowTagHandler extends TagHandler {

    private String look;

    private boolean selectable;

    private Integer selectIndex;

    private Integer scrollIndex;

    private HeaderTagHandler header;

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();
        if (parent instanceof ListTagHandler) {
            ((ListTagHandler) parent).addRow(this);
        } else if (parent instanceof AutoCompleteTagHandler) {
            ((AutoCompleteTagHandler) parent).addRow(this);
        }
        return false;
    }

    @Override
    public void validateTag() throws JspException {
        if (look != null && !Look.validateBasic(look) && !isEL(look)) {
            throw InvalidAttributeException.fromPossibleValues("row", "look", Look.getBasicValues());
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {
        StringWriter sw = new StringWriter();

        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(sw);
        }

        Tag tag;
        if (selectable) {
            tag = new A();
            tag.addAttribute("style", "cursor: pointer;");
        } else {
            tag = new Li();
        }

        setRandomId("row");

        tag.addAttribute("style", getTagValue(style))
            .addAttribute("class", Bootstrap.LIST_GROUP_ITEM)
            .addAttribute("class", isDisabled() ? Bootstrap.DISABLED : null)
            .addAttribute("list-index", selectIndex)
            .addAttribute("scroll-index", scrollIndex);

        appendRefId(tag, id);

        String lookVal = (String) getTagValue(look);

        if (Look.SUCCESS.equalsIgnoreCase(lookVal)) {
            tag.addAttribute("class", Bootstrap.LIST_GROUP_ITEM_SUCCESS);
        } else if (Look.INFO.equalsIgnoreCase(lookVal)) {
            tag.addAttribute("class", Bootstrap.LIST_GROUP_ITEM_INFO);
        } else if (Look.WARNING.equalsIgnoreCase(lookVal)) {
            tag.addAttribute("class", Bootstrap.LIST_GROUP_ITEM_WARNING);
        } else if (Look.DANGER.equalsIgnoreCase(lookVal)) {
            tag.addAttribute("class", Bootstrap.LIST_GROUP_ITEM_DANGER);
        }

        // At last place the style class
        tag.addAttribute("class", getTagValue(styleClass));

        appendEvent(tag);

        if (header != null) {
            tag.addTag(header.executeTag());
        }
        tag.addText(executeExpressions(sw.toString()));

        appendAjax(id);
        appendBind(id);

        return tag;
    }

    void setHeader(HeaderTagHandler header) {
        this.header = header;
    }

    void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    void setScrollIndex(Integer scrollIndex) {
        this.scrollIndex = scrollIndex;
    }

    void setSelectIndex(Integer selectIndex) {
        this.selectIndex = selectIndex;
    }

    public void setLook(String look) {
        this.look = look;
    }

}
