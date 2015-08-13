package com.github.reportengine.model;

import java.io.Serializable;

/**
 * Script 脚本对象
 * 
 * @author badqiu
 * 
 */
public class Script implements Cloneable,Serializable{
	private static final long serialVersionUID = 1L;

	private String lang;
	
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
	
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
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
