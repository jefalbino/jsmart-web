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

import com.jsmartframework.web.json.Ajax;
import com.jsmartframework.web.json.Param;
import com.jsmartframework.web.manager.BeanHandler;
import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.css.Bootstrap;
import com.jsmartframework.web.tag.html.A;
import com.jsmartframework.web.tag.html.Li;
import com.jsmartframework.web.tag.html.Set;
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Align;
import com.jsmartframework.web.tag.type.Event;
import com.jsmartframework.web.util.WebUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import static com.jsmartframework.web.tag.js.JsConstants.JSMART_AJAX;

public class DropActionTagHandler extends TagHandler {

    private String header;

    private boolean divider;

    private String label;

    private String outcome;

    private String action;

    private String update;

    private String beforeSend;

    private String onError;

    private String onSuccess;

    private String onComplete;

    private boolean skipValidation;

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();
        if (parent instanceof DropMenuTagHandler) {
            ((DropMenuTagHandler) parent).addDropAction(this);
            return false;
        }
        return super.beforeTag();
    }

    @Override
    public void validateTag() throws JspException {
        // DO NOTHING
    }

    @Override
    public Tag executeTag() throws JspException, IOException {
        StringWriter sw = new StringWriter();

        // Just to call nested tags
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(sw);
        }

        Set set = new Set();
        setRandomId("dropaction");

        if (header != null) {
            Li headerLi = new Li();
            headerLi.addAttribute("role", "presentation")
                    .addAttribute("class", Bootstrap.DROPDOWN_HEADER)
                    .addText(getTagValue(header));
            set.addTag(headerLi);
        }

        Li li = new Li();
        li.addAttribute("id", id)
                .addAttribute("role", "presentation")
                .addAttribute("style", getTagValue(style))
                .addAttribute("class", disabled ? Bootstrap.DISABLED : null)
                .addAttribute("class", getTagValue(styleClass));
        set.addTag(li);

        appendEvent(li);

        A a = new A();
        a.addAttribute("style", "cursor: pointer;");
        li.addTag(a);

        for (IconTagHandler iconTag : iconTags) {
            if (Align.LEFT.equalsIgnoreCase(iconTag.getSide())) {
                a.addTag(iconTag.executeTag());
                a.addText(" ");
            }
        }

        for (ImageTagHandler imageTag : imageTags) {
            if (Align.LEFT.equalsIgnoreCase(imageTag.getSide())) {
                a.addTag(imageTag.executeTag());
                a.addText(" ");
            }
        }

        a.addText(sw.toString());
        a.addText(getTagValue(label));

        for (IconTagHandler iconTag : iconTags) {
            if (Align.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
                a.addText(" ");
                a.addTag(iconTag.executeTag());
            }
        }

        for (ImageTagHandler imageTag : imageTags) {
            if (Align.RIGHT.equalsIgnoreCase(imageTag.getSide())) {
                a.addText(" ");
                a.addTag(imageTag.executeTag());
            }
        }

        if (divider) {
            Li dividerLi = new Li();
            dividerLi.addAttribute("class", Bootstrap.DIVIDER);
            set.addTag(dividerLi);
        }

        StringBuilder urlParams = new StringBuilder("?");
        for (String key : params.keySet()) {
            urlParams.append(key + "=" + params.get(key) + "&");
        }

        String url = "";
        String outcomeVal = WebUtils.decodePath((String) getTagValue(outcome));
        if (outcomeVal != null) {
            url = (outcomeVal.startsWith("/") ? outcomeVal.replaceFirst("/", "") : outcomeVal)
                    + urlParams.substring(0, urlParams.length() -1);
        }

        String href = "#";
        if (action == null && !url.isEmpty()) {
            href = (!url.startsWith("http") && !url.startsWith("mailto") && !url.startsWith("#") ?
                    getRequest().getContextPath() + "/" : "") + url;
            a.addAttribute("href", href);
        } else {
            appendDocScript(getFunction());
        }
        return set;
    }

    private StringBuilder getFunction() {
        Ajax jsonAjax = new Ajax();
        jsonAjax.setId(id);
        jsonAjax.setTag("dropaction");
        jsonAjax.setValidate(!skipValidation);

        // Params must be considered regardless the action for rest purpose
        for (String name : params.keySet()) {
            jsonAjax.addParam(new Param(name, params.get(name)));
        }

        if (action != null) {
            jsonAjax.setMethod("post");
            jsonAjax.setAction(getTagName(J_SBMT, action));

            if (!args.isEmpty()) {
                String argName = getTagName(J_SBMT_ARGS, action);
                for (Object arg : args.keySet()) {
                    jsonAjax.addArg(new Param(argName, arg, args.get(arg)));
                }
            }
        } else if (update != null) {
            jsonAjax.setMethod("get");
        }
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
        return getBindFunction(id, Event.CLICK.name(), builder);
    }

    @Override
    protected void checkAnnotatedAction() {
        BeanHandler.AnnotatedAction annotatedAction = getAnnotatedAction(id);
        if (annotatedAction != null) {
            action = annotatedAction.getBeanMethod();
            skipValidation = annotatedAction.isSkipValidation();

            if (StringUtils.isNotBlank(annotatedAction.getBeforeSend())) {
                beforeSend = annotatedAction.getBeforeSend();
            }
            if (StringUtils.isNotBlank(annotatedAction.getOnSuccess())) {
                onSuccess = annotatedAction.getOnSuccess();
            }
            if (StringUtils.isNotBlank(annotatedAction.getOnComplete())) {
                onComplete = annotatedAction.getOnComplete();
            }
            if (StringUtils.isNotBlank(annotatedAction.getOnError())) {
                onError = annotatedAction.getOnError();
            }
            if (StringUtils.isNotBlank(annotatedAction.getUpdate())) {
                update = annotatedAction.getUpdate();
            }
            setArgs(annotatedAction.getArguments());
        }
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setDivider(boolean divider) {
        this.divider = divider;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public void setAction(String action) {
        this.action = action;
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

    public void setSkipValidation(boolean skipValidation) {
        this.skipValidation = skipValidation;
    }
}
