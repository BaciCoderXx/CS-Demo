package com.xx.csframework.core;

import java.net.Socket;

import com.mec.util.ArgumentMaker;
import com.mec.util.MecCipher;

public class ClientConversation extends Communication {
	private Client client;
	private String id;
	private int keyLen;

	ClientConversation(Client client, Socket socket) throws Exception {
		super(socket);
		this.client = client;
		this.keyLen = IConversation.SECRET_KEY_LENGTH;
	}

	@Override
	protected void peerAbnormalOffline() {
		client.getClientAction().serverAbnormalDrop();
	}
	
	void dealToOne(NetMessage netMessage) {
		// TODO
	}
	
	void dealToOther(NetMessage netMessage) {
		// TODO
	}
	
	void sendToOther(String message) {
		// TODO
	}
	
	void sendToOne(String clientId, String message) {
		// TODO
	}
	
	void offline() {
		if (!client.getClientAction().confirmOffline()) {
			return;
		}
		send(new NetMessage()
				.setCommand(ENetCommand.REQUEST_OFFLINE));
		close();
	}

	void dealTooFast(NetMessage netMessage) {
		close();
		client.getClientAction().connectTooFast();
	}
	
	void dealOutOfRoom(NetMessage netMessage) {
		close();
		client.getClientAction().serverOutOfRoom();
	}
	
	private byte[] getSecretText(String id, int keyLen) {
		byte[] key = MecCipher.getSecretKey(keyLen);
		byte[] text = id.getBytes();
		MecCipher.encrypt(text, key);
		byte[] binMessage = new byte[key.length + text.length];
		for (int i = 0; i < key.length; i++) {
			binMessage[i] = key[i];
		}
		for (int i = 0; i < text.length; i++) {
			binMessage[keyLen + i] = text[i];
		}
		
		return binMessage;
	}
	
	void dealWhoAreYou(NetMessage netMessage) {
		this.id = netMessage.getPara();
		byte[] binMess = getSecretText(this.id, keyLen);
		ClientInformation myInfo = client.getMyInfo();
		myInfo.setId(this.id);
		String info = new ArgumentMaker()
				.addArg("info", myInfo).toString();
		send(new NetMessage()
				.setCommand(ENetCommand.I_AM)
				.setType(NetMessage.BIN)
				.setBytesCount(binMess.length)
				.setPara(info), binMess);
	}
	
	void dealConnectSuccess(NetMessage netMessage) {
		client.getClientAction().connectSuccess();
	}
	
	@Override
	protected void dealNetMessage(NetMessage netMessage) {
		try {
			if (netMessage.getCommand().equals(ENetCommand.RESPONSE)) {
				String action = netMessage.getAction();
				int colonIndex = action.indexOf("&");
				String response = action.substring(colonIndex + 1);
				String para = netMessage.getPara();
				
				client.getActionFactory().executeResponse(response, para);
				
				return;
			}
			CommandDispatcher.dispatcherCommand(this, netMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void dealNetMessage(NetMessage netMessage, byte[] bytes) {
		try {
			CommandDispatcher.dispatcherCommand(this, netMessage, bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
