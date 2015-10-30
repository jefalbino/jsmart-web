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

import static com.jsmartframework.web.tag.js.JsConstants.JSMART_PROGRESSGROUP;

import com.jsmartframework.web.exception.InvalidAttributeException;
import com.jsmartframework.web.json.Progress;
import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.css.Bootstrap;
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

public final class ProgressGroupTagHandler extends TagHandler {

    private Integer interval;

    private String onInterval;

    private List<ProgressBarTagHandler> bars;

    private List<String> barValues;

    public ProgressGroupTagHandler() {
        bars = new ArrayList<ProgressBarTagHandler>(3);
        barValues = new ArrayList<String>(3);
    }

    @Override
    public void validateTag() throws JspException {
        if (interval != null && interval < 0) {
            throw InvalidAttributeException.fromConstraint("progressgroup", "interval", "greater than 0");
        }
        if (onInterval != null && interval == null) {
            throw InvalidAttributeException.fromConflict("progressgroup", "interval", "Attribute must be specified case [onInterval] attribute is used");
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        setRandomId("progressgroup");

        Div group = new Div();
        group.addAttribute("id", id)
                .addAttribute("style", getTagValue(style))
                .addAttribute("class", Bootstrap.PROGRESS)
                .addAttribute("class", getTagValue(styleClass));

        // Save the differences to calculate the relation
        int total = 0;
        int[] relation = new int[bars.size()];

        for (int i = 0; i < relation.length; i++) {
            int diff = bars.get(i).initIntValues(true);
            relation[i] = diff;
            total += diff;
        }

        // Calculate the percentage of each bar related to total bars and execute tag
        for (int i = 0; i < relation.length; i++) {
            relation[i] = ((100 * relation[i] / total) | 0);

            bars.get(i).setRelation(relation[i]);
            group.addTag(bars.get(i).executeTag());
        }

        appendEvent(group);

        appendAjax(id);
        appendBind(id);

        appendTooltip(group);
        appendPopOver(group);

        if (onInterval != null) {
            appendDocScript(getIntervalScript(relation));
        }
        return group;
    }

    private StringBuilder getIntervalScript(final int[] relation) {
        Progress jsonProgress = new Progress();
        jsonProgress.setId(id);
        jsonProgress.setMethod("get");
        jsonProgress.setRequest(ajax);
        jsonProgress.setInterval(interval);
        jsonProgress.setOnInterval(onInterval);
        jsonProgress.setRelation(relation);
        return new StringBuilder(JSMART_PROGRESSGROUP.format(getJsonValue(jsonProgress)));
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public void setOnInterval(String onInterval) {
        this.onInterval = onInterval;
    }

    void addBar(ProgressBarTagHandler bar) {
        this.bars.add(bar);
    }

    void addBarValue(String value) {
        this.barValues.add(value);
    }

    boolean containsBarValue(String value) {
        return barValues.contains(value);
    }

}
