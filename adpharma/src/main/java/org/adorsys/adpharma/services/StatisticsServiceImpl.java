package org.adorsys.adpharma.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Customers;
import org.adorsys.adpharma.domain.LigneCmdClient;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.services.homestatisticsclasses.TopSelling;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.springframework.stereotype.Service;

@Service("statisticsServiceImpl")
public class StatisticsServiceImpl implements StatisticsService{
	

	@Override
	public List<TopSelling> topSellingProducts(Date debut, Date fin) {
		List<Object[]> topSellingProductsRepository = HomeStatisticsRepository.topSellingProductsRepository(debut, fin);
		
		List<TopSelling> topSellings= new ArrayList<TopSelling>();
		for(Object[] obj: topSellingProductsRepository){
			TopSelling topSelling = new TopSelling();
			topSelling.setId((Long)obj[0]);
			topSelling.setDesignation((String)obj[1]);
			topSelling.setCip((String)obj[2]);
			topSelling.setPrixUnitaire((BigDecimal)obj[3]);
			topSelling.setQteVendue((BigInteger)obj[4]);
			topSelling.setPrixTotal((BigDecimal)obj[5]);
			topSelling.setTotalRemise((BigDecimal)obj[6]);
			topSellings.add(topSelling);
		}
		
		return topSellings;
	}

	@Override
	public List<CommandeClient> lastTenSelling(Date debut, Date fin) {
		  List<CommandeClient> liste = HomeStatisticsRepository.lastTenSellingRepository(debut, fin);
		  return liste;
	}

	@Override
	public List<Caisse> stateOfOpenCash(Date debut, Date fin) {
		List<Caisse> list = HomeStatisticsRepository.stateCashsRepository(debut, fin); 
		return list;
	}

	@Override
	public List<ProductsOut> productOutOfStock(BigInteger qte, Date debut, Date fin) {
	       List<Object[]> list = HomeStatisticsRepository.productsOutOfStockRepository(qte, debut, fin);
	       List<ProductsOut> produits= new ArrayList<ProductsOut>();
	       for(Object[] obj: list){
	    	   ProductsOut product = new ProductsOut();
	    	   product.setId((Long)obj[0]);
	    	   product.setCip((String)obj[1]);
	    	   product.setDesignation((String)obj[2]);
	    	   product.setQteStock((BigInteger)obj[3]);
	    	   product.setPaUnit((BigDecimal)obj[4]);
	    	   product.setPvUnit((BigDecimal)obj[5]);
	    	   product.setDateRupture((Date)obj[6]);
	    	   produits.add(product);
	       }
	       
		   return produits;
	}

	@Override
	public List<ProductsOut> productsAvaries() {
		   List<Object[]> list = HomeStatisticsRepository.productsAvariesRepository();
		   List<ProductsOut> avaries= new ArrayList<ProductsOut>();
		   for(Object[] obj: list){
			   ProductsOut product= new ProductsOut();
			   product.setId((Long)obj[0]);
			   product.setCip((String)obj[1]);
			   product.setDesignation((String)obj[2]);
			   product.setDatePeremption((Date)obj[3]);
			   product.setQteStock((BigInteger)obj[4]);
			   avaries.add(product);
		   }
		
		return avaries;
	}

	@Override
	public List<Customers> avoirsClients(Date debut, Date fin) {
		         List<Object[]> list = HomeStatisticsRepository.avoirsClientsRepository(debut, fin);
		         List<Customers> avoirs= new ArrayList<Customers>();
		         for(Object[] obj: list){
		        	 Customers custom= new Customers();
		        	 custom.setId((Long)obj[0]);
		        	 custom.setNumero((String)obj[1]);
		        	 custom.setNomClient((String)obj[2]); 
		        	 custom.setMontant((BigDecimal)obj[3]);
		        	 custom.setAvance((BigDecimal)obj[4]);
		        	 custom.setReste((BigDecimal)obj[5]);
		        	 custom.setDateCreation((Date)obj[6]);
		        	 avoirs.add(custom);
		         }
		      return avoirs;
	        }

	@Override
	public List<Customers> dettesClients(Date debut, Date fin) {
		List<Object[]> list = HomeStatisticsRepository.dettesClientsRepository(debut, fin);
		List<Customers> dettes= new ArrayList<Customers>();
		for(Object[]obj: list){
			Customers custom= new Customers();
			custom.setId((Long)obj[0]);
			custom.setNomClient((String)obj[1]);
//<<<<<<< HEAD
			try {
				custom.setMontant((BigDecimal)obj[2]);
			} catch (Exception e) {
				custom.setMontant((BigDecimal)obj[2]);
			}
			custom.setMontant((BigDecimal)obj[2]);
			custom.setAvance((BigDecimal)obj[3]);
			custom.setReste((BigDecimal)obj[4]);
/*=======
//			custom.setMontant((BigDecimal)obj[2]);
			custom.setMontant(new BigDecimal((obj[2]).toString()));
			custom.setAvance(new BigDecimal((obj[3]).toString()));
			custom.setReste(new BigDecimal((obj[4]).toString()));
>>>>>>> 6b306bf01d7ce37acceca951f0907bc1cd6d1e4b*/
			custom.setDateCreation((Date)obj[5]);
			dettes.add(custom);
		}
		return dettes;
	}
	
	
	
	

}
