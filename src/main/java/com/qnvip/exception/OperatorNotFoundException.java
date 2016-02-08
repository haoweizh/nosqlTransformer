package com.qnvip.exception;

/**
 * 操作者没找到异常
 * 
 * @author Ericlin
 */
public class OperatorNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 229605100874055369L;

	public OperatorNotFoundException(Throwable cause) {
    	super(cause);
    }	    
    public OperatorNotFoundException(String string, Throwable throwable) {
    	super(string, throwable);
    }
    public OperatorNotFoundException(String string) {
    	super(string);
    }
    public OperatorNotFoundException() {
    	super();
    }
}
