package com.github.reportengine.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.github.rapid.common.util.DateConvertUtils;

public class MomUtilTest {

	List<Map> rows = new ArrayList<Map>();
	@Before
	public void before() {
		Date date = DateConvertUtils.extract(DateConvertUtils.parse("2015-01-01", "yyyy-MM-dd"), "yyyy-MM-dd");
		for(int i = 0; i < 100; i++) {
			rows.add(MapUtil.newMap("tdate",date,"age",i,"type","type_"+(i % 5)));
			date = DateUtils.addDays(date,1);
		}
	}
	
	@Test
	public void test() {
		
		
		MomUtil.ringUp(rows, "tdate", -7);
		
		for(Map row : rows) {
			System.out.println(row);
		}
	}

}
