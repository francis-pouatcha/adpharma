package org.adorsys.adpharma.utils;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.adorsys.adpharma.domain.PharmaUser;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.bouncycastle.jce.provider.JDKDSASigner.stdDSA;

public class CipMgenerator {
	@SuppressWarnings("deprecation")
	public static final String getCipM(String suffix){
		String dateString = PharmaDateUtil.format(new Date(), PharmaDateUtil.DATE_PATTERN_TRIM);
		 return  new StringBuffer().append(dateString).append(CipMgenerator.formatNumber(suffix, 6)).toString();
	 }
	
	public static String formatNumber(String stringToFormat , int patern){
	String format  = null ;
		if (stringToFormat.length() > patern-1) {
			return stringToFormat ;
			
		}else {
			
			format = new StringBuilder().append(CipMgenerator.getZero(patern-stringToFormat.length()-1)).append(stringToFormat).toString();
		}
		return format.trim();
	}
	
	public static String getZero(int nbOfZero){
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i <= nbOfZero; i++) {
			stringBuilder.append(0);
			
		}
		return stringBuilder.toString();
	}
	
	
	public static String generateSaleKey(){
		String randomNumeric = RandomStringUtils.randomNumeric(4);
		 List<PharmaUser> resultList = PharmaUser.findPharmaUsersBySaleKeyEquals(randomNumeric).getResultList();
		  while (!resultList.isEmpty()) {
			randomNumeric = RandomStringUtils.randomNumeric(4);
			resultList = PharmaUser.findPharmaUsersBySaleKeyEquals(randomNumeric).getResultList();
		}
		
		 return randomNumeric;
	}
	
	
}
