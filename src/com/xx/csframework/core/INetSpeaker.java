package com.xx.csframework.core;

public interface INetSpeaker {
	void addListener(INetListener listener);
	void removeListener(INetListener listener);
	void speakOut(String message);
}
