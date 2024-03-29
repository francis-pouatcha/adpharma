// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;

privileged aspect LigneCmdFournisseur_Roo_Finder {
    
    public static TypedQuery<LigneCmdFournisseur> LigneCmdFournisseur.findLigneCmdFournisseursByCommande(CommandeFournisseur commande) {
        if (commande == null) throw new IllegalArgumentException("The commande argument is required");
        EntityManager em = LigneCmdFournisseur.entityManager();
        TypedQuery<LigneCmdFournisseur> q = em.createQuery("SELECT o FROM LigneCmdFournisseur AS o WHERE o.commande = :commande", LigneCmdFournisseur.class);
        q.setParameter("commande", commande);
        return q;
    }
    
}
