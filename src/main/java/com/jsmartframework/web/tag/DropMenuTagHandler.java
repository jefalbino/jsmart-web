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
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.html.Ul;
import com.jsmartframework.web.tag.type.Align;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class DropMenuTagHandler extends TagHandler {

    private String align;

    private boolean dropUp;

    private boolean segmented;

    private List<DropActionTagHandler> dropActions;

    public DropMenuTagHandler() {
        dropActions = new ArrayList<DropActionTagHandler>();
    }

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();
        if (parent instanceof ButtonTagHandler) {
            ((ButtonTagHandler) parent).setDropMenu(this);
            return false;

        } else if (parent instanceof LinkTagHandler) {
            ((LinkTagHandler) parent).setDropMenu(this);
            return false;

        } else if (parent instanceof DropDownTagHandler) {
            ((DropDownTagHandler) parent).setDropMenu(this);
            return false;
        }
        return false;
    }

    @Override
    public void validateTag() throws JspException {
        if (align != null && !Align.validateLeftRight(align)) {
            throw InvalidAttributeException.fromPossibleValues("dropmenu", "align", Align.getLeftRightValues());
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        // Just to call nested tags
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        setRandomId("dropmenu");

        Ul ul = new Ul();
        ul.addAttribute("id", id)
            .addAttribute("role", "menu")
            .addAttribute("style", getTagValue(style))
            .addAttribute("class", Bootstrap.DROPDOWN_MENU);

        if (Align.RIGHT.equalsIgnoreCase(align)) {
            ul.addAttribute("class", Bootstrap.DROPDOWN_MENU_RIGHT);
        }

        // At last place the style class
        ul.addAttribute("class", getTagValue(styleClass));

        for (DropActionTagHandler dropAction : dropActions) {
            ul.addTag(dropAction.executeTag());
        }

        appendRepeatChild(ul);
        return ul;
    }

    void addDropAction(DropActionTagHandler dropAction) {
        this.dropActions.add(dropAction);
    }

    public void setAlign(String align) {
        this.align = align;
    }

    boolean isDropUp() {
        return dropUp;
    }

    public void setDropUp(boolean dropUp) {
        this.dropUp = dropUp;
    }

    boolean isSegmented() {
        return segmented;
    }

    public void setSegmented(boolean segmented) {
        this.segmented = segmented;
    }

}