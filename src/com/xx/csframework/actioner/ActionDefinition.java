package com.xx.csframework.actioner;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import com.xx.csframework.annotation.Para;

public class ActionDefinition {
	private Class<?> klass;
	private Method method;
	private Object object;
	private List<ActionParameter> parameterList;
	
	ActionDefinition() {
		parameterList = new ArrayList<>();
	}

	List<ActionParameter> getParameterList() {
		return parameterList;
	}
	
	Class<?> getKlass() {
		return klass;
	}

	void setKlass(Class<?> klass) {
		this.klass = klass;
	}

	Method getMethod() {
		return method;
	}
	
	void setMethodAndPara(Method method) throws Exception {
		this.method = method;
		
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			if (!parameter.isAnnotationPresent(Para.class)) {
				throw new Exception("方法[]的第" + (i+1) + "个参数无注解！");
			}
			Para para = parameter.getAnnotation(Para.class);
			parameterList.add(new ActionParameter()
					.setName(para.name())
					.setType(parameter.getType()));
		}
	}
	
	void setMethod(Method method) throws Exception {
		this.method = method;
	}

	Object getObject() {
		return object;
	}

	void setObject(Object object) {
		this.object = object;
	}
}
