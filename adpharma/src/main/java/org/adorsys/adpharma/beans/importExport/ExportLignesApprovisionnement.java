package org.adorsys.adpharma.beans.importExport;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.adorsys.adpharma.beans.LigneApprovisionementExcelRepresentation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.springframework.stereotype.Service;

@Service
public class ExportLignesApprovisionnement extends ExportDataAbstractService<LigneApprovisionementExcelRepresentation> {
	String[] columns={"CIP", "CIPM", "DESIGNATION", "QTE EN STOCK", "EMPLACEMENT"};
	
	public String[] getColumns() {
		return columns;
	}
	
	public void setColumns(String[] columns) {
		this.columns = columns;
	}
	
	@Override
	public void exportData(String fileName, List<LigneApprovisionementExcelRepresentation> data, String[] columns) throws IOException, FileNotFoundException{
		super.setSheetName("Produits Inventaire");
		super.exportData(fileName, data, columns);
		insertData(super.sheet, data);
		FileOutputStream outputStream = new FileOutputStream("/tools/"+fileName);
		super.workbook.write(outputStream);
		outputStream.close();
	}
	
	public void insertData(HSSFSheet sheet, List<LigneApprovisionementExcelRepresentation> data){
		for(int i=0; i<data.size(); i++){
			int j=i+1;
			LigneApprovisionementExcelRepresentation representation = data.get(i);
			HSSFRow row = sheet.createRow(j);
			row.createCell(0).setCellValue(representation.getCip());
			row.createCell(1).setCellValue(representation.getCipm());
			row.createCell(2).setCellValue(representation.getDesignation());
			row.createCell(3).setCellValue(representation.getQteStock().toString());
			row.createCell(4).setCellValue(representation.getEmplacement());
		}
		return;
	}
	
	

}
