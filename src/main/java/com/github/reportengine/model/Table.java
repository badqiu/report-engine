package com.github.reportengine.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.util.Assert;

import com.github.rapid.common.util.page.Paginator;
import com.github.reportengine.Constants;
import com.github.reportengine.ReportEngineLifecycle;

public class Table extends BaseDataListObject implements ReportEngineLifecycle,Cloneable,Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 表格标题
	 */
	private String title;
	/**
	 * 表格的列
	 */
	private Column[] columns = new Column[]{};
	/**
	 * 是否支持分页
	 */
	private boolean pageable = false;
	/**
	 * 分页器
	 */
	private Paginator paginator;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean isPageable() {
		return pageable;
	}

	public void setPageable(boolean pageable) {
		this.pageable = pageable;
	}
	
	public Column[] getColumns() {
		return columns;
	}

	public void setColumns(Column[] columns) {
		this.columns = columns;
	}
	
	public Paginator getPaginator() {
		return paginator;
	}

	public static class Column extends BaseObject implements Serializable{
		private static final long serialVersionUID = 1L;

		private String name;
		private String label;
		private String value;
		private boolean hidden = false;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public boolean isHidden() {
			return hidden;
		}

		public void setHidden(boolean hidden) {
			this.hidden = hidden;
		}

	}

	public void afterQuery(Map<String, Object> context) throws Exception {
		super.afterQuery(context);
		Assert.notNull(getDataList(),"dataList must be not null");
		if(pageable) {
			int page = MapUtils.getIntValue(context, "page", 0);
			int pageSize = MapUtils.getIntValue(context, "pageSize", Constants.DEFAULT_PAGE_SIZE);
			paginator = new Paginator(page, pageSize, getDataList().size());
			
			List<Map<String, Object>> subList = getDataList().subList(paginator.getOffset(),paginator.getOffset()+paginator.getLimit());
			setDataList(subList);
		}
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
}
