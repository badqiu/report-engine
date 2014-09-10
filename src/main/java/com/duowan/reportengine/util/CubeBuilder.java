package com.duowan.reportengine.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
/**
 * 生成多维数据集工具类
 * 
 * 
 * 输入数据: tdate,product,game,kpi,kpi_cnt,kpi_ucnt
 * 输出数据: 
 * 
 * @author badqiu
 *
 */
public class CubeBuilder {

	public Map buildCube(List<Map> rows,String aggrColumns,String[] dimensions) {
		return (Map)buildCube(rows,aggrColumns,dimensions,0);
	}
	
	private Object buildCube(List<Map> rows,String aggrColumns,String[] dimensions,int groupByKeyIndex) {
		if(groupByKeyIndex >= dimensions.length) {
			return aggr(dimensions,aggrColumns,rows);
		}
		
		Map<Object,List<Map>> dim = groupByKey(rows,dimensions[groupByKeyIndex]);
		Set entrySet = dim.entrySet();
		for(Object e : entrySet) {
			Map.Entry entry = (Map.Entry)e;
			List<Map> subRows = (List<Map>)entry.getValue();
			
			Object subDim = buildCube(subRows,aggrColumns,dimensions,groupByKeyIndex+1);
			entry.setValue(subDim);
		}
		
		return dim;
	}
	
	private Object aggr(String[] keys,String aggrColumns,List<Map> rows) {
		if(rows == null || rows.isEmpty()) {
			return Collections.EMPTY_MAP;
		}
		
		String sql = "select "+aggrColumns+" from "+ObjectSqlQueryUtil.TABLE_NAME+" group by "+StringUtils.join(keys,",");
		List<Map<String,Object>> result = ObjectSqlQueryUtil.query(sql, (List)rows);
		if(result.size() == 1) {
			return result.get(0);
		}else {
			return result;
		}
	}

	private Map<Object,List<Map>> groupByKey(Collection<Map> rows, String key) {
		Map<Object,List<Map>> dim = new LinkedHashMap<Object,List<Map>>();
		for(Map row : rows) {
			Object value = row.get(key);
			if(value == null) {
				throw new RuntimeException("groupByKey error,dim value is null by key:"+key);
			}
			List<Map> dimRows = dim.get(value);
			if(dimRows == null) {
				dimRows = new ArrayList<Map>();
				dim.put(value, dimRows);
			}
			dimRows.add(row);
		}
		return dim;
	}
	
	private class Aggr {
		String aggrColumn;
		String arrgFun;
	}
}
