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
import com.jsmartframework.web.tag.html.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class TabPaneTagHandler extends TagHandler {

    private String header;

    private String label;

    private String value;

    private String tabStyle;

    private String tabClass;

    private boolean divider;

    private List<TabPaneTagHandler> dropPanes;

    public TabPaneTagHandler() {
        dropPanes = new ArrayList<TabPaneTagHandler>();
    }

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();
        if (parent instanceof TabTagHandler) {
            ((TabTagHandler) parent).addTabPane(this);
            return false;

        } else if (parent instanceof TabPaneTagHandler) {
            ((TabPaneTagHandler) parent).addDropPane(this);
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
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(outputWriter);
        }
        return null;
    }

    @Override
    protected void appendEvent(Tag tag) {
        super.appendEvent(tag);
    }

    String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    String getTabStyle() {
        return tabStyle;
    }

    public void setTabStyle(String tabStyle) {
        this.tabStyle = tabStyle;
    }

    String getTabClass() {
        return tabClass;
    }

    public void setTabClass(String tabClass) {
        this.tabClass = tabClass;
    }

    boolean hasDivider() {
        return divider;
    }

    public void setDivider(boolean divider) {
        this.divider = divider;
    }

    private void addDropPane(TabPaneTagHandler dropPane) {
        this.dropPanes.add(dropPane);
    }

    List<TabPaneTagHandler> getDropPanes() {
        return dropPanes;
    }

}
