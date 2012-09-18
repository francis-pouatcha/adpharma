package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.PostPersist;
import javax.validation.constraints.Size;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.Digits;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "CategorieClient", finders = { "findCategorieClientsByCategorieNumberEquals" })
public class CategorieClient extends AdPharmaBaseEntity {

    private String categorieNumber;

    private String libelle;

    @Size(max = 256)
    private String note;

    @Min(0L)
    @Max(100L)
    private BigDecimal tauxRemiseMax;

    public String displayName() {
        return new StringBuilder().append(getLibelle()).append(" , ").append(getCategorieNumber()).toString();
    }

    public static void initCategorieClient() {
        if (CategorieClient.countCategorieClients() <= 0) {
            System.out.println("[ initialisation des  categories clients ]");
            CategorieClient categorieClient = new CategorieClient();
            categorieClient.setLibelle("CLIENTS DIVERS");
            categorieClient.setTauxRemiseMax(BigDecimal.ZERO);
            categorieClient.persist();
        }
    }

    @Override
    protected void internalPrePersist() {
    }

    public void protectSomeField() {
        CategorieClient categorieClient = CategorieClient.findCategorieClient(getId());
        categorieNumber = categorieClient.getCategorieNumber();
    }

    @PostPersist
    public void postPersit() {
        categorieNumber = NumberGenerator.getNumber("CAT-", getId(), 4);

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getLibelle().toUpperCase());
        return sb.toString();
    }
    
    public static List<CategorieClient> findCategorieClientEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM CategorieClient o ORDER BY o.id DESC", CategorieClient.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
}
