package org.adorsys.adpharma.beans;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.adorsys.adpharma.domain.AvoirClient;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.TypeDecaissement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.services.DisbursementExceptions.AmountLowerThanAvoirException;
import org.adorsys.adpharma.services.DisbursementExceptions.AmountLowerThanBalanceException;
import org.adorsys.adpharma.services.DisbursementExceptions.OpenCashException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/**
 * 
 * @author hsimo
 *
 */

@RooJavaBean
public class Decaissement{
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateDecaissement;
	
	private String typeDecaissement;
	
	private BigDecimal montantDecaissement;
	
	private PharmaUser operateur;
	
	private String numAvoir;
	
	private String note;
	
	public Decaissement(){
		dateDecaissement= new Date();
		operateur= SecurityUtil.getPharmaUser();
		numAvoir= "";
	}
	
	// Verifier si la caisse de l'utilisateur en cours est ouverte
	public static Caisse verifyOpenCash(Decaissement decaissement){
		  PharmaUser user = decaissement.getOperateur();
		  List<Caisse> caisses = Caisse.findCaissesByCaisseOuverteAndCaissier(true, user).getResultList();
		  if(!caisses.isEmpty()) return caisses.iterator().next();
		  else return null;
	}
	
	// Verification du montant du decaissement
	public static Caisse verifyAmount(Decaissement decaissement) throws OpenCashException, AmountLowerThanAvoirException, AmountLowerThanBalanceException{
		BigDecimal montant = decaissement.getMontantDecaissement();
		BigDecimal totalCash;
		AvoirClient avoir=null;
		BigDecimal montantAvoir=null;
		
		if(decaissement.getNumAvoir()!=""){
		  avoir = AvoirClient.findAvoirClientsByNumeroEquals(decaissement.getNumAvoir()).getSingleResult();
		  montantAvoir = avoir.getMontant();
		}
		Caisse cash = verifyOpenCash(decaissement);
		if(cash==null) throw new OpenCashException("Vous n'avez aucune caisse ouverte...");
	    totalCash=cash.getTotalCash();
		System.out.println("Cash: "+totalCash);

		
		if(decaissement.getTypeDecaissement().equals(TypeDecaissement.DEPENSES_COURANTES.toString())){
				if(totalCash.intValue()<montant.intValue()) 
					throw new AmountLowerThanBalanceException(montant, totalCash, "Le Montant cash de la caisse: "+totalCash+" est inferieur au montant de decaissement: "+montant);
		}
		if(decaissement.getTypeDecaissement().equals(TypeDecaissement.CONSOMMATION_AVOIR.toString())){
			if(totalCash.intValue()<montant.intValue()) 
				throw new AmountLowerThanBalanceException(montant, totalCash, "Le Montant cash de la caisse: "+totalCash+" est inferieur au montant de decaissement: "+montant);
			
			if(montant.intValue()>montantAvoir.intValue()) 
				    throw new AmountLowerThanAvoirException(montant, montantAvoir, "Le montant de decaissement: "+montant+" est superieur au montant de l'avoir: "+montantAvoir);
		}
		
		return cash;
	}
	
	
	// Validation globale
	public static Caisse validateDisbursement(BindingResult error, Decaissement decaissement) {
		Caisse caisse=null;
		try {
		  caisse = verifyAmount(decaissement);
		}catch (OpenCashException e) {
			error.addError(new ObjectError("Error", e.getMessage()));
			System.out.println("Erreur: "+e.getMessage());
		}catch (AmountLowerThanBalanceException e) {
			error.addError(new ObjectError("Error", e.getMessage()));
			System.out.println("Erreur: "+e.getMessage());
		}catch (AmountLowerThanAvoirException e) {
			error.addError(new ObjectError("Error", e.getMessage()));
			System.out.println("Erreur: "+e.getMessage());
		}
		
		return caisse;
	}
	
	

}
