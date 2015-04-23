package com.github.reportengine.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.github.rapid.common.util.DateConvertUtils;

public class DataUtilsTest extends Assert{
	
	
	@Test
	public void test() {
		List list1 = new ArrayList();
		Map map = newMap("channel","yy","iver","beta1","outer_ref","from 163");
		map.put("kpis", newMap("event_times","238","event_ucnt","200"));
		list1.add(map);
		Map map1 = newMap("channel","yy","iver","beta1","outer_ref","from 163");
		map1.put("kpis", newMap("event_times","2500","event_ucnt","200"));
		list1.add(map1);
		Map map2 = newMap("channel","yy","iver","beta1","outer_ref","from 163");
		map2.put("kpis", newMap("event_times","400d","event_ucnt","200"));
		list1.add(map2);
		Map map3 = newMap("channel","yy","iver","beta1","outer_ref","from 163");
		map3.put("kpis", newMap("event_times","256","event_ucnt","200"));
		list1.add(map3);
		Map map4 = newMap("channel","yy","iver","beta1","outer_ref","from 163");
		map4.put("kpis", newMap("event_times","222","event_ucnt","200"));
		list1.add(map4);
		
		Map map5 = newMap("channel","yy","iver","beta1","outer_ref","from 163");
		map5.put("kpis", newMap("event_times","230","event_ucnt","200"));
		list1.add(map5);
		System.out.println(DataUtils.topNGroupby(list1, 2, "kpis.event_times", "channel","iver"));
	}

	public Map newMap(Object... objects ) {
		Map map = new HashMap();
		for(int i = 0; i < objects.length; i+=2) {
			map.put(objects[i],objects[i+1]);
		}
		return map;
	}

	/**
		 * sort dataList by map key-value
		 */
		@Test
		public void testSortByKeyDataList() {
			List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
			Map<String, Object> map1 = new HashMap<String, Object>();
			Map<String, Object> map2 = new HashMap<String, Object>();
			Map<String, Object> map3 = new HashMap<String, Object>();
			Map<String, Object> map4 = new HashMap<String, Object>();
			Map<String, Object> map5 = new HashMap<String, Object>();
			Map<String, Object> sortMap1 = new HashMap<String, Object>();
			Map<String, Object> sortMap2 = new HashMap<String, Object>();
			Map<String, Object> sortMap3 = new HashMap<String, Object>();
			Map<String, Object> sortMap4 = new HashMap<String, Object>();
			Map<String, Object> sortMap5 = new HashMap<String, Object>();
			sortMap1.put("active", 1024);
			sortMap2.put("active", 2048);
			sortMap3.put("active", null);
			sortMap4.put("active", 3248);
			sortMap5.put("active", null);
			map1.put("kpis", sortMap1);
			map2.put("kpis", sortMap2);
			map3.put("kpis", sortMap3);
			map4.put("kpis", sortMap4);
			map5.put("kpis", sortMap5);
			dataList.add(map1);
			dataList.add(map2);
			dataList.add(map3);
			dataList.add(map4);
			dataList.add(map5);
			DataUtils.sortDataList(dataList, "kpis", "active", "desc");
			for (Map<String, Object> map : dataList) {
				System.out.println(map.get("kpis"));
			}
		}
	
		/**
		 * sort dataList by element
		 */
		@Test
		public void testSortByKeyDataList2() {
			List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
			Map<String, Object> map1 = new HashMap<String, Object>();
			Map<String, Object> map2 = new HashMap<String, Object>();
			Map<String, Object> map3 = new HashMap<String, Object>();
			Map<String, Object> map4 = new HashMap<String, Object>();
			Map<String, Object> map5 = new HashMap<String, Object>();
			Map<String, Object> sortMap1 = new HashMap<String, Object>();
			Map<String, Object> sortMap2 = new HashMap<String, Object>();
			Map<String, Object> sortMap3 = new HashMap<String, Object>();
			Map<String, Object> sortMap4 = new HashMap<String, Object>();
			Map<String, Object> sortMap5 = new HashMap<String, Object>();
			sortMap1.put("active", 1024);
			sortMap2.put("active", 2048);
			sortMap3.put("active", null);
			sortMap4.put("active", 3248);
			sortMap5.put("active", null);
			map1.put("kpis", sortMap1);
			map2.put("kpis", sortMap2);
			map3.put("kpis", sortMap3);
			map4.put("kpis", sortMap4);
			map5.put("kpis", sortMap5);
			map1.put("weight", 12);
			map2.put("weight", 14);
			map3.put("weight", null);
			map4.put("weight", 2);
			map5.put("weight", null);
			dataList.add(map1);
			dataList.add(map2);
			dataList.add(map3);
			dataList.add(map4);
			dataList.add(map5);
			DataUtils.sortDataList(dataList, "weight", "asc");
			for (Map<String, Object> map : dataList) {
				System.out.println(map.get("kpis") + "," + map.get("weight"));
			}
		}

		@Test
		public void testSplitList2Lists()  {
//			[tdate:2013-12-14, eventid:click/tj/tj1, seq:1, step_name:推荐1, devent_times:1727], 
//			[tdate:2013-12-14, eventid:heartbeat, seq:2, step_name:心跳, devent_times:288], 
//			[tdate:2013-12-14, eventid:enter_game, seq:3, step_name:进入游戏, devent_times:96]
			
//			[tdate:2013-12-15, eventid:click/tj/tj1, seq:1, step_name:推荐1, devent_times:1724], 
//			[tdate:2013-12-15, eventid:heartbeat, seq:2, step_name:心跳, devent_times:287], 
//			[tdate:2013-12-15, eventid:enter_game, seq:3, step_name:进入游戏, devent_times:96]
//
//			[tdate:2013-12-16, eventid:click/tj/tj1, seq:1, step_name:推荐1, devent_times:1725], 
//			[tdate:2013-12-16, eventid:heartbeat, seq:2, step_name:心跳, devent_times:288], 
//			[tdate:2013-12-16, eventid:enter_game, seq:3, step_name:进入游戏, devent_times:96]
			
			List<List<Map<String, Object>>> lists = initData();
			assertEquals(3, lists.size());
			for (List<Map<String, Object>> list : lists) {
				for (Map map : list) {
					System.out.println(map);
				}
			}
		}

		private List<List<Map<String, Object>>> initData() {
			List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
			Map<String, Object> row0 = new LinkedHashMap<String, Object>();
			row0.put("tdate", "2013-12-14");
			row0.put("eventid", "click/tj/tj1");
			row0.put("seq", 1);
			row0.put("step_name", "推荐1");
			row0.put("devent_times", 1727);
			Map<String, Object> row1 = new LinkedHashMap<String, Object>();
			row1.put("tdate", "2013-12-14");
			row1.put("eventid", "heartbeat");
			row1.put("seq", 2);
			row1.put("step_name", "心跳");
			row1.put("devent_times", 288);
			Map<String, Object> row2 = new LinkedHashMap<String, Object>();
			row2.put("tdate", "2013-12-14");
			row2.put("eventid", "enter_game");
			row2.put("seq", 3);
			row2.put("step_name", "进入游戏");
			row2.put("devent_times", 96);
			dataList.add(row0);
			dataList.add(row1);
			dataList.add(row2);
			//---
			Map<String, Object> row3 = new LinkedHashMap<String, Object>();
			row3.put("tdate", "2013-12-15");
			row3.put("eventid", "click/tj/tj1");
			row3.put("seq", 1);
			row3.put("step_name", "推荐1");
			row3.put("devent_times", 1724);
			Map<String, Object> row4 = new LinkedHashMap<String, Object>();
			row4.put("tdate", "2013-12-15");
			row4.put("eventid", "heartbeat");
			row4.put("seq", 2);
			row4.put("step_name", "心跳");
			row4.put("devent_times", 287);
			Map<String, Object> row5 = new LinkedHashMap<String, Object>();
			row5.put("tdate", "2013-12-15");
			row5.put("eventid", "enter_game");
			row5.put("seq", 3);
			row5.put("step_name", "进入游戏");
			row5.put("devent_times", 96);
			dataList.add(row3);
			dataList.add(row4);
			dataList.add(row5);
			//---
			Map<String, Object> row6 = new LinkedHashMap<String, Object>();
			row6.put("tdate", "2013-12-16");
			row6.put("eventid", "click/tj/tj1");
			row6.put("seq", 1);
			row6.put("step_name", "推荐1");
			row6.put("devent_times", 1725);
			Map<String, Object> row7 = new LinkedHashMap<String, Object>();
			row7.put("tdate", "2013-12-16");
			row7.put("eventid", "heartbeat");
			row7.put("seq", 2);
			row7.put("step_name", "心跳");
			row7.put("devent_times", 288);
			Map<String, Object> row8 = new LinkedHashMap<String, Object>();
			row8.put("tdate", "2013-12-16");
			row8.put("eventid", "enter_game");
			row8.put("seq", 3);
			row8.put("step_name", "进入游戏");
			row8.put("devent_times", 96);
			dataList.add(row6);
			dataList.add(row7);
			dataList.add(row8);
			List<List<Map<String, Object>>> lists = DataUtils.splitList2Lists(dataList);
			return lists;
		}

		@Test
		public void testGetRangeDataListsByDay()  {
			List<List<Map<String, Object>>> lists = initData();
			Date endDate = DateConvertUtils.parse("2013-12-16", "yyyy-MM-dd");
			List<List<Map<String, Object>>> resultLists = DataUtils.getRangeDataListsByDay(lists, endDate, 2);
			assertEquals(2, resultLists.size());
			System.out.println(resultLists);
		}

		@Test
		public void testConverXByDay() {
			List<List<Map<String, Object>>> lists = initData();
			System.out.println(DataUtils.converXByDay(lists));
		}

		@Test
		public void testDoubleMultiply(){
//			assertEquals(0, DataUtils.doubleMultiply(0.0024111 , 100).compareTo(new Double(0.241)));
//			assertEquals(0, DataUtils.doubleMultiply(null, null).compareTo(new Double(0d)));
			System.out.println(DataUtils.doubleMultiply(1168.974533 , 0.001));
		}
		
		@Test
		public void testCaseWhenToMapList() {
			List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
			Map<String, Object> row = new LinkedHashMap<String, Object>();
			row.put("range_type", "little");
			row.put("eventid", "click/tj/tj1");
			row.put("seq", 1);
			row.put("step_name", "推荐1");
			row.put("devent_times", 1727);
		}

		@Test
		public void testMultiply() {
			assertEquals(Long.valueOf(0),DataUtils.multiply(null, 1));
			assertEquals(Long.valueOf(0),DataUtils.multiply(10, null));
			assertEquals(Long.valueOf(100),DataUtils.multiply(10, 10));
		}

		@Test
		public void testInnerJoin() {
			Map<String, Object> map1 = newMap("proxy_id",33,"eventid","error/crash/d1191e61ca42cee12bc441e07a994498");
			Map<String, Object> map2 = newMap("proxy_id",33,"content",null);
			Map<String, Object> map3 = newMap("proxy_id",418219,"content","堆栈");
			Map<String, Object> map4 = newMap("proxy_id",418219,"eventid","error/crash/cc17e8378983b97c5a34288893d5c944");
			Map<String, Object> map5 = newMap("proxy_id",44,"eventid","error/crash/d1191e61ca42cee12bc441e07a994498");
			Map<String, Object> map6 = newMap("proxy_id",23,"eventid","error/crash/d1191e61ca42cee12bc441e07a994498");
			List<Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
			list1.add(map1);
			list1.add(map4);
			list1.add(map5);
			list1.add(map6);
			
			list2.add(map2);
			list2.add(map3);
			String[] clos = {"content"};
			String[] alias = {"content"};
			String[] inner = {"proxy_id"};
			
			DataUtils.innerJoin(list1, list2, clos, alias, inner);
			
			System.out.println(list1);
		}

}
