package com.qnvip.mybatis.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ObjectStruct {

	private Object object = null;
	private Map<String, Method> getMethods = null;
	private Map<String, Method> setMethods = null;
	private Set<String> fieldNames = null;

	public ObjectStruct(Object object) {
		this.object = object;
	}

	private void structMethods() {
		Method[] methods = object.getClass().getMethods();
		getMethods = new HashMap<String, Method>();
		setMethods = new HashMap<String, Method>();
		for (int i = 0; methods != null && i < methods.length; i++) {
			final String methodName = methods[i].getName();
			if (methodName.length() < 4) {
				continue;
			}
			String field = methodName.substring(3, 4).toLowerCase();
			if (methodName.length() > 4) {
				field = field + methodName.substring(4);
			}
			if (methodName.startsWith("get") && methodName.length() > 3) {
				getMethods.put(field, methods[i]);
			} else if (methodName.startsWith("set") && methodName.length() > 3) {
				setMethods.put(field, methods[i]);
			}
		}
	}

	public Map<String, Method> getSetMethods() {
		if (setMethods == null) {
			structMethods();
		}
		return setMethods;
	}

	public Set<String> getFieldNames() {
		return fieldNames;
	}

	public Map<String, Method> getGetMethods() {
		if (getMethods == null) {
			structMethods();
		}
		return getMethods;
	}
}
