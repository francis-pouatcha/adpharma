package org.adorsys.adpharma.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.IdGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.type.TrueFalseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooEntity(identifierType = String.class, mappedSuperclass = true)
@Configurable
public abstract class AdPharmaBaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	protected  Long id;
	
	protected Boolean archived = Boolean.FALSE ;

	@Embedded
	protected  FootPrint footPrint = new FootPrint();

	@PrePersist
	public void prePersist(){
		//id=IdGenerator.generateId();
		internalPrePersist();
		initFootPrint();
	}

	@PostPersist
	public void postPersist(){
		internalPostPersit();
	}

	@PreUpdate
	public void preUpdate(){
		internalPreUpdate();    	
	}

	// use for protect somme field before update
	public void protectSomeField(){

	}

	protected void gatherFootPrint() {
		FootPrint footPrint2 = new FootPrint();
		footPrint2.setCreated(footPrint.getCreated());
		footPrint2.setModified(new Date());
		footPrint2.setCreateUser(footPrint.getCreateUser());
		footPrint2.setModifyingUser( SecurityUtil.getUserName());
		footPrint2.setBusinessName(footPrint.getBusinessName());
		footPrint2.setBusinessKey(footPrint.getBusinessKey());
		footPrint2.setTechId(footPrint.getTechId());
		//ChangeHistory changeHistory = new ChangeHistory();
		//changeHistory.setFootPrint(this.footPrint);
		//changeHistory.persist();
		this.footPrint = footPrint2;
	}

	private void initFootPrint(){
		String userName = SecurityUtil.getUserName();
		if(userName==null){
			footPrint.setModifyingUser("System");
			footPrint.setCreateUser("System");
		} else {
			footPrint.setModifyingUser(userName);
			footPrint.setCreateUser(userName);

		}
		footPrint.setCreated(new Date());
		footPrint.setModified(new Date());
		footPrint.setBusinessKey(getBusinessKey());
		footPrint.setBusinessName(getClass().getSimpleName());
	}

	protected String getBusinessKey(){
		return "";
	}

	protected void internalPreUpdate() {

	}

	protected void internalPostPersit() {

	}

	/**
	 * Implement this for pre persist in subclasses
	 */
	protected void internalPrePersist(){

	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + getBusinessKey();
	}
	// custum merge method 
	@Transactional
	public AdPharmaBaseEntity merge() {
		protectSomeField() ;
		gatherFootPrint();
		if (this.entityManager == null) this.entityManager = entityManager();
		AdPharmaBaseEntity merged = this.entityManager.merge(this);
		this.entityManager.flush();
		return merged;
	}
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		AdPharmaBaseEntity other = (AdPharmaBaseEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Boolean getArchived() {
		return archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}


}
