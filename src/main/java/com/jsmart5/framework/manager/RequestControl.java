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

package com.jsmart5.framework.manager;

import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import static com.jsmart5.framework.manager.BeanHandler.HANDLER;

public class RequestControl implements ServletRequestListener {

    private RequestContextListener requestContextListener = new RequestContextListener();

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        requestContextListener.requestInitialized(event);

        for (ServletRequestListener requestListener : HANDLER.requestListeners) {
            HANDLER.executeInjection(requestListener);
            requestListener.requestInitialized(event);
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        requestContextListener.requestDestroyed(event);

        for (ServletRequestListener requestListener : HANDLER.requestListeners) {
            requestListener.requestDestroyed(event);
        }
    }
}
