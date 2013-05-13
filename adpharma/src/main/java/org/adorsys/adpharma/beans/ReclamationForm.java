package org.adorsys.adpharma.beans;

/**
 * Used to encapsulate data of reclamation form
 * @author hsimo
 *
 */
public class ReclamationForm {
	
	private String provider_id;
	
	private String date_min;
	
	private String date_max;
	
	public String getProvider_id() {
		return provider_id;
	}
	
	public void setProvider_id(String provider_id) {
		this.provider_id = provider_id;
	}
	
	public String getDate_min() {
		return date_min;
	}
	
	public void setDate_min(String date_min) {
		this.date_min = date_min;
	}
	
	public String getDate_max() {
		return date_max;
	}
	
	public void setDate_max(String date_max) {
		this.date_max = date_max;
	}

}
