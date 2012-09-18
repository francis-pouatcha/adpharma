package org.adorsys.adpharma.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.Size;
import org.adorsys.adpharma.domain.FamilleProduit;
import org.adorsys.adpharma.utils.NumberGenerator;

import javax.validation.constraints.NotNull;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "SousFamilleProduit")
public class SousFamilleProduit extends AdPharmaBaseEntity {

    private String sousFamilleNumber;
    
    @NotNull
    private String libelleSousFamille;

    private String libelleCourt;

    @Size(max = 256)
    private String note;

    @NotNull
    @ManyToOne
    private FamilleProduit famille;
    
    @Override
    protected void internalPrePersist() {

    }
    

    @PostPersist
    public void postPersist() {
    	sousFamilleNumber = NumberGenerator.getNumber("SF-", getId(), 4);

    }

    
    @Override
    protected void internalPreUpdate() {
    }
    
    @Override
    public void protectSomeField(){
    	SousFamilleProduit sousFamille = SousFamilleProduit.findSousFamilleProduit(getId());
    	  sousFamilleNumber = sousFamille.getSousFamilleNumber();
		  footPrint = sousFamille.getFootPrint();
	}
    

   
    
    public String displayName(){
        return new StringBuilder().append(getLibelleSousFamille()).toString();

    }
}
