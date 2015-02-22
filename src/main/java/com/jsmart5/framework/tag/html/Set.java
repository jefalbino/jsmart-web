package com.jsmart5.framework.tag.html;

import java.util.ArrayList;
import java.util.List;

public class Set extends Tag {

	private List<Tag> tags;

	public Set() {
		super("");
		this.tags = new ArrayList<Tag>();
	}

	public Set addTag(Tag tag) {
		this.tags.add(tag);
		return this;
	}
	
	public StringBuilder getHtml() {
		StringBuilder builder = new StringBuilder();
		for (Tag tag : tags) {
			builder.append(tag.getHtml());
		}
		return builder;
	}
}
