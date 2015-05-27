package com.github.reportengine.util;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class MomUtil {

	public static void ringUp(List<Map> rows,String dateKey,int days) {
		for(Map row : rows) {
			Date date = (Date)row.get(dateKey);
			Date nday = org.apache.commons.lang.time.DateUtils.addDays(date, days);
			Map ndayMap = getMapByKey(rows,dateKey,nday);
			row.put("nday"+Math.abs(days), ndayMap);
		}
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
