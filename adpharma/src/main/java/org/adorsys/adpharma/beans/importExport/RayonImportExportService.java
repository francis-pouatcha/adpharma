package org.adorsys.adpharma.beans.importExport;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;

import jxl.Cell;
import jxl.Sheet;

import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class RayonImportExportService extends ImportExportService<Rayon>{

	@Override
	public   List<Rayon> importListFromSheet(Sheet sheet) {
		ArrayList<Rayon> rayons = new ArrayList<Rayon>();
		if(isfieldNamesMacthed(sheet, useFieldName())){
			
			int rows = sheet.getRows();
			for (int i = 1; i < rows; i++) {
				Cell[] row = sheet.getRow(i);
				Rayon itemFromSheetRow = itemFromSheetRow(row);
				if(itemFromSheetRow!=null){
					System.out.println("rayon "+i+" "+rayons);
				if(itemFromSheetRow.existe())continue ;
				itemFromSheetRow.persist();
				rayons.add(itemFromSheetRow);
				}
			}
			
		}
		return rayons ;
	}
	
	@Override
	public  List<String> useFieldName(){
		String[] fieldNames = {"codeGeo","emplacement","libelle"} ;
	    return	Arrays.asList(fieldNames);
		
		
	}
	
	@Override
	public Rayon itemFromSheetRow(Cell...cells ){
		if(cells == null )return null ;
		if(StringUtils.isNotBlank(cells[2].getContents())){
		    Rayon item = new Rayon();
			item.setCodeGeo(cells[0].getContents());
			item.setEmplacement(cells[1].getContents());
			item.setName(cells[2].getContents());
			item.setMagasin(Site.findSite(new Long(1)));
			return item ;
		}
		return null ;
		}
		
	}

