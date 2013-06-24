package org.adorsys.adpharma.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.math.BigInteger;
import java.math.BigDecimal;
import org.adorsys.adpharma.domain.TypeVente;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

@RooJson
@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", finders = { "findSalesConfigurationsByTypeVente" })
public class SalesConfiguration extends AdPharmaBaseEntity {

    private BigInteger minValue;

    private BigInteger maxValue;

    @DecimalMin(value="0", message="Le taux de reduction ne doit pas etre inferieur a 0%")
    @DecimalMax(value="100", message="Le taux de reduction ne doit pas etre superieur a 100%")
    private BigDecimal tauxReduction;
    
    private Boolean activeConfig= Boolean.TRUE;

    @Enumerated(EnumType.STRING)
    private TypeVente typeVente;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MaxValue: ").append(getMaxValue()).append(", ");
        sb.append("MinValue: ").append(getMinValue()).append(", ");
        sb.append("TauxReduction: ").append(getTauxReduction()).append(", ");
        sb.append("TypeVente: ").append(getTypeVente()).append(", ");
        return sb.toString();
    }
    
    // Validation de la configuration
    public static void validateAll(BindingResult result, SalesConfiguration config){
    	BigInteger minValue= config.getMinValue();
    	BigInteger maxValue= config.getMaxValue();
    	if(minValue.intValue()> maxValue.intValue()){
    		ObjectError erreur = new ObjectError("error", "La quantite minimum doit etre inferieure a la quantite maximum");
    		result.addError(erreur);
    	}
    }
}
