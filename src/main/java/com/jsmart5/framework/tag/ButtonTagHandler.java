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

import static com.jsmart5.framework.tag.HtmlConstants.*;
import static com.jsmart5.framework.tag.CssConstants.*;
import static com.jsmart5.framework.tag.JsConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

import com.jsmart5.framework.json.JsonAjax;
import com.jsmart5.framework.json.JsonParam;
import com.jsmart5.framework.manager.SmartTagHandler;

/*
 * Button uses a json structure
 * 
 * {
 * 	  'method': '',
 *    'action': '',
 *    'update': '',
 *    'before': '',
 *    'exec': ''
 *  }
 */
public final class ButtonTagHandler extends SmartTagHandler {

	private String label;

	private Integer length;

	private boolean ellipsize;

	private String image;

	private String icon;

	private String action;
	
	private boolean ajax;

	private String update;

	private String beforeAjax;

	private String afterAjax;

	private Integer tabIndex;

	private boolean reset;

	private boolean disabled;

	private boolean async = true;

	private List<SmartTagHandler> actionItems;

	public ButtonTagHandler() {
		actionItems = new ArrayList<SmartTagHandler>();
	}

	@Override
	public boolean beforeTag() throws JspException, IOException {
		JspTag parent = getParent();
		if (parent instanceof GridTagHandler) {

			((GridTagHandler) parent).addTag(this);
			return false;
		}
		return true;
	}

	@Override
	public void validateTag() throws JspException {
		// DO NOTHING
	}

	@Override
	public void executeTag() throws JspException, IOException {

		boolean parentGroup = getParent() instanceof ButtonGroupTagHandler;
		if (parentGroup && image != null) {
			throw new JspException("Attribute image and parent tag buttongroup cannot coexist for button tag");
		}

		if (parentGroup) {
			this.theme = ((ButtonGroupTagHandler) getParent()).getTheme();
		}

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		if (!actionItems.isEmpty() && image != null) {
			throw new JspException("Attribute image and internal tag buttonaction cannot coexist for button tag");
		}

		StringBuilder builder = new StringBuilder();	
		
		if (!actionItems.isEmpty()) {
			builder.append(OPEN_DIV_TAG);
			appendClass(builder, CSS_BUTTON_GROUP);
			builder.append(CLOSE_TAG);
		}

		if (image != null) {
			builder.append(INPUT_TAG);
		} else {
			builder.append(OPEN_BUTTON_TAG);
		}

		if (id != null) {
			builder.append("id=\"" + id + "\" ");
		}
		if (style != null) {
			builder.append("style=\"" + style + "\" ");
		}
		if (styleClass != null) {
			builder.append("class=\"" + styleClass + "\" ");
		} else {
			if (image != null) {
				appendClass(builder, CSS_BUTTON_IMAGE);
			} else {
				if (parentGroup) {
					if (!actionItems.isEmpty()) {
						appendClass(builder, CSS_BUTTON_DEFAULT_GROUP_ITEM);
					} else {
						appendClass(builder, CSS_BUTTON_GROUP_ITEM);
					}
				} else {
					if (!actionItems.isEmpty()) {
						appendClass(builder, CSS_BUTTON_DEFAULT);
					} else {
						appendClass(builder, CSS_BUTTON);
					}
				}
			}
		}

		if (tabIndex != null) {
			builder.append("tabindex=\"" + tabIndex + "\" ");
		}
		if (disabled || isEditRowTagEnabled()) {
			builder.append("disabled=\"disabled\" ");
		}

		if (ajax) {
			if (image != null) {
				builder.append("type=\"image\" src=\"" + image + "\" ");
			} else if (reset) {
				builder.append("type=\"reset\" ");
			} else {
				builder.append("type=\"button\" ");
			}

			builder.append(ON_CLICK + JSMART_BUTTON.format(async, "$(this)") + "return false;\" ");

			JsonAjax jsonAjax = new JsonAjax();
			if (action != null) {
				jsonAjax.setMethod("post");
				jsonAjax.setAction(getTagName(J_SBMT, action));
				
				for (String name : params.keySet()) {						
					jsonAjax.getParams().add(new JsonParam(name, params.get(name)));
				}
			} else if (update != null) {
				jsonAjax.setMethod("get");
			}
			if (update != null) {
				jsonAjax.setUpdate(update.trim());
			}
			if (beforeAjax != null) {
				jsonAjax.setBefore(beforeAjax.trim());
			}
			if (afterAjax != null) {
				jsonAjax.setExec(afterAjax.trim());
			}

			builder.append("ajax=\"" + getJsonValue(jsonAjax) + "\" ");

		} else {
			if (action != null) {
				builder.append("name=\"" + getTagName(J_SBMT, action) + "\" ");
			}

			if (afterAjax != null) {
				builder.append(ON_CLICK + JSMART_EXEC.format((beforeAjax != null ? beforeAjax + "," : "") + afterAjax) + "return false;\" ");

			} else if (beforeAjax != null) {
				builder.append(ON_CLICK + JSMART_EXEC.format(beforeAjax) + "return false;\" ");
			}

			if (image != null) {
				builder.append("type=\"image\" src=\"" + image + "\" ");
			} else if (action != null) {
				builder.append("type=\"submit\" ");
			} else if (reset) {
				builder.append("type=\"reset\" ");
			} else {
				builder.append("type=\"button\" ");
			}
		}

		appendEvent(builder);

		String val = (String) getTagValue(label);

		if (val != null && length != null && length > 0 && val.length() >= length) {
			if (ellipsize && length > 4) {
				val = val.substring(0, length - 4) + " ...";
			} else {
				val = val.substring(0, length);
			}
		}

		if (image != null) {
			builder.append((val != null ? "value=\"" + val + "\"" : "") + " />");
		} else {
			builder.append(CLOSE_TAG);

			if (icon != null) {
				builder.append(IMG_TAG);
				appendClass(builder, CSS_BUTTON_ICON);
				builder.append("src=\"" + icon + "\" alt=\"" + icon + "\" >");
			}

			builder.append(val != null ? val : "");
			builder.append(CLOSE_BUTTON_TAG);
		}

		if (!actionItems.isEmpty()) {
			
			builder.append(OPEN_BUTTON_TAG);
			
			if (parentGroup) {
				appendClass(builder, CSS_BUTTON_DROPDOWN_GROUP_ITEM);
			} else {
				appendClass(builder, CSS_BUTTON_DROPDOWN);
			}

			if (disabled) {
				builder.append("disabled=\"disabled\" ");
			}

			builder.append(ON_CLICK + JSMART_BUTTON_DROPDOWN.format("$(this)") + "return false;\" ");

			builder.append("type=\"button\" >&nbsp;");
			builder.append(OPEN_DIV_TAG);
			appendClass(builder, CSS_BUTTON_DROPDOWN_ARROW);
			builder.append(CLOSE_TAG);
			builder.append(CLOSE_DIV_TAG);
			builder.append("&nbsp;" + CLOSE_BUTTON_TAG);

			builder.append(OPEN_UNORDERED_LIST_TAG);
			appendClass(builder, CSS_BUTTON_DROPDOWN_LIST);
			builder.append(CLOSE_TAG);
	
			for (SmartTagHandler actionItem : actionItems) {
				
				if (actionItem instanceof SeparatorTagHandler) {
					builder.append(OPEN_LIST_ITEM_TAG);
					appendClass(builder, CSS_BUTTON_DROPDOWN_SEPARATOR);
					builder.append(CLOSE_TAG);
					builder.append(CLOSE_LIST_ITEM_TAG);

				} else if (actionItem instanceof ButtonActionTagHandler) {

					ButtonActionTagHandler buttonActionItem = (ButtonActionTagHandler) actionItem;

					builder.append(OPEN_LIST_ITEM_TAG + CLOSE_TAG);
					builder.append(OPEN_LINK_TAG);
	
					builder.append(ON_CLICK + JSMART_BUTTON.format(async, "$(this)") + "return false;\" ");
	
					JsonAjax jsonAjax = new JsonAjax();
					jsonAjax.setMethod("post");
					jsonAjax.setAction(getTagName(J_SBMT, buttonActionItem.getAction()));
	
					for (String name : buttonActionItem.getParams().keySet()) {						
						jsonAjax.getParams().add(new JsonParam(name, buttonActionItem.getParams().get(name)));
					}
	
					jsonAjax.setUpdate(update);
					jsonAjax.setBefore(beforeAjax);
					jsonAjax.setExec(afterAjax);
					builder.append("ajax=\"" + getJsonValue(jsonAjax) + "\" >");
	
					builder.append(getTagValue(buttonActionItem.getLabel()));
	
					builder.append(CLOSE_LINK_TAG);
					builder.append(CLOSE_LIST_ITEM_TAG);
				}
			}

			builder.append(CLOSE_UNORDERED_LIST_TAG);
			builder.append(CLOSE_DIV_TAG);
		}

		printOutput(builder);
	}

	void addActionItem(SmartTagHandler actionItem) {
		this.actionItems.add(actionItem);
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public void setEllipsize(boolean ellipsize) {
		this.ellipsize = ellipsize;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public void setBeforeAjax(String beforeAjax) {
		this.beforeAjax = beforeAjax;
	}

	public void setAfterAjax(String afterAjax) {
		this.afterAjax = afterAjax;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

}