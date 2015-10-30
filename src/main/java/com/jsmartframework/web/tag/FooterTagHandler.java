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

import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.css.Bootstrap;
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Tag;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class FooterTagHandler extends TagHandler {

    private String title;

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();
        if (parent instanceof ModalTagHandler) {
            ((ModalTagHandler) parent).setFooter(this);
            return false;

        } else if (parent instanceof PanelTagHandler) {
            ((PanelTagHandler) parent).setFooter(this);
            return false;
        }
        return true;
    }

    @Override
    public void validateTag() throws JspException {
        // DO NOTHING
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        JspTag parent = getParent();

        StringWriter sw = new StringWriter();
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(sw);
        }

        setRandomId("footer");

        Tag footer = new Div();

        appendRefId(footer, id);

        if (parent instanceof ModalTagHandler) {
            footer.addAttribute("class", Bootstrap.MODAL_FOOTER);

        } else if (parent instanceof PanelTagHandler) {
            footer.addAttribute("class", Bootstrap.PANEL_FOOTER);
        }

        footer.addAttribute("style", getTagValue(style))
            .addAttribute("class", getTagValue(styleClass))
                .addText(getTagValue(title))
                .addText(sw.toString());

        if (parent instanceof TagHandler && getMappedValue(DELEGATE_TAG_PARENT) == null) {
            String tagId = ((TagHandler) parent).getId();
            appendAjax(tagId);
            appendBind(tagId);
        } else {
            appendAjax(id);
            appendBind(id);
        }

        return footer;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
