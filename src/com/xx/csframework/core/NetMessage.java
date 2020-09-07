package com.xx.csframework.core;

/**
 * 网络交互信息
 * @author MEC-Teacher
 *
 */
public class NetMessage {
	static final String NONE_ACTION = "NAC";
	static final String NONE_PARA = "NAP";
	static final int STR = 0;
	static final int BIN = 1;
	/**
	 * 信息类型；<br>
	 * <ol>
	 * 		<li>STRING:字符串信息</li>
	 * 		<li>BINARY:二进制信息；紧跟着发送/接收一次二进制信息。</li>
	 * </ol>
	 */
	int type;
	/**
	 * 若发送/接收二进制信息，该成员描述所要发送/接收的字节数
	 */
	int bytesCount;
	/**
	 * 网络信息命令；参见{@link ENetCommand}
	 */
	ENetCommand command;
	/**
	 * 请求/响应的具体动作
	 */
	String action;
	/**
	 * 请求/响应动作的参数；由用户制定；Json格式字符串
	 */
	String para;
	
	NetMessage() {
	}
	
	NetMessage(String message) {
		int atIndex = message.indexOf('@');
		String mess = message.substring(0, atIndex);
		this.para = message.substring(atIndex + 1);
		if (this.para.equals(NONE_PARA)) {
			this.para = null;
		}
		
		String[] messes = mess.split(":");
		this.type = Integer.valueOf(messes[0]);
		this.bytesCount = Integer.valueOf(messes[1]);
		this.command = ENetCommand.valueOf(messes[2]);
		this.action = messes[3];
		if (this.action.equals(NONE_ACTION)) {
			this.action = null;
		}
	}

	@Override
	public String toString() {
		return new StringBuffer(
			type == 0 ? "0" : "1"
		).append(':').append(this.bytesCount)
		.append(':').append(this.command.name())
		.append(':')
		.append(this.action == null ? NONE_ACTION : this.action)
		.append('@')
		.append(this.para == null ? NONE_PARA : this.para).toString();
	}
	
	NetMessage setType(int type) {
		if (type != 0 && type != 1) {
			type = 0;
		}
		this.type = type;
		return this;
	}
	
	int getType() {
		return type;
	}
	
	int getBytesCount() {
		return bytesCount;
	}

	NetMessage setBytesCount(int bytesCount) {
		this.bytesCount = bytesCount;
		return this;
	}

	ENetCommand getCommand() {
		return command;
	}

	NetMessage setCommand(ENetCommand command) {
		this.command = command;
		return this;
	}

	String getAction() {
		return action;
	}

	NetMessage setAction(String action) {
		this.action = action;
		return this;
	}

	String getPara() {
		return para;
	}

	NetMessage setPara(String para) {
		this.para = para;
		return this;
	}
	
}
