// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Boolean;
import java.math.BigDecimal;
import java.util.Date;
import org.adorsys.adpharma.domain.EtatSolde;

privileged aspect ConfigurationSoldes_Roo_JavaBean {
    
    public Date ConfigurationSoldes.getDebutSolde() {
        return this.debutSolde;
    }
    
    public void ConfigurationSoldes.setDebutSolde(Date debutSolde) {
        this.debutSolde = debutSolde;
    }
    
    public Date ConfigurationSoldes.getFinSolde() {
        return this.finSolde;
    }
    
    public void ConfigurationSoldes.setFinSolde(Date finSolde) {
        this.finSolde = finSolde;
    }
    
    public BigDecimal ConfigurationSoldes.getTauxSolde() {
        return this.tauxSolde;
    }
    
    public void ConfigurationSoldes.setTauxSolde(BigDecimal tauxSolde) {
        this.tauxSolde = tauxSolde;
    }
    
    public EtatSolde ConfigurationSoldes.getEtatSole() {
        return this.etatSole;
    }
    
    public void ConfigurationSoldes.setEtatSole(EtatSolde etatSole) {
        this.etatSole = etatSole;
    }
    
    public Boolean ConfigurationSoldes.getActiveConfig() {
        return this.activeConfig;
    }
    
    public void ConfigurationSoldes.setActiveConfig(Boolean activeConfig) {
        this.activeConfig = activeConfig;
    }
    
}
