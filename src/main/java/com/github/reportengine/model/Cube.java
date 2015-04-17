package com.github.reportengine.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.reportengine.util.CubeBuilder;

/**
 * 多维数据集
 * @author badqiu
 *
 */
public class Cube extends BaseDataListObject {
	private String dimensions;
	private String measures;
	private Map cube;
	private String colDims;
	private String rowDims;
	
	public String getDimensions() {
		return dimensions;
	}

	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}

	public String getMeasures() {
		return measures;
	}

	public void setMeasures(String measures) {
		this.measures = measures;
	}

	public Map getCube() {
		return cube;
	}

	public void setCube(Map cube) {
		this.cube = cube;
	}

	@Override
	public void afterQuery(Map<String, Object> context) throws Exception {
		super.afterQuery(context);
		
		CubeBuilder cb = new CubeBuilder();
		cube = cb.buildCube((List)getDataList(), measures, getDimensionArray());
	}
	
	public int getDimensionSize() {
		return getDimensionArray().length;
	}

	private String[] getDimensionArray() {
		return dimensions.split("\\s*,\\s*");
	}
	
	public List<Map> getDimListMapByLevel(int level) {
		return getDimListMapByLevel(cube,level);
	}

	public int getDimAllChildsSize(int level,int endLevel) {
		return getDimAllChildsSize(cube,0,endLevel);
	}
	
	public static int getDimAllChildsSize(Map cubeMap,int endLevel) {
		return getDimAllChildsSize(cubeMap,0, endLevel);
	}
	
	public static int getDimAllChildsSize(Map cubeMap, int curLevel,int endLevel) {
		if(curLevel == endLevel) {
			return cubeMap.keySet().size();
		}
		int sum = 0;
		for(Object key : cubeMap.keySet()) {
			Object newCube = cubeMap.get(key);
			if(newCube instanceof Map) {
				int childs = getDimAllChildsSize((Map)newCube,curLevel+1,endLevel);
				sum += childs;
			}
		}
		return sum;
	}

	public static List<Map> getDimListMapByLevel(Map cubeMap,int level) {
		List collector = new ArrayList();
		getDimListMapByLevel(cubeMap, collector,level);
		return collector;
	}
	
	public static void getDimListMapByLevel(Map cubeMap,List collector,int level) {
		if(level < 0) return;
		if(level == 0) {
			collector.add(cubeMap);
			return;
		}
		for(Object key : cubeMap.keySet()) {
			Object newCube = cubeMap.get(key);
			if(newCube instanceof Map) {
				getDimListMapByLevel((Map)newCube, collector, level-1);
			}
		}
	}
	
	public String toString() {
		return cube.toString();
	}
}
