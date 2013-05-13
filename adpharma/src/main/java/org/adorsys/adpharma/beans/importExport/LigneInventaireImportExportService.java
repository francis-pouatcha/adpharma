package org.adorsys.adpharma.beans.importExport;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;

import jxl.Cell;
import jxl.Sheet;

import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
import org.springframework.stereotype.Service;

/**
 * @author adorsys-clovis
 *
 */
@Service
public class LigneInventaireImportExportService extends ImportExportService<LigneInventaire>  {

	@Override
	public  List<String> useFieldName(){
		String[] fieldNames = {"cip","qte"} ;
	    return	Arrays.asList(fieldNames);
		
		
	}
	@Override
	public LigneInventaire itemFromSheetRow(Cell...cells ){
		if(cells == null )return null ;
		LigneInventaire item = new LigneInventaire();
		List<Produit> products = Produit.findProduitsByCip(cells[0].getContents()).getResultList();
		BigInteger qte = new BigInteger(cells[1].getContents());
		if(!products.isEmpty()){
			Produit prd = products.iterator().next();
			item.setProduit(prd);
			item.setQteEnStock(prd.getQuantiteEnStock());
			item.setQteReel(qte);
			item.setDateSaisie(new Date());
			item.calculerEcart();
			item.caculMontantEcart();
			return item ;
		}
		return null ;
		
		
	}
	
	
	public void mergeFromWorkbook(Inventaire targetClazz, Sheet sheet)
    {
		if(isfieldNamesMacthed(sheet, useFieldName())){
        int rows;
        rows = sheet.getRows();
        for (int i = 1; i < rows; i++) {
			Cell[] currentRow = sheet.getRow(i);
			LigneInventaire itemFromSheetRow = itemFromSheetRow(currentRow);
			LigneInventaire hasItem = targetClazz.hasItem(itemFromSheetRow);
			if(hasItem!=null){
				if(hasItem.getQteReel().intValue() == itemFromSheetRow.getQteReel().intValue()){
					continue;
				}else {
					hasItem.setQteReel(itemFromSheetRow.getQteReel());
					hasItem.calculerEcart();
					hasItem.caculMontantEcart();
				}
				
			}else {
				itemFromSheetRow.setInventaire(targetClazz);
				itemFromSheetRow.calculerEcart();
				itemFromSheetRow.caculMontantEcart();
				itemFromSheetRow.persist();
				targetClazz.getLigneInventaire().add(itemFromSheetRow);
			}
		}
		}
    }
	
	
	/**
	 * extract data from sheet row begining by second row we supose thath the first row containt row name
	 * @param sheet
	 * @param inventaire
	 * @return
	 */
	public   List<LigneInventaire> importListFromSheet(Sheet sheet,Inventaire inventaire) {
		if(isfieldNamesMacthed(sheet, useFieldName())){
			ArrayList<LigneInventaire> items = new ArrayList<LigneInventaire>();
			int rows = sheet.getRows();
			for (int i = 1; i < rows; i++) {
				Cell[] row = sheet.getRow(i);
				LigneInventaire itemFromSheetRow = itemFromSheetRow(row);
				if(itemFromSheetRow!=null){
				itemFromSheetRow.setInventaire(inventaire);
				itemFromSheetRow.persist();
				items.add(itemFromSheetRow);
				}
			}
			return items ;
		}
		return null ;
	}

}
