package org.adorsys.adpharma.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneCmdClient;
import org.springframework.stereotype.Service;


@Service
public class SaleService {

	public   LigneCmdClient  generateLigneCmd(LigneApprovisionement ligneApp , BigInteger qte ,BigDecimal remise ,CommandeClient commandeClient) {
		LigneCmdClient ligneCmdClient = null;
		if (qte.intValue()>0) {
			ligneCmdClient = new LigneCmdClient();							
			ligneCmdClient.setCip(ligneApp.getCip());
			ligneCmdClient.setCipM(ligneApp.getCipMaison());
			ligneCmdClient.setCommande(commandeClient);
			ligneCmdClient.setPrixUnitaire(ligneApp.getPrixVenteUnitaire());
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

	public  void addline(Long lineId , BigInteger quantite ,BigDecimal remise ,CommandeClient commandeClient){

		BigInteger qte =quantite ;

		LigneApprovisionement ligneApp = LigneApprovisionement.findLigneApprovisionement(lineId);

		LigneCmdClient sameCipM = commandeClient.getSameCipM(ligneApp.getCipMaison());

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
			LigneCmdClient sameCipM = commandeClient.getSameCipM(ligneApp.getCipMaison());
			if(sameCipM != null) sameCipM.increaseCmdQte(qteAfterUpdate, remise);

		}


	}


}


