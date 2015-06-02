package com.github.reportengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.ResourceUtils;

import com.github.rapid.common.util.DateConvertUtil;
import com.github.reportengine.model.Param;


public class ReportEngineTest extends Assert{
	
	ReportEngine engine = new ReportEngine();
	
	public static File h2TableSqlFile = getFile("classpath:h2_table.sql");
	private static File getFile(String file)  {
		try {
			return ResourceUtils.getFile(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void test_processParams() {
		Param[] defs = new Param[]{
				new Param("startDate",null,"${now?string('yyyy-MM-dd')}","date",true,true,"date")
		};
		Map params = new HashMap();
		Map context = new HashMap();
		context.put("now", DateConvertUtil.parse("1999-1-20 20:20:20", "yyyy-MM-dd HH:mm:ss"));
		Map<String,Object> result = engine.processParams(params, defs, context);
		assertEquals(DateConvertUtil.parse("1999-1-20", "yyyy-MM-dd"),result.get("startDate"));
	}
	
	@Before
	public void before() throws Exception {
		reportEngineEnvSetting();
//		DriverManagerDataSource userDs = new DriverManagerDataSource();
//		userDs.setDriverClassName("org.h2.Driver");
//		userDs.setUsername("sa");
//		userDs.setPassword("");
//		userDs.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM '"+h2TableSqlFile.getAbsolutePath().replace('\\', '/')+"'");
//		
//		engine.setBaseReportDir(ResourceUtils.getFile("classpath:report_conf").getAbsolutePath());
//		engine.getEngineContext().put("userDs",userDs);
//		engine.getEngineContext().put("ctx","/svnroot/gas/trunk/web/src/main/webapp/");
//		engine.afterPropertiesSet();
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/spring/report_engine/*.xml");
		engine = ctx.getBean("reportEngine",ReportEngine.class);
	}

	
	public static void reportEngineEnvSetting() throws FileNotFoundException {
		System.out.println("h2TableSqlFile: "+ h2TableSqlFile.getAbsolutePath());
		System.setProperty("h2_table_sql_file", h2TableSqlFile.getAbsolutePath().replace("\\", "/"));
		System.setProperty("pageSize", "2");
	}
	
	@Test
	public void renderReport() throws IOException {
		Map params = new HashMap();
		params.put("startDate", "1998-1-1");
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setQueryString("");
		params.put("request", request);
		
		
		String report = engine.renderReport("demo/simple_report", params);
		System.out.println("result:"+report);
		
		params.put("reportPath", "demo/simple_report");
		params.put("param", params);
		String parameter = engine.renderParameter("demo/simple_report", params);
		System.out.println("parameter:"+parameter);
//		FileUtils.writeStringToFile(new File("E:/svnroot/duowan-report-engine/src/main/webapp/report/simple_report.html"), string);
	}
}
