package com.github.reportengine.util;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class MomUtil {

	public static void ringUp(List<Map> rows,String addDateType,int days) {
		ringUp(rows,addDateType,"day",days);
	}
	
	public static void ringUp(List<Map> rows,String dateKey,String addDateType,int amount) {
		for(Map row : rows) {
			Date date = (Date)row.get(dateKey);
			Date nday = addDate(date,addDateType, amount);
			Map ndayMap = getMapByKey(rows,dateKey,nday);
			row.put(addDateType+Math.abs(amount), ndayMap);
		}
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
