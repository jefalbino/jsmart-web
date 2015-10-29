/*
 * JSmart Framework - Java Web Development Framework
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

package com.jsmartframework.web.adapter;

public final class SlideAdapter {

    private ImageAdapter image;

    private HeaderAdapter header;

    private boolean active;

    private String label;

    public ImageAdapter getImage() {
        return image;
    }

    public SlideAdapter setImage(ImageAdapter image) {
        this.image = image;
        return this;
    }

    public HeaderAdapter getHeader() {
        return header;
    }

    public SlideAdapter setHeader(HeaderAdapter header) {
        this.header = header;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public SlideAdapter setActive(boolean active) {
        this.active = active;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public SlideAdapter setLabel(String label) {
        this.label = label;
        return this;
    }

}
