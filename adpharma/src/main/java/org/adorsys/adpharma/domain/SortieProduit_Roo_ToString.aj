// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.String;

privileged aspect SortieProduit_Roo_ToString {
    
    public String SortieProduit.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AgentNumber: ").append(getAgentNumber()).append(", ");
        sb.append("Aprovisionnement: ").append(getAprovisionnement()).append(", ");
        sb.append("CauseSortie: ").append(getCauseSortie()).append(", ");
        sb.append("Cip: ").append(getCip()).append(", ");
        sb.append("CipMaison: ").append(getCipMaison()).append(", ");
        sb.append("DateSaisie: ").append(getDateSaisie()).append(", ");
        sb.append("Designation: ").append(getDesignation()).append(", ");
        sb.append("FootPrint: ").append(getFootPrint()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("PrixAchat: ").append(getPrixAchat()).append(", ");
        sb.append("PrixTotal: ").append(getPrixTotal()).append(", ");
        sb.append("Quantite: ").append(getQuantite()).append(", ");
        sb.append("Version: ").append(getVersion());
        return sb.toString();
    }
    
}
