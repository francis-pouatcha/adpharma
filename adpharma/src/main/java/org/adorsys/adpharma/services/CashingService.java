package org.adorsys.adpharma.services;

import java.math.BigDecimal;

import org.adorsys.adpharma.domain.Caisse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * 
 * @author hsimo
 *
 * Service des encaissements
 */

@Service
public class CashingService extends CashServiceAbstraction {
	
	private static final Logger LOGGER2= LoggerFactory.getLogger(CashingService.class);

	@Override
	public void generateCashOperation(Caisse cash, BigDecimal amount, String note) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTotalCash(Caisse cash, BigDecimal amount) {
		if(cash==null) throw new IllegalArgumentException("The cash argument is required");
		BigDecimal totalCash = cash.getTotalCash();
		try {
			cash.setTotalCash(totalCash.add(amount));
			cash.merge();
			LOGGER2.info("Sucess update of totalCash in Cashing...");
		} catch (Exception e) {
			LOGGER2.error("Error update of totalCash in Cashing, Reason: "+e.getCause());
		}
	}

}
