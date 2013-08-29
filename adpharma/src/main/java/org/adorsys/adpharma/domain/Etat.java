package org.adorsys.adpharma.domain;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


public enum Etat {

  ALL("label_org_adorsys_adpharma_domain_etat_all") , 
  EN_COUR("label_org_adorsys_adpharma_domain_etat_encours"), 
  CLOS("label_org_adorsys_adpharma_domain_etat_clos"), 
  RECEIVED("label_org_adorsys_adpharma_domain_etat_received"),
  SENDED_TO_PROVIDER("label_org_adorsys_adpharma_domain_etat_send");
  
  private String messageKey;
  
  private Etat(String name){
	  this.messageKey=name;
  }
  
  public String getMessageKey() {
	return messageKey;
}
  
  @Override
  public String toString(){
	  return messageKey;
  }
  
  
}
