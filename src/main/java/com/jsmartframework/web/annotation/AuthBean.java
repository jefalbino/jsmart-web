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
 * This annotation is used on class to provide authentication of type {@link AuthType} for session
 * or request mechanism.
 * <br>
 * If using the session type the class annotated with this annotation must implement {@link java.io.Serializable}
 * and will be stored on {@link javax.servlet.http.HttpSession}.
 * <br>
 * Also the class must provide one or more fields annotated with {@link AuthField} and one or more
 * methods annotated with {@link AuthMethod}.
 * If using the request type, the fields annotated with {@link AuthField} will be encrypted and set as
 * cookies to be carried on further requests.
 * <br>
 * In both cases the fields annotated with {@link AuthField} must be used by {@link AuthMethod} to validate if
 * client is authenticated. If those fields are set as {@value null} it means that it is not authenticated
 * and the secure URL Patterns cannot be accessed and any tentative of doing that will be redirected to login
 * page specified via {@code loginPath} attribute on this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthBean {

    /**
     * The name of the bean that can be mapped on JSP files.
     * <br>
     * Default value is the name of the class in camel case.
     */
    String name() default "";

    /**
     * Type of authentication mechanism.
     * <br>
     * In case type of request, the fields annotated with {@link AuthField} will be set as encrypted cookies.
     * In case type of session the entire class will be store on session.
     */
    AuthType type() default AuthType.REQUEST;

    /**
     * Secret key used to encrypt the fields annotated with {@link AuthField}. We recommend you
     * to define your own private secret key which must contain 16 characters.
     */
    String secretKey() default "";

    /**
     * The mapped path on configuration file {@code webConfig.xml} to specify the login
     * path of the application in case client is not authenticated.
     */
    String loginPath();

    /**
     * The mapped path on configuration file {@code webConfig.xml} to specify the home
     * path of the application in case client is authenticated.
     */
    String homePath();

}
