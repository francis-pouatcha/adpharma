package org.adorsys.adpharma.beans.importExport.core;

/**
 * @author adorsys-GKC
 *
 */
public class NotMatchFieldException extends Exception {
	
	public NotMatchFieldException(Class clazz , String fieldName) {
		super(clazz.getName()+"don't have field whith name  : "+fieldName);

}
}
