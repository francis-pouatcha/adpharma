package org.adorsys.adpharma.domain;

import org.adorsys.adpharma.utils.NumberGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.PostPersist;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "FamilleProduit")
public class FamilleProduit extends AdPharmaBaseEntity {

    private String familleNumber;
    @NotNull
    private String libelleFamille;

    private String libelleCourt;

    @Size(max = 256)
    private String note;
    
    @Override
    protected void internalPrePersist() {

    }
    
    @PostPersist
    public void postPersit(){
    	familleNumber = NumberGenerator.getNumber("FP-", getId(), 4);


    }
    
    @Override
    protected void internalPreUpdate() {
    }
    @Override
    public void protectSomeField(){
    	FamilleProduit familleProduit = FamilleProduit.findFamilleProduit(getId());
    	  familleNumber = familleProduit.getFamilleNumber();
		  footPrint = familleProduit.getFootPrint();
	}
    
   
    
    public String displayName(){
        return new StringBuilder().append(getLibelleFamille()).append(" , ").append(getFamilleNumber()).toString();

    }

}
