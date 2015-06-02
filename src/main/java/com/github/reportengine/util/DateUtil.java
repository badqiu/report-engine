package com.github.reportengine.util;

import java.util.Calendar;
import java.util.Date;

import com.github.rapid.common.util.DateConvertUtil;

public class DateUtil {
	/**
	 * 返回上个月的1号的日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getFirstDayOfLastMonth(Date date) {
		if (date == null)
			throw new RuntimeException("Date must be not null");
		Calendar cal = getCalendar(date);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return DateConvertUtil.format(cal.getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 返回上个月的最后的日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getEndDayOfLastMonth(Date date) {
		if (date == null)
			throw new RuntimeException("Date must be not null");
		Calendar cal = getCalendar(date);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return DateConvertUtil.format(cal.getTime(), "yyyy-MM-dd");
	}

	// 返回日历
	public static Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	/**
	 * 返回上周的周一的日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getBeginDayOfLastWeek(Date date) {
		if (date == null)
			throw new RuntimeException("Date must be not null");
		Calendar cal = getCalendar(date);
		cal.add(Calendar.WEEK_OF_MONTH, -1);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return DateConvertUtil.format(cal.getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 返回上周的周日的日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getEndDayOfLastWeek(Date date) {
		if (date == null)
			throw new RuntimeException("Date must be not null");
		Calendar cal = getCalendar(date);
		cal.add(Calendar.WEEK_OF_MONTH, -1);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return DateConvertUtil.format(cal.getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 返回当周周一的日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getBeginDayOfWeek(Date date) {
		if (date == null)
			throw new RuntimeException("Date must be not null");
		Calendar cal = getCalendar(date);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return DateConvertUtil.format(cal.getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 返回当周周日的日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getEndDayOfWeek(Date date) {
		if (date == null)
			throw new RuntimeException("Date must be not null");
		Calendar cal = getCalendar(date);
		cal.add(Calendar.WEEK_OF_MONTH, 1);
		cal.setFirstDayOfWeek(Calendar.SUNDAY);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return DateConvertUtil.format(cal.getTime(), "yyyy-MM-dd");
	}
	/**
	 * 返回上一个小时的开始时间
	 * @param date
	 * @return
	 */
	public static String getBeginTimeOfLastHour(Date date){
		if (date == null)
			throw new RuntimeException("Date must be not null");
		Calendar cal = getCalendar(org.apache.commons.lang.time.DateUtils.addMinutes(date, -60));
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return DateConvertUtil.format(cal.getTime(), "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 返回上5分钟的开始时间
	 * @param date
	 * @return
	 */
	public static String getBeginTimeOfLast5Minute(Date date){
		if (date == null)
			throw new RuntimeException("Date must be not null");
		Calendar cal = getCalendar(org.apache.commons.lang.time.DateUtils.addMinutes(date, -5));
		int minuteSlot = (cal.get(Calendar.MINUTE)/5)*5;
		cal.set(Calendar.MINUTE,minuteSlot);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return DateConvertUtil.format(cal.getTime(), "yyyy-MM-dd HH:mm:ss");
	}
}
