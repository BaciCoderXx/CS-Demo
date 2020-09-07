package com.xx.csframework.core;

public class TemporaryClient {
	ServerConversation clientConversation;
	int count;
	
	TemporaryClient(ServerConversation clientConversation) {
		this.clientConversation = clientConversation;
	}

	ServerConversation getClientConversation() {
		return clientConversation;
	}

	int getCount() {
		return count;
	}
	
	int increaseCount() {
		return ++count;
	}
	
}
