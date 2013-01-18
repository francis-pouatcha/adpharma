package org.adorsys.adpharma.beans.importExport.core;

public class NotMatchFieldException extends Exception {
	
	public NotMatchFieldException(Class clazz , String fieldName) {
		super(clazz.getName()+"don't have field whith name  : "+fieldName);

}
}
