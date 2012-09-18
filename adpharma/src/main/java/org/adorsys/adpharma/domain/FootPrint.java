package org.adorsys.adpharma.domain;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@Embeddable
public class FootPrint {
	@NotNull
    @Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date created = new Date();

	@NotNull
    @Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date modified =  new Date();

    @NotNull
    private String modifyingUser;
    
    @NotNull
    private String CreateUser ;
    

    private String businessKey;

    private String businessName;

    private Long techId;
    

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public String getModifyingUser() {
		return modifyingUser;
	}

	public void setModifyingUser(String modifyingUser) {
		this.modifyingUser = modifyingUser;
	}

	

	public String getCreateUser() {
		return CreateUser;
	}

	public void setCreateUser(String createUser) {
		CreateUser = createUser;
	}
	
	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Created: ").append(PharmaDateUtil.format(created, PharmaDateUtil.DATETIME_PATTERN_LONG)).append(", ");
        sb.append("CreateUser: ").append(getCreateUser()).append(", ");
        sb.append("Modified: ").append(PharmaDateUtil.format(modified, PharmaDateUtil.DATETIME_PATTERN_LONG)).append(", ");
        sb.append("ModifyingUser: ").append(getModifyingUser());
        return sb.toString();
    }

	
	  
   
	
	
      
    
}
