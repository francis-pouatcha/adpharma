package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Caisse", finders = { "findCaissesByCaisseOuverteNot", "findCaissesByCaisseOuverteNotAndCaissier", "findCaissesByDateOuvertureBetween" })
public class Caisse extends AdPharmaBaseEntity {

    private String caisseNumber;

    @ManyToOne
    private PharmaUser caissier;

    @ManyToOne
    private PharmaUser fermerPar;

    @ManyToOne
    private Site site;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date dateOuverture;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date dateFemeture;

    @Value("0")
    private BigDecimal fondCaisse;

    private BigDecimal totalEncaissement = BigDecimal.ZERO;

    private BigDecimal totalRetrait = BigDecimal.ZERO;

    private BigDecimal totalCash = BigDecimal.ZERO;

    private BigDecimal totalCredit = BigDecimal.ZERO;

    private BigDecimal totalCheque = BigDecimal.ZERO;

    private BigDecimal totalCarteCredit = BigDecimal.ZERO;

    private BigDecimal totalVentePartiel = BigDecimal.ZERO;
    
    private BigDecimal totalBonCmd = BigDecimal.ZERO;

    private BigDecimal totalBonCmdPartiel = BigDecimal.ZERO;

    
    private BigDecimal totalBonClient = BigDecimal.ZERO;


    private BigDecimal totalProformat = BigDecimal.ZERO;

    @Value("true")
    private Boolean caisseOuverte;

    @Override
    protected void internalPrePersist() {
        caissier = SecurityUtil.getPharmaUser();
        site = caissier.getOffice();
        dateOuverture = new Date();
    }

    public Caisse(PharmaUser pharmaUser) {
        if (pharmaUser != null) {
            caissier = SecurityUtil.getPharmaUser();
            site = caissier.getOffice();
        }
        dateOuverture = new Date();
    }

    public Caisse() {
    }

    @PostPersist
    public void postPersit() {
        caisseNumber = NumberGenerator.getNumber("CA-", getId(), 4);
    }

    public void protectSomeField() {
        Caisse caisse = Caisse.findCaisse(getId());
        caissier = caisse.getCaissier();
        caisseNumber = caisse.getCaisseNumber();
        site = caisse.getSite();
        dateOuverture = caisse.getDateOuverture();
        footPrint = caisse.getFootPrint();
    }

    public void updateCash(BigDecimal amount) {
        totalCash = totalCash.add(amount);
        totalEncaissement = totalEncaissement.add(amount);
    }

    public void updateCredit(BigDecimal amount) {
        totalCredit = totalCredit.add(amount);
        totalEncaissement = totalEncaissement.add(amount);
    }

    public void updateCheque(BigDecimal amount) {
        totalCheque = totalCheque.add(amount);
        totalEncaissement = totalEncaissement.add(amount);
    }

    public void updateRemiseTotal(BigDecimal amount) {
        totalProformat = totalProformat.add(amount);
    }

    public void updateRetrait(BigDecimal amount) {
        totalRetrait = totalRetrait.add(amount);
    }

    public void updateVentePartiel(BigDecimal amount) {
        totalVentePartiel = totalVentePartiel.add(amount);
        totalEncaissement = totalEncaissement.add(amount);
    }

    public void updateCarteCredit(BigDecimal amount) {
        totalCarteCredit = totalCarteCredit.add(amount);
        totalEncaissement = totalEncaissement.add(amount);
    }
    
    public void updateBonClient(BigDecimal amount) {
        totalBonClient = totalBonClient.add(amount);
        totalEncaissement = totalEncaissement.add(amount);
    }
    
    public void updateBonCmd(BigDecimal amount) {
    	if (amount!=null) {
    		totalBonCmd = totalBonCmd.add(amount);
            totalEncaissement = totalEncaissement.add(amount);
		}
        
    }
    
    public void updateBonCmdPartiel(BigDecimal amount) {
        totalBonCmdPartiel = totalBonCmdPartiel.add(amount);
        totalEncaissement = totalEncaissement.add(amount);
    }

    public void computeAmount(Caisse caisse) {
        List<Paiement> paiements = Paiement.findPaiementsByCaisse(caisse).getResultList();
        if (!paiements.isEmpty()) {
            for (Paiement paiement : paiements) {
                if (paiement.getTypePaiement().equals(TypePaiement.CASH)) {
                    updateCash(paiement.getMontant());
                }
                if (paiement.getTypePaiement().equals(TypePaiement.CHEQUE)) {
                    updateCheque(paiement.getMontant());
                }
                if (paiement.getTypePaiement().equals(TypePaiement.CREDIT)) {
                    updateCredit(paiement.getMontant());
                }
                if (paiement.getTypePaiement().equals(TypePaiement.CARTE_CREDIT)) {
                    updateCarteCredit(paiement.getMontant());
                }
                if (paiement.getTypePaiement().equals(TypePaiement.BON_AVOIR_CLIENT)) {
                    updateBonClient(paiement.getMontant());
                }
                if (paiement.getTypePaiement().equals(TypePaiement.BON_CMD)) {
                    updateBonCmd(paiement.getMontant());
                }
                if (paiement.getTypePaiement().equals(TypePaiement.BON_CMD_PARTIEL)) {
                    updateBonCmdPartiel(paiement.getMontant());
                }
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCaisseNumber());
        return sb.toString();
    }

    public static TypedQuery<Caisse> findCaissesByCaisseOuverteAndCaissier(Boolean caisseOuverte, PharmaUser caissier) {
        if (caisseOuverte == null) throw new IllegalArgumentException("The caisseOuverte argument is required");
        if (caissier == null) throw new IllegalArgumentException("The caissier argument is required");
        EntityManager em = Caisse.entityManager();
        TypedQuery<Caisse> q = em.createQuery("SELECT o FROM Caisse AS o WHERE o.caisseOuverte IS :caisseOuverte  AND o.caissier = :caissier ORDER BY o.id DESC ", Caisse.class);
        q.setParameter("caisseOuverte", caisseOuverte);
        q.setParameter("caissier", caissier);
        return q;
    }
    
    public static TypedQuery<Caisse> findCaissesByCaisseNumber(String caisseNumber) {
        if (caisseNumber == null) throw new IllegalArgumentException("The caisseNumber argument is required");
        EntityManager em = Caisse.entityManager();
        TypedQuery<Caisse> q = em.createQuery("SELECT o FROM Caisse AS o WHERE  o.caisseNumber = :caisseNumber ORDER BY o.id DESC ", Caisse.class);
        q.setParameter("caisseNumber", caisseNumber);
        return q;
    }

    public static List<Object[]> findEtatCaisse(String caisse) {
        if (caisse == null) throw new IllegalArgumentException("The caisse argument is required");
        EntityManager em = Caisse.entityManager();
        Query q = em.createQuery("SELECT distinct o.filiale , COUNT(distinct o.numeroTicket) ,SUM(o.pAchatTotal), SUM(o.pVenteTotal), SUM(o.remiseTotal)  FROM MouvementStock AS o WHERE o.caisse = :caisse AND o.typeMouvement = :typeMouvement   GROUP BY o.filiale HAVING  SUM(o.pAchatTotal) >= 0 ");
        q.setParameter("caisse", caisse);
        q.setParameter("typeMouvement", TypeMouvement.VENTE);
        return q.getResultList();
    }
    
    public static List<Object[]> findChiffreAffaire(Date debut ,Date fin) {
        if (debut == null || fin == null ) throw new IllegalArgumentException("The debut or fin  arguments are required");
        EntityManager em = Caisse.entityManager();
        Query q = em.createQuery("SELECT distinct o.filiale , COUNT(distinct o.numeroTicket) ,SUM(o.pAchatTotal), SUM(o.pVenteTotal), SUM(o.remiseTotal)  FROM MouvementStock AS o WHERE o.dateCreation BETWEEN :debut AND :fin AND o.typeMouvement = :typeMouvement   GROUP BY o.filiale HAVING  SUM(o.pAchatTotal) >= 0 ");
        q.setParameter("debut", debut);
        q.setParameter("fin", fin);
        q.setParameter("typeMouvement", TypeMouvement.VENTE);
        return q.getResultList();
    }
    
    public static List<Object[]> getNbClientAndSaleAmountByDate(Date debut ,Date fin) {
        if (debut == null || fin == null ) throw new IllegalArgumentException("The debut or fin  arguments are required");
        EntityManager em = Caisse.entityManager();
        Query q = em.createQuery("SELECT COUNT(distinct o.numeroTicket) , SUM(o.pVenteTotal)  FROM MouvementStock AS o WHERE o.dateCreation BETWEEN :debut AND :fin AND o.typeMouvement = :typeMouvement ");
        q.setParameter("debut", debut);
        q.setParameter("fin", fin);
        q.setParameter("typeMouvement", TypeMouvement.VENTE);
        return q.getResultList();
    }
    
    public static List<Object[]> findChiffreVente(Date debut ,Date fin) {
        if (debut == null || fin == null ) throw new IllegalArgumentException("The debut or fin  arguments are required");
        EntityManager em = Caisse.entityManager();
        Query q = em.createQuery("SELECT SUM(o.pAchatTotal), SUM(o.pVenteTotal) FROM MouvementStock AS o WHERE o.dateCreation BETWEEN :debut AND :fin AND o.typeMouvement = :typeMouvement ");
        q.setParameter("debut", debut);
        q.setParameter("fin", fin);
        q.setParameter("typeMouvement", TypeMouvement.VENTE);
        return q.getResultList();
    }
    
    public static List<Object[]> findChiffreAffaireByFiliale(String filiale ,  Date debut ,Date fin) {
        if (debut == null || fin == null || filiale == null) throw new IllegalArgumentException("The debut or fin or filiale  arguments are required");
        EntityManager em = Caisse.entityManager();
        Query q = em.createQuery("SELECT distinct o.filiale , COUNT(distinct o.numeroTicket) ,SUM(o.pAchatTotal), SUM(o.pVenteTotal), SUM(o.remiseTotal)  FROM MouvementStock AS o WHERE o.dateCreation BETWEEN :debut AND :fin AND o.filiale = :filiale  AND o.typeMouvement = :typeMouvement   GROUP BY o.filiale HAVING  SUM(o.pAchatTotal) >= 0 ");
        q.setParameter("debut", debut);
        q.setParameter("fin", fin);
        q.setParameter("filiale", filiale);
        q.setParameter("typeMouvement", TypeMouvement.VENTE);
        return q.getResultList();
    }

    public static List<Caisse> findCaisseEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Caisse o ORDER BY o.id DESC ", Caisse.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static TypedQuery<Caisse> search(String caisseNumber, PharmaUser caissier, Date dateOuverture, Date dateFemeture, Boolean caisseOuverte) {
        StringBuilder searchQuery = new StringBuilder("SELECT o FROM Caisse AS o WHERE o.caisseOuverte IS :caisseOuverte ");
        caisseOuverte = caisseOuverte == null ?caisseOuverte:Boolean.TRUE ;
        if (caissier !=null) {
            searchQuery.append(" AND  o.caissier = :caissier ");
        }
        if (dateOuverture != null) {
            searchQuery.append(" AND  o.dateOuverture >= :dateOuverture ");
        }
        
        if (dateFemeture != null) {
            searchQuery.append(" AND  o.dateFemeture >= :dateFemeture ");
        }
        if (StringUtils.isNotBlank(caisseNumber)) {
        	caisseNumber = "CA-"+caisseNumber.replace("CA-", " ").trim();
            searchQuery.append(" AND  o.caisseNumber = :caisseNumber ");
        }
       
        
        
        TypedQuery<Caisse> q = entityManager().createQuery(searchQuery.toString(), Caisse.class);
        if (caisseOuverte != null) {
            q.setParameter("caisseOuverte", caisseOuverte);

        }
        if (caissier != null) {
            q.setParameter("caissier", caissier);
        }
        if (dateOuverture != null) {
            q.setParameter("dateOuverture", dateOuverture);
        }
        
        if (dateFemeture != null) {
            q.setParameter("dateFemeture", dateFemeture);
        }
        if (StringUtils.isNotBlank(caisseNumber)) {
            q.setParameter("caisseNumber", caisseNumber);
        }
        return q;
    }
}
