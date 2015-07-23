package com.github.reportengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;

import com.github.reportengine.model.Groovy;
import com.github.reportengine.model.Param;
import com.github.reportengine.model.Query;
import com.github.reportengine.model.Report;
import com.github.reportengine.util.AggrFunctionUtil;
import com.github.reportengine.util.FreeMarkerConfigurationUtil;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * 简单的报表引擎
 * 
 * @author badqiu
 *
 */
public class ReportEngine implements InitializingBean,ApplicationContextAware{
	private static Logger logger = LoggerFactory.getLogger(ReportEngine.class);
	public static ThreadLocal<ReportEngine> reportEngineContext = new ThreadLocal<ReportEngine>();
	
	private Map<String,Object> engineContext = new HashMap<String,Object>();
	private File baseReportDir;
	private Configuration conf = FreeMarkerConfigurationUtil.newDefaultConfiguration();
	
	public ReportEngine() {
	}
	
	public void setBaseReportDir(File baseReportDir) {
		this.baseReportDir = baseReportDir;
	}
	
	public void setEngineContext(Map<String, Object> engineContext) {
		this.engineContext = engineContext;
	}
	
	public Map<String, Object> getEngineContext() {
		return engineContext;
	}
	
	/**
	 * 渲染报表
	 * @param reportPath
	 * @param params
	 * @return
	 */
	public String renderReport(String reportPath,Map<String,Object> params) {
		logger.info("renderReport() reportPath:"+reportPath);
		try {
			Map<String, Object> model = getTemplateModel(reportPath, params);
			
			Configuration conf = getFreemarkerConfiguration();
			Template reportTemplate = conf.getTemplate(reportPath+".ftl");
			String result = FreeMarkerTemplateUtils.processTemplateIntoString(reportTemplate, model);
			
			return result;
		}catch(Exception e) {
			throw new RuntimeException("renderReport error,reportPath:"+reportPath+" params:"+params,e);
		}
	}
	
	/**
	 * 渲染报表参数
	 * @param reportPath
	 * @param params
	 * @return
	 */
	public String renderParameter(String reportPath, Map params) {
		try {
			Map<String,Object> model = getTemplateModel(reportPath,params);
			
			Configuration conf = getFreemarkerConfiguration();
			Template template = conf.getTemplate("/parameter.ftl");
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		}catch(Exception e) {
			throw new RuntimeException("renderParameter error,reportPath:"+reportPath+" params:"+params,e);
		}
	}
	
	/**
	 * 下载报表的table为Csv
	 * @param reportPath
	 * @param params
	 * @return
	 */
	public String download(String reportPath, Map params) {
		try {
			Map<String,Object> model = getTemplateModel(reportPath,params);
			
			Configuration conf = getFreemarkerConfiguration();
			Template template = conf.getTemplate("/download.ftl");
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		}catch(Exception e) {
			throw new RuntimeException("renderParameter error,reportPath:"+reportPath+" params:"+params,e);
		}
	}
	
	public Map<String, Object> getTemplateModel(String reportPath,Map<String, Object> params) throws IOException {
		Assert.hasText(reportPath,"reportPath must be not empty");
		Report report = getReport(reportPath,params);
		Map<String,Object> context = processForModel(report, params);
		context.put("reportPath", reportPath);
		
		return context;
	}

//	private Map<String,Report> reportCache = new ConcurrentHashMap<String,Report>();
	public Report getReport(String reportPath) throws IOException {
		return getReport(reportPath,System.getProperties());
	}
	
	public Report getReport(String reportPath,Map model) throws IOException {
		Assert.hasText(reportPath,"reportPath must be not empty");
		reportPath = reportPath.trim();
		
		File reportXmlFile = new File(baseReportDir,reportPath+".xml");
		File reportTemplateFile = new File(baseReportDir,reportPath+".ftl");
//		Report report = reportCache.get(reportPath);
		Report report = null;
		if(report == null || reportXmlFile.lastModified() != report.getLastModifiedTime()) {
			report = newReport(model, reportXmlFile,reportTemplateFile);
//			reportCache.put(reportPath, report);
		}
		return report.deepClone();
	}

	private Report newReport(Map model, File reportXmlFile,File reportTemplateFile)
			throws FileNotFoundException {
		Report report;
		InputStream input = new FileInputStream(reportXmlFile);
		try {
			report = Report.parse((Configuration)conf.clone(),input,model);
			report.setLastModifiedTime(reportXmlFile.lastModified());
			
			if(StringUtils.isNotBlank(report.getExtend())) {
				String[] parentReportPaths = StringUtils.split(report.getExtend(),",");
				for(String parentReportPath : parentReportPaths) {
					Report parent = getReport(parentReportPath,model);
					report.extend(parent);
				}
			}
			
			if(reportTemplateFile.exists()) {
				report.setTemplate(FileUtils.readFileToString(reportTemplateFile));
			}
		}catch (Exception e){
			throw new RuntimeException("cannot parse report xml file:"+reportXmlFile,e);
		}finally {
			IOUtils.closeQuietly(input);
		}
		return report;
	}

	public Configuration getFreemarkerConfiguration() throws IOException {
		return conf;
	}
	
	/**
	 * 通过输入参数 处理并返回 生成的 model,以便通过model渲染模板
	 * @param report
	 * @param params
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 * @throws ScriptException
	 */
	public Map<String,Object> processForModel(Report report,Map<String,Object> params) {
		reportEngineContext.set(this);
		try {
			Map<String,Object> context = new HashMap<String,Object>(params);
			context.putAll(engineContext);
			context.put("report", report);
			context.put("now", new Date());
			context.put("context", context);
			
			report.fireBeforeQueryLiftcycle(context);
			
			Map processedParams = processParams(context,report.getParams(),context);
			processGroovyBeforeQuery(report,context,processedParams);
			
			processQuerys(report, context, processedParams);
			
			Map afterBindings = processGroovyAfterQuery(report,context,processedParams);
			
			report.fireAfterQueryLiftcycle(context);
			context.put("elements", report.getElements());
			context.putAll(processedParams);
			
			return returnContext(context, afterBindings);
		}catch(Exception e) {
			throw new RuntimeException("processForModel error,report:"+report+" params:"+params,e);
		}finally {
			reportEngineContext.set(null);
		}
	}

	private Map<String, Object> returnContext(Map<String, Object> context,Map afterBindings) {
		Map result = new HashMap(afterBindings);
		result.putAll(getEngineContext());
		result.putAll(context);
		return result;
	}

	private Map processGroovyBeforeQuery(Report report, Map<String, Object> context, Map params) throws ScriptException {
		Groovy g = report.getGroovy();
		if(g == null) return context;
		String groovyScript = g.getBeforeQuery();
		return processGroovyScript(context, params, groovyScript);
	}
	
	private Map processGroovyAfterQuery(Report report, Map<String, Object> context, Map params) throws ScriptException {
		Groovy g = report.getGroovy();
		if(g == null) return context;
		String groovyScript = g.getAfterQuery();
		return processGroovyScript(context, params, groovyScript);
	}

	private Map processGroovyScript(Map<String, Object> context, Map params,
			String groovyScript) throws ScriptException {
		try {
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName("groovy");
			Bindings bindings = engine.createBindings();
			bindings.put("context", context);
			bindings.put("param", params);
			engine.eval(new StringReader(groovyScript), bindings);
			return bindings;
		}catch(ScriptException e) {
			throw new RuntimeException("error eval script:"+groovyScript+" params:"+params,e);
		}
	}

	private void processQuerys(Report report, Map<String, Object> context, Map params)
			throws IOException, TemplateException {
		for(Query q : report.getQuerys()) {
			Object result = q.execute(params);
			context.put(q.getId(), result);
			if(result instanceof Collection) {
				Map sumArrgMap = q.getAutoSumResult();
				String sumId = q.getId()+"Sum";
				context.put(sumId, sumArrgMap);
				if(logger.isDebugEnabled()) {
					logger.debug("query result,"+q.getId()+" size:"+(getSize(result)) +" "+ sumId + ":"+sumArrgMap+" dataList:"+result);
				}
			}else {
				if(logger.isDebugEnabled()) {
					logger.debug("query result,"+q.getId()+" size:"+(getSize(result)) +" dataList:"+result);
				}
			}
		}
	}
	
	private int getSize(Object result) {
		if(result == null) return 0;
		if(result instanceof Collection) {
			return ((Collection)result).size();
		}else {
			return 1;
		}
	}

	Map<String,Object> processParams(Map<String, Object> params, Param[] paramDefs,Map context) {
		Map<String,Object> result = new HashMap<String,Object>();
		for(Param p : paramDefs) {
			try {
				Object rawValue = params.get(p.getId());
				Object stringValue = getStringParamValue(rawValue, context, p);
				Object value = parseParamValue(p,stringValue);
				p.setValue(value);
				p.setRawValue(rawValue);
				p.setStringValue(stringValue);
				result.put(p.getId(), value);
			}catch(Exception e) {
				throw new RuntimeException("param error,param:"+p.getId()+" cause:"+e,e);
			}
		}
		return result;
	}

	private Object getStringParamValue(Object rawValue, Map context,Param p) throws IOException,TemplateException {
		if(rawValue == null || rawValue instanceof String) {
			String defaultValue = processForParamDefaultValue(context, p);
			String stringRawValue = (String)rawValue;
			String stringValue = StringUtils.isBlank(stringRawValue) ? defaultValue : stringRawValue;
			if(StringUtils.isBlank(stringValue) && p.getRequired()) {
				throw new RuntimeException(p.getId() + " param is required");
			}
			return stringValue;
		}else {
			return rawValue;
		}
	}
	
	private Object parseParamValue(Param p,Object stringRawValue) throws IOException,
			TemplateException {
		if(stringRawValue == null || stringRawValue instanceof String) {
			Object parsedValue = Param.Util.parseValue(p, (String)stringRawValue , null);
			return parsedValue;
		}else {
			return stringRawValue;
		}
	}

	private String processForParamDefaultValue(Map context, Param p) throws IOException, TemplateException {
		if(StringUtils.isNotBlank(p.getDefaultValue())) {
			Template defaultValueTemplate = new Template("param",new StringReader(p.getDefaultValue()),getFreemarkerConfiguration());
			return FreeMarkerTemplateUtils.processTemplateIntoString(defaultValueTemplate, context);
		}
		return null;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		for (String name : applicationContext.getBeanDefinitionNames()) {
			try {
				engineContext.put(name, applicationContext.getBean(name));
			}catch(BeanIsAbstractException e) {
				//ignore
			}
		}
	}
	
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(baseReportDir,"baseReportDir must be not null");
		conf.setTemplateLoader(new FileTemplateLoader(baseReportDir));
		logger.info("ReportEngine inited, baseReportDir:"+baseReportDir+" engineContext.keys:"+engineContext.keySet());
	}

	
}
