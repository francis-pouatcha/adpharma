package org.adorsys.adpharma.domain;


public enum TypeBon {

  CREATION("label_org_adorsys_adpharma_domain_typebon_creation") , 
  RETOUR("label_org_adorsys_adpharma_domain_typebon_retour");
  
  private String messageKey;
  
  private TypeBon(String name){
	  this.messageKey= name;
  }
  
  @Override
  public String toString(){
	  return messageKey;
  }
  
  
}
