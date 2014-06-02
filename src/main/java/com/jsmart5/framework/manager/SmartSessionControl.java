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

package com.jsmart5.framework.manager;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import static com.jsmart5.framework.manager.SmartConfig.*;
import static com.jsmart5.framework.manager.SmartHandler.*;

public final class SmartSessionControl implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();

		session.setAttribute(SmartConstants.AJAX_RESET_ATTR, SmartConstants.SESSION_CREATED_FLAG);
		HANDLER.instantiateAuthBean(session);

		if (CONFIG.getContent().getSessionTimeout() > 0) {
			session.setMaxInactiveInterval(CONFIG.getContent().getSessionTimeout() * 60);
		}

		// Call registered SmartSessionListeners
		for (SmartSessionListener sessionListener : HANDLER.sessionListeners) {
			HANDLER.executeInjection(sessionListener);
			sessionListener.sessionCreated(session);
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();

		// Call registered SmartSessionListeners
		for (SmartSessionListener sessionListener : HANDLER.sessionListeners) {
			sessionListener.sessionDestroyed(session);
		}
		HANDLER.finalizeBeans(session);
	}

}
