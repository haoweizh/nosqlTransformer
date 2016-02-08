package com.qnvip.mybatis.sqlprovider;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.qnvip.mybatis.util.ObjectToJson;
import com.qnvip.mybatis.util.ObjectStruct;

public class SqlUpdateProvider {

	private Object object = null;

	private String setStatement = null;

	public SqlUpdateProvider(Object object) {
		this.object = object;
	}

	private void process() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			JsonGenerationException, JsonMappingException, IOException {
		ObjectStruct objectStruct = new ObjectStruct(object);
		Map<String, Method> getMethods = objectStruct.getGetMethods();
		Field[] fields = object.getClass().getDeclaredFields();
		for (int i = 0; fields != null && i < fields.length; i++) {
			Method getMethod = getMethods.get(fields[i].getName());
			if (getMethod != null) {
				Object childObject = getMethod.invoke(object);
				if (childObject != null) {
					if (setStatement.contains("=")) {
						setStatement += ",";
					}
					String value = ObjectToJson.postgresSqlStringify(childObject).trim();
					setStatement += fields[i].getName() + "=" + value;
				}
			}
		}
	}

	public String getSetStatement() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			JsonGenerationException, JsonMappingException, IOException {
		if (setStatement == null || setStatement.length() == 0) {
			String table = object.getClass().getName();
			if (table != null && table.contains(".")) {
				table = table.substring(table.lastIndexOf(".") + 1);
			}
			setStatement = "update " + table + " set ";
			process();
		}
		return setStatement;
	}

}
