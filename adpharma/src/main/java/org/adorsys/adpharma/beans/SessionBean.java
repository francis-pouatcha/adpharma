package org.adorsys.adpharma.beans;

import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Site;
/**
 * use to hold user session informtions
 */
public class SessionBean {
	
	private Long connectedSiteId  ;
	
	public Long getConnectedSiteId() {
		return connectedSiteId;
	}

	/**
	 * @param connectedSiteId
	 */
	public void setConnectedSiteId(Long connectedSiteId) {
		this.connectedSiteId = connectedSiteId;
	}

	private String connectedSiteName ;

	/**
	 * @return
	 */
	public String getConnectedSiteName() {
		return connectedSiteName;
	}

	public void setConnectedSiteName(String connectedSiteName) {
		this.connectedSiteName = connectedSiteName;
	}
	
	public SessionBean(){};
	
	public SessionBean(Long connectedSiteId,String connectedSiteName){
		
	}
	
	public SessionBean(Site site){
		connectedSiteId = site.getId();
		connectedSiteName = site.getDisplayName();
	};
	
	public boolean isAbleToConnect(PharmaUser user){
		if(user ==null) return false;
		return user.getOffice().getId() == connectedSiteId ;
	}
}
