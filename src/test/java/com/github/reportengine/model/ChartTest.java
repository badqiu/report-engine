package com.github.reportengine.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.rapid.common.util.DateConvertUtils;
import com.github.reportengine.util.MapUtil;
import com.github.reportengine.util.ObjectSqlQueryUtil;

public class ChartTest {
	Chart chart = new Chart();
	List rows = new ArrayList();
	@Before
	public void before() {
		chart.setX("date");
		rows.add(MapUtil.newMap("date",DateConvertUtils.parse("1999-1-1", "yyyy-MM-dd")));
		rows.add(MapUtil.newMap("date",DateConvertUtils.parse("1999-1-3", "yyyy-MM-dd")));
		rows.add(MapUtil.newMap("date",DateConvertUtils.parse("1999-1-7", "yyyy-MM-dd")));
	}
	
	@Test
	public void test_asc() {
		chart.fillLossDateData(rows);
		printDateList(rows,"date");
	}

	@Test
	public void test_desc() {
		rows = ObjectSqlQueryUtil.query("select * from t order by date desc", rows);
		System.out.println("desc rows:");
		printDateList(rows,"date");
		System.out.println("----------------------------");
		chart.fillLossDateData(rows);
		printDateList(rows,"date");
	}
	
	private void printDateList(List<Map> rows,String key) {
		for(Map row : rows) {
			Date date  = (Date)row.get(key);
//			row.remove(key);
			String dateString = DateConvertUtils.format(date, "yyyy-MM-dd");
			System.out.println(dateString+" => " + row);
		}
	}
}
