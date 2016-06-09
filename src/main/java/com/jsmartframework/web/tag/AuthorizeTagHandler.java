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

import com.jsmartframework.web.manager.TagHandler;
import com.jsmartframework.web.tag.html.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;


public final class AuthorizeTagHandler extends TagHandler {

    private List<WhenTagHandler> whens;

    private OtherwiseTagHandler otherwise;

    public AuthorizeTagHandler() {
        whens = new ArrayList<>();
    }

    @Override
    public void validateTag() throws JspException {
        // DO NOTHING
    }

    @Override
    public Tag executeTag() throws JspException, IOException {

        // Execute body to get whens and otherwise
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }

        Collection<String> userAccess = getUserAuthorizationAccess();

        if (userAccess != null && !userAccess.isEmpty()) {
            for (WhenTagHandler when : whens) {
                if (isDeniedAccess(userAccess, when.getDeny())) {
                    return when.executeTag();
                }
                if (isGrantedAccess(userAccess, when.getGrant())) {
                    return when.executeTag();
                }
            }
        }

        if (otherwise != null)  {
            return otherwise.executeTag();
        }
        return null;
    }

    private boolean isDeniedAccess(Collection<String> userAccess, List<String> denyList) {
        if (denyList == null || denyList.isEmpty()) {
            return false;
        }
        boolean denied = true;

        for (String deny : denyList) {
            denied &= !userAccess.contains(deny.trim());
        }
        return denied;
    }

    private boolean isGrantedAccess(Collection<String> userAccess, List<String> grantList) {
        if (grantList == null || grantList.isEmpty()) {
            return false;
        }
        for (String grant : grantList) {
            if (userAccess.contains(grant.trim())) {
                return true;
            }
        }
        return false;
    }

    void addWhen(WhenTagHandler when) {
        this.whens.add(when);
    }

    void setOtherwise(OtherwiseTagHandler otherwise) {
        this.otherwise = otherwise;
    }

}
