package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javassist.expr.NewArray;
import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.adorsys.adpharma.domain.TypeFacture;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.loader.custom.Return;
import com.mysql.jdbc.jmx.LoadBalanceConnectionGroupManager;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Facture", finders = { "findFacturesByCommande", "findFacturesByCaisseAndEncaisserNot", "findFacturesByFactureNumberEquals", "findFacturesByDateCreationBetween", "findFacturesByVendeurAndDateCreationBetween", "findFacturesByClientAndDateCreationBetween", "findFacturesBySolderNot" })
@RooJson
public class Facture extends AdPharmaBaseEntity {

    private String factureNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date dateCreation;

    @NotNull
    @ManyToOne
    private Client client;

    @ManyToOne
    private Caisse caisse;

    @NotNull
    @ManyToOne
    private PharmaUser vendeur;

    @ManyToOne
    private Site site;

    @ManyToOne
    private CommandeClient commande;

    @Enumerated
    private TypeCommande typeCommande;

    private BigInteger montantTotal = BigInteger.ZERO;

    private BigInteger montantRemise = BigInteger.ZERO;

    private BigInteger netPayer = BigInteger.ZERO;

    private Boolean solder = Boolean.FALSE;

    private Boolean encaisser = Boolean.FALSE;

    private BigInteger avance = BigInteger.ZERO;

    private BigInteger reste = BigInteger.ZERO;

    @Transient
    private String cip;

    @Transient
    private String designation;

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "facture")
    private Set<LigneFacture> lineFacture = new HashSet<LigneFacture>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "facture")
    private Set<Paiement> paiement = new HashSet<Paiement>();

    @Enumerated
    private TypeFacture typeFacture;

    @PostPersist
    public void postPersist() {
        factureNumber = NumberGenerator.getNumber("FAC-", getId(), 4);
    }

    @Override
    protected void internalPrePersist() {
        dateCreation = new Date();
        typeCommande = commande.getTypeCommande();
    }

    public void avancerPaiement(BigInteger montant) {
        avance = avance.add(montant);
        reste = netPayer.subtract(avance);
        solder = estSolder();
    }

    public boolean estSolder() {
        return reste.intValue() == 0;
    }

    public boolean isCashPaiement() {
        boolean cash = false;
        if (getEncaisser()) {
            cash = true;
        } else {
            if (getTypeCommande().equals(TypeCommande.VENTE_AU_PUBLIC)) {
                cash = true;
            }
            if (getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT)) {
                if (getCommande().getVentePartiel()) {
                    cash = true;
                } else {
                    cash = false;
                }
            }
        }
        return cash;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Facture other = (Facture) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    public BigInteger calculerPartClient() {
        BigInteger amount = BigInteger.ZERO;
        if (netPayer != null) {
            System.out.println(netPayer);
            BigInteger taux = BigInteger.valueOf(new Long(this.getCommande().getTauxCouverture()));
            amount = netPayer.multiply(taux);
            return netPayer.subtract(amount.divide(BigInteger.valueOf(100)));
        }
        return BigInteger.ZERO;
    }

    public boolean validaterSortieProduit(Model uiModel) {
        boolean valider = false;
        if (!encaisser) {
            Set<LigneCmdClient> lineCommande = commande.getLineCommande();
            if (!lineCommande.isEmpty()) {
                for (LigneCmdClient line : lineCommande) {
                    if (line.getQuantiteCommande().intValue() > line.getProduit().getQuantieEnStock().intValue()) {
                        uiModel.addAttribute("apMessage", line.getCipM() + "! la quantite achetee: " + line.getQuantiteCommande() + " est superieur a la quantite en stock :" + line.getProduit().getQuantieEnStock());
                        valider = true;
                        break;
                    }
                }
            }
        }
        return valider;
    }

    public void addLine(LigneFacture ligneFacture) {
        ligneFacture.setFacture(this);
        getLineFacture().add(ligneFacture);
    }

    public static TypedQuery<Facture> findFacturesByCaisseAndEncaisserNot(Caisse caisse, Boolean encaisser) {
        if (encaisser == null) throw new IllegalArgumentException("The encaisser argument is required");
        TypedQuery<Facture> q;
        EntityManager em = Facture.entityManager();
        if (caisse == null) {
            q = em.createQuery("SELECT o FROM Facture AS o WHERE o.encaisser IS NOT :encaisser  AND o.solder IS :solder AND o.typeFacture != :typeFacture ORDER BY o.id ASC", Facture.class);
        } else {
            q = em.createQuery("SELECT o FROM Facture AS o WHERE o.caisse = :caisse AND o.encaisser IS NOT :encaisser  AND o.solder IS :solder AND o.typeFacture != :typeFacture ORDER BY o.id ASC", Facture.class);
        }
        if (caisse != null) {
            q.setParameter("caisse", caisse);
        }
        q.setParameter("encaisser", encaisser);
        q.setParameter("solder", Boolean.FALSE);
        q.setParameter("typeFacture", TypeFacture.PROFORMAT);
        return q;
    }

    public static TypedQuery<Facture> findFacturesByVendeurAndDateCreationBetweenNotSolder(PharmaUser vendeur, Date minDateCreation, Date maxDateCreation) {
        if (vendeur == null) throw new IllegalArgumentException("The vendeur argument is required");
        if (minDateCreation == null) throw new IllegalArgumentException("The minDateCreation argument is required");
        if (maxDateCreation == null) throw new IllegalArgumentException("The maxDateCreation argument is required");
        EntityManager em = Facture.entityManager();
        TypedQuery<Facture> q = em.createQuery("SELECT o FROM Facture AS o WHERE o.vendeur = :vendeur AND o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation AND o.solder IS :solder ORDER BY o.id DESC ", Facture.class);
        q.setParameter("vendeur", vendeur);
        q.setParameter("minDateCreation", minDateCreation);
        q.setParameter("maxDateCreation", maxDateCreation);
        q.setParameter("solder", Boolean.FALSE);
        return q;
    }

    public static TypedQuery<Facture> findFacturesFactureNumber(String factureNumber) {
        if (factureNumber == null) throw new IllegalArgumentException("The factureNumber argument is required");
        EntityManager em = Facture.entityManager();
        TypedQuery<Facture> q = em.createQuery("SELECT o FROM Facture AS o WHERE o.factureNumber = :factureNumber ", Facture.class);
        q.setParameter("factureNumber", factureNumber);
        return q;
    }

    public static TypedQuery<Facture> findFacturesByClientAndDateCreationBetweenNotsolder(Client client, Date minDateCreation, Date maxDateCreation) {
        if (client == null) throw new IllegalArgumentException("The client argument is required");
        if (minDateCreation == null) throw new IllegalArgumentException("The minDateCreation argument is required");
        if (maxDateCreation == null) throw new IllegalArgumentException("The maxDateCreation argument is required");
        EntityManager em = Facture.entityManager();
        TypedQuery<Facture> q = em.createQuery("SELECT o FROM Facture AS o WHERE o.client = :client AND o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation  AND o.solder IS :solder ORDER BY o.id DESC ", Facture.class);
        q.setParameter("client", client);
        q.setParameter("minDateCreation", minDateCreation);
        q.setParameter("maxDateCreation", maxDateCreation);
        q.setParameter("solder", Boolean.FALSE);
        return q;
    }

    public static TypedQuery<Facture> findFacturesByDateCreationBetweenNotsolder(Date minDateCreation, Date maxDateCreation) {
        if (minDateCreation == null) throw new IllegalArgumentException("The minDateCreation argument is required");
        if (maxDateCreation == null) throw new IllegalArgumentException("The maxDateCreation argument is required");
        EntityManager em = Facture.entityManager();
        TypedQuery<Facture> q = em.createQuery("SELECT o FROM Facture AS o WHERE o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation  AND o.solder IS :solder ORDER BY o.id DESC ", Facture.class);
        q.setParameter("minDateCreation", minDateCreation);
        q.setParameter("maxDateCreation", maxDateCreation);
        q.setParameter("solder", Boolean.FALSE);
        return q;
    }

    public static TypedQuery<Facture> findFacturesByFactureNumberEqualsNotSolder(String factureNumber) {
        if (factureNumber == null || factureNumber.length() == 0) throw new IllegalArgumentException("The factureNumber argument is required");
        EntityManager em = Facture.entityManager();
        TypedQuery<Facture> q = em.createQuery("SELECT o FROM Facture AS o WHERE o.factureNumber = :factureNumber AND o.solder IS :solder ORDER BY o.id DESC ", Facture.class);
        q.setParameter("factureNumber", factureNumber);
        q.setParameter("solder", Boolean.FALSE);
        return q;
    }

    public static List<Facture> search(Boolean solder, String cip, String designation, BigInteger netPayer, Date dateCreation) {
        StringBuilder searchQuery = new StringBuilder("SELECT o FROM Facture AS o WHERE o.netPayer >= :netPayer AND o.dateCreation >= :dateCreation AND o.solder IS :solder AND o.id IN (SELECT l.facture.id FROM LigneFacture AS l WHERE l.id IS NOT NULL  ");
        dateCreation = dateCreation != null ? dateCreation : PharmaDateUtil.parseToDate("16-05-2012", PharmaDateUtil.DATE_PATTERN_LONG);
        netPayer = netPayer != null ? netPayer : BigInteger.ZERO;
        solder = solder != null ? solder : Boolean.TRUE;
        if (StringUtils.isNotBlank(cip)) {
            searchQuery.append(" AND l.cip = :cip ");
        }
        if (StringUtils.isNotBlank(designation)) {
            designation = designation + "%";
            searchQuery.append(" AND  LOWER(l.designation) LIKE LOWER(:designation) ");
        }
        TypedQuery<Facture> q = entityManager().createQuery(searchQuery.append(" ORDER BY l.designation ASC ) ").toString(), Facture.class);
        if (StringUtils.isNotBlank(designation)) {
            q.setParameter("designation", designation);
        }
        if (StringUtils.isNotBlank(cip)) {
            q.setParameter("cip", cip);
        }
        q.setParameter("dateCreation", dateCreation);
        q.setParameter("netPayer", netPayer);
        q.setParameter("solder", solder);
        return q.getResultList();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFactureNumber()).append(" du ");
        sb.append(PharmaDateUtil.format(getDateCreation(), "dd-MM-yyyy hh:mm"));
        return sb.toString();
    }

    public static List<Facture> findFactureEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Facture o ORDER BY o.id DESC ", Facture.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
}
