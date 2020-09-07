package com.xx.csframework.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xx.csframework.actioner.ActionFactory;
import com.xx.csframework.actioner.IActionFactory;
import com.mec.util.DateAndTime;
import com.mec.util.Didadida;
import com.mec.util.PropertiesParser;

/**
 * 对外提供的功能调用：
 * 1、启、停服务器；
 * 2、强制宕机；
 * 3、获取在线客户端列表；
 * 4、强制终止客户端。
 * 内部应该拥有的基本功能：
 * 1、侦听并连接客户端(上线)；
 * 2、处理客户端下线及异常下线；
 * 3、转发消息(群发和个发)；
 * 4、action分发器，或者说，客户端请求的处理；
 * 5、网络消息通知(日志)；
 * @author 铁血教主
 *
 */
public class Server implements INetSpeaker, Runnable {
	private static final Gson gson = new GsonBuilder().create();
	public static final int MAX_CLIENT_COUNT = 50;
	public static final int MIN_CYCLE_TIME = 1000;
	
	private ServerSocket serverSocket;
	private int port;
	private volatile boolean goon;
	private volatile Object lock;
	
	private int maxClientCount;
	private int minCycleTime;
	
	private IActionFactory actioner;
	
	private ConcurrentLinkedQueue<Socket> clientSocketQueue;
	private Map<String, ServerConversation> clientPool;
	private Map<String, TemporaryClient> temporaryPool;
	private Didadida clientCleaner;
	
	private List<INetListener> listenerList;
	
	public Server() {
		this(0);
	}
	
	public Server(int port) {
		this.listenerList = new ArrayList<>();
		this.lock = new Object();
		this.port = port;
		this.goon = false;
		this.maxClientCount = MAX_CLIENT_COUNT;
		this.minCycleTime = MIN_CYCLE_TIME;
		this.actioner = new ActionFactory();
	}
	
	IActionFactory getActioner() {
		return actioner;
	}
	
	static Gson getGson() {
		return gson;
	}
	
	public Server setPort(int port) {
		this.port = port;
		return this;
	}
	
	public void initServer(String configPath) {
		try {
			PropertiesParser.loadProperties(configPath);
			String portString = PropertiesParser.value("server_port");
			if (portString == null) {
				throw new Exception("port未设置！");
			}
			this.port = Integer.parseInt(portString);
			
			String maxClientCountString = PropertiesParser.value("max_client_count");
			if (maxClientCountString != null) {
				this.maxClientCount = Integer.parseInt(maxClientCountString);
			}
			
			String minCycleTimeString = PropertiesParser.value("min_cycle_time");
			if (minCycleTimeString != null) {
				this.minCycleTime = Integer.parseInt(minCycleTimeString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startup() throws Exception {
		if (goon) {
			// 告知上一层服务器已启动！
			speakOut("服务器已启动，无需再次启动！");
			return;
		}
		if (port < 1000 || port > 65535) {
			throw new Exception("无效port值！");
		}
		this.clientPool = new ConcurrentHashMap<>();
		this.temporaryPool = new ConcurrentHashMap<>();
		this.clientSocketQueue = new ConcurrentLinkedQueue<Socket>();
		this.clientCleaner = new TemporaryClientCleaner(500);
		this.clientCleaner.start();

		serverSocket = new ServerSocket(port);
		speakOut(DateAndTime.currentTime() + "服务器成功启动！");
		goon = true;
		new Thread(this, "客户端连接请求侦听线程").start();
	}
	
	public void shutdown() {
		if (!goon) {
			speakOut("服务器已宕机，无需再次宕机！");
			return;
		}
		if (clientPool.size() > 0) {
			speakOut("尚存在在线客户端，不能宕机！");
			return;
		}
		closeServer();
	}
	
	private void closeServer() {
		clientCleaner.stop();
		goon = false;
		if (serverSocket != null && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (IOException e) {
			} finally {
				serverSocket = null;
			}
		}
	}
	
	void addClient(ServerConversation client, String clientId) {
		synchronized (clientPool) {
			clientPool.put(clientId, client);
		}
	}
	
	void removeClient(String clientId) {
		synchronized (clientPool) {
			clientPool.remove(clientId);
		}
	}

	@Override
	public void run() {
		speakOut(DateAndTime.currentTime() + "开始侦听客户端连接请求……");
		new Pretreat();
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		while (goon) {
			try {
				Socket socket = serverSocket.accept();
				clientSocketQueue.add(socket);
			} catch (IOException e) {
				goon = false;
			}
		}
		speakOut(DateAndTime.currentTime() + "服务器停止侦听！");
	}
	
	void removeTemporaryClient(String clientId) {
		synchronized (Server.class) {
			if (temporaryPool.containsKey(clientId)) {
				temporaryPool.remove(clientId);
			}
		}
	}
	
	public boolean isServerStartup() {
		return goon;
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
		for (INetListener listener : listenerList) {
			listener.dealNetMessage(message);
		}
	}
	
	class Pretreat implements Runnable {

		public Pretreat() {
			new Thread(this, "Pretreat Service").start();
		}
		
		@Override
		public void run() {
			speakOut(DateAndTime.currentTime() + "客户端连接预处理线程启动！");
			synchronized (lock) {
				lock.notify();
			}
			
			while (goon) {
				Socket socket = clientSocketQueue.poll();
				if (socket == null) {
					continue;
				}
				try {
					ServerConversation clientConversation
							= new ServerConversation(Server.this, socket);
					
					String clientIp = socket.getInetAddress().getHostAddress();
					String clientId;
					if (isOutOfRoom(clientConversation) 
							|| (clientId = connectTooFast(clientConversation, clientIp)) == null) {
						continue;
					}
					speakOut("客户端[" + clientId + "]请求连接！");
					clientConversation.setClientId(clientId);
					clientConversation.send(new NetMessage()
							.setCommand(ENetCommand.WHO_ARE_YOU)
							.setPara(clientId));
					synchronized (Server.class) {
						temporaryPool.put(clientId, new TemporaryClient(clientConversation));
					}
				} catch (Exception e) {
					// TODO 以后用日志记录该错误！
				}
			}
		}
		
	}
	
	void sendToOther(String clientId, String message) {
		// TODO 
	}
	
	void sendToOne(String sourceId, String targetId, String message) {
		// TODO
	}
	
	public void sendMessageToAllClient(String message) {
		Set<String> clientSet = clientPool.keySet();
		synchronized (clientPool) {
			NetMessage netMessage = new NetMessage()
					.setCommand(ENetCommand.SERVER_MESSAGE_TO_ALL_CLIENT)
					.setPara(message);
			for (String clientId : clientSet) {
				ServerConversation client = clientPool.get(clientId);
				client.send(netMessage);
			}
		}
	}
	
	class TemporaryClientCleaner extends Didadida {
		public TemporaryClientCleaner(long delayTime) {
			super();
			setDelayTime(delayTime);
		}
		
		@Override
		public void doIt() {
			synchronized (Server.class) {
				Set<String> clientIdSet = temporaryPool.keySet();
				for (String clientId : clientIdSet) {
					TemporaryClient temp = temporaryPool.get(clientId);
					if (temp.increaseCount() >= 2) {
						temporaryPool.remove(clientId);
						temp.getClientConversation().close();
					}
				}
			}
		}

		@Override
		public void beforeDida() {
			speakOut(DateAndTime.currentTime() + "临时连接池清道夫开始工作……");
		}

		@Override
		public void afterStopDida() {
			speakOut(DateAndTime.currentTime() + "临时连接池清道夫停止工作！");
		}
	}
	
	private boolean isOutOfRoom(ServerConversation clientConversation) {
		int currentClientCount = clientPool.size() + temporaryPool.size();
		if (currentClientCount >= maxClientCount) {
			clientConversation.send(new NetMessage()
					.setCommand(ENetCommand.OUT_OF_ROOM));
			clientConversation.close();
			return true;
		}
		return false;
	}
	
	private String connectTooFast(ServerConversation clientConversation, String clientIp) {
		long curTime = System.currentTimeMillis();
		curTime /= minCycleTime;
		String clientId = clientIp + ':' + curTime;
		if (clientPool.containsKey(clientId) || temporaryPool.containsKey(clientId)) {
			clientConversation.send(new NetMessage()
					.setCommand(ENetCommand.TOO_FAST));
			clientConversation.close();
			return null;
		}
		return clientId;
	}
	
}
