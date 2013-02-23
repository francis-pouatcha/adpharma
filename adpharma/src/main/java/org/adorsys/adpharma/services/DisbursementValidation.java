package org.adorsys.adpharma.services;

import org.adorsys.adpharma.beans.Decaissement;
import org.springframework.validation.BindingResult;

public interface DisbursementValidation {
	
	public void validateDisbursement(BindingResult error, Decaissement decaissement);

}
