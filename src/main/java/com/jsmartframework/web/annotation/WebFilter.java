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

import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebInitParam;

/**
 * This annotation is used on classes to declare {@link javax.servlet.Filter}
 * instance. The class must implement {@link javax.servlet.Filter} interface.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface WebFilter {

    /**
     * The name of the filter.
     */
    String name() default "";

    /**
     * The order in which this filter will be called before Servlet execution.
     * The order is also relative to other filters declared in your application.
     */
    int order();

    /**
     * The URL patterns to which the filter applies.
     */
    String[] urlPatterns() default {"/*"};

    /**
     * The dispatcher types to which the filter applies
     */
    DispatcherType[] dispatcherTypes() default {DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.INCLUDE, DispatcherType.ASYNC};

    /**
     * The init parameters of the filter.
     */
    WebInitParam[] initParams() default {};

    /**
     * Declares whether the filter supports asynchronous operation mode.
     */
    boolean asyncSupported() default true;

}
