package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.services.SupplyService;
import org.adorsys.adpharma.utils.CipMgenerator;
import org.adorsys.adpharma.utils.Contract;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

/**
 * @author adorsys-clovis
 *
 */
@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "LigneApprovisionement", finders = { "findLigneApprovisionementsByProduit", "findLigneApprovisionementsByApprovisionement", "findLigneApprovisionementsByCipMaisonEquals", "findLigneApprovisionementsByDesignationLike", "findLigneApprovisionementsByQuantieEnStockAndDesignationLike", "findLigneApprovisionementsByQuantieEnStockAndCipEquals" })
@RooJson(deepSerialize = false)
public class LigneApprovisionement extends AdPharmaBaseEntity {

	private String lotNumber;

	private int indexLine;

	public boolean isSkipPriceConvertion() {
		return skipPriceConvertion;
	}

	public void setSkipPriceConvertion(boolean skipPriceConvertion) {
		this.skipPriceConvertion = skipPriceConvertion;
	}

	private String cip;

	private String viewMsg ;

	public String getViewMsg() {
		return viewMsg;
	}

	public void setViewMsg(String viewMsg) {
		this.viewMsg = viewMsg;
	}

	public String getCip() {
		return cip;
	}

	private transient boolean skipPriceConvertion = Boolean.FALSE;

	public void setCip(String cip) {
		this.cip = cip;
	}

	@Size(min = 7)
	private String cipMaison = null;

	@NotNull
	@ManyToOne(cascade=CascadeType.ALL)
	private Produit produit;

	private String designation;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date dateFabrication;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date datePeremtion = DateUtils.addYears(new Date(), 10);

	private String agentSaisie;
	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateSaisie = new Date();

	private BigInteger quantiteAprovisione;
	
	private BigInteger quantiteUniteGratuite = BigInteger.ZERO;

	private BigInteger quantiteVendu = BigInteger.ZERO;
	
	private BigInteger quantiteReclame= BigInteger.ZERO;

	public Produit getProduit() {
		return produit;
	}

	public void setProduit(Produit produit) {
		this.produit = produit;
	}

	public BigInteger getQuantiteVendu() {
		return quantiteVendu;
	}

	public void setQuantiteVendu(BigInteger quantiteVendu) {
		this.quantiteVendu = quantiteVendu;
	}

	private BigInteger quantieEnStock = BigInteger.ZERO;

	private BigInteger quantiteSortie = BigInteger.ZERO;

	private BigDecimal prixAchatUnitaire = BigDecimal.ZERO;

	private BigDecimal prixAchatTotal;

	private BigDecimal margeBrute = BigDecimal.ZERO;

	private BigDecimal prixVenteUnitaire = BigDecimal.ZERO;

	private boolean venteAutorise = true;

	private BigDecimal remiseMax = BigDecimal.ZERO;

	private transient BigDecimal prixAchatSTock;

	private transient BigDecimal prixVenteStock;
	
	public BigDecimal getPrixAchatSTock() {
		return prixAchatSTock;
	}
	
	public BigDecimal getPrixVenteStock() {
		return prixVenteStock;
	}
	
	public void setPrixAchatSTock(BigDecimal prixAchatSTock) {
		this.prixAchatSTock = prixAchatSTock;
	}
	
	public void setPrixVenteStock(BigDecimal prixVenteStock) {
		this.prixVenteStock = prixVenteStock;
	}

	public BigInteger getQteCip() {
		return qteCip;
	}

	public void setQteCip(BigInteger qteCip) {
		this.qteCip = qteCip;
	}
	
	public Approvisionement getApprovisionement() {
        return this.approvisionement;
    }
    
    public void setApprovisionement(Approvisionement approvisionement) {
        this.approvisionement = approvisionement;
    }

	private transient BigInteger qteCip;

	@ManyToOne(cascade=CascadeType.ALL)
	private Approvisionement approvisionement;

	@Value("true")
	private Boolean remiseAutorise;

	private transient String fournisseur;
	
	private transient String nonRayon;

	private transient String saisiele;

	public LigneApprovisionement transforme(BigInteger qte, Model uiModel) {
		List<TransformationProduit> relation = TransformationProduit.findTransformationProduitsByProduitOrigine(produit).getResultList();
		LigneApprovisionement ligne = null;
		TransformationProduit tf = null;
		if (!relation.isEmpty()) {
			tf = relation.iterator().next();
			if (!tf.getActif()) {
				uiModel.addAttribute("apMessage", "imppossible de transformer Ce produit ! RELATION DE DECOMPOSITION INACTIF ");
				return null;
			}
			ligne = new LigneApprovisionement();
			ligne.setProduit(tf.getProduitCible());
			ligne.setQuantiteAprovisione(qte.multiply(tf.getQteCible()));
			BigInteger bigInteger = prixAchatUnitaire.toBigInteger();
			bigInteger = bigInteger.divide(BigInteger.valueOf(tf.getQteCible().longValue()));
			ligne.setPrixAchatUnitaire(BigDecimal.valueOf(bigInteger.longValue()));
			ligne.setDatePeremtion(datePeremtion);
			ligne.setDateFabrication(dateFabrication);
			ligne.setApprovisionement(approvisionement);
			ligne.setSkipPriceConvertion(true);
			quantieEnStock = quantieEnStock.subtract(qte);
			quantiteSortie = quantiteSortie.add(qte);
			ligne.setPrixVenteUnitaire(tf.getPrixVente());
		} else {
			uiModel.addAttribute("apMessage", "imppossible de transformer Ce produit ! AUCUNE  RELATION DE DECOMPOSITION TROUVEE ");
			return null;
		}
		return ligne;
	}

	public Boolean isOldItem(List<LigneApprovisionement> lines){
		List<LigneApprovisionement> items  = null ;
		if(!lines.isEmpty()){
			LigneApprovisionement next = lines.iterator().next() ;
			if(next.getId().intValue() == getId().intValue()) return true ;
		}
		return false ;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public LigneApprovisionement clone() {
		LigneApprovisionement line = new LigneApprovisionement();
		line.setId(id);
		line.setCip(cip);
		line.setCipMaison(cipMaison);
		line.setPrixVenteUnitaire(prixVenteUnitaire);
		line.setDesignation(produit.getDesignation());
		line.setQuantiteAprovisione(quantiteAprovisione);
		line.setQuantieEnStock(quantieEnStock);
		line.setQteCip(getProduit().getQuantiteEnStock());
		line.setFournisseur(getApprovisionement().getFounisseur().displayShotName());
		line.setSaisiele(PharmaDateUtil.format(getDateSaisie(), PharmaDateUtil.DATETIME_PATTERN_LONG));
		//calculRemise();
		line.setQteCip(produit.getQuantiteEnStock());
		line.setViewMsg(viewMsg);
		line.setRemiseMax(remiseMax);
		line.setNonRayon(getProduit().getRayon().getName());
		return line;
	}

	public String toJson() {
		return new JSONSerializer().transform(new DateTransformer("dd-MM-yyyy"), Date.class).include("id","cip","cipMaison","prixVenteUnitaire","designation","quantiteAprovisione","quantieEnStock","fournisseur","saisiele","remiseMax","viewMsg","qteCip","nonRayon", "produit.configSolde.tauxSolde","datePeremtion", "produit.configSolde.activeConfig", "produit.quantiteEnStock").exclude("*","*.class").serialize(this);
	}
	
	public String toJson2() {
		return new JSONSerializer().include("id","cip","cipMaison","prixVenteUnitaire","prixAchatUnitaire","designation","quantiteAprovisione","quantiteReclame","quantieEnStock","fournisseur","qteCip","nonRayon").exclude("*","*.class").serialize(this);
	}
	
	public static String toJsonArray(Collection<LigneApprovisionement> collection) {
		return new JSONSerializer().transform(new DateTransformer("dd-MM-yyyy"), Date.class).include("id","cip","cipMaison","prixVenteUnitaire","designation","quantiteAprovisione","quantieEnStock","quantieEnStock","fournisseur","saisiele","remiseMax","viewMsg","qteCip","nonRayon","datePeremtion").exclude("*","*.class").serialize(collection);
	}

	public String getFournisseur() {
		return fournisseur;
	}

	public void setFournisseur(String fournisseur) {
		this.fournisseur = fournisseur;
	}

	public String getNonRayon() {
		return nonRayon;
	}

	public void setNonRayon(String nonRayon) {
		this.nonRayon = nonRayon;
	}

	public String getSaisiele() {
		return saisiele;
	}

	public void setSaisiele(String saisiele) {
		this.saisiele = saisiele;
	}

	public void CalculePaTotal() {
		Contract.notNull("qte approvisionne", prixAchatUnitaire);
		Contract.notNull("Quantite Aprovisionnee", quantiteAprovisione); 
		quantiteUniteGratuite = quantiteUniteGratuite==null?BigInteger.ZERO:quantiteUniteGratuite;
		prixAchatTotal = BigDecimal.valueOf(prixAchatUnitaire.multiply(BigDecimal.valueOf(quantiteAprovisione.longValue()-quantiteUniteGratuite.longValue())).longValue());
	}

	public BigInteger pushProductsOutForInventory(BigInteger amount){
		if (quantieEnStock.intValue() <=0)return amount;
		if(quantieEnStock.intValue() <= amount.intValue()){
			this.setQuantiteSortie(this.getQuantiteSortie().add(quantieEnStock));
			amount = amount.subtract(quantieEnStock);
		}else {
			this.setQuantiteSortie(this.getQuantiteSortie().add(amount));
			amount = amount.subtract(amount);
		}
		CalculeQteEnStock();
		merge();
		return amount ;
	}

	public BigInteger pushProductsInForInventory(BigInteger amount){
		BigInteger pushInQuantity = getIncreaseQte();
		if(pushInQuantity.intValue() <= 0) return amount;
		if(pushInQuantity.intValue() <= amount.intValue()){
			setQuantiteSortie(BigInteger.ZERO);
			setQuantiteVendu(BigInteger.ZERO);
			amount = amount.subtract(pushInQuantity);
		}else {
			if(getQuantiteSortie().intValue() <= amount.intValue()){
				if(getQuantiteSortie().intValue()>0) amount = amount.subtract(getQuantiteSortie());
				setQuantiteSortie(BigInteger.ZERO);
				setQuantiteVendu(getQuantiteVendu().subtract(amount));

			}else {
				amount = amount.subtract(getQuantiteVendu());
				setQuantiteVendu(BigInteger.ZERO);
				setQuantiteSortie(getQuantiteSortie().subtract(amount));
			}
			amount = BigInteger.ZERO;
		}
		CalculeQteEnStock();
		merge();
		return amount;
	}

	public BigInteger pushAllInForInventory(BigInteger amount)
	{
		BigInteger pushInQuantity = getIncreaseQte();
		if(pushInQuantity.intValue() < amount.intValue())
		{
			BigInteger diff = amount.subtract(pushInQuantity);
			quantiteAprovisione = quantiteAprovisione.add(diff);
			quantiteSortie = quantiteSortie.add(diff);
		}
		pushInQuantity = getIncreaseQte();
		setQuantiteSortie(BigInteger.ZERO);
		setQuantiteVendu(BigInteger.ZERO);
		amount = amount.subtract(pushInQuantity);
		CalculeQteEnStock();
		merge();
		return amount;
	}



	public BigInteger getIncreaseQte() {
		return quantiteVendu.add(quantiteSortie);
	}

	public BigInteger getDecreaseQte() {
		return getQuantieEnStock().intValue()<0?BigInteger.ZERO:getQuantieEnStock();
	}

	public Long getRemise() {
		BigDecimal remise = BigDecimal.ZERO;
		PharmaUser pharmaUser = SecurityUtil.getPharmaUser();
		if (getRemiseAutorise()) {
			if (pharmaUser == null) {
				return remise.longValue();
			} else {
				BigDecimal tauxRemise = pharmaUser.getTauxRemise();
				if (tauxRemise == null) {
					return remise.longValue();
				} else {
					BigDecimal divide = (getPrixVenteUnitaire().multiply(tauxRemise)).divide(BigDecimal.valueOf(100));
					return divide.longValue();
				}
			}
		} else {
			return remise.longValue();
		}
	}



	public BigDecimal CalculeMontantStock() {
		Contract.notNull("qte approvisionne", prixAchatUnitaire);
		Contract.notNull("quantieEnStock", quantieEnStock);
		return BigDecimal.valueOf(prixAchatUnitaire.multiply(BigDecimal.valueOf(quantieEnStock.longValue())).longValue());
	}

	public BigDecimal calculePrixVenteStock() {
		Contract.notNull("qte approvisionne", prixAchatUnitaire);
		Contract.notNull("quantieEnStock", quantieEnStock);
		return BigDecimal.valueOf(prixVenteUnitaire.multiply(BigDecimal.valueOf(quantieEnStock.longValue())).longValue());
	}

	public void CalculeQteEnStock() {
		Contract.notNull("quatite vendu", quantiteVendu);
		Contract.notNull("Quantite Aprovisionnee", quantiteAprovisione);
		quantieEnStock = (quantiteAprovisione.subtract(quantiteVendu)).subtract(quantiteSortie);
	}

	public void CalculerMargeBrute() {
		Contract.notNull("prix Vente Unitaire", prixVenteUnitaire);
		Contract.notNull("prix Achat Unitaire", prixAchatUnitaire);
		margeBrute = BigDecimal.valueOf(prixVenteUnitaire.subtract(prixAchatUnitaire).longValueExact());
	}

	public void CalculerPrixVente() {
		if (prixVenteUnitaire.longValue() == Long.valueOf(0)) {
			prixVenteUnitaire = BigDecimal.valueOf(prixAchatUnitaire.add(prixAchatUnitaire.multiply(produit.getTauxDeMarge().getMargeValue().divide(BigDecimal.valueOf(100)))).longValue());
		} else {
			if(!skipPriceConvertion) prixVenteUnitaire = convertoCfa(prixVenteUnitaire);
		}
		prixVenteUnitaire = new BigDecimal(ProcessHelper.roundMoney(prixVenteUnitaire.toBigInteger()));
	}

	@Transactional
	public static int  deleteAllItems(Approvisionement approvisionement) {
		Query query = entityManager().createQuery("DELETE FROM LigneApprovisionement o WHERE  o.approvisionement = :approvisionement  ");
		query.setParameter("approvisionement", approvisionement);
		return query.executeUpdate() ;
	}

	public void protectSomeField() {
		LigneApprovisionement ligne = LigneApprovisionement.findLigneApprovisionement(getId());
		lotNumber = ligne.getLotNumber();
		cip = ligne.getProduit().getCip();
		cipMaison = ligne.getCipMaison();
		designation = ligne.getProduit().getDesignation();
		quantiteSortie = ligne.getQuantiteSortie();
		quantiteVendu = ligne.getQuantiteVendu();
		agentSaisie = ligne.getAgentSaisie();
		dateSaisie = ligne.getDateSaisie();
		approvisionement = ligne.getApprovisionement();
		quantieEnStock = ligne.getQuantieEnStock();
		produit = ligne.getProduit();
		footPrint = ligne.getFootPrint();
		if (approvisionement.getCloturer()) {
			quantiteAprovisione = ligne.getQuantiteAprovisione();
			if(prixAchatUnitaire == null)  prixAchatUnitaire = ligne.getPrixAchatUnitaire();
			CalculePaTotal();
		}
	}

	protected void internalPreUpdate() {
		CalculerMargeBrute();
	}

	public boolean canCompencse(BigInteger qte){
		boolean test = quantieEnStock.subtract(qte).intValue() > 0 ;
		System.out.println(test);
		return test ;
	}


	public void compenserStock(){
		new SupplyService().compenserStock(getQuantieEnStock(), this);
	}

	@Override
	protected void internalPrePersist() {
		agentSaisie = SecurityUtil.getUserName();
		dateSaisie = new Date();
		designation = produit.getDesignation();
		cip = produit.getCip();
		quantieEnStock = quantiteAprovisione;
		venteAutorise = produit.isVenteAutorise();
		if(!skipPriceConvertion) prixAchatUnitaire = convertoCfa(prixAchatUnitaire);
		CalculerPrixVente();
		CalculePaTotal();
		/* long ide = LigneApprovisionement.countLigneApprovisionements();
        cipMaison = CipMgenerator.getCipM(ide + "");*/
	}

	public Boolean isValide(Model uiModel) {
		String msg= "" ;
		if (prixAchatUnitaire == null || prixVenteUnitaire == null){
			uiModel.addAttribute("apMessage", "le prix d'achat et de vente doivent etre non null !") ;
			return false ;
		}
		if (prixAchatUnitaire.intValue() > prixVenteUnitaire.intValue()) {
			uiModel.addAttribute("apMessage", "le prix d'achat doit etre inferieur  au prix de vente !") ;
			return false ;
		}

		return true ;
	}


	@PostPersist
	public void postPersist() {
		lotNumber = NumberGenerator.getNumber("LOT-", getId(), 4);
		cipMaison = cipMaison = CipMgenerator.getCipM(getId() + "");
	}

	@PostLoad
	public void postload() {
		calculRemise();
		designation = produit.getDesignation();
	}
	
	

	public void augmenterQteApprovisione(BigInteger qte) {
		setQuantiteAprovisione(quantiteAprovisione.add(qte));
	}

	public void augmenterQteStock(BigInteger qte) {
		setQuantieEnStock(quantieEnStock.add(qte));
	}

	public void retourEnStock(BigInteger qte) {
		if (quantiteVendu.intValue() >= qte.intValue()) {
			quantiteVendu = quantiteVendu.subtract(qte);
			CalculeQteEnStock();
		}
	}

	public BigInteger getQteToCompensate(){
		return  quantieEnStock.intValue()<0?quantieEnStock.abs():BigInteger.ZERO;
	}
	public void annulerRetourEnStock(BigInteger qte) {
		quantiteVendu = quantiteVendu.add(qte);
		CalculeQteEnStock();
	}

	public BigDecimal convertoCfa(BigDecimal valueToConvert) {
		BigDecimal taux = approvisionement.getDevise().getEquivalenceCfa();
		BigInteger amount = BigDecimal.valueOf(valueToConvert.multiply(taux).longValue()).toBigInteger();
		// return new BigDecimal(ProcessHelper.roundMoney(amount));
		return new BigDecimal(amount );
	}

	public void calculRemise() {
		PharmaUser pharmaUser=null;
		try {
			pharmaUser = SecurityUtil.getPharmaUser();
			System.out.println("Utilisateur: "+pharmaUser.getUserName());
		} catch (Exception e) {
			System.out.println("Utilisateur introuvable...");
			System.out.println("Cause: "+e.getCause()+"\n"+"Message: "+e.getMessage());
		}
		remiseMax = BigDecimal.ZERO;
		if(pharmaUser != null){
			BigDecimal tauxRemise = pharmaUser.getTauxRemise();
			BigDecimal tauxRemiseMax = tauxRemise ==null? BigDecimal.ZERO :tauxRemise ; //produit.getTauxRemiseMax();
			remiseMax = prixVenteUnitaire.multiply(tauxRemiseMax).divide(BigDecimal.valueOf(100));
			remiseMax =  BigDecimal.valueOf(remiseMax.longValue());
		}
		
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CipM: ").append(getCipMaison()).append(", ");
		sb.append("Des: ").append(getDesignation()).append(", ");
		sb.append("PAU: ").append(getPrixAchatUnitaire()).append(", ");
		sb.append("QteAp: ").append(getQuantiteAprovisione()).append(", ");
		sb.append("PAT: ").append(getPrixAchatTotal()).append(", ");
		sb.append("PVU: ").append(getPrixVenteUnitaire()).append("\n");
		return sb.toString();
	}

	public static TypedQuery<LigneApprovisionement> findLigneApprovisionementsByQuantieEnStockUpThanAndDesignationLike(BigInteger quantieEnStock, String designation, Etat etat) {
		if (quantieEnStock == null) throw new IllegalArgumentException("The quantieEnStock argument is required");
		if (designation == null || designation.length() == 0) throw new IllegalArgumentException("The designation argument is required");
		designation = designation + "%";
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.quantieEnStock >= :quantieEnStock AND o.approvisionement.etat = :etat AND LOWER(o.produit.designation) LIKE LOWER(:designation) order by o.designation ASC ", LigneApprovisionement.class);
		q.setParameter("quantieEnStock", quantieEnStock);
		q.setParameter("designation", designation);
		q.setParameter("etat", etat);
		return q;
	}

	public static TypedQuery<LigneApprovisionement> findLigneApprovisionementsByQuantieEnStockUpThanAndCipEquals(BigInteger quantieEnStock, String cip) {
		if (quantieEnStock == null) throw new IllegalArgumentException("The quantieEnStock argument is required");
		if (cip == null || cip.length() == 0) throw new IllegalArgumentException("The cip argument is required");
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.quantieEnStock >= :quantieEnStock AND o.cip = :cip order by o.id ASC", LigneApprovisionement.class);
		q.setParameter("quantieEnStock", quantieEnStock);
		q.setParameter("cip", cip);
		return q;
	}

	public static TypedQuery<LigneApprovisionement> findLigneApprovisionementsByQuantieEnStockNotEqualsAndCipEquals(BigInteger quantieEnStock, String cip) {
		if (quantieEnStock == null) throw new IllegalArgumentException("The quantieEnStock argument is required");
		if (cip == null || cip.length() == 0) throw new IllegalArgumentException("The cip argument is required");
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.quantieEnStock <> :quantieEnStock AND o.cip = :cip order by o.id ASC", LigneApprovisionement.class);
		q.setParameter("quantieEnStock", quantieEnStock);
		q.setParameter("cip", cip);
		return q;
	}
	public static TypedQuery<LigneApprovisionement> findNextProduits(Long id ) {
        if (id == null ) throw new IllegalArgumentException("The id argument is required");
        EntityManager em = LigneApprovisionement.entityManager();
        TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.id > :id ORDER BY o.id ", LigneApprovisionement.class);
        q.setParameter("id", id);
        return q;
    }
 public static TypedQuery<LigneApprovisionement> findPreviousProduits(Long id ) {
        if (id == null ) throw new IllegalArgumentException("The id argument is required");
        EntityManager em = LigneApprovisionement.entityManager();
        TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.id < :id ORDER BY o.id DESC", LigneApprovisionement.class);
        q.setParameter("id", id);
        return q;
    }
	public static TypedQuery<LigneApprovisionement> findLigneApprovisionementsByQuantieEnStockLessThanAndCipEquals(BigInteger quantieEnStock, String cip) {
		if (quantieEnStock == null) throw new IllegalArgumentException("The quantieEnStock argument is required");
		if (cip == null || cip.length() == 0) throw new IllegalArgumentException("The cip argument is required");
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.quantieEnStock < :quantieEnStock AND o.cip = :cip order by o.id DESC", LigneApprovisionement.class);
		q.setParameter("quantieEnStock", quantieEnStock);
		q.setParameter("cip", cip);
		return q;
	}

	public static TypedQuery<LigneApprovisionement> findLigneApprovisionementsByQteVendueUpThanAndCipEquals(BigInteger quantiteVendu, String cip) {
		if (quantiteVendu == null) throw new IllegalArgumentException("The quantiteVendu argument is required");
		if (cip == null || cip.length() == 0) throw new IllegalArgumentException("The cip argument is required");
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.quantiteVendu >= :quantiteVendu AND o.cip = :cip order by o.id ASC", LigneApprovisionement.class);
		q.setParameter("quantiteVendu", quantiteVendu);
		q.setParameter("cip", cip);
		return q;
	}
	public static List<LigneApprovisionement> findLigneApprovisionementEntries(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.approvisionement.etat = :etat ORDER BY o.id DESC", LigneApprovisionement.class).setParameter("etat", Etat.CLOS).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}

	public static List<LigneApprovisionement> findLigneApprovisionementsByProduitEntries(Produit produit, int firstResult, int maxResults) {
		if (produit == null) throw new IllegalArgumentException("The produit argument is required");
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.produit = :produit ORDER BY o.id DESC", LigneApprovisionement.class).setFirstResult(firstResult).setMaxResults(maxResults);
		q.setParameter("produit", produit);
		return q.getResultList();
	}

	public static TypedQuery<LigneApprovisionement> findLigneApprovisionementsByDesignationLike(String designation) {
		if (designation == null || designation.length() == 0) throw new IllegalArgumentException("The designation argument is required");
		designation = designation + "%";
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE LOWER(o.produit.designation) LIKE LOWER(:designation) ORDER BY o.designation ASC", LigneApprovisionement.class);
		q.setParameter("designation", designation);
		return q;
	}

	public static TypedQuery<LigneApprovisionement> findLigneApprovisionementsByQteStockAndProduit(Produit produit, BigInteger quantieEnStock) {
		if (produit == null) throw new IllegalArgumentException("The produit argument is required");
		if (quantieEnStock == null) throw new IllegalArgumentException("The quantieEnStock argument is required");
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.quantieEnStock >= :quantieEnStock AND o.produit = :produit order by o.id ASC", LigneApprovisionement.class);
		q.setParameter("quantieEnStock", quantieEnStock);
		q.setParameter("produit", produit);
		return q;
	}

	public static TypedQuery<LigneApprovisionement> findOldLigneApprovisionementsByQteStockAndProduit(Produit produit, BigInteger quantieEnStock) {
		if (produit == null) throw new IllegalArgumentException("The produit argument is required");
		if (quantieEnStock == null) throw new IllegalArgumentException("The quantieEnStock argument is required");
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.quantieEnStock >= :quantieEnStock AND o.produit = :produit order by o.id ASC", LigneApprovisionement.class);
		q.setParameter("quantieEnStock", quantieEnStock);
		q.setParameter("produit", produit);
		return q;
	}

	public static TypedQuery<LigneApprovisionement> findLigneApprovisionementsByQteStockOrQuantiteSortieAndProduit(Produit produit, BigInteger quantiteSortie, BigInteger quantiteVendu) {
		if (produit == null) throw new IllegalArgumentException("The produit argument is required");
		if (quantiteSortie == null) throw new IllegalArgumentException("The quantiteSortie argument is required");
		if (quantiteVendu == null) throw new IllegalArgumentException("The quantiteVendu argument is required");
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE  o.produit = :produit AND ( o.quantiteSortie >= :quantiteSortie OR o.quantiteVendu >= :quantiteVendu  ) order by o.quantiteSortie DESC ", LigneApprovisionement.class);
		q.setParameter("quantiteVendu", quantiteVendu);
		q.setParameter("quantiteSortie", quantiteSortie);
		q.setParameter("produit", produit);
		return q;
	}

	public static TypedQuery<LigneApprovisionement> findLigneApprovisionementsByApprovisionement(Approvisionement approvisionement) {
		if (approvisionement == null) throw new IllegalArgumentException("The approvisionement argument is required");
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.approvisionement = :approvisionement ORDER BY o.id DESC", LigneApprovisionement.class);
		q.setParameter("approvisionement", approvisionement);
		return q;
	}
	
	public static TypedQuery<LigneApprovisionement> findLigneApprovisionementsByApprovisionementOrdered(Approvisionement approvisionement) {
		if (approvisionement == null) throw new IllegalArgumentException("The approvisionement argument is required");
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.approvisionement = :approvisionement ORDER BY o.designation ASC", LigneApprovisionement.class);
		q.setParameter("approvisionement", approvisionement);
		return q;
	}

	public static List<Long> findLigneApprovisionementsByFourAndCip(String cip, Fournisseur fournisseur) {
		fournisseur = fournisseur == null ? Fournisseur.findFournisseur(new Long(1)) : fournisseur;
		cip = StringUtils.isBlank(cip) ? " " : cip;
		EntityManager em = LigneApprovisionement.entityManager();
		Query q = em.createQuery("SELECT MAX(o.id)  FROM LigneApprovisionement AS o WHERE o.produit.cip = :cip AND  o.approvisionement.founisseur = :founisseur ");
		q.setParameter("cip", cip);
		q.setParameter("founisseur", fournisseur);
		return (List<Long>) q.getResultList();
	}
	public static TypedQuery<LigneApprovisionement>  findLastLigneApprovisionementsByCip(String cip) {
		EntityManager em = LigneApprovisionement.entityManager();
		TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o  FROM LigneApprovisionement AS o WHERE o.produit.cip = :cip ORDER BY o.id DESC" ,LigneApprovisionement.class);
		q.setParameter("cip", cip);
		return q;
	}

	public static BigInteger findTrueStocK(String cip) {
		if(StringUtils.isBlank(cip)) throw new IllegalArgumentException("cip must not be null or empty");
		EntityManager em = LigneApprovisionement.entityManager();
		Query q = em.createQuery("SELECT SUM(o.quantieEnStock)  FROM LigneApprovisionement AS o WHERE o.quantieEnStock  != :quantieEnStock  AND o.cip = :cip ");
		q.setParameter("quantieEnStock", BigInteger.ZERO);
		q.setParameter("cip",cip);
		List<BigInteger> trueStockList = q.getResultList();
		if(!trueStockList.isEmpty()){
			return (BigInteger) trueStockList.iterator().next();
		}
		return null ;
	}

	public static List<LigneApprovisionement> findAllLigneApprovisionements() {
		return entityManager().createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.approvisionement.etat = :etat ORDER BY o.designation ASC", LigneApprovisionement.class).setParameter("etat", Etat.CLOS).getResultList();
	}

	public static List<BigDecimal> findlastPrices(Produit produit) {
		EntityManager em = LigneApprovisionement.entityManager();
		Query q = em.createQuery("SELECT o.prixAchatUnitaire ,o.prixVenteUnitaire  FROM LigneApprovisionement AS o WHERE   o.id IN (select p.id from LigneApprovisionement as p where p.produit = :produit  ) ") ;
		q.setParameter("produit",produit);
		List<Object[]> resultList = q.getResultList();
		ArrayList<BigDecimal> arrayList = new ArrayList<BigDecimal>();
		if(!resultList.isEmpty())
		{
			arrayList.add((BigDecimal)((Object[])resultList.get(0))[0]);
			arrayList.add((BigDecimal)((Object[])resultList.get(0))[1]);
		}
		return arrayList;
	}

	public static List<BigDecimal> findlastPrices(Produit produit,Fournisseur fournisseur) {
		EntityManager em = LigneApprovisionement.entityManager();
		Query q = em.createQuery("SELECT o.prixAchatUnitaire ,o.prixVenteUnitaire  FROM LigneApprovisionement AS o WHERE   o.id IN (select p.id from LigneApprovisionement as p where p.produit = :produit  and p.approvisionement.founisseur = :founisseur ) ") ;
		q.setParameter("produit",produit);
		q.setParameter("founisseur",fournisseur);
		List<Object[]> resultList = q.getResultList();
		ArrayList<BigDecimal> arrayList = new ArrayList<BigDecimal>();
		if(!resultList.isEmpty())
		{
			arrayList.add((BigDecimal)((Object[])resultList.get(0))[0]);
			arrayList.add((BigDecimal)((Object[])resultList.get(0))[1]);
		}
		return arrayList;
	}

	public static long countLigneApprovisionements() {
		return entityManager().createQuery("SELECT COUNT(o) FROM LigneApprovisionement AS o ", Long.class).getSingleResult();
	}

	public static TypedQuery<LigneApprovisionement> search(FamilleProduit familleProduit,SousFamilleProduit sousFamilleProduit,String designation, String cipMaison, Rayon rayon, String beginBy, String endBy, Filiale filiale, Date minDate, Date maxDate, BigInteger qteStock, Etat etat) {
		StringBuilder searchQuery = new StringBuilder("SELECT o FROM LigneApprovisionement AS o WHERE o.dateSaisie BETWEEN :minDateSaisie AND :maxDateSaisie ");
		minDate = minDate != null ? minDate : PharmaDateUtil.parse("10-10-2010 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
		maxDate = maxDate != null ? maxDate : PharmaDateUtil.parse("10-10-2090 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
		etat = etat == null ? Etat.CLOS : etat ;
		if (StringUtils.isNotBlank(cipMaison)) {
			return entityManager().createQuery("SELECT o FROM LigneApprovisionement AS o WHERE  o.cipMaison = :cipMaison ", LigneApprovisionement.class).setParameter("cipMaison", cipMaison);
		}
		if (StringUtils.isNotBlank(designation)) {
			designation = designation + "%";
			searchQuery.append(" AND  LOWER(o.produit.designation) LIKE LOWER(:designation) ");
		} else {
			beginBy = StringUtils.isBlank(beginBy) ? "A" : beginBy;
			endBy = StringUtils.isBlank(endBy) ? "ZZ" : endBy;
			searchQuery.append(" AND LOWER(o.produit.designation) BETWEEN LOWER(:beginBy) AND LOWER(:endBy) ");
		}
		if (rayon != null) {
			searchQuery.append(" AND o.produit.rayon = :rayon ");
		}
		if (familleProduit != null) {
			searchQuery.append(" AND o.produit.familleProduit = :familleProduit ");
		}
		if (sousFamilleProduit != null) {
			searchQuery.append(" AND o.produit.sousfamilleProduit = :sousfamilleProduit ");
		}
		if (qteStock != null) {
			searchQuery.append(" AND o.quantieEnStock  >= :quantieEnStock ");
		}
		if (etat != null) {
			searchQuery.append(" AND  o.approvisionement.etat = :etat ");
		}
		if (filiale != null) {
			searchQuery.append(" AND o.produit.filiale = :filiale ");
		}
		TypedQuery<LigneApprovisionement> q = entityManager().createQuery(searchQuery.toString(), LigneApprovisionement.class);
		if (StringUtils.isNotBlank(designation)) {
			q.setParameter("designation", designation);
		} else {
			q.setParameter("beginBy", beginBy);
			q.setParameter("endBy", endBy);
		}
		if (rayon != null) {
			q.setParameter("rayon", rayon);
		}
		if (filiale != null) {
			q.setParameter("filiale", filiale);
		}
		if (familleProduit != null) {
			q.setParameter("familleProduit", familleProduit);
		}
		if (sousFamilleProduit != null) {
			q.setParameter("sousfamilleProduit", sousFamilleProduit);

		}
		if (qteStock != null) {
			q.setParameter("quantieEnStock", qteStock);
		}
		if (etat != null) {
			q.setParameter("etat", etat);
		}
		q.setParameter("minDateSaisie", minDate);
		q.setParameter("maxDateSaisie", maxDate);
		return q;
	}

	public static TypedQuery<LigneApprovisionement> searchAJAX(String designation, BigInteger qteStock, Etat etat) {
		StringBuilder searchQuery = new StringBuilder("SELECT o FROM LigneApprovisionement AS o WHERE o.approvisionement.etat = :etat ");
		etat = etat != null ? etat : Etat.CLOS;
		if (StringUtils.isNotBlank(designation)) {
			designation = designation + "%";
			searchQuery.append(" AND  LOWER(o.produit.designation) LIKE LOWER(:designation) ");
		}
		if (qteStock != null) {
			searchQuery.append(" AND o.quantieEnStock  >= :quantieEnStock ");
		}
		TypedQuery<LigneApprovisionement> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.cip , o.id").toString(), LigneApprovisionement.class);
		if (StringUtils.isNotBlank(designation)) {
			q.setParameter("designation", designation);
		}
		if (qteStock != null) {
			q.setParameter("quantieEnStock", qteStock);
		}
		q.setParameter("etat", etat);
		return q;
	}

	public static TypedQuery<LigneApprovisionement> fatalSearchAJAX(String designation) {
		StringBuilder searchQuery = new StringBuilder("SELECT o FROM LigneApprovisionement AS o WHERE o.approvisionement.etat = :etat ");
		if (StringUtils.isNotBlank(designation)) {
			designation = designation + "%";
			searchQuery.append(" AND  LOWER(o.produit.designation) LIKE LOWER(:designation) ");
		}
		TypedQuery<LigneApprovisionement> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.cip , o.id ").toString(), LigneApprovisionement.class);
		if (StringUtils.isNotBlank(designation)) {
			q.setParameter("designation", designation);
		}
		q.setParameter("etat", Etat.CLOS);
		return q;
	}
	
	
	 public static TypedQuery<LigneApprovisionement> findLigneApprovisionementsByCipMaisonEqualsAndReclamations(String cipMaison) {
	        if (cipMaison == null || cipMaison.length() == 0) throw new IllegalArgumentException("The cipMaison argument is required");
	        EntityManager em = LigneApprovisionement.entityManager();
	        TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.cipMaison = :cipMaison AND o.quantiteReclame > :qteReclame", LigneApprovisionement.class);
	        q.setParameter("cipMaison", cipMaison);
	        q.setParameter("qteReclame", BigInteger.ZERO);
	        return q;
	    }
	 
	 
	 public static TypedQuery<LigneApprovisionement> findLigneApprovisionementsByCipMaisonEquals(String cipMaison) {
	        if (cipMaison == null || cipMaison.length() == 0) throw new IllegalArgumentException("The cipMaison argument is required");
	        EntityManager em = LigneApprovisionement.entityManager();
	        TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.cipMaison = :cipMaison", LigneApprovisionement.class);
	        q.setParameter("cipMaison", cipMaison);
	        return q;
	    }
	
	
	
}