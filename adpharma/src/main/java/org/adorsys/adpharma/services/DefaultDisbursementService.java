package org.adorsys.adpharma.services;

import java.math.BigDecimal;
import java.util.Date;

import org.adorsys.adpharma.beans.process.CashDisbursementBean;
import org.adorsys.adpharma.domain.AvoirClient;
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
public class DefaultDisbursementService implements CashDisbursementService {
	
	@Override
	public void generateCashDisbursementOperation(Caisse cash,
			CashDisbursementBean disbursementBean) {
		OperationCaisse operationCaisse = new OperationCaisse();
		operationCaisse.setCaisse(cash);
		operationCaisse.setOperateur(SecurityUtil.getPharmaUser());
		operationCaisse.setDateOperation(new Date());
		operationCaisse.setTypeOperation(TypeOpCaisse.DECAISSEMENT);
		operationCaisse.setModePaiement(TypePaiement.CASH);
		operationCaisse.setMontant(disbursementBean.getAmount());
        operationCaisse.setGiveTo(disbursementBean.getGiveTo());
        operationCaisse.setOrderBy(disbursementBean.getOrderBy());
        operationCaisse.setNote(disbursementBean.getNote());
        if(disbursementBean.isHaveDisbursement()){
        	operationCaisse.setHavenumber(disbursementBean.getHaveNumber());
        }
        operationCaisse.setRaisonOperation(disbursementBean.getRaison().name());
        operationCaisse.persist();
		
	}


    @Transactional
	@Override
	public void processDisbursement(Caisse cash,
			CashDisbursementBean disbursementBean) {
		if(disbursementBean.isHaveDisbursement()){
			AvoirClient concernHave = disbursementBean.getConcernHave();
			concernHave.avancer(disbursementBean.getAmount());
			concernHave.merge();
		}
		cash.updateRetrait( disbursementBean.getAmount());
		generateCashDisbursementOperation(cash, disbursementBean);
		cash.merge();
		
	}



	

	
}
