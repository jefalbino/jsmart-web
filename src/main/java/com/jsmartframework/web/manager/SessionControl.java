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

package com.jsmartframework.web.manager;

import static com.jsmartframework.web.config.Config.CONFIG;
import static com.jsmartframework.web.manager.BeanHandler.HANDLER;

import com.jsmartframework.web.config.Constants;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public final class SessionControl implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        synchronized (session) {
            session.setAttribute(Constants.SESSION_RESET_ATTR, "#");
            HANDLER.instantiateAuthBean(session);

            if (CONFIG.getContent().getSessionTimeout() > 0) {
                session.setMaxInactiveInterval(CONFIG.getContent().getSessionTimeout() * 60);
            }

            for (HttpSessionListener sessionListener : HANDLER.sessionListeners) {
                HANDLER.executeInjection(sessionListener);
                sessionListener.sessionCreated(event);
            }
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        synchronized (session) {
            for (HttpSessionListener sessionListener : HANDLER.sessionListeners) {
                sessionListener.sessionDestroyed(event);
            }
            HANDLER.finalizeBeans(session);
        }
    }

}
