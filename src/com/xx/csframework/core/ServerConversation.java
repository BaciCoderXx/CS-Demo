package com.xx.csframework.core;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.mec.util.DateAndTime;
import com.mec.util.MecCipher;

public class ServerConversation extends Communication {
	private static final Type type = new TypeToken<Map<String, String>>() {}.getType();
	private String clientId;
	private int keyLen;
	private Server server;
	private ClientInformation clientInfo;

	ServerConversation(Server server, Socket socket) throws NonSocketException, IOException {
		super(socket);
		this.server = server;
		keyLen = IConversation.SECRET_KEY_LENGTH;
	}
	
	@Override
	protected void peerAbnormalOffline() {
		server.removeTemporaryClient(clientId);
		server.removeClient(clientId);
		server.speakOut(DateAndTime.currentTime() 
				+ "客户端[" + (clientInfo == null ? clientId : clientInfo) 
				+ "]异常掉线！");
	}
	
	void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	void dealToOne(NetMessage netMessage) {
		server.sendToOne(clientId, netMessage.getAction(), netMessage.getPara());
	}
	
	void dealToOther(NetMessage netMessage) {
		server.sendToOther(clientId, netMessage.getPara());
	}
	
	private String getClientSecretId(byte[] secret) {
		byte[] key = new byte[keyLen];
		byte[] text = new byte[secret.length - keyLen];
		
		for (int i = 0; i < keyLen; i++) {
			key[i] = secret[i];
		}
		for (int i = 0; i < text.length; i++) {
			text[i] = secret[keyLen + i];
		}
		MecCipher.encrypt(text, key);
		
		return new String(text);
	}
	
	void dealIAm(NetMessage netMessage, byte[] secretText) {
		String secretId = getClientSecretId(secretText);
		if (secretId == null || !secretId.equals(clientId)) {
			send(new NetMessage().setCommand(ENetCommand.ILLEGAL_USER));
			close();
		} else {
			String para = netMessage.getPara();
			Map<String, String> paraMap = Server.getGson().fromJson(para, type);
			String infoString = paraMap.get("info");
			this.clientInfo  = Server.getGson()
					.fromJson(infoString, ClientInformation.class);
			synchronized (Server.class) {
				server.addClient(this, clientId);
			}
			server.speakOut(DateAndTime.currentTime() + "客户端["
					+ clientInfo.getInfo() + "]接入服务器！");
			send(new NetMessage()
					.setCommand(ENetCommand.CONNECT_SUCCESS));
		}
		synchronized (Server.class) {
			server.removeTemporaryClient(clientId);
		}
	}
	
	void dealRequestOffline(NetMessage netMessage) {
		close();
		server.removeClient(clientId);
		server.speakOut(DateAndTime.currentTime() + "客户端[" + clientInfo + "]下线！");
	}

	@Override
	protected void dealNetMessage(NetMessage netMessage) {
		ENetCommand command = netMessage.getCommand();
		if (command.equals(ENetCommand.REQUEST)) {
			String action = netMessage.getAction();
			int colonIndex = action.indexOf('&');
			String request = action.substring(0, colonIndex);
			
			try {
				String result = server.getActioner()
						.executeRequest(request, netMessage.getPara());
				send(new NetMessage()
						.setCommand(ENetCommand.RESPONSE)
						.setAction(action)
						.setPara(result));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return;
		}
		try {
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
