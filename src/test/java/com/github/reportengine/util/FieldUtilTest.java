package com.github.reportengine.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.github.reportengine.model.Param;


public class FieldUtilTest extends Assert {

	FieldTestBean p1 = new FieldTestBean("badqiu","pwd",20,new String[]{"red","blue"},Arrays.asList("100"),new HashMap());
	
	@Test
	public void test_override_normal() throws SecurityException, NoSuchFieldException {
		FieldTestBean p2 = new FieldTestBean();
		FieldUtil.inheritanceFields(p1, p2);
		assertEquals(p2.username , "badqiu");
		
		System.out.println(Arrays.toString(Param.class.getDeclaredFields()));
		assertNotNull(FieldUtil.getField(Param.class, "id"));
	}
	
	@Test
	public void test_override_normal_2() throws SecurityException, NoSuchFieldException {
		FieldTestBean p2 = new FieldTestBean();
		p1.booleanValue = false;
		p1.booleanObject = true;
		FieldUtil.inheritanceFields(p1, p2);
		assertEquals(p2.booleanObject , true);
		assertEquals(p2.booleanValue , false);
		
		
		p2 = new FieldTestBean();
		p1.booleanObject = null;
		FieldUtil.inheritanceFields(p1, p2);
		assertEquals(p2.booleanObject , null);
		
		
		p2 = new FieldTestBean();
		p1.booleanObject = false;
		FieldUtil.inheritanceFields(p1, p2);
		assertEquals(p2.booleanObject , false);
		
		p2 = new FieldTestBean();
		p1.booleanObject = true;
		p2.booleanObject = false;
		FieldUtil.inheritanceFields(p1, p2);
		assertEquals(p2.booleanObject , false);
	}
	
	@Test
	public void test_override_() {
		FieldTestBean p2 = new FieldTestBean();
		p2.username  = "jane";
		FieldUtil.inheritanceFields(p1, p2);
		assertEquals(p2.username , "jane");
		assertEquals(p2.age , 20);
	}
	
	public static class FieldTestBean {
		private String username;
		private String password;
		private int age;
		private String[] colors;
		private List<String> names;
		private Map properties;
		private Boolean booleanObject;
		private boolean booleanValue;
		
		public FieldTestBean(){
		}
		
		public FieldTestBean(String username, String password, int age,
				String[] colors, List<String> names, Map properties) {
			super();
			this.username = username;
			this.password = password;
			this.age = age;
			this.colors = colors;
			this.names = names;
			this.properties = properties;
		}
		
	}
}
