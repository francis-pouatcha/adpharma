package org.adorsys.platform.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.json.RooJson;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Provider", finders = { "findProvidersByExchangeKeyEquals" })
public class Provider extends PlatformUser {

    public Provider(String firstName, String lastName, String address,
			String email, String site, String mobile, String phone, String fax,
			Gender gender, String exchangeKey, String taxPayersNumber, String name) {
		super(firstName, lastName, address, email, site, mobile, phone, fax, gender);
		
		this.exchangeKey= exchangeKey;
		this.taxPayersNumber= taxPayersNumber;
		this.name= name; 
	}
    
    public Provider(){
    	super();
    }
    
    
    
    @Transactional
	public void persist() {
		if (this.entityManager == null) this.entityManager = entityManager();
		this.entityManager.persist(this);
	}

	@Column(unique = true)
    private String exchangeKey;

    private String name;
    
   /* @OneToOne
    @NotNull
    private UserAccount account; */

    @Column(unique = true)
    private String taxPayersNumber;

    @PrePersist
    public void prePersist() {
        setPlatformUserType(PlatformUserType.PROVIDER);
        setUserType(UserType.CORPORATION);
        setGender(Gender.NEUTRAL);
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((taxPayersNumber == null) ? 0 : taxPayersNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Provider other = (Provider) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (taxPayersNumber == null) {
			if (other.taxPayersNumber != null)
				return false;
		} else if (!taxPayersNumber.equals(other.taxPayersNumber))
			return false;
		return true;
	}

	public String getExchangeKey() {
		return exchangeKey;
	}

	public void setExchangeKey(String exchangeKey) {
		this.exchangeKey = exchangeKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTaxPayersNumber() {
		return taxPayersNumber;
	}

	public void setTaxPayersNumber(String taxPayersNumber) {
		this.taxPayersNumber = taxPayersNumber;
	}
	
	
    
}
