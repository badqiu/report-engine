package com.github.reportengine.util;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.github.rapid.common.util.DateConvertUtil;

public class DateUtilsTest extends Assert{
	private Date date = DateConvertUtil.parse("2013-10-10 13:24:00", "yyyy-MM-dd HH:mm:ss");
	@Test
	public void testGetFirstDayOfLastMonth() {
		assertEquals(DateUtil.getFirstDayOfLastMonth(date), "2013-09-01");
	}

	@Test
	public void testGetBeginDayOfLastWeek() {
		assertEquals(DateUtil.getBeginDayOfLastWeek(date), "2013-09-30");
		Date date2 = DateConvertUtil.parse("2013-10-02", "yyyy-MM-dd");
		System.out.println(DateUtil.getBeginDayOfLastWeek(date2));
	}

	@Test
	public void testGetEndDayOfLastWeek() {
		assertEquals(DateUtil.getEndDayOfLastWeek(date), "2013-10-06");
	}

	@Test
	public void testGetEndDayOfLastMonth() {
		assertEquals(DateUtil.getEndDayOfLastMonth(date), "2013-09-30");
	}

	@Test
	public void testGetBeginTimeOfLastHour(){
		assertEquals(DateUtil.getBeginTimeOfLastHour(date), "2013-10-10 12:00:00");
	}
	@Test
	public void testGetBeginTimeOfLast5Minute(){
		assertEquals(DateUtil.getBeginTimeOfLast5Minute(date), "2013-10-10 13:15:00");
	}
}
