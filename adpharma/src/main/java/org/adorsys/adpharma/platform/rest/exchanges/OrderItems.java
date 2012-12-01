package org.adorsys.adpharma.platform.rest.exchanges;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

public class OrderItems {


    private int itemIndex;

    private String cip;

    @NotNull
    private String designation;
    
    @NotNull
    private String commandKey;

    private String productId;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
    private Date creationDate;

    @Value("0")
    private BigInteger qtyOrdered;

    private BigInteger qtyProvided;

    private BigDecimal priceOrderd;
    
    private BigDecimal priceProvided;

    private Boolean isValid;

     public OrderItems(){}
     
     public OrderItems(LigneCmdFournisseur item){
    	 setCip(item.getCip());
    	 setCommandKey(item.getCommande().getCmdNumber());
    	 setCreationDate(item.getDateSaisie());
    	 setDesignation(item.getDesignation());
    	 setIsValid(item.isValid());
    	 setItemIndex(item.getIndexLine());
    	 setPriceOrderd(item.getPrixAchatMin());
    	 setPriceProvided(item.getPrixFourniMin());
    	 setProductId("");
    	 setQtyOrdered(item.getQuantiteCommande());
    	 setQtyProvided(item.getQuantiteFournie());
     }
     
	public int getItemIndex() {
		return itemIndex;
	}

	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}

	public String getCip() {
		return cip;
	}

	public void setCip(String cip) {
		this.cip = cip;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public BigInteger getQtyOrdered() {
		return qtyOrdered;
	}

	public void setQtyOrdered(BigInteger qtyOrdered) {
		this.qtyOrdered = qtyOrdered;
	}

	public BigInteger getQtyProvided() {
		return qtyProvided;
	}

	public void setQtyProvided(BigInteger qtyProvided) {
		this.qtyProvided = qtyProvided;
	}

	public BigDecimal getPriceOrderd() {
		return priceOrderd;
	}

	public void setPriceOrderd(BigDecimal priceOrderd) {
		this.priceOrderd = priceOrderd;
	}

	public BigDecimal getPriceProvided() {
		return priceProvided;
	}

	public void setPriceProvided(BigDecimal priceProvided) {
		this.priceProvided = priceProvided;
	}

	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	public String getCommandKey() {
		return commandKey;
	}

	public void setCommandKey(String commandKey) {
		this.commandKey = commandKey;
	}
	
    
    
    
}
