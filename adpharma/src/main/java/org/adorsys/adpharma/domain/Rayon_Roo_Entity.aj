// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Long;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.adorsys.adpharma.domain.Rayon;

privileged aspect Rayon_Roo_Entity {
    
    declare @type: Rayon: @Entity(name = "Rayon");
    
    declare @type: Rayon: @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS);
    
    public static long Rayon.countRayons() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Rayon o", Long.class).getSingleResult();
    }
    
    public static Rayon Rayon.findRayon(Long id) {
        if (id == null) return null;
        return entityManager().find(Rayon.class, id);
    }
    
    public static List<Rayon> Rayon.findRayonEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Rayon o", Rayon.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
