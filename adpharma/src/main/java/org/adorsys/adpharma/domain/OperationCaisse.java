package org.adorsys.adpharma.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.adorsys.adpharma.domain.Caisse;
import javax.validation.constraints.NotNull;
import javax.persistence.ManyToOne;
import org.adorsys.adpharma.domain.PharmaUser;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.springframework.format.annotation.DateTimeFormat;
import org.adorsys.adpharma.domain.TypeOpCaisse;
import javax.persistence.Enumerated;
import org.adorsys.adpharma.domain.TypePaiement;
import org.adorsys.adpharma.utils.NumberGenerator;
import java.math.BigDecimal;
import javax.validation.constraints.Size;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "OperationCaisse", finders = { "findOperationCaissesByCaisse", "findOperationCaissesByDateOperationBetween" })
public class OperationCaisse extends AdPharmaBaseEntity {

    private String opNumber;

   // @NotNull
    @ManyToOne
    private Caisse caisse;

    @NotNull
    @ManyToOne
    private PharmaUser operateur;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date dateOperation;

    @Enumerated
    private TypeOpCaisse typeOperation;

    @Enumerated
    private TypePaiement modePaiement;

    private BigDecimal montant;

    @Size(max = 256)
    private String raisonOperation;

    private String note;

    @PostPersist
    public void postPersit() {
        opNumber = NumberGenerator.getNumber("OPC-", getId(), 4);
    }

    public static TypedQuery<OperationCaisse> findOperationCaissesByCaisse(Caisse caisse) {
        if (caisse == null) throw new IllegalArgumentException("The caisse argument is required");
        EntityManager em = OperationCaisse.entityManager();
        TypedQuery<OperationCaisse> q = em.createQuery("SELECT  o FROM OperationCaisse AS o WHERE o.caisse = :caisse ORDER BY o.id DESC ", OperationCaisse.class);
        q.setParameter("caisse", caisse);
        return q;
    }

    public static TypedQuery<OperationCaisse> findOperationCaissesByCaisseAndTypeOperation(TypePaiement modePaiement, Caisse caisse) {
        if (caisse == null) throw new IllegalArgumentException("The caisse argument is required");
        if (modePaiement == null) throw new IllegalArgumentException("The modePaiement argument is required");
        EntityManager em = OperationCaisse.entityManager();
        TypedQuery<OperationCaisse> q = em.createQuery("SELECT  o FROM OperationCaisse AS o WHERE o.caisse = :caisse AND  o.modePaiement = :modePaiement ORDER BY o.id DESC  ", OperationCaisse.class);
        q.setParameter("caisse", caisse);
        q.setParameter("modePaiement", modePaiement);
        return q;
    }

    public static List<OperationCaisse> findOperationCaisseAndCaissierEntries(int firstResult, int maxResults, Caisse caisse) {
        TypedQuery<OperationCaisse> typedQuery = OperationCaisse.findOperationCaissesByCaisse(caisse);
        return typedQuery.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<OperationCaisse> findOperationCaisseEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM OperationCaisse o ORDER BY o.id DESC", OperationCaisse.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    
    public static TypedQuery<OperationCaisse> findOperationCaissesByDateOperationBetween(Date minDateOperation, Date maxDateOperation) {
        if (minDateOperation == null) throw new IllegalArgumentException("The minDateOperation argument is required");
        if (maxDateOperation == null) throw new IllegalArgumentException("The maxDateOperation argument is required");
        EntityManager em = OperationCaisse.entityManager();
        TypedQuery<OperationCaisse> q = em.createQuery("SELECT o FROM OperationCaisse AS o WHERE o.dateOperation BETWEEN :minDateOperation AND :maxDateOperation ORDER BY o.id DESC", OperationCaisse.class);
        q.setParameter("minDateOperation", minDateOperation);
        q.setParameter("maxDateOperation", maxDateOperation);
        return q;
    }
}
