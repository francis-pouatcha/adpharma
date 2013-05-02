package org.adorsys.adpharma.beans.importExport.core;

/**
 * @author adorsys-GKC
 *
 */
public class FieldConvertionFailedException extends Exception {
	
	private Object value ;
	private Class clazz ;
	
	public FieldConvertionFailedException(Object value ,Class clazz) {
		super(value+"can not be converted to "+ clazz.getName());
		this.value = value ;
		this.clazz = clazz ;
		
	}

}
