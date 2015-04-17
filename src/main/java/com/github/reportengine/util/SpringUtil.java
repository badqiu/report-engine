package com.github.reportengine.util;

import org.springframework.beans.factory.InitializingBean;

public class SpringUtil {
	public static void initializing(Object[] array) {
		if(array == null) return;
		
		for(Object obj : array) {
			SpringUtil.initializing(obj);
		}
	}
	
	public static void initializing(Object obj) {
		if(obj == null) return;
		
		if(obj instanceof InitializingBean) {
			try {
				((InitializingBean)obj).afterPropertiesSet();
			}catch(Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}
	
}
