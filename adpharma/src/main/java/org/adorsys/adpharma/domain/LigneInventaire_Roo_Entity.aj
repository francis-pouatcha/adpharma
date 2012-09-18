// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Integer;
import java.lang.Long;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Version;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.springframework.transaction.annotation.Transactional;

privileged aspect LigneInventaire_Roo_Entity {
    
    declare @type: LigneInventaire: @Entity;
    
    @PersistenceContext
    transient EntityManager LigneInventaire.entityManager;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long LigneInventaire.id;
    
    @Version
    @Column(name = "version")
    private Integer LigneInventaire.version;
    
    public Long LigneInventaire.getId() {
        return this.id;
    }
    
    public void LigneInventaire.setId(Long id) {
        this.id = id;
    }
    
    public Integer LigneInventaire.getVersion() {
        return this.version;
    }
    
    public void LigneInventaire.setVersion(Integer version) {
        this.version = version;
    }
    
    @Transactional
    public void LigneInventaire.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void LigneInventaire.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            LigneInventaire attached = LigneInventaire.findLigneInventaire(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void LigneInventaire.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void LigneInventaire.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public LigneInventaire LigneInventaire.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        LigneInventaire merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager LigneInventaire.entityManager() {
        EntityManager em = new LigneInventaire().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long LigneInventaire.countLigneInventaires() {
        return entityManager().createQuery("SELECT COUNT(o) FROM LigneInventaire o", Long.class).getSingleResult();
    }
    
    public static List<LigneInventaire> LigneInventaire.findAllLigneInventaires() {
        return entityManager().createQuery("SELECT o FROM LigneInventaire o", LigneInventaire.class).getResultList();
    }
    
    public static LigneInventaire LigneInventaire.findLigneInventaire(Long id) {
        if (id == null) return null;
        return entityManager().find(LigneInventaire.class, id);
    }
    
    public static List<LigneInventaire> LigneInventaire.findLigneInventaireEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM LigneInventaire o", LigneInventaire.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
