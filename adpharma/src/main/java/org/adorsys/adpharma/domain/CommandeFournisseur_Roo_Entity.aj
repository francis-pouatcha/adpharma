// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Long;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.adorsys.adpharma.domain.CommandeFournisseur;

privileged aspect CommandeFournisseur_Roo_Entity {
    
    declare @type: CommandeFournisseur: @Entity(name = "CommandeFournisseur");
    
    declare @type: CommandeFournisseur: @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS);
    
    public static long CommandeFournisseur.countCommandeFournisseurs() {
        return entityManager().createQuery("SELECT COUNT(o) FROM CommandeFournisseur o", Long.class).getSingleResult();
    }
    
    public static List<CommandeFournisseur> CommandeFournisseur.findAllCommandeFournisseurs() {
        return entityManager().createQuery("SELECT o FROM CommandeFournisseur o", CommandeFournisseur.class).getResultList();
    }
    
    public static CommandeFournisseur CommandeFournisseur.findCommandeFournisseur(Long id) {
        if (id == null) return null;
        return entityManager().find(CommandeFournisseur.class, id);
    }
    
}
