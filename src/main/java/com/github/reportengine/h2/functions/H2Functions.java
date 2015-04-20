package com.github.reportengine.h2.functions;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.beanutils.NestedNullException;
import org.h2.value.ValueJavaObject;

import com.duowan.common.beanutils.PropertyUtils;

/**
 * 
 * H2数据库相关的自定义函数
 *
 * @author badqiu
 *
 */
public class H2Functions  {
	/**
	 *  生成map结构的数据,使用 map(key1,value1,key2,value2,key3,value3....)
	 *  CREATE ALIAS map FOR "com.github.reportengine.h2.functions.H2Functions.map"
	 * @param keyValues
	 * @return
	 */
	private static Map newMap(Object... keyValues) {
		Map result = new LinkedHashMap();
		for(int i = 0; i < keyValues.length; i+=2) {
			Object key = keyValues[i];
			Object value = keyValues[i+1];
			result.put(key, value);
		}
		return result;
	}

	public static Map string_map(String key,String value) {
		Map result = new LinkedHashMap();
		Object convertedValue = convertValue(value);
		result.put(key, value);
		return result;
	}

	public static Map number_map(String key,Double value) {
		Map result = new LinkedHashMap();
		result.put(key, value);
		return result;
	}

	public static Map date_map(String key,Date value) {
		Map result = new LinkedHashMap();
		result.put(key, value);
		return result;
	}
	
	public static String get_property(Object map,String key) {
		if(map != null) {
			Object object = (Object)convertValue(map);
			try {
				Object value = PropertyUtils.getNestedProperty(object, key);
				return value == null ? null : value.toString();
			}catch(NestedNullException e) {
				//ignore
			}
		}
		return null;
	}
	
	private static Object convertValue(Object value) {
		if(value == null) return null;
		try {
			if(value.getClass().isArray()) {
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream((byte[])value));
				return ois.readObject();
			}else {
				return value;
			}
		}catch(Exception e) {
			return ValueJavaObject.getNoCopy((byte[])value).getString();
			//throw new RuntimeException("convert value error,input value:"+value,e);
		}
	}
	

	
//	public static Map map(Integer... keyValues) {
//		return newMap((Object[])keyValues);
//	}
//	public static Map map(Long... keyValues) {
//		return newMap((Object[])keyValues);
//	}
//	public static Map map(Float... keyValues) {
//		return newMap((Object[])keyValues);
//	}
//	public static Map map(Double... keyValues) {
//		return newMap((Object[])keyValues);
//	}
//	public static Map map(Boolean... keyValues) {
//		return newMap((Object[])keyValues);
//	}
//	public static Map map(Date... keyValues) {
//		return newMap((Object[])keyValues);
//	}
//	public static Map map(java.sql.Date... keyValues) {
//		return newMap((Object[])keyValues);
//	}
//	public static Map map(java.sql.Time... keyValues) {
//		return newMap((Object[])keyValues);
//	}
//	public static Map map(java.sql.Timestamp... keyValues) {
//		return newMap((Object[])keyValues);
//	}
}