// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.String;

privileged aspect LigneCmdFournisseur_Roo_ToString {
    
    public String LigneCmdFournisseur.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AgentSaisie: ").append(getAgentSaisie()).append(", ");
        sb.append("Archived: ").append(getArchived()).append(", ");
        sb.append("Cip: ").append(getCip()).append(", ");
        sb.append("Commande: ").append(getCommande()).append(", ");
        sb.append("DateSaisie: ").append(getDateSaisie()).append(", ");
        sb.append("Designation: ").append(getDesignation()).append(", ");
        sb.append("FootPrint: ").append(getFootPrint()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("IndexLine: ").append(getIndexLine()).append(", ");
        sb.append("PrixAVenteMin: ").append(getPrixAVenteMin()).append(", ");
        sb.append("PrixAchatMin: ").append(getPrixAchatMin()).append(", ");
        sb.append("PrixAchatTotal: ").append(getPrixAchatTotal()).append(", ");
        sb.append("PrixFourniMin: ").append(getPrixFourniMin()).append(", ");
        sb.append("Produit: ").append(getProduit()).append(", ");
        sb.append("QuantiteCommande: ").append(getQuantiteCommande()).append(", ");
        sb.append("QuantiteFournie: ").append(getQuantiteFournie()).append(", ");
        sb.append("Version: ").append(getVersion()).append(", ");
        sb.append("IsValid: ").append(isIsValid()).append(", ");
        sb.append("Valid: ").append(isValid());
        return sb.toString();
    }
    
}
