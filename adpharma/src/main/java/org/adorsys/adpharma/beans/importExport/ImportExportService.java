package org.adorsys.adpharma.beans.importExport;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public abstract class ImportExportService<T> {
	/**
	 * <p>use for import  list of object which depend of another object </p>
	 * @param sheet in which data to import are stored  
	 * @param dependClazz the class to use during import 
	 * @return the list of  object to import 
	 */
	public <D>  List<T> importListFromSheet(Sheet sheet,D dependClazz ) {return null;} 

	/**
	 * @param sheet
	 * @return
	 */
	public   List<T> importListFromSheet(Sheet sheet) {return null;} 

	public   T importFromSheet(Sheet sheet){return null ;} 

	public  <D> T importFromSheet(Sheet sheet,D dependClazz){return null ;} 

	public  HSSFWorkbook exportToxlsFile(T clazz){ return null ;} 

	public  HSSFWorkbook exportToxlsFile(List<T> listclazz) { return null ;}
	public  File exportToTxtFile(List<T> listclazz) throws IOException{ return null ;}
	public abstract List<String> useFieldName();
	public abstract T itemFromSheetRow(Cell...cells);
	
	public boolean isfieldNamesMacthed(Sheet sheet , List<String> fieldNames){
		if(sheet==null || fieldNames ==null) return false ;
		Cell[] row = sheet.getRow(0);
		for (int i = 0; i < fieldNames.size(); i++) {
			if(!StringUtils.equalsIgnoreCase(fieldNames.get(i), row[i].getContents())){
				return false;	
			}
		}
		return true ;

	}
}
