package org.adorsys.adpharma.beans;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class InventaireProcess {


	private  Long invId;
	private Produit produit ;
	private LigneInventaire lineToUpdate ;
	private  List<LigneInventaire>  productSelected = new ArrayList<LigneInventaire>();
	private List<Produit> productResult = new ArrayList<Produit>();
	private String displayName ;
	private PharmaUser agent ;





	public  InventaireProcess(Long invId ){
		this.invId= invId ;
		displayName = displayName() ;

	}
	public  InventaireProcess( ){


	}


	public  InventaireProcess(Long invId ,List<LigneInventaire> line  ){
		this.invId= invId ;
		productSelected = line ;
		displayName = displayName() ;
	}

	public String displayName(){
		return Inventaire.findInventaire(invId).toString();
	}


	public static void makeStockCorrection(LigneApprovisionement line){

	}


}
