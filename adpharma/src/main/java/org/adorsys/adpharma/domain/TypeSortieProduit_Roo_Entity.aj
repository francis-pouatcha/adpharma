// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Long;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.adorsys.adpharma.domain.TypeSortieProduit;

privileged aspect TypeSortieProduit_Roo_Entity {
    
    declare @type: TypeSortieProduit: @Entity(name = "TypeSortieProduit");
    
    declare @type: TypeSortieProduit: @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS);
    
    public static long TypeSortieProduit.countTypeSortieProduits() {
        return entityManager().createQuery("SELECT COUNT(o) FROM TypeSortieProduit o", Long.class).getSingleResult();
    }
    
    public static List<TypeSortieProduit> TypeSortieProduit.findAllTypeSortieProduits() {
        return entityManager().createQuery("SELECT o FROM TypeSortieProduit o", TypeSortieProduit.class).getResultList();
    }
    
    public static TypeSortieProduit TypeSortieProduit.findTypeSortieProduit(Long id) {
        if (id == null) return null;
        return entityManager().find(TypeSortieProduit.class, id);
    }
    
    public static List<TypeSortieProduit> TypeSortieProduit.findTypeSortieProduitEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM TypeSortieProduit o", TypeSortieProduit.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}