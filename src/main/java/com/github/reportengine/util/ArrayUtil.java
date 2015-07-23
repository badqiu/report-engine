package com.github.reportengine.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayUtil {

	public static <T> T[] addAll(T[] target, T[] extend) {
		try {
			T[] result = (T[])java.lang.reflect.Array.newInstance(extend.getClass().getComponentType(), 0);
			
			List<T> resultList = new ArrayList<T>(asList(target));
			for(T e : asList(extend)) {
				if(resultList.contains(e)) {
					int oldIndex = resultList.indexOf(e);
					resultList.remove(e);
					resultList.add(oldIndex, e);
				}else {
					resultList.add(e);
				}
			}
			return (T[])resultList.toArray((T[])result);
		} catch (Exception e) {
			throw new RuntimeException("error on addAll",e);
		}
	}
	
	public static <T> List<T> asList(T[] target) {
		if(target == null) return Collections.EMPTY_LIST;
		return Arrays.asList(target);
	}
}
