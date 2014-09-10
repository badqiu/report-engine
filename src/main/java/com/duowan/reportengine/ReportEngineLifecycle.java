package com.duowan.reportengine;

import java.util.Map;
/**
 * 报表引擎生命周期接口
 * 
 * @author badqiu
 *
 */
public interface ReportEngineLifecycle {
	/**
	 * querys执行前前执行
	 * @param context
	 */
	public void beforeQuery(Map<String,Object> context) throws Exception;
	
	/**
	 * querys执行后执行
	 * @param context
	 */
	public void afterQuery(Map<String,Object> context) throws Exception;
	
//	public void beforeRender(Map<String,Object> context);
	
//	public void afterRender(Map<String,Object> context);
}
