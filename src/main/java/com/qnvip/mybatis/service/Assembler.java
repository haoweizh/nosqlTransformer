package com.qnvip.mybatis.service;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

public interface Assembler {

	@InsertProvider(type = com.qnvip.mybatis.sqlprovider.SqlProvider.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	public Integer insert(Object object);

	@DeleteProvider(type = com.qnvip.mybatis.sqlprovider.SqlProvider.class, method = "delete")
	public Integer delete(Object object);

	@UpdateProvider(type = com.qnvip.mybatis.sqlprovider.SqlProvider.class, method = "update")
	public Integer update(@Param("value") Object value, @Param("condition") Object condition);
	
	@UpdateProvider(type = com.qnvip.mybatis.sqlprovider.SqlProvider.class, method = "update")
	public Integer updateWithCondition(@Param("value") Object value, @Param("condition") Object condition,  @Param("otherCondition") String otherCondition);
	
	@SelectProvider(type = com.qnvip.mybatis.sqlprovider.SqlProvider.class, method = "count")
	public Integer count(@Param("condition") Object condition, @Param("otherCondition") String otherCondition);
	
}
