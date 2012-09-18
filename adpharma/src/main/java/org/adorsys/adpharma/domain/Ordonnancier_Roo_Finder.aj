// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.String;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Ordonnancier;

privileged aspect Ordonnancier_Roo_Finder {
    
    public static TypedQuery<Ordonnancier> Ordonnancier.findOrdonnanciersByCommande(CommandeClient commande) {
        if (commande == null) throw new IllegalArgumentException("The commande argument is required");
        EntityManager em = Ordonnancier.entityManager();
        TypedQuery<Ordonnancier> q = em.createQuery("SELECT o FROM Ordonnancier AS o WHERE o.commande = :commande", Ordonnancier.class);
        q.setParameter("commande", commande);
        return q;
    }
    
    public static TypedQuery<Ordonnancier> Ordonnancier.findOrdonnanciersByOrdNumberEquals(String ordNumber) {
        if (ordNumber == null || ordNumber.length() == 0) throw new IllegalArgumentException("The ordNumber argument is required");
        EntityManager em = Ordonnancier.entityManager();
        TypedQuery<Ordonnancier> q = em.createQuery("SELECT o FROM Ordonnancier AS o WHERE o.ordNumber = :ordNumber", Ordonnancier.class);
        q.setParameter("ordNumber", ordNumber);
        return q;
    }
    
}
