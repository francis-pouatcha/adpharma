// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.PharmaUser;

privileged aspect Inventaire_Roo_Finder {
    
    public static TypedQuery<Inventaire> Inventaire.findInventairesByAgentAndDateInventaireBetween(PharmaUser agent, Date minDateInventaire, Date maxDateInventaire) {
        if (agent == null) throw new IllegalArgumentException("The agent argument is required");
        if (minDateInventaire == null) throw new IllegalArgumentException("The minDateInventaire argument is required");
        if (maxDateInventaire == null) throw new IllegalArgumentException("The maxDateInventaire argument is required");
        EntityManager em = Inventaire.entityManager();
        TypedQuery<Inventaire> q = em.createQuery("SELECT o FROM Inventaire AS o WHERE o.agent = :agent AND o.dateInventaire BETWEEN :minDateInventaire AND :maxDateInventaire", Inventaire.class);
        q.setParameter("agent", agent);
        q.setParameter("minDateInventaire", minDateInventaire);
        q.setParameter("maxDateInventaire", maxDateInventaire);
        return q;
    }
    
    public static TypedQuery<Inventaire> Inventaire.findInventairesByDateInventaireBetween(Date minDateInventaire, Date maxDateInventaire) {
        if (minDateInventaire == null) throw new IllegalArgumentException("The minDateInventaire argument is required");
        if (maxDateInventaire == null) throw new IllegalArgumentException("The maxDateInventaire argument is required");
        EntityManager em = Inventaire.entityManager();
        TypedQuery<Inventaire> q = em.createQuery("SELECT o FROM Inventaire AS o WHERE o.dateInventaire BETWEEN :minDateInventaire AND :maxDateInventaire", Inventaire.class);
        q.setParameter("minDateInventaire", minDateInventaire);
        q.setParameter("maxDateInventaire", maxDateInventaire);
        return q;
    }
    
    public static TypedQuery<Inventaire> Inventaire.findInventairesByEtat(Etat etat) {
        if (etat == null) throw new IllegalArgumentException("The etat argument is required");
        EntityManager em = Inventaire.entityManager();
        TypedQuery<Inventaire> q = em.createQuery("SELECT o FROM Inventaire AS o WHERE o.etat = :etat", Inventaire.class);
        q.setParameter("etat", etat);
        return q;
    }
    
}
