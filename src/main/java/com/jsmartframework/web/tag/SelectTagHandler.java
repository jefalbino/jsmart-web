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

import static com.jsmartframework.web.tag.js.JsConstants.JSMART_AJAX;

import com.jsmartframework.web.exception.InvalidAttributeException;
import com.jsmartframework.web.json.Ajax;
import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.css.Bootstrap;
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Label;
import com.jsmartframework.web.tag.html.Select;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Event;
import com.jsmartframework.web.tag.type.Size;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class SelectTagHandler extends TagHandler {

    private String selectValues;

    private boolean multiple;

    private Integer tabIndex;

    private String label;

    private String leftAddOn;

    private String rightAddOn;

    private String size;

    private String update;

    private String beforeSend;

    private String onError;

    private String onSuccess;

    private String onComplete;

    private List<OptionTagHandler> options;

    private List<TagHandler> childAddOns;

    public SelectTagHandler() {
        options = new ArrayList<OptionTagHandler>();
        childAddOns = new ArrayList<TagHandler>(2);
    }

    @Override
    public void validateTag() throws JspException {
        if (size != null && !Size.validateSmallLarge(size)) {
            throw InvalidAttributeException.fromPossibleValues("select", "size", Size.getSmallLargeValues());
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        // Just to call nested tags
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        setRandomId("select");

        Div formGroup = null;
        Div inputGroup = null;

        JspTag parent = getParent();
        if (label != null || parent instanceof FormTagHandler || parent instanceof RestTagHandler) {
            formGroup = new Div();
            formGroup.addAttribute("class", Bootstrap.FORM_GROUP);

            String size = null;
            if (parent instanceof FormTagHandler) {
                size = ((FormTagHandler) parent).getSize();
            } else if (parent instanceof RestTagHandler) {
                size = ((RestTagHandler) parent).getSize();
            }
            if (Size.LARGE.equalsIgnoreCase(size)) {
                formGroup.addAttribute("class", Bootstrap.FORM_GROUP_LARGE);
            } else if (Size.SMALL.equalsIgnoreCase(size)) {
                formGroup.addAttribute("class", Bootstrap.FORM_GROUP_SMALL);
            }
        }

        if (label != null) {
            Label labelTag = new Label();
            labelTag.addAttribute("for", id)
                    .addAttribute("class", Bootstrap.LABEL_CONTROL)
                    .addText(getTagValue(label));
            formGroup.addTag(labelTag);
        }

        if (leftAddOn != null || rightAddOn != null) {
            inputGroup = new Div();
            inputGroup.addAttribute("class", Bootstrap.INPUT_GROUP);

            if (Size.SMALL.equalsIgnoreCase(size)) {
                inputGroup.addAttribute("class", Bootstrap.INPUT_GROUP_SMALL);
            } else if (Size.LARGE.equalsIgnoreCase(size)) {
                inputGroup.addAttribute("class", Bootstrap.INPUT_GROUP_LARGE);
            }

            if (formGroup != null) {
                formGroup.addTag(inputGroup);
            }
        }

        if (leftAddOn != null) {
            boolean foundAddOn = false;

            for (int i = 0; i < childAddOns.size(); i++) {
                if (leftAddOn.equalsIgnoreCase(childAddOns.get(i).getId())) {
                    inputGroup.addTag(childAddOns.get(i).executeTag());
                    foundAddOn = true;
                    break;
                }
            }
            if (!foundAddOn) {
                Div div = new Div();
                div.addAttribute("class", Bootstrap.INPUT_GROUP_ADDON)
                    .addText(getTagValue(leftAddOn));
                inputGroup.addTag(div);
            }
        }

        String name = getTagName((multiple ? J_ARRAY : J_TAG), selectValues);

        Select select = new Select();
        select.addAttribute("id", id)
             .addAttribute("class", Bootstrap.FORM_CONTROL)
             .addAttribute("name", name)
             .addAttribute("tabindex", tabIndex)
             .addAttribute("disabled", isDisabled() ? "disabled" : null)
             .addAttribute("multiple", multiple ? "multiple" : null);

        if (Size.SMALL.equalsIgnoreCase(size)) {
            select.addAttribute("class", Bootstrap.INPUT_SMALL);
        } else if (Size.LARGE.equalsIgnoreCase(size)) {
            select.addAttribute("class", Bootstrap.INPUT_LARGE);
        }

        // Add the style class at last
        if (inputGroup != null) {
            inputGroup.addAttribute("style", getTagValue(style))
                .addAttribute("class", getTagValue(styleClass));
        } else {
            select.addAttribute("style", getTagValue(style))
                .addAttribute("class", getTagValue(styleClass));
        }

        appendValidator(select);
        appendRest(select, name);
        appendEvent(select);

        if (inputGroup != null) {
            inputGroup.addTag(select);
        } else if (formGroup != null) {
            formGroup.addTag(select);
        }

        if (rightAddOn != null) {
            boolean foundAddOn = false;

            for (int i = 0; i < childAddOns.size(); i++) {
                if (rightAddOn.equalsIgnoreCase(childAddOns.get(i).getId())) {
                    inputGroup.addTag(childAddOns.get(i).executeTag());
                    foundAddOn = true;
                    break;
                }
            }
            if (!foundAddOn) {
                Div div = new Div();
                div.addAttribute("class", Bootstrap.INPUT_GROUP_ADDON)
                    .addText(getTagValue(rightAddOn));
                inputGroup.addTag(div);
            }
        }

        if (ajax) {
            appendDocScript(getFunction());
        }

        for (OptionTagHandler option : options) {
            option.setName(selectValues);
            select.addTag(option.executeTag());
        }

        appendAjax(id);
        appendBind(id);

        if (formGroup != null) {
            appendTooltip(formGroup);
            appendPopOver(formGroup);

        } else if (inputGroup != null) {
            appendTooltip(inputGroup);
            appendPopOver(inputGroup);
        } else {
            appendTooltip(select);
            appendPopOver(select);
        }

        return formGroup != null ? formGroup : inputGroup != null ? inputGroup : select;
    }

    private StringBuilder getFunction() {
        Ajax jsonAjax = new Ajax();
        jsonAjax.setId(id);
        jsonAjax.setMethod("post");
        jsonAjax.setTag("select");

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

        StringBuilder builder = new StringBuilder();
        builder.append(JSMART_AJAX.format(getJsonValue(jsonAjax)));
        return getBindFunction(id, Event.CHANGE.name(), builder);
    }

    void addChildAddOn(TagHandler childAddOn) {
        this.childAddOns.add(childAddOn);
    }

    void addOption(OptionTagHandler option) {
        this.options.add(option);
    }

    public void setSelectValues(String selectValues) {
        this.selectValues = selectValues;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLeftAddOn(String leftAddOn) {
        this.leftAddOn = leftAddOn;
    }

    public void setRightAddOn(String rightAddOn) {
        this.rightAddOn = rightAddOn;
    }

    public void setSize(String size) {
        this.size = size;
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
