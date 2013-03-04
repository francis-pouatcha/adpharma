package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.adorsys.adpharma.platform.rest.exchanges.ExchangeBeanState;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.OrderBy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "CommandeFournisseur", finders = { "findCommandeFournisseursByDateCreationBetweenAndEtatCmd", "findCommandeFournisseursByFournisseurAndDateCreationBetween", "findCommandeFournisseursByCmdNumberEquals", "findCommandeFournisseursByDateLimiteLivraisonBetween", "findCommandeFournisseursByLivreNot" })
public class CommandeFournisseur extends AdPharmaBaseEntity {

	private String cmdNumber;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm:ss")
	private Date submitionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm:ss")
	private Date traitmentDate;

	private Long approvisionnementId ;
	
	private String commercialKey;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
	private Date dateCreation;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm", iso=DateTimeFormat.ISO.TIME)
	private Date dateLimiteLivraison;
	
	private String lastestUbipharmError;
	
	private CommandType commandType ;
	
	@Enumerated
	private ExchangeBeanState exchangeBeanState = ExchangeBeanState.DRUGSTORE_PROCESSING;

	@NotNull
	@ManyToOne
	private Fournisseur fournisseur;

	public Boolean getValider() {
		return livre;
	}

	public void setValider(Boolean livre) {
		this.livre = livre;
	}

	@Value("false")
	private Boolean livre;

	@OrderBy(clause = "dateSaisie DESC")
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "commande",fetch=FetchType.LAZY)
	private Set<LigneCmdFournisseur> ligneCommande = new HashSet<LigneCmdFournisseur>();

	@Enumerated
	private ModeSelection ModeDeSelection;

	@ManyToOne
	private Site site;

	@ManyToOne
	private PharmaUser creePar;

	@Enumerated
	private Etat etatCmd = Etat.EN_COUR;

	@Value("false")
	private Boolean annuler;

	private BigDecimal montantHt = BigDecimal.ZERO;

	private BigDecimal montantTva= BigDecimal.ZERO;

	private BigDecimal montantTtc= BigDecimal.ZERO;

	@ManyToOne
	private TVA tva;

	@Override
	protected void internalPrePersist() {
		dateCreation = new Date();
		creePar = SecurityUtil.getPharmaUser();
	}

	@PostPersist
	public void postPersit() {
		cmdNumber = NumberGenerator.getNumber("CF-", getId(), 4);

	}

	@PostLoad
	public void postLoad() {

	}

	public Date getSubmitionDate() {
		return submitionDate;
	}

	public void setSubmitionDate(Date submitionDate) {
		this.submitionDate = submitionDate;
	}

	public Date getTraitmentDate() {
		return traitmentDate;
	}

	public void setTraitmentDate(Date traitmentDate) {
		this.traitmentDate = traitmentDate;
	}



	public ExchangeBeanState getExchangeBeanState() {
		return exchangeBeanState;
	}

	public void setExchangeBeanState(ExchangeBeanState exchangeBeanState) {
		this.exchangeBeanState = exchangeBeanState;
	}
	
	

	public Long getApprovisionnementId() {
		return approvisionnementId;
	}

	public void setApprovisionnementId(Long approvisionnementId) {
		this.approvisionnementId = approvisionnementId;
	}

	public void protectSomeField() {
		CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(getId());
		cmdNumber = commandeFournisseur.getCmdNumber();
		setDateCreation(commandeFournisseur.getDateCreation());
		setEtatCmd(commandeFournisseur.getEtatCmd());
		setAnnuler(commandeFournisseur.getAnnuler());
		footPrint = commandeFournisseur.getFootPrint();
		footPrint.setModifyingUser(SecurityUtil.getUserName());
		montantHt = calculerMontanthHt();
		montantTva = calculerMontantTva();
		montantTtc = montantHt.add(montantTva);
		livre = commandeFournisseur.getLivre();
		tva = commandeFournisseur.getTva();
		site = commandeFournisseur.getSite();
		creePar = commandeFournisseur.getCreePar();
		etatCmd =commandeFournisseur.getEtatCmd();
		ModeDeSelection = commandeFournisseur.getModeDeSelection();
	}



	public String getCommercialKey() {
		return commercialKey;
	}

	public void setCommercialKey(String commercialKey) {
		this.commercialKey = commercialKey;
	}

	public LigneCmdFournisseur getProduct(Produit produit) {
		if (ligneCommande.isEmpty()) {
			return null;
		} else {
			for (LigneCmdFournisseur ligneCmdFournisseur : ligneCommande) {
				if (ligneCmdFournisseur.getProduit().equals(produit)) {
					return ligneCmdFournisseur;
				}
			}
		}
		return null;
	}

	public boolean contientProduit(Produit produit) {
		if (ligneCommande.isEmpty()) {
			return false;
		} else {
			for (LigneCmdFournisseur ligneCmdFournisseur : ligneCommande) {
				if (ligneCmdFournisseur.getProduit().equals(produit)) {
					return true;
				}
			}
		}
		return false;
	}

	public String displayName() {
		return new StringBuilder().append(getCmdNumber()).append(" DU ").append(PharmaDateUtil.format(getDateCreation(), "dd-MM-yyyy HH:mm")).toString();
	}

	public int getNewIndex() {
		return ligneCommande.size() + 1;
	}

	public void convertToLineAprov(Approvisionement approvisionement) {
		approvisionement.deleteAllLine();
		if (!ligneCommande.isEmpty()) {
			for (LigneCmdFournisseur line : ligneCommande) {
				LigneApprovisionement ligneApprovisionement = new LigneApprovisionement();
				ligneApprovisionement.setApprovisionement(approvisionement);
				ligneApprovisionement.setCip(line.getCip());
				ligneApprovisionement.setDesignation(line.getProduit().getDesignation());
				ligneApprovisionement.setIndexLine(getNewIndex());
				ligneApprovisionement.setPrixAchatUnitaire(line.getPrixAchatMin());
				ligneApprovisionement.setPrixVenteUnitaire(line.getPrixAVenteMin());
				ligneApprovisionement.setQuantiteAprovisione(line.getQuantiteCommande());
				ligneApprovisionement.setProduit(line.getProduit());
				ligneApprovisionement.setPrixAchatTotal(line.getPrixAchatTotal());
				ligneApprovisionement.setAgentSaisie(SecurityUtil.getUserName());
				ligneApprovisionement.CalculeQteEnStock();
				ligneApprovisionement.persist();
				approvisionement.getLigneApprivisionement().add(ligneApprovisionement);
			}
		}
		approvisionement.setFounisseur(this.getFournisseur());
		approvisionement.setCommande(this);
		
	}
	
	public void increaseMontant(BigDecimal montant) {
		montantHt = montantHt ==null ?BigDecimal.ZERO:montantHt ;
		montantHt = montantHt.add(montant);
		montantTtc = montantHt ;
    }

    public void decreaseMontant(BigDecimal montant) {
    	montantHt = montantHt ==null ?BigDecimal.ZERO:montantHt ;
		montantHt = montantHt.subtract(montant) ;
		montantTtc = montantHt ;
    }

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CmdNo: ").append(getCmdNumber()).append(" Du ");
		sb.append(PharmaDateUtil.format(getDateCreation(), "dd-MM-yyyy HH:mm"));
		sb.append(" , Fournisseur: ").append(getFournisseur().displayName());
		return sb.toString();
	}

	public BigDecimal calculerMontantTva(){
		BigDecimal montant = BigDecimal.ZERO;
		if (tva!=null) {
			if (tva.getValide()) {
				montant = montantHt.multiply(tva.getTauxTva().multiply(BigDecimal.valueOf(0.01)));
				montant = BigDecimal.valueOf(montant.longValue());

			}
		}
		return montant ;
	}



	public BigDecimal calculerMontanthHt(){
		BigDecimal montant = BigDecimal.ZERO;
		if (!ligneCommande.isEmpty()) {
			for (LigneCmdFournisseur line : ligneCommande) {
				montant = montant.add(line.getPrixAchatTotal());
			}
			montant = BigDecimal.valueOf(montant.longValue());

		}
		return montant ;

	}

	public static List<CommandeFournisseur> findCommandeFournisseurEntries(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM CommandeFournisseur o ORDER BY o.id DESC ", CommandeFournisseur.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}

	public static List<CommandeFournisseur> search(String cmdNumber, Etat etatCmd, Date minDate, Date maxDate, Fournisseur fournisseur) {
		StringBuilder searchQuery = new StringBuilder("SELECT o FROM CommandeFournisseur AS o WHERE o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation ");
		minDate = minDate != null ? minDate : PharmaDateUtil.parse("10-10-2010 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
		maxDate = maxDate != null ? maxDate : PharmaDateUtil.parse("10-10-2050 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
		if (StringUtils.isNotBlank(cmdNumber)) {
			return entityManager().createQuery("SELECT o FROM CommandeFournisseur AS o WHERE  o.cmdNumber = :cmdNumber ", CommandeFournisseur.class).setParameter("cmdNumber", "CF-"+cmdNumber).getResultList();
		} else {
			if (!etatCmd.equals(Etat.ALL)) {
				searchQuery.append(" AND o.etatCmd = :etatCmd ");
			}
			if (fournisseur != null) {
				searchQuery.append(" AND o.fournisseur = :fournisseur ");
			}
			TypedQuery<CommandeFournisseur> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.id DESC").toString(), CommandeFournisseur.class);
			if (!etatCmd.equals(Etat.ALL)) {
				q.setParameter("etatCmd", etatCmd);
			}
			if (fournisseur != null) {
				q.setParameter("fournisseur", fournisseur);
			}
			q.setParameter("minDateCreation", minDate);
			q.setParameter("maxDateCreation", maxDate);
			return q.getResultList(); 
		}

	}

	public static TypedQuery<CommandeFournisseur> findCommandByCommandKeyAndDrugstoreKeyAndProviderKey(String commandNumber,String drugstoreKey,String providerKey) {
		TypedQuery<CommandeFournisseur> q = entityManager().createQuery("SELECT o FROM CommandeFournisseur o WHERE o.cmdNumber =:cmdNumber" +
				" AND o.Fournisseur.providerKey =:providerKey AND o.site.drugstoreKey =:drugstoreKey ", CommandeFournisseur.class) ;
		q.setParameter("drugstoreKey", drugstoreKey);
		q.setParameter("providerKey", providerKey);
		q.setParameter("commandNumber", commandNumber);
		return  q;
	}
	
	public static TypedQuery<CommandeFournisseur> findCmdByFournisseurLike(String designation) {
		if (designation == null || designation.length() == 0) throw new IllegalArgumentException("The designation argument is required");
		designation ="%"+designation + "%";
		EntityManager em = CommandeFournisseur.entityManager();
		TypedQuery<CommandeFournisseur> q = em.createQuery("SELECT o FROM CommandeFournisseur AS o WHERE LOWER(o.fournisseur.name) LIKE LOWER(:designation) order By  o.fournisseur.name ASC ", CommandeFournisseur.class);
		q.setParameter("designation", designation);
		
		return q;
	}

	public String getLastestUbipharmError() {
		return lastestUbipharmError;
	}

	public void setLastestUbipharmError(String lastestUbipharmError) {
		this.lastestUbipharmError = lastestUbipharmError;
	}

	public CommandType getCommandType() {
		return commandType;
	}

	public void setCommandType(CommandType commandType) {
		this.commandType = commandType;
	}
	
	
}
