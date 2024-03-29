package org.adorsys.adpharma.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Configuration;
import org.adorsys.adpharma.domain.DetteClient;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneCmdClient;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.OperationCaisse;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.RoleName;
import org.adorsys.adpharma.domain.SalesConfiguration;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.domain.TypeOpCaisse;
import org.springframework.stereotype.Service;


@Service
public class SaleService {

	public   LigneCmdClient  generateLigneCmd(LigneApprovisionement ligneApp , BigInteger qte ,BigDecimal remise ,CommandeClient commandeClient) {
		LigneCmdClient ligneCmdClient = null;
		BigDecimal prixVente= getPrixVenteLigne(qte, ligneApp);
		if (qte.intValue()>0) {
			ligneCmdClient = new LigneCmdClient();							
			ligneCmdClient.setCip(ligneApp.getCip());
			ligneCmdClient.setCipM(ligneApp.getCipMaison());
			ligneCmdClient.setCommande(commandeClient);
//			ligneCmdClient.setPrixUnitaire(ligneApp.getPrixVenteUnitaire());
			ligneCmdClient.setPrixUnitaire(prixVente);
			ligneCmdClient.setQuantiteCommande(qte);
			ligneCmdClient.setDesignation(ligneApp.getDesignation());
			ligneCmdClient.setProduit(ligneApp);
			if (remise != null) {
				ligneCmdClient.setRemise(remise);
			}
			ligneCmdClient.calculPrixTotal();
			ligneCmdClient.calculremiseTotal();
		}

		return ligneCmdClient ;
	}
	
	// Fonction qui retourne le prix de vente unitaire pour la configuration de vente active(vente en details, vente en semi-gros, vente en gros)
	public BigDecimal getPrixVenteLigne(BigInteger qte, LigneApprovisionement line){
		List<SalesConfiguration> configurations = SalesConfiguration.findAllSalesConfigurations();
		BigDecimal prixVente=line.getPrixVenteUnitaire();
		for(SalesConfiguration config: configurations){
			if(config.getActiveConfig()==Boolean.TRUE){
				BigInteger minValue = config.getMinValue();
				BigInteger maxValue = config.getMaxValue();
				if(qte.intValue()>= minValue.intValue() && qte.intValue()<= maxValue.intValue()){
					prixVente= line.getPrixVenteUnitaire().multiply((new BigDecimal(1).subtract(config.getTauxReduction().divide(new BigDecimal(100))))); 
				}
			}else{
				prixVente= line.getPrixVenteUnitaire();
			}
		}
		return prixVente;
	}

	public  void addline(Long lineId , BigInteger quantite ,BigDecimal remise ,CommandeClient commandeClient){

		BigInteger qte =quantite ;

		LigneApprovisionement ligneApp = LigneApprovisionement.findLigneApprovisionement(lineId);

		LigneCmdClient sameCipM = commandeClient.getProdcutWhithCipM(ligneApp.getCipMaison());

		if(sameCipM == null) {

			BigInteger enStock = ligneApp.getQuantieEnStock();

			if (enStock.intValue()>=qte.intValue()) {

				LigneCmdClient generateLigne = generateLigneCmd(ligneApp, qte, remise, commandeClient);
				if(generateLigne != null){
					generateLigne.persist();
					qte = qte.subtract(qte);
				}


			}else {

				if (enStock.intValue() > 0) {
					LigneCmdClient generateLigne = generateLigneCmd(ligneApp, enStock, remise, commandeClient);
					if(generateLigne != null) {
						generateLigne.persist();
						qte = qte.subtract(enStock);
					}


				}

			}

		}

		List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndCipEquals(BigInteger.ONE, ligneApp.getCip()).getResultList();

		List<LigneApprovisionement> allLineApForSameCip = commandeClient.getAllLineApForSameCip(ligneApp.getCip());

		resultList.removeAll(allLineApForSameCip);

		resultList.remove(ligneApp);

		BigInteger qteAfterUpdate = commandeClient.updateQteOfItem(qte, remise, ligneApp.getCip());

		if (qteAfterUpdate.intValue()>0 && !resultList.isEmpty()) {

			for (LigneApprovisionement line : resultList) {		
				if(qteAfterUpdate.intValue()<=0) break;
				BigInteger enStock = line.getQuantieEnStock();
				if (enStock.intValue()>=qteAfterUpdate.intValue()) {
					LigneCmdClient generateLigne = generateLigneCmd(line, qteAfterUpdate, remise, commandeClient);
					if(generateLigne != null){
						generateLigne.persist();
						qteAfterUpdate = qteAfterUpdate.subtract(qteAfterUpdate);
					}
				}else {
					LigneCmdClient generateLigne = generateLigneCmd(line, enStock, remise, commandeClient);
					if(generateLigne != null){
						generateLigne.persist();
						qteAfterUpdate = qteAfterUpdate.subtract(enStock);
					}


				}

			}

		}else {
			if(sameCipM != null) {
				sameCipM.increaseCmdQte(qteAfterUpdate, remise);

			}else {
				LigneCmdClient generateLigne = generateLigneCmd(ligneApp, qteAfterUpdate, remise, commandeClient);
				if(generateLigne != null) generateLigne.persist();
			}
		}
	}

	public  void addlineForcer(Long lineId , BigInteger quantite ,BigDecimal remise ,CommandeClient commandeClient){

		LigneApprovisionement ligneApp = LigneApprovisionement.findLigneApprovisionement(lineId);

		List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndCipEquals(BigInteger.ONE, ligneApp.getCip()).getResultList();

		List<LigneApprovisionement> allLineApForSameCip = commandeClient.getAllLineApForSameCip(ligneApp.getCip());

		if(!allLineApForSameCip.isEmpty() && !resultList.isEmpty())resultList.removeAll(allLineApForSameCip);

		BigInteger qteAfterUpdate = commandeClient.updateQteOfItem(quantite,remise, ligneApp.getCip());
		int size = resultList.size();

		if (!resultList.isEmpty()) {

			for (LigneApprovisionement ligne : resultList) {
				if (resultList.lastIndexOf(ligne) == size-1) {
					generateLigneCmd(ligne, qteAfterUpdate, remise, commandeClient).persist();
					break ;
				}else {
					if ((ligne.getQuantieEnStock().intValue() < qteAfterUpdate.intValue())) {
						LigneCmdClient generateLigne = generateLigneCmd(ligne, ligne.getQuantieEnStock(), remise, commandeClient);
						if (generateLigne!=null) {
							generateLigne.persist();
							qteAfterUpdate = qteAfterUpdate.subtract(ligne.getQuantieEnStock());
						}

					}else{
						LigneCmdClient generateLigne = generateLigneCmd(ligne, qteAfterUpdate, remise, commandeClient);
						if (generateLigne!=null)generateLigne.persist();
						break ;
					}
				}

			}
		}else {
			LigneCmdClient sameCipM = commandeClient.getProdcutWhithCipM(ligneApp.getCipMaison());
			if(sameCipM != null) sameCipM.increaseCmdQte(qteAfterUpdate, remise);

		}


	}


	public static boolean enableSaleCash(Configuration conf,PharmaUser user){
		ArrayList<RoleName> enableRole = new ArrayList<RoleName>();
		enableRole.add(RoleName.ROLE_CASHIER);
		enableRole.add(RoleName.ROLE_SITE_MANAGER);
		if(conf==null || user==null)throw new IllegalArgumentException("conf or user are required !");
		if(!conf.getSaleCash().booleanValue()) return false;
		if(!user.hasAnyRole(enableRole)) return false ;
		return true ;
	}

	/**
	 * usefful method for generating list of product for a PARTICULAR CIPM order by datesaisie
	 * @param produit
	 * @param cmd
	 * @return list of oldcipm
	 */
	public static List<LigneApprovisionement> getoldProductLisForSale(Produit  produit , CommandeClient cmd){
		List<LigneApprovisionement> oldProductsList = new ArrayList<LigneApprovisionement>() ;
		if(produit == null || cmd==null) return oldProductsList ;
		List<LigneApprovisionement> resultList = LigneApprovisionement.findOldLigneApprovisionementsByQteStockAndProduit(produit, BigInteger.ONE).getResultList();
		if(!resultList.isEmpty()){
			oldProductsList = new ArrayList<LigneApprovisionement>(resultList) ;
			for (LigneApprovisionement orderItem : resultList) {
				LigneCmdClient sameCipm = cmd.getItemHasSameCipm(orderItem.getCipMaison());
				if(sameCipm!=null){
					if(sameCipm.getQuantiteCommande().equals(orderItem.getQuantieEnStock())){
						oldProductsList.remove(orderItem);
					}else {
						return oldProductsList ;
					}
				}else {
					return oldProductsList ;
				}

			}
		}
		return oldProductsList ;

	}
	
	
	/**
	 * 
	 * @return the first sale
	 */
	public MouvementStock getFirstSale(){
		MouvementStock mouvementStock = MouvementStock.findMouvementStocksByTypeMouvement(TypeMouvement.VENTE).getResultList().get(0); 
		return mouvementStock;
	}
	
	/**
	 * 
	 * @return the first cash
	 */
	public OperationCaisse getFirstCash(){
		OperationCaisse operationCaisse = OperationCaisse.findOperationCaissesByTypeOperation(TypeOpCaisse.ENCAISSEMENT).getResultList().get(0);
		return operationCaisse;
	}
	
	public DetteClient getFirstDebt(){
		DetteClient detteClient = DetteClient.findDetteClientsBySolderNotAndAnnulerNot(Boolean.TRUE, Boolean.TRUE).getResultList().get(0);
 		return detteClient;
	}



}


