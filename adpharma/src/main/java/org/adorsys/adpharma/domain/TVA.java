package org.adorsys.adpharma.domain;

import org.adorsys.adpharma.utils.NumberGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import java.math.BigDecimal;

import javax.persistence.PostPersist;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "TVA")
public class TVA extends AdPharmaBaseEntity {

	private String tvaNumber;
    @NotNull
    private BigDecimal tauxTva;

    @Value("true")
    private Boolean valide;
    
    @Override
    protected void internalPrePersist() {

    }
    
    @PostPersist
    public void postPersist() {
    	tvaNumber = NumberGenerator.getNumber("T-", getId(), 4);

    }
    
    public void protectSomeField(){
    	TVA tva = TVA.findTVA(getId());
    	tvaNumber = tva.getTvaNumber();
    	footPrint = tva.getFootPrint();
	}
    
   
    public static void initTva() {
        if (TVA.countTVAS()<=0) {
            System.out.println("[ initialisation des tVA]");
            TVA tva = new TVA();
            tva.setTauxTva(new BigDecimal(19.25));
            tva.setValide(true);
            tva.persist();
        }
    }
    
    public String displayName(){
        return new StringBuilder().append(getTauxTva()).toString();

    }
}
