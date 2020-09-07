package com.xx.csframework.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 实现底层数据通讯；不关心是服务器端或者客户端；<br>
 * 无论服务器或者客户机，都存在发送、接收来自对端的信息的工作；这是这个类要实现的基础功能。<br>
 * 1、建立通信信道；
 * 2、提供安全的，可识别(可处理)错误的通信机制；
 * 3、提供发送信息的工具(方法、函数)；
 * 4、侦听来自对端的信息(必须使用线程)。
 * @author MEC-Teacher
 *
 */
public abstract class Communication implements Runnable {
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	volatile boolean goon;
	
	volatile Object communicationThreadRunning;
	static long communicationId;
	
	protected abstract void peerAbnormalOffline();
	protected abstract void dealNetMessage(NetMessage netMessage);
	protected abstract void dealNetMessage(NetMessage netMessage, byte[] bytes);
	
	Communication(Socket socket) throws NonSocketException, IOException {
		if (socket == null) {
			throw new NonSocketException("无效的Socket");
		}
		
		communicationThreadRunning = new Object();
		
		this.socket = socket;
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		
		synchronized (communicationThreadRunning) {
			goon = true;
			new Thread(this, "MEC-Communication-" + ++communicationId).start();
			try {
				communicationThreadRunning.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 发送字符串信息
	 * @param message
	 */
	void send(NetMessage message) {
		try {
			dos.writeUTF(message.toString());
		} catch (IOException e) {
			// 对端异常下线
			peerAbnormalOffline();
			close();
		}
	}
	
	/**
	 * 发送字符串信息或者二进制字节信息
	 * @param message
	 * @param bytes
	 */
	void send(NetMessage message, byte[] bytes) {
		try {
			message.setBytesCount(bytes.length);
			message.setType(NetMessage.BIN);
			dos.writeUTF(message.toString());
			if (bytes != null) {
				dos.write(bytes);
			}
		} catch (IOException e) {
			// 对端异常下线
			peerAbnormalOffline();
			close();
		}
	}
	
	/**
	 * 结束通信
	 */
	void close() {
		goon = false;
		try {
			if (dis != null) {
				dis.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			dis = null;
		}
		try {
			if (dos != null) {
				dos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			dos = null;
		}
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket = null;
		}
	}

	/**
	 * 读入长度为bytesCount字节的字节信息
	 * @param bytesCount
	 * @return
	 * @throws IOException
	 */
	private byte[] getBytes(int bytesCount) throws IOException {
		byte[] bytes = new byte[bytesCount];
		
		int off = 0;
		int len = bytesCount;
		while (len > 0) {
			int readLen = dis.read(bytes, off, len);
			off += readLen;
			len -= readLen;
		}
		
		return bytes;
	}
	
	@Override
	public void run() {
		String message;
		synchronized (communicationThreadRunning) {
			communicationThreadRunning.notify();
		}
		
		while (goon) {
			try {
				message = dis.readUTF();
				NetMessage netMessage = new NetMessage(message);
				if (netMessage.getType() == NetMessage.BIN) {
					int bytesCount = netMessage.bytesCount;
					byte[] bytes = getBytes(bytesCount);
					dealNetMessage(netMessage, bytes);
				} else {
					dealNetMessage(netMessage);
				}
			} catch (IOException e) {
				if (goon == true) {
					peerAbnormalOffline();
					goon = false;
				}
			}
		}
		close();
	}

}
