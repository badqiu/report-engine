package com.github.reportengine.h2.functions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

public class CollectSetAggrFunction implements org.h2.api.AggregateFunction {
	private Set result = new LinkedHashSet();
	
	public void init(Connection conn) throws SQLException {
		
	}

	public int getType(int[] inputTypes) throws SQLException {
		return 0;
	}

	public void add(Object value) throws SQLException {
		
	}

	public Object getResult() throws SQLException {
		return null;
	}

}
