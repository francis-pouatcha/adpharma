package org.adorsys.adpharma.utils;

import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;

public class NumberGenerator {
 public static final String getNumber(String Prefix , Long suffix , int patern){
	 return Prefix + CipMgenerator.formatNumber(""+suffix, patern) ;
	/* DateTime dateTime = new DateTime();
     int year = dateTime.getYear();
     return Prefix + RandomStringUtils.randomNumeric(6).toUpperCase() + "-" + year;
*/
 }
}
