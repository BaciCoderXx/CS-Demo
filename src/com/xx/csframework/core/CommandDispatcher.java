package com.xx.csframework.core;

import java.lang.reflect.Method;

public class CommandDispatcher {
	
	CommandDispatcher() {
	}
	
	private static String command2MethodName(String command) {
		StringBuffer result = new StringBuffer("deal");
		
		String[] words = command.split("_");
		for (String word : words) {
			String tmp = word.substring(0, 1);
			tmp += word.substring(1).toLowerCase();
			result.append(tmp);
		}
		
		return result.toString();
	}
	
	static void dispatcherCommand(Object object, NetMessage netMessage, byte[] binMessage) throws Exception {
		Class<?> klass = object.getClass();
		String methodName = command2MethodName(netMessage.getCommand().name());
		Method method = klass.getDeclaredMethod(methodName, 
				new Class<?>[]{ NetMessage.class, byte[].class });
		method.invoke(object, netMessage, binMessage);
	}
	
	static void dispatcherCommand(Object object, NetMessage netMessage) throws Exception {
		Class<?> klass = object.getClass();
		String methodName = command2MethodName(netMessage.getCommand().name());
		Method method = klass.getDeclaredMethod(methodName, NetMessage.class);
		method.invoke(object, netMessage);
	}
	
}
