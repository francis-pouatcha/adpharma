package org.adorsys.adpharma.domain;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
@RooJavaBean
@RooToString
@RooEntity
public class LicenceEntity {

	private String beginDate = PharmaDateUtil.format(new Date(), PharmaDateUtil.DATETIME_PATTERN_LONG);

	private String endDate =  PharmaDateUtil.format(DateUtils.addYears(new Date(), 5), PharmaDateUtil.DATETIME_PATTERN_LONG);

	@Transient
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM yyyy HH:mm")
	private Date transtientBegin = new Date();

	@Transient
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM yyyy HH:mm")
	private Date transtientEnd = DateUtils.addDays(new Date(), 5);

	@Value("true")
	private Boolean isValid;

	private String generateKey;

	//encoding  byte array into base 64
	public static String encodeString(String value){
		byte[] encoded = Base64.encodeBase64(value.getBytes());     
		return new String(encoded);

	}

	//decoding byte array into base64
	public static String decodeString(String value){
		byte[] decoded = Base64.decodeBase64(value.getBytes());      
		return new String(decoded);

	}

	public void validate(BindingResult bindingResult) {

		if (!transtientBegin.before(transtientEnd)) {
			ObjectError error = new ObjectError("endDate", "La date de fin Doit etre  superieur a la date date de debut ");
			bindingResult.addError(error);
		}
	}

	public boolean isNotValidLicence(){
		if(transtientEnd !=null){
			System.out.println("firdt"+PharmaDateUtil.format(transtientEnd, "dd-MM yyyy HH:mm"));
			System.out.println("second"+PharmaDateUtil.format(new Date(), "dd-MM yyyy HH:mm"));
			return new Date().before(transtientEnd);
		}
		return false;

	}

	@PostLoad
	public void postLoad(){
		decodeDate();
		initTransientDate();
	}

	@PrePersist
	public void prePersist(){
		encodeDate();

	}

	@PreUpdate
	public void PeUpdate(){
		encodeDate();
	}


	public void initTransientDate(){
		if(StringUtils.isNotBlank(beginDate) ) transtientBegin = PharmaDateUtil.parse(beginDate, PharmaDateUtil.DATETIME_PATTERN_LONG);
		if(StringUtils.isNotBlank(endDate) ) transtientEnd = PharmaDateUtil.parse(endDate, PharmaDateUtil.DATETIME_PATTERN_LONG);

	}

	public void initDate(){
		if(transtientBegin!=null) beginDate = PharmaDateUtil.format(transtientBegin, PharmaDateUtil.DATETIME_PATTERN_LONG);
		if(transtientEnd!=null)   endDate = PharmaDateUtil.format(transtientEnd, PharmaDateUtil.DATETIME_PATTERN_LONG);

	}

	public void encodeDate(){
		if(StringUtils.isNotBlank(beginDate) ) beginDate = encodeString(beginDate);
		if(StringUtils.isNotBlank(endDate) ) endDate =  encodeString(endDate);
	}
	public void decodeDate(){
		if(StringUtils.isNotBlank(beginDate) ) beginDate = decodeString(beginDate);
		if(StringUtils.isNotBlank(endDate) ) endDate =  decodeString(endDate);
	}   

	public void generateKey(){
		if(StringUtils.isBlank(generateKey)){
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < 3; i++) {
				String randomAlphanumeric = RandomStringUtils.randomAlphanumeric(3).toUpperCase();
				String encodeString = encodeString(randomAlphanumeric);
				System.out.println(encodeString);
				System.out.println(randomAlphanumeric);
				builder.append(encodeString);
			}

			generateKey =  builder.toString();// StringUtils.removeEnd(builder.toString().replaceAll("==", "-"),"-") ;
			System.out.println(generateKey);
		}
	}



	public boolean isValidKey(String Key){
		if(StringUtils.isBlank(Key)) return false ;
		String[] listString = Key.split("-");
		String generateKey2 = " " ;
		int length = listString.length ;
		if (length > 0) {
			StrBuilder strBuilder = new StrBuilder();
			for (int i = 0; i < length-1; i++) {
				strBuilder.append(encodeString(listString[i].toUpperCase()));	
				System.out.println(listString[i].toUpperCase());
				System.out.println(encodeString(listString[i].toUpperCase()));
			}
			generateKey2 =  StringUtils.removeEnd(strBuilder.toString().replaceAll("==", "-"),"-");
			System.out.println(generateKey2+" "+generateKey);
			return StringUtils.equals(generateKey2, generateKey) && NumberUtils.isNumber(decodeString(listString[3]));
		}else {
			return false ;
		}

	}

}
