package org.adorsys.adpharma.beans;

import java.math.BigInteger;

public class LigneApprovisionementExcelRepresentation {
	
	private String cip;
	
	private String cipm;
	
	private String designation;
	
	private BigInteger qteStock;
	
	private String emplacement;
	
	

	public LigneApprovisionementExcelRepresentation() {
		super();
	}

	public LigneApprovisionementExcelRepresentation(String cip, String cipm,
			String designation, BigInteger qteStock, String emplacement) {
		super();
		this.cip = cip;
		this.cipm = cipm;
		this.designation = designation;
		this.qteStock = qteStock;
		this.emplacement = emplacement;
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

	public String getEmplacement() {
		return emplacement;
	}

	public void setEmplacement(String emplacement) {
		this.emplacement = emplacement;
	}
	
	

}
