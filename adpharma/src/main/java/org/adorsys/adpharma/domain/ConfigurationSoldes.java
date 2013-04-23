package org.adorsys.adpharma.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import org.adorsys.adpharma.domain.EtatSolde;
import javax.persistence.Enumerated;
import javax.validation.ValidationException;
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
    @DateTimeFormat(pattern="dd-MM-yyyy HH:mm")
	@NotNull(message="Date invalide, veuillez entrer la date de debut de solde")
    private Date debutSolde;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="dd-MM-yyyy HH:mm")
    @NotNull(message="Date invalide, veuillez entrer la date de fin de solde")
    private Date finSolde;

    @DecimalMin(value="0", message="Le taux de solde ne doit pas etre inferieur 0")
    @DecimalMax(value="100", message="Le taux de solde ne doit pas etre superieur a 100")
    @NotNull(message="Taux invalide, veuillez entrer le taux de solde de ce produit")
    private Long tauxSolde;

    @Enumerated(EnumType.STRING)
    private EtatSolde etatSole= EtatSolde.EN_COURS;

    private Boolean actif= Boolean.TRUE;
    
    @NotNull(message="Le cip du produit ne doit pas etre null")    
    private transient String cipProduit;
    
   public String getCipProduit() {
	return cipProduit;
}
   public void setCipProduit(String cipProduit) {
	this.cipProduit = cipProduit;
}
    
    
    @PrePersist
    @PreUpdate
    public void validateAll(BindingResult result){
    	ObjectError error=null;
    	if(debutSolde.after(finSolde)){
    	    error= new ObjectError("date", "la date de fin de solde doit etre superieure a la date de debut de solde");
    		result.addError(error);
    	}
    	if(finSolde.before(new Date())){
    		error= new ObjectError("date", "La date de fin de solde doit etre superieure a la date du jour");
    		result.addError(error);
    	}
    }
    
    
    
}
