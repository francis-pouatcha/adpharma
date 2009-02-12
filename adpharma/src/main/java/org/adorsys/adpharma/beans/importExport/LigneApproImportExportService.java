package org.adorsys.adpharma.beans.importExport;

import java.math.BigDecimal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;

import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.security.SecurityUtil;
import org.apache.commons.lang.time.DateUtils;
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
		BigInteger qte = new BigInteger(cells[1].getContents());
		BigDecimal pa = new BigDecimal(cells[2].getContents());
		BigDecimal pv = new BigDecimal(cells[3].getContents());
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
			if(qte.intValue() == 0) return null;
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
}
