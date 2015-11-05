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

package com.jsmartframework.web.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used on classes responsible to validate the request by
 * implementing the interface {@link com.jsmartframework.web.listener.CsrfRequestListener}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface WebSecurity {

    /**
     * Secret key used to encrypt CSRF name and token. We recommend you
     * to define your own private secret key which must contain 16 characters.
     */
    String secretKey() default "4zK7koRONFkbtRK6";

    /**
     * Boolean value to disable CSRF token encryption, so if it is true the token
     * name and value will be carried on as text plain, otherwise they will be encrypted.
     */
    boolean disableEncrypt() default false;

}
