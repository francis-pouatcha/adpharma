package org.adorsys.adpharma.domain;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.MvcNamespaceHandler;
import org.adorsys.adpharma.domain.Produit;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.apache.commons.collections.set.CompositeSet.SetMutator;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * @author adorsys-clovis
 *
 */

@RooJavaBean
@RooToString
@RooEntity(finders = { "findLigneInventairesByInventaire" })
public class LigneInventaire {

	private BigInteger qteEnStock;

	private BigInteger qteReel;

	private BigInteger ecart;

	private BigDecimal prixUnitaire;

	private BigDecimal prixTotal;

	private String agentSaisie ;

	private String cipm ;

	@ManyToOne
	private Inventaire inventaire;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateSaisie;


	@ManyToOne
	private Produit produit;


	public LigneInventaire() {
		// TODO Auto-generated constructor stub
	}
	
	public LigneInventaire(Produit produit,Inventaire inventaire,BigInteger qteReel) {
		this.inventaire=inventaire;
		setProduit(produit);
		setQteEnStock(produit.getQuantiteEnStock());
		setQteReel(qteReel);
		calculerEcart();
		caculMontantEcart();
	}
	public LigneInventaire(LigneApprovisionement ligne,Inventaire inventaire,BigInteger qteReel) {
		this.inventaire=inventaire;
		setProduit(ligne.getProduit());
		setQteEnStock(ligne.getQuantieEnStock());
		setPrixUnitaire(ligne.getPrixVenteUnitaire());
		setQteReel(qteReel);
		setCipm(ligne.getCipMaison());
		calculerEcart();
		caculMontantEcart();
	}
	public void calculerEcart(){
		ecart = qteReel.subtract(qteEnStock);
	}

	@PrePersist
	public void prePersit(){
		agentSaisie = SecurityUtil.getPharmaUser().getDisplayName();
		dateSaisie = new Date();
		calculerEcart() ;
	}

	@PreUpdate
	public void preUpdate(){
		calculerEcart() ;
	}

	/**
	 * calculate price of product defficite ecart
	 */
	public void caculMontantEcart(){
		if(prixUnitaire!=null){
			prixTotal = prixUnitaire.multiply(BigDecimal.valueOf(ecart.longValue()));
		}else {
			List<BigDecimal> lastPrices = LigneApprovisionement.findlastPrices(produit);
			prixUnitaire = BigDecimal.ZERO;
			prixTotal = BigDecimal.ZERO;
			if(!lastPrices.isEmpty()){
				prixUnitaire = lastPrices.get(0);
				prixTotal = prixUnitaire.multiply(BigDecimal.valueOf(ecart.longValue()));

			}


		}




	}
	public String getCipm() {
		return cipm;
	}

	public void setCipm(String cipm) {
		this.cipm = cipm;
	}

	public String toJson() {
		return new JSONSerializer().exclude("*.class").serialize(this);
	}

	public static String toJsonArray(Collection<LigneInventaire> collection) {
		return new JSONSerializer().exclude("*.class").serialize(collection);
	}

	@Transactional
	public void restoreEcart(){
		BigInteger restoreQte = BigInteger.ZERO ;
		if (ecart.intValue()< 0) {
			restoreQte = ecart.abs();
			List<LigneApprovisionement> ligneApprovisionements = LigneApprovisionement.findLigneApprovisionementsByQteStockAndProduit(getProduit(), BigInteger.ONE).getResultList();
			if (!ligneApprovisionements.isEmpty()) {
				for (LigneApprovisionement ligne : ligneApprovisionements) {
					BigInteger decreaseQte = ligne.getDecreaseQte();
					if (decreaseQte.intValue() >= restoreQte.intValue() ) {
						ligne.setQuantiteSortie(ligne.getQuantiteSortie().add(restoreQte));
						ligne.CalculeQteEnStock();
						ligne.CalculeMontantStock();
						ligne.merge();
						genereMvt(ligne, restoreQte, true);
						break;

					}else {
						ligne.setQuantiteSortie(ligne.getQuantiteSortie().add(decreaseQte));
						ligne.CalculeQteEnStock();
						ligne.CalculeMontantStock();
						ligne.merge();
						restoreQte = restoreQte.subtract(decreaseQte);
						genereMvt(ligne, decreaseQte, true);


					}

				}
				produit.setQuantiteEnStock(qteReel);
				produit.setDateDerniereSortie(new  Date());
				produit.merge();
			}


		}

		if (ecart.intValue() > 0) {
			restoreQte = ecart.abs();
			List<LigneApprovisionement> ligneApprovisionements = LigneApprovisionement.findLigneApprovisionementsByQteStockOrQuantiteSortieAndProduit(getProduit(), BigInteger.ONE, BigInteger.ONE).setMaxResults(restoreQte.intValue()+100).getResultList();
			if (!ligneApprovisionements.isEmpty()) {
				for (LigneApprovisionement ligne : ligneApprovisionements) {
					BigInteger increaseQte = ligne.getIncreaseQte();
					if (increaseQte.intValue() >= restoreQte.intValue() ) {
						BigInteger qte = BigInteger.valueOf(restoreQte.longValue());

						if (ligne.getQuantiteSortie().intValue() > 0) {
							restoreQte = restoreQte.subtract(ligne.getQuantiteSortie());
							ligne.setQuantiteSortie(BigInteger.ZERO);
						}	
						ligne.setQuantiteVendu(ligne.getQuantiteVendu().subtract(restoreQte));
						ligne.CalculeQteEnStock();
						ligne.CalculeMontantStock();
						ligne.merge();
						genereMvt(ligne, qte, false);

						break;

					}else {
						BigInteger qte = BigInteger.valueOf(increaseQte.longValue());

						if (ligne.getQuantiteSortie().intValue() > 0) {
							increaseQte = increaseQte.subtract(ligne.getQuantiteSortie());
							ligne.setQuantiteSortie(BigInteger.ZERO);
						}
						ligne.setQuantiteVendu(ligne.getQuantiteVendu().subtract(increaseQte));
						ligne.CalculeQteEnStock();
						ligne.CalculeMontantStock();
						ligne.merge();
						genereMvt(ligne, increaseQte, false);
						restoreQte = restoreQte.subtract(increaseQte);
					}
				}
				produit.setQuantiteEnStock(qteReel);
				produit.setDateDerniereSortie(new  Date());
				produit.merge();

			}

		}


	}

	public void restoreEcartIn(BigInteger qte){

	}

	public void restoreEcartOut(BigInteger qte){

	}

	public void genereMvt(LigneApprovisionement ligne ,BigInteger quantite , boolean sortie){
		if (quantite.intValue() != 0) {
			MouvementStock mouvementStock = new MouvementStock();
			mouvementStock.setAgentCreateur(SecurityUtil.getUserName());
			mouvementStock.setCip(ligne.getProduit().getCip());
			mouvementStock.setCipM(ligne.getCipMaison());
			mouvementStock.setDesignation(ligne.getDesignation());
			mouvementStock.setDestination(DestinationMvt.MAGASIN);
			mouvementStock.setOrigine(DestinationMvt.MAGASIN);
			mouvementStock.setQteDeplace(quantite);
			mouvementStock.setQteFinale(ligne.getQuantieEnStock());
			if (sortie) {
				mouvementStock.setTypeMouvement(TypeMouvement.SORTIE_INVENTAIRE);
				mouvementStock.setQteInitiale(mouvementStock.getQteFinale().add(quantite));

			}else {
				mouvementStock.setTypeMouvement(TypeMouvement.RETOUR_INVENTAIRE);
				mouvementStock.setQteInitiale(mouvementStock.getQteFinale().subtract(quantite));
			}
			mouvementStock.persist();


		}
	}

	public static TypedQuery<LigneInventaire> findLigneInventairesByInventaire(Inventaire inventaire) {
		if (inventaire == null) throw new IllegalArgumentException("The inventaire argument is required");
		EntityManager em = LigneInventaire.entityManager();

		TypedQuery<LigneInventaire> q = em.createQuery("SELECT o FROM LigneInventaire AS o WHERE o.inventaire = :inventaire ORDER BY o.produit.designation ", LigneInventaire.class);
		q.setParameter("inventaire", inventaire);
		return q;
	}

}