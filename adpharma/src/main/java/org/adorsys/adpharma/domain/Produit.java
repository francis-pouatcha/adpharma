package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.adorsys.adpharma.services.DefaultInventoryService;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;


@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Produit", finders = { "findProduitsByDesignationLike", "findProduitsByProduitNumberLike", "findProduitsByQuantiteEnStock", "findProduitsByFamilleProduit", "findProduitsByRayon", "findProduitsByCipEquals", "findProduitsByDesignationEquals" })
@RooJson
public class Produit extends AdPharmaBaseEntity {

	private String produitNumber;
    
	@NotNull
	private String designation;

	private String fabricant;

	@ManyToOne
	private Rayon rayon;

	 @Enumerated(EnumType.STRING)
	 private CipType cipType = CipType.CIP39 ;
	
	/**
	 * Indique si le produit le produit est actif ou pas, ie si des mouvements peuvent etre effectues dessus 
	 */
	public Boolean actif = Boolean.TRUE ;
	
	
	@ManyToOne
	private FamilleProduit familleProduit;
	
	/**
	 * Qte de produits a commander en cas d'alerte de stock
	 */
	private BigInteger qteCommande= BigInteger.ZERO;
	

	/**
	 * Sous famille d'une famille de produits
	 */
	@ManyToOne(cascade=CascadeType.ALL)
	private SousFamilleProduit SousfamilleProduit;

	private BigInteger quantiteEnStock = BigInteger.ZERO;
	
	private  BigDecimal prixAchatU = BigDecimal.ZERO;

	private  BigDecimal prixVenteU = BigDecimal.ZERO;

	private transient BigDecimal prixAchatSTock = BigDecimal.ZERO;

	private transient BigDecimal prixVenteStock = BigDecimal.ZERO;

	private transient BigInteger qtevendu = BigInteger.ZERO;
	
	private boolean perissable;
	
	private boolean clone = false;

	
	public boolean isClone() {
		return clone;
	}


	public void setClone(boolean clone) {
		this.clone = clone;
	}


	public BigDecimal getPrixAchatSTock() {
		return prixAchatSTock;
	}
	

	public BigDecimal getPrixAchatU() {
		return prixAchatU;
	}


	public void setPrixAchatU(BigDecimal prixAchatU) {
		this.prixAchatU = prixAchatU;
	}


	public BigDecimal getPrixVenteU() {
		return prixVenteU;
	}


	public void setPrixVenteU(BigDecimal prixVenteU) {
		this.prixVenteU = prixVenteU;
	}


	public void setPrixAchatSTock(BigDecimal prixAchatSTock) {
		this.prixAchatSTock = prixAchatSTock;
	}

	public BigDecimal getPrixVenteStock() {
		return prixVenteStock;
	}

	public void setPrixVenteStock(BigDecimal prixVenteStock) {
		this.prixVenteStock = prixVenteStock;
	}
	public void initPrice(){
		this.prixVenteStock = prixVenteU!=null ?prixVenteU :BigDecimal.ZERO;
		this.prixAchatSTock = prixAchatU !=null ?prixAchatU :BigDecimal.ZERO;
	}

	public BigInteger getQtevendu() {
		return qtevendu;
	}

	public void setQtevendu(BigInteger qtevendu) {
		this.qtevendu = qtevendu;
	}
	
    

	public CipType getCipType() {
		if(this.cipType == null) return CipType.CIP39;
		return cipType;
	}


	public void setCipType(CipType cipType) {
		this.cipType = cipType;
	}



	/**
	 * Quantite de produits qui declanche une alerte de stock
	 */
	@Value("0")
	private BigInteger seuilComande;

	@Value("5")
	private BigDecimal tauxRemiseMax;

	@Value("0")
	private BigDecimal prixTotalStock;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateDerniereEntre;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateDerniereSortie;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateDerniereRupture;


	@Size(max = 256)
	private String posologie;

	@Size(max = 256)
	private String principeActif;

	@Value("false")
	private Boolean produitCompose;

	@Size(max = 256)
	private String observation;

	@ManyToOne
	private TVA tvaProduit;

	@ManyToOne
	private TauxMarge tauxDeMarge;

	@Column(unique = true)
	private String cip;

	@ManyToOne
	private ModeConditionement modeConditionement;

	private boolean venteAutorise = true;

	private boolean commander;

	private BigInteger plafondStock;
	
	/**
	 * Cette variable indique si le produit a deja deja approvisionne en stock ou pas
	 */
	private boolean inStock= Boolean.FALSE;
	
	@Embedded
	private ConfigurationSoldes configSolde;
	
	 public String getDesignation() {
	        return this.designation;
	    }

	public void calculQteVendue(Date dateMin, Date dateMax) {
		dateMin = dateMin != null ? dateMin : PharmaDateUtil.parse("10-10-2010 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
		dateMax = dateMax != null ? dateMax : PharmaDateUtil.parse("10-10-2090 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
		List<Object[]> venduByCip = MouvementStock.getQteVenduByCip(cip,null,null,null, dateMin, dateMax);
		if (!venduByCip.isEmpty()) {
			qtevendu = (BigInteger) venduByCip.iterator().next()[2];
		}
	}
	
	
	
	/**
	 * This method verify if a product have been put once in stock
	 * @param produit
	 * @return true or false
	 */
	public static boolean alreadyInStock(Produit produit){
		List<MouvementStock> mouvements = MouvementStock.findMouvementStocksByCipAndTypeMouvement(produit.getCip(), TypeMouvement.APPROVISIONEMENT).getResultList();
		if(!mouvements.isEmpty()){
			return true;
		}else{
			return false;
		}
	    
	}

	public void setTrueStockValue(){
		quantiteEnStock = new DefaultInventoryService().getTruecloseStockQte(this);
	}

	@ManyToOne
	private Filiale filiale;

	public void desableOrEnableSale(boolean status) {
		setVenteAutorise(status);
		Produit merge = (Produit) merge();
		List<LigneApprovisionement> lignes = LigneApprovisionement.findLigneApprovisionementsByProduit(this).getResultList();
		if (!lignes.isEmpty()) {
			for (LigneApprovisionement ligneApprovisionement : lignes) {
				ligneApprovisionement.setVenteAutorise(status);
				ligneApprovisionement.merge();
			}
		}
	}

	public BigDecimal calculPrixAchatStock() {
		BigDecimal amount = BigDecimal.ZERO;
		List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndCipEquals(BigInteger.ONE, cip).getResultList();
		if (!resultList.isEmpty()) {
			for (LigneApprovisionement ligne : resultList) {
				amount = amount.add(ligne.getPrixAchatUnitaire().multiply(BigDecimal.valueOf(ligne.getQuantieEnStock().longValue())));
			}
		}
		return amount;
	}

	public BigDecimal calculPrixVenteStock() {
		BigDecimal amount = BigDecimal.ZERO;
		List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndCipEquals(BigInteger.ONE, cip).getResultList();
		if (!resultList.isEmpty()) {
			for (LigneApprovisionement ligne : resultList) {
				amount = amount.add(ligne.getPrixVenteUnitaire().multiply(BigDecimal.valueOf(ligne.getQuantieEnStock().longValue())));
			}
		}
		return amount;
	}

	@Override
	protected void internalPreUpdate() {
		//quantiteEnStock= DefaultInventoryService.getTruecloseStockQte(this);
	}
	
	
	public void defineArchived(){
		archived = LigneApprovisionement.findLigneApprovisionementsByProduit(this).setMaxResults(1).getResultList().isEmpty();

	}

	@PostUpdate
	public void postUpdateProduct(){
		//quantiteEnStock  = InventoryService.getStockIncludeNegativeQte(this);
		if(quantiteEnStock.intValue()==0){
			dateDerniereRupture= new Date();
		}
		
	}

	
	 public static TypedQuery<Produit> findProduitsByCipEqualsAndVenteAutorise(String cip ,Boolean venteAutorise ) {
	        if (cip == null || cip.length() == 0) throw new IllegalArgumentException("The cip argument is required");
	        EntityManager em = Produit.entityManager();
	        TypedQuery<Produit> q = em.createQuery("SELECT o FROM Produit AS o WHERE o.cip = :cip AND  o.venteAutorise = :venteAutorise", Produit.class);
	        q.setParameter("cip", cip);
	        q.setParameter("venteAutorise", venteAutorise);
	        return q;
	    }
	 public static TypedQuery<Produit> findNextProduits(Long id ) {
	        if (id == null ) throw new IllegalArgumentException("The id argument is required");
	        EntityManager em = Produit.entityManager();
	        TypedQuery<Produit> q = em.createQuery("SELECT o FROM Produit AS o WHERE o.id > :id And o.actif =:actif ORDER BY o.id  ", Produit.class);
	        q.setParameter("id", id);
	        q.setParameter("actif", Boolean.TRUE);
	        return q;
	    }
	 public static TypedQuery<Produit> findPreviousProduits(Long id ) {
	        if (id == null ) throw new IllegalArgumentException("The id argument is required");
	        EntityManager em = Produit.entityManager();
	        TypedQuery<Produit> q = em.createQuery("SELECT o FROM Produit AS o WHERE o.id < :id  And o.actif =:actif  ORDER BY o.id DESC", Produit.class);
	        q.setParameter("id", id);
	        q.setParameter("actif", Boolean.TRUE);
	        return q;
	    }
	 
	public void calculTransientPrice() {

		List<LigneApprovisionement> lines = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndCipEquals(BigInteger.ONE, cip).getResultList();
		if (!lines.isEmpty()) {
			for (LigneApprovisionement ligne : lines) {
				prixAchatSTock = prixAchatSTock.add(ligne.CalculeMontantStock());
				prixVenteStock = prixVenteStock.add(ligne.calculePrixVenteStock());
			}
		}
	}

	public void calculTransientQteVendue() {
		List<LigneApprovisionement> lines = LigneApprovisionement.findLigneApprovisionementsByQteVendueUpThanAndCipEquals(BigInteger.ONE, cip).getResultList();
		if (!lines.isEmpty()) {
			for (LigneApprovisionement ligne : lines) {
				qtevendu = qtevendu.add(ligne.getQuantiteVendu());
			}
		}
	}

	@PostPersist
	public void postPersist() {
		produitNumber = NumberGenerator.getNumber("PD-", getId(), 4);
	}
	
	public boolean isOut(){
		if(quantiteEnStock==null) quantiteEnStock = BigInteger.ZERO;
		return quantiteEnStock.intValue() <= 0 ;
	}
	
	public boolean existe(){
	return	!Produit.findProduitsByCipEquals(cip).getResultList().isEmpty() ;
	}

	// Permet de verifier si un produit est en alerte de stock ou pas
	public boolean isAlert(){
		if (quantiteEnStock == null || seuilComande == null) return true ;
		return quantiteEnStock.intValue() <= seuilComande.intValue();
	}

	public BigInteger getDefaultCommandQte(){
		if (quantiteEnStock == null || seuilComande == null) return BigInteger.ONE ;
		if (quantiteEnStock.intValue() < seuilComande.intValue()){
			return seuilComande.subtract(quantiteEnStock).add(BigInteger.ONE);
		}else {
			return BigInteger.ZERO ;
		}

	}


	public void protectSomeField() {
		Produit produit = Produit.findProduit(getId());
		produitNumber = produit.getProduitNumber();
		footPrint = produit.getFootPrint();
		cip = produit.getCip();
		quantiteEnStock = produit.getQuantiteEnStock();
		dateDerniereEntre = produit.getDateDerniereEntre();
		dateDerniereSortie = produit.getDateDerniereSortie();
		commander = isAlert();
		if (isOut()) dateDerniereRupture = dateDerniereRupture ==null ?new Date():dateDerniereRupture ;

	}

	public void validate(BindingResult bindingResult,Boolean whithcip) {
		if(!(whithcip ==null || whithcip)) cip = null ;
		if (StringUtils.isBlank(getCip())) {
			cip = generateDefaultCip();
		}
		List<Produit> resultList = Produit.findProduitsByCipEquals(cip).getResultList();
		if (!resultList.isEmpty()) {
			ObjectError error = new ObjectError("cip", "un produit avec ce Cip existe");
			bindingResult.addError(error);
		}
		System.out.println(cip);
	}

	public void addproduct(BigInteger qte) {
		setQuantiteEnStock(getQuantiteEnStock().add(qte));
	}

	public void removeProduct(BigInteger qte) {
		quantiteEnStock = getQuantiteEnStock().subtract(qte);
	}

	public String generateDefaultCip() {
		cip = "9" + RandomStringUtils.randomNumeric(6) + "9";
		while (!Produit.findProduitsByCipEquals(cip).getResultList().isEmpty()) {
			cip = "9" + RandomStringUtils.randomNumeric(6) + "9";
		}
		return cip;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getDesignation()).append(" , ");
		sb.append(getCip());
		return sb.toString();
	}
	
	// Override json product
	public String toJson(){
		return new JSONSerializer().transform(new DateTransformer("dd-MM-yyyy hh:mm:ss"), Date.class).include("cip", "designation", "quantiteEnStock"
				,"rayon.codeRayon","produitNumber","fabricant","prixAchatU","prixVenteU","filiale.libelle","tauxDeMarge.margeValue","tauxRemiseMax","prixAchatSTock","prixVenteStock","id").exclude("*","*.class").serialize(this);
	}
	 public static String toJsonArray(Collection<Produit> collection) {
	        return new JSONSerializer().transform(new DateTransformer("dd-MM-yyyy hh:mm:ss"), Date.class).include("cip", "designation", "quantiteEnStock"
	        		,"rayon.codeRayon","produitNumber","fabricant","prixAchatU","prixVenteU","filiale.libelle","tauxDeMarge.margeValue","tauxRemiseMax","prixAchatSTock","prixVenteStock","id").exclude("*","*.class").serialize(collection);
	    }
	public String toJson1(){
		return new JSONSerializer().include("cip", "designation", "quantiteEnStock").exclude("*.class", "*").serialize(this);
	}
	
	public void calculPrixTotalStock() {
		prixTotalStock = BigDecimal.ZERO;
		List<LigneApprovisionement> ligne = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndCipEquals(BigInteger.ZERO, cip).getResultList();
		if (!ligne.isEmpty()) {
			for (LigneApprovisionement ligneApprovisionement : ligne) {
				prixTotalStock = prixTotalStock.add(ligneApprovisionement.CalculeMontantStock());
			}
		}
	}

	public void getFournisseurPrice(Fournisseur fournisseur) {
		List<BigDecimal> lastPrices = new ArrayList<BigDecimal>();
		lastPrices= LigneApprovisionement.findlastPrices(this,fournisseur);
		lastPrices = lastPrices.isEmpty()?LigneApprovisionement.findlastPrices(this):lastPrices;
		if(!lastPrices.isEmpty()){
			setPrixAchatSTock(lastPrices.get(0));
			setPrixVenteStock(lastPrices.get(1));
		}else {
			setPrixAchatSTock(prixAchatU != null ?prixAchatU :BigDecimal.ZERO);
			setPrixVenteStock(prixVenteU != null ?prixVenteU :BigDecimal.ZERO);
		}

	}
	
	
    @PreUpdate
	//@Transactional(readOnly=true)
	public void notifyProduct(){
		if(this.isAlert() && this.getActif()==true){
			this.setCommander(Boolean.TRUE);
		}else if(!this.isAlert() && this.getActif() == true){
			this.setCommander(Boolean.FALSE);
		}
		/*if(Produit.alreadyInStock(this)){
			this.setInStock(Boolean.TRUE);
		}*/
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Produit other = (Produit) obj;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		return true;
	}

	public String displayName() {
		return new StringBuilder().append(getDesignation()).append(" , ").append(getCip()).toString();
	}
	
	
	/* ----------------------------Utilitaires de configuration de soldes -------------------------------------*/
	
	// Desactiver un solde
	public static void cancelSolde(Produit produit){
		produit.getConfigSolde().setActiveConfig(Boolean.FALSE);
		produit.merge();
		produit.flush();
	}
	
	// Activer un solde
	public static void activateSolde(Produit produit){
		produit.getConfigSolde().setActiveConfig(Boolean.TRUE);
		produit.merge();
		produit.flush();
	}
	
	// Supprimer un solde
	public static void deleteSolde(Produit produit){
		produit.setConfigSolde(null);
		produit.merge();
		produit.flush();
	}
	
	// Methode qui verfie si le produit est en solde ou pas
	public static boolean hasSolde(Produit produit){
		  ConfigurationSoldes solde = produit.getConfigSolde();
		  if(solde!=null){
			  if(solde.getActiveConfig()==Boolean.TRUE){
				  return true;
			  }else{
				  return false;
			  }
		  }
		return false;
	}
	
	// Recherche de produits ayant une configuration de solde
		public static TypedQuery<Produit> findProduitsWithConfigSolde() {
			EntityManager em = Produit.entityManager();
			TypedQuery<Produit> q = em.createQuery("SELECT o FROM Produit AS o WHERE o.configSolde IS NOT NULL ORDER BY o.designation ASC", Produit.class);
			return q;
		}
	
	
	
	
	
	

	public static TypedQuery<Produit> findProduitsByQuantiteEnStock(BigInteger quantiteEnStock) {
		if (quantiteEnStock == null) throw new IllegalArgumentException("The quantiteEnStock argument is required");
		EntityManager em = Produit.entityManager();
		TypedQuery<Produit> q = em.createQuery("SELECT o FROM Produit AS o WHERE o.quantiteEnStock <= :quantiteEnStock  And o.actif =:actif  ORDER BY o.designation ASC", Produit.class);
		q.setParameter("quantiteEnStock", quantiteEnStock);
		q.setParameter("actif", Boolean.TRUE);
		return q;
	}
	
	
	// Recherche d'un produit par son cip egale en triant les colonnes
	public static TypedQuery<Produit> findProduitsByCipEqualsNew(String cip) {
        if (cip == null || cip.length() == 0) throw new IllegalArgumentException("The cip argument is required");
        EntityManager em = Produit.entityManager();
        TypedQuery<Produit> q = em.createQuery("SELECT o.cip, o.designation, o.quantiteEnStock, o.prixAchatU, o.prixVenteU FROM Produit AS o WHERE o.cip = :cip", Produit.class);
        q.setParameter("cip", cip);
        return q;
    }

	public static List<Produit> findProduitAlerteStockEntries(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM Produit AS o WHERE o.quantiteEnStock <= o.seuilComande ORDER BY o.designation ASC ", Produit.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}

	public static List<Produit> findProduitRuptureStockEntries(int firstResult, int maxResults) {
		return Produit.findProduitsByQuantiteEnStock(BigInteger.ZERO).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}
	
	// Alternative requete native
	public static List<Produit> findProduitRuptureStockEntries1(int firstResult, int maxResults) {
		EntityManager em = Produit.entityManager();
		String test= "SELECT o FROM Produit AS o WHERE o.quantiteEnStock <= :quantiteEnStock  And o.actif =:actif  ORDER BY o.designation ASC";
		em.createNativeQuery("SELECT * FROM produit as p WHERE p.quantite_en_stock <= ?1 AND p.actif= ?2 ORDER BY p.designation ASC", Produit.class);
		
		return null;
	}

	public static List<Produit> findProduitEntries(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM Produit o WHERE o.actif =:actif  ORDER BY o.designation ASC", Produit.class).setParameter("actif", Boolean.TRUE).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}
	
	public static List<Produit> findProduitEntries1(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM Produit o ORDER BY o.designation ASC", Produit.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}

	public static TypedQuery<Produit> findProduitsByOrdreAlphabetique(String debut, String fin) {
		debut = StringUtils.isBlank(debut) ? "A" : debut;
		fin = StringUtils.isBlank(fin) ? "Z" : fin;
		debut = debut + "%";
		fin = fin + "%";
		EntityManager em = Produit.entityManager();
		TypedQuery<Produit> q = em.createQuery("SELECT o FROM Produit AS o WHERE LOWER(o.designation) BETWEEN LOWER(:debut) AND LOWER(:fin)  And o.actif =:actif ORDER BY o.designation ASC ", Produit.class);
		q.setParameter("debut", debut);
		q.setParameter("fin", fin);
		q.setParameter("actif", Boolean.TRUE);
		return q;
	}

	public static long countOutProduits() {
		return entityManager().createQuery("SELECT COUNT(o) FROM Produit o WHERE o.quantiteEnStock <= :quantiteEnStock", Long.class).setParameter("quantiteEnStock", BigInteger.ZERO).getSingleResult();
	}

	public static TypedQuery<Produit> findProduitsByRayon(Rayon rayon) {
		if (rayon == null) throw new IllegalArgumentException("The rayon argument is required");
		EntityManager em = Produit.entityManager();
		TypedQuery<Produit> q = em.createQuery("SELECT o FROM Produit AS o WHERE o.rayon = :rayon And o.actif =:actif  ORDER BY o.designation ASC", Produit.class);
		q.setParameter("rayon", rayon);
		q.setParameter("actif", Boolean.TRUE);
		return q;
	}
	
	
	public static TypedQuery<Produit> findProduitsByCip(String cip) {
		if (cip == null) throw new IllegalArgumentException("The cip argument is required");
		EntityManager em = Produit.entityManager();
		TypedQuery<Produit> q = em.createQuery("SELECT o FROM Produit AS o WHERE o.cip = :cip ", Produit.class);
		q.setParameter("cip", cip);
		return q;
	}

	public static TypedQuery<Produit> findProduitsByDesignationLike(String designation) {
		if (designation == null || designation.length() == 0) throw new IllegalArgumentException("The designation argument is required");
		designation =designation + "%";
		EntityManager em = Produit.entityManager();
		TypedQuery<Produit> q = em.createQuery("SELECT o FROM Produit AS o WHERE LOWER(o.designation) LIKE LOWER(:designation)  And o.actif =:actif  order By  o.designation ASC ", Produit.class);
		q.setParameter("designation", designation); 
		q.setParameter("actif", Boolean.TRUE);
		return q;
	}
	
	public static TypedQuery<Produit> findInStockProduitsByDesignationLike(String designation) {
		if (designation == null || designation.length() == 0) throw new IllegalArgumentException("The designation argument is required");
		designation =designation + "%";
		EntityManager em = Produit.entityManager();
		TypedQuery<Produit> q = em.createQuery("SELECT o FROM Produit AS o WHERE LOWER(o.designation) LIKE LOWER(:designation)  And o.actif =:actif And o.inStock =:inStock  order By  o.designation ASC ", Produit.class);
		q.setParameter("designation", designation); 
		q.setParameter("actif", Boolean.TRUE);
		q.setParameter("inStock", Boolean.TRUE);
		return q;
	}
	
	
	public static TypedQuery<Produit> findProduitsForOrderByDesignationLike(String designation) {
		if (designation == null || designation.length() == 0) throw new IllegalArgumentException("The designation argument is required");
		designation ="%"+designation + "%";
		EntityManager em = Produit.entityManager();
		TypedQuery<Produit> q = em.createQuery("SELECT o FROM Produit AS o WHERE LOWER(o.designation) LIKE LOWER(:designation)  order By  o.designation ASC ", Produit.class);
		q.setParameter("designation", designation);
		return q;
	}

	public static BigInteger findQteToCompensate(String cip,BigInteger quantieEnStock) {
		if (cip == null || cip.length() == 0) throw new IllegalArgumentException("The cip argument is required");
		if (quantieEnStock == null ) throw new IllegalArgumentException("The quantieEnStock argument is required");
		EntityManager em = LigneApprovisionement.entityManager();
		Query q = em.createQuery("SELECT SUM(o.quantieEnStock) FROM LigneApprovisionement AS o WHERE o.quantieEnStock < :quantieEnStock AND o.cip = :cip ");
		q.setParameter("quantieEnStock",quantieEnStock);
		q.setParameter("cip", cip);
		Object object = q.getSingleResult();
		if (object == null) {
			return BigInteger.ZERO;
		}else {
			return (BigInteger)object;
		}
	}

	public static TypedQuery<LigneApprovisionement> findByCipAndQteStockLessThan(String cip,BigInteger quantieEnStock) {
		if (cip == null || cip.length() == 0) throw new IllegalArgumentException("The cip argument is required");
		if (quantieEnStock == null ) throw new IllegalArgumentException("The quantieEnStock argument is required");
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.quantieEnStock < :quantieEnStock AND o.cip = :cip order by o.id ASC " , LigneApprovisionement.class);
		q.setParameter("quantieEnStock",quantieEnStock);
		q.setParameter("cip", cip);
		return q;
	}

	public static TypedQuery<Produit> search(FamilleProduit familleProduit,SousFamilleProduit sousFamilleProduit ,String cip, String designation, String beginBy, String endBy, Rayon rayon, Filiale filiale ,Date dateDerniereRupture,BigInteger qte) {
		StringBuilder searchQuery = new StringBuilder("SELECT o FROM Produit AS o WHERE  o.inStock IS :inStock");
		if (StringUtils.isNotBlank(cip)) {
			return entityManager().createQuery("SELECT o FROM Produit AS o WHERE  o.cip = :cip", Produit.class).setParameter("cip", cip);
		}
		if (StringUtils.isNotBlank(designation)) {
			designation = designation + "%";
			searchQuery.append(" AND  LOWER(o.designation) LIKE LOWER(:designation) ");
		} 

		if (StringUtils.isNotBlank(endBy)) {
			endBy = endBy + "%";
			searchQuery.append(" AND  LOWER(o.designation) <= LOWER(:endBy) ");
		}

		if (StringUtils.isNotBlank(beginBy)) {
			beginBy = beginBy + "%";
			searchQuery.append(" AND  LOWER(o.designation) >= LOWER(:beginBy) ");
		}

		if (familleProduit != null) {
			searchQuery.append(" AND o.familleProduit = :familleProduit ");
		}
		if (sousFamilleProduit != null) {
			searchQuery.append(" AND o.sousfamilleProduit = :sousfamilleProduit ");
		}
		if (rayon != null) {
			searchQuery.append(" AND o.rayon = :rayon ");
		}
		if (filiale != null) {
			searchQuery.append(" AND o.filiale = :filiale ");
		}

		if (dateDerniereRupture != null) {
			searchQuery.append(" AND o.dateDerniereRupture >= :dateDerniereRupture  ");
		}
		if (qte != null) {
			searchQuery.append("  AND o.quantiteEnStock <= :qteRup  ");
		}
		TypedQuery<Produit> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.designation ASC").toString(), Produit.class);
		if (StringUtils.isNotBlank(designation)) {
			q.setParameter("designation", designation);
		} 
		if (StringUtils.isNotBlank(endBy)) {
			q.setParameter("endBy", endBy);

		}

		if (StringUtils.isNotBlank(beginBy)) {
			q.setParameter("beginBy", beginBy);

		}

		if (rayon != null) {
			q.setParameter("rayon", rayon);
		}
		if (familleProduit != null) {
			q.setParameter("familleProduit", familleProduit);
		}
		if (sousFamilleProduit != null) {
			q.setParameter("sousfamilleProduit", sousFamilleProduit);

		}
		if (filiale != null) {
			q.setParameter("filiale", filiale);
		}

		if (dateDerniereRupture != null) {
			q.setParameter("dateDerniereRupture", dateDerniereRupture);
		}
		if (qte != null) {
			q.setParameter("qteRup", qte);
		}
		q.setParameter("inStock", true);
		return q;
	}
}