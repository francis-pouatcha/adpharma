package org.adorsys.adpharma.beans.importExport;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;

import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.DocumentsPath;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
@Service
public class LigneApproImportExportService extends ImportExportService<LigneApprovisionement> {

	@Override
	public  List<String> useFieldName(){
		String[] fieldNames = {"cip","qte","pa","pv"} ;
	    return	Arrays.asList(fieldNames);
		
		
	}
	@Override
	public LigneApprovisionement itemFromSheetRow(Cell...cells ){
		if(cells == null )return null ;
		LigneApprovisionement item = new LigneApprovisionement();
		List<Produit> products = Produit.findProduitsByCip(cells[0].getContents()).getResultList();
		BigInteger qte = new BigDecimal(cells[1].getContents()).toBigInteger();
		BigDecimal pa = BigDecimal.ZERO;
		BigDecimal pv = BigDecimal.ZERO;
		try {
			 pa = new BigDecimal(StringUtils.remove(cells[2].getContents(), "�").trim());
			 pv = new BigDecimal(StringUtils.remove(cells[3].getContents(), "�").trim());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(!(products.isEmpty() || qte ==null)){
			Produit prd = products.iterator().next();
			item.setProduit(prd);
			item.setCip(prd.getCip());
			item.setDatePeremtion(DateUtils.addYears(new Date(), 2));
			item.setDateSaisie(new Date());
			item.setAgentSaisie(SecurityUtil.getUserName());
			item.setQuantiteAprovisione(qte);
			item.setPrixAchatUnitaire(pa);
			item.setPrixVenteUnitaire(pv);
			item.CalculePaTotal();
			item.CalculeQteEnStock();
//			if(qte.intValue() == 0) return null;
			return item ;
		}
		return null ;
		
		
	}
	
	
	/**
	 * extract data from sheet row begining by second row we supose thath the first row containt row name
	 * @param sheet
	 * @param inventaire
	 * @return
	 */
	public   List<LigneApprovisionement> importListFromSheet(Sheet sheet,Approvisionement ap) {
		if(isfieldNamesMacthed(sheet, useFieldName())){
			List<LigneApprovisionement> items = new ArrayList<LigneApprovisionement>();
			int rows = sheet.getRows();
			for (int i = 1; i < rows; i++) {
				Cell[] row = sheet.getRow(i);
				LigneApprovisionement itemFromSheetRow = itemFromSheetRow(row);
				if(itemFromSheetRow!=null){
				itemFromSheetRow.setApprovisionement(ap);
				itemFromSheetRow.persist();
				Produit produit = itemFromSheetRow.getProduit();
				produit.addproduct(itemFromSheetRow.getQuantiteAprovisione());
				produit.merge();
				ap.increaseMontant(itemFromSheetRow.getPrixAchatTotal());
				items.add(itemFromSheetRow);
				}
			}
			return items ;
		}
		return null ;
	}
	
	@Override
	public HSSFWorkbook exportToxlsFile(List<LigneApprovisionement> listclazz) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = createSheetWithHeader(workbook);
		insertDataIntheSheet(sheet, listclazz);
		return workbook ;
	}
	@Override
	public File exportToTxtFile(List<LigneApprovisionement> listclazz) throws IOException {
		String SEPARATEUR= "$" ;
		File fileToSend = new File(DocumentsPath.ROOT_DIR+"catalogueCipm.txt");
		String lineToWrite ="cipm"+SEPARATEUR+"cip"+SEPARATEUR+"designation"+SEPARATEUR+"quantite"+SEPARATEUR +"prix"+SEPARATEUR+"emplacement";
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(lineToWrite);
		for (LigneApprovisionement ligne : listclazz) {
			lineToWrite = ligne.getCipMaison()+SEPARATEUR+ligne.getCip()+SEPARATEUR+ligne.getDesignation()+SEPARATEUR+ligne.getQuantieEnStock()+
					SEPARATEUR+ligne.getPrixVenteUnitaire().intValue()+SEPARATEUR+ligne.getProduit().getRayon().getName();
			arrayList.add(lineToWrite);
		}
		FileUtils.writeLines(fileToSend, arrayList);
		return fileToSend ;
	}
	
	
	
	private HSSFSheet createSheetWithHeader(HSSFWorkbook workbook) {
		if(workbook == null )throw new IllegalArgumentException("Null Argument Is not required here. Invalid sheet value !");
		HSSFSheet sheet = workbook.createSheet("cataloqueCipm");
		HSSFRow row = sheet.createRow(0);
		row.createCell(0).setCellValue("cipm");
		row.createCell(1).setCellValue("cip");
		row.createCell(2).setCellValue("designation");
		row.createCell(3).setCellValue("quantite");
		row.createCell(4).setCellValue("prix");
		row.createCell(5).setCellValue("emplacement");
		return sheet;
	}
	
	private void insertDataIntheSheet(HSSFSheet sheet,List<LigneApprovisionement> data){
		for(int i = 0; i < data.size(); i++){
			int j = i+1;
			LigneApprovisionement rowData = data.get(i);
			HSSFRow row = sheet.createRow(j);
			row.createCell(0 ).setCellValue(rowData.getCipMaison());
			row.createCell(1).setCellValue(rowData.getCip());
			row.createCell(2).setCellValue(rowData.getDesignation());
			row.createCell(3).setCellValue(rowData.getQuantieEnStock()+"");
			row.createCell(4).setCellValue(rowData.getPrixVenteUnitaire()+"");
			row.createCell(5).setCellValue(rowData.getProduit().getRayon().getName());
		}
		return ;
	}
}

