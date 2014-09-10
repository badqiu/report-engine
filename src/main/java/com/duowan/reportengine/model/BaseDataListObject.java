package com.duowan.reportengine.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.duowan.reportengine.util.CloneUtil;
import com.duowan.reportengine.util.ObjectSqlQueryUtil;

/**
 * 需要使用dataList的基础对象
 * 
 * @author badqiu
 * 
 */
public class BaseDataListObject extends BaseObject {

	private List<Map<String,Object>> dataList;
	private String refDataList;
	private Query query;
	private String orderBy; //排序,使用sql语法,示例: tdate desc,game asc
	
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
		
		if(StringUtils.isNotBlank(orderBy)) {
			dataList = ObjectSqlQueryUtil.query("select * from t order by "+orderBy, dataList);
		}
		
	}
}
