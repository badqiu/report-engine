package com.github.reportengine.h2.functions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * 聚焦函数，collect_map(map) : 可以将多个map合并为一个map
 * 函数创建: CREATE AGGREGATE collect_map FOR "com.github.reportengine.h2.functions.CollectMapAggrFunction";
 * @author badqiu
 *
 */
public class CollectMapAggrFunction implements org.h2.api.AggregateFunction {
	private Map result = new LinkedHashMap();
	
	// add item支持参数数组
	public void add(Object item) throws SQLException {
		if(item == null) {
			return;
		}
		if(item instanceof Map) {
			result.putAll((Map)item);
		}else if(item.getClass().isArray()){
			Map map = readMap(item);
			result.putAll(map);
		}else {
			throw new RuntimeException("unsupport type:"+item.getClass());
		}
	}

	private Map readMap(Object item)  {
		try {
			Map map = (Map)readObject((byte[])item);
			return map;
		}catch(ClassCastException e) {
			Object[] array = (Object[])item;
			Assert.isTrue(array.length % 2 ==0,"collect_map() function [args.length % 2 == 0] must be true");
			Map map = new LinkedHashMap();
			for(int i = 0; i < array.length; i+=2) {
				try {
					map.put(array[i], readObject(array[i+1]));
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
			}
			return map;
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object readObject(byte[] bytes) throws ClassNotFoundException, IOException {
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream((bytes)));
		return ois.readObject();
	}

	private static Object readObject(Object object) throws ClassNotFoundException, IOException {
		if(object == null) return null;
		if(object.getClass().isArray()) {
			return readObject((byte[])object);
		}else {
			return object;
		}
	}

	public Object getResult() throws SQLException {
		return result;
	}

	public int getType(int[] ints) throws SQLException {
		return java.sql.Types.JAVA_OBJECT;
	}

	public void init(Connection conn) throws SQLException {
		result = new LinkedHashMap();
	}

}