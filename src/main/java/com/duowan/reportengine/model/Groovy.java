package com.duowan.reportengine.model;

import java.io.Serializable;

/**
 * groovy 脚本对象
 * 
 * @author badqiu
 * 
 */
public class Groovy implements Cloneable,Serializable{
	private static final long serialVersionUID = 1L;

	private String beforeQuery;

	private String afterQuery;

	public String getBeforeQuery() {
		return beforeQuery;
	}

	public void setBeforeQuery(String beforeQuery) {
		this.beforeQuery = beforeQuery;
	}

	public String getAfterQuery() {
		return afterQuery;
	}

	public void setAfterQuery(String afterQuery) {
		this.afterQuery = afterQuery;
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
