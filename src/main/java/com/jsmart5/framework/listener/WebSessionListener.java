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

package com.jsmart5.framework.listener;

import javax.servlet.http.HttpSession;

import com.jsmart5.framework.annotation.SmartListener;

/**
 * This interface is used to notify the application about {@link HttpSession} 
 * creation and its destruction, so the application can execute any steps needed
 * after the session is created and before it is destroyed.
 * <br>
 * The class implementing this interface must be annotated with {@link SmartListener} annotation.
 */
public interface WebSessionListener {

	/**
	 * It indicates that the session of specific client is created.
	 * 
	 * @param session {@link HttpSession} object
	 */
	public void sessionCreated(final HttpSession session);

	/**
	 * It indicates that the session of specific client has been destroyed.
	 * 
	 * @param session {@link HttpSession} object
	 */
	public void sessionDestroyed(final HttpSession session);

}
