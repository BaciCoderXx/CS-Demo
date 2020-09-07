package com.xx.csframework.actioner;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xx.csframework.annotation.Actioner;
import com.xx.csframework.annotation.MecAction;
import com.mec.util.PackageScanner;

public class ActionFactory implements IActionFactory {
	private static final Type type;
	private static final Map<String, ActionDefinition> actionMap;
	private static final Gson gson;
	
	static {
		actionMap = new HashMap<>();
		type = new TypeToken<Map<String, String>>() {}.getType();
		gson = new GsonBuilder().create();
	}
	
	public static void addAction(Object object) throws Exception {
		Class<?> klass = object.getClass();
		if(!klass.isAnnotationPresent(MecAction.class)) {
			throw new Exception("类["
					+ klass.getName() + "]没有MecAction注解！");
		}
		Method[] methods = klass.getDeclaredMethods();
		for (Method method : methods) {
			if (!method.isAnnotationPresent(Actioner.class)) {
				continue;
			}
			Actioner actioner = method.getAnnotation(Actioner.class);
			String action = actioner.action();
			ActionDefinition actionDefinition = new ActionDefinition();
			actionDefinition.setObject(object);
			actionDefinition.setKlass(klass);
			actionDefinition.setMethod(method);
			
			actionMap.put(action, actionDefinition);
		}
	}
	
	private static void processClass(Class<?> klass) {
		try {
			Object object = klass.newInstance();
			Method[] methods = klass.getDeclaredMethods();
			for (Method method : methods) {
				if (!method.isAnnotationPresent(Actioner.class)) {
					continue;
				}
				Actioner actioner = method.getAnnotation(Actioner.class);
				ActionDefinition ad = new ActionDefinition();
				ad.setKlass(klass);
				ad.setObject(object);
				ad.setMethodAndPara(method);
				actionMap.put(actioner.action(), ad);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void scanActioner(String packageName) {
		new PackageScanner() {
			@Override
			public void dealClass(Class<?> klass) {
				if (klass.isInterface() || klass.isPrimitive()
						|| klass.isArray() || klass.isAnnotation()
						|| klass.isEnum()
						|| !klass.isAnnotationPresent(MecAction.class)) {
					return;
				}
				try {
					processClass(klass);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.scannerPackage(packageName);
	}
	
	@Override
	public String executeRequest(String action, String para) throws Exception {
		ActionDefinition ad = actionMap.get(action);
		if (ad == null) {
			throw new Exception("action:[" + action + "]没有配置！");
		}
		Object object = ad.getObject();
		Method method = ad.getMethod();
		List<ActionParameter> paras = ad.getParameterList();
		Map<String, String> paraMap = gson.fromJson(para, type);
		
		Object result = null;
		if (paras.size() <= 0) {
			result = method.invoke(object);
		} else {
			Object[] values = new Object[paras.size()];
			int i = 0;
			for (ActionParameter parameter : paras) {
				values[i++] = parameter.getValue(gson, 
						paraMap.get(parameter.getName()));
			}
			result = method.invoke(object, values);
		}
		
		return gson.toJson(result);
	}

	@Override
	public void executeResponse(String action, String para) throws Exception {
		ActionDefinition ad = actionMap.get(action);
		if(ad == null) {
			throw new Exception("action:[" + action + "]没有配置！");
		}
		Object object = ad.getObject();
		Method method = ad.getMethod();
		if (method.getParameterTypes().length <= 0) {
			method.invoke(object);
			return;
		}
		Class<?> paraType = method.getParameterTypes()[0];
		Object objPara = gson.fromJson(para, paraType);
		method.invoke(object, new Object[] { objPara });
	}

}
