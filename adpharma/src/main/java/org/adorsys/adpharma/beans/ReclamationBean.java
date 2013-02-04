package org.adorsys.adpharma.beans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.apache.commons.beanutils.converters.LongConverter;
import org.springframework.roo.addon.javabean.RooJavaBean;

import com.ibm.icu.math.BigDecimal;


/**
 * Used to encapsulate the list of reclamations
 * @author hsimo
 *
 */

public class ReclamationBean {
	
	private String provider;
	
	private String bordereauNumber;
	
	private String approNumber;
	
	private String lotNumber;
	
	private String cipm;
	
	private BigInteger qteStock=BigInteger.ZERO;
	
	private String cip;
	
	private BigInteger reclamQuantity= BigInteger.ZERO;
	
	private BigInteger returnQuantity= BigInteger.ZERO;
	
	private String designation;
	
	private BigDecimal prixAchat=BigDecimal.ZERO;
	
	private Date reclamationDate;
	
	public BigInteger getReturnQuantity() {
		return returnQuantity;
	}
	
	public void setReturnQuantity(BigInteger returnQuantity) {
		this.returnQuantity = returnQuantity;
	}
	
	public String getCip() {
		return cip;
	}
	
	public void setCip(String cip) {
		this.cip = cip;
	}
	
	public BigInteger getQteStock() {
		return qteStock;
	}
	
	public void setQteStock(BigInteger qteStock) {
		this.qteStock = qteStock;
	}
	
	public String getProvider() {
		return provider;
	}
	
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public String getBordereauNumber() {
		return bordereauNumber;
	}
	
	public void setBordereauNumber(String bordereauNumber) {
		this.bordereauNumber = bordereauNumber;
	}
	
	public String getDesignation() {
		return designation;
	}
	
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	
	public String getApproNumber() {
		return approNumber;
	}
	
	public void setApproNumber(String approNumber) {
		this.approNumber = approNumber;
	}
	
	public String getLotNumber() {
		return lotNumber;
	}
	
	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}
	
	public BigDecimal getPrixAchat() {
		return prixAchat;
	}
	
	public void setPrixAchat(BigDecimal prixAchat) {
		this.prixAchat = prixAchat;
	}
	
	public BigInteger getReclamQuantity() {
		return reclamQuantity;
	}
	
	public void setReclamQuantity(BigInteger reclamQuantity) {
		this.reclamQuantity = reclamQuantity;
	}
	
	public Date getReclamationDate() {
		return reclamationDate;
	}
	
	public void setReclamationDate(Date reclamationDate) {
		this.reclamationDate = reclamationDate;
	}
	
	public String getCipm() {
		return cipm;
	}
	
	public void setCipm(String cipm) {
		this.cipm = cipm;
	}
	
	
	// Used to prepare the list of preparations
	public static List<ReclamationBean> prepareReclamations(List<Approvisionement> approvisionements){
		List<ReclamationBean> liste= new ArrayList<ReclamationBean>();
		for(Approvisionement approvisionement:approvisionements){
			Set<LigneApprovisionement> lignes = approvisionement.getLigneApprivisionement();
			for(LigneApprovisionement ligne:lignes){
			if(ligne.getQuantiteReclame().intValue()!=0){	
				ReclamationBean reclam = new ReclamationBean();
				reclam.setApproNumber(ligne.getApprovisionement().getApprovisionementNumber());
				reclam.setBordereauNumber(ligne.getApprovisionement().getBordereauNumber());
				reclam.setDesignation(ligne.getDesignation());
				reclam.setCipm(ligne.getCipMaison());
				reclam.setQteStock(ligne.getQuantieEnStock());
				reclam.setPrixAchat(new BigDecimal(ligne.getPrixAchatUnitaire()));
			    reclam.setReclamQuantity(ligne.getQuantiteReclame());
				reclam.setReclamationDate(ligne.getApprovisionement().getDateCreation());
				reclam.setProvider(ligne.getApprovisionement().getFounisseur().getName());
			    liste.add(reclam);
			}
		}
	}
		return liste;
	}
	
	

}
