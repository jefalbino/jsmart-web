package com.jsmart5.framework.tag.html;

public class Input extends Tag {

	public Input() {
		super("input");
	}

	public StringBuilder getHtml() {
		StringBuilder builder = new StringBuilder("<");
		builder.append(name);

		for (String attr : attributes.keySet()) {
			builder.append(" ").append(attr).append("=\"").append(attributes.get(attr)).append("\"");
		}
		builder.append(" />");
		return builder;
	}

}
