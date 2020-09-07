package com.xx.csframework.core;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.mec.util.ByteAndDigit;

public class ClientInformation {
	private String id;
	private String ip;
	private String mac;
	private String name;
	private String context;
	
	public ClientInformation() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			this.name = addr.getHostName();

			this.ip = addr.getHostAddress();
			if(!ip.startsWith("192")) {
				ip = "192.168.146.1";
			}
			byte[] macArr = NetworkInterface.getByInetAddress(addr).getHardwareAddress();
			this.mac = ByteAndDigit.byte2Hex(macArr);
		} catch (UnknownHostException e) {
			this.ip = "127.0.0.1";
		} 
		catch (SocketException e) {
			this.mac = null;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public String getMac() {
		return mac;
	}

	public String getName() {
		return name;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
	
	public String getInfo() {
		return id + "(" + name + ")";
	}
	
	public String getDetailInfo() {
		StringBuffer res = new StringBuffer();
		
		res.append(id).append(':').append(name)
		.append(':').append(mac).append(':').append(context);
		
		return res.toString();
	}
	
	@Override
	public String toString() {
		return getInfo();
	}
	
}
