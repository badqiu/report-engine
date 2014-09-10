package com.duowan.reportengine.model;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class XtreamBean {

	private String name;
	private int age;
	private Date birthDate;
	private Map map;
	private Properties props;

	public void setMap(Map ext) {
		this.map = ext;
	}

	public Map getMap() {
		return map;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public static void main(String[] args) {
		
	}

}
