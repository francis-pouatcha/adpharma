package org.adorsys.adpharma.platform.rest.exchanges.exceptions;

import org.apache.commons.lang.StringUtils;

/**
 * @author w2b
 *
 */
public class NoProviderFoundException extends Exception {
	private final static String message = "NO DRUGSTRORE FOUND EXCEPTION";
	private static final long serialVersionUID = 1L;
	public NoProviderFoundException(String msg){
		super(StringUtils.isBlank(msg)?message:msg);
	}
	
	public NoProviderFoundException(){
		super(message);
	}

}
