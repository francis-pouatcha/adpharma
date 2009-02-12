package org.adorsys.adpharma.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

public class PharmaDateUtil {

	public static final String DATE_PATTERN_LONG = "dd-MM-yyyy";
	public static final String DATE_PATTERN_TRIM = "ddMMyy";
	public static final String DATE_PATTERN_LONG2 = "yyyy-MM-dd";

	public static final String DATE_PATTERN_LONG_LIT = "EEE, dd MMM yyyy";
	public static final String DATE_PATTERN_SHORT = "dd-MM";
	public static final String DATE_PATTERN_SHORT_LIT = "EEE, dd MMM";
	
	public static final String DATETIME_PATTERN_LONG = "dd-MM-yyyy HH:mm";
	public static final String DATETIME_PATTERN_LONG_LIT = "EEE, dd MMM yyyy HH:mm";
	public static final String DATETIME_PATTERN_SHORT = "dd-MM HH:mm";
	public static final String DATETIME_PATTERN_SHORT_LIT = "HH:mm EEE, dd MMM";
	
	public static final String DATETIME_PATTERN_LONG_SEC = "dd-MM-yyyy HH:mm:ss";
	public static final String DATETIME_PATTERN_LONG_LIT_SEC = "EEE, dd MMM yyyy HH:mm:ss";
	public static final String DATETIME_PATTERN_SHORT_SEC = "dd-MM HH:mm:ss";
	public static final String DATETIME_PATTERN_SHORT_LIT_SEC = "HH:mm:ss EEE, dd MMM";

	public static final String format(Date date, String pattern){
		if (date == null) {
			return new SimpleDateFormat(pattern).format(new Date());
		}else {
			return new SimpleDateFormat(pattern).format(date);

		}
	}
	
	public static final Date parse(String date, String pattern){
		try {
			return new SimpleDateFormat(pattern).parse(date);
		} catch (ParseException e) {
			
			return new DateUtils().addYears(new Date(), 2);
		}
	}
	
	public static final Date parseToDate(String date, String pattern){
		        System.out.println(date);
		if (StringUtils.isNotBlank(date)) {
			if (date.length() == 4) {
				 date = "20"+date.charAt(2)+date.charAt(3)+"-"+date.charAt(0)+date.charAt(1)+"-01";
				 System.out.println(date);
			}
			
		}
		try {
			return new SimpleDateFormat(pattern).parse(date);
		} catch (ParseException e) {
			
			return new DateUtils().addYears(new Date(), 50);
		}
	}
	
	public static Date getBeginDayDate(){
		Date date = new Date();
		 String stringDate = format(date, PharmaDateUtil.DATE_PATTERN_LONG);
		 stringDate = stringDate + " 00:00" ;
		return parse(stringDate, DATETIME_PATTERN_LONG);
	}
	
	public static Date getEndDayDate(){
		Date date = new Date();
		 String stringDate = format(date, PharmaDateUtil.DATE_PATTERN_LONG);
		 stringDate = stringDate + " 23:59" ;
		return parse(stringDate, DATETIME_PATTERN_LONG);
	}

}
