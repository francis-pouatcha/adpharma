package org.adorsys.adpharma.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.beans.factory.annotation.Value;

@RooJavaBean
@RooToString
@RooEntity
public class Configuration {

	@Value("true")
	private Boolean saleForce;

	@Value("false")
	private Boolean closeCashUnpaySale;

	@Value("true")
	private Boolean closeCancelSale;

	@Value("true")
	private Boolean restoreCancelSale;

	@Value("false")
	private Boolean saleCash;
	
	@Value("false")
	private Boolean editCloseSupply;
	
	@Value("false")
	private Boolean enableLicence;
	
	@Value("false")
	private Boolean onlySaleOld;
	
	@Value("false")
	private Boolean forceToSameLine;
	
	@Value("false")
	private Boolean onlyCashReceiveCreditPay;
	
	private String receptionFolder ;
	
	private String sendFolder ;


	public static void init(){
		if(Configuration.countConfigurations() <= 0){
			Configuration configuration = new Configuration();
			configuration.persist();
		}
	}


	public Boolean getSaleCash() {
		return saleCash;
	}


	public void setSaleCash(Boolean saleCash) {
		this.saleCash = saleCash;
	}


	public Boolean getEditCloseSupply() {
		return editCloseSupply;
	}


	public void setEditCloseSupply(Boolean editCloseSupply) {
		this.editCloseSupply = editCloseSupply;
	}


	public Boolean getEnableLicence() {
		return enableLicence;
	}


	public void setEnableLicence(Boolean enableLicence) {
		this.enableLicence = enableLicence;
	}


	public Boolean getOnlySaleOld() {
		return onlySaleOld;
	}


	public void setOnlySaleOld(Boolean onlySaleOld) {
		this.onlySaleOld = onlySaleOld;
	}


	public Boolean getForceToSameLine() {
		return forceToSameLine;
	}


	public void setForceToSameLine(Boolean forceToSameLine) {
		this.forceToSameLine = forceToSameLine;
	}


	public String getReceptionFolder() {
		return receptionFolder;
	}


	public void setReceptionFolder(String receptionFolder) {
		this.receptionFolder = receptionFolder;
	}


	public String getSendFolder() {
		return sendFolder;
	}


	public void setSendFolder(String sendFolder) {
		this.sendFolder = sendFolder;
	}


	public Boolean getOnlyCashReceiveCreditPay() {
		return onlyCashReceiveCreditPay;
	}


	public void setOnlyCashReceiveCreditPay(Boolean onlyCashReceiveCreditPay) {
		this.onlyCashReceiveCreditPay = onlyCashReceiveCreditPay;
	}


	
	





}
