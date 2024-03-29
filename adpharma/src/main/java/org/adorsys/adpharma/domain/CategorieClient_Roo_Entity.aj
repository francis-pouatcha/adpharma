// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Long;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.adorsys.adpharma.domain.CategorieClient;

privileged aspect CategorieClient_Roo_Entity {
    
    declare @type: CategorieClient: @Entity(name = "CategorieClient");
    
    declare @type: CategorieClient: @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS);
    
    public static long CategorieClient.countCategorieClients() {
        return entityManager().createQuery("SELECT COUNT(o) FROM CategorieClient o", Long.class).getSingleResult();
    }
    
    public static List<CategorieClient> CategorieClient.findAllCategorieClients() {
        return entityManager().createQuery("SELECT o FROM CategorieClient o", CategorieClient.class).getResultList();
    }
    
    public static CategorieClient CategorieClient.findCategorieClient(Long id) {
        if (id == null) return null;
        return entityManager().find(CategorieClient.class, id);
    }
    
}
