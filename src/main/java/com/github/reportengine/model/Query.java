package com.github.reportengine.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import com.github.reportengine.ReportEngineLifecycle;
import com.github.reportengine.util.AggrFunctionUtil;
import com.github.reportengine.util.FreeMarkerConfigurationUtil;
import com.github.reportengine.util.FreemarkerUtil;
import com.github.reportengine.util.ListUtil;
import com.github.reportengine.util.MetaDataRowMapperResultSetExtractor;
import com.github.reportengine.util.ObjectSqlQueryUtil;
import com.github.reportengine.util.ResultSetMetaDataInfo;

import freemarker.template.Configuration;

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
	private Boolean singleResult;
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
	
	/**
	 * 查询结果
	 */
	private Object result;
	
	/**
	 * 自动求和的汇总结果
	 */
	private Map autoSumResult;
	
	/**
	 * 查询结果的元数据，元数据内容参考： @see java.sql.ResultSetMetaData
	 */
	public List<ResultSetMetaDataInfo> metaDatas;
	
	/**
	 * 将多行的查询结果转换为map
	 */
	private Boolean result2Map;
	
	/**
	 * 将多行的查询结果转换为map,指定的作为map key列,配合result2Map属性一起使用
	 */
	private String mapKeyColumn;
	
	public String getRefDataSource() {
		return refDataSource;
	}
	public void setRefDataSource(String refDataSource) {
		this.refDataSource = refDataSource;
	}
	public Boolean isSingleResult() {
		return singleResult;
	}
	public void setSingleResult(Boolean singleResult) {
		this.singleResult = singleResult;
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
	
	public Boolean isResult2Map() {
		return result2Map;
	}
	
	public void setResult2Map(Boolean result2Map) {
		this.result2Map = result2Map;
	}
	
	public String getMapKeyColumn() {
		return mapKeyColumn;
	}
	
	public void setMapKeyColumn(String mapKeyColumn) {
		this.mapKeyColumn = mapKeyColumn;
	}
	
	public Object getResult() {
		return result;
	}
	
	public Map getAutoSumResult() {
		return autoSumResult;
	}
	
	public void afterPropertiesSet() throws Exception {
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
		return execute0(new HashMap(params));
	}
	
	private Object execute0(Map params) {
		try {
			Assert.notNull(dataSource,"dataSource must be not null");
			Assert.hasText(sql,"sql must be not empty");
			
			params.put("this", this);
			Configuration conf = FreeMarkerConfigurationUtil.newDefaultConfiguration();
			String sql = FreemarkerUtil.processTemplateIntoString(conf,getSql(),params);
			List<Map<String,Object>> result = executeSqlQuery(params,dataSource, sql);
			
			if(StringUtils.isNotBlank(requerySql)) {
				result = ObjectSqlQueryUtil.query(FreemarkerUtil.processTemplateIntoString(conf,requerySql,params), result,params);
			}
			
			this.autoSumResult = AggrFunctionUtil.autoSumAggr((List)result);
			this.result = processResultRows(result);
			return this.result;
		}catch(Exception e) {
			throw new RuntimeException("execute query,id:"+getId()+" sql:"+sql+" error"+" params:"+params,e);
		}
	}
	
	private List<Map<String,Object>> executeSqlQuery(Map params,DataSource ds, String sql) {
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(ds);
//		List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql, params);
		
		ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
		MetaDataRowMapperResultSetExtractor resultSetExtractor  = new MetaDataRowMapperResultSetExtractor(rowMapper);
		
		List<Map<String,Object>> rows = (List<Map<String,Object>>)jdbcTemplate.query(sql, array2listForFixedSpringUnsupport(params),resultSetExtractor);
		metaDatas = resultSetExtractor.getMetaDatas();
		Assert.notNull(metaDatas,"metaDatas must be not null");
		return rows;
	}
	
	private Object processResultRows(List<Map<String,Object>> rows) {
		if(singleResult != null && singleResult) {
			Map<String,Object> row = DataAccessUtils.singleResult(rows);
			if(row != null && row.keySet().size() == 1) {
				Object value = row.entrySet().iterator().next().getValue();
				return value;
			}else {
				return row == null ? Collections.EMPTY_MAP : row;
			}
		}else if(result2Map != null && result2Map) {
			Assert.hasText(mapKeyColumn,"if result2Map=true, 'mayKeyColumn' must be not empty");
			return ListUtil.list2Map(rows,mapKeyColumn);
		}else {
			return rows;
		}
	}
	
	/**
	 * 将array 转换成 list,因为spring in (:param) 不支持param是Array,只支持是List
	 * @param params
	 * @return
	 */
	public static Map array2listForFixedSpringUnsupport(Map params) {
		return ListUtil.array2list(params);
	}
	
	public void beforeQuery(Map<String, Object> context) throws Exception {
		super.beforeQuery(context);
	}
	
	public void afterQuery(Map<String, Object> context) throws Exception {
		super.afterQuery(context);
	}

	
}
