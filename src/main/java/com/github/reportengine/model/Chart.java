package com.github.reportengine.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.github.rapid.common.beanutils.PropertyUtils;
import com.github.rapid.common.util.DateConvertUtil;
import com.github.reportengine.ReportEngineLifecycle;
import com.github.reportengine.util.MapUtil;

/**
 * 报表图片
 * 
 * @author badqiu
 *
 */
public class Chart extends BaseDataListObject implements ReportEngineLifecycle,Cloneable,Serializable{
	private static final long serialVersionUID = 1L;
	
	// 图表名称 
	private String title;
	// 图表在页面上显示的宽度
	private int width;
	// 图表在页面上显示的高度
	private int height;
	// 图表类型（分为七种）
	private String chartType;
	// x轴字段
	private String x;
	// x轴的值
	private List<Object> xValues;
	
	// x的格式化,日期使用:yyyy-MM-dd,数字使用: #####.##
	private String xFormat;
	// 横坐标名称 
	private String xTitle;
	// 纵坐标名称 
	private String yTitle;
	
	private Ser[] sers;
	
	private int dateIntervalSeconds = 60 * 60 * 24;
	/** 设置tooltip shared属性 */
	private boolean tipShared=false;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public String getxTitle() {
		return xTitle;
	}

	public void setxTitle(String xTitle) {
		this.xTitle = xTitle;
	}

	public String getyTitle() {
		return yTitle;
	}

	public void setyTitle(String yTitle) {
		this.yTitle = yTitle;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}
	
	public List<Object> getxValues() {
		return xValues;
	}

	public void setxValues(List<Object> xValues) {
		this.xValues = xValues;
	}

	public String getxFormat() {
		return xFormat;
	}

	public void setxFormat(String xFormat) {
		this.xFormat = xFormat;
	}

	public Ser[] getSers() {
		return sers;
	}

	public void setSers(Ser[] sers) {
		this.sers = sers;
	}
	
	public void addSer(Ser ser) {
		this.sers = (Ser[])ArrayUtils.add(sers, ser);
	}
	
	public int getDateIntervalSeconds() {
		return dateIntervalSeconds;
	}

	public void setDateIntervalSeconds(int dateIntervalSeconds) {
		this.dateIntervalSeconds = dateIntervalSeconds;
	}
	
	public boolean isTipShared() {
		return tipShared;
	}

	public void setTipShared(boolean tipShared) {
		this.tipShared = tipShared;
	}

	@Override
	public void afterQuery(Map<String, Object> context) throws Exception {
		super.afterQuery(context);
		if(Arrays.asList("line","bar","column").contains(chartType)) {
			if(dateIntervalSeconds > 0) {
				fillLossDateData(getDataList());
			}
		}
		formatDataList();
		
		if(sers != null) {
			for(Ser ser : sers) {
				if(ser.getValues() == null) {
					ser.fillValues(getDataList());
				}
			}
		}
		
		xValues = getXValue(getDataList(),x);
	}
	
	private List<Object> getXValue(List<Map<String, Object>> dataList, String x) {
		List<Object> result = new ArrayList();
		for(Map row : dataList) {
			try {
				Object value = PropertyUtils.getNestedProperty(row, x);
				result.add(value);
			}catch(NestedNullException e) {
				//ignore
			}
		}
		return result;
	}

	/**
	 * 日期的数据如果为空，需要填充丢失的数据
	 */
	void fillLossDateData(List<Map<String,Object>> dataList) {
		int dateIntervalMills = (dateIntervalSeconds * 1000);
		Date beforeDate = null;
		for (int i = 0; i < dataList.size(); i++) {
			Map row = dataList.get(i);
			Object value = row.get(x);
			if (value instanceof Date) {
				Date date = (Date) value;
				if (beforeDate == null) {
					beforeDate = date;
				}
	
				long intervalMills = getTimeIntervalMills(beforeDate, date);
				if (Math.abs(intervalMills) > dateIntervalMills) {
					Date insertDate = new Timestamp(beforeDate.getTime() + (intervalMills < 0 ? dateIntervalMills : -dateIntervalMills ));
					Map map = MapUtil.newLinkedMap(x,insertDate);
					dataList.add(i, map);
					beforeDate = insertDate;
				}else {
					beforeDate = date;
				}
			}
		}
	}

	private static long getTimeIntervalMills(Date d1, Date d2) {
		return d1.getTime() - d2.getTime();
	}

	/**
	 * 将dataList的数据格式化显示
	 */
	private void formatDataList() {
		List<Map<String,Object>> dataList = getDataList();
		for(Map<String,Object> row : dataList) {
			formatByKey(row,x,xFormat);
			for(Ser ser : sers) {
				formatByKey(row,ser.y,ser.yFormat);
			}
		}
	}

	private void formatByKey(Map<String, Object> map,String key,String keyFormat) {
		if(StringUtils.isBlank(keyFormat)) return;
		
		try {
			Object value = map.get(x);
			String formatedValue = formatBy(value,xFormat);
			map.put(x, formatedValue);
		}catch(Exception e) {
			throw new RuntimeException("format error,key:"+key+" keyFormat:"+keyFormat+" on map:"+map);
		}
	}

	private String formatBy(Object value, String format) {
		if(value == null) return null;
		if(value instanceof Date) {
			return DateConvertUtil.format((Date)value, format);
		}else if(value instanceof Number) {
			DecimalFormat df = new DecimalFormat();
			return df.format((Number)value);
		}else {
			throw new RuntimeException("cannot format value:"+value+" by format:"+format);
		}
	}

	public static class Ser implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private String y;
		private String yAxis;
		private String chartType;
		private String yFormat;
		private String title;
		private List<Number> values;
		
		public Ser() {
			super();
		}
		
		public Ser(String y, String title) {
			super();
			this.y = y;
			this.title = title;
		}
		
		public Ser(String title,List<Number> values) {
			this.title = title;
			this.values = values;
		}
		
		public void fillValues(List<Map<String, Object>> dataList) {
			values = new ArrayList();
			for(Map row : dataList) {
				try {
					Number value = (Number)PropertyUtils.getNestedProperty(row, y);
					values.add(value);
				}catch(NestedNullException e) {
					values.add(0);
					//ignore
					e.printStackTrace();
				}
			}
		}
		
		public String getyAxis() {
			return yAxis;
		}

		public void setyAxis(String yAxis) {
			this.yAxis = yAxis;
		}

		public String getChartType() {
			return chartType;
		}

		public void setChartType(String chartType) {
			this.chartType = chartType;
		}

		public String getY() {
			return y;
		}
		public void setY(String y) {
			this.y = y;
		}
		public String getyFormat() {
			return yFormat;
		}
		public void setyFormat(String yFormat) {
			this.yFormat = yFormat;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public List<Number> getValues() {
			return values;
		}
		public void setValues(List<Number> values) {
			this.values = values;
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
