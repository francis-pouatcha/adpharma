package org.adorsys.adpharma.platformHelper.platformBeans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
public class CommandItem {

    private int itemIndex;

    private String cip;

    private String designation;

    private String productId;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
    private Date creationDate;

    private BigInteger qtyOrdered;

    private BigInteger qtyProvided;

    private BigDecimal priceOrderd;

    private BigDecimal priceProvided;

    private Boolean isValid;

    private Command command;
}
