package com.xx.csframework.core;

public class ClientActionAdapter implements IClientAction {

	@Override
	public void connectSuccess() {}
	@Override
	public void serverAbnormalDrop() {}
	@Override
	public void serverOutOfRoom() {}
	@Override
	public void connectTooFast() {}

	@Override
	public boolean confirmOffline() {
		return true;
	}

}
