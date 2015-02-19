package com.jsmart5.framework.tag.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tag {
	
	protected String name;
	
	protected StringBuilder text;
	
	private int textPosition;

	protected Map<String, Object> attributes;

	protected List<Tag> tags;

	public Tag(String name) {
		this.name = name;
		this.attributes = new HashMap<String, Object>();
		this.tags = new ArrayList<Tag>();
		this.text = new StringBuilder();
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
	
	public String getText() {
		return text.toString();
	}
	
	public Tag addText(String text) {
		// Set the position of text between inner tags
		this.textPosition = tags.size() -1;

		this.text.append(text);
		return this;
	}

	public Tag setText(String text) {
		this.text = new StringBuilder(text);
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

		if (text != null && textPosition < 0) {
			builder.append(text);
		}

		for (int i = 0; i < tags.size(); i++) {
			builder.append(tags.get(i).getHtml());
			if (i == textPosition) {
				builder.append(text);
			}
		}

		builder.append("</").append(name).append(">");
		return builder;
	}

}
