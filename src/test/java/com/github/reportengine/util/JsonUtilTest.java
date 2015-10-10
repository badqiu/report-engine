package com.github.reportengine.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class JsonUtilTest {

	@Test
	public void test() {
		assertEquals("null",JsonUtil.toJson(null));
	}

}
