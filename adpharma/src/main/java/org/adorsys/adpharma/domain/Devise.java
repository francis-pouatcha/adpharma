package org.adorsys.adpharma.domain;

import org.adorsys.adpharma.utils.NumberGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.PostPersist;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Devise")
public class Devise extends AdPharmaBaseEntity {

    private String deviseNumber;

    @NotNull
    private String libelle;

    private String libelleCourt;

    private BigDecimal equivalenceCfa;
    
    @Override
    protected void internalPrePersist() {

    }
    
    @PostPersist
    public void postPersit(){
    	deviseNumber = NumberGenerator.getNumber("DV-",getId(), 4);


    }
    
    public void protectSomeField(){
     	Devise devise	= Devise.findDevise(getId());
     	deviseNumber = devise.getDeviseNumber();
		  footPrint = devise.getFootPrint();
	}
    
   
    
    public static void initDevise() {
        if (Devise.countDevises()<=0) {
            System.out.println("[ initialisation des Devises ]");
            Devise devise = new Devise();
            devise.setLibelle("Franc Cfa");
            devise.setLibelleCourt("Cfa");
            devise.setEquivalenceCfa(BigDecimal.ONE);
            devise.persist();
        }
    }
    
    public String displayName(){
        return new StringBuilder().append(getLibelle()).append(" , ").append(getDeviseNumber()).toString();

    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getLibelle());
        return sb.toString();
    }
}
