// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.beans.process;

import java.lang.Long;
import java.lang.String;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.Facture;

privileged aspect PaiementProcess_Roo_JavaBean {
    
    public boolean PaiementProcess.isShowPostForm() {
        return this.showPostForm;
    }
    
    public void PaiementProcess.setShowPostForm(boolean showPostForm) {
        this.showPostForm = showPostForm;
    }
    
    public boolean PaiementProcess.isShowPutForm() {
        return this.showPutForm;
    }
    
    public void PaiementProcess.setShowPutForm(boolean showPutForm) {
        this.showPutForm = showPutForm;
    }
    
    public boolean PaiementProcess.isShowDetailForm() {
        return this.showDetailForm;
    }
    
    public void PaiementProcess.setShowDetailForm(boolean showDetailForm) {
        this.showDetailForm = showDetailForm;
    }
    
    public String PaiementProcess.getCassier() {
        return this.cassier;
    }
    
    public void PaiementProcess.setCassier(String cassier) {
        this.cassier = cassier;
    }
    
    public Client PaiementProcess.getClient() {
        return this.client;
    }
    
    public void PaiementProcess.setClient(Client client) {
        this.client = client;
    }
    
    public String PaiementProcess.getCaisseNumber() {
        return this.caisseNumber;
    }
    
    public void PaiementProcess.setCaisseNumber(String caisseNumber) {
        this.caisseNumber = caisseNumber;
    }
    
    public String PaiementProcess.getDateOuvertureCaisse() {
        return this.dateOuvertureCaisse;
    }
    
    public void PaiementProcess.setDateOuvertureCaisse(String dateOuvertureCaisse) {
        this.dateOuvertureCaisse = dateOuvertureCaisse;
    }
    
    public BigDecimal PaiementProcess.getFondCaisse() {
        return this.fondCaisse;
    }
    
    public void PaiementProcess.setFondCaisse(BigDecimal fondCaisse) {
        this.fondCaisse = fondCaisse;
    }
    
    public Long PaiementProcess.getFactureId() {
        return this.factureId;
    }
    
    public void PaiementProcess.setFactureId(Long factureId) {
        this.factureId = factureId;
    }
    
    public Facture PaiementProcess.getFactureSelected() {
        return this.factureSelected;
    }
    
    public void PaiementProcess.setFactureSelected(Facture factureSelected) {
        this.factureSelected = factureSelected;
    }
    
    public BigInteger PaiementProcess.getDetteclient() {
        return this.detteclient;
    }
    
    public void PaiementProcess.setDetteclient(BigInteger detteclient) {
        this.detteclient = detteclient;
    }
    
    public BigInteger PaiementProcess.getDettePayeur() {
        return this.dettePayeur;
    }
    
    public void PaiementProcess.setDettePayeur(BigInteger dettePayeur) {
        this.dettePayeur = dettePayeur;
    }
    
    public List<Facture> PaiementProcess.getFactureResult() {
        return this.factureResult;
    }
    
    public void PaiementProcess.setFactureResult(List<Facture> factureResult) {
        this.factureResult = factureResult;
    }
    
}