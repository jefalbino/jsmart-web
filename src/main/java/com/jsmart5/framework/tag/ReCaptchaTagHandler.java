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

import com.jsmart5.framework.exception.ConstraintTagException;
import com.jsmart5.framework.exception.InvalidAttributeException;
import com.jsmart5.framework.manager.ReCaptchaHandler;
import com.jsmart5.framework.manager.TagHandler;
import com.jsmart5.framework.tag.css.Bootstrap;
import com.jsmart5.framework.tag.css.JSmart5;
import com.jsmart5.framework.tag.html.A;
import com.jsmart5.framework.tag.html.Div;
import com.jsmart5.framework.tag.html.Input;
import com.jsmart5.framework.tag.html.Label;
import com.jsmart5.framework.tag.html.Script;
import com.jsmart5.framework.tag.html.Tag;
import com.jsmart5.framework.tag.type.Align;
import com.jsmart5.framework.tag.type.Size;
import com.jsmart5.framework.tag.type.Type;

public final class ReCaptchaTagHandler extends TagHandler {

	private Integer version = ReCaptchaHandler.RECAPTCHA_V1;

	private String siteKey;

	private String label;

	private String audioLabel;

	private Integer tabIndex;

	private boolean autoFocus;
	
	private boolean disabled;

	private String placeHolder;

	private String locale;

	private String size;
	
	private String align = Align.RIGHT.name();
	
	@Override
	public void validateTag() throws JspException {
		if (version != null && !version.equals(ReCaptchaHandler.RECAPTCHA_V1) && !version.equals(ReCaptchaHandler.RECAPTCHA_V2)) {
			throw InvalidAttributeException.fromPossibleValues("recaptcha", "version", new String[]{"1", "2"});
		}
		if (size != null && !Size.validateSmallLarge(size)) {
			throw InvalidAttributeException.fromPossibleValues("recaptcha", "size", Size.getSmallLargeValues());
		}
		if (align != null && !Align.validateLeftRight(align)) {
			throw InvalidAttributeException.fromPossibleValues("recaptcha", "align", Align.getLeftRightValues());
		}
		if (ReCaptchaHandler.RECAPTCHA_V1.equals(version) && label == null) {
			throw InvalidAttributeException.fromConstraint("recaptcha", "label", "specified case version = 1");
		}
	}

	@Override
	public Tag executeTag() throws JspException, IOException {

		JspTag parent = getParent();
		if (!(parent instanceof FormTagHandler)) {
			throw ConstraintTagException.fromConstraint("recaptcha", "Tag must be placed inside [form] tag");
		}

		// Just to call nested tags
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(null);
		}

		setRandomId("recaptcha");
		
		// ReCaptcha V2
		if (version.equals(ReCaptchaHandler.RECAPTCHA_V2)) {
			Div formGroup = new Div();
			formGroup.addAttribute("class", Bootstrap.FORM_GROUP);

			if (parent instanceof FormTagHandler) {
				String size = ((FormTagHandler) parent).getSize();

				if (Size.LARGE.equalsIgnoreCase(size)) {
					formGroup.addAttribute("class", Bootstrap.FORM_GROUP_LARGE);

				} else if (Size.SMALL.equalsIgnoreCase(size)) {
					formGroup.addAttribute("class", Bootstrap.FORM_GROUP_SMALL);
				}
			}

			Label labelTag = new Label();
			labelTag.addAttribute("for", ReCaptchaHandler.RESPONSE_V1_FIELD_NAME)
					.addAttribute("class", Bootstrap.LABEL_CONTROL)
					.addAttribute("class", "recaptcha_only_if_image")
					.addText(getTagValue(label));
			formGroup.addTag(labelTag);

			Div div = new Div();
			div.addAttribute("id", id);
			formGroup.addTag(div);
			
			Input input = new Input();
			input.addAttribute("name", getTagName(J_CAPTCHA, fakeTagName(ReCaptchaHandler.RESPONSE_V2_FIELD_NAME)))
				 .addAttribute("type", Type.HIDDEN.name().toLowerCase());
			formGroup.addTag(input);

			Script script = new Script();
			script.addAttribute("type", "text/javascript")
				.addAttribute("src", String.format(ReCaptchaHandler.RECAPTCHA_CHALLENGE_V2_URL, "onloadReCaptcha", locale != null ? locale : ""))
				.addAttribute("async", "async")
				.addAttribute("defer", "defer");
			formGroup.addTag(script);

			Script callback = new Script();
			callback.addAttribute("type", "text/javascript")
				.addText("var onloadReCaptcha = function() {")
				.addText("grecaptcha.render('" + id + "', {")
				.addText("'sitekey': '" + siteKey + "'")
				.addText("});").addText("};");

			((FormTagHandler) parent).addBeforeFormTag(callback);

			return formGroup;
		}
		
		// Custom ReCaptcha V1
		Div div = new Div();
		div.addAttribute("id", id)
			.addAttribute("style", "display: none;");
		
		Div formImage = new Div();
		formImage.addAttribute("class", Bootstrap.FORM_GROUP)
			.addAttribute("align", align);
		
		Div recaptchaImageGroup = new Div();
		recaptchaImageGroup.addAttribute("class", Bootstrap.THUMBNAIL)
			.addAttribute("class", JSmart5.RECAPTCHA_IMAGE_GROUP);

		Div recaptchaImage = new Div();
		recaptchaImage.addAttribute("id", "recaptcha_image")
			.addAttribute("class", JSmart5.RECAPTCHA_IMAGE);

		Div recaptchaLogo = new Div();
		recaptchaLogo.addAttribute("class", JSmart5.RECAPTCHA_LOGO)
			.addAttribute("role", "presentation");
		
		Div recaptchaLogoImg = new Div();
		recaptchaLogoImg.addAttribute("class", JSmart5.RECAPTCHA_LOGO_IMAGE);
		
		Div recaptchaLogoText = new Div();
		recaptchaLogoText.addAttribute("class", JSmart5.RECAPTCHA_LOGO_TEXT)
			.addText("reCAPTCHA");
		
		recaptchaLogo.addTag(recaptchaLogoImg)
			.addTag(recaptchaLogoText);
		recaptchaImageGroup.addTag(recaptchaImage)
			.addTag(recaptchaLogo);

		formImage.addTag(recaptchaImageGroup);

		Div formGroup = new Div();
		formGroup.addAttribute("class", Bootstrap.FORM_GROUP);
		
		if (parent instanceof FormTagHandler) {
			String size = ((FormTagHandler) parent).getSize();

			if (Size.LARGE.equalsIgnoreCase(size)) {
				formGroup.addAttribute("class", Bootstrap.FORM_GROUP_LARGE);

			} else if (Size.SMALL.equalsIgnoreCase(size)) {
				formGroup.addAttribute("class", Bootstrap.FORM_GROUP_SMALL);
			}
		}

		Label labelTag = new Label();
		labelTag.addAttribute("for", ReCaptchaHandler.RESPONSE_V1_FIELD_NAME)
				.addAttribute("class", Bootstrap.LABEL_CONTROL)
				.addAttribute("class", "recaptcha_only_if_image")
				.addText(getTagValue(label));
		formGroup.addTag(labelTag);
		
		if (audioLabel != null) {
			Label audioLabelTag = new Label();
			audioLabelTag.addAttribute("for", ReCaptchaHandler.RESPONSE_V1_FIELD_NAME)
					.addAttribute("class", Bootstrap.LABEL_CONTROL)
					.addAttribute("class", "recaptcha_only_if_audio")
					.addText(getTagValue(audioLabel));
			formGroup.addTag(audioLabelTag);
		}
		
		Div inputGroup = new Div();
		inputGroup.addAttribute("class", Bootstrap.INPUT_GROUP);

		if (Size.SMALL.equalsIgnoreCase(size)) {
			inputGroup.addAttribute("class", Bootstrap.INPUT_GROUP_SMALL);
		} else if (Size.LARGE.equalsIgnoreCase(size)) {
			inputGroup.addAttribute("class", Bootstrap.INPUT_GROUP_LARGE);
		}
		formGroup.addTag(inputGroup);

		Input input = new Input();
		input.addAttribute("name", getTagName(J_CAPTCHA, fakeTagName(ReCaptchaHandler.RESPONSE_V1_FIELD_NAME)))
			 .addAttribute("type", Type.TEXT.name().toLowerCase())
			 .addAttribute("style", style)
			 .addAttribute("class", Bootstrap.FORM_CONTROL)
			 .addAttribute("tabindex", tabIndex)
			 .addAttribute("disabled", disabled ? "disabled" : null)
			 .addAttribute("placeholder", getTagValue(placeHolder))
			 .addAttribute("datatype", Type.TEXT.name().toLowerCase())
			 .addAttribute("autofocus", autoFocus ? autoFocus : null);
		
		appendRefId(input, ReCaptchaHandler.RESPONSE_V1_FIELD_NAME);
		
		if (Size.SMALL.equalsIgnoreCase(size)) {
			input.addAttribute("class", Bootstrap.INPUT_SMALL);
		} else if (Size.LARGE.equalsIgnoreCase(size)) {
			input.addAttribute("class", Bootstrap.INPUT_LARGE);
		}
		
		// Add the style class at last
		input.addAttribute("class", styleClass);
		
		appendValidator(input);
		appendRest(input);
		appendEvent(input);

		inputGroup.addTag(input);

		inputGroup.addTag(getAddOnButton("glyphicon-refresh", "Recaptcha.reload()", null));
		if (audioLabel != null) {
			inputGroup.addTag(getAddOnButton("glyphicon-headphones", "Recaptcha.switch_type('audio')", "recaptcha_only_if_image"));
			inputGroup.addTag(getAddOnButton("glyphicon-eye-open", "Recaptcha.switch_type('image')", "recaptcha_only_if_audio"));
		}
		inputGroup.addTag(getAddOnButton("glyphicon-info-sign", "Recaptcha.showhelp()", null));

		appendAjax(ReCaptchaHandler.RESPONSE_V1_FIELD_NAME);
		appendBind(ReCaptchaHandler.RESPONSE_V1_FIELD_NAME);
		
		appendTooltip(formGroup);
		appendPopOver(formGroup);
		
		Script options = new Script();
		options.addAttribute("type", "text/javascript")
			.addText("var RecaptchaOptions = {")
			.addText("theme: 'custom', ")
			.addText("custom_theme_widget: '" + id + "'");

		if (locale != null) {
			options.addText(", lang: '" + locale + "'");
		}
		options.addText("};");
		
		((FormTagHandler) parent).addBeforeFormTag(options);

		Script challenge = new Script();
		challenge.addAttribute("type", "text/javascript")
			.addAttribute("src", String.format(ReCaptchaHandler.RECAPTCHA_V1_CHALLENGE_URL, siteKey));

		div.addTag(formImage)
			.addTag(formGroup)
			.addTag(challenge);

		return div;
	}
	
	private A getAddOnButton(final String icon, final String script, final String style) throws JspException, IOException {
		A a = new A();
		a.addAttribute("class", Bootstrap.BUTTON)
			.addAttribute("class", Bootstrap.BUTTON_DEFAULT)
			.addAttribute("class", Bootstrap.INPUT_GROUP_ADDON)
			.addAttribute("class", style)
			.addAttribute("href", "javascript:" + script)
			.addAttribute("disabled", disabled ? "disabled" : null);
		
		IconTagHandler iconTag = new IconTagHandler();
		iconTag.setName(icon);
		a.addTag(iconTag.executeTag());
		return a;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public void setSiteKey(String siteKey) {
		this.siteKey = siteKey;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setAudioLabel(String audioLabel) {
		this.audioLabel = audioLabel;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setAutoFocus(boolean autoFocus) {
		this.autoFocus = autoFocus;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setAlign(String align) {
		this.align = align;
	}

}
