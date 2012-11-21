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


	
	





}
