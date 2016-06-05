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

import static com.jsmartframework.web.tag.js.JsConstants.JSMART_TAB;
import static com.jsmartframework.web.tag.js.JsConstants.JSMART_TABPANE;

import com.jsmartframework.web.exception.InvalidAttributeException;
import com.jsmartframework.web.json.Ajax;
import com.jsmartframework.web.json.Param;
import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.css.Bootstrap;
import com.jsmartframework.web.tag.html.A;
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Input;
import com.jsmartframework.web.tag.html.Li;
import com.jsmartframework.web.tag.html.Span;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.html.Ul;
import com.jsmartframework.web.tag.type.Align;
import com.jsmartframework.web.tag.type.Event;
import com.jsmartframework.web.tag.type.Type;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

public final class TabTagHandler extends TagHandler {

    private String navStyle;

    private String navClass;

    private String tabValue;

    private String pills;

    private boolean justified;

    private boolean fade;

    private String onShow;

    private String onShown;

    private String onHide;

    private String onHidden;

    private String update;

    private String beforeSend;

    private String onError;

    private String onSuccess;

    private String onComplete;

    private List<TabPaneTagHandler> tabPanes;

    public TabTagHandler() {
        tabPanes = new ArrayList<TabPaneTagHandler>();
    }

    @Override
    public void validateTag() throws JspException {
        if (pills != null && !Type.validateTab(pills)) {
            throw InvalidAttributeException.fromPossibleValues("tab", "pills", Type.getTabValues());
        }
        if (Type.STACKED.name().equalsIgnoreCase(pills) && justified) {
            throw InvalidAttributeException.fromConflict("tab", "pills", "Attributes [justified] and [pills=stacked] cannot coexist");
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        setRandomId("tab");

        Div tab = new Div();
        tab.addAttribute("id", id)
            .addAttribute("style", getTagValue(style))
            .addAttribute("class", getTagValue(styleClass))
            .addAttribute("role", "tabpanel");

        appendEvent(tab);

        Input input = null;
        if (tabValue != null) {
            input = new Input();
            input.addAttribute("type", "hidden")
                .addAttribute("name", getTagName(J_TAG, tabValue));
            tab.addTag(input);
        }

        Ul ul = new Ul();
        ul.addAttribute("role", "tablist")
            .addAttribute("class", Bootstrap.NAV)
            .addAttribute("style", getTagValue(navStyle));

        if (pills == null) {
            ul.addAttribute("class", Bootstrap.NAV_TABS);
        } else {
            ul.addAttribute("class", Bootstrap.NAV_PILLS);
            if (Type.STACKED.equalsIgnoreCase(pills)) {
                ul.addAttribute("class", Bootstrap.NAV_STACKED);
            }
        }

        if (justified) {
            ul.addAttribute("class", Bootstrap.NAV_JUSTIFIED);
        }

        // At last the custom style class
        ul.addAttribute("class", getTagValue(navClass));

        Div content = new Div();
        content.addAttribute("class", Bootstrap.TAB_CONTENT);

        tab.addTag(ul)
            .addTag(content);

        // Value to keep track of what tab pane was opened
        Object tabVal = getTagValue(tabValue);

        for (TabPaneTagHandler tabPane : tabPanes) {

            // The execute tag must be called first to decide if there are drop down children
            StringWriter swPane = new StringWriter();
            tabPane.setOutputWriter(swPane);
            tabPane.executeTag();

            setRandomId(tabPane, "tabpane");

            String liId = getRandomId();

            Li li = new Li();
            li.addAttribute("id", liId)
                .addAttribute("role", "presentation")
                .addAttribute("class", tabPane.isDisabled() ? Bootstrap.DISABLED : null)
                .addAttribute("style", getTagValue(tabPane.getTabStyle()))
                .addAttribute("class", getTagValue(tabPane.getTabClass()));

            appendAjax(tabPane, liId);
            appendBind(tabPane, liId);

            A a = new A();

            for (IconTagHandler iconTag : tabPane.getIconTags()) {
                if (Align.LEFT.equalsIgnoreCase(iconTag.getSide())) {
                    a.addTag(iconTag.executeTag());
                    a.addText(" ");
                }
            }

            a.addText(getTagValue(tabPane.getLabel()));

            for (IconTagHandler iconTag : tabPane.getIconTags()) {
                if (Align.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
                    a.addText(" ");
                    a.addTag(iconTag.executeTag());
                }
            }

            // Case drop panes not empty we must include drop down style
            if (!tabPane.getDropPanes().isEmpty()) {
                li.addAttribute("class", Bootstrap.DROPDOWN);

                a.addAttribute("role", "button")
                    .addAttribute("style", "cursor: pointer;")
                    .addAttribute("class", Bootstrap.DROPDOWN_TOGGLE)
                    .addAttribute("class", tabPane.isDisabled() ? Bootstrap.DISABLED : null)
                    .addAttribute("data-toggle", "dropdown")
                    .addAttribute("aria-expanded", "false");

                Span span = new Span();
                span.addAttribute("class", Bootstrap.CARET);
                a.addTag(span);

            } else {
                a.addAttribute("href", "#" + tabPane.getId())
                    .addAttribute("aria-controls", tabPane.getId())
                    .addAttribute("class", tabPane.isDisabled() ? Bootstrap.DISABLED : null)
                    .addAttribute("role", "tab")
                    .addAttribute("data-toggle", "tab");
            }

            li.addTag(a);
            appendEvent(li, tabPane);
            ul.addTag(li);

            // Case dropDowns not empty we must include on drop down li tags
            if (!tabPane.getDropPanes().isEmpty()) {

                Ul dropUl = new Ul();
                dropUl.addAttribute("role", "menu")
                    .addAttribute("class", Bootstrap.DROPDOWN_MENU);
                li.addTag(dropUl);

                for (TabPaneTagHandler dropPane : tabPane.getDropPanes()) {

                    // The execute tag must be called first to decide if there are drop down children
                    StringWriter swDropPane = new StringWriter();
                    dropPane.setOutputWriter(swDropPane);
                    dropPane.executeTag();

                    Li dropLi = createDropTab(dropUl, dropPane);
                    tabVal = addTabContent(content, dropLi, swDropPane, dropPane, tabVal);
                }

            } else {
                tabVal = addTabContent(content, li, swPane, tabPane, tabVal);
            }
        }

        // Case tabValue was specified
        if (input != null) {
            input.addAttribute("value", tabVal);

            appendDocScript(getTabFunction());
            if (ajax) {
                appendDocScript(getTabPaneFunction());
            }
        }

        if (onShow != null) {
            appendDocScript(getBindFunction(id, "show.bs.tab", new StringBuilder(onShow)));
        }
        if (onShown != null) {
            appendDocScript(getBindFunction(id, "shown.bs.tab", new StringBuilder(onShown)));
        }
        if (onHide != null) {
            appendDocScript(getBindFunction(id, "hide.bs.tab", new StringBuilder(onHide)));
        }
        if (onHidden != null) {
            appendDocScript(getBindFunction(id, "hidden.bs.tab", new StringBuilder(onHidden)));
        }

        return tab;
    }

    private Li createDropTab(Ul dropUl, TabPaneTagHandler dropPane) throws JspException, IOException {
        setRandomId(dropPane, "tabpane");

        if (dropPane.getHeader() != null) {
            Li headerLi = new Li();
            headerLi.addAttribute("role", "presentation")
                .addAttribute("class", Bootstrap.DROPDOWN_HEADER)
                .addText(getTagValue(dropPane.getHeader()));
            dropUl.addTag(headerLi);
        }

        String dropLiId = getRandomId();

        Li dropLi = new Li();
        dropLi.addAttribute("id", dropLiId)
            .addAttribute("role", "presentation")
            .addAttribute("class", dropPane.isDisabled() ? Bootstrap.DISABLED : null);

        appendAjax(dropPane, dropLiId);
        appendBind(dropPane, dropLiId);

        A dropA = new A();
        dropA.addAttribute("href", "#" + dropPane.getId())
            .addAttribute("tabIndex", "-1")
            .addAttribute("role", "tab")
            .addAttribute("class", dropPane.isDisabled() ? Bootstrap.DISABLED : null)
            .addAttribute("data-toggle", "tab")
            .addAttribute("aria-controls", dropPane.getId())
            .addAttribute("aria-expanded", "false");

        for (IconTagHandler iconTag : dropPane.getIconTags()) {
            if (Align.LEFT.equalsIgnoreCase(iconTag.getSide())) {
                dropA.addTag(iconTag.executeTag());
                dropA.addText(" ");
            }
        }

        dropA.addText(getTagValue(dropPane.getLabel()));

        for (IconTagHandler iconTag : dropPane.getIconTags()) {
            if (Align.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
                dropA.addText(" ");
                dropA.addTag(iconTag.executeTag());
            }
        }

        dropLi.addTag(dropA);
        dropUl.addTag(dropLi);

        if (dropPane.hasDivider()) {
            Li dividerLi = new Li();
            dividerLi.addAttribute("class", Bootstrap.DIVIDER);
            dropUl.addTag(dividerLi);
        }

        return dropLi;
    }

    private Object addTabContent(Div tab, Li tabLi, StringWriter swContent, TabPaneTagHandler tabPane, Object tabVal) {

        String tabPaneValue = (String) getTagValue(tabPane.getValue());
        tabLi.addAttribute("tab-value", tabPaneValue != null ? tabPaneValue : tabPane.getId());

        Div tabContent = new Div();
        tabContent.addAttribute("id", tabPane.getId())
            .addAttribute("style", getTagValue(tabPane.getStyle()))
            .addAttribute("role", "tabpanel")
            .addAttribute("class", Bootstrap.TAB_PANE)
            .addAttribute("class", fade ? Bootstrap.FADE : null)
            .addAttribute("class", getTagValue(tabPane.getStyleClass()));

        // Include the tab values
        if (tabVal == null) {
            tabVal = tabPaneValue != null ? tabPaneValue : tabPane.getId();
            tabLi.addAttribute("class", Bootstrap.ACTIVE)
                .addAttribute("class", fade ? Bootstrap.IN : null);
            tabContent.addAttribute("class", Bootstrap.ACTIVE);

        } else if (tabVal.equals(tabPaneValue)) {
            tabLi.addAttribute("class", Bootstrap.ACTIVE)
                .addAttribute("class", fade ? Bootstrap.IN : null);
            tabContent.addAttribute("class", Bootstrap.ACTIVE);
        }

        tabContent.addText(executeExpressions(swContent.toString()));
        tab.addTag(tabContent);
        return tabVal;
    }

    private StringBuilder getTabFunction() {
        Ajax jsonAjax = getJsonAjax();
        StringBuilder builder = new StringBuilder();
        builder.append(JSMART_TAB.format(getJsonValue(jsonAjax)));
        return builder;
    }

    private StringBuilder getTabPaneFunction() {
        Ajax jsonAjax = getJsonAjax();
        StringBuilder builder = new StringBuilder();
        builder.append(JSMART_TABPANE.format(getJsonValue(jsonAjax)));
        return getDelegateFunction(id, "ul li", Event.CLICK.name(), builder);
    }

    private Ajax getJsonAjax() {
        Ajax jsonAjax = new Ajax();
        jsonAjax.setId(id);
        jsonAjax.setMethod("post");

        jsonAjax.addParam(new Param(getTagName(J_TAG, tabValue), ""));

        if (update != null) {
            jsonAjax.setUpdate(update.trim());
        }
        if (beforeSend != null) {
            jsonAjax.setBefore((String) getTagValue(beforeSend.trim()));
        }
        if (onError != null) {
            jsonAjax.setError((String) getTagValue(onError.trim()));
        }
        if (onSuccess != null) {
            jsonAjax.setSuccess((String) getTagValue(onSuccess.trim()));
        }
        if (onComplete != null) {
            jsonAjax.setComplete((String) getTagValue(onComplete.trim()));
        }
        return jsonAjax;
    }

    void addTabPane(TabPaneTagHandler tabItem) {
        tabPanes.add(tabItem);
    }

    public void setNavStyle(String navStyle) {
        this.navStyle = navStyle;
    }

    public void setNavClass(String navClass) {
        this.navClass = navClass;
    }

    public void setTabValue(String tabValue) {
        this.tabValue = tabValue;
    }

    public void setPills(String pills) {
        this.pills = pills;
    }

    public void setJustified(boolean justified) {
        this.justified = justified;
    }

    public void setFade(boolean fade) {
        this.fade = fade;
    }

    public void setOnShow(String onShow) {
        this.onShow = onShow;
    }

    public void setOnShown(String onShown) {
        this.onShown = onShown;
    }

    public void setOnHide(String onHide) {
        this.onHide = onHide;
    }

    public void setOnHidden(String onHidden) {
        this.onHidden = onHidden;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public void setBeforeSend(String beforeSend) {
        this.beforeSend = beforeSend;
    }

    public void setOnError(String onError) {
        this.onError = onError;
    }

    public void setOnSuccess(String onSuccess) {
        this.onSuccess = onSuccess;
    }

    public void setOnComplete(String onComplete) {
        this.onComplete = onComplete;
    }

}
