package org.adorsys.adpharma.utils;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.LocaleUtils;
import org.apache.tiles.util.LocaleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

public class BundleMessages{
	
public static final String BASE_NAME= "classpath:WEB-INF/i18n/messages";

@Autowired
ReloadableResourceBundleMessageSource resourceBundleMessageSource;

public BundleMessages() {
	super();
}

public String getLocalize(String code, Locale locale){
	String message = resourceBundleMessageSource.getMessage(code, null, locale); 
	return message;
}

	
	
	
	// Get the traduction of the text in properties files
  public static String	getTextLocalize(Locale locale, String key){
	  ResourceBundle bundle = ResourceBundle.getBundle(BundleMessages.BASE_NAME, locale);
	  String text = bundle.getString(key);
	  return text;
	}


}
