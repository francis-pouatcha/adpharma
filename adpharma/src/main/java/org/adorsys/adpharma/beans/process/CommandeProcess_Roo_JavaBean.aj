// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.beans.process;

import java.lang.Long;
import java.lang.String;
import java.util.List;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;

privileged aspect CommandeProcess_Roo_JavaBean {
    
    public Long CommandeProcess.getCmdId() {
        return this.cmdId;
    }
    
    public void CommandeProcess.setCmdId(Long cmdId) {
        this.cmdId = cmdId;
    }
    
    public Produit CommandeProcess.getProduit() {
        return this.produit;
    }
    
    public void CommandeProcess.setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public String CommandeProcess.getFournisseur() {
        return this.fournisseur;
    }
    
    public void CommandeProcess.setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }
    
    public LigneCmdFournisseur CommandeProcess.getLineToUpdate() {
        return this.lineToUpdate;
    }
    
    public void CommandeProcess.setLineToUpdate(LigneCmdFournisseur lineToUpdate) {
        this.lineToUpdate = lineToUpdate;
    }
    
    public List<LigneCmdFournisseur> CommandeProcess.getProductSelected() {
        return this.productSelected;
    }
    
    public void CommandeProcess.setProductSelected(List<LigneCmdFournisseur> productSelected) {
        this.productSelected = productSelected;
    }
    
    public List<Produit> CommandeProcess.getProductResult() {
        return this.productResult;
    }
    
    public void CommandeProcess.setProductResult(List<Produit> productResult) {
        this.productResult = productResult;
    }
    
    public String CommandeProcess.getDisplayName() {
        return this.displayName;
    }
    
    public void CommandeProcess.setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public PharmaUser CommandeProcess.getCommandAgent() {
        return this.commandAgent;
    }
    
    public void CommandeProcess.setCommandAgent(PharmaUser commandAgent) {
        this.commandAgent = commandAgent;
    }
    
}
