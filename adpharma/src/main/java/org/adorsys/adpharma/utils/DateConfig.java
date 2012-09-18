package org.adorsys.adpharma.utils ;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

public abstract class DateConfig {

	public static DateConfigPeriod getBegingEndOfDay(Date date){
		Date beginDate = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date enDate = DateUtils.addDays(beginDate, 1);
		enDate = DateUtils.addSeconds(enDate, -1);
		return new DateConfigPeriod(beginDate, enDate);
	}
	
	public static Date getNextDay(Date date){
		//Date beginDate = DateUtils.truncate(date, Calendar.DAY_OF_YEAR);
		Date enDate = DateUtils.addDays(date, 1);
		enDate = DateUtils.addSeconds(enDate, -1);
		return enDate ;
	}
	
	public static DateConfigPeriod getBegingEndOfWeek(Date date){
		Date beginDate = DateUtils.truncate(date, Calendar.WEEK_OF_MONTH);
		Date enDate = DateUtils.addWeeks(beginDate, 1);
		enDate = DateUtils.addSeconds(enDate, -1);
		return new DateConfigPeriod(beginDate, enDate);
	}
	public static Date getNextWeek(Date date){
		//Date beginDate = DateUtils.truncate(date, Calendar.WEEK_OF_MONTH);
		Date enDate = DateUtils.addWeeks(date, 1);
		enDate = DateUtils.addSeconds(enDate, -1);
		return enDate;
	}
	
	public static DateConfigPeriod getBegingEndOfMonth(Date date){
		Date beginDate = DateUtils.truncate(date, Calendar.WEEK_OF_MONTH);
		Date enDate = DateUtils.addMonths(beginDate, 1);
		enDate = DateUtils.addSeconds(enDate, -1);
		return new DateConfigPeriod(beginDate, enDate);
	}
	
	public static Date getNextMonth(Date date){
		//Date beginDate = DateUtils.truncate(date, Calendar.WEEK_OF_MONTH);
		Date enDate = DateUtils.addMonths(date, 1);
		enDate = DateUtils.addSeconds(enDate, -1);
		return enDate;
	}
	
	public static Date getNextYear(Date date){
		//Date beginDate = DateUtils.truncate(date, Calendar.YEAR);
		Date enDate = DateUtils.addYears(date, 1);
		enDate = DateUtils.addSeconds(enDate, -1);
		return enDate;
	}

	
}
