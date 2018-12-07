package com.github.reportengine.h2.functions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class MiddleNumberAggrFunction implements org.h2.api.AggregateFunction {
	
	List numbers = null;
	public void init(Connection conn) throws SQLException {
		numbers = new ArrayList();
	}

	public int getType(int[] inputTypes) throws SQLException {
		return java.sql.Types.DOUBLE;
	}

	public void add(Object value) throws SQLException {
		numbers.add(value);
	}

	public Object getResult() throws SQLException {
		return middleNumber(numbers);
	}
	
	public static <T extends Comparable<? super T>> T middleNumber(List<T> numbers) {
		if(CollectionUtils.isEmpty(numbers)) {
			return null;
		}
		List<T> tmpNumbers = new ArrayList<T>(numbers.size());
		for(T obj : numbers) {
			if(obj == null) continue;
			
			tmpNumbers.add(obj);
		}
		
		if(CollectionUtils.isEmpty(tmpNumbers)) {
			return null;
		}
		
		Collections.sort((List<T>) tmpNumbers);
		return tmpNumbers.get(tmpNumbers.size() / 2);
	}
}
