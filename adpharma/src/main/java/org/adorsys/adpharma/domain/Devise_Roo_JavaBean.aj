// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.String;
import java.math.BigDecimal;

privileged aspect Devise_Roo_JavaBean {
    
    public String Devise.getDeviseNumber() {
        return this.deviseNumber;
    }
    
    public void Devise.setDeviseNumber(String deviseNumber) {
        this.deviseNumber = deviseNumber;
    }
    
    public String Devise.getLibelle() {
        return this.libelle;
    }
    
    public void Devise.setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public String Devise.getLibelleCourt() {
        return this.libelleCourt;
    }
    
    public void Devise.setLibelleCourt(String libelleCourt) {
        this.libelleCourt = libelleCourt;
    }
    
    public BigDecimal Devise.getEquivalenceCfa() {
        return this.equivalenceCfa;
    }
    
    public void Devise.setEquivalenceCfa(BigDecimal equivalenceCfa) {
        this.equivalenceCfa = equivalenceCfa;
    }
    
}
