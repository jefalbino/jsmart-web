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

import static com.jsmartframework.web.tag.js.JsConstants.JSMART_AJAX;
import static com.jsmartframework.web.tag.js.JsConstants.JSMART_FUNCTION_VAR;
import static com.jsmartframework.web.manager.BeanHandler.AnnotatedFunction;

import com.jsmartframework.web.annotation.Arg;
import com.jsmartframework.web.exception.InvalidAttributeException;
import com.jsmartframework.web.json.Ajax;
import com.jsmartframework.web.json.Param;
import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.manager.WebContext;
import com.jsmartframework.web.tag.html.Tag;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

public final class FunctionTagHandler extends TagHandler {

    private String name;

    private String action;

    private Integer timeout;

    private String update;

    private String beforeSend;

    private String onError;

    private String onSuccess;

    private String onComplete;

    private StringBuilder functionArgs;

    private StringBuilder functionVars;

    public FunctionTagHandler() {
        functionArgs = new StringBuilder();
        functionVars = new StringBuilder();
    }

    @Override
    public void validateTag() throws JspException {
        if (timeout != null && timeout < 0) {
            throw InvalidAttributeException.fromConstraint("function", "timeout", "greater or equal to 0");
        }
        if (action != null && action.trim().contains(" ")) {
            throw InvalidAttributeException.fromConflict("function", "action", "Value cannot contain space characters");
        }
    }

    @Override
    public Tag executeTag() throws JspException, IOException {
        // Just to call nested tags
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        Ajax jsonAjax = getJsonAjax(WebContext.getRequest(), id);
        StringBuilder builder = new StringBuilder(JSMART_AJAX.format(getJsonValue(jsonAjax)));
        appendFunction(getFunction(name, functionArgs, functionVars, builder));
        return null;
    }

    public void executeTag(HttpServletRequest httpRequest, AnnotatedFunction annotatedFunction) throws JspException, IOException {
        setRandomId("function");
        name = annotatedFunction.getFunctionName();
        action = annotatedFunction.getBeanMethod();
        timeout = annotatedFunction.getFunction().timeout();

        if (StringUtils.isNotBlank(annotatedFunction.getBeforeSend())) {
            beforeSend = annotatedFunction.getBeforeSend();
        }
        if (StringUtils.isNotBlank(annotatedFunction.getOnSuccess())) {
            onSuccess = annotatedFunction.getOnSuccess();
        }
        if (StringUtils.isNotBlank(annotatedFunction.getOnComplete())) {
            onComplete = annotatedFunction.getOnComplete();
        }
        if (StringUtils.isNotBlank(annotatedFunction.getOnError())) {
            onError = annotatedFunction.getOnError();
        }
        if (StringUtils.isNotBlank(annotatedFunction.getUpdate())) {
            update = annotatedFunction.getUpdate();
        }

        setArgs(annotatedFunction.getArguments());
        validateTag();

        Ajax jsonAjax = getJsonAjax(httpRequest, id);
        StringBuilder builder = new StringBuilder(JSMART_AJAX.format(getJsonValue(jsonAjax)));
        appendFunction(httpRequest, getFunction(name, functionArgs, functionVars, builder));
    }

    private Ajax getJsonAjax(HttpServletRequest httpRequest, String id) {
        Ajax jsonAjax = new Ajax();
        jsonAjax.setId(id);
        jsonAjax.setTimeout(timeout);
        jsonAjax.setTag("function");

        // Params must be considered regardless the action for rest purpose
        for (String name : params.keySet()) {
            jsonAjax.addParam(new Param(name, null));
        }

        if (action != null) {
            jsonAjax.setMethod("post");
            jsonAjax.setAction(getTagName(httpRequest, J_SBMT, action));

            if (!args.isEmpty()) {
                String name = getTagName(httpRequest, J_SBMT_ARGS, action);

                for (Object arg : args.keySet()) {
                    jsonAjax.addArg(new Param(name, arg, args.get(arg)));
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
        return jsonAjax;
    }

    public void appendFunctionArg(String functionArg) {
        if (functionArgs.length() != 0) {
            functionArgs.append(",");
        }
        functionArgs.append(functionArg);
        functionVars.append(JSMART_FUNCTION_VAR.format(functionArg, functionArg));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
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
