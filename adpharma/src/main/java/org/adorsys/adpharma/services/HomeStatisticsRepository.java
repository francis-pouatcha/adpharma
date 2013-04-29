package org.adorsys.adpharma.services;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.adorsys.adpharma.domain.AvoirClient;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.DetteClient;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneCmdClient;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.utils.PharmaDateUtil;

public class HomeStatisticsRepository {
	
	
	public static List<Object[]> topSellingProductsRepository(Date debut, Date fin) {
		if(debut==null) throw new IllegalArgumentException("The begin date argument is required");
		if(fin==null) throw new IllegalArgumentException("The end date argument is required");
		StringBuilder searchQuery= new StringBuilder();
		searchQuery.append("SELECT distinct lc.produit.produit.id, lc.designation, lc.cip, lc.prixUnitaire, SUM(quantiteCommande-quantiteRetourne) AS qte_vendue, SUM(prixTotal), SUM(totalRemise) FROM LigneCmdClient AS lc");
		searchQuery.append(" WHERE lc.commande.encaisse = :encaisse AND dateSaisie BETWEEN :debut AND :fin");
		searchQuery.append(" GROUP BY lc.designation");
		searchQuery.append(" ORDER BY qte_vendue DESC");
		EntityManager em = LigneCmdClient.entityManager();
		Query query = em.createQuery(searchQuery.toString());
		query.setParameter("encaisse", Boolean.TRUE);
		query.setParameter("debut", debut);
		query.setParameter("fin", fin);
		query.setMaxResults(20);
		return query.getResultList();
     }
	
	public static List<CommandeClient> lastTenSellingRepository(Date debut, Date fin){
		if(debut==null) throw new IllegalArgumentException("The begin date argument is required");
		if(fin==null) throw new IllegalArgumentException("The end date argument is required");
		StringBuilder searchQuery= new StringBuilder();
		searchQuery.append("SELECT c FROM CommandeClient AS c join fetch c.vendeur join fetch c.client");
		searchQuery.append(" WHERE c.dateCreation BETWEEN :debut AND :fin");
		searchQuery.append(" ORDER BY c.dateCreation DESC");
		EntityManager em = CommandeClient.entityManager();
		TypedQuery<CommandeClient> query = em.createQuery(searchQuery.toString(), CommandeClient.class);
		query.setParameter("debut", debut);
		query.setParameter("fin", fin);
		query.setMaxResults(10);
		return query.getResultList();
	}
	
	public static List<Caisse> stateCashsRepository(Date debut, Date fin){
		if(debut==null) throw new IllegalArgumentException("The begin date argument is required");
		if(fin==null) throw new IllegalArgumentException("The end date argument is required");
		StringBuilder searchQuery= new StringBuilder();
		searchQuery.append("SELECT c FROM Caisse AS c join fetch c.caissier");
		searchQuery.append(" WHERE c.caisseOuverte = :ouvert AND c.dateOuverture BETWEEN :debut AND :fin");
		searchQuery.append(" ORDER BY c.dateOuverture DESC");
		EntityManager em = Caisse.entityManager();
		TypedQuery<Caisse> query = em.createQuery(searchQuery.toString(), Caisse.class);
		query.setParameter("ouvert", Boolean.TRUE);
		query.setParameter("debut", debut);
		query.setParameter("fin", fin);
		return query.getResultList();
	}
	
	public static List<Object[]> productsOutOfStockRepository(BigInteger qte, Date debut, Date fin){
		StringBuilder searchQuery= new StringBuilder();
		searchQuery.append("SELECT o.id, o.cip, o.designation, o.quantiteEnStock, o.prixAchatU, o.prixVenteU, o.dateDerniereRupture FROM Produit AS o");
		searchQuery.append(" WHERE o.quantiteEnStock <= :quantiteEnStock AND o.actif =:actif");
		searchQuery.append(" AND dateDerniereRupture BETWEEN :debut AND :fin");
		searchQuery.append(" ORDER BY o.dateDerniereRupture DESC");
		EntityManager em = Produit.entityManager();
		Query query = em.createQuery(searchQuery.toString());
		query.setParameter("quantiteEnStock", qte);
		query.setParameter("actif", Boolean.TRUE);
		query.setParameter("debut", debut);
		query.setParameter("fin", fin);
		query.setMaxResults(10);
		return query.getResultList();
	}
	
	public static List<Object[]> productsAvariesRepository(){
		StringBuilder searchQuery= new StringBuilder();
		searchQuery.append("SELECT distinct o.produit.id, o.cip, o.designation, o.datePeremtion, o.quantieEnStock FROM LigneApprovisionement AS o");
		searchQuery.append(" WHERE o.datePeremtion <= :today"); 
		searchQuery.append(" AND  o.produit.actif = :actif");
		searchQuery.append(" AND  o.cip = o.produit.cip");
		searchQuery.append(" AND  o.quantieEnStock > 0");
		searchQuery.append(" ORDER BY o.datePeremtion ASC");
		EntityManager em = LigneApprovisionement.entityManager();
		Query query = em.createQuery(searchQuery.toString());
		query.setParameter("actif", Boolean.TRUE);
		query.setParameter("today", PharmaDateUtil.parse(new Date().toString(), PharmaDateUtil.DATE_PATTERN_LONG));
		query.setMaxResults(10);
		return query.getResultList();
	}
	
	public static List<Object[]> avoirsClientsRepository(Date debut, Date fin){
		StringBuilder searchQuery= new StringBuilder();
		searchQuery.append("SELECT o.id, o.numero, o.clientName, o.montant, o.montantUtilise, o.reste, o.dateEdition FROM AvoirClient AS o");
		searchQuery.append(" WHERE o.solder = :solder AND o.dateEdition BETWEEN :debut AND :fin");
		searchQuery.append(" ORDER BY o.dateEdition DESC");
		EntityManager em = AvoirClient.entityManager();
		Query query = em.createQuery(searchQuery.toString());
		query.setParameter("solder", Boolean.FALSE);
		query.setParameter("debut", debut);
		query.setParameter("fin", fin);
		query.setMaxResults(10);
		return query.getResultList();
	}
	
	public static List<Object[]> dettesClientsRepository(Date debut, Date fin){
		StringBuilder searchQuery= new StringBuilder();
	    searchQuery.append("SELECT o.id, o.clientName, o.montantInitial, o.avance, o.reste, o.dateCreation FROM DetteClient AS o");
	    searchQuery.append(" WHERE o.solder = :solder AND o.dateCreation BETWEEN :debut AND :fin");
	    searchQuery.append(" ORDER BY o.dateCreation DESC");
	    EntityManager em = DetteClient.entityManager();
		Query query = em.createQuery(searchQuery.toString());
		query.setParameter("solder", Boolean.FALSE);
		query.setParameter("debut", debut);
		query.setParameter("fin", fin);
		query.setMaxResults(10);
		return query.getResultList();
	}
	
}	
