package com.qnvip.mybatis.sqlprovider;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.qnvip.mybatis.exception.ParameterNotFoundException;

public class SqlProvider {

	private String getTable(Object object) {
		String table = object.getClass().getName();
		if (table.contains(".")) {
			table = table.substring(table.lastIndexOf(".") + 1);
		}
		return table;
	}

	public String select(Map<String, Object> parameter)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String otherCondition = (String) parameter.get("otherCondition");
		String targetColumns = (String) parameter.get("targetColumns");
		Object object = parameter.get("condition");
		if (object == null) {
			new ParameterNotFoundException(
					"Require an 'object' named key to return object value as the target select from table.");
		}
		if (targetColumns == null) {
			targetColumns = "*";
		}
		String table = getTable(object);
		SqlConditionProvider sqlConditionProvider = new SqlConditionProvider(object);
		if (otherCondition == null) {
			otherCondition = "";
		} else {
			otherCondition = otherCondition.trim();
			if (!(otherCondition.toLowerCase().startsWith("limit")
					|| otherCondition.toLowerCase().startsWith("order by"))) {
				otherCondition = " and " + otherCondition;
			}
		}
		String condition = sqlConditionProvider.getCondition();
		if (condition != null && condition.length() > 0) {
			condition = " and " + condition;
		}
		String clause = "select " + targetColumns + " from " + table + " where 0=0 " + condition + otherCondition;
		System.out.println(clause);
		return clause;
	}
	
	public String count(Map<String, Object> parameter) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object object = parameter.get("condition");
		String otherCondition = (String) parameter.get("otherCondition");
		if (object == null) {
			new ParameterNotFoundException(
					"Require an 'object' named key to return object value as the target select from table.");
		}
		String table = getTable(object);
		SqlConditionProvider sqlConditionProvider = new SqlConditionProvider(object);
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from ").append(table).append(" where 0=0");
		String condition = sqlConditionProvider.getCondition();
		if (StringUtils.isNotEmpty(condition)) {
			sql.append(" and ").append(condition);
		}
		if (StringUtils.isNotEmpty(otherCondition)) {
			sql.append(" and ").append(otherCondition);
		}
		return sql.toString();
	}

	public String delete(Object object)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (object == null) {
			new ParameterNotFoundException("Require an 'object' to delete.");
		}
		String table = getTable(object);
		SqlConditionProvider sqlConditionProvider = new SqlConditionProvider(object);
		String clause = "delete from " + table + " where " + sqlConditionProvider.getCondition();
		System.out.println(clause);
		return clause;
	}

	public String update(Map<String, Object> parameter) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, JsonGenerationException, JsonMappingException, IOException {
		Object value = parameter.get("value");
		Object condition = parameter.get("condition");
		if (value == null) {
			new ParameterNotFoundException(
					"Require an 'value' named key to return object value as the target update table.");
		}
		if (condition == null) {
			new ParameterNotFoundException(
					"Require an 'condition' named key to return object value to generate update where clause.");
		}
		SqlUpdateProvider sqlUpdateProvider = new SqlUpdateProvider(value);
		SqlConditionProvider sqlConditionProvier = new SqlConditionProvider(condition);
		String clause = sqlUpdateProvider.getSetStatement() + " where " + sqlConditionProvier.getCondition();
		System.out.println(clause);
		return clause;
	}

	public String insert(Object object) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, JsonGenerationException, JsonMappingException, IOException {
		if (object == null) {
			new ParameterNotFoundException("Require an 'object' to insert.");
		}
		SqlInsertProvider sqlInsertProvider = new SqlInsertProvider(object);
		String clause = sqlInsertProvider.getInsertStatement();
		System.out.println(clause);
		return clause;
	}

}
