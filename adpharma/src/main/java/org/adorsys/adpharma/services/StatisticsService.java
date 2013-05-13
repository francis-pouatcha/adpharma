package org.adorsys.adpharma.services;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Customers;
import org.adorsys.adpharma.domain.LigneCmdClient;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.services.homestatisticsclasses.TopSelling;

public interface StatisticsService {
	
	
	// Return les produits les plus vendus aucours du mois courant 
	public List<TopSelling> topSellingProducts(Date debut, Date fin);
	
	// Les dix dernieres ventes du jour
	public List<CommandeClient> lastTenSelling(Date debut, Date fin);
	
	// Etat des caisses ouvertes du jour
	public List<Caisse> stateOfOpenCash(Date debut, Date fin);
	
	// Etat des produits en rupture de stock au cours du mois
	public List<ProductsOut> productOutOfStock(BigInteger qte, Date debut, Date fin);
	
	// Etat des produits avaries au cours du mois
	public List<ProductsOut> productsAvaries();
	
	// Etat des avoirs clients
	public List<Customers> avoirsClients(Date debut, Date fin);
	
	// Etat des dettes clients
	public List<Customers> dettesClients(Date debut, Date fin);
	
	
	

}
