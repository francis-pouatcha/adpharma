package org.adorsys.adpharma.utils;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.LocaleUtils;
import org.apache.tiles.util.LocaleUtil;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

@Service
public class BundleMessages extends ResourceBundleMessageSource {
	
	public static final String BASE_NAME= "classpath:WEB-INF/i18n/messages";

	
	
	@Override
	protected ResourceBundle getResourceBundle(String basename, Locale locale) {
		// TODO Auto-generated method stub
		return super.getResourceBundle(basename, locale);
	}
	
	// Get the traduction of the text in properties files
  public String	getTextLocalize(Locale locale, String key){
	  ResourceBundle bundle = getResourceBundle(BundleMessages.BASE_NAME, locale);
	  System.out.println("Bundle: "+bundle);
	  String text = bundle.getString(key);
	  return text;
	}

}
