// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.String;

privileged aspect DetteClient_Roo_ToString {
    
    public String DetteClient.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Annuler: ").append(getAnnuler()).append(", ");
        sb.append("Assurer: ").append(getAssurer()).append(", ");
        sb.append("Avance: ").append(getAvance()).append(", ");
        sb.append("ClientId: ").append(getClientId()).append(", ");
        sb.append("ClientName: ").append(getClientName()).append(", ");
        sb.append("ClientNo: ").append(getClientNo()).append(", ");
        sb.append("DateCreation: ").append(getDateCreation()).append(", ");
        sb.append("DateDernierVersement: ").append(getDateDernierVersement()).append(", ");
        sb.append("DatePaiement: ").append(getDatePaiement()).append(", ");
        sb.append("EndDate: ").append(getEndDate()).append(", ");
        sb.append("EtatCredit: ").append(getEtatCredit()).append(", ");
        sb.append("FactureId: ").append(getFactureId()).append(", ");
        sb.append("FactureNo: ").append(getFactureNo()).append(", ");
        sb.append("FootPrint: ").append(getFootPrint()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("MontantInitial: ").append(getMontantInitial()).append(", ");
        sb.append("NumBon: ").append(getNumBon()).append(", ");
        sb.append("PartAssure: ").append(getPartAssure()).append(", ");
        sb.append("Reste: ").append(getReste()).append(", ");
        sb.append("Solder: ").append(getSolder()).append(", ");
        sb.append("SousTotal: ").append(getSousTotal()).append(", ");
        sb.append("Taux: ").append(getTaux()).append(", ");
        sb.append("TauxAssure: ").append(getTauxAssure()).append(", ");
        sb.append("Version: ").append(getVersion());
        return sb.toString();
    }
    
}
