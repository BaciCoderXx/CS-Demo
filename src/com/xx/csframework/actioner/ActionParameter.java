package com.xx.csframework.actioner;

import com.google.gson.Gson;

public class ActionParameter {
	private String name;
	private Class<?> type;
	
	ActionParameter() {
	}
	
	ActionParameter setName(String name) {
		this.name = name;
		return this;
	}
	
	String getName() {
		return name;
	}
	
	ActionParameter setType(Class<?> type) {
		this.type = type;
		return this;
	}
	
	Object getValue(Gson gson, String json) {
		return gson.fromJson(json, type);
	}
	
}
