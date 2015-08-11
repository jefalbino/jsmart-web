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

/**
 * {@link ScopeType} enumerator is used as attribute of {@link WebBean} annotation
 * to indicate the scope of the bean on the application.
 */
public enum ScopeType {

	/**
	 * Indicates that the bean is on request scope, i.e., the bean instance is
	 * kept only during the request processing.
	 */
	REQUEST_SCOPE, 
	
	/**
	 * Indicates that the bean is on session scope, i.e., the bean instance is kept 
	 * as long as the session is alive.
	 */
	SESSION_SCOPE, 
	
	/**
	 * Indicates that the bean is on page scope, i.e., the bean instance is kept as
	 * long as the mapped URL keep being accessed.
	 */
	PAGE_SCOPE, 
	
	/**
	 * Indicated that the bean is on application scope, i.e., the bean instance is kept
	 * as long as the application is alive.
	 */
	APPLICATION_SCOPE;

}
