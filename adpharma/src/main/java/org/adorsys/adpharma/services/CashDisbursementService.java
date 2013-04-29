package org.adorsys.adpharma.services;

import java.math.BigDecimal;

import org.adorsys.adpharma.beans.process.CashDisbursementBean;
import org.adorsys.adpharma.domain.Caisse;


/**
 * 
 * @author hsimo
 * @author Clovis gakam
 *
 */
public interface CashDisbursementService {
	
	// Generer operation de caisse(encaissement, decaissement)
	public void generateCashDisbursementOperation(Caisse cash,CashDisbursementBean disbursementBean);
	public void processDisbursement(Caisse cash,CashDisbursementBean disbursementBean) ;
}
