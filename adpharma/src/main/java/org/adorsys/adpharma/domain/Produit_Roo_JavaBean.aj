// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Boolean;
import java.lang.String;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.adorsys.adpharma.domain.ConfigurationSoldes;
import org.adorsys.adpharma.domain.FamilleProduit;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.ModeConditionement;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.SousFamilleProduit;
import org.adorsys.adpharma.domain.TVA;
import org.adorsys.adpharma.domain.TauxMarge;

privileged aspect Produit_Roo_JavaBean {
    
    public String Produit.getProduitNumber() {
        return this.produitNumber;
    }
    
    public void Produit.setProduitNumber(String produitNumber) {
        this.produitNumber = produitNumber;
    }
    
    public void Produit.setDesignation(String designation) {
        this.designation = designation;
    }
    
    public String Produit.getFabricant() {
        return this.fabricant;
    }
    
    public void Produit.setFabricant(String fabricant) {
        this.fabricant = fabricant;
    }
    
    public Rayon Produit.getRayon() {
        return this.rayon;
    }
    
    public void Produit.setRayon(Rayon rayon) {
        this.rayon = rayon;
    }
    
    public Boolean Produit.getActif() {
        return this.actif;
    }
    
    public void Produit.setActif(Boolean actif) {
        this.actif = actif;
    }
    
    public FamilleProduit Produit.getFamilleProduit() {
        return this.familleProduit;
    }
    
    public void Produit.setFamilleProduit(FamilleProduit familleProduit) {
        this.familleProduit = familleProduit;
    }
    
    public BigInteger Produit.getQteCommande() {
        return this.qteCommande;
    }
    
    public void Produit.setQteCommande(BigInteger qteCommande) {
        this.qteCommande = qteCommande;
    }
    
    public SousFamilleProduit Produit.getSousfamilleProduit() {
        return this.SousfamilleProduit;
    }
    
    public void Produit.setSousfamilleProduit(SousFamilleProduit SousfamilleProduit) {
        this.SousfamilleProduit = SousfamilleProduit;
    }
    
    public BigInteger Produit.getQuantiteEnStock() {
        return this.quantiteEnStock;
    }
    
    public void Produit.setQuantiteEnStock(BigInteger quantiteEnStock) {
        this.quantiteEnStock = quantiteEnStock;
    }
    
    public boolean Produit.isPerissable() {
        return this.perissable;
    }
    
    public void Produit.setPerissable(boolean perissable) {
        this.perissable = perissable;
    }
    
    public BigInteger Produit.getSeuilComande() {
        return this.seuilComande;
    }
    
    public void Produit.setSeuilComande(BigInteger seuilComande) {
        this.seuilComande = seuilComande;
    }
    
    public BigDecimal Produit.getTauxRemiseMax() {
        return this.tauxRemiseMax;
    }
    
    public void Produit.setTauxRemiseMax(BigDecimal tauxRemiseMax) {
        this.tauxRemiseMax = tauxRemiseMax;
    }
    
    public BigDecimal Produit.getPrixTotalStock() {
        return this.prixTotalStock;
    }
    
    public void Produit.setPrixTotalStock(BigDecimal prixTotalStock) {
        this.prixTotalStock = prixTotalStock;
    }
    
    public Date Produit.getDateDerniereEntre() {
        return this.dateDerniereEntre;
    }
    
    public void Produit.setDateDerniereEntre(Date dateDerniereEntre) {
        this.dateDerniereEntre = dateDerniereEntre;
    }
    
    public Date Produit.getDateDerniereSortie() {
        return this.dateDerniereSortie;
    }
    
    public void Produit.setDateDerniereSortie(Date dateDerniereSortie) {
        this.dateDerniereSortie = dateDerniereSortie;
    }
    
    public Date Produit.getDateDerniereRupture() {
        return this.dateDerniereRupture;
    }
    
    public void Produit.setDateDerniereRupture(Date dateDerniereRupture) {
        this.dateDerniereRupture = dateDerniereRupture;
    }
    
    public String Produit.getPosologie() {
        return this.posologie;
    }
    
    public void Produit.setPosologie(String posologie) {
        this.posologie = posologie;
    }
    
    public String Produit.getPrincipeActif() {
        return this.principeActif;
    }
    
    public void Produit.setPrincipeActif(String principeActif) {
        this.principeActif = principeActif;
    }
    
    public Boolean Produit.getProduitCompose() {
        return this.produitCompose;
    }
    
    public void Produit.setProduitCompose(Boolean produitCompose) {
        this.produitCompose = produitCompose;
    }
    
    public String Produit.getObservation() {
        return this.observation;
    }
    
    public void Produit.setObservation(String observation) {
        this.observation = observation;
    }
    
    public TVA Produit.getTvaProduit() {
        return this.tvaProduit;
    }
    
    public void Produit.setTvaProduit(TVA tvaProduit) {
        this.tvaProduit = tvaProduit;
    }
    
    public TauxMarge Produit.getTauxDeMarge() {
        return this.tauxDeMarge;
    }
    
    public void Produit.setTauxDeMarge(TauxMarge tauxDeMarge) {
        this.tauxDeMarge = tauxDeMarge;
    }
    
    public String Produit.getCip() {
        return this.cip;
    }
    
    public void Produit.setCip(String cip) {
        this.cip = cip;
    }
    
    public ModeConditionement Produit.getModeConditionement() {
        return this.modeConditionement;
    }
    
    public void Produit.setModeConditionement(ModeConditionement modeConditionement) {
        this.modeConditionement = modeConditionement;
    }
    
    public boolean Produit.isVenteAutorise() {
        return this.venteAutorise;
    }
    
    public void Produit.setVenteAutorise(boolean venteAutorise) {
        this.venteAutorise = venteAutorise;
    }
    
    public boolean Produit.isCommander() {
        return this.commander;
    }
    
    public void Produit.setCommander(boolean commander) {
        this.commander = commander;
    }
    
    public BigInteger Produit.getPlafondStock() {
        return this.plafondStock;
    }
    
    public void Produit.setPlafondStock(BigInteger plafondStock) {
        this.plafondStock = plafondStock;
    }
    
    public boolean Produit.isInStock() {
        return this.inStock;
    }
    
    public void Produit.setInStock(boolean inStock) {
        this.inStock = inStock;
    }
    
    public ConfigurationSoldes Produit.getConfigSolde() {
        return this.configSolde;
    }
    
    public void Produit.setConfigSolde(ConfigurationSoldes configSolde) {
        this.configSolde = configSolde;
    }
    
    public Filiale Produit.getFiliale() {
        return this.filiale;
    }
    
    public void Produit.setFiliale(Filiale filiale) {
        this.filiale = filiale;
    }
    
}
