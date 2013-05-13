// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.beans.process;

import java.lang.Long;
import java.lang.String;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneCmdClient;
import org.adorsys.adpharma.domain.Ordonnancier;

privileged aspect SaleProcess_Roo_JavaBean {
    
    public LigneApprovisionement SaleProcess.getProduit() {
        return this.produit;
    }
    
    public void SaleProcess.setProduit(LigneApprovisionement produit) {
        this.produit = produit;
    }
    
    public Long SaleProcess.getCmdId() {
        return this.cmdId;
    }
    
    public void SaleProcess.setCmdId(Long cmdId) {
        this.cmdId = cmdId;
    }
    
    public List<CommandeClient> SaleProcess.getCommmandes() {
        return this.commmandes;
    }
    
    public void SaleProcess.setCommmandes(List<CommandeClient> commmandes) {
        this.commmandes = commmandes;
    }
    
    public LigneCmdClient SaleProcess.getLineToUpdate() {
        return this.lineToUpdate;
    }
    
    public void SaleProcess.setLineToUpdate(LigneCmdClient lineToUpdate) {
        this.lineToUpdate = lineToUpdate;
    }
    
    public List<LigneApprovisionement> SaleProcess.getProductResult() {
        return this.productResult;
    }
    
    public void SaleProcess.setProductResult(List<LigneApprovisionement> productResult) {
        this.productResult = productResult;
    }
    
    public Long SaleProcess.getClientId() {
        return this.clientId;
    }
    
    public void SaleProcess.setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public String SaleProcess.getCmdNumber() {
        return this.cmdNumber;
    }
    
    public void SaleProcess.setCmdNumber(String cmdNumber) {
        this.cmdNumber = cmdNumber;
    }
    
    public String SaleProcess.getClientName() {
        return this.clientName;
    }
    
    public void SaleProcess.setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public String SaleProcess.getTypeCommande() {
        return this.typeCommande;
    }
    
    public void SaleProcess.setTypeCommande(String typeCommande) {
        this.typeCommande = typeCommande;
    }
    
    public Date SaleProcess.getDateCommande() {
        return this.dateCommande;
    }
    
    public void SaleProcess.setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }
    
    public BigDecimal SaleProcess.getTotalCommande() {
        return this.totalCommande;
    }
    
    public void SaleProcess.setTotalCommande(BigDecimal totalCommande) {
        this.totalCommande = totalCommande;
    }
    
    public BigDecimal SaleProcess.getTotalRemise() {
        return this.totalRemise;
    }
    
    public void SaleProcess.setTotalRemise(BigDecimal totalRemise) {
        this.totalRemise = totalRemise;
    }
    
    public BigDecimal SaleProcess.getNetApayer() {
        return this.netApayer;
    }
    
    public void SaleProcess.setNetApayer(BigDecimal netApayer) {
        this.netApayer = netApayer;
    }
    
    public String SaleProcess.getDisplayName() {
        return this.displayName;
    }
    
    public void SaleProcess.setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public Ordonnancier SaleProcess.getOrdonnancier() {
        return this.ordonnancier;
    }
    
    public void SaleProcess.setOrdonnancier(Ordonnancier ordonnancier) {
        this.ordonnancier = ordonnancier;
    }
    
    public String SaleProcess.getDisplayOrdonance() {
        return this.displayOrdonance;
    }
    
    public void SaleProcess.setDisplayOrdonance(String displayOrdonance) {
        this.displayOrdonance = displayOrdonance;
    }
    
}
