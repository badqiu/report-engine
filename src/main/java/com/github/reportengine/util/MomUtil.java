package com.github.reportengine.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MomUtil {

	public static List<Map> ringUp(List<Map> rows,String addDateType,int days) {
		return ringUp(rows,addDateType,"day",days);
	}
	
	public static List<Map> ringUp(List<Map> rows,String dateKey,String addDateType,int amount) {
		List<Map> resultList = new ArrayList<Map>();
		for(Map row : rows) {
			Map resultRow = new HashMap(row);
			Date date = (Date)row.get(dateKey);
			Date nday = addDate(date,addDateType, amount);
			Map ndayMap = getMapByKey(rows,dateKey,nday);
			resultRow.put(addDateType+Math.abs(amount), ndayMap);
			resultRow.put("pre"+Math.abs(amount), ndayMap);
			
			resultList.add(resultRow);
		}
		return resultList;
	}

	private static Date addDate(Date date,String addDateType, int amount) {
		Date nday = null;
		if("day".equals(addDateType)) {
			nday = org.apache.commons.lang.time.DateUtils.addDays(date, amount);
		}else if("week".equals(addDateType)){
			nday = org.apache.commons.lang.time.DateUtils.addWeeks(date, amount);
		}else if("month".equals(addDateType)){
			nday = org.apache.commons.lang.time.DateUtils.addMonths(date, amount);
		}else if("quarter".equals(addDateType)){
			nday = org.apache.commons.lang.time.DateUtils.addMonths(date, amount * 3);
		}else {
			throw new RuntimeException("unsupport dateType:"+addDateType+". enums is [day,week,month,quarter]");
		}
		return nday;
	}

	private static Map getMapByKey(List<Map> rows, String rowKey, Object rowValue) {
		for(Map row : rows) {
			Object val = row.get(rowKey);
			if(val.equals(rowValue)) {
				return row;
			}
		}
		return null;
	}
}
