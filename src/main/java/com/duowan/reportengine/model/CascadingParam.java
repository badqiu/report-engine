package com.duowan.reportengine.model;
/**
 * 级联参数
 * 
 * @author badqiu
 *
 */
public class CascadingParam extends BaseDataListObject{
	
	private Param[] params = new Param[0];

	public Param[] getParams() {
		return params;
	}

	public void setParams(Param[] params) {
		this.params = params;
	}
	
	public void test() {
		// sex , 名称
		// select sex,name from user;
		
	}
	
}
