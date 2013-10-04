package org.adorsys.adpharma.beans;

import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;

import org.adorsys.adpharma.domain.Produit;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe de gestion des evenements sur les produits
 * @author hsimo
 *
 */
public class ProductMonitor {
	
	private static Logger LOGS= Logger.getLogger(ProductMonitor.class);
	
	@PostLoad
	@Transactional
	public void notifyProduct(Produit produit){
		if(produit.isAlert() && produit.getActif()==true){
			LOGS.info("Produit en alerte de stock");
			produit.setCommander(Boolean.TRUE);
		}else if(!produit.isAlert() && produit.getActif() == true){
			LOGS.info("Produit N'est pas en alerte de stock");
			produit.setCommander(Boolean.FALSE);
		}
		if(Produit.alreadyInStock(produit)){
			LOGS.info("Produit deja en stock");
			produit.setInStock(Boolean.TRUE);
		}
		produit.merge().flush();
	}
}
