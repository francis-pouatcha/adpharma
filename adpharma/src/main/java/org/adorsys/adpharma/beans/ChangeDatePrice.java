package org.adorsys.adpharma.beans;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

public class ChangeDatePrice {
	
	private String cipm;
	
	private String designation;
	
	private Bigdecimal prixActuel;
	
	private Bigdecimal nouveauPrix;
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date datePeremptionActuel;
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date nouvelDatePeremption;
	
	private boolean appliqueLeNouveauPrixATousLesCipmDeMemeCip =true;
	
	private boolean appliqueLaNouvelDateATousLesCipmDeMemeCip =true;

}
