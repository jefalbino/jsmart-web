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

package com.jsmartframework.web.listener;

import javax.servlet.AsyncContext;

/**
 * This listener is meant to work along with {@link com.jsmartframework.web.annotation.AsyncBean}
 * to provide asynchronous event from server to client via specified path.
 */
public interface WebAsyncListener {

    public static enum Reason {
        COMPLETE, TIMEOUT, ERROR
    }

    /**
     * This method is called when asynchronous context is created and you should use it
     * to start your own thread to start sending events to client via event stream that
     * is easely provided via {@link com.jsmartframework.web.manager.WebContext}.
     *
     * @param asyncContext - asynchronous context which hold the request and response
     */
    public void asyncContextCreated(AsyncContext asyncContext);

    /**
     * This method is called when asynchronous context is destroyed so you can
     * destroy any resource created and complete the asynchronous context
     *
     * @param asyncContext - asynchronous context which hold the request and response
     * @param reason - reason why asynchronous context was destroyed
     */
    public void asyncContextDestroyed(AsyncContext asyncContext, Reason reason);

}
