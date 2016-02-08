package com.qnvip.mybatis.service;

public interface BaseService {

	public Object insert(Object object);

	public Integer delete(Object object);

	public Integer update(Object value, Object condition);
	
	public Integer count(Object condition, String otherCondition);
}
