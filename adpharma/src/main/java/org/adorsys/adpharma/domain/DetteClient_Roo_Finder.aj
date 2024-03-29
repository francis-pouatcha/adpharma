// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Boolean;
import java.lang.String;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.adorsys.adpharma.domain.DetteClient;

privileged aspect DetteClient_Roo_Finder {
    
    public static TypedQuery<DetteClient> DetteClient.findDetteClientsByClientNameLike(String clientName) {
        if (clientName == null || clientName.length() == 0) throw new IllegalArgumentException("The clientName argument is required");
        clientName = clientName.replace('*', '%');
        if (clientName.charAt(0) != '%') {
            clientName = "%" + clientName;
        }
        if (clientName.charAt(clientName.length() - 1) != '%') {
            clientName = clientName + "%";
        }
        EntityManager em = DetteClient.entityManager();
        TypedQuery<DetteClient> q = em.createQuery("SELECT o FROM DetteClient AS o WHERE LOWER(o.clientName) LIKE LOWER(:clientName)", DetteClient.class);
        q.setParameter("clientName", clientName);
        return q;
    }
    
    public static TypedQuery<DetteClient> DetteClient.findDetteClientsByClientNameLikeAndSolderNot(String clientName, Boolean solder) {
        if (clientName == null || clientName.length() == 0) throw new IllegalArgumentException("The clientName argument is required");
        clientName = clientName.replace('*', '%');
        if (clientName.charAt(0) != '%') {
            clientName = "%" + clientName;
        }
        if (clientName.charAt(clientName.length() - 1) != '%') {
            clientName = clientName + "%";
        }
        if (solder == null) throw new IllegalArgumentException("The solder argument is required");
        EntityManager em = DetteClient.entityManager();
        TypedQuery<DetteClient> q = em.createQuery("SELECT o FROM DetteClient AS o WHERE LOWER(o.clientName) LIKE LOWER(:clientName)  AND o.solder IS NOT :solder", DetteClient.class);
        q.setParameter("clientName", clientName);
        q.setParameter("solder", solder);
        return q;
    }
    
    public static TypedQuery<DetteClient> DetteClient.findDetteClientsByClientNoLike(String clientNo) {
        if (clientNo == null || clientNo.length() == 0) throw new IllegalArgumentException("The clientNo argument is required");
        clientNo = clientNo.replace('*', '%');
        if (clientNo.charAt(0) != '%') {
            clientNo = "%" + clientNo;
        }
        if (clientNo.charAt(clientNo.length() - 1) != '%') {
            clientNo = clientNo + "%";
        }
        EntityManager em = DetteClient.entityManager();
        TypedQuery<DetteClient> q = em.createQuery("SELECT o FROM DetteClient AS o WHERE LOWER(o.clientNo) LIKE LOWER(:clientNo)", DetteClient.class);
        q.setParameter("clientNo", clientNo);
        return q;
    }
    
    public static TypedQuery<DetteClient> DetteClient.findDetteClientsByDateCreationBetween(Date minDateCreation, Date maxDateCreation) {
        if (minDateCreation == null) throw new IllegalArgumentException("The minDateCreation argument is required");
        if (maxDateCreation == null) throw new IllegalArgumentException("The maxDateCreation argument is required");
        EntityManager em = DetteClient.entityManager();
        TypedQuery<DetteClient> q = em.createQuery("SELECT o FROM DetteClient AS o WHERE o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation", DetteClient.class);
        q.setParameter("minDateCreation", minDateCreation);
        q.setParameter("maxDateCreation", maxDateCreation);
        return q;
    }
    
    public static TypedQuery<DetteClient> DetteClient.findDetteClientsBySolderNotAndAnnulerNot(Boolean solder, Boolean annuler) {
        if (solder == null) throw new IllegalArgumentException("The solder argument is required");
        if (annuler == null) throw new IllegalArgumentException("The annuler argument is required");
        EntityManager em = DetteClient.entityManager();
        TypedQuery<DetteClient> q = em.createQuery("SELECT o FROM DetteClient AS o WHERE o.solder IS NOT :solder  AND o.annuler IS NOT :annuler", DetteClient.class);
        q.setParameter("solder", solder);
        q.setParameter("annuler", annuler);
        return q;
    }
    
    public static TypedQuery<DetteClient> DetteClient.findDetteClientsBySolderNotOrClientNoEquals(Boolean solder, String clientNo) {
        if (solder == null) throw new IllegalArgumentException("The solder argument is required");
        if (clientNo == null || clientNo.length() == 0) throw new IllegalArgumentException("The clientNo argument is required");
        EntityManager em = DetteClient.entityManager();
        TypedQuery<DetteClient> q = em.createQuery("SELECT o FROM DetteClient AS o WHERE o.solder IS NOT :solder  OR o.clientNo = :clientNo", DetteClient.class);
        q.setParameter("solder", solder);
        q.setParameter("clientNo", clientNo);
        return q;
    }
    
}
