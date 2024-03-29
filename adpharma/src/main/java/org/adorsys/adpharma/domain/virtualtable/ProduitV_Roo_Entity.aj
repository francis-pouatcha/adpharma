// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain.virtualtable;

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
import org.adorsys.adpharma.domain.virtualtable.ProduitV;
import org.springframework.transaction.annotation.Transactional;

privileged aspect ProduitV_Roo_Entity {
    
    declare @type: ProduitV: @Entity;
    
    @PersistenceContext
    transient EntityManager ProduitV.entityManager;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long ProduitV.id;
    
    @Version
    @Column(name = "version")
    private Integer ProduitV.version;
    
    public Long ProduitV.getId() {
        return this.id;
    }
    
    public void ProduitV.setId(Long id) {
        this.id = id;
    }
    
    public Integer ProduitV.getVersion() {
        return this.version;
    }
    
    public void ProduitV.setVersion(Integer version) {
        this.version = version;
    }
    
    @Transactional
    public void ProduitV.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void ProduitV.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            ProduitV attached = ProduitV.findProduitV(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void ProduitV.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void ProduitV.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public ProduitV ProduitV.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        ProduitV merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager ProduitV.entityManager() {
        EntityManager em = new ProduitV().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long ProduitV.countProduitVs() {
        return entityManager().createQuery("SELECT COUNT(o) FROM ProduitV o", Long.class).getSingleResult();
    }
    
    public static ProduitV ProduitV.findProduitV(Long id) {
        if (id == null) return null;
        return entityManager().find(ProduitV.class, id);
    }
    
    public static List<ProduitV> ProduitV.findProduitVEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM ProduitV o", ProduitV.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
