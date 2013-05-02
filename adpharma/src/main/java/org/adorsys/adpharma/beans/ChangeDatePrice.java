package org.adorsys.adpharma.beans;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;


public class ChangeDatePrice {
	
	private String cipm;
	
	private String designation;
	
	private BigDecimal prixActuel;
	
	private BigDecimal nouveauPrix;
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date datePeremptionActuel;
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date nouvelDatePeremption;
	
	private boolean appliqueLeNouveauPrixATousLesCipmDeMemeCip =true;
	
	private boolean appliqueLaNouvelDateATousLesCipmDeMemeCip =true;

	public String getCipm() {
		return cipm;
	}

	public void setCipm(String cipm) {
		this.cipm = cipm;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public BigDecimal getPrixActuel() {
		return prixActuel;
	}

	public void setPrixActuel(BigDecimal prixActuel) {
		this.prixActuel = prixActuel;
	}

	public BigDecimal getNouveauPrix() {
		return nouveauPrix;
	}

	public void setNouveauPrix(BigDecimal nouveauPrix) {
		this.nouveauPrix = nouveauPrix;
	}

	public Date getDatePeremptionActuel() {
		return datePeremptionActuel;
	}

	public void setDatePeremptionActuel(Date datePeremptionActuel) {
		this.datePeremptionActuel = datePeremptionActuel;
	}

	public Date getNouvelDatePeremption() {
		return nouvelDatePeremption;
	}

	public void setNouvelDatePeremption(Date nouvelDatePeremption) {
		this.nouvelDatePeremption = nouvelDatePeremption;
	}

	public boolean isAppliqueLeNouveauPrixATousLesCipmDeMemeCip() {
		return appliqueLeNouveauPrixATousLesCipmDeMemeCip;
	}

	public void setAppliqueLeNouveauPrixATousLesCipmDeMemeCip(
			boolean appliqueLeNouveauPrixATousLesCipmDeMemeCip) {
		this.appliqueLeNouveauPrixATousLesCipmDeMemeCip = appliqueLeNouveauPrixATousLesCipmDeMemeCip;
	}

	public boolean isAppliqueLaNouvelDateATousLesCipmDeMemeCip() {
		return appliqueLaNouvelDateATousLesCipmDeMemeCip;
	}

	public void setAppliqueLaNouvelDateATousLesCipmDeMemeCip(
			boolean appliqueLaNouvelDateATousLesCipmDeMemeCip) {
		this.appliqueLaNouvelDateATousLesCipmDeMemeCip = appliqueLaNouvelDateATousLesCipmDeMemeCip;
	}
	
	

}
