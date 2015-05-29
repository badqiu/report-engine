package com.github.reportengine.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ListUtil {

	public static Map<Object,Map> list2Map(Collection<Map> rows,String mapKeyColumn) {
		if(rows == null) return null;
		
		Map result = new HashMap();
		for(Map row : rows) {
			Object key = row.get(mapKeyColumn);
			if(key != null) {
				result.put(key, row);
			}
		}
		return result;
	}
	
	/**
	 * 将Map中的array 转换成 list,应用场景：spring in (:param) 不支持param是Array,只支持是List
	 * @param params
	 * @return
	 */
	public static Map array2list(Map params) {
		Map result = new HashMap();
		Set<Map.Entry> entrySet = params.entrySet();
		for(Map.Entry entry : entrySet) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			if(value != null && value.getClass().isArray()) {
				value = Arrays.asList((Object[])value);
			}
			result.put(key, value);
		}
		return result;
	}
	
}
