package com.github.reportengine.model;

import java.util.Locale;

import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

public class MessageSourceTest {

	MessageSource ms;
	
	@Test
	public void test() {
		ms = new ResourceBundleMessageSource();
		String result = ms.getMessage("中央银行", null,"中央银行",Locale.getDefault());
		System.out.println(result);
	}
}
