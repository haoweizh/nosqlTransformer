package com.qnvip.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.qnvip.mybatis.util.JsonToObject;

public class StringUtil {

	public static List<?> parseArray(String str, Class<?> claz) {
		List<?> list = null;
		if (str == null || str.length() == 0) {
			return null;
		}
		try {
			String[] strs = str.split(",");
			list = JsonToObject.convertArray(strs, claz);
			if (list.size() == 0) {
				list = null;
			}
		} catch (Exception e) {
			// TODO
		}
		return list;
	}

	public static String composeString(Collection<?> collection) {
		if (collection == null || collection.size() == 0) {
			return null;
		}
		StringBuilder str = new StringBuilder();
		collection.forEach(tmp -> str.append(",").append(tmp));
		return str.substring(1);
	}

	/**
	 * 首字母大写
	 * 
	 * <pre>
	 * StringUtils.uperFirst("hello") => Hello
	 * StringUtils.uperFirst("") => ""
	 * StringUtils.uperFirst(null) => ""
	 * StringUtils.uperFirst("helloWorld") => HelloWorld
	 * </pre>
	 * 
	 * @param str
	 * 
	 */
	public static String upperFirst(String str) {
		if (StringUtils.isEmpty(str))
			return StringUtils.EMPTY;
		return StringUtils.upperCase(String.valueOf(str.charAt(0))) + StringUtils.substring(str, 1);
	}
	
	/**
	 * 将list中对象的指定字段取出用分隔符连接组成字符串
	 * 
	 * @param list
	 * @param column
	 *            指定字段
	 * @param separator
	 *            分隔符
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static String getColumnValue(List<?> list, String column, String separator)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		if (list == null || list.size() == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		Method m = null;
		for (Object object : list) {
			m = object.getClass().getMethod("get" + upperFirst(column));
			sb.append(separator).append(m.invoke(object));
		}
		return sb.substring(1);
	}

	public static String getColumnValue(List<?> list, String column)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return getColumnValue(list, column, ",");
	}

	/**
	 * 将list中对象的指定字段取出用分隔符连接组成字符串
	 * 
	 * @param list
	 * @param column
	 *            指定字段
	 * @param separator
	 *            分隔符
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static String getNestedColumnValue(List<?> list, String column)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return getNestedColumnValue(list, column, ",");
	}
	
	/**
	 * 将list中对象的指定字段取出用分隔符连接组成字符串
	 * 
	 * @param list
	 * @param column
	 *            指定字段
	 * @param separator
	 *            分隔符
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static String getNestedColumnValue(List<?> list, String column, String separator)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		if (list == null || list.size() == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		for (Object object : list) {
			sb.append(separator).append(getNestedValue(object, column, 0));
		}
		return sb.substring(1);
	}
	
	private static Object getNestedValue(Object object, String column, int depth) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String[] columns = column.split("\\.");
		Method m = object.getClass().getMethod("get" + upperFirst(columns[depth]));
		Object target = m.invoke(object);
		if (depth == (columns.length - 1))
			return target;
		else
			return getNestedValue(target, column, ++depth);
	}
}
