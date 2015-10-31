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

import javax.servlet.annotation.WebInitParam;

/**
 * This annotation is used on classes to declare {@link javax.servlet.Servlet} instance.
 * The class must extends {@link javax.servlet.http.HttpServlet}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface WebServlet {

    /**
     * The name of the servlet.
     */
    String name() default "";

    /**
     * The URL patterns of the servlet.
     */
    String[] urlPatterns();

    /**
     * The init parameters of the servlet.
     */
    WebInitParam[] initParams() default {};

    /**
     * The load-on-startup order of the servlet.
     */
    int loadOnStartup() default -1;

    /**
     * Declares whether the servlet supports asynchronous operation mode.
     */
    boolean asyncSupported() default false;

}
