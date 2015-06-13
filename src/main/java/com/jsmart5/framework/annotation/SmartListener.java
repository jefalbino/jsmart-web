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

package com.jsmart5.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.jsmart5.framework.listener.WebContextListener;
import com.jsmart5.framework.listener.WebSessionListener;

/**
 * The {@link SmartListener} annotation is used on classes implementing {@link WebSessionListener}
 * or {@link WebContextListener} interfaces.
 * <br>
 * The classes annotated with {@link SmartListener} allow dependency injection.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SmartListener {

}
