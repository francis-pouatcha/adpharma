package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Size;

import jxl.Cell;

import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.services.DefaultInventoryService;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.web.multipart.MultipartFile;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Inventaire", finders = { "findInventairesByDateInventaireBetween", "findInventairesByEtat", "findInventairesByAgentAndDateInventaireBetween" })
public class Inventaire extends AdPharmaBaseEntity {

	private String numeroInventaire;
	private Long aproId;
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

	private Long rayonId;

	transient MultipartFile fichier ;

	/*
	 * champ utili  pour l'edition des fiches d'inventaires
	 */
	private transient FamilleProduit familleProduit  ;

	public Approvisionement associateAppro(){
		return Approvisionement.findApprovisionement(aproId);
	}

	public MultipartFile getFichier() {
		return fichier;
	}

	public void setFichier(MultipartFile fichier) {
		this.fichier = fichier;
	}

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

	private transient String cipmProduct;



	public String getCipmProduct() {
		return cipmProduct;
	}

	public void setCipmProduct(String cipmProduct) {
		this.cipmProduct = cipmProduct;
	}

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

	private  Boolean inventoryBycip = true ;

	private  Boolean inventoryBycipm ;

	private transient boolean doNotSelectAnyProduct  = true;

	private transient Boolean pAchat  = false;

	private transient Boolean pVente  = false;

	private transient List<LigneApprovisionement> ligneApprovisionements = new ArrayList<LigneApprovisionement>() ;	
	private transient List<Produit> produits = new ArrayList<Produit>() ;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "inventaire")
	private List<LigneInventaire> ligneInventaire = new ArrayList<LigneInventaire>();

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



	public Boolean getInventoryBycip() {
		return inventoryBycip;
	}

	public void setInventoryBycip(Boolean inventoryBycip) {
		inventoryBycip = inventoryBycip;
	}

	public boolean isDoNotSelectAnyProduct() {
		return doNotSelectAnyProduct;
	}

	public void setDoNotSelectAnyProduct(boolean doNotSelectAnyProduct) {
		this.doNotSelectAnyProduct = doNotSelectAnyProduct;
	}

	public Boolean isInventoryBycipm() {
		return inventoryBycipm;
	}
	public Boolean getInventoryBycipm() {
		return inventoryBycipm;
	}


	public void setInventoryBycipm(Boolean inventoryBycipm) {
		this.inventoryBycipm = inventoryBycipm;
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


	public LigneInventaire hasItem(LigneInventaire item){
		if(ligneInventaire.isEmpty())return null;
		for (LigneInventaire line : ligneInventaire) {
			if(item.getProduit().equals(line.getProduit()))return line;
		}
		return null;
	}


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


	public void calculateMontantEcart(){
		montant = BigDecimal.ZERO ;
		for (LigneInventaire item : ligneInventaire) {
			montant = montant.add(item.getPrixTotal());

		}

	}

	public static LigneInventaire itemFromProduct(Produit prd ){
		if(prd == null )return null ;
		LigneInventaire item = new LigneInventaire();
		item.setProduit(prd);
		item.setQteEnStock(prd.getQuantiteEnStock());
		item.setQteReel(prd.getQuantiteEnStock());
		item.setDateSaisie(new Date());
		item.calculerEcart();
		item.caculMontantEcart();
		return item ;
	}




	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Inventaire No: ").append(getNumeroInventaire()).append(" Du :");
		sb.append(PharmaDateUtil.format(getDateInventaire(), "dd-MM-yyyy hh:mm")).append(" , ");
		sb.append("Agent: ").append(getAgent().displayName()).append(" , ");
		sb.append("Site: ").append(getSite());
		return sb.toString();
	}

	public boolean contientProduit(Produit produit) {
		if (ligneInventaire.isEmpty())return false;
		for (LigneInventaire ligne : ligneInventaire) {
			if (ligne.getProduit().equals(produit)) {
				return true;
			}
		}
		return false;
	}

	public boolean contientLigneApprovisionnemnt(LigneApprovisionement ligne){
		if (ligneInventaire.isEmpty())return false;
		for (LigneInventaire line : ligneInventaire) {
			if (ligne.getCipMaison().equals(line.getCipm())) {
				return true;
			}
		}
		return false;
	}
	public boolean contientProductOrOpproItems(Produit produit,LigneApprovisionement ligne){
		if(inventoryBycipm){
			return contientLigneApprovisionnemnt(ligne);
		}else {
			return contientProduit(produit);
		}
	}
	public void restoreStock(){
		DefaultInventoryService inventoryService = new DefaultInventoryService();
		if(getInventoryBycipm()){
			inventoryService.makeStockCorrectionFromInventoryByCipm(this);
		}else {
			inventoryService.makeStockCorrectionFromInventoryByCip(this);
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

	public static List<Inventaire> searchInventaire(String numeroInventaire,PharmaUser agent, Etat etat,Date beginDate,Date endDate) {
		StringBuilder searchQuery = new StringBuilder("SELECT o FROM Inventaire AS o WHERE o.id IS NOT NULL ");
		if (StringUtils.isNotBlank(numeroInventaire)) {
			numeroInventaire = "INV-"+StringUtils.removeStart(numeroInventaire, "INV-");
			return entityManager().createQuery("SELECT o FROM Inventaire AS o WHERE  o.numeroInventaire = :numeroInventaire ", Inventaire.class).setParameter("numeroInventaire", numeroInventaire).getResultList();
		} else {

			if (agent != null) {
				searchQuery.append(" AND o.agent = :agent ");
			}
			if (etat != null) {
				searchQuery.append(" AND o.etat = :etat ");
			}
			if (beginDate != null) {
				searchQuery.append(" AND o.dateInventaire >= :beginDate ");
			}
			if (endDate != null) {
				searchQuery.append(" AND o.dateInventaire <= :endDate ");
			}
			TypedQuery<Inventaire> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.id ASC").toString(), Inventaire.class);

			if (agent != null) {
				q.setParameter("agent", agent);
			}
			if (etat != null) {
				q.setParameter("etat", etat);
			}
			if (beginDate != null) {
				q.setParameter("beginDate", beginDate);
			}
			if (endDate != null) {
				q.setParameter("endDate", endDate);
			}
			return q.getResultList(); 
		}

	}

	public static TypedQuery<Inventaire> findInventaireByNomAgentLike(String nom) {
		if (nom == null || nom.length() == 0) throw new IllegalArgumentException("The nom argument is required");
		nom =nom + "%";
		EntityManager em = Inventaire.entityManager();
		TypedQuery<Inventaire> q = em.createQuery("SELECT o FROM Inventaire AS o WHERE LOWER(o.agent.lastName) LIKE LOWER(:nom) OR LOWER(o.agent.firstName) LIKE LOWER(:nom)   ORDER BY o.agent.lastName ASC", Inventaire.class);
		q.setParameter("nom", nom);
		return q;
	}

}
