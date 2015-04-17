package com.github.reportengine;

import org.apache.commons.collections.MapUtils;

public interface Constants {
	/**
	 * 默认分页大小
	 */
	public final static int DEFAULT_PAGE_SIZE = MapUtils.getIntValue(System.getProperties(),"pageSize",Integer.MAX_VALUE);
	
}
