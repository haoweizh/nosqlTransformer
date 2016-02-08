package com.qnvip.mybatis.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.postgresql.jdbc4.Jdbc4Array;
import org.postgresql.util.PGobject;

public class JsonToObject {

	@SuppressWarnings({"rawtypes"})
	private static Object convertMap(Map<Object, Object> map, Class to) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException, SecurityException {
		Iterator<Object> itr = map.keySet().iterator();
		Object object = to.newInstance();
		ObjectStruct objectStruct = new ObjectStruct(object);
		Map<String, Method> setMethods = objectStruct.getSetMethods();
		Map<String, Method> getMethods = objectStruct.getGetMethods();
		while (itr.hasNext()) {
			String key = itr.next().toString();
			Object value = map.get(key);
			Method setMethod = setMethods.get(key);
			Method getMethod = getMethods.get(key);
			if (setMethod != null && getMethod != null && value != null) {
				/**
				 * 放弃嵌套调用，改由pojo中的set方法自行决定是否调用本方法
				 * if(!value.getClass().equals(getMethod.getReturnType())) { convertObject(value,
				 * getMethod.getReturnType()); }
				 */
				if (getMethod.getReturnType().getName().startsWith("java.lang")
						|| getMethod.getReturnType().getName().startsWith("com.qnvip")
						|| getMethod.getReturnType().getName().startsWith("com.qncentury")) {
					setMethod.invoke(object, value);
				} else if (getMethod.getReturnType().equals(Timestamp.class)) {
					setMethod.invoke(object, Timestamp.valueOf(value.toString()));
				}
			}
		}
		return object;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static List convertArray(Object from, Class to)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, JsonParseException, JsonMappingException, IOException {
		Object[] objects = null;
		if (from instanceof Object[]) {
			objects = (Object[]) from;
		} else if (from instanceof java.util.Collection) {
			objects = ((java.util.Collection) from).toArray();
		} else if (from instanceof java.util.AbstractCollection) {
			objects = ((java.util.AbstractCollection) from).toArray();
		} else if (from instanceof Jdbc4Array) {
			objects = ObjectToJson.parsePGJsonList(((Jdbc4Array) from).toString(), to);
		}
		List list = new ArrayList();
		for (int i = 0; objects != null && i < objects.length; i++) {
			list.add(convertObject(objects[i], to));
		}
		return list;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Object convertObject(Object from, Class to)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			NoSuchMethodException, SecurityException, JsonParseException, JsonMappingException, IOException {
		if (from == null || from.getClass().equals(to)) {
			return from;
		} else if (from instanceof Map) {
			return convertMap(((Map<Object, Object>) from), to);
		} else if (from.getClass().getName().startsWith("java.lang")) {
			Class[] parameters = new Class[] {String.class};
			Constructor constructor = to.getConstructor(parameters);
			if (constructor != null) {
				return constructor.newInstance(from.toString());
			}
			return null;
		} else if (to.equals(Timestamp.class)) {
			return Timestamp.valueOf(from.toString());
		} else if (from.getClass().equals(PGobject.class)) {
			return ObjectToJson.parsePGJson(((PGobject) from).getValue(), to);
		} else if (from.getClass().equals(String.class)) {
			return ObjectToJson.parsePGJson(((PGobject) from).getValue(), to);
		}
		return null;
	}

}
