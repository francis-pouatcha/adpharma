package org.adorsys.adpharma.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

public class ProductsOut {
	
	private Long id;
	
	private String cip;
	
	private String numProduit;
	
	private String designation;
	
	private String cipm;
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date datePeremption;
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date dateRupture;
	
	private BigInteger qteStock;
	
	private BigDecimal paUnit;
	
	private BigDecimal pvUnit;
	
	public ProductsOut(){
		qteStock= BigInteger.ZERO;
		paUnit= BigDecimal.ZERO;
		pvUnit= BigDecimal.ZERO;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumProduit() {
		return numProduit;
	}

	public void setNumProduit(String numProduit) {
		this.numProduit = numProduit;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public BigInteger getQteStock() {
		return qteStock;
	}

	public void setQteStock(BigInteger qteStock) {
		this.qteStock = qteStock;
	}

	public BigDecimal getPaUnit() {
		return paUnit;
	}

	public void setPaUnit(BigDecimal paUnit) {
		this.paUnit = paUnit;
	}

	public BigDecimal getPvUnit() {
		return pvUnit;
	}

	public void setPvUnit(BigDecimal pvUnit) {
		this.pvUnit = pvUnit;
	}
	
	public String getCip() {
		return cip;
	}
	
	public void setCip(String cip) {
		this.cip = cip;
	}

	public String getCipm() {
		return cipm;
	}

	public void setCipm(String cipm) {
		this.cipm = cipm;
	}

	public Date getDatePeremption() {
		return datePeremption;
	}

	public void setDatePeremption(Date datePeremption) {
		this.datePeremption = datePeremption;
	}
	public Date getDateRupture() {
		return dateRupture;
	}

	public void setDateRupture(Date dateRupture) {
		this.dateRupture = dateRupture;
	}
	
	

}
