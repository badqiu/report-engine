package com.github.reportengine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	private boolean singleResult;
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
	private boolean resultAsMap;
	/**
	 * 将多行的查询结果转换为map,指定的作为map key列
	 */
	private String mapKeyColumn;
	
	public String getRefDataSource() {
		return refDataSource;
	}
	public void setRefDataSource(String refDataSource) {
		this.refDataSource = refDataSource;
	}
	public boolean isSingleResult() {
		return singleResult;
	}
	public void setSingleResult(boolean singleResult) {
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
	
	public boolean isResultAsMap() {
		return resultAsMap;
	}
	
	public void setResultAsMap(boolean resultAsMap) {
		this.resultAsMap = resultAsMap;
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
			Object result = processResultRows(executeSqlQuery(params,dataSource, sql));
			
			if(StringUtils.isNotBlank(requerySql)) {
				List tempInputList = null;
				if(result instanceof List) {
					tempInputList =  (List)result;
				}else {
					tempInputList = result == null ? new ArrayList() : Arrays.asList(result);
				}
				result = ObjectSqlQueryUtil.query(FreemarkerUtil.processTemplateIntoString(conf,requerySql,params), (List)tempInputList,params);
			}
			
			this.result = result;
			if(result instanceof List) {
				this.autoSumResult = AggrFunctionUtil.autoSumAggr((List)result);
			}else {
				this.autoSumResult = Collections.EMPTY_MAP;
			}
			
			return result;
		}catch(Exception e) {
			throw new RuntimeException("execute query,id:"+getId()+" sql:"+sql+" error"+" params:"+params,e);
		}
	}
	
	private List<Map> executeSqlQuery(Map params,DataSource ds, String sql) {
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(ds);
//		List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql, params);
		
		ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
		MetaDataRowMapperResultSetExtractor resultSetExtractor  = new MetaDataRowMapperResultSetExtractor(rowMapper);
		
		List<Map> rows = (List<Map>)jdbcTemplate.query(sql, array2listForFixedSpringUnsupport(params),resultSetExtractor);
		metaDatas = resultSetExtractor.getMetaDatas();
		Assert.notNull(metaDatas,"metaDatas must be not null");
		return rows;
	}
	
	private Object processResultRows(List<Map> rows) {
		if(isSingleResult()) {
			Map<String,Object> row = DataAccessUtils.singleResult(rows);
			if(row != null && row.size() == 1) {
				Object value = row.entrySet().iterator().next().getValue();
				return value;
			}else {
				return row;
			}
		}else if(resultAsMap) {
			Assert.hasText(mapKeyColumn,"if resultAsMap=true, 'mayKeyColumn' must be not empty");
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
