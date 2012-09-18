// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.LigneApprovisionement;

privileged aspect LigneApprovisionement_Roo_JavaBean {
    
    public String LigneApprovisionement.getLotNumber() {
        return this.lotNumber;
    }
    
    public void LigneApprovisionement.setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }
    
    public int LigneApprovisionement.getIndexLine() {
        return this.indexLine;
    }
    
    public void LigneApprovisionement.setIndexLine(int indexLine) {
        this.indexLine = indexLine;
    }
    
    public String LigneApprovisionement.getCipMaison() {
        return this.cipMaison;
    }
    
    public void LigneApprovisionement.setCipMaison(String cipMaison) {
        this.cipMaison = cipMaison;
    }
    
    public String LigneApprovisionement.getDesignation() {
        return this.designation;
    }
    
    public void LigneApprovisionement.setDesignation(String designation) {
        this.designation = designation;
    }
    
    public Date LigneApprovisionement.getDateFabrication() {
        return this.dateFabrication;
    }
    
    public void LigneApprovisionement.setDateFabrication(Date dateFabrication) {
        this.dateFabrication = dateFabrication;
    }
    
    public Date LigneApprovisionement.getDatePeremtion() {
        return this.datePeremtion;
    }
    
    public void LigneApprovisionement.setDatePeremtion(Date datePeremtion) {
        this.datePeremtion = datePeremtion;
    }
    
    public String LigneApprovisionement.getAgentSaisie() {
        return this.agentSaisie;
    }
    
    public void LigneApprovisionement.setAgentSaisie(String agentSaisie) {
        this.agentSaisie = agentSaisie;
    }
    
    public Date LigneApprovisionement.getDateSaisie() {
        return this.dateSaisie;
    }
    
    public void LigneApprovisionement.setDateSaisie(Date dateSaisie) {
        this.dateSaisie = dateSaisie;
    }
    
    public BigInteger LigneApprovisionement.getQuantiteAprovisione() {
        return this.quantiteAprovisione;
    }
    
    public void LigneApprovisionement.setQuantiteAprovisione(BigInteger quantiteAprovisione) {
        this.quantiteAprovisione = quantiteAprovisione;
    }
    
    public BigInteger LigneApprovisionement.getQuantieEnStock() {
        return this.quantieEnStock;
    }
    
    public void LigneApprovisionement.setQuantieEnStock(BigInteger quantieEnStock) {
        this.quantieEnStock = quantieEnStock;
    }
    
    public BigInteger LigneApprovisionement.getQuantiteSortie() {
        return this.quantiteSortie;
    }
    
    public void LigneApprovisionement.setQuantiteSortie(BigInteger quantiteSortie) {
        this.quantiteSortie = quantiteSortie;
    }
    
    public BigDecimal LigneApprovisionement.getPrixAchatUnitaire() {
        return this.prixAchatUnitaire;
    }
    
    public void LigneApprovisionement.setPrixAchatUnitaire(BigDecimal prixAchatUnitaire) {
        this.prixAchatUnitaire = prixAchatUnitaire;
    }
    
    public BigDecimal LigneApprovisionement.getPrixAchatTotal() {
        return this.prixAchatTotal;
    }
    
    public void LigneApprovisionement.setPrixAchatTotal(BigDecimal prixAchatTotal) {
        this.prixAchatTotal = prixAchatTotal;
    }
    
    public BigDecimal LigneApprovisionement.getMargeBrute() {
        return this.margeBrute;
    }
    
    public void LigneApprovisionement.setMargeBrute(BigDecimal margeBrute) {
        this.margeBrute = margeBrute;
    }
    
    public BigDecimal LigneApprovisionement.getPrixVenteUnitaire() {
        return this.prixVenteUnitaire;
    }
    
    public void LigneApprovisionement.setPrixVenteUnitaire(BigDecimal prixVenteUnitaire) {
        this.prixVenteUnitaire = prixVenteUnitaire;
    }
    
    public boolean LigneApprovisionement.isVenteAutorise() {
        return this.venteAutorise;
    }
    
    public void LigneApprovisionement.setVenteAutorise(boolean venteAutorise) {
        this.venteAutorise = venteAutorise;
    }
    
    public BigDecimal LigneApprovisionement.getRemiseMax() {
        return this.remiseMax;
    }
    
    public void LigneApprovisionement.setRemiseMax(BigDecimal remiseMax) {
        this.remiseMax = remiseMax;
    }
    
    public Approvisionement LigneApprovisionement.getApprovisionement() {
        return this.approvisionement;
    }
    
    public void LigneApprovisionement.setApprovisionement(Approvisionement approvisionement) {
        this.approvisionement = approvisionement;
    }
    
    public Boolean LigneApprovisionement.getRemiseAutorise() {
        return this.remiseAutorise;
    }
    
    public void LigneApprovisionement.setRemiseAutorise(Boolean remiseAutorise) {
        this.remiseAutorise = remiseAutorise;
    }
    
}
