package org.adorsys.adpharma.beans;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

public class EtatCreditFinder {
	
	private String clientName ;
	
	private String EtatNumber ;
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
	private Date dateEdition ;
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
	private Date datePaiement;
	
	private Boolean solder ;
	
	private Boolean annuler ;
	
	private Boolean encaisser ;
	
	
	

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getEtatNumber() {
		return EtatNumber;
	}

	public void setEtatNumber(String etatNumber) {
		EtatNumber = etatNumber;
	}

	public Date getDateEdition() {
		return dateEdition;
	}

	public void setDateEdition(Date dateEdition) {
		this.dateEdition = dateEdition;
	}

	public Date getDatePaiement() {
		return datePaiement;
	}

	public void setDatePaiement(Date datePaiement) {
		this.datePaiement = datePaiement;
	}

	public Boolean getSolder() {
		return solder;
	}

	public void setSolder(Boolean solder) {
		this.solder = solder;
	}

	public Boolean getAnnuler() {
		return annuler;
	}

	public void setAnnuler(Boolean annuler) {
		this.annuler = annuler;
	}

	public Boolean getEncaisser() {
		return encaisser;
	}

	public void setEncaisser(Boolean encaisser) {
		this.encaisser = encaisser;
	}
	

}
