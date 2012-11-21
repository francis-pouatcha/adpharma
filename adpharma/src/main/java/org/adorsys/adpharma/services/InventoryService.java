package org.adorsys.adpharma.services;

import java.math.BigInteger;
import java.util.List;

import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Gkc
 *
 */
@Service
public class InventoryService {
 private static final Logger  LOG =LoggerFactory.getLogger(InventoryService.class);
	/**
	 * use to set all negative sock of produit to zero
	 * @param produit
	 */
	public void setNegativeStockToZero(Produit produit){
		if(produit == null) return ;
		List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockLessThanAndCipEquals(BigInteger.ZERO, produit.getCip()).getResultList();
		if (!resultList.isEmpty()) {
			for (LigneApprovisionement line : resultList) {
				line.setQuantiteVendu(line.getQuantiteAprovisione());
				line.setQuantiteSortie(BigInteger.ZERO);
				line.CalculeQteEnStock();
				line.merge();
			}
		}
	}

	/**
	 * @param produit
	 * @return the true value of stock
	 */
	public BigInteger getTrueStockQuantity(Produit produit){
		BigInteger trueStock = BigInteger.ZERO;
		if(produit == null) return trueStock ;
		List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndCipEquals(BigInteger.ONE, produit.getCip()).getResultList();
		if (!resultList.isEmpty()) {
			for (LigneApprovisionement line : resultList) {
				trueStock = trueStock.add(line.getQuantieEnStock());
			}
		}
		return trueStock ;
	}


	/**
	 * use to resotre stock quantity to down 
	 * @param produit
	 * @param quantity
	 */
	public void updateStockToDown(Produit produit ,BigInteger quantity){
		if(produit == null || quantity == null ) throw  new IllegalArgumentException(" produit or quantity arguments is required ")	;
		if(quantity.intValue() <= 0) return ;
		List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndCipEquals(BigInteger.ONE, produit.getCip()).getResultList();
		LOG.debug(resultList.toString());
		if(!resultList.isEmpty()){
			for (LigneApprovisionement line : resultList) {
				if(quantity.intValue() <= 0) break ;
				quantity = line.pushProductsOutForInventory(quantity);

			}
		}

	}

	/**
	 * use to restore stock quantity to up 
	 * @param produit
	 * @param quantity
	 */
	public void updateStockToUp(Produit produit, BigInteger quantity){
		if(produit == null || quantity == null ) throw  new IllegalArgumentException(" produit or quantity arguments is required ")	;
		if(quantity.intValue() <= 0) return ;
		List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockLessThanAndCipEquals(BigInteger.ONE, produit.getCip()).setMaxResults(100).getResultList();
		if(!resultList.isEmpty()){
			for (LigneApprovisionement line : resultList) {
				if(quantity.intValue() <= 0) break ;
				quantity = line.pushProductsInForInventory(quantity);
			}
		}

	}
	
	
	public static void generateStockMouvement(LigneApprovisionement line ,BigInteger qte,TypeMouvement type){
		
	}


}
