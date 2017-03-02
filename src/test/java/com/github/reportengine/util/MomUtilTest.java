package com.github.reportengine.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.github.rapid.common.util.DateConvertUtil;

public class MomUtilTest {

	@Rule public TestName testName = new TestName();
	
	List<Map> rows = new ArrayList<Map>();
	@Before
	public void before() {
		Date date = DateConvertUtil.extract(DateConvertUtil.parse("2015-01-01", "yyyy-MM-dd"), "yyyy-MM-dd");
		for(int i = 0; i < 100; i++) {
			rows.add(MapUtil.newMap("tdate",date,"age",i,"type","type_"+(i % 5)));
			date = DateUtils.addDays(date,1);
		}
		
		System.out.println("\n------------------------- "+testName.getMethodName()+" ------------------------------\n");
	}
	
	@Test
	public void test_ringUpByDay() {
		
		
		MomUtil.ringUp(rows, "tdate", -7);
		
		for(Map row : rows) {
			System.out.println(row);
		}
	}

	@Test
	public void test_ringUpByMonth() {
		
		
		rows = MomUtil.ringUp(rows, "tdate","month", -2);
		
		for(Map row : rows) {
			System.out.println(row);
		}
	}
}
