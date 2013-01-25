/**
 * 
 */
package org.adorsys.adpharma.beans.importExport;

import java.util.ArrayList;
import java.util.List;

import org.adorsys.adpharma.beans.DecomposedProductExcelRepresentation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @author w2b
 *
 */
public class ExportDecomposedProductService {
	private List<DecomposedProductExcelRepresentation> data = new ArrayList<DecomposedProductExcelRepresentation>();
	private String sheetName ;
	private static int HEADER_ROW_POSITION = 0;
	private static int FIRST_COLUMN_NUM = 0;
	private static int SECOND_COLUMN_NUM = 1;
	private static int THIRD_COLUMN_NUM = 2;
	private static int FOURTH_COLUMN_NUM = 3;
	private static int FIFTH_COLUMN_NUM=4;
	
	private int firstDataRownum = HEADER_ROW_POSITION +1 ;
	public ExportDecomposedProductService(
			List<DecomposedProductExcelRepresentation> data) {
		super();
		if(data == null) throw new IllegalArgumentException("Null Argument are not accepted");
		this.data = data;
	}

	public HSSFWorkbook export(){
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = createSheetWithHeader(workbook);
		insertDataIntheSheet(sheet, firstDataRownum);
		return workbook ;
	}
	private void insertDataIntheSheet(HSSFSheet sheet,int firstDataRowNum){
		for(int i = 0; i < data.size(); i++){
			int j = i+1;
			DecomposedProductExcelRepresentation rowData = data.get(i);
			HSSFRow row = sheet.createRow(j);
			row.createCell(FIRST_COLUMN_NUM ).setCellValue(rowData.getCipMaison());
			row.createCell(SECOND_COLUMN_NUM).setCellValue(rowData.getDesignation());
			row.createCell(THIRD_COLUMN_NUM).setCellValue(rowData.getSalePriceAsCFA());
			row.createCell(FOURTH_COLUMN_NUM).setCellValue(rowData.getProvider());
			row.createCell(FIFTH_COLUMN_NUM).setCellValue(rowData.getSite());
		}
		return ;
	}
	private HSSFSheet createSheetWithHeader(HSSFWorkbook workbook) {
		if(workbook == null )throw new IllegalArgumentException("Null Argument Is not required here. Invalid sheet value !");
		if(this.sheetName == null || this.sheetName.isEmpty()) sheetName = "Decomposed Product";
		HSSFSheet sheet = workbook.createSheet(this.sheetName);
		HSSFRow row = sheet.createRow(HEADER_ROW_POSITION);
		row.createCell(FIRST_COLUMN_NUM).setCellValue("cipm");
		row.createCell(SECOND_COLUMN_NUM).setCellValue("designation");
		row.createCell(THIRD_COLUMN_NUM).setCellValue("pv");
		row.createCell(FOURTH_COLUMN_NUM).setCellValue("fournisseur");
		row.createCell(FIFTH_COLUMN_NUM).setCellValue("site");
		return sheet;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	
}
