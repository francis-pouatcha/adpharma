package org.adorsys.adpharma.beans.importExport;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public abstract class ExportDataAbstractService<T> implements ExportDataService<T> {
	
	// The workbook for create sheet
	protected HSSFWorkbook workbook= new HSSFWorkbook();
	
	// List of positions of the sheet
	private static int HEADER_ROW_POSITION=0;
	
	// The used used to fill data into row and cells
	protected HSSFSheet sheet;
	
	// The name of the created sheet
	private String sheetName;
	
	public String getSheetName() {
		return sheetName;
	}
	
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	
	@Override
	public void exportData(String fileName, List<T> data, String[] columns) throws IOException, FileNotFoundException {
	   createSheetWithHeader(workbook, columns);
	}
	
	
	// Creeate a sheet with column header
	public void createSheetWithHeader(HSSFWorkbook book, String[] columns){
		if(book==null) throw new IllegalArgumentException("Null workbook is not accepted");
		
		// Create a new font for the header columns
		HSSFFont font = workbook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		HSSFCellStyle style= workbook.createCellStyle();
		style.setFont(font);
		// Create the sheet of the workbook
		sheet= book.createSheet(this.sheetName);
		Row row = sheet.createRow(HEADER_ROW_POSITION);
		
		for(int i=0; i<columns.length; i++){
			Cell cell = row.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(style);
		}
		return;
	}
	

}
