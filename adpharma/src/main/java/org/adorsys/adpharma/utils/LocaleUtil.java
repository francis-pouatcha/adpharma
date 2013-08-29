package org.adorsys.adpharma.utils;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;

public class LocaleUtil {
	
	// This method return the current Locale language of the application used by the current thread
	public static String getCurrentLocaleName(){
		String lang = LocaleContextHolder.getLocaleContext().getLocale().getLanguage();
        return lang;
	}
	
	// This method return the current Locale of the application
	public static Locale getCurrentLocale(){
		Locale locale = LocaleContextHolder.getLocaleContext().getLocale();
		return locale;
	}

}
