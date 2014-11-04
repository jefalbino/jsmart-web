package com.jsmart5.framework.json;

import java.util.ArrayList;
import java.util.List;

public final class JsonSearch {

	private String method;

	private String action;

	private List<JsonParam> params = new ArrayList<JsonParam>();

	private String update;

	private String before;

	private String exec;

	private boolean track;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<JsonParam> getParams() {
		return params;
	}

	public void setParams(List<JsonParam> params) {
		this.params = params;
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public String getExec() {
		return exec;
	}

	public void setExec(String exec) {
		this.exec = exec;
	}

	public boolean getTrack() {
		return track;
	}

	public void setTrack(boolean track) {
		this.track = track;
	}

}
