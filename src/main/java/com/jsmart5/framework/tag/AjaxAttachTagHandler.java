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

import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.io.StringWriter;

import static com.jsmart5.framework.tag.js.JsConstants.JSMART_AJAX_ATTACH;

public class AjaxAttachTagHandler extends TagHandler {

    @Override
    public void validateTag() throws JspException {
        // DO NOTHING
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        StringWriter sw = new StringWriter();
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(sw);
        }

        Div div = new Div();
        div.addAttribute("id", id)
            .addAttribute("class", getTagValue(styleClass))
            .addAttribute("style", "display: none;")
            .addAttribute("style", getTagValue(style));

        div.addText(sw.toString());
        appendDocScript(new StringBuilder(JSMART_AJAX_ATTACH.format(id)));
        return div;
    }

}
