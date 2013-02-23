package org.adorsys.adpharma.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.DestinationMvt;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de retour des reclamations fournisseurs
 * @author hsimo
 *
 */
@Service
public class ClaimsService{
	
	private static final Logger LOGS= LoggerFactory.getLogger(ClaimsService.class);
	
	private BigInteger qteRetour=BigInteger.ZERO;
	
	public BigInteger getQteRetour() {
		return qteRetour;
	}
	
	public void setQteRetour(BigInteger qteRetour) {
		this.qteRetour = qteRetour;
	}
	
	
	// Mise a jour de la quantite reclamee de la ligne d'approvisionement
	public void updateClaimQty(LigneApprovisionement ligne){
		BigInteger reclame = ligne.getQuantiteReclame();
		BigInteger result= null;
		try {
			result = reclame.subtract(qteRetour);
			ligne.setQuantiteReclame(result);
			ligne.merge();
			LOGS.info("Success de mise a jour de la quantite reclamee de la ligne");
		} catch (Exception e) {
			LOGS.debug("Erreur de mise a jour de la quantite reclamee");
		}
	}
	
	// Mise a jour de la quantite approvisionee de la ligne d'approvisionement
	public void updateQteApprovisione(LigneApprovisionement ligne){
		BigInteger aprovisione = ligne.getQuantiteAprovisione();
		BigInteger result= null;
		try {
			result= aprovisione.add(qteRetour);
			ligne.setQuantiteAprovisione(result);
			ligne.merge();
			LOGS.info("Succes de mise a jour de la quantite approvisionnee de la ligne");
		} catch (Exception e) {
			LOGS.debug("Erreur de mise a jour de la quantite approvisionnee produite a "+new Date()+ " par la classe: "+this.getClass());
		}
	}
	
	// Mise a jour de la quantite en stock de la ligne d'approvisionement
	public void updateQteEnStockLigne(LigneApprovisionement ligne){
		try {
			ligne.setQuantieEnStock(ligne.getQuantiteAprovisione());
			ligne.merge();
			LOGS.info("Succes de mise a jour de la quantite en stock de la ligne");
		} catch (Exception e) {
			LOGS.debug("Erreur de mise a jour de la quantite en stock de la ligne produite a "+new Date()+ " par la classe: "+this.getClass());
		}
	}
	
	// Mise a jour du prix d'achat total de la ligne d'approvisionement
	public void updatePrixTotalLigne(LigneApprovisionement ligne){
		BigDecimal prixAchatTotal = ligne.getPrixAchatTotal();
		BigDecimal result= null;
		try {
			result = new BigDecimal(qteRetour.multiply(ligne.getPrixAchatUnitaire().toBigInteger()));
			ligne.setPrixAchatTotal(prixAchatTotal.add(result));
			ligne.merge();
			LOGS.info("Succes de mise a jour du prix d'achat total de la ligne");
		} catch (Exception e) {
			LOGS.debug("Erreur de mise a jour du prix total de la ligne produite a "+new Date()+ " par la classe: "+this.getClass());
		}
	}
	
	// Mise a jour de la quantite en stock du produit
	public void updateQteEnStockProduit(LigneApprovisionement ligne){
		BigInteger quantiteEnStock = ligne.getProduit().getQuantiteEnStock();
		Produit produit = ligne.getProduit();
		BigInteger result=null;
		try {
			result = quantiteEnStock.add(qteRetour);
			produit.setQuantiteEnStock(result);
			produit.merge();
			LOGS.info("Succes de mise a jour de la quantite en stock du produit");
		} catch (Exception e) {
			LOGS.info("Erreur de mise a jour de la quantite en stock du produit produite a "+new Date()+ " par la classe: "+this.getClass());
		}
	}
	
	// Verifier si un approvisionement contient une reclamation ou pas
	public boolean hasReclamations(Approvisionement approvisionement){
		Set<LigneApprovisionement> ligneApprovisionement = approvisionement.getLigneApprivisionement();
		for(LigneApprovisionement line: ligneApprovisionement){
			BigInteger quantiteReclame = line.getQuantiteReclame();
			if(!quantiteReclame.equals(BigInteger.ZERO)){
				return true;
			}
		}
		return false;
	}
	
	// Mise a jour du montant total de l'approvisionement
	public void updateApprovisionement(LigneApprovisionement ligne){
		BigDecimal montantNap = ligne.getApprovisionement().getMontantNap();
		Approvisionement approvisionement= ligne.getApprovisionement();
		boolean hasReclamations = hasReclamations(approvisionement);
		BigDecimal result=null;
		try {
			result= new BigDecimal(qteRetour.multiply(ligne.getPrixAchatUnitaire().toBigInteger()));
			approvisionement.setMontantNap(montantNap.add(result));
			if(hasReclamations==true){
				approvisionement.setReclamations(Boolean.TRUE);
			}else{
				approvisionement.setReclamations(Boolean.FALSE);
			}
			approvisionement.merge();
			LOGS.info("Succes de mise a jour de l'approvisionement");
		} catch (Exception e) {
			LOGS.debug("Erreur de mise a jour de l'approvisionement produite a "+new Date()+ " par la classe: "+this.getClass());
		}
	}
	

	
	// Generation d'un mouvement de stock pour cet approvisionement
	public void generateMvt(LigneApprovisionement ligne){
		try {
			MouvementStock mouvementStock = new MouvementStock();
			mouvementStock.setAgentCreateur(SecurityUtil.getUserName());
			mouvementStock.setCip(ligne.getCip());
			mouvementStock.setCipM(ligne.getCipMaison());
			mouvementStock.setDesignation(ligne.getDesignation());
			mouvementStock.setOrigine(DestinationMvt.FOURNISSEUR);
			mouvementStock.setDestination(DestinationMvt.MAGASIN);
			mouvementStock.setNote("Entree de reclamation");
			mouvementStock.setDateCreation(new Date());
			// Quantite initiale en stock
			mouvementStock.setQteInitiale(ligne.getQuantieEnStock().subtract(qteRetour));
			mouvementStock.setQteInitiale(ligne.getQuantieEnStock());
			mouvementStock.setTypeMouvement(TypeMouvement.RETOUR_PRODUIT);
			mouvementStock.setQteDeplace(qteRetour);
			mouvementStock.setNumeroBordereau(ligne.getApprovisionement().getBordereauNumber());
			mouvementStock.setPAchatTotal(ligne.getPrixAchatTotal().toBigInteger());
			mouvementStock.persist();
		} catch (Exception e) {
			 System.out.println(e.getMessage());
		}
		
	}
	
	
	// Methode transactionelle de mise a jour du stock
	
	@Transactional(isolation=Isolation.DEFAULT, readOnly=true)
	public void updateStock(LigneApprovisionement ligne){
		    updateClaimQty(ligne);
		    
		    updateQteApprovisione(ligne);
		    
		    updateQteEnStockLigne(ligne);
		    
		    updatePrixTotalLigne(ligne);
		    
		    updateQteEnStockProduit(ligne);
		    
		    updateApprovisionement(ligne);
		    
		    generateMvt(ligne);
	}
	
	

}
