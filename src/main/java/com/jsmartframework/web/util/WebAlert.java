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

package com.jsmartframework.web.util;

/**
 * Utility class to provide alert information from server to client
 * via {@code alert} component. This class is meant to be used along
 * with {@link com.jsmartframework.web.manager.WebContext} to send feedback
 * to client side using specific alert by its id.
 */
public final class WebAlert {

    private AlertType type;

    private String title;

    private String titleIcon;

    private String message;

    private String messageUrl;

    public WebAlert(AlertType type) {
        this.type = type;
    }

    public WebAlert(AlertType type, String title, String message) {
        this.type = type;
        this.title = title;
        this.message = message;
    }

    public WebAlert(AlertType type, String title, String message, String titleIcon) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.titleIcon = titleIcon;
    }

    public AlertType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleIcon() {
        return titleIcon;
    }

    public void setTitleIcon(String titleIcon) {
        this.titleIcon = titleIcon;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageUrl() {
        return messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }

    public static enum AlertType {
        INFO, WARNING, DANGER, SUCCESS;
    }

}
