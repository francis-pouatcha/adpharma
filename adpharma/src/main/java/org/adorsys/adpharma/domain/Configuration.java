package org.adorsys.adpharma.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.beans.factory.annotation.Value;

@RooJavaBean
@RooToString
@RooEntity
public class Configuration {

    @Value("true")
    private Boolean saleForce;

    @Value("false")
    private Boolean closeCashUnpaySale;

    @Value("true")
    private Boolean closeCancelSale;
    
    @Value("true")
    private Boolean restoreCancelSale;
    
    
    public static void init(){
    	if(Configuration.countConfigurations() <= 0){
    		Configuration configuration = new Configuration();
    		configuration.persist();
    	}
    }
}
