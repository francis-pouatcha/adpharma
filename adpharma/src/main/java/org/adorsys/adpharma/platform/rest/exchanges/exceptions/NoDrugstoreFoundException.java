package org.adorsys.adpharma.platform.rest.exchanges.exceptions;

import org.apache.commons.lang.StringUtils;

/**
 * @author w2b
 *
 */
public class NoDrugstoreFoundException extends Exception {
	
	private final static String message = "NO DRUGSTRORE FOUND EXCEPTION";
	private static final long serialVersionUID = 1L;
	public NoDrugstoreFoundException(String msg){
		super(StringUtils.isBlank(msg)?message:msg);
	}
	public NoDrugstoreFoundException(){
		super(message);
	}

}
