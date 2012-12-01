/**
 * 
 */
package org.adorsys.adpharma.platform.rest.exchanges;

/**
 * @author w2b
 *
 */
public enum PlatformRestException {
	
	NO_PROVIDER_FOUND_EXCEPTION(""),NO_DRUGSTORE_FOUND_EXCEPTION(""),UNKNOW_EXCEPTION(""),NO_ORDER_FOUND_EXCEPTION("") ;
	
	private String message ;
	
	
	
	  PlatformRestException (String message){
		this.message = message ;
	}
	  PlatformRestException (){
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
