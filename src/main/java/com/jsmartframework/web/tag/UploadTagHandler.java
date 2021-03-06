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
import com.jsmartframework.web.tag.html.Div;
import com.jsmartframework.web.tag.html.Input;
import com.jsmartframework.web.tag.html.Label;
import com.jsmartframework.web.tag.html.Set;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Size;
import com.jsmartframework.web.tag.type.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public final class UploadTagHandler extends TagHandler {

    private String value;

    private String label;

    private String size;

    private Integer tabIndex;

    private String leftAddOn;

    private String rightAddOn;

    private String placeholder;

    private boolean readOnly;

    private boolean autoFocus;

    private String onUpload;

    private List<TagHandler> childAddOns;

    private String accept;

    public UploadTagHandler() {
        childAddOns = new ArrayList<TagHandler>(2);
    }

    @Override
    public void validateTag() throws JspException {
        if (size != null && !Size.validateSmallLarge(size)) {
            throw InvalidAttributeException.fromPossibleValues("upload", "size", Size.getSmallLargeValues());
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        // Just to call nested tags
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        setRandomId("upload");

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

        final String name = getTagName(J_FILE, value);

        // Hidden input must be included to be captured on request parameters
        Input hidden = new Input();
        hidden.addAttribute("type", "hidden")
            .addAttribute("name", name);

        Input input = new Input();
        input.addAttribute("name", name != null ? name.replace(J_FILE, J_PART) : null)
             .addAttribute("type", Type.FILE.name().toLowerCase())
             .addAttribute("accept", accept)
             .addAttribute("class", Bootstrap.FORM_CONTROL)
             .addAttribute("tabindex", tabIndex)
             .addAttribute("readonly", readOnly ? readOnly : null)
             .addAttribute("disabled", isDisabled() ? "disabled" : null)
             .addAttribute("placeholder", getTagValue(placeholder))
             .addAttribute("datatype", Type.FILE.name().toLowerCase())
             .addAttribute("autofocus", autoFocus ? autoFocus : null)
             .addAttribute("onupload", onUpload);

        appendRefId(input, id);

        input.addAttribute("value", getTagValue(value));

        if (Size.SMALL.equalsIgnoreCase(size)) {
            input.addAttribute("class", Bootstrap.INPUT_SMALL);
        } else if (Size.LARGE.equalsIgnoreCase(size)) {
            input.addAttribute("class", Bootstrap.INPUT_LARGE);
        }

        // Add the style class at last
        if (inputGroup != null) {
            inputGroup.addAttribute("style", getTagValue(style))
                .addAttribute("class", getTagValue(styleClass));
        } else {
            input.addAttribute("style", getTagValue(style))
                .addAttribute("class", getTagValue(styleClass));
        }

        appendValidator(input);
        appendRest(input, name);
        appendEvent(input);

        if (inputGroup != null) {
            inputGroup.addTag(input);
            inputGroup.addTag(hidden);
        } else if (formGroup != null) {
            formGroup.addTag(input);
            formGroup.addTag(hidden);
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

        appendAjax(id);
        appendBind(id);

        Set set = new Set();
        set.addTag(input);
        set.addTag(hidden);

        if (formGroup != null) {
            appendTooltip(formGroup);
            appendPopOver(formGroup);

        } else if (inputGroup != null) {
            appendTooltip(inputGroup);
            appendPopOver(inputGroup);
        } else {
            appendTooltip(input);
            appendPopOver(input);
        }

        return formGroup != null ? formGroup : inputGroup != null ? inputGroup : set;
    }

    void addChildAddOn(TagHandler childAddOn) {
        this.childAddOns.add(childAddOn);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    public void setLeftAddOn(String leftAddOn) {
        this.leftAddOn = leftAddOn;
    }

    public void setRightAddOn(String rightAddOn) {
        this.rightAddOn = rightAddOn;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

    public void setOnUpload(String onUpload) {
        this.onUpload = onUpload;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }
}
