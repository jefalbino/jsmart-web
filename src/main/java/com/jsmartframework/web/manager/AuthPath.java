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

class AuthPath {

    private String path;

    private boolean homePath;

    AuthPath(String path) {
        this(path, false);
    }

    AuthPath(String path, boolean homePath) {
        this.path = path;
        this.homePath = homePath;
    }

    String getPath() {
        return path;
    }

    boolean isHomePath() {
        return homePath;
    }

    boolean shouldRedirectFromPath(String path) {
        return this.path != null && !this.path.equals(path);
    }
}
