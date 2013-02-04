package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.util.Calendar;
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
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.adorsys.adpharma.utils.UseItemsInterfaces;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.ui.Model;
import org.adorsys.adpharma.domain.Filiale;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Approvisionement", finders = { "findApprovisionementsByDateCreationBetween", "findApprovisionementsByFounisseurAndDateCreationBetween", "findApprovisionementsByReclamationsNot" })
public class Approvisionement extends AdPharmaBaseEntity implements UseItemsInterfaces {

    private String approvisionementNumber;

    @NotNull
    private String bordereauNumber = "";

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Calendar dateBordereau = Calendar.getInstance();

    @ManyToOne
    private PharmaUser agentCreateur;

    @ManyToOne
    private CommandeFournisseur commande = null;

    public CommandeFournisseur getCommande() {
        return commande;
    }

    public void setCommande(CommandeFournisseur commande) {
        this.commande = commande;
    }

    @ManyToOne
    private Site magasin;

    @ManyToOne
    private Devise devise;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateCommande = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateLivraison = new Date();

    @NotNull
    @ManyToOne
    private Fournisseur founisseur;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateReglement;

    @NotNull
    private BigDecimal montantHt = BigDecimal.ZERO;

    private BigDecimal montantTtc = BigDecimal.ZERO;

    private BigDecimal montantRemise = BigDecimal.ZERO;

    private BigDecimal montantNap = BigDecimal.ZERO;

    private transient BigDecimal montant = BigDecimal.ZERO;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateCreation = new Date();

    /**
     * Used to verified if an Approvisionement contains a reclamations of products or not
     */
    private Boolean reclamations = Boolean.FALSE;

    public Approvisionement() {
    }

    public Approvisionement(Fournisseur fournisseur, String bordereau, Devise devise, String filiale, Site magasin) {
        this.founisseur = fournisseur;
        this.bordereauNumber = bordereau;
        this.devise = devise;
        this.filiale = filiale;
        this.magasin = magasin;
        agentCreateur = SecurityUtil.getPharmaUser();
    }

    @Value("false")
    private Boolean urgence;

    @Value("false")
    private Boolean cloturer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "approvisionement")
    private Set<LigneApprovisionement> ligneApprivisionement = new HashSet<LigneApprovisionement>();

    @Enumerated
    private Etat etat = Etat.EN_COUR;

    private String filiale;

    @ManyToOne
    private transient Filiale filiales;

    public Filiale getFiliales() {
        return filiales;
    }

    public void setFiliales(Filiale filiales) {
        this.filiales = filiales;
    }

    @Override
    protected void internalPrePersist() {
        agentCreateur = SecurityUtil.getPharmaUser();
        dateCreation = new Date();
    }

    @PostPersist
    public void postPersit() {
        approvisionementNumber = NumberGenerator.getNumber("AP-", getId(), 4);
    }

    public Approvisionement(CommandeFournisseur commandeFournisseur) {
        this.agentCreateur = SecurityUtil.getPharmaUser();
        this.bordereauNumber = RandomStringUtils.randomAlphanumeric(6);
        this.commande = commandeFournisseur;
        this.dateReglement = new Date();
        this.devise = Devise.findDevise(new Long(1));
        this.etat = Etat.EN_COUR;
        this.filiale = Filiale.findFiliale(new Long(1)).getFilialeNumber();
        this.founisseur = commandeFournisseur.getFournisseur();
        this.magasin = commandeFournisseur.getSite();
    }

    public void protectSomeField() {
        Approvisionement approvisionement = Approvisionement.findApprovisionement(getId());
        approvisionementNumber = approvisionement.getApprovisionementNumber();
        agentCreateur = approvisionement.getAgentCreateur();
        dateCreation = approvisionement.getDateCreation();
        urgence = approvisionement.getUrgence();
        cloturer = approvisionement.getCloturer();
        magasin = approvisionement.getMagasin();
        devise = approvisionement.getDevise();
        etat = approvisionement.getEtat();
        footPrint = approvisionement.getFootPrint();
    }

    public LigneApprovisionement getLineContaintProduct(Produit produit) {
        if (ligneApprivisionement.isEmpty()) {
            return null;
        } else {
            for (LigneApprovisionement ligne : ligneApprivisionement) {
                if (ligne.getProduit().equals(produit)) {
                    return ligne;
                }
            }
        }
        return null;
    }

    public boolean contientProduit(Produit produit) {
        if (ligneApprivisionement.isEmpty()) {
            return false;
        } else {
            for (LigneApprovisionement ligne : ligneApprivisionement) {
                if (ligne.getProduit().equals(produit)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void increaseMontant(BigDecimal montant) {
        montantNap = montantNap.add(montant);
    }

    public void decreaseMontant(BigDecimal montant) {
        montantNap = montantNap.subtract(montant);
    }

    public void calculateMontant() {
        montant = BigDecimal.ZERO;
        List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByApprovisionement(this).getResultList();
        if (!resultList.isEmpty()) {
            for (LigneApprovisionement ligne : resultList) {
                if (ligne.getPrixAchatTotal() != null) montant = montant.add(ligne.getPrixAchatTotal());
            }
        }
    }

    public boolean CommandeContientProduit(Produit produit) {
        if (commande != null) {
            return !contientProduit(produit);
        } else {
            return false;
        }
    }

    public String displayName() {
        return new StringBuilder().append(getApprovisionementNumber()).append(" , ").append("Bordereau No: " + getBordereauNumber()).toString();
    }

    public int getNewIndex() {
        return ligneApprivisionement.size() + 1;
    }

    public void close() {
        if (getCommande() != null) {
            getCommande().setValider(true);
            getCommande().setLivre(true);
            getCommande().setEtatCmd(Etat.CLOS);
            getCommande().setApprovisionnementId(getId());
            getCommande().merge();
        }
        this.setCloturer(true);
        this.setEtat(Etat.CLOS);
    }

    public void deleteAllLine() {
        for (LigneApprovisionement ligne : ligneApprivisionement) {
            ligne.remove();
        }
    }

    public String toString() {
        calculateMontant();
        StringBuilder sb = new StringBuilder();
        sb.append(getApprovisionementNumber()).append(" Du ");
        sb.append(PharmaDateUtil.format(getDateCreation(), "dd-MM-yyyy HH:mm"));
        sb.append(" , Bordereau No : ").append(getBordereauNumber());
        sb.append(" , MontantHT : ").append(getMontantHt());
        sb.append(" " + Devise.findDevise(new Long(1)).getLibelleCourt());
        sb.append(" , MontantSaisie : ").append(getMontant());
        sb.append(" " + Devise.findDevise(new Long(1)).getLibelleCourt());
        if (commande != null) sb.append(" | " + commande);
        return sb.toString();
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public static List<Approvisionement> findApprovisionementEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Approvisionement o ORDER BY o.id DESC ", Approvisionement.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<Approvisionement> findApprovisionementByCommandeFournisseur(CommandeFournisseur commande) {
        if (commande == null) throw new IllegalArgumentException("commande argument is required ");
        return entityManager().createQuery("SELECT o FROM Approvisionement o WHERE o.commande = :commande ORDER BY o.id DESC ", Approvisionement.class).setParameter("commande", commande).getResultList();
    }

    public static List<Approvisionement> search(String apNumber, Etat etat, Date minDate, Date maxDate, Fournisseur fournisseur, PharmaUser user) {
        StringBuilder searchQuery = new StringBuilder("SELECT o FROM Approvisionement AS o WHERE o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation ");
        minDate = minDate != null ? minDate : PharmaDateUtil.parse("10-10-2010", PharmaDateUtil.DATE_PATTERN_LONG);
        maxDate = maxDate != null ? maxDate : PharmaDateUtil.parse("10-10-2050", PharmaDateUtil.DATE_PATTERN_LONG);
        if (StringUtils.isNotBlank(apNumber)) {
            apNumber = "AP-" + StringUtils.removeStart(apNumber, "AP-");
            return entityManager().createQuery("SELECT o FROM Approvisionement AS o WHERE  o.approvisionementNumber = :approvisionementNumber ", Approvisionement.class).setParameter("approvisionementNumber", apNumber).getResultList();
        } else {
            if (!etat.equals(Etat.ALL)) searchQuery.append(" AND o.etat = :etat ");
            if (user != null) searchQuery.append(" AND o.agentCreateur = :user ");
            if (fournisseur != null) searchQuery.append(" AND o.founisseur = :founisseur ");
            TypedQuery<Approvisionement> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.id DESC").toString(), Approvisionement.class);
            if (!etat.equals(Etat.ALL)) q.setParameter("etat", etat);
            if (fournisseur != null) q.setParameter("founisseur", fournisseur);
            if (user != null) q.setParameter("user", user);
            q.setParameter("minDateCreation", minDate);
            q.setParameter("maxDateCreation", maxDate);
            return q.getResultList();
        }
    }
    
    // Find approvisionement for reclamations
    public static TypedQuery<Approvisionement> findApprovisionementsByFounisseurAndDateCreationBetweenAndReclamationsNot(Fournisseur founisseur, Date minDateCreation, Date maxDateCreation, Boolean reclamation) {
        if (founisseur == null) throw new IllegalArgumentException("The founisseur argument is required");
        if (minDateCreation == null) throw new IllegalArgumentException("The minDateCreation argument is required");
        if (maxDateCreation == null) throw new IllegalArgumentException("The maxDateCreation argument is required");
        if(reclamation == null) throw new IllegalArgumentException("The reclamation argument is required");
        
        StringBuilder query= new StringBuilder();
        query.append("SELECT o FROM Approvisionement AS o WHERE o.founisseur = :founisseur");
        query.append(" AND o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation");
        query.append(" AND o.reclamations IS NOT :reclamations");
        query.append(" ORDER BY o.dateCreation ASC");
        
        EntityManager em = Approvisionement.entityManager();
        TypedQuery<Approvisionement> q = em.createQuery(query.toString(), Approvisionement.class);
        q.setParameter("founisseur", founisseur);
        q.setParameter("minDateCreation", minDateCreation);
        q.setParameter("maxDateCreation", maxDateCreation);
        q.setParameter("reclamations", reclamation);
        return q;
    }

    public boolean isValide(Model uiModel) {
        boolean valider = true;
        calculateMontant();
        PharmaUser pharmaUser = SecurityUtil.getPharmaUser();
        Set<LigneApprovisionement> lines = Approvisionement.findApprovisionement(getId()).getLigneApprivisionement();
        if (lines.isEmpty()) {
            uiModel.addAttribute("appMessage", "Impossible de cloturer ! L'approvisionnement ne contient Aucun Produits .");
            return false;
        }
        if (getEtat().equals(Etat.CLOS)) {
            uiModel.addAttribute("appMessage", "APROVISIONNEMENT CLOS  ");
            return false;
        }
        if (pharmaUser.hasAnyRole(RoleName.ROLE_SITE_MANAGER)) {
            return true;
        }
        if (getMontant().intValue() != getMontantHt().intValue()) {
            uiModel.addAttribute("appMessage", "Impossible de cloturer !  LE MontantHt est different du montant Saisie  ");
            return false;
        }
        return valider;
    }
    
    
    

    @Override
    public boolean hasItems() {
        if (ligneApprivisionement == null) return false;
        return !ligneApprivisionement.isEmpty();
    }
}
