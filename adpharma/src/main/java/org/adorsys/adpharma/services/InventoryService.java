package org.adorsys.adpharma.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.adorsys.adpharma.domain.AdPharmaBaseEntity;
import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.DestinationMvt;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateUtils;
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
	 * @param produit
	 * @return the true value of stock
	 */
	public static BigInteger stock(Produit produit){
		BigInteger trueStock = BigInteger.ZERO;
		if(produit == null) return trueStock ;
		List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockNotEqualsAndCipEquals(BigInteger.ZERO, produit.getCip()).getResultList();
		if (!resultList.isEmpty()) {
			for (LigneApprovisionement line : resultList) {
				trueStock = trueStock.add(line.getQuantieEnStock());
			}
		}
		return trueStock ;
	}
	
	public void mergeProduct(Produit product,Produit mergeWith){
		if(product==null||mergeWith==null) return;
		if(product.equals(mergeWith)) return;
		List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByProduit(mergeWith).getResultList();
		if(!resultList.isEmpty()){
			List<BigDecimal> prices = LigneApprovisionement.findlastPrices(product);
			BigDecimal pa = null;
			BigDecimal pv = null;
			if(!prices.isEmpty()){
				pa = prices.get(0);
				pv = prices.get(1);
			}
			BigInteger trueStockQuantity = getTrueStockQuantity(mergeWith);
			for (LigneApprovisionement line : resultList) {
				line.setCip(product.getCip());
				line.setDesignation(product.getDesignation());
				line.setProduit(product);
				if(pa!=null)line.setPrixAchatUnitaire(pa);
				if(pv!=null)line.setPrixAchatUnitaire(pv);
				line.merge();
				
			}
			product.addproduct(trueStockQuantity);
			MouvementStock mvt = new MouvementStock();
			mvt.setAgentCreateur(SecurityUtil.getUserName());
			mvt.setCaisse("-//-");
			mvt.setCip(product.getCip());
			mvt.setCipM("-//-");
			mvt.setDesignation(product.getDesignation());
			mvt.setDateCreation(new Date());
			mvt.setDestination(DestinationMvt.MAGASIN);
			mvt.setOrigine(DestinationMvt.MAGASIN);
			mvt.setFiliale("-//-");
			mvt.setNumeroBordereau("-//-");
			mvt.setQteDeplace(trueStockQuantity);
			mvt.setQteInitiale(product.getQuantiteEnStock());
			mvt.setQteFinale(product.getQuantiteEnStock().subtract(trueStockQuantity));
			mvt.setTypeMouvement(TypeMouvement.FUSION_CIP);
			mvt.persist();
		}
		
		mergeWith.setCip(RandomStringUtils.randomAlphabetic(3)+mergeWith.getId());
		mergeWith.setActif(Boolean.FALSE);
		mergeWith.merge();
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
		List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndCipEquals(BigInteger.ZERO, produit.getCip()).setMaxResults(100).getResultList();
		if(!resultList.isEmpty()){
			for (LigneApprovisionement line : resultList) {
				if(quantity.intValue() <= 0) break ;
				if(resultList.indexOf(line) == resultList.size() - 1)
				{
					quantity = line.pushAllInForInventory(quantity);
				} else
				{
					quantity = line.pushProductsInForInventory(quantity);
				}
			}
		}else {
			LigneApprovisionement item = new LigneApprovisionement();
			item.setProduit(produit);
			item.setCip(produit.getCip());
			item.setDatePeremtion(DateUtils.addYears(new Date(), 2));
			item.setDateSaisie(new Date());
			item.setAgentSaisie(SecurityUtil.getUserName());
			item.setQuantiteAprovisione(quantity);
			BigDecimal prixAchatU = produit.getPrixAchatU()!=null?produit.getPrixAchatU():BigDecimal.ZERO;
			BigDecimal prixVenteU = produit.getPrixVenteU()!=null?produit.getPrixVenteU():BigDecimal.ZERO;
			if(prixAchatU==null||prixVenteU ==null){
				List<BigDecimal> findlastPrices = LigneApprovisionement.findlastPrices(produit);
				if(!findlastPrices.isEmpty()){
					prixAchatU = findlastPrices.get(0);
					prixVenteU = findlastPrices.get(1);
				}
				produit.setPrixAchatU(prixAchatU);
				produit.setPrixVenteU(prixVenteU);
				Produit merge = (Produit) produit.merge();
				item.setProduit(merge);

			}
			item.setPrixAchatUnitaire(prixAchatU);
			item.setPrixVenteUnitaire(prixVenteU);
			item.CalculePaTotal();
			item.CalculeQteEnStock();
			try {
				item.setApprovisionement(Approvisionement.getFirstApprovisionement());
			} catch (Exception e) {
				System.out.println("Null Pointer exception");
			}
			item.persist();

			//TODO:
		}

	}

}
