package com.github.reportengine.model;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;

import com.github.rapid.common.beanutils.PropertyUtils;
import com.github.reportengine.ReportEngine;
import com.github.reportengine.ReportEngineLifecycle;
import com.github.reportengine.util.FieldUtil;
import com.github.reportengine.util.JsonUtil;
/**
 * 所有model可以引用的基准对象
 * 
 * @author badqiu
 *
 */
public class BaseObject  implements ReportEngineLifecycle,InitializingBean,Serializable,MessageSourceAware{
	/**
	 * 对象ID
	 */
	private String id;
	/**
	 * 引用的对象名称
	 */
	private String ref;
	/**
	 * 备注
	 */
	private String remarks;
	/**
	 * 自定义属性
	 */
	private Properties props;
	
	/**
	 * 自定义css style
	 */
	private String cssStyle;
	
	/**
	 * 自定义css class
	 */
	private String cssClass;
	
	/**
	 * i18n国际化使用的MessageSource资源类
	 */
	private transient MessageSource messageSource;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remark) {
		this.remarks = remark;
	}
	
	public String getCssStyle() {
		return cssStyle;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}
	
	@JsonIgnore
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	@JsonIgnore
	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void beforeQuery(Map<String, Object> context) throws Exception {
		if(StringUtils.isNotBlank(ref)) {
			Object refObject = lookupBean(context, ref);
			if(refObject == null) {
				throw new RuntimeException("not found " + getClass().getSimpleName() + " by ref:"+ref+" on context:"+context.keySet());
			}
			FieldUtil.inheritanceFields(refObject, this);
//			PropertyUtils.copyProperties(this, refObject);
		}
		
		setBeanPropertiesByRef(context,false);
	}

	/**
	 * 根据bean对象的ref前缀，设置bean属性。如 refDataList=blogList,将设置blogList作为dataList的属性值
	 * @param context
	 * @param required
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException 
	 */
	private void setBeanPropertiesByRef(Map<String, Object> context,boolean required) throws IllegalAccessException, InvocationTargetException, IOException {
		for(PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(getClass())) {
			String name = pd.getName();
			if(name.equals("ref")) {
				continue;
			}
			
			if(name.startsWith("ref")) {
				String beanName = (String)pd.getReadMethod().invoke(this);
				if(StringUtils.isBlank(beanName)) {
					continue;
				}
				
				
				String propertyName = StringUtils.uncapitalise(name.substring("ref".length()));
				Object propertyValue = PropertyUtils.getProperty(this, propertyName);
				if(propertyValue == null) {
					Object bean = lookupBean(context,beanName);
					if(required) {
						Assert.notNull(bean,"not found bean by "+name+"="+beanName);
					}
					PropertyUtils.setProperty(this, propertyName, bean);
				}
			}
		}
	}

	private Object lookupBean(Map<String, Object> context, String beanName)
			throws IOException {
		Object bean = null;
		if(beanName.contains("/")) {
			int index = beanName.lastIndexOf("/");
			String reportPath = beanName.substring(0,index); 
			String id = beanName.substring(index+1); 
			ReportEngine reportEngine = ReportEngine.reportEngineContext.get();
			Assert.notNull(reportEngine,"reportEngine not found on ReportEngine.reportEngineContext");
			Report report = reportEngine.getReport(reportPath);
			bean = report.getElementById(id);
		}else {
			bean = context.get(beanName);
		}
		return bean;
	}

	public void afterQuery(Map<String, Object> context) throws Exception {
		setBeanPropertiesByRef(context,true);
	}

	public void afterPropertiesSet() throws Exception {
		if(StringUtils.isBlank(ref)) {
			Assert.hasText(id,"id must be not empty on "+getClass());
		}
		
	}
	
	public String toJson() {
		return JsonUtil.toJson(this);
	}
	
	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj.getClass() != getClass()) {
			return false;
		}
		BaseObject other = (BaseObject)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.append(getRef(), other.getRef())
			.isEquals() ;
	}
	
	@Override
	public int hashCode() {
		return (id+"\1\2\3"+ref).hashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+" id:"+id+" ref:"+ref;
	}

}
