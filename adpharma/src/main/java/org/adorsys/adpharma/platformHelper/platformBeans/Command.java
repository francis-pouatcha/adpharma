package org.adorsys.adpharma.platformHelper.platformBeans;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import flexjson.JSONSerializer;

@RooJavaBean
@RooToString
@RooJson
public class Command {

    private DrugStore drugstore;

    private Provider provider;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date creationDate;

    private BigDecimal amountHT;

    private BigDecimal amountTTC;

    private BigDecimal amountTva;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
    private Date validationDate;

    @Column(unique = true)
    private String identifier;

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
}
