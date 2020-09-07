package com.xx.csframework.core;

/**
 * 网络信息命令
 * @author MEC-Teacher
 *
 */
public enum ENetCommand {
	/**
	 * 客户端连接数量超过最大限
	 */
	OUT_OF_ROOM,
	/**
	 * 客户端连接过于频繁
	 */
	TOO_FAST,
	/**
	 * 客户端连接接入临时连接池后，询问客户端身份
	 */
	WHO_ARE_YOU,
	/**
	 * 客户端响应服务器身份验证，要发送二进制信息！
	 */
	I_AM,
	/**
	 * 非法用户
	 */
	ILLEGAL_USER,
	/**
	 * 客户端连接服务器成功
	 */
	CONNECT_SUCCESS,
	/**
	 * 客户端请求下线
	 */
	REQUEST_OFFLINE,
	/**
	 * 有服务器向所有在线客户端发送消息
	 */
	SERVER_MESSAGE_TO_ALL_CLIENT,
	/**
	 * 客户端向服务器发出的“请求”
	 */
	REQUEST,
	/**
	 * 服务器对客户端的响应
	 */
	RESPONSE,
	
	TO_ONE,
	TO_ALL,
}
