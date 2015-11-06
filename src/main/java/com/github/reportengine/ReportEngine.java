package com.github.reportengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;

import com.github.reportengine.model.BaseObject;
import com.github.reportengine.model.Param;
import com.github.reportengine.model.Query;
import com.github.reportengine.model.Report;
import com.github.reportengine.model.Script;
import com.github.reportengine.util.FreeMarkerConfigurationUtil;
import com.github.reportengine.util.MD5Util;
import com.github.reportengine.util.SpringUtil;

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
public class ReportEngine implements InitializingBean,ApplicationContextAware,MessageSourceAware{
	private static Logger logger = LoggerFactory.getLogger(ReportEngine.class);
	public static ThreadLocal<ReportEngine> reportEngineContext = new ThreadLocal<ReportEngine>();
	
	private Map<String,Object> engineContext = new HashMap<String,Object>();
	private File baseReportDir;
	private Configuration conf = FreeMarkerConfigurationUtil.newDefaultConfiguration();
	
	/** report渲染缓存设置 */
	private boolean reportCache=false;
	/** 缓存目录 */
	private String reportCacheDir=null;
	
	/**
	 * i18n国际化使用的MessageSource资源类
	 */
	private MessageSource messageSource = new ResourceBundleMessageSource();
	
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
	
	public boolean isReportCache() {
		return reportCache;
	}

	public void setReportCache(boolean reportCache) {
		this.reportCache = reportCache;
	}

	public String getReportCacheDir() {
		return reportCacheDir;
	}

	public void setReportCacheDir(String reportCacheDir) {
		this.reportCacheDir = reportCacheDir;
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
			Assert.hasText(reportPath,"reportPath must be not empty");
			Report report = getReport(reportPath,params);
			//缓存文件
			File cacheFile=null;
			//读取缓存
			if(isReportCache(report)){
				cacheFile = processParamsAndGetCache(reportPath, params, report);
				if(cacheFile.exists()){
					if(logger.isDebugEnabled()){
						logger.debug("use cache file:"+cacheFile.getAbsolutePath());
					}
					return FileUtils.readFileToString(cacheFile, "UTF-8");
				}
			}
			/*Map<String, Object> model = getTemplateModel(reportPath, params);*/
			Map<String,Object> model = processForModel(report, params);
			model.put("reportPath", reportPath);
			
			Configuration conf = getFreemarkerConfiguration();
			Template reportTemplate = conf.getTemplate(reportPath+".ftl");
			String result = FreeMarkerTemplateUtils.processTemplateIntoString(reportTemplate, model);
			
			//写入缓存
			if(isReportCache(report)){
				FileUtils.writeStringToFile(cacheFile, result, "UTF-8");
				if(logger.isDebugEnabled()){
					logger.debug("write cache file:"+cacheFile.getAbsolutePath());
				}
			}
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
			report.setMessageSource(messageSource);
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
			
			SpringUtil.initializing(report);
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
			processBeforeQueryScript(report,context,processedParams);
			
			processQuerys(report, context, processedParams);
			
			Map afterBindings = processAfterQueryScript(report,context,processedParams);
			
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

	private Map processBeforeQueryScript(Report report, Map<String, Object> context, Map params) throws ScriptException {
		Script g = report.getScript();
		if(g == null) return context;
		String groovyScript = g.getBeforeQuery();
		if(StringUtils.isBlank(groovyScript)) return context;
		return processScript(context, params, groovyScript,g.getLang());
	}
	
	private Map processAfterQueryScript(Report report, Map<String, Object> context, Map params) throws ScriptException {
		Script g = report.getScript();
		if(g == null) return context;
		String groovyScript = g.getAfterQuery();
		if(StringUtils.isBlank(groovyScript)) return context;
		return processScript(context, params, groovyScript,g.getLang());
	}

	private Map processScript(Map<String, Object> context, Map params,
			String script,String lang) throws ScriptException {
		Assert.hasText(lang,"'lang' must be not empty for lookup ScriptEngine,lang:[groovy,javascript,...]");
		try {
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName(lang);
			Bindings bindings = engine.createBindings();
			bindings.put("context", context);
			bindings.put("param", params);
			engine.eval(new StringReader(script), bindings);
			return bindings;
		}catch(ScriptException e) {
			throw new RuntimeException("error eval script:"+script+" params:"+params,e);
		}
	}

	private void processQuerys(Report report, Map<String, Object> context, Map params)
			throws IOException, TemplateException {
		for(Query q : report.getQuerys()) {
			Object result = q.execute(params);
			context.put(q.getId(), result);
			if(result instanceof Collection) {
				context.put(q.getId()+"Sum", q.getAutoSumResult());
				context.put(q.getId()+"Avg", q.getAutoAvgResult());
				if(logger.isDebugEnabled()) {
					logger.debug("query result,"+q.getId()+" size:"+(getSize(result)) +" "+ (q.getId()+"Sum") + ":"+q.getAutoSumResult()+" Avg:" + q.getAutoAvgResult() +" dataList:"+result);
				}
			}else {
				if(logger.isDebugEnabled()) {
					logger.debug("query result,"+q.getId()+" size:"+(getSize(result)) +" dataList:"+result);
				}
			}
		}
	}
	
	private static int getSize(Object result) {
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
		if(reportCache){
			Assert.notNull(reportCacheDir, "reportCacheDir must is not null");
			//生成缓存文件夹
			File reportCacheDirFile=new File(reportCacheDir);
			if(!reportCacheDirFile.exists()){
				reportCacheDirFile.mkdirs();
			}else if(reportCacheDirFile.isFile()){
				throw new RuntimeException("reportCacheDir:"+getReportCacheDir()+" is file!");
			}
			if(!reportCacheDir.endsWith(File.separator)){
				reportCacheDir=reportCacheDir+File.separator;
			}
		}
	}
	
	private boolean isReportCache(Report report) {
		return reportCache&&!report.isNotCache();
	}
	
	/**
	 * 处理缓存参数并获得缓存文件
	 * @param reportPath
	 * @param params
	 * @param report
	 * @return
	 * @throws Exception
	 */
	private File processParamsAndGetCache(String reportPath,
			Map<String, Object> params, Report report) throws Exception {
		reportEngineContext.set(this);
		Map<String,Object> context = new HashMap<String,Object>(params);
		context.putAll(engineContext);
		context.put("report", report);
		context.put("now", new Date());
		context.put("context", context);
		
		report.fireBeforeQueryLiftcycle(context);
		
		Map<String,Object> processedParamMap = processParams(context,report.getParams(),context);
		//放入报表路径
		processedParamMap.put("reportPath", reportPath);
		String hashKey=genReportCacheHashKey(processedParamMap);
		String date=DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		String language=LocaleContextHolder.getLocale().getLanguage();
		return new File(reportCacheDir+date+File.separator+language+File.separator+hashKey+".html");
	}
	
	/**
	 * 生成缓存key
	 * @param reportPath
	 * @param processedParamMap
	 * @return
	 */
	private String genReportCacheHashKey(Map<String, Object> processedParamMap) {
		//对key进行排序，可以防止参数位置发生变化，而导致缓存失效
		List<String> keys=new ArrayList<String>(processedParamMap.keySet());
		Collections.sort(keys);
		StringBuilder sb=new StringBuilder();
		for(String key:keys){
			sb.append(key);
			sb.append("=");
			sb.append(String.valueOf(processedParamMap.get(key)));
			sb.append(";");
		}
		String cacheKey=sb.toString();
		if(logger.isDebugEnabled()){
			logger.debug("cacheKey:"+cacheKey);
		}
		return MD5Util.getMD5String(cacheKey);
	}
	
	/**
	 * 清除缓存文件
	 * @param cleanDate
	 * @throws IOException
	 */
	public void cleanReportCache(String cleanDate) throws IOException {
		File reportCacheDirFile=new File(StringUtils.isNotBlank(cleanDate) ? reportCacheDir+File.separator+cleanDate:reportCacheDir);
		FileUtils.cleanDirectory(reportCacheDirFile);
		logger.info("clean reportCache:"+reportCacheDirFile.getAbsolutePath()+" is success!");
	}
	
	public String getMessage(String code,String... args) {
		return getMessageSource().getMessage(code, args, code,LocaleContextHolder.getLocale());
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	/**
	 * 返回json数据
	 * @param reportPath
	 * @param params
	 * @return
	 */
	public String renderJson(String reportPath, Map params) {
		logger.info("renderJson() reportPath:"+reportPath);
		try {
			Assert.hasText(reportPath,"reportPath must be not empty");
			String objectIdName="elementId";
			Report report = getReport(reportPath,params);
			Map<String,Object> model = processForModel(report, params);
			String objectId=(String) params.get(objectIdName);
			Assert.hasText(objectId,objectIdName+" must be not empty");
			BaseObject baseObject=(BaseObject) report.getElementById(objectId);
			if(baseObject==null){
			   throw new RuntimeException(objectIdName+":"+objectId+" is error,not find it.");
			}
			return baseObject.toJson();
		}catch(Exception e) {
			throw new RuntimeException("renderJson error,reportPath:"+reportPath+" params:"+params,e);
		}
	}
	
}
