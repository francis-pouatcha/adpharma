// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.String;

privileged aspect ModeConditionement_Roo_ToString {
    
    public String ModeConditionement.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FootPrint: ").append(getFootPrint()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Libelle: ").append(getLibelle()).append(", ");
        sb.append("LibelleCourt: ").append(getLibelleCourt()).append(", ");
        sb.append("Version: ").append(getVersion());
        return sb.toString();
    }
    
}
