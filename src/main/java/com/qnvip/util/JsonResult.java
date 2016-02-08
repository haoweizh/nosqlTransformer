package com.qnvip.util;

import org.apache.commons.lang3.StringUtils;

import com.qnvip.mybatis.util.ObjectToJson;

public class JsonResult {
	private Object values;
	private String errcode = "0";
	private String errmsg = StringUtils.EMPTY;
	
	public static JsonResult N(String errcode, String errmsg) {
		return new JsonResult().setErrcode(errcode).setErrmsg(errmsg); 
	}
	
	public static JsonResult N(String[] errInfo) {
		return new JsonResult().setErrcode(errInfo[0]).setErrmsg(errInfo[1]);
	}
	
	public static JsonResult N(Object obj) {
		return new JsonResult().setValues(obj);
	}
	
	public void setError(String[] errInfo) {
		this.errcode = errInfo[0];
		this.setErrmsg(errInfo[1]);
	}

	public String toJsonString() {
		return ObjectToJson.stringify(this);
	}

	public String getErrcode() {
		return errcode;
	}

	public JsonResult setErrcode(String errcode) {
		this.errcode = errcode;
		return this;
	}

	public Object getValues() {
		return values;
	}

	public JsonResult setValues(Object values) {
		this.values = values;
		return this;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public JsonResult setErrmsg(String errmsg) {
		this.errmsg = errmsg;
		return this;
	}
}
