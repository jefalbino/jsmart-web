package com.jsmart5.framework.tag.html;

public class Image extends Tag {

	public Image() {
		super("image");
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
