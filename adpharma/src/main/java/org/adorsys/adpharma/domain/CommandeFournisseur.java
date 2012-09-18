package org.adorsys.adpharma.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import java.math.BigDecimal;
import org.adorsys.adpharma.domain.TVA;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.OrderBy;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "CommandeFournisseur", finders = { "findCommandeFournisseursByDateCreationBetweenAndEtatCmd", "findCommandeFournisseursByFournisseurAndDateCreationBetween", "findCommandeFournisseursByCmdNumberEquals", "findCommandeFournisseursByDateLimiteLivraisonBetween", "findCommandeFournisseursByLivreNot" })
public class CommandeFournisseur extends AdPharmaBaseEntity {

    private String cmdNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date dateCreation;
     
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date dateLimiteLivraison;

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
                ligneApprovisionement.setQuantiteAprovisione(line.getQuantiteCommande());
                ligneApprovisionement.setProduit(line.getProduit());
                ligneApprovisionement.setPrixAchatTotal(line.getPrixAchatTotal());
                ligneApprovisionement.setAgentSaisie(SecurityUtil.getUserName());
                ligneApprovisionement.persist();
            }
        }
        approvisionement.setFounisseur(this.getFournisseur());
        approvisionement.setCommande(this);
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
}
