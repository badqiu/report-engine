package com.duowan.reportengine.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.junit.Test;


public class CascadingParameterUtilTest {
	
	@Test
	public void test() {
		List<Map> rows = CubeBuilderTest.rows;
		Map cube = CascadingParameterUtil.build(rows , new KeyValue[]{new DefaultKeyValue("tdate","tdate"),new DefaultKeyValue("game","game")});
		System.out.println(cube);
		CubeBuilderTest.printDim(cube);
		
		System.out.println(JsonUtil.toJson(cube));
	}
	
}
