// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.String;

privileged aspect Paiement_Roo_ToString {
    
    public String Paiement.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AvancePartClient: ").append(getAvancePartClient()).append(", ");
        sb.append("Caisse: ").append(getCaisse()).append(", ");
        sb.append("Caisses: ").append(getCaisses()).append(", ");
        sb.append("Cassier: ").append(getCassier()).append(", ");
        sb.append("Compenser: ").append(getCompenser()).append(", ");
        sb.append("DatePaiement: ").append(getDatePaiement()).append(", ");
        sb.append("DateSaisie: ").append(getDateSaisie()).append(", ");
        sb.append("Difference: ").append(getDifference()).append(", ");
        sb.append("Facture: ").append(getFacture()).append(", ");
        sb.append("FootPrint: ").append(getFootPrint()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Informations: ").append(getInformations()).append(", ");
        sb.append("Montant: ").append(getMontant()).append(", ");
        sb.append("MontantBon: ").append(getMontantBon()).append(", ");
        sb.append("NomClient: ").append(getNomClient()).append(", ");
        sb.append("NumeroBon: ").append(getNumeroBon()).append(", ");
        sb.append("PaiementNumber: ").append(getPaiementNumber()).append(", ");
        sb.append("PayerPar: ").append(getPayerPar()).append(", ");
        sb.append("QuiPaye: ").append(getQuiPaye()).append(", ");
        sb.append("Reduction: ").append(getReduction()).append(", ");
        sb.append("RestePartClient: ").append(getRestePartClient()).append(", ");
        sb.append("Site: ").append(getSite()).append(", ");
        sb.append("SommeRecue: ").append(getSommeRecue()).append(", ");
        sb.append("TicketImprimer: ").append(getTicketImprimer()).append(", ");
        sb.append("TypePaiement: ").append(getTypePaiement()).append(", ");
        sb.append("Version: ").append(getVersion());
        return sb.toString();
    }
    
}
