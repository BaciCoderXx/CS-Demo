package com.xx.csframework.core;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.mec.util.PropertiesParser;
import com.xx.csframework.actioner.ActionFactory;
import com.xx.csframework.actioner.IActionFactory;

public class Client implements INetSpeaker {
	private String serverIp;
	private int serverPort;
	private IClientAction clientAction;
	private ClientInformation myInfo;
	private ClientConversation conversation;
	private IActionFactory actionFactory;
	
	IActionFactory getActionFactory() {
		return actionFactory;
	}

	private List<INetListener> listenerList;
	
	public Client() {
		this.myInfo = new ClientInformation();
		this.listenerList = new ArrayList<>();
		this.actionFactory = new ActionFactory();
	}
	
	ClientInformation getMyInfo() {
		return myInfo;
	}

	public void setContext(String context) {
		myInfo.setContext(context);
	}
	
	public String getServerIp() {
		return serverIp;
	}

	public void setClientAction(IClientAction clientAction) {
		this.clientAction = clientAction;
	}
	
	public void sendToOther(String message) {
		conversation.sendToOther(message);
	}
	
	public void sendToOne(String clientId, String message) {
		conversation.sendToOne(clientId, message);
	}
	
	IClientAction getClientAction() {
		return clientAction;
	}
	
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	public void offline() {
		conversation.offline();
	}
	
	/**
	 * 向服务器发出请求，执行action对应的动作，从而产生响应
	 * @param action 请求的action
	 * @param parameter 请求的参数
	 */
	public void sendRequest(String action, String parameter) {
		String actionString = action + '&' + action;
		conversation.send(new NetMessage()
				.setCommand(ENetCommand.REQUEST)
				.setAction(actionString)
				.setPara(parameter));
	}
	
	public void sendRequest(String request, String response, String parameter) {
		String actionString = request + '&' + response;
		conversation.send(new NetMessage()
				.setCommand(ENetCommand.REQUEST)
				.setAction(actionString)
				.setPara(parameter));
	}
	
	public void initClient(String configPath) throws Exception {
		PropertiesParser.loadProperties(configPath);
		this.serverIp = PropertiesParser.value("server_ip");
		if (this.serverIp == null) {
			this.serverPort = 0;
			throw new Exception("无效的Server IP！");
		}
		String portString = PropertiesParser.value("server_port");
		if (portString == null) {
			this.serverPort = 0;
			throw new Exception("无效的Server PORT！");
		}
		this.serverPort = Integer.parseInt(portString);
	}
	
	public boolean connectToServer() {
		if (serverIp == null || serverPort <= 1000 || serverPort > 65535
				|| clientAction == null) {
			return false;
		}
		Socket socket;
		try {
			socket = new Socket(serverIp, serverPort);
			this.conversation = new ClientConversation(this, socket);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	@Override
	public void addListener(INetListener listener) {
		if (listenerList.contains(listener)) {
			return;
		}
		listenerList.add(listener);
	}

	@Override
	public void removeListener(INetListener listener) {
		if (!listenerList.contains(listener)) {
			return;
		}
		listenerList.remove(listener);
	}

	@Override
	public void speakOut(String message) {
		try {
			Exception e = Exception.class.newInstance();
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		for (INetListener listener : listenerList) {
			listener.dealNetMessage(message);
		}
	}
	
}
