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

package com.jsmart5.framework.tag;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.xml.soap.Text;

import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Align;
import com.jsmart5.framework.tag.type.Case;
import com.jsmart5.framework.tag.type.Look;
import com.jsmart5.framework.tag.type.Output;

public final class OutputTagHandler extends TagHandler {

	private Object value;
	
	private String align;

	private String transform;
	
	private String look;

	private Integer length;

	private boolean ellipsize;

	private String type;

	private String target;
	
	private FormatTagHandler format;

	@Override
	public void validateTag() throws JspException {
		if (type != null && !Output.validate(type)) {
			throw InvalidAttributeException.fromPossibleValues("output", "type", Output.getValues());
		}
		if (align != null && !Align.validate(align)) {
			throw InvalidAttributeException.fromPossibleValues("output", "align", Align.getValues());
		}
		if (transform != null && !Case.validate(transform)) {
			throw InvalidAttributeException.fromPossibleValues("output", "transform", Case.getValues());
		}
		if (look != null && !Look.validateText(look) && !isEL(look)) {
			throw InvalidAttributeException.fromPossibleValues("output", "look", Look.getTextValues());
		}
	}

	@Override
	@SuppressWarnings("all")
	public Tag executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
        StringWriter writer = new StringWriter();
		if (body != null) {
			body.invoke(writer);
		}
		
		setRandomId("output");

		Tag tag = null;

		if (Output.LEGEND.equalsIgnoreCase(type)) {
			tag =  new Tag("legend");
		} else if (Output.STRONG.equalsIgnoreCase(type)) {
			tag =  new Tag("strong");
		} else if (Output.MARK.equalsIgnoreCase(type)) {
			tag =  new Tag("mark");
		} else if (Output.EM.equalsIgnoreCase(type)) {
			tag =  new Tag("em");
		} else if (Output.SMALL.equalsIgnoreCase(type)) {
			tag =  new Tag("small");
		} else if (Output.LABEL.equalsIgnoreCase(type)) {
			tag =  new Tag("label");
		} else if (Output.OUTPUT.equalsIgnoreCase(type)) {
			tag =  new Tag("output");
		} else if (Output.DEL.equalsIgnoreCase(type)) {
			tag =  new Tag("del");
		} else if (Output.S.equalsIgnoreCase(type)) {
			tag =  new Tag("s");
		} else if (Output.INS.equalsIgnoreCase(type)) {
			tag =  new Tag("ins");
		} else if (Output.U.equalsIgnoreCase(type)) {
			tag =  new Tag("u");
		} else if (Output.P.equalsIgnoreCase(type)) {
			tag =  new Tag("p");
		} else if (Output.H1.equalsIgnoreCase(type)) {
			tag =  new Tag("h1");
		} else if (Output.H2.equalsIgnoreCase(type)) {
			tag =  new Tag("h2");
		} else if (Output.H3.equalsIgnoreCase(type)) {
			tag =  new Tag("h3");
		} else if (Output.H4.equalsIgnoreCase(type)) {
			tag =  new Tag("h4");
		} else if (Output.H5.equalsIgnoreCase(type)) {
			tag =  new Tag("h5");
		} else if (Output.H6.equalsIgnoreCase(type)) {
			tag =  new Tag("h6");
		} else {
			tag =  new Span();
		}

		tag.addAttribute("style", getTagValue(style));

		appendRefId(tag, id);
        appendEvent(tag);

		if (Case.CAPITALIZE.equalsIgnoreCase(transform)) {
			tag.addAttribute("class", Bootstrap.TEXT_CAPITALIZE);
		} else if (Case.UPPERCASE.equalsIgnoreCase(transform)) {
			tag.addAttribute("class", Bootstrap.TEXT_UPPERCASE);
		} else if (Case.LOWERCASE.equalsIgnoreCase(transform)) {
			tag.addAttribute("class", Bootstrap.TEXT_LOWERCASE);
		}
		
		if (Align.LEFT.equalsIgnoreCase(align)) {
			tag.addAttribute("class", Bootstrap.TEXT_LEFT);
		} else if (Align.RIGHT.equalsIgnoreCase(align)) {
			tag.addAttribute("class", Bootstrap.TEXT_RIGHT);
		} else if (Align.CENTER.equalsIgnoreCase(align)) {
			tag.addAttribute("class", Bootstrap.TEXT_CENTER);
		} else if (Align.JUSTIFY.equalsIgnoreCase(align)) {
			tag.addAttribute("class", Bootstrap.TEXT_JUSTIFY);
		}
		
		String lookVal = (String) getTagValue(look);
		
		if (Look.PRIMARY.equalsIgnoreCase(lookVal)) {
			tag.addAttribute("class", Bootstrap.TEXT_PRIMARY);
		} else if (Look.SUCCESS.equalsIgnoreCase(lookVal)) {
			tag.addAttribute("class", Bootstrap.TEXT_SUCCESS);
		} else if (Look.INFO.equalsIgnoreCase(lookVal)) {
			tag.addAttribute("class", Bootstrap.TEXT_INFO);
		} else if (Look.WARNING.equalsIgnoreCase(lookVal)) {
			tag.addAttribute("class", Bootstrap.TEXT_WARNING);
		} else if (Look.DANGER.equalsIgnoreCase(lookVal)) {
			tag.addAttribute("class", Bootstrap.TEXT_DANGER);
		} else if (Look.MUTED.equalsIgnoreCase(lookVal)) {
			tag.addAttribute("class", Bootstrap.TEXT_MUTED);
		}

		JspTag parent = getParent();
		if (parent instanceof RowTagHandler) {
			tag.addAttribute("class", Bootstrap.LIST_GROUP_ITEM_TEXT);
		}

		// Add the style class at last
		tag.addAttribute("class", getTagValue(styleClass));

		if (target != null && (Output.LABEL.equalsIgnoreCase(type) || Output.OUTPUT.equalsIgnoreCase(type))) {
			tag.addAttribute("for", getTagValue(target));
		}

        String text = writer.toString();
        if (!params.isEmpty() && !text.trim().isEmpty()) {
            text = TextTagHandler.formatText(text, params);
        }

        // Add inner text before the text in the value
        tag.addText(text);

		Object obj = getTagValue(value);
		if (format != null) {
			tag.addText(format.formatValue(obj));

		} else if (obj != null) {
			text = obj.toString();

            if (!params.isEmpty() && !text.trim().isEmpty()) {
                text = TextTagHandler.formatText(text, params);
            }

			if (length != null && length > 0 && text.length() >= length) {
				if (ellipsize && length > 4) {
					text = text.substring(0, length - 4) + " ...";
				} else {
					text = text.substring(0, length);
				}
			}
			tag.addText(text);
		}

		appendAjax(id);
		appendBind(id);
		
		appendTooltip(tag);
		appendPopOver(tag);
		
		return tag;
	}
	
	void setFormat(FormatTagHandler format) {
		this.format = format;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public void setEllipsize(boolean ellipsize) {
		this.ellipsize = ellipsize;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public void setLook(String look) {
		this.look = look;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setTransform(String transform) {
		this.transform = transform;
	}

}
