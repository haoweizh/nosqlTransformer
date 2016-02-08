package com.qnvip.mybatis.typehandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.qnvip.mybatis.util.ObjectToJson;
import com.qnvip.mybatis.util.ObjectStruct;

@Deprecated
/**
 * 由于嵌套逻辑原因暂不使用
 * 
 * @author simon
 *
 */
public class DBPojoConverter {
	private static Log log = LogFactory.getLog(ObjectToJson.class);

	public static String postgresSqlStringify(Object object) {
		if (object == null) {
			return null;
		}
		String className = object.getClass().getName();
		if (className.startsWith("java.lang") || className.startsWith("java.sql")) {
			if (object instanceof String) {
				return "\"" + object.toString() + "\"";
			} else {
				return object.toString();
			}
		} else if (object instanceof Object[]) {
			String result = "array[";
			for (int i = 0; i < ((Object[]) object).length; i++) {
				result += postgresSqlStringify(((Object[]) object)[i]) + ",";
			}
			if (result.endsWith(",")) {
				result = result.substring(0, result.length() - 1) + "]";
			}
			return result;
		} else {
			try {
				return process(object);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error(e.getMessage(), e);
			}
		}
		return null;
	}

	private static String process(Object object)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String dbPojo = "'{";
		ObjectStruct objectStruct = new ObjectStruct(object);
		Map<String, Method> getMethods = objectStruct.getGetMethods();
		Field[] fields = object.getClass().getDeclaredFields();
		for (int i = 0; fields != null && i < fields.length; i++) {
			Method getMethod = getMethods.get(fields[i].getName());
			if (getMethod != null) {
				Object childObject = getMethod.invoke(object);
				if (childObject != null) {
					if (childObject != null) {
						dbPojo += "\"" + fields[i].getName() + "\":" + postgresSqlStringify(childObject) + ",";
					}
				}
			}
		}
		if (dbPojo.endsWith(",")) {
			dbPojo = dbPojo.substring(0, dbPojo.length() - 1) + "}'::jsonb";
		}
		return dbPojo;
	}

}
