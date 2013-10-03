package org.adorsys.adpharma.domain;

import java.util.Locale;

import org.adorsys.adpharma.utils.BundleMessages;
import org.adorsys.adpharma.utils.LocaleUtil;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;


public enum TypeBon {

  CREATION("label_org_adorsys_adpharma_domain_typebon_creation") , 
  RETOUR("label_org_adorsys_adpharma_domain_typebon_retour");
  
  private String messageKey;
  
  private TypeBon(String name){
	  this.messageKey= name;
  }
  
  public String getMessageKey() {
	return messageKey;
}
  
  @Override
  public String toString(){
	  return name();
  }
  
  public String toString1(){
	  return messageKey;
  }
  
  
}
