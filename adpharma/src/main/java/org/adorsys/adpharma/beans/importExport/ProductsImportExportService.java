package org.adorsys.adpharma.beans.importExport;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;

import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.TauxMarge;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ProductsImportExportService extends ImportExportService<Produit>{

	@Override
	public List<String> useFieldName() {
		String[] fieldNames = {"cip","designation","pa","pv","rayon"} ;
		//String[] fieldNames = {"cip","designation"} ;
	    return	Arrays.asList(fieldNames);
		
	}
	
	public   List<Produit> importListFromSheet(Sheet sheet,Rayon rayon) {
		if(isfieldNamesMacthed(sheet, useFieldName())){
			List<Produit> items = new ArrayList<Produit>();
			int rows = sheet.getRows();
			for (int i = 1; i < rows; i++) {
				Cell[] row = sheet.getRow(i);
				Produit itemFromSheetRow = itemFromSheetRow(row);
				if(itemFromSheetRow!=null){
				if(rayon!=null)itemFromSheetRow.setRayon(rayon);
				if(itemFromSheetRow.existe())continue;
					itemFromSheetRow.persist();
				items.add(itemFromSheetRow);
				}
			}
			return items ;
		}
		return null ;
	}

	@Override
	public Produit itemFromSheetRow(Cell... cells) {
		if(cells == null )return null ;
		if(StringUtils.isNotBlank(cells[1].getContents())){
			Produit prd = new Produit();
			prd.setActif(Boolean.TRUE);
			prd.setCip(cells[0].getContents().trim());
			prd.setCommander(Boolean.TRUE);
			prd.setDesignation(cells[1].getContents());
			prd.setFabricant("-//-");
			prd.setFiliale(Filiale.findFiliale(new Long(1)));
			/*List<Rayon> resultList = Rayon.findRayonsByEmplacement(cells[4].getContents()).getResultList();
			if(!resultList.isEmpty()){
				prd.setRayon(resultList.iterator().next());
			}else {
				prd.setRayon(Rayon.findRayon(new Long(1)));
			}*/
			prd.setRayon(Rayon.findRayon(new Long(1)));
			prd.setPrixAchatU(ProcessHelper.stringToBigDecimal(StringUtils.remove(cells[2].getContents(), "�").trim()));
			prd.setPrixVenteU(ProcessHelper.stringToBigDecimal(StringUtils.remove(cells[3].getContents(), "�").trim()));
			prd.setPlafondStock(new BigInteger("50"));
			prd.setTauxDeMarge(TauxMarge.findTauxMarge(new Long(1)));
			prd.setTauxRemiseMax(BigDecimal.valueOf(2));
			return prd ;
		}
		return null ;
		}
	

}
