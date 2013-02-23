package org.adorsys.adpharma.services;

import java.math.BigDecimal;

import org.adorsys.adpharma.domain.Caisse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author hsimo
 *
 *  Classe d'abstraction des services d'encaissement et de decaissement
 */
public abstract class CashServiceAbstraction implements CashServiceInterface{
	
	private static final Logger LOGS= LoggerFactory.getLogger(CashServiceAbstraction.class);
		
	
	public void updateTotalCashing(Caisse cash, BigDecimal amount){
		if(cash==null) throw new IllegalArgumentException("The cash argument is required");
		BigDecimal totalEncaissement = cash.getTotalEncaissement();
		try {
			cash.setTotalEncaissement(totalEncaissement.add(amount));
			cash.merge();
			LOGS.info("Sucess update of totalEncaissement...");
		} catch (Exception e) {
			LOGS.error("Error update of totalEncaissement, Reason: "+e.getCause());
		}
	}
	
	public void updateTotalDisbursement(Caisse cash, BigDecimal amount){
		if(cash==null) throw new IllegalArgumentException("The cash argument is required");
		BigDecimal totalRetrait = cash.getTotalRetrait();
		try {
			 cash.setTotalRetrait(totalRetrait.add(amount));
			 cash.merge();
			 LOGS.info("Sucess update of totalRetrait...");
		} catch (Exception e) {
			LOGS.error("Error update of totalDecaissement, Reason: "+e.getCause());
		}
		
	}
	
	// List of Common methods 
	public abstract void generateCashOperation(Caisse cash, BigDecimal amount, String note);
	
	public abstract void updateTotalCash(Caisse cash, BigDecimal amount);

}
