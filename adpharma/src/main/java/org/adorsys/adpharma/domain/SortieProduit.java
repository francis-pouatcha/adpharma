package org.adorsys.adpharma.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.validation.constraints.Size;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "SortieProduit")
public class SortieProduit extends AdPharmaBaseEntity {

    @NotNull
    private String aprovisionnement;

    @NotNull
    private String cipMaison;

    @NotNull
    private String cip;

    private String designation;

    private BigDecimal prixAchat;

    private BigInteger quantite;

    private BigDecimal prixTotal;

    @Size(max = 255)
    private String causeSortie;

    private String agentNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date dateSaisie;
}
