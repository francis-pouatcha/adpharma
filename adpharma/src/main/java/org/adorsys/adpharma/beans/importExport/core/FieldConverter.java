package org.adorsys.adpharma.beans.importExport.core;

import java.math.BigInteger;
import java.util.Date;

import com.ibm.icu.math.BigDecimal;

/**
 * converter interface use to converter string volue to specifique object 
 * and Object to string 
 * @author adorsys-clovis
 *
 * @param <T>
 */

public interface  FieldConverter {
	
	/**
	 * @author adorsys-clovis
	 *
	 * @param value  string to convert to target type T
	 * @return Object
	 */
	public <T> T getAsObject(String  value ,Class<T> targetClazz) throws FieldConvertionFailedException ;
		 
	
	/**
	 * @author adorsys-clovis
	 *
	 * @param clazz  target  type to convert to String 
	 * @return String
	 */
	public <T> String getAsString(T  objectToConvert) throws FieldConvertionFailedException;
	
	public boolean isSupportedClass(Class  clazz) ;


}
