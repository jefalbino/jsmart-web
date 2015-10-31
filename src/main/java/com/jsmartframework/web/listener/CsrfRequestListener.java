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

package com.jsmartframework.web.listener;

import com.jsmartframework.web.adapter.CsrfAdapter;

/**
 * Listener to provide token name and value to prevent CSRF attacks.
 * This listener is meant to work together with {@link com.jsmartframework.web.annotation.WebSecurity}
 * to provide request authentication.
 */
public interface CsrfRequestListener {

    /**
     * This method is called every GET to provide token name and value per customer.
     * <br>
     * We recommend that the token is regenerated from time to time and also you should
     * manage its storage if using session or another external resource.
     *
     * @return CsrfAdapter containing token name and value
     */
    public CsrfAdapter generateToken();

    /**
     * This method is called every POST and it carries the token name and value for
     * request validation.
     *
     * @param csrfAdapter - CsrfAdapter containing token name and value
     *
     * @return true if request is valid false otherwise
     */
    public boolean isValidToken(CsrfAdapter csrfAdapter);

}
