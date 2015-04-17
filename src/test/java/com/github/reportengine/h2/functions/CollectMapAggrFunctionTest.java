package com.github.reportengine.h2.functions;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.h2.Driver;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.github.reportengine.ReportEngineTest;

public class CollectMapAggrFunctionTest {

	DriverManagerDataSource ds = new DriverManagerDataSource();
	@Before
	public void before() {
		ds.setDriverClassName(Driver.class.getName());
		ds.setUrl("jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM '"+ReportEngineTest.h2TableSqlFile.getAbsolutePath().replace('\\', '/')+"'");
		ds.setUsername("sa");
		ds.setPassword("");
		new JdbcTemplate(ds).execute("CREATE AGGREGATE collect_map FOR \"com.duowan.reportengine.h2.functions.CollectMapAggrFunction\"");
		new JdbcTemplate(ds).execute("CREATE ALIAS map FOR \"com.duowan.reportengine.h2.functions.H2Functions.string_map\"");
	}
	
	@Test
	public void test() {
		
		
		List rows = new JdbcTemplate(ds).queryForList("select tdate, collect_map(map(game,click)) click_map from st_blog group by tdate");
		System.out.println(rows);
		assertEquals(rows.toString(),"[{TDATE=2013-08-15 00:00:00.0, CLICK_MAP={ddt=120, sxd=10}}, {TDATE=2013-08-16 00:00:00.0, CLICK_MAP={dfw=24}}, {TDATE=2013-08-14 00:00:00.0, CLICK_MAP={ddt=100}}]");
		
		rows = new JdbcTemplate(ds).queryForList("select tdate,collect_map(click_map) from (select tdate, map(game,click) click_map from st_blog) group by tdate");
		System.out.println(rows);
	}
	
	@Test
	public void test_multi_collect_map() {
		List rows = new JdbcTemplate(ds).queryForList("select tdate,collect_map(game,product) tdate_map from st_blog group by tdate");
		System.out.println(rows);
		rows = new JdbcTemplate(ds).queryForList("select collect_map(tdate,tdate_map) top_map from  (  select tdate,collect_map(game,product) tdate_map from st_blog group by tdate) group by tdate");
		System.out.println(rows);
		
		rows = new JdbcTemplate(ds).queryForList("select tdate,collect_map('123456789012345678901234567890',product) tdate_map from st_blog group by tdate");
		System.out.println(rows);
	}

}
