package org.adorsys.adpharma.services;

import java.math.BigDecimal;

import org.adorsys.adpharma.domain.Caisse;


/**
 * 
 * @author hsimo
 *
 */
public interface CashServiceInterface {
	
	// Generer operation de caisse(encaissement, decaissement)
	public void generateCashOperation(Caisse cash, BigDecimal amount, String note);
	
	// Mettre a jour le total du cash(encaissement, decaissement)
	public void updateTotalCash(Caisse cash, BigDecimal amount);

	// Mise a jour du total des encaissements
	public void updateTotalCashing(Caisse cash, BigDecimal amount);
	
	// Mise a jour du total des decaissements
	public void updateTotalDisbursement(Caisse cash, BigDecimal amount);
	
	
	
	
	
	

}
