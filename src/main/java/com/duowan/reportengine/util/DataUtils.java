package com.duowan.reportengine.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;

import com.duowan.common.beanutils.PropertyUtils;
import com.duowan.common.util.DateConvertUtils;

public class DataUtils {
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private final static DecimalFormat NUM_FORMAT = new DecimalFormat("0.00");

	public static List topNGroupby(List<Map<String,Object>> rows,final int n, final String orderColumn, String...groupByColumns){
		Map<String,TopN> topNMap = new HashMap<String,TopN>();
		Comparator<Map<String, Object>> comp = getComparator(orderColumn);
		for(Map row :rows) {
			String gvalue = getGroupByValue(row,groupByColumns);
			TopN temp = topNMap.get(gvalue);
			if(temp == null) {
				temp = new TopN<Map<String,Object>>(n,comp);
				topNMap.put(gvalue, temp);
			}
			temp.put(row);
		}
		return combineResult(topNMap);
	}

	private static Comparator<Map<String, Object>> getComparator(final String orderColumn) {
		Comparator<Map<String,Object>> comp = new Comparator<Map<String,Object>>() {
				public int compare(Map<String,Object> row1,
						Map<String,Object> row2) {
					Object o1 = PropertyUtils.getNestedProperty(row1, orderColumn);
					Object o2 = PropertyUtils.getNestedProperty(row2, orderColumn);
					if(o1==null && o2 == null) {
						return 0;
					}
					if(o1 == null) {
						return -1;
					}
					if(o2 == null) {
						return 1;
					}
					Double number1 = Double.valueOf(o1.toString());
					Double number2 = Double.valueOf(o2.toString());
					return number1.compareTo(number2);
				}
		};
		return comp;
	}
	
	private static String getGroupByValue(Map row, String...groupByColumns) {
		StringBuffer groupByValue = new StringBuffer();
		for(String column:groupByColumns) {
			groupByValue.append(row.get(column)).append(",");
		}
		return groupByValue.toString();
	}
	
	private static List combineResult( Map<String,TopN> topNMap) {
		Iterator<String> it = topNMap.keySet().iterator();
		List result = new ArrayList();
		while(it.hasNext()) {
			List list = topNMap.get(it.next()).retrieve();
			for(Object o: list) {
				result.add(o);
			}
		}
		return result;
	}
	
	/**
	 * 合并后数据集根据map中的map的某key-value排序
	 * @param dataList 数据集
	 * @param mapCol 根据dataList中的map<map>排序
	 * @param kpi 排序map中的key
	 * @param order 排序方式：asc，desc
	 * @return 排序好的数据集
	 */
	public static List<Map<String, Object>> sortDataList(List<Map<String, Object>> dataList, final String mapCol, final String key, final String order) {
		
		//实现Comparator
		Collections.sort(dataList, new Comparator<Map<String, Object>>() {

			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				Map<String, Object> map1 = (Map<String, Object>) o1.get(mapCol);
				Map<String, Object> map2 = (Map<String, Object>) o2.get(mapCol);
				return sortByKey(key, order, map1, map2);
			}
		});
		return dataList;
	}
	
	private static int sortByKey(final String key, final String order,
			Map<String, Object> map1, Map<String, Object> map2) {
			if (map1.get(key) == null ) {
				return 1;
			}
			else if (map2.get(key) == null) {
				return -1;
			}else {
				return order.equalsIgnoreCase("asc")?Integer.valueOf(map1.get(key).toString()).compareTo(Integer.valueOf(map2.get(key).toString())):Integer.valueOf(map2.get(key).toString()).compareTo(Integer.valueOf(map1.get(key).toString()));
			}
	}
	
	public static List<List<Map<String, Object>>> splitList2Lists(List<Map<String, Object>> dataList){
		final String tdate = "tdate";
		Object object = null;
		List<List<Map<String, Object>>> resultlLists = new ArrayList<List<Map<String,Object>>>();
		List<Map<String, Object>> dList = new ArrayList<Map<String,Object>>();
		int i = dataList.size() - 1;
		for (Map<String, Object> row : dataList) {
			if (object == null) {
				object = row.get(tdate);
				dList.add(row);
			}else if (object.equals(row.get(tdate))) {
				dList.add(row);
				if (i == 0 ) {
					resultlLists.add(dList);
				}
			}else {
				resultlLists.add(dList);
				dList = new ArrayList<Map<String,Object>>();
				dList.add(row);
				if (i == 0 ) {
					resultlLists.add(dList);
				}
				object = row.get(tdate);
			}
			i--;
		}
		return resultlLists;
	}
	
	/**
	 * 把第二数据集合并到第一个
	 * @param rows1
	 * @param rows2
	 * @param combineColumens
	 * @param alias
	 * @param innerColumns
	 * @return
	 */
	public static void innerJoin(List<Map<String,Object>> rows1,List<Map<String,Object>> rows2,String[] combineColumens,String[] alias,String[] innerColumns){
		for (Map<String, Object> targetMap : rows1) {
			for (Map<String, Object> sourceMap : rows2) {
				for (int i = 0; i < innerColumns.length; i++) {
					if (targetMap.get(innerColumns[i]).toString().equals(sourceMap.get(innerColumns[i]).toString())) {
						for (int j = 0; j < combineColumens.length; j++) {
							targetMap.put(alias[i], sourceMap.get(combineColumens[i]));
						}
					}
				}
			}
		}
	}
	
	public static Long multiply(Object number1,Object number2) {
		if (number1 == null || number2 ==null) {
			return 0L;
		}
		return Long.valueOf(number1.toString())*Long.valueOf(number2.toString());
	}
	
	public static Double doubleMultiply(Object number1,Object number2) {
		if (number1 == null || number2 ==null) {
			return 0D;
		}
		double result = Double.valueOf(number1.toString())*Double.valueOf(number2.toString());
		BigDecimal decimal = new BigDecimal(result);
		return decimal.setScale(3, BigDecimal.ROUND_DOWN).doubleValue();
	}
	
	/**
	 * 合并后数据集根据map中的key-value排序
	 * @param dataList 数据集
	 * @param kpi 排序map中的key
	 * @param order 排序方式：asc，desc
	 * @return 排序好的数据集
	 */
	public static List<Map<String, Object>> sortDataList(List<Map<String, Object>> dataList, final String key, final String order) {
		
		//实现Comparator
		Collections.sort(dataList, new Comparator<Object>() {

			public int compare(Object o1, Object o2) {
				Map<String, Object> map1 = (Map<String, Object>) o1;
				Map<String, Object> map2 = (Map<String, Object>) o2;
				return sortByKey(key, order, map1, map2);
			}

		});
		return dataList;
	}
	
	public static Map<String, List<Map<String, Object>>> caseWhenToMapList(List<Map<String, Object>> rows, String key, String[] caseType){
		Map<String, List<Map<String, Object>>> caseMap = new HashMap<String, List<Map<String,Object>>>();
		for (String type : caseType) {
			List<Map<String, Object>> caseList = new ArrayList<Map<String,Object>>();
			for (Map<String, Object> row : rows) {
				if (row.get(key).equals(type)) {
					caseList.add(row);
				}
			}
			caseMap.put(type, caseList);
		}
		return caseMap;
	}
	
	public static void sumColumns(List<Map<String, Object>> rows, String alias, String[] columns ) {
		for(Map<String, Object> row: rows) {
			long sum = 0;
			for(String key :columns) {
				try {
					Object value = PropertyUtils.getNestedProperty(row, key);
					if(value != null) {
						sum += NumberUtils.toLong(value.toString());
					}
				}catch(NestedNullException e) {
					//ignore
				}
			}
		  row.put(alias, sum);
		}
	}
	
	public static List<List<Map<String, Object>>> getRangeDataListsByDay(List<List<Map<String, Object>>> dataLists, Date endDate, int nums){
		final String tdate = "tdate";
		String firstDate = DateConvertUtils.format(DateUtils.addDays(endDate, -nums), DATE_FORMAT);
		List<List<Map<String, Object>>> resultDataLists = new ArrayList<List<Map<String,Object>>>();
		for (List<Map<String, Object>> list : dataLists) {
			for (Map<String, Object> map : list) {
				if ((DateConvertUtils.format((Date)map.get(tdate), DATE_FORMAT)).compareTo(firstDate) > 0) {
					resultDataLists.add(list);
					break;
				}
			}
		}
		return resultDataLists;
	}
	
	/**
	 * 将输入的dataLists中转为单含tdate的List<Map>
	 * @param dataLists
	 * @return
	 */
	public static List<Map<String, Object>> converXByDay (List<List<Map<String, Object>>> dataLists){
		final String tdate = "tdate";
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		for (List<Map<String, Object>> list : dataLists) {
			resultList.addAll(MiscUtil.distinctByCol(list, tdate));
		}
		return resultList;
	}
}