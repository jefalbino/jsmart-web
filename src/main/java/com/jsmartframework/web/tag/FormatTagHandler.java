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
import com.jsmartframework.web.tag.html.Tag;
import com.jsmartframework.web.tag.type.Type;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

public final class FormatTagHandler extends TagHandler {

    private String type;

    private String regex;

    @Override
    public void validateTag() throws JspException {
        JspTag parent = getParent();

        if (!(parent instanceof DateTagHandler) && !Type.validateFormat(type)) {
            throw InvalidAttributeException.fromPossibleValues("format", "type", Type.getFormatValues());
        }
    }

    @Override
    public boolean beforeTag() throws JspException, IOException {
        JspTag parent = getParent();

        if (parent instanceof OutputTagHandler) {
            ((OutputTagHandler) parent).setFormat(this);

        } else if (parent instanceof DateTagHandler) {
            ((DateTagHandler) parent).setFormat(this);
        }
        return false;
    }

    @Override
    public Tag executeTag() throws JspException, IOException {
        // DO NOTHING
        return null;
    }

    String formatValue(final Object value) {
        if (value != null) {
            if (Type.NUMBER.equalsIgnoreCase(type)) {
                return new DecimalFormat(regex).format(value);

            } else if (Type.DATE.equalsIgnoreCase(type)) {
                if (value instanceof Date) {
                    return new SimpleDateFormat(regex, getRequest().getLocale()).format(value);

                } else if (value instanceof DateTime) {
                    return ((DateTime) value).toString(DateTimeFormat.forPattern(regex).withLocale(getRequest().getLocale()));
                }
            }
            return value.toString();
        }
        return null;
    }

    public void setType(String type) {
        this.type = type;
    }

    String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

}
