package com.jsmart5.framework.tag.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Tag {
	
	protected String name;
	
	protected String text;

	protected Map<String, Object> attributes;

	protected List<Tag> tags;

	public Tag(String name) {
		this.name = name;
		this.attributes = new HashMap<String, Object>();
		this.tags = new ArrayList<Tag>();
	}

	public Tag addAttribute(String name, Object value) {
		if (value != null) {
			Object oldValue = attributes.get(name);
			if (oldValue == null) {
				attributes.put(name, value);
			} else {
				attributes.put(name, oldValue + " " + value);
			}
		}
		return this;
	}
	
	public Tag setText(String text) {
		this.text = text;
		return this;
	}

	public Tag addTag(Tag tag) {
		tags.add(tag);
		return this;
	}

	public StringBuilder getHtml() {
		StringBuilder builder = new StringBuilder("<");
		builder.append(name);

		for (String attr : attributes.keySet()) {
			builder.append(" ").append(attr).append("=\"").append(attributes.get(attr)).append("\"");
		}
		builder.append(">");

		if (text != null) {
			builder.append(text);
		}

		for (Tag tag : tags) {
			builder.append(tag.getHtml());
		}

		builder.append("</").append(name).append(">");
		return builder;
	}

}
