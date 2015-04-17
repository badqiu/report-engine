package com.github.reportengine.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
/**
 * 构建级联参数的工具类
 * 
 * @author badqiu
 *
 */
public class CascadingParameterUtil {
	
	public static Map build(List<Map> rows,KeyValue[] keyValues){
		return build(rows,keyValues,0);
	}
	
	private static Map build(List<Map> rows,KeyValue[] keyValues,int index){
		if(index >= keyValues.length) {
			return null;
		}
		Map map = new LinkedHashMap();
		for(Map row : rows) {
			KeyValue keyValue = keyValues[index];
			Object key = row.get(keyValue.getKey());
			Object value = row.get(keyValue.getValue());
			map.put(new DefaultKeyValue(key, value), build(rows,keyValues,index+1));
		}
		return map;
	}
	
}
