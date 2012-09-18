package org.adorsys.adpharma.beans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.CipMgenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class CommandeProcess {
	
	
	private  Long cmdId;
	private Produit produit ;
	private String fournisseur ;
	private LigneCmdFournisseur lineToUpdate ;
	
    private  List<LigneCmdFournisseur>  productSelected = new ArrayList<LigneCmdFournisseur>();
    private List<Produit> productResult = new ArrayList<Produit>();
    private String displayName ;
    private PharmaUser commandAgent ;
    
   
    
    public  CommandeProcess(Long cmdId , String fournisseur ){
    	this.cmdId= cmdId ;
     	this.fournisseur = fournisseur ;
    	displayName = displayName() ;
    	
    }
 
 
 public  CommandeProcess(Long cmdId ,List<LigneCmdFournisseur> line , String fournisseur ){
 	this.cmdId= cmdId ;
 	this.fournisseur = fournisseur ;
 	productSelected = line ;
 	displayName = displayName() ;
 }
 
 public String displayName(){
	 return CommandeFournisseur.findCommandeFournisseur(cmdId).toString();
 }
 

}
