package org.adorsys.adpharma.domain;

import java.math.BigInteger;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.adorsys.adpharma.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "LigneFacture")
@RooJson
public class LigneFacture extends AdPharmaBaseEntity {

    private int indexline;

    private String cip;

    private String designation;

    private String cipM;

    private BigInteger qteAchete;

    private BigInteger qteRetourne = BigInteger.ZERO;

    private BigInteger prixUnitaire;

    @Value("0")
    private BigInteger remise;

    private BigInteger prixTotal;

    public void protectSomeField() {
        LigneFacture ligneFacture = LigneFacture.findLigneFacture(id);
        facture = ligneFacture.getFacture();
    }

    @NotNull
    @ManyToOne
    private Facture facture;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cip: ").append(getCip()).append(", ");
        sb.append("CipM: ").append(getCipM()).append(", ");
        sb.append("Des: ").append(getDesignation()).append(", ");
        sb.append("PrixU: ").append(getPrixUnitaire()).append(", ");
        sb.append("qteA: ").append(getQteAchete()).append(", ");
        sb.append("PrixT: ").append(getPrixTotal()).append(", ");
        sb.append("Rem: ").append(getRemise()).append("\n ");
        return sb.toString();
    }
}
