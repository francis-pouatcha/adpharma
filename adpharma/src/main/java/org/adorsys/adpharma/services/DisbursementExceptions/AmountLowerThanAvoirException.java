package org.adorsys.adpharma.services.DisbursementExceptions;

import java.math.BigDecimal;

public class AmountLowerThanAvoirException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;
	
	private BigDecimal amount;
	
	private BigDecimal avoir;
	
	public AmountLowerThanAvoirException(BigDecimal amount, BigDecimal avoir, String message){
		this.amount= amount;
		this.avoir= avoir;
		this.message= message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

 	public BigDecimal getAvoir() {
		return avoir;
	}

	public void setAvoir(BigDecimal avoir) {
		this.avoir = avoir;
	}
	
	

}
