// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.String;

privileged aspect LicenceEntity_Roo_ToString {
    
    public String LicenceEntity.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BeginDate: ").append(getBeginDate()).append(", ");
        sb.append("EndDate: ").append(getEndDate()).append(", ");
        sb.append("GenerateKey: ").append(getGenerateKey()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("IsValid: ").append(getIsValid()).append(", ");
        sb.append("TranstientBegin: ").append(getTranstientBegin()).append(", ");
        sb.append("TranstientEnd: ").append(getTranstientEnd()).append(", ");
        sb.append("Version: ").append(getVersion()).append(", ");
        return sb.toString();
    }
    
}
