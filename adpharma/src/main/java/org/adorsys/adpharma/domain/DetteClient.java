package org.adorsys.adpharma.domain;

import org.adorsys.adpharma.domain.AdPharmaBaseEntity;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PostLoad;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigInteger;
import org.springframework.beans.factory.annotation.Value;
import org.adorsys.adpharma.domain.EtatCredits;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;

import flexjson.JSONSerializer;

import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "DetteClient", finders = { "findDetteClientsByFactureIdEqualsAndClientIdEquals", "findDetteClientsByClientIdEqualsAndSolderNot", "findDetteClientsByClientNameLike", "findDetteClientsByClientNameLikeAndSolderNot", "findDetteClientsByDateCreationBetween", "findDetteClientsBySolderNotOrClientNoEquals" })
public class DetteClient extends AdPharmaBaseEntity {

    @NotNull
    private Long factureId;

    @NotNull
    private Long clientId;

    private String clientName;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date dateCreation = new Date();

    private BigInteger montantInitial = BigInteger.ZERO;

    private BigInteger avance = BigInteger.ZERO;

    private BigInteger reste = BigInteger.ZERO;

    @Value("false")
    private Boolean solder;
    
    @Transient
    private String assurer ;
    
    @Transient
    private String numBon ;

    @Transient
    private int taux ;
    
    public String getNumBon() {
		return numBon;
	}

	public void setNumBon(String numBon) {
		this.numBon = numBon;
	}

	public int getTaux() {
		return taux;
	}

	public void setTaux(int taux) {
		this.taux = taux;
	}

	public BigInteger getSousTotal() {
		return sousTotal;
	}

	public void setSousTotal(BigInteger sousTotal) {
		this.sousTotal = sousTotal;
	}

	@Transient
    private BigInteger sousTotal ;
    
    public String getAssurer() {
		return assurer;
	}

	public void setAssurer(String assurer) {
		this.assurer = assurer;
	}

	@Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date dateDernierVersement;

    private String clientNo;

    private String factureNo;

    @Value("false")
    private Boolean annuler;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date datePaiement;

    @ManyToOne
    private EtatCredits etatCredit;

    public String toJson() {
        return new JSONSerializer().include("id","factureNo","clientName","avance","montantInitial","reste").exclude("*.class","*").serialize(this);
    }
    public static String toJsonArray(Collection<DetteClient> collection) {
        return new JSONSerializer().include("id","factureNo","clientName","avance","montantInitial","reste").exclude("*.class","*").serialize(collection);
    }
    public void avancer(BigInteger montant) {
        avance = avance.add(montant);
        reste = montantInitial.subtract(avance);
        solder = estSolder();
        dateDernierVersement = new Date();
    }

    public boolean estSolder() {
        return reste.intValue() == 0;
    }
    
    @PostLoad
    public void postLoad(){
    	if( StringUtils.isNotBlank(factureNo)){
    		 Facture facture = Facture.findFacturesByFactureNumberEquals(factureNo).getResultList().iterator().next();
    		assurer =facture.getClient().displayNameShot();
    		numBon = facture.getCommande().getBonNumber();
    		taux = 100 - facture.getCommande().getTauxCouverture();
    		sousTotal = facture.getNetPayer();
    	}
    }
    

    public static List<DetteClient> findDetteClientEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM DetteClient o ORDER BY o.id DESC ", DetteClient.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static TypedQuery<DetteClient> findDetteClientsBySolderNotAndClientNoEquals(Boolean solder, String clientNo) {
        if (solder == null) throw new IllegalArgumentException("The solder argument is required");
        if (clientNo == null || clientNo.length() == 0) throw new IllegalArgumentException("The clientNo argument is required");
        EntityManager em = DetteClient.entityManager();
        TypedQuery<DetteClient> q = em.createQuery("SELECT o FROM DetteClient AS o WHERE o.solder IS NOT :solder  AND o.clientNo = :clientNo ORDER BY o.dateCreation DESC ", DetteClient.class);
        q.setParameter("solder", solder);
        q.setParameter("clientNo", clientNo);
        return q;
    }

    public static TypedQuery<DetteClient> findDetteClientsByClientIdEqualsAndSolderNot(Long clientId, Boolean solder) {
        if (clientId == null) throw new IllegalArgumentException("The clientId argument is required");
        if (solder == null) throw new IllegalArgumentException("The solder argument is required");
        EntityManager em = DetteClient.entityManager();
        TypedQuery<DetteClient> q = em.createQuery("SELECT o FROM DetteClient AS o WHERE o.clientId = :clientId  AND o.annuler IS NOT :annuler AND o.solder IS NOT :solder", DetteClient.class);
        q.setParameter("clientId", clientId);
        q.setParameter("solder", solder);
        q.setParameter("annuler", Boolean.TRUE);
        return q;
    }

    public static TypedQuery<DetteClient> findDetteClientsByFactureIdEqualsAndClientIdEquals(Long factureId, Long clientId) {
        if (factureId == null) throw new IllegalArgumentException("The factureId argument is required");
        if (clientId == null) throw new IllegalArgumentException("The clientId argument is required");
        EntityManager em = DetteClient.entityManager();
        TypedQuery<DetteClient> q = em.createQuery("SELECT o FROM DetteClient AS o WHERE o.factureId = :factureId  AND o.clientId = :clientId AND o.annuler IS NOT :annuler", DetteClient.class);
        q.setParameter("factureId", factureId);
        q.setParameter("clientId", clientId);
        q.setParameter("annuler", Boolean.TRUE);
        return q;
    }
    
    public static BigInteger getTotalDetteAvailable(Long clientId){
    	BigInteger detteAvailable = BigInteger.ZERO ;
    	Query query = entityManager().createQuery("SELECT SUM(o.reste) FROM DetteClient AS o WHERE  o.clientId = :clientId AND o.solder IS :solder AND o.annuler IS :annuler ");
        query.setParameter("clientId", clientId);
        query.setParameter("solder", Boolean.FALSE);
        query.setParameter("annuler", Boolean.FALSE);
        List<Object> resultList =  query.getResultList();
        if(!resultList.isEmpty()){
        	BigInteger next = (BigInteger) resultList.iterator().next();
        	detteAvailable = next != null ?next : detteAvailable;
        }
    	return detteAvailable;
    }
    
   

    public static TypedQuery<DetteClient> search(Long clientId, EtatCredits etatCredit, Boolean annuler, Boolean solder) {
        StringBuilder searchQuery = new StringBuilder("SELECT o FROM DetteClient AS o WHERE  ");
        if (etatCredit != null) {
            searchQuery.append("  o.etatCredit = :etatCredit  ");
        } else {
            searchQuery.append(" o.etatCredit IS  NULL ");
        }
        if (clientId != null) {
            searchQuery.append(" AND o.clientId = :clientId ");
        }
        if (annuler != null) {
            searchQuery.append(" AND o.annuler = :annuler ");
        }
        if (solder != null) {
            searchQuery.append(" AND o.solder = :solder ");
        }
        TypedQuery<DetteClient> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.dateCreation ASC").toString(), DetteClient.class);
        if (clientId != null) {
            q.setParameter("clientId", clientId);
        }
        if (annuler != null) {
            q.setParameter("annuler", annuler);
        }
        if (solder != null) {
            q.setParameter("solder", solder);
        }
        if (etatCredit != null) {
            q.setParameter("etatCredit", etatCredit);
        }
        return q;
    }
    
    public static TypedQuery<DetteClient> search(String clientName,String assurer,  Date dateCreation, Boolean solder, String factureNo) {
        StringBuilder searchQuery = new StringBuilder("SELECT o FROM DetteClient AS o WHERE   o.dateCreation >= :dateCreation ");
        dateCreation = dateCreation!=null?dateCreation:PharmaDateUtil.parse("15-05-2012 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
        if (StringUtils.isNotBlank(factureNo)) {
        	factureNo = "FAC-"+StringUtils.removeStart(factureNo, "FAC-");
            searchQuery.append(" AND o.factureNo = :factureNo ");
        }
        if (solder!=null) {
            searchQuery.append(" AND o.solder = :solder ");
        }
        if (StringUtils.isNotBlank(clientName)) {
        	clientName = "%"+clientName+"%";
            searchQuery.append(" AND  LOWER(o.clientName) LIKE LOWER(:clientName) ");
        }
        
        if (StringUtils.isNotBlank(assurer)) {
        	assurer = "%"+assurer+"%";
            searchQuery.append(" AND o.factureNo IN (SELECT f.factureNumber FROM Facture AS f WHERE LOWER(f.commande.client.nomComplet) LIKE LOWER(:assurer)  AND f.dateCreation >= :dateCreation ) ");
        }
       
        TypedQuery<DetteClient> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.id DESC").toString(), DetteClient.class);
        if (StringUtils.isNotBlank(factureNo)) {
            q.setParameter("factureNo", factureNo);
        }
        if (dateCreation != null) {
            q.setParameter("solder", solder);
        }
        if (StringUtils.isNotBlank(clientName)) {
            q.setParameter("clientName", clientName);
        }
        
        if (StringUtils.isNotBlank(assurer)) {
            q.setParameter("assurer", assurer);
        }
        q.setParameter("dateCreation", dateCreation);

        return q;
    }
}
