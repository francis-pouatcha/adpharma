package org.adorsys.adpharma.domain;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.beans.factory.annotation.Value;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "MouvementStock", finders = { "findMouvementStocksByDateCreationBetween", "findMouvementStocksByTypeMouvementAndDateCreationBetween", "findMouvementStocksByDesignationEqualsAndDateCreationBetween", "findMouvementStocksByDateCreationBetweenAndAgentCreateurEquals", "findMouvementStocksByDateCreationBetweenAndAgentCreateurLike", "findMouvementStocksByCipMEquals" })
public class MouvementStock extends AdPharmaBaseEntity {

    private String mvtNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
    private Date dateCreation = new Date();

    @NotNull
    private String agentCreateur;

    @Enumerated
    private TypeMouvement typeMouvement;

    private BigInteger qteDeplace;

    @ManyToOne
    private Site site;

    @Enumerated
    private DestinationMvt origine;

    @Enumerated
    private DestinationMvt destination;

    private String numeroTicket;

    private String numeroBordereau;

    private String cip;

    private String cipM;

    private String filiale;

    private String designation;

    private BigInteger qteInitiale;

    private BigInteger qteFinale;

    private String caisse;

    private BigInteger pAchatTotal;

   

	public BigInteger getpAchatTotal() {
		return pAchatTotal;
	}

	public void setpAchatTotal(BigInteger pAchatTotal) {
		this.pAchatTotal = pAchatTotal;
	}

	public BigInteger getpVenteTotal() {
		return pVenteTotal;
	}

	public void setpVenteTotal(BigInteger pVenteTotal) {
		this.pVenteTotal = pVenteTotal;
	}


	private BigInteger pVenteTotal;

    private BigInteger remiseTotal;

    private String note;

    @Value("false")
    private Boolean annuler;

    @PrePersist
    public void PrePersist() {
        dateCreation = new Date();
    }

    @PostPersist
    public void postPersist() {
        mvtNumber = NumberGenerator.getNumber("MVT-", getId(), 4);
    }

    public static List<MouvementStock> search(TypeMouvement typeMouvement, String cipM, Date minDate, Date maxDate, String designation) {
        StringBuilder searchQuery = new StringBuilder("SELECT o FROM MouvementStock AS o WHERE o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation ");
        minDate = minDate != null ? minDate : PharmaDateUtil.parse("10-10-2010 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
        maxDate = maxDate != null ? maxDate : PharmaDateUtil.parse("10-10-2050 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
        if (StringUtils.isNotBlank(cipM)) {
            searchQuery.append(" AND o.cipM = :cipM ");
        }
        if (!typeMouvement.equals(TypeMouvement.ALL)) {
            searchQuery.append(" AND o.typeMouvement = :typeMouvement ");
        }
        if (StringUtils.isNotBlank(designation)) {
            designation = designation + "%";
            searchQuery.append(" AND  LOWER(o.designation) LIKE LOWER(:designation) ");
        }
        TypedQuery<MouvementStock> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.id DESC").toString(), MouvementStock.class);
        if (!typeMouvement.equals(TypeMouvement.ALL)) {
            q.setParameter("typeMouvement", typeMouvement);
        }
        if (StringUtils.isNotBlank(designation)) {
            q.setParameter("designation", designation);
        }
        if (StringUtils.isNotBlank(cipM)) {
            q.setParameter("cipM", cipM);
        }
        q.setParameter("minDateCreation", minDate);
        q.setParameter("maxDateCreation", maxDate);
        return q.getResultList();
    }
    
    public static List<MouvementStock> search(TypeMouvement typeMouvement, Date minDate, Date maxDate) {
        StringBuilder searchQuery = new StringBuilder("SELECT o FROM MouvementStock AS o WHERE o.dateCreation >= :minDateCreation AND o.dateCreation <= :maxDateCreation ");
        minDate = minDate != null ? minDate : PharmaDateUtil.parse("10-10-2010 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
        maxDate = maxDate != null ? maxDate : PharmaDateUtil.parse("10-10-2050 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
        
        if (!typeMouvement.equals(TypeMouvement.ALL)) {
            searchQuery.append(" AND o.typeMouvement = :typeMouvement ");
        }
       
        TypedQuery<MouvementStock> q = entityManager().createQuery(searchQuery.append(" AND o.annuler = :annuler  ORDER BY o.id DESC").toString(), MouvementStock.class);
        if (!typeMouvement.equals(TypeMouvement.ALL)) {
            q.setParameter("typeMouvement", typeMouvement);
        }
       
        q.setParameter("minDateCreation", minDate ,TemporalType.TIMESTAMP);
        q.setParameter("maxDateCreation", maxDate ,TemporalType.TIMESTAMP);
        q.setParameter("annuler", Boolean.FALSE);
        return q.getResultList();
    }

    public static List<MouvementStock> findAllMouvementStocks() {
        return entityManager().createQuery("SELECT o FROM MouvementStock o ORDER BY o.id DESC ", MouvementStock.class).getResultList();
    }

    public static List<MouvementStock> findMouvementStockEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM MouvementStock o ORDER BY o.id DESC", MouvementStock.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<Object[]> getEtatVente(Date debut ,Date fin) {
        if (debut == null || fin == null ) throw new IllegalArgumentException("The debut or fin  arguments are required");
        EntityManager em = MouvementStock.entityManager();
        Query q = em.createQuery("SELECT  o.designation ,SUM(o.qteDeplace)  ,SUM(o.pAchatTotal), SUM(o.pVenteTotal), SUM(o.remiseTotal)  FROM MouvementStock AS o WHERE o.dateCreation BETWEEN :debut AND :fin AND o.typeMouvement = :typeMouvement   GROUP BY o.designation  ORDER BY SUM(o.qteDeplace) DESC ");
        q.setParameter("debut", debut);
        q.setParameter("fin", fin);
        q.setParameter("typeMouvement", TypeMouvement.VENTE);
        return q.getResultList();
    }
    
    public static List<Object[]> getQteVenduByCip(String cip,String designation,String beginDes,String endDes,Date debut ,Date fin) {
        if (debut == null) throw new IllegalArgumentException("The debut arguments are required");
        fin = fin ==null?new Date():fin;
        EntityManager em = MouvementStock.entityManager();
        StringBuilder searchQuery = new StringBuilder("SELECT   o.cip , o.designation  ,SUM(o.qteDeplace) as qte FROM MouvementStock AS o WHERE o.dateCreation BETWEEN :debut AND :fin AND o.typeMouvement = :typeMouvement ");


        if (StringUtils.isNotBlank(cip)) {
        	searchQuery.append(" AND o.cip = :cip  ");
		}
        if (StringUtils.isNotBlank(designation)) {
        	  designation =  "%"+designation + "%";
        	searchQuery.append("  AND  LOWER(o.designation) LIKE LOWER(:designation)  ");
		}
        if (StringUtils.isNotBlank(beginDes)) {
        	beginDes = beginDes + "%";
      	searchQuery.append("  AND  LOWER(o.designation) >= LOWER(:beginDes)  ");
		}
        if (StringUtils.isNotBlank(endDes)) {
        	endDes = endDes + "%";
        	searchQuery.append("  AND  LOWER(o.designation) <= LOWER(:endDes)  ");
  		}
        Query q= em.createQuery(searchQuery.append("  GROUP BY o.cip  ORDER BY  qte ASC").toString());
        if (StringUtils.isNotBlank(cip)) q.setParameter("cip", cip);
        if (StringUtils.isNotBlank(designation)) q.setParameter("designation", designation);
        if (StringUtils.isNotBlank(beginDes)) q.setParameter("beginDes", beginDes);
        if (StringUtils.isNotBlank(endDes)) q.setParameter("endDes", endDes);
        q.setParameter("debut", debut);
        q.setParameter("fin", fin);
        q.setParameter("typeMouvement", TypeMouvement.VENTE);
        return q.getResultList();
    }
    
    
    public static List<Object[]> findProduitAndQuantiteVendue(String cip,String designation,String beginDes,String endDes,Date debut ,Date fin,Rayon rayon , Filiale filiale) {
        if (debut == null) throw new IllegalArgumentException("The debut arguments are required");
        fin = fin ==null?new Date():fin;
        EntityManager em = MouvementStock.entityManager();
        StringBuilder searchQuery = new StringBuilder("SELECT p , SUM(o.qteDeplace) as qte FROM MouvementStock AS o , Produit AS p WHERE o.cip = p.cip AND o.dateCreation BETWEEN :debut AND :fin AND o.typeMouvement = :typeMouvement ");

        if (StringUtils.isNotBlank(cip)) {
        	searchQuery.append(" AND o.cip = :cip  ");
		}
        if (StringUtils.isNotBlank(designation)) {
        	  designation =  "%"+designation + "%";
        	searchQuery.append("  AND  LOWER(o.designation) LIKE LOWER(:designation)  ");
		}
        if (StringUtils.isNotBlank(beginDes)) {
        	beginDes = beginDes + "%";
      	searchQuery.append("  AND  LOWER(o.designation) >= LOWER(:beginDes)  ");
		}
        if (StringUtils.isNotBlank(endDes)) {
        	endDes = endDes + "%";
        	searchQuery.append("  AND  LOWER(o.designation) <= LOWER(:endDes)  ");
  		}
        
        if (rayon != null) {
        	searchQuery.append("  AND p.rayon = :rayon  ");
  		}
        if (filiale != null) {
        	searchQuery.append("  AND p.filiale = :filiale  ");
  		}
        Query q= em.createQuery(searchQuery.append("  GROUP BY p  ORDER BY  o.designation").toString());
        if (StringUtils.isNotBlank(cip)) q.setParameter("cip", cip);
        if (StringUtils.isNotBlank(designation)) q.setParameter("designation", designation);
        if (StringUtils.isNotBlank(beginDes)) q.setParameter("beginDes", beginDes);
        if (StringUtils.isNotBlank(endDes)) q.setParameter("endDes", endDes);
        if (rayon!=null) q.setParameter("rayon", rayon);
        if (filiale!=null) q.setParameter("filiale", filiale);
        q.setParameter("debut", debut);
        q.setParameter("fin", fin);
        q.setParameter("typeMouvement", TypeMouvement.VENTE);
        return q.getResultList();
    }
    
    
    
    public static List<Object[]> courbeApprovisionement(String debut, String fin, String frequence){
      StringBuilder query= new StringBuilder();
      query.append("SELECT sum(p_achat_total) as pa , month(date_creation) as mo, year(date_creation) as y  FROM mouvement_stock");
      query.append(" where type_mouvement= 1");
      query.append(" group by y, mo ");
      query.append(" having mo in(1,2,3,4,5,6,7,8,9,10,11,12)  And  y in (2012,2013)");
      query.append(" order by y asc, mo asc;");
      Query requete = entityManager().createNativeQuery(query.toString());
      List<Object[]> liste = requete.getResultList();
      System.out.println("Liste des objets: "+liste);
    	return liste;
    }
    
    
    


    public static TypedQuery<MouvementStock> findMouvementStocksByAndCipAndTypeMouvementAndDateCreationBetween(String cip, TypeMouvement typeMouvement, Date minDateCreation, Date maxDateCreation) {
        if (typeMouvement == null) throw new IllegalArgumentException("The typeMouvement argument is required");
        if (minDateCreation == null) throw new IllegalArgumentException("The minDateCreation argument is required");
        if (maxDateCreation == null) throw new IllegalArgumentException("The maxDateCreation argument is required");
        if (cip == null) throw new IllegalArgumentException("The cip argument is required");
        EntityManager em = MouvementStock.entityManager();
        TypedQuery<MouvementStock> q = em.createQuery("SELECT o FROM MouvementStock AS o WHERE o.typeMouvement = :typeMouvement  AND o.cip = :cip AND o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation", MouvementStock.class);
        q.setParameter("typeMouvement", typeMouvement);
        q.setParameter("cip", cip);
        q.setParameter("minDateCreation", minDateCreation);
        q.setParameter("maxDateCreation", maxDateCreation);
        return q;
    }
}
