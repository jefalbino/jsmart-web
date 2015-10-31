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
 * Annotation to specify bean class to be able to send asynchronous events via
 * Server Sent Events to client. The class annotated with this annotation must
 * implement the interface {@link com.jsmartframework.web.listener.WebAsyncListener}
 * <br>
 * This annotation is also created to work along with {@code async} component or
 * {@code EventSource} object from JavaScript
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncBean {

    /**
     * Specify the relative URL path for the asynchronous event. This path also must be
     * specified as URL pattern tag on configuration file {@code webConfig.xml}
     * <br>
     * @return relative string path to return asynchronous events.
     */
    String value();

}
