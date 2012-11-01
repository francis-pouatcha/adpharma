package org.adorsys.adpharma.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.adorsys.adpharma.domain.PharmaUser;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.adorsys.adpharma.domain.Etat;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.adorsys.adpharma.domain.Site;
import java.util.Set;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.services.InventoryService;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Inventaire", finders = { "findInventairesByDateInventaireBetween", "findInventairesByEtat", "findInventairesByAgentAndDateInventaireBetween" })
public class Inventaire extends AdPharmaBaseEntity {


	private String numeroInventaire;

	@ManyToOne
	private PharmaUser agent;

	private BigDecimal montant;

	@Enumerated
	private Etat etat;

	@Size(max = 256)
	private String note;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateInventaire;

	@ManyToOne
	private Site site;

	/*
	 * champ utili  pour l'edition des fiches d'inventaires
	 */
	private transient FamilleProduit familleProduit  ;

	public FamilleProduit getFamilleProduit() {
		return familleProduit;
	}

	public void setFamilleProduit(FamilleProduit familleProduit) {
		this.familleProduit = familleProduit;
	}

	public SousFamilleProduit getSousFamilleProduit() {
		return sousFamilleProduit;
	}

	public void setSousFamilleProduit(SousFamilleProduit sousFamilleProduit) {
		this.sousFamilleProduit = sousFamilleProduit;
	}

	private transient SousFamilleProduit sousFamilleProduit ;
	private transient Rayon rayon ;

	private transient Filiale filiale ;

	private transient String beginBy ;

	private transient String endBy;

	private transient String cipProduct;

	public String getCipProduct() {
		return cipProduct;
	}

	public void setCipProduct(String cipProduct) {
		this.cipProduct = cipProduct;
	}

	private transient String designation ;
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private transient Date dateDebut;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private transient Date dateFin;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private transient Date dateRupture;


	public Date getDateRupture() {
		return dateRupture;
	}

	public void setDateRupture(Date dateRupture) {
		this.dateRupture = dateRupture;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public Rayon getRayon() {
		return rayon;
	}

	public void setRayon(Rayon rayon) {
		this.rayon = rayon;
	}

	public Filiale getFiliale() {
		return filiale;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public void setFiliale(Filiale filiale) {
		this.filiale = filiale;
	}

	public String getBeginBy() {
		return beginBy;
	}

	public void setBeginBy(String beginBy) {
		this.beginBy = beginBy;
	}

	public String getEndBy() {
		return endBy;
	}

	public void setEndBy(String endBy) {
		this.endBy = endBy;
	}

	public Boolean getCip() {
		return cip;
	}

	public void setCip(Boolean cip) {
		this.cip = cip;
	}

	public boolean isCipm() {
		return cipm;
	}

	public void setCipm(boolean cipm) {
		this.cipm = cipm;
	}

	public Boolean getpAchat() {
		return pAchat;
	}

	public void setpAchat(Boolean pAchat) {
		this.pAchat = pAchat;
	}

	public Boolean getpVente() {
		return pVente;
	}

	public void setpVente(Boolean pVente) {
		this.pVente = pVente;
	}

	public List<LigneApprovisionement> getLigneApprovisionements() {
		return ligneApprovisionements;
	}

	public void setLigneApprovisionements(
			List<LigneApprovisionement> ligneApprovisionements) {
		this.ligneApprovisionements = ligneApprovisionements;
		this.produits = new ArrayList<Produit>();
	}

	public List<Produit> getProduits() {
		return produits;
	}

	public void setProduits(List<Produit> produits) {
		this.produits = produits;
		this.ligneApprovisionements = new ArrayList<LigneApprovisionement>();
	}

	private transient Boolean cip = true ;

	private transient boolean cipm  = false;

	private transient Boolean pAchat  = false;

	private transient Boolean pVente  = false;

	private transient List<LigneApprovisionement> ligneApprovisionements = new ArrayList<LigneApprovisionement>() ;	
	private transient List<Produit> produits = new ArrayList<Produit>() ;



	@PrePersist
	public void prePersit(){
		agent = SecurityUtil.getPharmaUser();
		dateInventaire = new Date();
		site = agent.getOffice();
		etat = Etat.EN_COUR;
	}

	public void protectSomeField() {
		Inventaire inventaire = Inventaire.findInventaire(getId());
		numeroInventaire = inventaire.getNumeroInventaire();
		agent = inventaire.getAgent();
		etat = inventaire.getEtat();
		dateInventaire = inventaire.getDateInventaire();
		site = inventaire.getSite();
		footPrint = inventaire.getFootPrint();
		footPrint.setModifyingUser(SecurityUtil.getUserName());

	}

	@PostPersist
	public void postPersit(){
		numeroInventaire = NumberGenerator.getNumber("INV-", getId(), 4);


	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "inventaire")
	private Set<LigneInventaire> ligneInventaire = new HashSet<LigneInventaire>();

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Inventaire No: ").append(getNumeroInventaire()).append(" Du :");
		sb.append(PharmaDateUtil.format(getDateInventaire(), "dd-MM-yyyy hh:mm")).append(" , ");
		sb.append("Agent: ").append(getAgent().displayName()).append(" , ");
		sb.append("Site: ").append(getSite());
		return sb.toString();
	}

	public boolean contientProduit(Produit produit) {
		if (ligneInventaire.isEmpty()) {
			return false;
		} else {
			for (LigneInventaire ligne : ligneInventaire) {
				if (ligne.getProduit().equals(produit)) {
					return true;
				}
			}
		}
		return false;
	}

	public void restoreStock(){
		InventoryService inventoryService = new InventoryService();
		Set<LigneInventaire> ligneInventaires =	getLigneInventaire();
		for (LigneInventaire ligneInventaire : ligneInventaires) {
			Produit produit = ligneInventaire.getProduit();
			inventoryService.setNegativeStockToZero(produit);
			BigInteger trueStock = inventoryService.getTrueStockQuantity(produit);
			BigInteger qteReel = ligneInventaire.getQteReel();
			if (qteReel.intValue() > trueStock.intValue()) {
				BigInteger ecart = qteReel.subtract(trueStock);
				inventoryService.updateStockToUp(produit, ecart);
			}
			if (qteReel.intValue() < trueStock.intValue()) {
				BigInteger ecart = trueStock.subtract(qteReel);
				inventoryService.updateStockToDown(produit, ecart);
			}
			produit.setQuantiteEnStock(qteReel);
			produit.merge();


		}


	}

	public boolean isFindall(){
		return  rayon==null &&filiale==null;
	}

	public static List<Inventaire> findInventaireEntries(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM Inventaire o ORDER BY o.id DESC ", Inventaire.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}  

	public static List<Inventaire> findAllInventaires() {
		return entityManager().createQuery("SELECT o FROM Inventaire o ORDER BY o.id DESC", Inventaire.class).getResultList();
	}

	public static List<Produit> searchFicheInventaire(String cip,String cipm, String designation,String beginBy,String endBy, Rayon rayon, Filiale filiale) {
		StringBuilder searchQuery = new StringBuilder("SELECT o FROM Produit AS o WHERE o.produitNumber IS NOT NULL ");
		if (StringUtils.isNotBlank(cip)) {
			return entityManager().createQuery("SELECT o FROM Produit AS o WHERE  o.cip = :cip ", Produit.class).setParameter("cip", cip).getResultList();
		} else {
			if (StringUtils.isNotBlank(designation)) {
				designation = designation + "%";
				searchQuery.append(" AND  LOWER(o.designation) LIKE LOWER(:designation) ");
			}
			if (rayon != null) {
				searchQuery.append(" AND o.rayon = :rayon ");
			}
			if (filiale != null) {
				searchQuery.append(" AND o.filiale = :filiale ");
			}
			TypedQuery<Produit> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.designation ASC").toString(), Produit.class);
			if (StringUtils.isNotBlank(designation)) {
				q.setParameter("designation", designation);
			}
			if (rayon != null) {
				q.setParameter("rayon", rayon);
			}
			if (filiale != null) {
				q.setParameter("filiale", filiale);
			}
			return q.getResultList(); 
		}

	}

}
