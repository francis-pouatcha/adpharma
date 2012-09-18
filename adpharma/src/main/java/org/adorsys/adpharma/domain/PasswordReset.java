package org.adorsys.adpharma.domain;

import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class PasswordReset {

	private String id;
	
	private String version;

	private String userName;

    private String currentPassword;

    private String newPassword;

    private String confirmNewPassword;
    
	public boolean passwordsEqual(){
		return StringUtils.equals(newPassword, confirmNewPassword);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
