package com.qnvip.mybatis.sqlprovider;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.qnvip.mybatis.util.ObjectStruct;

/**
 * 0.3版本限制 1. 不支持int long double等基本类型 2. 不支持数组，list，map或任何其他集合类型 3.
 * 由于没有名称映射文件，要求数据库表名equals类名，数据库列名equals类中的属性名切第一个单词全小写 4. 只对com.qnvip开头的类进行处理
 * 
 * @author simon
 *
 */
public class SqlConditionProvider {

	private String condition = "";

	private Object object = null;

	public SqlConditionProvider(Object object) {
		this.object = object;
	}

	private void process(String baseKey, Object baseObject)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (baseObject == null) {
			return;
		}
		String className = baseObject.getClass().getName();
		if (className.startsWith("java.lang") || className.startsWith("java.sql")) {
			addCondition(baseKey, baseObject.toString());
		} else if (className.startsWith("com.qnvip") || className.startsWith("com.qncentury")) {
			selfDefineProcess(baseKey, baseObject);
		}
	}

	private void selfDefineProcess(String baseKey, Object baseObject)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ObjectStruct objectStruct = new ObjectStruct(baseObject);
		Map<String, Method> getMethods = objectStruct.getGetMethods();
		Field[] fields = baseObject.getClass().getDeclaredFields();
		for (int i = 0; fields != null && i < fields.length; i++) {
			Method getMethod = getMethods.get(fields[i].getName());
			if (getMethod != null) {
				Object childObject = getMethod.invoke(baseObject);
				if (childObject != null) {
					if (baseKey == null || baseKey.length() == 0) {
						process(fields[i].getName(), childObject);
					} else {
						process(baseKey + "->" + fields[i].getName(), childObject);
					}
				}
			}
		}
	}

	private void addCondition(String field, String value) {
		if (condition.length() > 0) {
			condition = condition + "and ";
		}
		if (field.contains("->")) {
			String[] fields = field.split("->");
			for (int i = 0; i < fields.length; i++) {
				if (i == 0) {
					condition += fields[i];
				} else {
					condition += "'" + fields[i] + "'";
				}
				if (i == fields.length - 2) {
					condition += "->>";
				} else if (i < fields.length - 2) {
					condition += "->";
				}
			}
		} else {
			condition += field;
		}
		condition += "='" + value + "' ";
	}

	public String getCondition() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (condition == null || condition.length() == 0) {
			process(null, object);
		}
		return condition;
	}

}
