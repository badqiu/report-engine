package com.github.reportengine.util;

import java.util.UUID;

import org.junit.Test;

public class UUIDTest {

	@Test
	public void test() {
		System.out.println(UUID.randomUUID().toString().replace("-", ""));
	}
	
}
