package com.github.reportengine.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import com.github.reportengine.util.CloneUtil;
import com.github.reportengine.util.FreeMarkerConfigurationUtil;


public class ReportTest extends Assert{
	String reportFile = "classpath:report_conf/demo/simple_report.xml";
	
	Report r;
	
	@Before
	public void before() throws Exception {
		File file = ResourceUtils.getFile(reportFile);
		r = Report.parse(FreeMarkerConfigurationUtil.newDefaultConfiguration(),new FileInputStream(file));
	}
	
	@Test
	public void test_parse() throws FileNotFoundException {
		assertEquals(r.getTitle(),"测试报表标题");
		assertEquals(r.getProps().toString(),"{sex=1}");
	}

	@Test
	public void test_Boolean_property() throws FileNotFoundException {
		Param param = (Param)r.getElementById("hiddenGame");
		assertTrue(param.getHidden());
		assertTrue(param.getDistinct());
	}
	
	@Test
	public void test_getElementById() throws FileNotFoundException {
		Table t = (Table)r.getElementById("table1");
		assertNotNull(t);
		
		Chart pieChart = (Chart)r.getElementById("pieChart");
		assertNotNull(pieChart.getId(),"pieChart");
		
		Report cloneReport = r.clone();
		assertEquals(cloneReport.getGroovy(),r.getGroovy());
		
		cloneReport = CloneUtil.deepClone(r);
		assertFalse(cloneReport.getGroovy() == r.getGroovy());
	}
	
	@Test
	public void test_getKpis() {
		printKpis("<column value='${ViewUtils.percent(row.pur)}' label='PUR'/>","[percent, pur]");
		printKpis("<column value='${row.pur}' label='PUR'/>","[pur]");
		printKpis("<ser y=\"avg_val\"/>","[avg_val]");
		printKpis("<ser y='avg_val'/>","[avg_val]");
	}

	private void printKpis(String input,String assertResult) {
		Collection<String> kpis = Report.getKpis(input);
		System.out.println(kpis);
		assertEquals(kpis.toString(),assertResult);
	}
	
}
