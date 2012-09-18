// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Long;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.adorsys.adpharma.domain.Filiale;

privileged aspect Filiale_Roo_Entity {
    
    declare @type: Filiale: @Entity(name = "Filiale");
    
    declare @type: Filiale: @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS);
    
    public static long Filiale.countFiliales() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Filiale o", Long.class).getSingleResult();
    }
    
    public static Filiale Filiale.findFiliale(Long id) {
        if (id == null) return null;
        return entityManager().find(Filiale.class, id);
    }
    
    public static List<Filiale> Filiale.findFilialeEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Filiale o", Filiale.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
