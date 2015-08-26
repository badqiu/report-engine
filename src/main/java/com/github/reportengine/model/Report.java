package com.github.reportengine.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.github.reportengine.ReportEngineLifecycle;
import com.github.reportengine.model.Chart.Ser;
import com.github.reportengine.model.Table.Column;
import com.github.reportengine.util.ArrayUtil;
import com.github.reportengine.util.CloneUtil;
import com.github.reportengine.util.RegexHelper;
import com.github.reportengine.util.SelectorUtil;
import com.github.reportengine.util.SpringUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 报表
 * 
 * @author badqiu
 *
 */
public class Report extends BaseObject implements InitializingBean,Cloneable,Serializable{
	private static final long serialVersionUID = 1L;
	private String author; //报表作者
	private String title; //报表标题
	private String help; //报表帮助文档
	private String extend; // 继承某个Report对象;
	private String refDataSource; //引用的公共数据源名称
	
	private long lastModifiedTime = 0; //report对象的最后修改时间,用于内部缓存使用,外部禁止使用
	
	private Script script = new Script();
	
	private Param[] params = new Param[]{};
	private Query[] querys = new Query[]{};
	private Chart[] charts = new Chart[]{};
	private Table[] tables = new Table[]{};
	private Cube[] cubes = new Cube[]{};
	
	private String xml; //report自身的xml
	private String template; //report自身的freemarker template文件内容
	private boolean notCache; //是否不缓存报表
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getHelp() {
		return help;
	}
	public void setHelp(String help) {
		this.help = help;
	}
	public String getRefDataSource() {
		return refDataSource;
	}
	public void setRefDataSource(String dataSource) {
		this.refDataSource = dataSource;
	}
	public Param[] getParams() {
		return params;
	}
	public void setParams(Param[] params) {
		this.params = params;
	}
	public Query[] getQuerys() {
		return querys;
	}
	public void setQuerys(Query[] querys) {
		this.querys = querys;
	}
	public Chart[] getCharts() {
		return charts;
	}
	public void setCharts(Chart[] pics) {
		this.charts = pics;
	}
	public Script getScript() {
		return script;
	}
	public void setScript(Script script) {
		this.script = script;
	}
	public boolean isNotCache() {
		return notCache;
	}
	public void setNotCache(boolean notCache) {
		this.notCache = notCache;
	}
	public Table[] getTables() {
		return tables;
	}
	
	public void setTables(Table[] tables) {
		this.tables = tables;
	}
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	public Cube[] getCubes() {
		return cubes;
	}
	public void setCubes(Cube[] cubes) {
		this.cubes = cubes;
	}
	
	public String getExtend() {
		return extend;
	}
	public void setExtend(String extend) {
		this.extend = extend;
	}
	
	public String getXml() {
		return xml;
	}
	
	public void setXml(String reportXml) {
		this.xml = reportXml;
	}
	
	public String getTemplate() {
		return template;
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public Collection<String> getKpis() {
		return getKpis(xml,template);
	}
	
	public static Collection<String> getKpis(String... strings) {
		LinkedHashSet linkedHashSet = new LinkedHashSet();
		for(String str : strings) {
			linkedHashSet.addAll(RegexHelper.findMatchs(str,"\\{[\\w]+.([\\w_]+)",1));
			linkedHashSet.addAll(RegexHelper.findMatchs(str,"\\([\\w]+.([\\w_]+)",1));
			linkedHashSet.addAll(RegexHelper.findMatchs(str,"y=['\"]([\\w_]+)['\"]",1));
		}
		return linkedHashSet;
	}
	
	public Object getElementById(Object id) {
		if(id instanceof String) {
			return SelectorUtil.getElementById(this,(String)id);
		}else {
			return id;
		}
	}
	
	/**
	 * 从另外一个报表继承属性
	 * @param parent
	 */
	public void extend(Report parent) {
		if(StringUtils.isBlank(getId())) setId(parent.getId());
		if(StringUtils.isBlank(author)) setAuthor(parent.getAuthor());
		if(StringUtils.isBlank(title)) setTitle(parent.getTitle());
		if(StringUtils.isBlank(help)) setHelp(parent.getHelp());
		if(StringUtils.isBlank(refDataSource)) setRefDataSource(parent.getRefDataSource());
		if(script == null) setScript(parent.getScript());
		
		setQuerys(ArrayUtil.addAll(parent.getQuerys(),this.querys));
		setParams(ArrayUtil.addAll(parent.getParams(),this.params));
		setCharts(ArrayUtil.addAll(parent.getCharts(),this.charts));
		setCubes(ArrayUtil.addAll(parent.getCubes(),this.cubes));
		setTables(ArrayUtil.addAll(parent.getTables(),this.tables));
	}
	
	@Override
	public String toString() {
		return "Report [id=" + getId() + ", author=" + author + ", title=" + title
				+ ", refDataSource=" + refDataSource + "]";
	}
	
	public void afterPropertiesSet() throws Exception {
		setRefDataSourceProperty(params);
		setRefDataSourceProperty(querys);
		setRefDataSourceProperty(charts);
		setRefDataSourceProperty(cubes);
		setRefDataSourceProperty(tables);
		
		SpringUtil.initializing(params);
		SpringUtil.initializing(querys);
		SpringUtil.initializing(charts);
		SpringUtil.initializing(script);
		SpringUtil.initializing(cubes);
		SpringUtil.initializing(tables);
	}
	
	private void setRefDataSourceProperty(Query[] array) {
		if(array == null)
			return;
		for(Query q : array) {
			setRefDataSourceIfBlank(q);
		}
	}
	
	private void setRefDataSourceIfBlank(Query q) {
		if(q == null) return;
		
		if(StringUtils.isBlank(q.getRefDataSource())) {
			q.setRefDataSource(refDataSource);
		}
	}
	
	private void setRefDataSourceProperty(BaseDataListObject[] array) {
		if(array == null) return;
		
		for(BaseDataListObject q : array) {
			setRefDataSourceIfBlank(q.getQuery());
		}
	}	
	
	public static Template newSyntaxFreemarkerTemplate(Reader reader,Configuration conf) throws IOException {
        String templateString = IOUtils.toString(reader);
        conf.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
        templateString = StringUtils.replace(templateString, "${", "${'$'}{");
        templateString = StringUtils.replace(templateString, "@{", "${");
        Template t = new Template("A",new StringReader(templateString),conf);
        return t;
	}
	
	public static Report parse(Configuration conf,InputStream input){
		return parse(conf,input,System.getProperties());
	}
	
	public static Report parse(Configuration conf,InputStream input,Map model) {
		try {
			Template template = newSyntaxFreemarkerTemplate(new InputStreamReader(input,"UTF-8"),conf);
			String xml = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			XStream xstream = buildReportXStream();
			Report report = (Report)xstream.fromXML(xml);
			report.setXml(xml);
			SpringUtil.initializing(report);
			return report;
		}catch(Exception e) {
			throw new RuntimeException("cannot parse report from input:"+input,e);
		}
	}
	
	private static XStream buildReportXStream() {
		XStream xstream = new XStream(new PureJavaReflectionProvider(),new DomDriver());
		xstream.useAttributeFor(int.class);
		xstream.useAttributeFor(long.class);
		xstream.useAttributeFor(char.class);
		xstream.useAttributeFor(float.class);
		xstream.useAttributeFor(double.class);
		xstream.useAttributeFor(boolean.class);
		xstream.useAttributeFor(Integer.class);
		xstream.useAttributeFor(Long.class);
		xstream.useAttributeFor(Character.class);
		xstream.useAttributeFor(Float.class);
		xstream.useAttributeFor(Double.class);
		xstream.useAttributeFor(Boolean.class);
		xstream.useAttributeFor(String.class);
		
		xstream.alias("report", Report.class);
		xstream.alias("query", Query.class);
		xstream.alias("param", Param.class);
		xstream.alias("script", Script.class);
		xstream.alias("cube", Cube.class);
		
		xstream.alias("chart", Chart.class);
		xstream.alias("ser", Ser.class);
		
		xstream.alias("table", Table.class);
		xstream.alias("column", Column.class);
		return xstream;
	}
	
	public void fireAfterQueryLiftcycle(Map<String,Object> context) throws Exception {
		fireAfterQueryLiftcycle(params,context);
		fireAfterQueryLiftcycle(querys,context);
		fireAfterQueryLiftcycle(charts,context);
		fireAfterQueryLiftcycle(tables,context);
		fireAfterQueryLiftcycle(cubes,context);
	}
	
	public void fireBeforeQueryLiftcycle(Map<String, Object> context) throws Exception {
		fireBeforeQueryLiftcycle(params,context);
		fireBeforeQueryLiftcycle(querys,context);
		fireBeforeQueryLiftcycle(charts,context);
		fireBeforeQueryLiftcycle(tables,context);
		fireBeforeQueryLiftcycle(cubes,context);
	}
	
	public static void fireAfterQueryLiftcycle(Object[] array,Map<String, Object> context) {
		if(array == null) return;
		
		for(Object item : array) {
			if(item instanceof ReportEngineLifecycle) {
				try {
					((ReportEngineLifecycle)item).afterQuery(context);
				} catch (Exception e) {
					throw new RuntimeException("Lifecycle fire error on Object:"+item.getClass().getName(),e);
				}
			}
		}
	}
	
	public static void fireBeforeQueryLiftcycle(Object[] array,Map<String, Object> context) {
		if(array == null) return;
		
		for(Object item : array) {
			if(item instanceof ReportEngineLifecycle) {
				try {
					((ReportEngineLifecycle)item).beforeQuery(context);
				} catch (Exception e) {
					throw new RuntimeException("Lifecycle fire error on Object:"+item.getClass().getName(),e);
				}
			}
		}
	}
	
	@Override
	public Report clone()  {
		try {
			return (Report)super.clone();
		} catch (CloneNotSupportedException e) {
			//ignore
			return null;
		}
	}
	
	public Report deepClone() {
		return CloneUtil.deepClone(this);
	}
	
	private Map elements = null;
	public Map getElements() {
		if(elements == null) {
			elements = new LinkedHashMap();
			putItems(elements,params);
			putItems(elements,tables);
			putItems(elements,charts);
			putItems(elements,querys);
			putItems(elements,cubes);
		}
		return elements;
	}
	
	private static void putItems(Map result,BaseObject[] items) {
		if(items == null) return;
		for(BaseObject p : items) {
			if(p != null) {
				result.put(p.getId(), p);
			}
		}
	}

	
}
