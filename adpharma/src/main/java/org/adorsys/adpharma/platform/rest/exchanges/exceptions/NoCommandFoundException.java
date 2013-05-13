package org.adorsys.adpharma.platform.rest.exchanges.exceptions;

import org.apache.commons.lang.StringUtils;

public class NoCommandFoundException extends Exception{
	private final static String message = "NO ORDER FOUND EXCEPTION";
	private static final long serialVersionUID = 1L;
	public NoCommandFoundException(String msg){
		super(StringUtils.isBlank(msg)?message:msg);
	}
	public NoCommandFoundException(){
		super(message);
	}
	
}
