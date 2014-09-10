package com.duowan.reportengine.util;

import java.util.List;
import java.util.Map;
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
}
