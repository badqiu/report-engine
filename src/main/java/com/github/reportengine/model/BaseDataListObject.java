package com.github.reportengine.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.github.reportengine.util.CloneUtil;
import com.github.reportengine.util.ObjectSqlQueryUtil;

/**
 * 需要使用dataList的基础对象
 * 
 * @author badqiu
 * 
 */
public class BaseDataListObject extends BaseObject {

	private static Logger logger = LoggerFactory.getLogger(BaseDataListObject.class);
	
	private List<Map<String,Object>> dataList;
	private String refDataList;
	private Query query;
	private String orderBy; //排序,使用sql语法,示例: tdate desc,game asc
	private String limit; //sql limit,使用sql语法,示例: 10,1
	private String requerySql; //将数据重新查询一次的sql
	
	public List<Map<String, Object>> getDataList() {
		return dataList;
	}

	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}

	public String getRefDataList() {
		return refDataList;
	}

	public void setRefDataList(String refDataList) {
		this.refDataList = refDataList;
	}
	
	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}
	
	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getRequerySql() {
		return requerySql;
	}

	public void setRequerySql(String requerySql) {
		this.requerySql = requerySql;
	}

	@Override
	public void beforeQuery(Map<String, Object> context) throws Exception {
		super.beforeQuery(context);
		Report.fireBeforeQueryLiftcycle(new Object[]{query}, context);
	}
	@Override
	public void afterQuery(Map<String, Object> context) throws Exception {
		super.afterQuery(context);
		
		if(StringUtils.isNotBlank(refDataList)) {
			List tmpDataList = (List)context.get(refDataList);
			dataList = CloneUtil.deepClone(tmpDataList);
			Assert.notNull(dataList,"not found dataList for "+getClass().getSimpleName() + " on id:"+getId()+" by refDataList:"+refDataList);
		}
		
		if(query != null) {
			dataList = (List)query.execute(context);
		}
		
		executeRequerySql();
	}

	private void executeRequerySql() {
		if(StringUtils.isBlank(requerySql) && StringUtils.isBlank(orderBy) &&  StringUtils.isBlank(limit)){
			return;
		}
		String requerySql = "select * from t";
		if(StringUtils.isNotBlank(this.requerySql)) {
			requerySql = this.requerySql;
		}
		if(StringUtils.isNotBlank(orderBy)) {
			requerySql = requerySql + " order by " + orderBy;
		}
		if(StringUtils.isNotBlank(limit)) {
			requerySql = requerySql + " limit " + limit;
		}
		logger.info("execute requerySql:"+requerySql);
		dataList = ObjectSqlQueryUtil.query(requerySql, dataList);
	}
}
