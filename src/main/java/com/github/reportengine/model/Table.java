package com.github.reportengine.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;

import com.github.rapid.common.util.page.Paginator;
import com.github.reportengine.Constants;
import com.github.reportengine.ReportEngineLifecycle;
import com.github.reportengine.util.MessageSourceUtil;

public class Table extends BaseDataListObject implements ReportEngineLifecycle,InitializingBean,Cloneable,Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 表格标题
	 */
	private String title;
	/**
	 * 表格的列
	 */
	private Column[] columns =  null;
	/**
	 * 是否支持分页
	 */
	private boolean pageable = false;
	/**
	 * 分页器
	 */
	private Paginator paginator;
	
	/**
	 * 控制显示合计值，显示位置: table foot
	 */
	private Boolean showSum = true;
	
	public String getTitle() {
		return getMessageSource().getMessage(title, null,title,LocaleContextHolder.getLocale());
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
	
	public boolean isShowSum() {
		return showSum;
	}

	public void setShowSum(boolean showSum) {
		this.showSum = showSum;
	}

	public static class Column extends BaseObject implements Serializable{
		private static final long serialVersionUID = 1L;

		private String name;
		/**
		 * 显示名称
		 */
		private String label;
		/**
		 * 显示值的表达式
		 */
		private String value;
		/**
		 * 是否隐藏
		 */
		private boolean hidden = false;
		
		private String cssClass;
		private String cssStyle;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLabel() {
			return getMessageSource().getMessage(label, null,label,LocaleContextHolder.getLocale());
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

		public String getCssClass() {
			return cssClass;
		}

		public void setCssClass(String cssClass) {
			this.cssClass = cssClass;
		}

		public String getCssStyle() {
			return cssStyle;
		}

		public void setCssStyle(String cssStyle) {
			this.cssStyle = cssStyle;
		}

	}

	public void afterQuery(Map<String, Object> context) throws Exception {
		super.afterQuery(context);
		Assert.notNull(getDataList(),"dataList must be not null");
		if(columns == null) {
			columns = generateColumnsByDataList(getDataList());
		}
		
		if(pageable) {
			int page = MapUtils.getIntValue(context, "page", 0);
			int pageSize = MapUtils.getIntValue(context, "pageSize", Constants.DEFAULT_PAGE_SIZE);
			paginator = new Paginator(page, pageSize, getDataList().size());
			
			List<Map<String, Object>> subList = getDataList().subList(paginator.getOffset(),paginator.getOffset()+paginator.getLimit());
			setDataList(subList);
		}
	}
	
	private Column[] generateColumnsByDataList(List<Map<String, Object>> dataList) {
		if(CollectionUtils.isEmpty(dataList)) {
			return new Column[]{};
		}
		Map<String,Object> row = dataList.get(0);
		Column[] result = new Column[row.size()];
		int index = 0;
		for(String key : row.keySet()) {
			Column c = new Column();
			c.setLabel(key);
			c.setValue("${row."+key+"!}");
//			c.setName(key);
			result[index] = c;
			index++;
		}
		return result;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
//		this.showSum  = this.showSum == null ?  true : this.showSum;
		MessageSourceUtil.initMessageSource(getMessageSource(),columns);
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
