package com.github.reportengine.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.github.reportengine.model.Cube;

public class CubeBuilderTest {
	CubeBuilder c = new CubeBuilder();
	
	public static List<Map> rows = new ArrayList<Map>();
	static {
		rows.add(MapUtil.newMap("tdate","2013-01-01","game","ddt","gameServer","s1","click",100,"login",100));
		rows.add(MapUtil.newMap("tdate","2013-01-02","game","ddt","gameServer","s2","click",200,"login",200));
		rows.add(MapUtil.newMap("tdate","2013-01-02","game","ddt","gameServer","s3","click",300,"login",300));
		rows.add(MapUtil.newMap("tdate","2013-01-01","game","sxd","gameServer","s1","click",100,"login",100));
		rows.add(MapUtil.newMap("tdate","2013-01-01","game","sxd","gameServer","s2","click",200,"login",200));
	}
	@Before 
	public void before() {
	}
	
	@Test
	public void test() {
		printCross(rows,"game","gameServer");
		printCross(rows,"game");
		printCross(rows,"tdate");
		printCross(rows,"tdate","game");
		printRowColCube(rows,new String[]{"game","gameServer"},new String[]{"tdate"},"count(game) cnt_game","game","gameServer","tdate");

	}
	
	@Test
	public void getDimListMapByLevel() {
		String[] dimensions = new String[]{"tdate","game","gameServer"};
		Map cubeMap = c.buildCube(rows,"count(game) cnt_game,sum(click) sum_click", dimensions);
		for(int i = 0; i < dimensions.length; i++) {
			List<Map> list = Cube.getDimListMapByLevel(cubeMap,i);
			System.out.println(i + " " + dimensions[i]+"___"+list);
			for(Map row : list) {
				System.out.println(row.keySet()+"    "+row.values());
			}
		}
		
		System.out.println(Cube.getDimAllChildsSize(cubeMap, 0, 3));
	}

	private void printCross(List<Map> rows,String... keys) {
		System.out.println("\n\n-------------" + StringUtils.join(keys,",") + "-------------");
		printDim(c.buildCube(rows,"count(game) cnt_game,sum(click) sum_click", keys));
	}
	
	private void printRowColCube(List<Map> rows,String[] rowPath,String[] colPath,String arrgColumns,String... keys) {
		System.out.println("\n\n------------- printRowColCube() " + StringUtils.join(keys,",") + "-------------");
		Map map = c.buildCube(rows,arrgColumns, keys);
		printRowHeaders(map,rowPath,0);
		for(Object e : map.entrySet()) {
			Map.Entry entry = (Map.Entry)e;
			Object key = entry.getKey();
			Object value = entry.getValue();
			printDim(value);
		}
	}

	private Object printRowHeaders(Object input,String[] rowPath,int pathCount) {
		if(pathCount >= rowPath.length) {
			printDim(input);
			return null;
		}
		Map map = (Map)input;
		
		for(Object e : map.entrySet()) {
			Map.Entry entry = (Map.Entry)e;
			Object key = entry.getKey();
			Object value = entry.getValue();
			System.out.print(key+"\t\t\t\t");
		}
		System.out.println("\t");
		for(Object e : map.entrySet()) {
			Map.Entry entry = (Map.Entry)e;
			Object key = entry.getKey();
			Object value = entry.getValue();
			printRowHeaders(value,rowPath,pathCount+1);
		}
		return null;
	}
	
	public static void printDim(Object obj) {
		printDim(System.out,obj, 0);
	}
	
	public static String dimToString(Object obj,int tabCount) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		printDim(ps,obj, tabCount);
		return out.toString();
	}
	
	public static void printDim(PrintStream out,Object obj,int tabCount) {
		if(obj instanceof Map) {
			Map map = (Map)obj;
			for(Object e : map.entrySet()) {
				Map.Entry entry = (Map.Entry)e;
				out.println(StringUtils.repeat("\t", tabCount) + entry.getKey());
				printDim(out,entry.getValue(),tabCount+1);
			}
		}else {
			out.println(StringUtils.repeat("\t", tabCount) + obj);
		}
	}

}
