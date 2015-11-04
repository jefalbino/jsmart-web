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
 * This annotation is used to map relative URL paths to be integrated with
 * SpringMVC which are part of same context of the application mapped via
 * configuration file {@code webConfig.xml}.
 * <br>
 * The url path mapped via this annotation do not need to be specified as
 * URL Pattern on {@code webConfig.xml} and it must end with /*
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestPath {

    /**
     * Relative URL path to map this request path with must end with /*
     */
    String value();

}
