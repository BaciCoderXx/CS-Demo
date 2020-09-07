package com.xx.csframework.server.test.model;

public class UserModel {
	private String id;
	private String password;
	private String nick;
	private String imagecode;
	
	public String getImagecode() {
		return imagecode;
	}

	public void setImagecode(String imagecode) {
		this.imagecode = imagecode;
	}

	public String getNick() {
		return nick;
	}

	public UserModel setNick(String nick) {
		this.nick = nick;
		return this;
	}

	public UserModel() {
	}
	
	public UserModel(String id, String password, String nick, String imagecode) {
		setId(id);
		setpassword(password);
		setNick(nick);
		setImagecode(imagecode);
	}

	public String getId() {
		return id;
	}

	public UserModel setId(String id) {
		this.id = id;
		return this;
	}

	public String getpassword() {
		return password;
	}

	public void setpassword(String password) {
		this.password = password;
	}
	
}
