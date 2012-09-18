// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Boolean;
import java.lang.String;
import java.math.BigInteger;
import java.util.Date;
import org.adorsys.adpharma.domain.DestinationMvt;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TypeMouvement;

privileged aspect MouvementStock_Roo_JavaBean {
    
    public String MouvementStock.getMvtNumber() {
        return this.mvtNumber;
    }
    
    public void MouvementStock.setMvtNumber(String mvtNumber) {
        this.mvtNumber = mvtNumber;
    }
    
    public Date MouvementStock.getDateCreation() {
        return this.dateCreation;
    }
    
    public void MouvementStock.setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public String MouvementStock.getAgentCreateur() {
        return this.agentCreateur;
    }
    
    public void MouvementStock.setAgentCreateur(String agentCreateur) {
        this.agentCreateur = agentCreateur;
    }
    
    public TypeMouvement MouvementStock.getTypeMouvement() {
        return this.typeMouvement;
    }
    
    public void MouvementStock.setTypeMouvement(TypeMouvement typeMouvement) {
        this.typeMouvement = typeMouvement;
    }
    
    public BigInteger MouvementStock.getQteDeplace() {
        return this.qteDeplace;
    }
    
    public void MouvementStock.setQteDeplace(BigInteger qteDeplace) {
        this.qteDeplace = qteDeplace;
    }
    
    public Site MouvementStock.getSite() {
        return this.site;
    }
    
    public void MouvementStock.setSite(Site site) {
        this.site = site;
    }
    
    public DestinationMvt MouvementStock.getOrigine() {
        return this.origine;
    }
    
    public void MouvementStock.setOrigine(DestinationMvt origine) {
        this.origine = origine;
    }
    
    public DestinationMvt MouvementStock.getDestination() {
        return this.destination;
    }
    
    public void MouvementStock.setDestination(DestinationMvt destination) {
        this.destination = destination;
    }
    
    public String MouvementStock.getNumeroTicket() {
        return this.numeroTicket;
    }
    
    public void MouvementStock.setNumeroTicket(String numeroTicket) {
        this.numeroTicket = numeroTicket;
    }
    
    public String MouvementStock.getNumeroBordereau() {
        return this.numeroBordereau;
    }
    
    public void MouvementStock.setNumeroBordereau(String numeroBordereau) {
        this.numeroBordereau = numeroBordereau;
    }
    
    public String MouvementStock.getCip() {
        return this.cip;
    }
    
    public void MouvementStock.setCip(String cip) {
        this.cip = cip;
    }
    
    public String MouvementStock.getCipM() {
        return this.cipM;
    }
    
    public void MouvementStock.setCipM(String cipM) {
        this.cipM = cipM;
    }
    
    public String MouvementStock.getFiliale() {
        return this.filiale;
    }
    
    public void MouvementStock.setFiliale(String filiale) {
        this.filiale = filiale;
    }
    
    public String MouvementStock.getDesignation() {
        return this.designation;
    }
    
    public void MouvementStock.setDesignation(String designation) {
        this.designation = designation;
    }
    
    public BigInteger MouvementStock.getQteInitiale() {
        return this.qteInitiale;
    }
    
    public void MouvementStock.setQteInitiale(BigInteger qteInitiale) {
        this.qteInitiale = qteInitiale;
    }
    
    public BigInteger MouvementStock.getQteFinale() {
        return this.qteFinale;
    }
    
    public void MouvementStock.setQteFinale(BigInteger qteFinale) {
        this.qteFinale = qteFinale;
    }
    
    public String MouvementStock.getCaisse() {
        return this.caisse;
    }
    
    public void MouvementStock.setCaisse(String caisse) {
        this.caisse = caisse;
    }
    
    public BigInteger MouvementStock.getPAchatTotal() {
        return this.pAchatTotal;
    }
    
    public void MouvementStock.setPAchatTotal(BigInteger pAchatTotal) {
        this.pAchatTotal = pAchatTotal;
    }
    
    public BigInteger MouvementStock.getPVenteTotal() {
        return this.pVenteTotal;
    }
    
    public void MouvementStock.setPVenteTotal(BigInteger pVenteTotal) {
        this.pVenteTotal = pVenteTotal;
    }
    
    public BigInteger MouvementStock.getRemiseTotal() {
        return this.remiseTotal;
    }
    
    public void MouvementStock.setRemiseTotal(BigInteger remiseTotal) {
        this.remiseTotal = remiseTotal;
    }
    
    public String MouvementStock.getNote() {
        return this.note;
    }
    
    public void MouvementStock.setNote(String note) {
        this.note = note;
    }
    
    public Boolean MouvementStock.getAnnuler() {
        return this.annuler;
    }
    
    public void MouvementStock.setAnnuler(Boolean annuler) {
        this.annuler = annuler;
    }
    
}
