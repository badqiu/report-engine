package com.duowan.reportengine.util;

import org.apache.commons.lang.ClassUtils;
import org.junit.Test;


public class SelectorUtilTest {

	@Test
	public void test() {
		Integer n = 1;
		System.out.println(n.getClass());
		System.out.println(n.getClass().isPrimitive());
		System.out.println(new Long(n).getClass().isPrimitive());
		System.out.println("1".getClass().isPrimitive());
	}
}
