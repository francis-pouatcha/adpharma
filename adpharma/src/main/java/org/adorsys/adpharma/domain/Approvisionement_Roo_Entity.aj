// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Long;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.adorsys.adpharma.domain.Approvisionement;

privileged aspect Approvisionement_Roo_Entity {
    
    declare @type: Approvisionement: @Entity(name = "Approvisionement");
    
    declare @type: Approvisionement: @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS);
    
    public static long Approvisionement.countApprovisionements() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Approvisionement o", Long.class).getSingleResult();
    }
    
    public static Approvisionement Approvisionement.findApprovisionement(Long id) {
        if (id == null) return null;
        return entityManager().find(Approvisionement.class, id);
    }
    
}
