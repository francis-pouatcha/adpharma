package org.adorsys.adpharma.domain;

import java.util.Locale;

import org.adorsys.adpharma.utils.BundleMessages;
import org.adorsys.adpharma.utils.LocaleUtil;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public enum Periode {
JOURNALIER("label_org_adorsys_adpharma_domain_period_daily"),
HEBDOMADAIRE("label_org_adorsys_adpharma_domain_period_weekly"),
MENSUEL("label_org_adorsys_adpharma_domain_period_monthly"),
ANNUEL("label_org_adorsys_adpharma_domain_period_annual");	


private String messageKey;

private Periode(String name){
	  this.messageKey=name;
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
