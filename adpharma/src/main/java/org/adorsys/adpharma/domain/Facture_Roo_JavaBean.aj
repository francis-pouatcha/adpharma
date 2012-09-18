// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.math.BigInteger;
import java.util.Date;
import java.util.Set;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.LigneFacture;
import org.adorsys.adpharma.domain.Paiement;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TypeCommande;
import org.adorsys.adpharma.domain.TypeFacture;

privileged aspect Facture_Roo_JavaBean {
    
    public String Facture.getFactureNumber() {
        return this.factureNumber;
    }
    
    public void Facture.setFactureNumber(String factureNumber) {
        this.factureNumber = factureNumber;
    }
    
    public Date Facture.getDateCreation() {
        return this.dateCreation;
    }
    
    public void Facture.setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public Client Facture.getClient() {
        return this.client;
    }
    
    public void Facture.setClient(Client client) {
        this.client = client;
    }
    
    public Caisse Facture.getCaisse() {
        return this.caisse;
    }
    
    public void Facture.setCaisse(Caisse caisse) {
        this.caisse = caisse;
    }
    
    public PharmaUser Facture.getVendeur() {
        return this.vendeur;
    }
    
    public void Facture.setVendeur(PharmaUser vendeur) {
        this.vendeur = vendeur;
    }
    
    public Site Facture.getSite() {
        return this.site;
    }
    
    public void Facture.setSite(Site site) {
        this.site = site;
    }
    
    public CommandeClient Facture.getCommande() {
        return this.commande;
    }
    
    public void Facture.setCommande(CommandeClient commande) {
        this.commande = commande;
    }
    
    public TypeCommande Facture.getTypeCommande() {
        return this.typeCommande;
    }
    
    public void Facture.setTypeCommande(TypeCommande typeCommande) {
        this.typeCommande = typeCommande;
    }
    
    public BigInteger Facture.getMontantTotal() {
        return this.montantTotal;
    }
    
    public void Facture.setMontantTotal(BigInteger montantTotal) {
        this.montantTotal = montantTotal;
    }
    
    public BigInteger Facture.getMontantRemise() {
        return this.montantRemise;
    }
    
    public void Facture.setMontantRemise(BigInteger montantRemise) {
        this.montantRemise = montantRemise;
    }
    
    public BigInteger Facture.getNetPayer() {
        return this.netPayer;
    }
    
    public void Facture.setNetPayer(BigInteger netPayer) {
        this.netPayer = netPayer;
    }
    
    public Boolean Facture.getSolder() {
        return this.solder;
    }
    
    public void Facture.setSolder(Boolean solder) {
        this.solder = solder;
    }
    
    public Boolean Facture.getEncaisser() {
        return this.encaisser;
    }
    
    public void Facture.setEncaisser(Boolean encaisser) {
        this.encaisser = encaisser;
    }
    
    public BigInteger Facture.getAvance() {
        return this.avance;
    }
    
    public void Facture.setAvance(BigInteger avance) {
        this.avance = avance;
    }
    
    public BigInteger Facture.getReste() {
        return this.reste;
    }
    
    public void Facture.setReste(BigInteger reste) {
        this.reste = reste;
    }
    
    public Set<LigneFacture> Facture.getLineFacture() {
        return this.lineFacture;
    }
    
    public void Facture.setLineFacture(Set<LigneFacture> lineFacture) {
        this.lineFacture = lineFacture;
    }
    
    public Set<Paiement> Facture.getPaiement() {
        return this.paiement;
    }
    
    public void Facture.setPaiement(Set<Paiement> paiement) {
        this.paiement = paiement;
    }
    
    public TypeFacture Facture.getTypeFacture() {
        return this.typeFacture;
    }
    
    public void Facture.setTypeFacture(TypeFacture typeFacture) {
        this.typeFacture = typeFacture;
    }
    
}
