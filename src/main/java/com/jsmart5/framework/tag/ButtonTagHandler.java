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

import static com.jsmart5.framework.tag.JSConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.jsmart5.framework.json.JSONAjax;
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

	private List<ButtonActionTagHandler> actionItems;

	public ButtonTagHandler() {
		actionItems = new ArrayList<ButtonActionTagHandler>();
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
			builder.append(HtmlConstants.OPEN_DIV_TAG + CssConstants.CSS_BUTTON_GROUP + ">");
		}

		if (image != null) {
			builder.append(HtmlConstants.INPUT_TAG);
		} else {
			builder.append(HtmlConstants.OPEN_BUTTON_TAG);
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
				builder.append(CssConstants.CSS_BUTTON_IMAGE);
			} else {
				if (parentGroup) {
					if (!actionItems.isEmpty()) {
						builder.append(CssConstants.CSS_BUTTON_DEFAULT_GROUP_ITEM);
					} else {
						builder.append(CssConstants.CSS_BUTTON_GROUP_ITEM);
					}
				} else {
					if (!actionItems.isEmpty()) {
						builder.append(CssConstants.CSS_BUTTON_DEFAULT);
					} else {
						builder.append(CssConstants.CSS_BUTTON);
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

			JSONAjax jsonAjax = new JSONAjax();
			if (action != null) {
				jsonAjax.setMethod("post");
				jsonAjax.setAction(getTagName(J_SBMT, action));

			} else if (update != null) {
				jsonAjax.setMethod("get");
			}
			jsonAjax.setUpdate(update);
			jsonAjax.setBefore(beforeAjax);
			jsonAjax.setExec(afterAjax);

			builder.append("ajax=\"" + getJSONValue(jsonAjax) + "\" ");

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

		appendEventBuilder(builder);

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
			builder.append(">");

			if (icon != null) {
				builder.append(HtmlConstants.IMG_TAG + CssConstants.CSS_BUTTON_ICON);
				builder.append("src=\"" + icon + "\" alt=\"" + icon + "\" >");
			}

			builder.append(val != null ? val : "");
			builder.append(HtmlConstants.CLOSE_BUTTON_TAG);
		}

		if (!actionItems.isEmpty()) {
			
			builder.append(HtmlConstants.OPEN_BUTTON_TAG);
			
			if (parentGroup) {
				builder.append(CssConstants.CSS_BUTTON_DROPDOWN_GROUP_ITEM);
			} else {
				builder.append(CssConstants.CSS_BUTTON_DROPDOWN);
			}

			if (disabled) {
				builder.append("disabled=\"disabled\" ");
			}

			builder.append(ON_CLICK + JSMART_BUTTON_DROPDOWN.format("$(this)") + "return false;\" ");

			builder.append("type=\"button\" >&nbsp;");
			builder.append(HtmlConstants.OPEN_DIV_TAG);
			builder.append(CssConstants.CSS_BUTTON_DROPDOWN_ARROW + ">");
			builder.append(HtmlConstants.CLOSE_DIV_TAG);
			builder.append("&nbsp;" + HtmlConstants.CLOSE_BUTTON_TAG);

			builder.append(HtmlConstants.OPEN_UNORDERED_LIST_TAG);
			builder.append(CssConstants.CSS_BUTTON_DROPDOWN_LIST + ">");

			for (ButtonActionTagHandler actionItem : actionItems) {
				builder.append(HtmlConstants.OPEN_LIST_ITEM_TAG + ">");
				builder.append(HtmlConstants.OPEN_LINK_TAG);

				builder.append(ON_CLICK + JSMART_BUTTON.format(async, "$(this)") + "return false;\" ");

				JSONAjax jsonAjax = new JSONAjax();
				jsonAjax.setMethod("post");
				jsonAjax.setAction(getTagName(J_SBMT, actionItem.getAction()));
				jsonAjax.setUpdate(update);
				jsonAjax.setBefore(beforeAjax);
				jsonAjax.setExec(afterAjax);
				builder.append("ajax=\"" + getJSONValue(jsonAjax) + "\" >");

				builder.append(getTagValue(actionItem.getLabel()));

				builder.append(HtmlConstants.CLOSE_LINK_TAG);
				builder.append(HtmlConstants.CLOSE_LIST_ITEM_TAG);
			}

			builder.append(HtmlConstants.CLOSE_UNORDERED_LIST_TAG);
			builder.append(HtmlConstants.CLOSE_DIV_TAG);
		}

		printOutput(builder);
	}

	/*package*/ void addActionItem(ButtonActionTagHandler actionItem) {
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