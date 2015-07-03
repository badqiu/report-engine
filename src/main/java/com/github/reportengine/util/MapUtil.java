package com.github.reportengine.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class MapUtil {

	public static double sumValues(Map<String,Object> map) {
		double sum = 0;
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			Object objValue = entry.getValue();
			if(objValue instanceof Number) {
				sum += ((Number)objValue).doubleValue();
			}
		}
		return sum;
	}
	
	/**
	 * 将所有key 转换为小写
	 * @param list
	 * @return
	 */
	public static List<Map<String, Object>> allMapKey2LowerCase(
			List<Map<String, Object>> list) {
		List<Map<String,Object>> result = new ArrayList(list.size());
		for(Map<String,Object> row : list) {
			Map newRow = new HashMap();
			for(Map.Entry<String, Object> entry : row.entrySet()) {
				newRow.put(StringUtils.lowerCase(entry.getKey()), entry.getValue());
			}
			result.add(newRow);
		}
		return result;
	}
	
	public static Map newMap(Object... args) {
		Map map = new HashMap();
		for(int i = 0; i < args.length; i+=2) {
			map.put(args[i], args[i+1]);
		}
		return map;
	}
	
	public static Map newLinkedMap(Object... args) {
		Map map = new LinkedHashMap();
		for(int i = 0; i < args.length; i+=2) {
			map.put(args[i], args[i+1]);
		}
		return map;
	}
}
