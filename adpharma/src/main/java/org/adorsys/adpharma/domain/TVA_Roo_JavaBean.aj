// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Boolean;
import java.lang.String;
import java.math.BigDecimal;

privileged aspect TVA_Roo_JavaBean {
    
    public String TVA.getTvaNumber() {
        return this.tvaNumber;
    }
    
    public void TVA.setTvaNumber(String tvaNumber) {
        this.tvaNumber = tvaNumber;
    }
    
    public BigDecimal TVA.getTauxTva() {
        return this.tauxTva;
    }
    
    public void TVA.setTauxTva(BigDecimal tauxTva) {
        this.tauxTva = tauxTva;
    }
    
    public Boolean TVA.getValide() {
        return this.valide;
    }
    
    public void TVA.setValide(Boolean valide) {
        this.valide = valide;
    }
    
}
