// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.String;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Etat;

privileged aspect CommandeClient_Roo_Finder {
    
    public static TypedQuery<CommandeClient> CommandeClient.findCommandeClientsByCmdNumberEquals(String cmdNumber) {
        if (cmdNumber == null || cmdNumber.length() == 0) throw new IllegalArgumentException("The cmdNumber argument is required");
        EntityManager em = CommandeClient.entityManager();
        TypedQuery<CommandeClient> q = em.createQuery("SELECT o FROM CommandeClient AS o WHERE o.cmdNumber = :cmdNumber", CommandeClient.class);
        q.setParameter("cmdNumber", cmdNumber);
        return q;
    }
    
    public static TypedQuery<CommandeClient> CommandeClient.findCommandeClientsByDateCreationBetween(Date minDateCreation, Date maxDateCreation) {
        if (minDateCreation == null) throw new IllegalArgumentException("The minDateCreation argument is required");
        if (maxDateCreation == null) throw new IllegalArgumentException("The maxDateCreation argument is required");
        EntityManager em = CommandeClient.entityManager();
        TypedQuery<CommandeClient> q = em.createQuery("SELECT o FROM CommandeClient AS o WHERE o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation", CommandeClient.class);
        q.setParameter("minDateCreation", minDateCreation);
        q.setParameter("maxDateCreation", maxDateCreation);
        return q;
    }
    
    public static TypedQuery<CommandeClient> CommandeClient.findCommandeClientsByStatusAndDateCreationBetween(Etat status, Date minDateCreation, Date maxDateCreation) {
        if (status == null) throw new IllegalArgumentException("The status argument is required");
        if (minDateCreation == null) throw new IllegalArgumentException("The minDateCreation argument is required");
        if (maxDateCreation == null) throw new IllegalArgumentException("The maxDateCreation argument is required");
        EntityManager em = CommandeClient.entityManager();
        TypedQuery<CommandeClient> q = em.createQuery("SELECT o FROM CommandeClient AS o WHERE o.status = :status AND o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation", CommandeClient.class);
        q.setParameter("status", status);
        q.setParameter("minDateCreation", minDateCreation);
        q.setParameter("maxDateCreation", maxDateCreation);
        return q;
    }
    
}
