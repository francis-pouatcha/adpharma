package org.adorsys.adpharma.domain;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;

import org.adorsys.adpharma.domain.EtatSolde;


import javax.persistence.Enumerated;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@RooJavaBean
@Embeddable
public class ConfigurationSoldes implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
 
	@Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="dd-MM-yyyy")
	@NotNull(message="Veuillez entrer la date de debut du solde")
    private Date debutSolde;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    @NotNull(message="Veuillez entrer la date de fin du solde")
    private Date finSolde;

    @DecimalMin(value="1.0", message="Le taux de solde ne doit pas etre inferieur a 1")
    @DecimalMax(value="100.0", message="Le taux de solde ne doit pas etre superieur a 100")
    @NotNull(message="Veuillez entrer le taux de solde de ce produit")
    @Column(columnDefinition="Decimal(10,2)")
    private BigDecimal tauxSolde;

    @Enumerated(EnumType.STRING)
    private EtatSolde etatSole= EtatSolde.EN_COURS;

    private Boolean activeConfig= Boolean.TRUE;
    
    @NotNull(message="Entrez le CIP du produit")    
    private transient String cipProduit;
    
   public String getCipProduit() {
	return cipProduit;
}
   public void setCipProduit(String cipProduit) {
	this.cipProduit = cipProduit;
}
    
    
    // Others types of validations 
    public static void validateAll(BindingResult result, ConfigurationSoldes solde){
    	ObjectError error= null;
    	if(solde.getCipProduit()==null || solde.getCipProduit()==""){
    		error= new ObjectError("cipProduit", "Entrez le cip du produit");
    		result.addError(error);
    	}
    if(solde.getDebutSolde()!=null && solde.getFinSolde()!=null){ 	
 	   if(solde.getDebutSolde().after(solde.getFinSolde())){
    	    error= new ObjectError("error",	"la date de fin du solde doit etre superieure a la date de debut du solde");
    		result.addError(error);
    	}
 	   if(solde.getDebutSolde().before(new Date())){
 		   error= new ObjectError("error", "la date de debut du solde doit etre superieure a la date du jour");
 		   result.addError(error);
 	   }
    	if(solde.getFinSolde().before(new Date())){
    		error= new ObjectError("error", "La date de fin du solde doit etre superieure a la date du jour");
    		result.addError(error);
    	}
     }	
    }
    
    
    
}
