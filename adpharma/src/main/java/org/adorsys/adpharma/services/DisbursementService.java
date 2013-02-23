package org.adorsys.adpharma.services;

import java.math.BigDecimal;
import java.util.Date;

import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.OperationCaisse;
import org.adorsys.adpharma.domain.TypeOpCaisse;
import org.adorsys.adpharma.domain.TypePaiement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author hsimo
 *
 * Service des decaissements
 */
@Service
public class DisbursementService extends CashServiceAbstraction {
	
	private static final Logger LOGGER1= LoggerFactory.getLogger(DisbursementService.class);

	@Override
	public void generateCashOperation(Caisse cash, BigDecimal amount, String note) {
		OperationCaisse operationCaisse = new OperationCaisse();
		operationCaisse.setCaisse(cash);
		operationCaisse.setOperateur(SecurityUtil.getPharmaUser());
		operationCaisse.setDateOperation(new Date());
		operationCaisse.setTypeOperation(TypeOpCaisse.DECAISSEMENT);
		operationCaisse.setModePaiement(TypePaiement.CASH);
		operationCaisse.setMontant(amount);
        operationCaisse.setNote(note);
        operationCaisse.setRaisonOperation("Decaissement de: "+operationCaisse.getMontant()+ " Effectue le: "+operationCaisse.getDateOperation()+" Par: "+operationCaisse.getOperateur());
        operationCaisse.persist();
	}

	@Override
	public void updateTotalCash(Caisse cash, BigDecimal amount) {
		if(cash==null) throw new IllegalArgumentException("The cash argument is required");
		BigDecimal totalCash = cash.getTotalCash();
	  try {
		    cash.setTotalCash(totalCash.subtract(amount));
			cash.merge();
			LOGGER1.info("Sucess update of totalCash in disbursement...");
	    } catch (Exception e) {
		   LOGGER1.error("Error update of totalCash in disbursement, Reason: "+e.getCause());
	    }	
	}
	
	@Transactional(readOnly=true, isolation=Isolation.DEFAULT)
	public void makeDisbursement(Caisse cash, BigDecimal amount, String note){
		generateCashOperation(cash, amount, note);
		
		super.updateTotalDisbursement(cash, amount);
		
		updateTotalCash(cash, amount);
		System.out.println("Congratulations!!!");
	}

}
