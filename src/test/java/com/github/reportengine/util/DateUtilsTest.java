package com.github.reportengine.util;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.duowan.common.util.DateConvertUtils;

public class DateUtilsTest extends Assert{
	private Date date = DateConvertUtils.parse("2013-10-10 13:24:00", "yyyy-MM-dd HH:mm:ss");
	@Test
	public void testGetFirstDayOfLastMonth() {
		assertEquals(DateUtils.getFirstDayOfLastMonth(date), "2013-09-01");
	}

	@Test
	public void testGetBeginDayOfLastWeek() {
		assertEquals(DateUtils.getBeginDayOfLastWeek(date), "2013-09-30");
		Date date2 = DateConvertUtils.parse("2013-10-02", "yyyy-MM-dd");
		System.out.println(DateUtils.getBeginDayOfLastWeek(date2));
	}

	@Test
	public void testGetEndDayOfLastWeek() {
		assertEquals(DateUtils.getEndDayOfLastWeek(date), "2013-10-06");
	}

	@Test
	public void testGetEndDayOfLastMonth() {
		assertEquals(DateUtils.getEndDayOfLastMonth(date), "2013-09-30");
	}

	@Test
	public void testGetBeginTimeOfLastHour(){
		assertEquals(DateUtils.getBeginTimeOfLastHour(date), "2013-10-10 12:00:00");
	}
	@Test
	public void testGetBeginTimeOfLast5Minute(){
		assertEquals(DateUtils.getBeginTimeOfLast5Minute(date), "2013-10-10 13:15:00");
	}
}
