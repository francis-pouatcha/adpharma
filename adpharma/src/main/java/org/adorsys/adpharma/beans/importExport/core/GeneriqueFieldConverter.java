package org.adorsys.adpharma.beans.importExport.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public   class GeneriqueFieldConverter implements FieldConverter  {

	@Override
	public <T> T getAsObject(String value, Class<T> targetClazz)
			throws FieldConvertionFailedException {
    try {
    	if(isSameClass(targetClazz, BigInteger.class)) return (T) new BigInteger(value);
    	if(isSameClass(targetClazz, BigDecimal.class)) return (T) new BigDecimal(value);
    	if(isSameClass(targetClazz, Double.class)) return (T) new Double(value);
    	if(isSameClass(targetClazz, Integer.class)) return (T) new Integer(value);
    	if(isSameClass(targetClazz, Long.class)) return (T) new Long(value);
    	if(isSameClass(targetClazz, Date.class)) return (T) new Date(value);
    	if(isSameClass(targetClazz, String.class)) return (T) new String(value);
			
		} catch (Exception e) {
			new FieldConvertionFailedException(value, targetClazz);
		}
		return null;
	}

	@Override
	public <T> String getAsString(T objectToConvert)
			throws FieldConvertionFailedException {
		try {
			
		} catch (Exception e) {
			new FieldConvertionFailedException(objectToConvert, String.class);
		}
		return null;
	}

	
	public boolean isSameClass(Class clazz1 ,Class clazz2){
		if(StringUtils.equalsIgnoreCase(clazz1.getName(), clazz2.getName()));
		return false ;
		
		
	}

	@Override
	public boolean isSupportedClass(Class clazz) {
		if(isSameClass(clazz, BigInteger.class)) return true ;
    	if(isSameClass(clazz, BigDecimal.class)) return true ;
    	if(isSameClass(clazz, Double.class)) return true ;
    	if(isSameClass(clazz, Integer.class)) return true ;
    	if(isSameClass(clazz, Long.class)) return true ;
    	if(isSameClass(clazz, Date.class)) return true ;
    	if(isSameClass(clazz, String.class)) return true ;
		return false;
	}
	

	
}
