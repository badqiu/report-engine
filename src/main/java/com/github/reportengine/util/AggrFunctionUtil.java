package com.github.reportengine.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;
/**
 * 聚集函数
 * 
 * @author badqiu
 *
 */
public class AggrFunctionUtil {

//	public static aggr(List<Map> rows,String aggrFunc,String key) {
//		
//	}
	
	public static int count(List<Map> rows,String key) {
		if("*".equals(key)) {
			return rows.size();
		}
		
		int count = 0;
		for(Map row : rows) {
			Object value = row.get(key);
			if(value != null) {
				count++;
			}
		}
		return count;
	}

	public static Object min(List<Map> rows,String key) {
		Comparable min = null;
		for(Map row : rows) {
			Object value = row.get(key);
			if(value instanceof Comparable) {
				Comparable compValue = (Comparable)value;
				if(min == null) {
					min = compValue;
				}
				if(compValue.compareTo(min) < 0) {
					min = compValue;
				}
			}
			
		}
		return min;
	}

	public static Object max(List<Map> rows,String key) {
		Comparable max = null;
		for(Map row : rows) {
			Object value = row.get(key);
			if(value instanceof Comparable) {
				Comparable compValue = (Comparable)value;
				if(max == null) {
					max = compValue;
				}
				if(compValue.compareTo(max) > 0) {
					max = compValue;
				}
			}
			
		}
		return max;
	}

	public static Object first(List<Map> rows,String key) {
		if(rows.isEmpty()) {
			return null;
		}
		return rows.get(0).get(key);
	}
	
	public static Object last(List<Map> rows,String key) {
		if(rows.isEmpty()) {
			return null;
		}
		return rows.get(rows.size()-1).get(key);
	}
	
	public static double avg(List<Map> rows,String key) {
		double sum = 0;
		int count = 0;
		for(Map row : rows) {
			Object value = row.get(key);
			if(value instanceof Number) {
				count++;
				sum += ((Number)value).doubleValue();
			}
		}
		return sum / count;
	}
	
	public static double sum(List<Map> rows,String key) {
		double sum = 0;
		for(Map row : rows) {
			Object value = row.get(key);
			if(value instanceof Number) {
				sum += ((Number)value).doubleValue();
			}
		}
		return sum;
	}
	
	public static Map<String,Object> autoSumAggr(List<Map<String,Object>> rows) {
		if(CollectionUtils.isEmpty(rows)) {
			return Collections.EMPTY_MAP;
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		for(Map.Entry<String, Object> entry : rows.get(0).entrySet()) {
			Object notNullValue = getNotNullValue(entry.getKey(),rows);
			if(notNullValue != null && notNullValue instanceof Number) {
				List tempRows = rows;
				double sum = sum(tempRows,entry.getKey());
				result.put(entry.getKey(), sum);
			}else {
				result.put(entry.getKey(), (double)0);
			}
		}
		return result;
	}

	public static Object getNotNullValue(Object key, List<Map<String, Object>> rows) {
		for(Map<String,Object> row : rows) {
			Object value = row.get(key);
			if(value != null) {
				return value;
			}
		}
		return null;
	}
}
