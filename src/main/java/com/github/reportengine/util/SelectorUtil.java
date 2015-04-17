package com.github.reportengine.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ClassUtils;

import com.duowan.common.beanutils.PropertyUtils;

public class SelectorUtil {

	public static Object getElementById(Object root, String id){
		try {
			return getElementByKeyName(root,"id",id,new HashSet<Object>());
		} catch (Exception e) {
			throw new RuntimeException("getElementById error,id="+id+" root:"+root,e);
		}
	}
	
	private static Object getElementByKeyName(Object root,String keyName, String keyValue,Set<Object> visitedObject) throws IllegalArgumentException, IllegalAccessException {
		if(root == null) return null;
		if(ClassUtils.isPrimitiveOrWrapper(root.getClass())) return null;
		if(root.getClass().getName().startsWith("java.lang")) return null;
		
		if(visitedObject.contains(root)) return null;
		visitedObject.add(root);
		
		if(root instanceof Map) {
			Map map = (Map)root;
			Object result = map.get(keyName);
			if(keyValue.equals(result)) { 
				return map;
			}
			return getElementByKeyName(map.values(),keyName,keyValue,visitedObject);
		}else if(root instanceof Collection) {
			for(Object item : (Collection)root) {
				Object result = getElementByKeyName(item,keyName,keyValue,visitedObject);
				if(result != null) {
					return result;
				}
			}
		}else if(root.getClass().isArray()) {
			for(int i = 0; i < Array.getLength(root); i++) {
				Object item = Array.get(root, i);
				Object result = getElementByKeyName(item, keyName,keyValue,visitedObject);
				if(result != null) {
					return result;
				}
			}
		}else {
			try {
				Field idField = root.getClass().getDeclaredField(keyName);
				idField.setAccessible(true);
				if(keyValue.equals(idField.get(root))) {
					return root;
				}
			}catch(NoSuchFieldException e) {
			}
			
			try {
				if(keyValue.equals(PropertyUtils.getProperty(root, keyName))) {
					return root;
				}
			}catch(Exception e) {
			}
			
			
			for(Field f : root.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				Object fieldValue = f.get(root);
				Object result = getElementByKeyName(fieldValue,keyName,keyValue,visitedObject);
				
				if(result != null) {
					return result;
				}
			}
		}
		return null;
	}

}
