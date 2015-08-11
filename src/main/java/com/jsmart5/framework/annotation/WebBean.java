/*
 * JSmart5 - Java Web Development Framework
 * Copyright (c) 2014, Jeferson Albino da Silva, All rights reserved.
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

package com.jsmart5.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@link WebBean} annotation is used on a class that contains the business
 * logic and can be used as a bean object to be mapped on JSP files.
 * <br>
 * The classes annotated with {@link WebBean} allow dependency injection.
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
	 * <br>
	 * Default value is ScopeType.REQUEST_SCOPE
	 */
	ScopeType scope() default ScopeType.REQUEST_SCOPE;

}
