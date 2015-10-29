package com.github.reportengine.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;

import com.github.reportengine.ReportEngineLifecycle;


/**
 * 报表参数
 * @author badqiu
 *
 */
public class Param extends BaseDataListObject implements InitializingBean,ReportEngineLifecycle,Cloneable,Serializable{
	private static final long serialVersionUID = 1L;
	
	private String label; //参数显示名称
	private String help; //帮助文本
	private String defaultValue; //默认值
	/**
	 * 数据类型: int,date,boolean,string,long,float,double
	 */
	private String dataType;
	private Boolean required; //参数不能为空
	private Boolean hidden;   //参数隐藏
	private Boolean readonly = false; //是否只读
	private Boolean distinct;  
	private String displayType; //显示类型 date,text,select,radio,checkbox
	private String labelExpr; //displayType为radio,checkbox,select显示的文本字段
	private String valueExpr; //displayType为radio,checkbox,select显示的值字段
	private String sortByColumn; //radio,checkbox,select的排序列
	private String sortDirection; //asc desc 
	
	private Object value; // 参数值 (经过处理后)
	private Object stringValue; // string参数值 (经过处理后)
	private Object rawValue; //参数原始值
	
	public Param() {
	}
	
	public Param(String id,String ref,String defaultValue, String dataType, Boolean required,
			Boolean hidden, String displayType) {
		super();
		setId(id);
		setRef(ref);
		this.defaultValue = defaultValue;
		this.dataType = dataType;
		this.required = required;
		this.hidden = hidden;
		this.displayType = displayType;
	}
	public String getLabel() {
		return getMessageSource().getMessage(label, null,label,LocaleContextHolder.getLocale());
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String type) {
		this.dataType = type;
	}
	public Boolean  getRequired() {
		return required == null ? false : required;
	}
	public void setRequired(Boolean required) {
		this.required = required;
	}
	
	public String getHelp() {
		return help;
	}
	public void setHelp(String help) {
		this.help = help;
	}
	public Boolean getHidden() {
		return hidden;
	}
	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}
	public Boolean  getDistinct() {
		return distinct == null ? false : distinct;
	}
	public void setDistinct(Boolean distinct) {
		this.distinct = distinct;
	}
	public String getDisplayType() {
		return displayType;
	}
	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}
	public String getLabelExpr() {
		return labelExpr;
	}
	public void setLabelExpr(String labelExpr) {
		this.labelExpr = labelExpr;
	}
	public String getValueExpr() {
		return valueExpr;
	}
	public void setValueExpr(String valueExpr) {
		this.valueExpr = valueExpr;
	}
	public String getSortByColumn() {
		return sortByColumn;
	}
	public void setSortByColumn(String sortByColumn) {
		this.sortByColumn = sortByColumn;
	}
	public String getSortDirection() {
		return sortDirection;
	}
	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}
	public Boolean getReadonly() {
		return readonly;
	}

	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public Object getStringValue() {
		return stringValue;
	}

	public void setStringValue(Object stringValue) {
		this.stringValue = stringValue;
	}

	public Object getRawValue() {
		return rawValue;
	}

	public void setRawValue(Object rawValue) {
		this.rawValue = rawValue;
	}

	public Object[] getStringValues() {
		if(stringValue == null) return null;
		if(stringValue.getClass().isArray()) {
			return (String[])stringValue;
		}else {
			return new Object[]{stringValue};
		}
	}
	
	public Object[] getValues() {
		if(value == null) return null;
		if(value.getClass().isArray()) {
			return (Object[])value;
		}else {
			return new Object[]{value};
		}
	}

	public Object[] getRawValues() {
		if(rawValue == null) return null;
		if(rawValue.getClass().isArray()) {
			return (Object[])rawValue;
		}else {
			return new Object[]{rawValue};
		}
	}
	
	@Override
	public void beforeQuery(Map<String, Object> context) throws Exception {
		super.beforeQuery(context);
		
		readonly = readonly == null ? false : readonly;
		hidden = hidden == null ? false : hidden;
		required = required == null ? false : required;
		distinct = distinct == null ? false : distinct;
		dataType = StringUtils.defaultIfEmpty(dataType, "string");
//		label = StringUtils.defaultIfEmpty(label, getId());
		displayType = StringUtils.defaultString(displayType, "text");
		valueExpr = StringUtils.defaultIfEmpty(valueExpr, labelExpr);
		labelExpr = StringUtils.defaultIfEmpty(labelExpr, valueExpr);
		
		Assert.notNull(hidden,"hidden must be not null");
	}
	
	@Override
	public void afterQuery(Map<String, Object> context) throws Exception {
		super.afterQuery(context);
	}
	
	public void afterPropertiesSet() throws Exception {
		
//		Assert.notNull(isHidden(),"hidden must be not null");
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
	
	public static class Util {
		public static Object parseValue(Param p,String value,String defaultValue) {
			try {
				return parseValue0(p.getDataType(), value,defaultValue);
			}catch(Exception e) {
				throw new RuntimeException("parse param value error,id:"+p.getId()+" dataType:"+p.getDataType()+" value:"+value+", cause message:"+e.getMessage(),e);
			}
		}
		
		private static Object parseValue0(String type,String value,String defaultValue) {
			value = StringUtils.defaultIfEmpty(value, defaultValue);
			if(StringUtils.isBlank(value)) {
				return null;
			}
			
			if("int".equals(type)) {
				return Integer.parseInt(value);
			}else if("long".equals(type)) {
				return Long.parseLong(value);	
			}else if("float".equals(type)) {
				return Float.parseFloat(value);				
			}else if("double".equals(type)) {
				return Double.parseDouble(value);
			}else if("boolean".equals(type)) {
				return Boolean.parseBoolean(value);
			}else if("date".equals(type)) {
				try {
					return DateUtils.parseDate(value, new String[]{"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss","yyyyMMdd","yyyyMMddHHmmss"});
				} catch (ParseException e) {
					throw new RuntimeException("cannot parse string:"+value+" for date");
				}
			}else if("string".equals(type)) {
				return value;
			}else {
				throw new RuntimeException("unknow param type:"+type+" valid type is[int,double,date,boolean,string]");
			}
		}
	}
}
