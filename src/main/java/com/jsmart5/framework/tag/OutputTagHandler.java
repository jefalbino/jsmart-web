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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.manager.SmartTagHandler;
import com.jsmart5.framework.tag.css3.Bootstrap;
import com.jsmart5.framework.tag.html.Span;
import com.jsmart5.framework.tag.html.Tag;

public final class OutputTagHandler extends SmartTagHandler {
	
	private static final String LEFT = "left";

	private static final String RIGHT = "left";
	
	private static final String JUSTIFY = "justify";
	
	private static final String CENTER = "center";

	
	private static final String LOWERCASE = "lowercase";
	
	private static final String UPPERCASE = "uppercase";
	
	private static final String CAPITALIZE = "capitalize";


	private static final String LEGEND = "legend";

	private static final String BOLD = "bold";

	private static final String MARK = "mark";

	private static final String ITALIC = "italic";

	private static final String SMALL = "small";

	private static final String LABEL = "label";

	private static final String OUTPUT = "output";
	
	private static final String DELETED = "delete";
	
	private static final String STRIKE_THROUGH = "strike";
	
	private static final String INSERTED = "insert";
	
	private static final String UNDERLINED = "underline";
	
	private static final String PARAGRAPH = "paragraph";

	private static final String H1 = "h1";
	
	private static final String H2 = "h2";
	
	private static final String H3 = "h3";
	
	private static final String H4 = "h4";
	
	private static final String H5 = "h5";
	
	private static final String H6 = "h6";


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
		if (type != null && !LEGEND.equalsIgnoreCase(type) && !BOLD.equalsIgnoreCase(type) && !MARK.equalsIgnoreCase(type)  && !ITALIC.equalsIgnoreCase(type) 
				&& !SMALL.equalsIgnoreCase(type) && !LABEL.equalsIgnoreCase(type) && !OUTPUT.equalsIgnoreCase(type) && !DELETED.equalsIgnoreCase(type) 
				&& !STRIKE_THROUGH.equalsIgnoreCase(type) && !INSERTED.equalsIgnoreCase(type) && !UNDERLINED.equalsIgnoreCase(type) && !PARAGRAPH.equalsIgnoreCase(type)
				&& !H1.equalsIgnoreCase(type) && !H2.equalsIgnoreCase(type) && !H3.equalsIgnoreCase(type) && !H4.equalsIgnoreCase(type) && !H5.equalsIgnoreCase(type) 
				&& !H6.equalsIgnoreCase(type)) {
			throw new JspException("Invalid type value for output tag. Valid values are "
					+ LEGEND + ", " + BOLD + ", " + MARK + ", " + ITALIC + ", " + SMALL + ", " + LABEL + ", " + OUTPUT + ", "
					+ DELETED + ", " + STRIKE_THROUGH + ", " + INSERTED + ", " + UNDERLINED + ", " + PARAGRAPH + ", "
					+ H1 + ", " + H2 + ", " + H3 + ", " + H4 + ", " + H5 + ", " + H6);
		}
		if (align != null && !align.equalsIgnoreCase(LEFT) && !align.equalsIgnoreCase(RIGHT) && !align.equalsIgnoreCase(CENTER) && !align.equalsIgnoreCase(JUSTIFY)) {
			throw new JspException("Invalid align value for output tag. Valid values are " + LEFT + ", " + RIGHT + ", " + CENTER + ", " + JUSTIFY);
		}
		if (transform != null && !transform.equalsIgnoreCase(CAPITALIZE) && !transform.equalsIgnoreCase(LOWERCASE) && !transform.equalsIgnoreCase(UPPERCASE)) {
			throw new JspException("Invalid transform value for output tag. Valid values are " + CAPITALIZE + ", " + LOWERCASE + ", " + UPPERCASE);
		}
		if (look != null && !look.equalsIgnoreCase(DEFAULT) && !look.equalsIgnoreCase(PRIMARY) && !look.equalsIgnoreCase(SUCCESS)
				&& !look.equalsIgnoreCase(INFO) && !look.equalsIgnoreCase(WARNING) && !look.equalsIgnoreCase(DANGER) && !look.equalsIgnoreCase(MUTED)) {
			throw new JspException("Invalid look value for output tag. Valid values are " + DEFAULT + ", " + PRIMARY 
					+ ", " + SUCCESS + ", " + INFO + ", " + WARNING + ", " + DANGER + ", " + MUTED);
		}
	}

	@Override
	@SuppressWarnings("all")
	public Tag executeTag() throws JspException, IOException {

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}
		
		if (!ajaxTags.isEmpty() && id == null) {
			id = getRandonId();
		}

		Tag tag = null;

		if (LEGEND.equals(type)) {
			tag =  new Tag("legend");
		} else if (BOLD.equals(type)) {
			tag =  new Tag("strong");
		} else if (MARK.equals(type)) {
			tag =  new Tag("mark");
		} else if (ITALIC.equals(type)) {
			tag =  new Tag("em");
		} else if (SMALL.equals(type)) {
			tag =  new Tag("small");
		} else if (LABEL.equals(type)) {
			tag =  new Tag("label");
		} else if (OUTPUT.equals(type)) {
			tag =  new Tag("output");
		} else if (DELETED.equals(type)) {
			tag =  new Tag("del");
		} else if (STRIKE_THROUGH.equals(type)) {
			tag =  new Tag("s");
		} else if (INSERTED.equals(type)) {
			tag =  new Tag("ins");
		} else if (UNDERLINED.equals(type)) {
			tag =  new Tag("u");
		} else if (PARAGRAPH.equals(type)) {
			tag =  new Tag("p");
		} else if (H1.equals(type)) {
			tag =  new Tag("h1");
		} else if (H2.equals(type)) {
			tag =  new Tag("h2");
		} else if (H3.equals(type)) {
			tag =  new Tag("h3");
		} else if (H4.equals(type)) {
			tag =  new Tag("h4");
		} else if (H5.equals(type)) {
			tag =  new Tag("h5");
		} else if (H6.equals(type)) {
			tag =  new Tag("h6");
		} else {
			tag =  new Span();
		}

		tag.addAttribute("id", id)
			.addAttribute("style", style);

		if (CAPITALIZE.equalsIgnoreCase(transform)) {
			tag.addAttribute("class", Bootstrap.TEXT_CAPITALIZE);
		} else if (UPPERCASE.equalsIgnoreCase(transform)) {
			tag.addAttribute("class", Bootstrap.TEXT_UPPERCASE);
		} else if (LOWERCASE.equalsIgnoreCase(transform)) {
			tag.addAttribute("class", Bootstrap.TEXT_LOWERCASE);
		}
		
		if (LEFT.equalsIgnoreCase(align)) {
			tag.addAttribute("class", Bootstrap.TEXT_LEFT);
		} else if (RIGHT.equalsIgnoreCase(align)) {
			tag.addAttribute("class", Bootstrap.TEXT_RIGHT);
		} else if (CENTER.equalsIgnoreCase(align)) {
			tag.addAttribute("class", Bootstrap.TEXT_CENTER);
		} else if (JUSTIFY.equalsIgnoreCase(align)) {
			tag.addAttribute("class", Bootstrap.TEXT_JUSTIFY);
		}
		
		if (PRIMARY.equalsIgnoreCase(look)) {
			tag.addAttribute("class", Bootstrap.TEXT_PRIMARY);
		} else if (SUCCESS.equalsIgnoreCase(look)) {
			tag.addAttribute("class", Bootstrap.TEXT_SUCCESS);
		} else if (INFO.equalsIgnoreCase(look)) {
			tag.addAttribute("class", Bootstrap.TEXT_INFO);
		} else if (WARNING.equalsIgnoreCase(look)) {
			tag.addAttribute("class", Bootstrap.TEXT_WARNING);
		} else if (DANGER.equalsIgnoreCase(look)) {
			tag.addAttribute("class", Bootstrap.TEXT_DANGER);
		} else if (MUTED.equalsIgnoreCase(look)) {
			tag.addAttribute("class", Bootstrap.TEXT_MUTED);
		}

		JspTag parent = getParent();
		if (parent instanceof RowTagHandler) {
			tag.addAttribute("class", Bootstrap.LIST_GROUP_ITEM_TEXT);
		}

		// Add the style class at last
		tag.addAttribute("class", styleClass);

		if (target != null && (LABEL.equals(type) || OUTPUT.equals(type))) {
			tag.addAttribute("for", target);
		}
		
		for (IconTagHandler iconTag : iconTags) {
			if (IconTagHandler.LEFT.equalsIgnoreCase(iconTag.getSide())) {
				tag.addTag(iconTag.executeTag());
			}
		}

		Object obj = getTagValue(value);
		if (format != null) {
			tag.addText(format.formatValue(obj));

		} else if (obj != null) {
			String text = obj.toString();

			if (length != null && length > 0 && text.length() >= length) {
				if (ellipsize && length > 4) {
					text = text.substring(0, length - 4) + " ...";
				} else {
					text = text.substring(0, length);
				}
			}
			tag.addText(text);
		}

		for (IconTagHandler iconTag : iconTags) {
			if (IconTagHandler.RIGHT.equalsIgnoreCase(iconTag.getSide())) {
				tag.addTag(iconTag.executeTag());
			}
		}

		if (!ajaxTags.isEmpty()) {
			for (AjaxTagHandler ajax : ajaxTags) {
				appendScript(ajax.getFunction(id));
			}
		}
		
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
