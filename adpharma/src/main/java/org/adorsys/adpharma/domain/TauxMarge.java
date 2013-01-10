package org.adorsys.adpharma.domain;

import org.adorsys.adpharma.utils.NumberGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import java.math.BigDecimal;

import javax.persistence.PostPersist;
import javax.validation.constraints.NotNull;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "TauxMarge")
public class TauxMarge extends AdPharmaBaseEntity {
	private String margeNumber;
    @NotNull
    private BigDecimal margeValue;
    
    @Value("true")
    private Boolean valide;
    
    @Override
    protected void internalPrePersist() {

    }
    

    @PostPersist
    public void postPersist() {
    	margeNumber = NumberGenerator.getNumber("MRG-", getId(), 4);

    }
    
    public void protectSomeField(){
    	TauxMarge tauxMarge = TauxMarge.findTauxMarge(getId());
    	 margeNumber= tauxMarge.getMargeNumber();
		  footPrint = tauxMarge.getFootPrint();
	}
    
    public static void initTauxMarge() {
        if (TauxMarge.countTauxMarges()<=0) {
            System.out.println("[ initialisation des taux de marge]");
            TauxMarge tauxMarge = new TauxMarge();
            tauxMarge.setMargeValue(new BigDecimal(34));
            tauxMarge.setValide(true);
            tauxMarge.persist();
        }
    }
    
    public String displayName(){
        return new StringBuilder().append(getMargeValue()).toString();

    }
}
