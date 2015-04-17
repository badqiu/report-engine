package com.github.reportengine.util;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.github.reportengine.model.Chart;


public class MiscUtilTest extends Assert{

	@Test
	public void testGetDuration() {
		assertEquals("0:0:0.120", MiscUtil.getDuration(3601, 30));
		assertEquals("", MiscUtil.getDuration(null, 30));
		assertEquals("", MiscUtil.getDuration(null, null));
		assertEquals("", MiscUtil.getDuration(null, 0));
		assertEquals("", MiscUtil.getDuration(3601, 0));
	}

	@Test
	public void testParseRateString() {
		assertEquals("20(66.67%)", MiscUtil.parseRateString(20, 30));
		assertEquals("", MiscUtil.parseRateString(null, null));
		assertEquals("", MiscUtil.parseRateString(null, 0));
		assertEquals("", MiscUtil.parseRateString(null, 30));
		assertEquals("", MiscUtil.parseRateString(5655, 0));
	}

	@Test
	public void testUnionAll() {
		List<Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
		list1.add((Map<String, Object>) new HashMap<String, Object>().put("list1", "list1"));
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		list2.add((Map<String, Object>) new HashMap<String, Object>().put("list2", "list2"));
		List<Map<String, Object>> unionAll = MiscUtil.unionAll(list1,list2);
		assertTrue(unionAll.size() == 2);
	}

	@Test
	public void testInitDynamicChart() {
//		[[kpis:[active_user_ucnt:6843, mid_active_user_ucnt:2207, mid_new_user_ucnt:5048, new_user_ucnt:5048, startup_times:13694], tdate:2013-09-21 00:00:00.0, iver:(2.10.0.2)-2013.7.16.7270], 
//		[kpis:[active_user_ucnt:8551, mid_active_user_ucnt:2564, mid_new_user_ucnt:6148, new_user_ucnt:6148, startup_times:17104], tdate:2013-09-21 00:00:00.0, iver:(2.11.0.2)-2013.7.31.7337],
		Map<String, Object> map1 = new HashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		map1.put("tdate", "2013-09-21 00:00:00.0");
		map1.put("iver", "(2.10.0.2)-2013.7.16.7270");
		map2.put("tdate", "2013-09-21 00:00:00.0");
		map2.put("iver", "(2.11.0.2)-2013.7.31.7337");
		Map<String, Object> kpisMap1 = new HashMap<String, Object>();
		kpisMap1.put("new_user_ucnt", 5048);
		Map<String, Object> kpisMap2 = new HashMap<String, Object>();
		kpisMap2.put("new_user_ucnt", 6148);
		map1.put("kpis", kpisMap1);
		map2.put("kpis", kpisMap2);
		List<Map<String, Object>> dataList = new LinkedList<Map<String,Object>>();
		dataList.add(map1);
		dataList.add(map2);
		MiscUtil.initDynamicChart(dataList, new Chart(), "iver", "new_user_ucnt");
	}

	@Test
	public void testDistinctByCol() {
		Map<String, Object> map1 = new HashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		Map<String, Object> map3 = new HashMap<String, Object>();
		Map<String, Object> map4 = new HashMap<String, Object>();
		map1.put("tdate", "2013-09-21 00:00:00.0");
		map1.put("iver", "(2.10.0.2)-2013.7.16.7270");
		map2.put("tdate", "2013-09-22 00:00:00.0");
		map2.put("iver", "(2.10.0.2)-2013.7.16.7270");
		map3.put("tdate", "2013-09-21 00:00:00.0");
		map3.put("iver", "(2.11.0.2)-2013.7.31.7337");
		map4.put("tdate", "2013-09-22 00:00:00.0");
		map4.put("iver", "(2.11.0.2)-2013.7.31.7337");
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		dataList.add(map1);
		dataList.add(map2);
		dataList.add(map3);
		dataList.add(map4);
		assertTrue(MiscUtil.distinctByCol(dataList, "tdate").size()==2);
		for (Map<String, Object> map : MiscUtil.distinctByCol(dataList, "tdate")) {
			System.out.println(map.get("tdate"));
		}
		
		List<Map<String, Object>> nullDataList = new LinkedList<Map<String,Object>>();
		assertTrue(MiscUtil.distinctByCol(nullDataList, "tdate").isEmpty());
	}

	@Test
	public void testCalDuration()  {
		assertEquals("1:0:0.1", MiscUtil.calDuration(3600001D));
		assertEquals("0:0:0.0", MiscUtil.calDuration(null));
		assertEquals("142085:42:13.915",MiscUtil.calDuration(511508533915D));
	}

	@Test
	public void testCalMsDuration()  {
		assertEquals(MiscUtil.calDuration(994.65),"0:0:0.994");
		System.out.println(MiscUtil.calDuration(1731.949779679956));
		assertEquals(MiscUtil.calDuration(1994.65),"0:0:1.994");
	}

	@Test
	public void testUrlDecode() throws Exception {
		String str = "java.lang.RuntimeException%3A+Unable+to+start+activity+ComponentInfo%7Bcom.duowan.gamevision%2Fcom.duowan.gamevision.activitys.PlayMainActivity%7D%3A+java.lang.RuntimeException%3A+setOnItemClickListener+cannot+be+used+with+a+spinner.%0A%09at+android.app.ActivityThread.performLaunchActivity%28ActivityThread.java%3A1956%29%0A%09at+android.app.ActivityThread.handleLaunchActivity%28ActivityThread.java%3A1981%29%0A%09at+android.app.ActivityThread.access%24600%28ActivityThread.java%3A123%29%0A%09at+android.app.ActivityThread%24H.handleMessage%28ActivityThread.java%3A1147%29%0A%09at+android.os.Handler.dispatchMessage%28Handler.java%3A99%29%0A%09at+android.os.Looper.loop%28Looper.java%3A137%29%0A%09at+android.app.ActivityThread.main%28ActivityThread.java%3A4424%29%0A%09at+java.lang.reflect.Method.invokeNative%28Native+Method%29%0A%09at+java.lang.reflect.Method.invoke%28Method.java%3A511%29%0A%09at+com.android.internal.os.ZygoteInit%24MethodAndArgsCaller.run%28ZygoteInit.java%3A784%29%0A%09at+com.android.internal.os.ZygoteInit.main%28ZygoteInit.java%3A551%29%0A%09at+dalvik.system.NativeStart.main%28Native+Method%29%0ACaused+by%3A+java.lang.RuntimeException%3A+setOnItemClickListener+cannot+be+used+with+a+spinner.%0A%09at+android.widget.Spinner.setOnItemClickListener%28Spinner.java%3A280%29%0A%09at+com.duowan.gamevision.activitys.PlayMainActivity.initUI%28PlayMainActivity.java%3A400%29%0A%09at+com.duowan.gamevision.activitys.PlayMainActivity.onCreate%28PlayMainActivity.java%3A128%29%0A";
		final String enc = "UTF-8";
		System.out.println(URLDecoder.decode(str.replaceAll("%", "%25"), enc));
	}

//	@Test
//	public void testSubStringIndex() throws Exception {
//		String str1 = "a/b/c";
//		assertEquals("a/b/c", MiscUtil.subStringIndex(str1, "/", 3));
//		assertEquals("a/b", MiscUtil.subStringIndex(str1, "/", 2));
//		assertEquals("a", MiscUtil.subStringIndex(str1, "/", 1));
//		assertEquals("a/b/c", MiscUtil.subStringIndex(str1, "/", 4));
//		try {
//			MiscUtil.subStringIndex(str1, "/", 0);
//			fail("No exception thrown.");
//		} catch (Exception ex) {
//			 assertTrue(ex instanceof RuntimeException);
//		}
//		String str2 = "a";
//		assertEquals("a", MiscUtil.subStringIndex(str2, "/", 3));
//		assertEquals("a", MiscUtil.subStringIndex(str2, "/", 2));
//		assertEquals("a", MiscUtil.subStringIndex(str2, "/", 1));
//		assertEquals("a", MiscUtil.subStringIndex(str2, "/", 4));
//		try {
//			MiscUtil.subStringIndex(str2, "/", 0);
//			fail("No exception thrown.");
//		} catch (Exception ex) {
//			 assertTrue(ex instanceof RuntimeException);
//		}
//	}
}
