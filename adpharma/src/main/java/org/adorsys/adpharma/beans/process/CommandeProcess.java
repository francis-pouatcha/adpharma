package org.adorsys.adpharma.beans.process;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.ModeSelection;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.CipMgenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;
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

	public static List<Object[]> findProduitAndQuantiteVendue(String beginDes,String endDes,Date debut ,Date fin,Rayon rayon , Filiale filiale,BigInteger qte) {
		if (debut == null) throw new IllegalArgumentException("The debut arguments are required");
		fin = fin ==null?new Date():fin;
		EntityManager em = MouvementStock.entityManager();
		StringBuilder searchQuery = new StringBuilder("SELECT p , SUM(o.qteDeplace) as qte ,(select MAX(l.prixAchatUnitaire ) FROM  LigneApprovisionement as l WHERE l.cip = p.cip ) as pa , (select MAX(l.prixVenteUnitaire ) FROM  LigneApprovisionement as l WHERE l.cip = p.cip )as pv  FROM MouvementStock AS o , Produit AS p WHERE o.cip = p.cip AND o.dateCreation BETWEEN :debut AND :fin AND o.typeMouvement = :typeMouvement ");

		if (qte ==null) {
			qte = BigInteger.ZERO ;
		}

		if (StringUtils.isNotBlank(beginDes)) {
			beginDes = beginDes + "%";
			searchQuery.append("  AND  LOWER(o.designation) >= LOWER(:beginDes)  ");
		}
		if (StringUtils.isNotBlank(endDes)) {
			endDes = endDes + "%";
			searchQuery.append("  AND  LOWER(o.designation) <= LOWER(:endDes)  ");
		}

		if (rayon != null) {
			searchQuery.append("  AND p.rayon = :rayon  ");
		}
		if (filiale != null) {
			searchQuery.append("  AND p.filiale = :filiale  ");
		}
		Query q= em.createQuery(searchQuery.append("  GROUP BY p HAVING SUM(o.qteDeplace) >= :qte ").toString());
		q.setParameter("qte", qte);
		if (StringUtils.isNotBlank(beginDes)) q.setParameter("beginDes", beginDes);
		if (StringUtils.isNotBlank(endDes)) q.setParameter("endDes", endDes);
		if (rayon!=null) q.setParameter("rayon", rayon);
		if (filiale!=null) q.setParameter("filiale", filiale);
		q.setParameter("debut", debut);
		q.setParameter("fin", fin);
		q.setParameter("typeMouvement", TypeMouvement.VENTE);
		return q.getResultList();
	}

	public static List<Object[]> findProduitAndRuptureOrAlert(String beginDes,String endDes,Rayon rayon , Filiale filiale,ModeSelection selection) {
		EntityManager em = MouvementStock.entityManager();
		StringBuilder  searchQuery = null ;
		if(selection.equals(ModeSelection.MANUELLE)){
			return null ;
		}
		if(selection.equals(ModeSelection.RUPTURE_STOCK)){
			searchQuery = new StringBuilder("SELECT p , SUM(p.seuilComande) as qte ,(select MAX(l.prixAchatUnitaire ) FROM  LigneApprovisionement as l WHERE l.cip = p.cip ) as pa , (select MAX(l.prixVenteUnitaire ) FROM  LigneApprovisionement as l WHERE l.cip = p.cip )as pv  FROM Produit AS p WHERE p.quantiteEnStock <= :qte  ");
		}
		if(selection.equals(ModeSelection.ALERTE_STOCK)){
			searchQuery = new StringBuilder("SELECT p , SUM(p.seuilComande) as qte ,(select MAX(l.prixAchatUnitaire ) FROM  LigneApprovisionement as l WHERE l.cip = p.cip ) as pa , (select MAX(l.prixVenteUnitaire ) FROM  LigneApprovisionement as l WHERE l.cip = p.cip )as pv  FROM Produit AS p WHERE p.quantiteEnStock <= p.seuilComande  ");
		}


		if (StringUtils.isNotBlank(beginDes)) {
			beginDes = beginDes + "%";
			searchQuery.append("  AND  LOWER(p.designation) >= LOWER(:beginDes)  ");
		}
		if (StringUtils.isNotBlank(endDes)) {
			endDes = endDes + "%";
			searchQuery.append("  AND  LOWER(p.designation) <= LOWER(:endDes)  ");
		}

		if (rayon != null) {
			searchQuery.append("  AND p.rayon = :rayon  ");
		}
		if (filiale != null) {
			searchQuery.append("  AND p.filiale = :filiale  ");
		}
		Query q= em.createQuery(searchQuery.toString());
		if(selection.equals(ModeSelection.RUPTURE_STOCK)) q.setParameter("qte", BigInteger.ZERO);
		if (StringUtils.isNotBlank(beginDes)) q.setParameter("beginDes", beginDes);
		if (StringUtils.isNotBlank(endDes)) q.setParameter("endDes", endDes);
		if (rayon!=null) q.setParameter("rayon", rayon);
		if (filiale!=null) q.setParameter("filiale", filiale);
		return q.getResultList();
	}


}
