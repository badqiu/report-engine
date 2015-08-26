package com.github.reportengine.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class ViewUtilsTest extends Assert{
	
	
	@Test
	public void test() {
//		List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
//		rows.add(newMap("user_cnt",100,"mid_cnt",1));
//		rows.add(newMap("user_cnt",200,"mid_cnt",2));
//		rows.add(newMap("click_map",newMap("click_cnt",100)));
//		rows.add(newMap("click_map",newMap("click_cnt",200)));
//		
//		Map<String,Double> sumMap = ViewUtils.sum(rows, "user_cnt","mid_cnt","click_map.click_cnt");
//		System.out.println(sumMap);
//		assertEquals("{user_cnt=300.0, mid_cnt=3.0, click_map.click_cnt=300.0}",sumMap.toString());
	}

	private Map newMap(Object... objects ) {
		Map map = new HashMap();
		for(int i = 0; i < objects.length; i+=2) {
			map.put(objects[i],objects[i+1]);
		}
		return map;
	}
	
	@Test
	public void testPercent() {
		assertTrue(ViewUtils.percent(0, 0.0).equals(""));
	}

	@Test
	public void testDiv() {
		assertTrue(ViewUtils.div(null, null)==0);
		assertEquals(ViewUtils.div(1318064, 8190364),0);
		System.out.println(ViewUtils.div("10278209236590804416", 123.0));
	}

	@Test
	public void testShowTimeDuration() {
		assertEquals(ViewUtils.showTimeDuration(1800),"00:30:00");
		assertEquals(ViewUtils.showTimeDuration(3600),"01:00:00");
		assertEquals(ViewUtils.showTimeDuration(3610),"01:00:10");
		assertEquals(ViewUtils.showTimeDuration(3600 * 49),"01:00:00");
	}
	
}
