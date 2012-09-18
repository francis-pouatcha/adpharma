// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Boolean;
import java.lang.String;
import java.math.BigDecimal;
import java.util.Date;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.QuiPaye;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TypePaiement;

privileged aspect Paiement_Roo_JavaBean {
    
    public String Paiement.getPaiementNumber() {
        return this.paiementNumber;
    }
    
    public void Paiement.setPaiementNumber(String paiementNumber) {
        this.paiementNumber = paiementNumber;
    }
    
    public Date Paiement.getDatePaiement() {
        return this.datePaiement;
    }
    
    public void Paiement.setDatePaiement(Date datePaiement) {
        this.datePaiement = datePaiement;
    }
    
    public Date Paiement.getDateSaisie() {
        return this.dateSaisie;
    }
    
    public void Paiement.setDateSaisie(Date dateSaisie) {
        this.dateSaisie = dateSaisie;
    }
    
    public BigDecimal Paiement.getMontant() {
        return this.montant;
    }
    
    public void Paiement.setMontant(BigDecimal montant) {
        this.montant = montant;
    }
    
    public BigDecimal Paiement.getSommeRecue() {
        return this.sommeRecue;
    }
    
    public void Paiement.setSommeRecue(BigDecimal sommeRecue) {
        this.sommeRecue = sommeRecue;
    }
    
    public BigDecimal Paiement.getDifference() {
        return this.difference;
    }
    
    public void Paiement.setDifference(BigDecimal difference) {
        this.difference = difference;
    }
    
    public Site Paiement.getSite() {
        return this.site;
    }
    
    public void Paiement.setSite(Site site) {
        this.site = site;
    }
    
    public PharmaUser Paiement.getCassier() {
        return this.cassier;
    }
    
    public void Paiement.setCassier(PharmaUser cassier) {
        this.cassier = cassier;
    }
    
    public Caisse Paiement.getCaisse() {
        return this.caisse;
    }
    
    public void Paiement.setCaisse(Caisse caisse) {
        this.caisse = caisse;
    }
    
    public Facture Paiement.getFacture() {
        return this.facture;
    }
    
    public void Paiement.setFacture(Facture facture) {
        this.facture = facture;
    }
    
    public TypePaiement Paiement.getTypePaiement() {
        return this.typePaiement;
    }
    
    public void Paiement.setTypePaiement(TypePaiement typePaiement) {
        this.typePaiement = typePaiement;
    }
    
    public Boolean Paiement.getTicketImprimer() {
        return this.ticketImprimer;
    }
    
    public void Paiement.setTicketImprimer(Boolean ticketImprimer) {
        this.ticketImprimer = ticketImprimer;
    }
    
    public String Paiement.getPayerPar() {
        return this.payerPar;
    }
    
    public void Paiement.setPayerPar(String payerPar) {
        this.payerPar = payerPar;
    }
    
    public QuiPaye Paiement.getQuiPaye() {
        return this.quiPaye;
    }
    
    public void Paiement.setQuiPaye(QuiPaye quiPaye) {
        this.quiPaye = quiPaye;
    }
    
    public String Paiement.getNomClient() {
        return this.nomClient;
    }
    
    public void Paiement.setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }
    
    public String Paiement.getInformations() {
        return this.informations;
    }
    
    public void Paiement.setInformations(String informations) {
        this.informations = informations;
    }
    
    public String Paiement.getNumeroBon() {
        return this.numeroBon;
    }
    
    public void Paiement.setNumeroBon(String numeroBon) {
        this.numeroBon = numeroBon;
    }
    
    public BigDecimal Paiement.getMontantBon() {
        return this.montantBon;
    }
    
    public void Paiement.setMontantBon(BigDecimal montantBon) {
        this.montantBon = montantBon;
    }
    
}
