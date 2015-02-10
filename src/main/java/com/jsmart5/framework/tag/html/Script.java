package com.jsmart5.framework.tag.html;

import com.jsmart5.framework.util.SmartUtils;

public class Script extends Tag {
	
	private static final String SCRIPT_ID = SmartUtils.randomId();

	public Script() {
		super("script");
	}

	public StringBuilder getHtml() {
		StringBuilder builder = new StringBuilder("<");
		builder.append(name);
		builder.append(" ").append("id").append("=\"").append(SCRIPT_ID).append("\"");
		builder.append(">");

		builder.append("$(document).ready(function() {");

		if (text != null) {
			builder.append(text);
		}

		builder.append("});");
		builder.append("</").append(name).append(">");
		return builder;
	}
}
