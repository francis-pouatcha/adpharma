package org.adorsys.adpharma.utils;

import org.apache.commons.lang.StringUtils;

/**
 * Contract :-)
 * 
 * @author Willie Chieukam
 */
public class Contract {

	public static void notNull(String param, Object value) {
		if( value == null) {
			throw new IllegalArgumentException( param + " can not be null." );
		}
	}

	public static void notBlank(String param, String value) {
		if( StringUtils.isBlank(value) ) {
			throw new IllegalArgumentException( param + " can not be null." );
		}
	}

}
