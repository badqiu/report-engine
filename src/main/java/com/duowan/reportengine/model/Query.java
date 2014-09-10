package com.duowan.reportengine.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;

import com.duowan.reportengine.ReportEngineLifecycle;
import com.duowan.reportengine.util.FreeMarkerConfigurationUtil;
import com.duowan.reportengine.util.FreemarkerUtil;
import com.duowan.reportengine.util.ObjectSqlQueryUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 报表查询SQL语名
 * 
 * @author badqiu
 *
 */
public class Query extends BaseObject implements InitializingBean,ReportEngineLifecycle,Cloneable,Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * 查询结果是否单条对象
	 */
	private boolean singleRow;
	/**
	 * 查询SQL
	 */
	private String sql;
	/**
	 * 对sql查询回来的结果集，进行二次查询，二次查询的sql语法为H2数据库语法
	 */
	private String requerySql;
	/**
	 * 数据源名称
	 */
	private String refDataSource;	
	/**
	 * 引用的数据源
	 */
	private transient DataSource dataSource;
	
	public String getRefDataSource() {
		return refDataSource;
	}
	public void setRefDataSource(String refDataSource) {
		this.refDataSource = refDataSource;
	}
	public boolean isSingleRow() {
		return singleRow;
	}
	public void setSingleRow(boolean singleRow) {
		this.singleRow = singleRow;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getRequerySql() {
		return requerySql;
	}
	public void setRequerySql(String requerySql) {
		this.requerySql = requerySql;
	}
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(sql,"sql must be not empty");
	}
	
	@Override
	public Object clone()  {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			//ignore
			return null;
		}
	}
	
	public Object execute(Map params) {
		try {
			Assert.notNull(dataSource,"dataSource must be not null");
			Configuration conf = FreeMarkerConfigurationUtil.newDefaultConfiguration();
			String sql = FreemarkerUtil.processTemplateIntoString(conf,getSql(),params);
			Object result = executeSqlQuery(params,dataSource, sql);
			
			if(StringUtils.isNotBlank(requerySql)) {
				List tempInputList = null;
				if(result instanceof List) {
					tempInputList =  (List)result;
				}else {
					tempInputList = result == null ? new ArrayList() : Arrays.asList(result);
				}
				result = ObjectSqlQueryUtil.query(FreemarkerUtil.processTemplateIntoString(conf,requerySql,params), (List)tempInputList,params);
			}
			
			return result;
		}catch(Exception e) {
			throw new RuntimeException("execute query,id:"+getId()+" sql:"+sql+" error"+" params:"+params,e);
		}
	}
	
	private Object executeSqlQuery(Map params,DataSource ds, String sql) {
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(ds);
		List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql, params);
		if(isSingleRow()) {
			Map<String,Object> row = DataAccessUtils.singleResult(rows);
			if(row.size() == 1) {
				Object value = row.entrySet().iterator().next().getValue();
				return value;
			}else {
				return row;
			}
		}else {
			return rows;
		}
	}

	public void beforeQuery(Map<String, Object> context) throws Exception {
		super.beforeQuery(context);
	}
	
	public void afterQuery(Map<String, Object> context) throws Exception {
		super.afterQuery(context);
	}

	
}
