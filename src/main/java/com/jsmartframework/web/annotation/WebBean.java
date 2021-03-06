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
 * This annotation is used on a class that contains the business
 * logic and can be used as a bean object to be mapped on JSP files.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface WebBean {

    /**
     * The name of the bean that can be mapped on JSP files.
     * <br>
     * Default value is the name of the class in camel case.
     */
    String name() default "";

    /**
     * The scope of the bean.
     */
    ScopeType scope() default ScopeType.REQUEST;

}
