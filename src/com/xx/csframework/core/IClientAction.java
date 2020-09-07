package com.xx.csframework.core;

public interface IClientAction {
	void connectSuccess();
	void serverAbnormalDrop();
	void serverOutOfRoom();
	void connectTooFast();
	boolean confirmOffline();
}
