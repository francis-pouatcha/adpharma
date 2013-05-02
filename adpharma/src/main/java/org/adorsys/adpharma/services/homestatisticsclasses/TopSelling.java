package org.adorsys.adpharma.services.homestatisticsclasses;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TopSelling {
	
	 private BigInteger qteVendue;
	 
	 private BigDecimal prixTotal;
	 
	 private BigDecimal totalRemise;
	 
	 private BigDecimal prixUnitaire;
	 
	 private Long id;
	 
	 private String designation;
	 
	 private String cip;
	 
	 public TopSelling(){
		 prixTotal= BigDecimal.ZERO;
		 totalRemise= BigDecimal.ZERO;
		 qteVendue= BigInteger.ZERO;
	 }
	 

	public BigInteger getQteVendue() {
		return qteVendue;
	}

	public void setQteVendue(BigInteger qteVendue) {
		this.qteVendue = qteVendue;
	}

	public BigDecimal getPrixTotal() {
		return prixTotal;
	}

	public void setPrixTotal(BigDecimal prixTotal) {
		this.prixTotal = prixTotal;
	}

	public BigDecimal getTotalRemise() {
		return totalRemise;
	}

	public void setTotalRemise(BigDecimal totalRemise) {
		this.totalRemise = totalRemise;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getCip() {
		return cip;
	}

	public void setCip(String cip) {
		this.cip = cip;
	}
	
	
	 
	public BigDecimal getPrixUnitaire() {
		return prixUnitaire;
	}


	public void setPrixUnitaire(BigDecimal prixUnitaire) {
		this.prixUnitaire = prixUnitaire;
	}


	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}


	public String toString(){
		return new StringBuilder().append(getId()).append("-"+getDesignation()).append("-"+getCip()).append("-"+getQteVendue()).append("-"+getPrixUnitaire()).append("-"+getPrixTotal()).append("-"+getTotalRemise()).toString();
	}
	 
	

}
