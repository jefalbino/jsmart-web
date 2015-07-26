/*
 * JSmart5 - Java Web Development Framework
 * Copyright (c) 2014, Jeferson Albino da Silva, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
*/

package com.jsmart5.framework.tag;

import static com.jsmart5.framework.tag.js.JsConstants.*;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.adapter.ListAdapter;
import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.json.Ajax;
import com.jsmart5.framework.json.Param;
import com.jsmart5.framework.json.Scroll;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.css.JSmart5;
import com.jsmart5.framework.tag.html.*;
import com.jsmart5.framework.tag.html.Set;
import com.jsmart5.framework.tag.type.Event;
import com.jsmart5.framework.tag.type.Size;
import com.jsmart5.framework.tag.type.Type;

public final class AutoCompleteTagHandler extends TagHandler {

	private static final int DEFAULT_MIN_LENGTH = 1;

    private String var;

    private String value;

    private String values;

    private Integer scrollSize;

    private String scrollOffset;

    private String maxHeight;

    private String size;

    private Integer length;

    private Integer minLength = DEFAULT_MIN_LENGTH;

    private boolean readOnly;

    private boolean autoFocus;

    private Integer tabIndex;

    private String mask;

    private String placeHolder;

    private String inputText;

    private String label;

    private String leftAddOn;

    private String rightAddOn;

    private boolean disabled;

    private String update;

    private String beforeSend;

    private String onError;

    private String onSuccess;

    private String onComplete;

    private List<TagHandler> childAddOns;

    private final List<RowTagHandler> rows;

    public AutoCompleteTagHandler() {
        rows = new ArrayList<RowTagHandler>();
        childAddOns = new ArrayList<TagHandler>(2);
    }

	@Override
	public void validateTag() throws JspException {
		if (minLength != null && minLength <= 0) {
            throw InvalidAttributeException.fromConstraint("autocomplete", "minLength", "greater than 0");
		}
        if (size != null && !Size.validateSmallLarge(size)) {
            throw InvalidAttributeException.fromPossibleValues("autocomplete", "size", Size.getSmallLargeValues());
        }
        if (scrollSize != null && scrollSize <= 0) {
            throw InvalidAttributeException.fromConstraint("autocomplete", "scrollSize", "greater than zero");
        }
        if (scrollSize != null && maxHeight == null) {
            throw InvalidAttributeException.fromConflict("autocomplete", "maxHeight", "Attribute [maxHeight] must be specified");
        }
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

        // Just to call nested tags
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        Tag inputPart = executeInputPart();
        Tag listPart = executeListPart();
        
        appendDocScript(getAjaxFunction());

        Set set = new Set();
        set.addTag(inputPart);
        set.addTag(listPart);
        return set;
	}

    private Tag executeInputPart() throws JspException, IOException {
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

        String name = getTagName(J_TAG, value);

        Input input = new Input();
        input.addAttribute("name", name + (readOnly ? EL_PARAM_READ_ONLY : ""))
                .addAttribute("type", Type.TEXT.name().toLowerCase())
                .addAttribute("class", Bootstrap.FORM_CONTROL)
                .addAttribute("tabindex", tabIndex)
                .addAttribute("maxlength", length)
                .addAttribute("readonly", readOnly ? readOnly : null)
                .addAttribute("disabled", disabled ? "disabled" : null)
                .addAttribute("placeholder", getTagValue(placeHolder))
                .addAttribute("datatype", Type.TEXT.name().toLowerCase())
                .addAttribute("autofocus", autoFocus ? autoFocus : null)
                .addAttribute("data-mask", mask)
                .addAttribute("value", getTagValue(value))
                .addAttribute("min-length", minLength);

        appendRefId(input, id);

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

        Span span = new Span();
        span.addAttribute("auto-refresh-id", id)
            .addAttribute("refresh-icon", "")
            .addAttribute("class", Bootstrap.GLYPHICON)
            .addAttribute("class", Bootstrap.GLYPHICON_REFRESH)
            .addAttribute("class", Bootstrap.GLYPHICON_ANIMATE)
            .addAttribute("class", JSmart5.AUTO_COMPLETE_REFRESH)
            .addAttribute("aria-hidden", "true");

        if (inputGroup != null) {
            inputGroup.addTag(input);
            inputGroup.addTag(span);
        } else if (formGroup != null) {
            formGroup.addTag(input);
            formGroup.addTag(span);
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

        Set inputSet = new Set().addTag(input).addTag(span);
        return formGroup != null ? formGroup : inputGroup != null ? inputGroup : inputSet;
    }

    @SuppressWarnings("unchecked")
	private Tag executeListPart() throws JspException, IOException {
        HttpServletRequest request = getRequest();

        Ul ul = new Ul();
        ul.addAttribute("auto-list-id", id)
                .addAttribute("style", maxHeight != null ? "max-height: " + maxHeight + ";" : null)
                .addAttribute("style", "display: none;")
                .addAttribute("class", Bootstrap.LIST_GROUP);

        if (scrollSize != null) {
            ul.addAttribute("style", "overflow: auto;")
                    .addAttribute("scroll-size", scrollSize);
        }

        if (loadTag != null) {
            Li li = new Li();
            li.addAttribute("class", Bootstrap.LIST_GROUP_ITEM)
                    .addAttribute("style", "display: none;")
                    .addAttribute("style", "text-align: center;");

            li.addTag(loadTag.executeTag());
            ul.addTag(li);
        }

        // It means that the auto complete was requested
        String autoCpltParam = request.getParameter(getTagName(J_AUTOCPLT, fakeTagName(id)));
        if (autoCpltParam != null) {
        	
        	// Get the scroll parameters case requested by scroll list
            Scroll scroll = null;

            // It means that a scroll maybe happened
            String scrollParam = request.getParameter(getTagName(J_SCROLL, fakeTagName(id)));
            if (scrollParam != null) {
                scroll = GSON.fromJson(scrollParam, Scroll.class);
            }

	        Object object = getListContent(getTagValue(values), scroll);
	
	        if (object instanceof List<?>) {
	            Iterator<Object> iterator = ((List<Object>) object).iterator();
	
	            int scrollIndex = scroll != null ? scroll.getIndex() : 0;
	            int selectIndex = scrollIndex;
	
	            while (iterator.hasNext()) {
	            	Object obj = iterator.next();
	            	if (obj == null) {
	            		continue;
	            	}
	                request.setAttribute(var, obj);

	                for (RowTagHandler row : rows) {
	                    row.setSelectable(value != null);
	                    row.setSelectIndex(selectIndex);
	                    row.setScrollIndex(scrollIndex);

	                    Tag rowTag = row.executeTag();
                        if (inputText != null) {
                            rowTag.addAttribute("to-string", getTagValue(inputText));
                        } else {
                            rowTag.addAttribute("to-string", obj.toString());
                        }

                        Object scrollOffsetVal = getTagValue(scrollOffset);
                        if (scrollOffsetVal != null) {
                            rowTag.addAttribute("scroll-offset", scrollOffsetVal);
                        }
	                    ul.addTag(rowTag);
	                }
	                selectIndex++;
	                request.removeAttribute(var);
	            }
	        }
        }

        if (scrollSize != null) {
            appendDocScript(getScrollFunction());
        }
        return ul;
    }

    @SuppressWarnings("unchecked")
    private List<?> getListContent(Object object, Scroll scroll) throws JspException {
        int index = scroll != null ? scroll.getIndex() : 0;
        Object offset = scroll != null ? scroll.getOffset() : null;

        if (object instanceof ListAdapter) {
            if (scrollSize == null) {
                throw InvalidAttributeException.fromConflict("autocomplete", "scrollSize",
                        "Attribute [scrollSize] must be specified to use AutoCompleteAdapter");
            }

            ListAdapter<Object> adapter = (ListAdapter<Object>) object;
            return adapter.load(index, offset, scrollSize);

        } else if (object instanceof List) {
            List<Object> list = (List<Object>) object;
            Object[] array = list.toArray();

            List<Object> retList = new ArrayList<Object>();

            int size = list.size();
            if (scrollSize != null) {
                size = index + scrollSize >= list.size() ? list.size() : (int) (index + scrollSize);
            }

            for (int i = index; i < size; i++) {
                retList.add(array[i]);
            }
            return retList;
        }
        return Collections.EMPTY_LIST;
    }

    private StringBuilder getAjaxFunction() {
		Ajax jsonAjax = new Ajax();

		jsonAjax.setId(id);
		jsonAjax.setMethod("post");
		jsonAjax.setTag("autocomplete");
		
		jsonAjax.addParam(new Param(getTagName(J_AUTOCPLT, fakeTagName(id)), ""));

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
		builder.append(JSMART_AUTOCOMPLETE.format(getJsonValue(jsonAjax)));
		return getBindFunction(id, Event.KEYUP.name(), builder);
	}

    private StringBuilder getScrollFunction() {
		Ajax jsonAjax = new Ajax();
		jsonAjax.setId(id);
		jsonAjax.setMethod("post");
		jsonAjax.setTag("autoscroll");

		jsonAjax.addParam(new Param(getTagName(J_AUTOCPLT, fakeTagName(id)), ""));
		jsonAjax.addParam(new Param(getTagName(J_SCROLL, fakeTagName(id)), ""));

		StringBuilder builder = new StringBuilder();
		builder.append(JSMART_AUTOCPLTSCROLL.format(getJsonValue(jsonAjax)));
		return builder;
	}

    void addRow(RowTagHandler row) {
        rows.add(row);
    }

    void addChildAddOn(TagHandler childAddOn) {
        this.childAddOns.add(childAddOn);
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public void setMaxHeight(String maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setScrollSize(Integer scrollSize) {
        this.scrollSize = scrollSize;
    }

    public void setScrollOffset(String scrollOffset) {
        this.scrollOffset = scrollOffset;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public void setLeftAddOn(String leftAddOn) {
        this.leftAddOn = leftAddOn;
    }

    public void setRightAddOn(String rightAddOn) {
        this.rightAddOn = rightAddOn;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
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
