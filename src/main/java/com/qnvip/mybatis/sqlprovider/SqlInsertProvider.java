package com.qnvip.mybatis.sqlprovider;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.qnvip.mybatis.util.ObjectToJson;
import com.qnvip.mybatis.util.ObjectStruct;

public class SqlInsertProvider {

	private Object object = null;

	private String insert = null;

	private Map<String, String> map = new HashMap<String, String>();

	public SqlInsertProvider(Object object) {
		this.object = object;
		String table = object.getClass().getName();
		if (table != null && table.contains(".")) {
			insert = "insert into " + table.substring(table.lastIndexOf(".") + 1);
		}
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
					map.put(fields[i].getName(), ObjectToJson.postgresSqlStringify(childObject));
				}
			}
		}
	}

	public String getInsertStatement() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, JsonGenerationException, JsonMappingException, IOException {
		if (map.size() == 0) {
			process();
			Iterator<String> itr = map.keySet().iterator();
			String left = "(";
			String right = "values(";
			while (itr.hasNext()) {
				String key = itr.next();
				String value = map.get(key);
				if (!left.endsWith("(")) {
					left += ",";
					right += ",";
				}
				left += key;
				right += value;
			}
			insert += left + ")" + right + ")";
		}
		return insert;
	}
}
