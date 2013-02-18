// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.String;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.adorsys.adpharma.domain.Fournisseur;

privileged aspect Fournisseur_Roo_Finder {
    
    public static TypedQuery<Fournisseur> Fournisseur.findFournisseursByNameLike(String name) {
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
        name = name.replace('*', '%');
        if (name.charAt(0) != '%') {
            name = "%" + name;
        }
        if (name.charAt(name.length() - 1) != '%') {
            name = name + "%";
        }
        EntityManager em = Fournisseur.entityManager();
        TypedQuery<Fournisseur> q = em.createQuery("SELECT o FROM Fournisseur AS o WHERE LOWER(o.name) LIKE LOWER(:name)", Fournisseur.class);
        q.setParameter("name", name);
        return q;
    }
    
}