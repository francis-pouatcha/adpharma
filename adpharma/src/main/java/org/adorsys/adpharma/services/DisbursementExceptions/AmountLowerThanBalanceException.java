package org.adorsys.adpharma.services.DisbursementExceptions;

import java.math.BigDecimal;

public class AmountLowerThanBalanceException extends Exception {
	

	private static final long serialVersionUID = 1L;

	private BigDecimal amount;
	
	private BigDecimal totalCash;
	
	private String message;
	
	public AmountLowerThanBalanceException(BigDecimal amount, BigDecimal totalCash, String message){
	    this.amount= amount;
	    this.totalCash= totalCash;
	    this.message=message;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public BigDecimal getTotalCash() {
		return totalCash;
	}
	
	public void setTotalCash(BigDecimal totalCash) {
		this.totalCash = totalCash;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

}
