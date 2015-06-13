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

import javax.servlet.ServletContext;

import com.jsmart5.framework.annotation.SmartListener;

/**
 * This interface is used to notify the application about {@link ServletContext} 
 * initialization and its destruction, so the application can execute any steps needed
 * after the context is initialized and before it is destroyed.
 * <br>
 * The class implementing this interface must be annotated with {@link SmartListener} annotation.
 */
public interface WebContextListener {

	/**
	 * It indicates that the context of application has been initialized.
	 * 
	 * @param context {@link ServletContext} object to the application.
	 */
	public void contextInitialized(final ServletContext context);

	/**
	 * It indicates that the context of application has been destroyed.
	 * 
	 * @param context {@link ServletContext} object to the application.
	 */
	public void contextDestroyed(final ServletContext context);

}
