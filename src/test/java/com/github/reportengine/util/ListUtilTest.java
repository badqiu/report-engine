package com.github.reportengine.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ListUtilTest {

	List<Map<String,Object>> rows = new ArrayList();
	@Before
	public void before() {
		rows.add(MapUtil.newLinkedMap("name","badqiu","age",20));
		rows.add(MapUtil.newLinkedMap("name","jane","age",20));
		rows.add(MapUtil.newLinkedMap("name","ping","age",20));
		rows.add(MapUtil.newLinkedMap("name",null,"age",20));
		rows.add(MapUtil.newLinkedMap("nickname","ping","age",20));
		rows.add(MapUtil.newMap(null,"ping","age",20));
//		System.out.println("rows:"+rows);
	}
	
	@Test
	public void test() {
		Map<Object, Map> list2Map = ListUtil.list2Map(rows, "name");
		System.out.println(list2Map);
		assertEquals(list2Map.get("badqiu").toString(),"{name=badqiu, age=20}");
		assertEquals(list2Map.get(null).toString(),"{name=null, age=20}");
//		assertEquals(list2Map.toString(),"{null={name=null, age=20}, ping={name=ping, age=20}, badqiu={name=badqiu, age=20}, jane={name=jane, age=20}}");
	}
	
	@Test
	public void testError() {
		Map<Object, Map> list2Map = ListUtil.list2Map(rows, "name1");
		System.out.println(list2Map);
		assertEquals(list2Map.size(),0);
	}

	@Test
	public void testError2() {
		Map<Object, Map> list2Map = ListUtil.list2Map(null, "name");
		System.out.println(list2Map);
		assertNull(list2Map);
	}
}
