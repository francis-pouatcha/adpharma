package org.adorsys.adpharma.beans.importExport.core;

import java.util.HashMap;
/**
 * @author adorsys-GKC
 *
 */
public class FieldAndTypeMap extends HashMap<String, Class>{
	private String field ;
	private Class clazz ;
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public Class getClazz() {
		return clazz;
	}
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	
	public FieldAndTypeMap() {}
	
	public FieldAndTypeMap(String field , Class clazz) {
		this.clazz = clazz ;
		this.field = field ;
		
	}

} 
