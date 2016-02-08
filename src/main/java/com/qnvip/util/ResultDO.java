package com.qnvip.util;

import java.io.Serializable;

public class ResultDO<T> implements Serializable {
	private static final long serialVersionUID = -5953039510672472469L;
	private boolean success = false; // 执行是否成功
	private String errTrace;
	private T module; // 实际的返回结果

	public boolean isSuccess() {
		return this.success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrTrace() {
		return this.errTrace;
	}

	public void setErrTrace(String errTrace) {
		this.errTrace = errTrace;
	}

	public T getModule() {
		return this.module;
	}

	public void setModule(T module) {
		this.module = module;
	}
}
