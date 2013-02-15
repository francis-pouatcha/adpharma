package org.adorsys.adpharma.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.TypedQuery;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import org.adorsys.adpharma.domain.virtualtable.ProduitV;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Rayon", finders = { "findRayonsByDisplayNameEquals", "findRayonsByNameEquals", "findRayonsByDisplayNameLike", "findRayonsByNameLike" })
public class Rayon extends AdPharmaBaseEntity {

    private String codeRayon;

    @NotNull
    private String name;

    private String displayName;

    private String emplacement;

    private String codeGeo;

    private String note;

    @NotNull
    @ManyToOne
    private Site magasin;

    protected void internalPrePersist() {
    }

    @PostPersist
    public void postPersist() {
        codeRayon = NumberGenerator.getNumber("RAY-", getId(), 4);
    }

    protected void internalPostPersit() {
    }

    public void protectSomeField() {
        Rayon rayon = Rayon.findRayon(getId());
        codeRayon = rayon.getCodeRayon();
        footPrint = rayon.getFootPrint();
    }

    public String toString() {
        return displayName();
    }

    public String displayName() {
        return new StringBuilder().append(getName()).toString();
    }

    protected String getBusinessKey() {
        return codeRayon;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public String getCodeGeo() {
        return codeGeo;
    }

    public void setCodeGeo(String codeGeo) {
        this.codeGeo = codeGeo;
    }

    public boolean existe() {
        if (StringUtils.isNotBlank(codeGeo)) return !Rayon.findRayonsByCodeGeoEquals(codeGeo).getResultList().isEmpty();
        return !Rayon.findRayonsByNameEquals(name).getResultList().isEmpty();
    }

    public static TypedQuery<Rayon> findRayonsByCodeGeoEquals(String codeGeo) {
        if (codeGeo == null || codeGeo.length() == 0) throw new IllegalArgumentException("The codeGeo argument is required");
        EntityManager em = Rayon.entityManager();
        TypedQuery<Rayon> q = em.createQuery("SELECT o FROM Rayon AS o WHERE o.codeGeo = :codeGeo", Rayon.class);
        q.setParameter("codeGeo", codeGeo);
        return q;
    }

    public static TypedQuery<Rayon> findRayonsByEmplacement(String emplacement) {
        if (emplacement == null || emplacement.length() == 0) throw new IllegalArgumentException("The emplacement argument is required");
        EntityManager em = Rayon.entityManager();
        TypedQuery<Rayon> q = em.createQuery("SELECT o FROM Rayon AS o WHERE o.emplacement = :emplacement", Rayon.class);
        q.setParameter("emplacement", emplacement);
        return q;
    }

    public static List<Rayon> findAllRayons() {
        return entityManager().createQuery("SELECT o FROM Rayon o ORDER BY o.name ASC ", Rayon.class).getResultList();
    }
}
