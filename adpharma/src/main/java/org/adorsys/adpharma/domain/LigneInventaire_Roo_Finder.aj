// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneInventaire;

privileged aspect LigneInventaire_Roo_Finder {
    
    /*public static TypedQuery<LigneInventaire> LigneInventaire.findLigneInventairesByInventaire(Inventaire inventaire) {
        if (inventaire == null) throw new IllegalArgumentException("The inventaire argument is required");
        EntityManager em = LigneInventaire.entityManager();

        TypedQuery<LigneInventaire> q = em.createQuery("SELECT o FROM LigneInventaire AS o WHERE o.inventaire = :inventaire ORDER BY o.produit.designation ", LigneInventaire.class);
        q.setParameter("inventaire", inventaire);
        return q;
    }*/
    
}
