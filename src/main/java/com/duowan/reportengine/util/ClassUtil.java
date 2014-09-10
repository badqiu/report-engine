package com.duowan.reportengine.util;

public class ClassUtil {

	public static Object newInstance(String clazz) {
		try {
			Class cla = Class.forName(clazz);
			return cla.newInstance();
		}catch(Exception e) {
			throw new RuntimeException("cannot newInstance by class:"+clazz,e);
		}
	}
	
}
