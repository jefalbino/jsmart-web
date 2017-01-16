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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used on methods which you want to be called directly via
 * JavaScript function on client side.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Function {

    /**
     * Name of JavaScript function to be invoked on client side
     */
    String name() default "";

    /**
     * Specifies the timeout in milliseconds to wait before Ajax request is performed.
     */
    int timeout() default 0;

    /**
     * Specifies the timeout in milliseconds for the Ajax request.
     */
    int requestTimeout() default 0;

    /**
     * List of url-pattern which the function will be mapped for
     * direct call via JavaScript function.
     */
    String[] forPaths();

    /**
     * Response type to be written automatically from method return.
     */
    ProduceType produces() default ProduceType.VOID;

    /**
     * List of component ids to be updated after function call succeed. Only works
     * if the return is HTML content, otherwise the update will not apply.
     */
    String[] update() default "";

    /**
     * JavaScript functions to be called before the Ajax request is made to server.
     * The function may receive parameters such as jqXHR(XmlHttpRequest) and settings(Object).
     * For more details about callback functions, check <a href="http://api.jquery.com/jquery.ajax">http://api.jquery.com/jquery.ajax</a>
     */
    String[] beforeSend() default "";

    /**
     * JavaScript functions to be called if the Ajax request returns successfully from server.
     * The function may receive parameters such as data(Object), textStatus(String) and jqXHR(XmlHttpRequest).
     * For more details about callback functions, check <a href="http://api.jquery.com/jquery.ajax">http://api.jquery.com/jquery.ajax</a>
     */
    String[] onSuccess() default "";

    /**
     * JavaScript functions to be called if the Ajax request returns error from server.
     * The function may receive parameters such as jqXHR(XmlHttpRequest), textStatus(String) and error(Error).
     * For more details about callback functions, check <a href="http://api.jquery.com/jquery.ajax">http://api.jquery.com/jquery.ajax</a>
     */
    String[] onError() default "";

    /**
     * JavaScript functions to be called when the Ajax request completes regardless error or success.
     * The function may receive parameters such as jqXHR(XmlHttpRequest) and textStatus(String).
     * For more details about callback functions, check <a href="http://api.jquery.com/jquery.ajax">http://api.jquery.com/jquery.ajax</a>
     */
    String[] onComplete() default "";
}
