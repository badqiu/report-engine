package com.github.reportengine.util;

import java.util.Collection;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;

public class MessageSourceUtil {

	public static void initMessageSource(MessageSource messageSource,MessageSourceAware... array) {
		Assert.notNull(messageSource,"messageSource must be not null");
		if(array != null) {
			for(MessageSourceAware ser : array) {
				ser.setMessageSource(messageSource);
			}
		}
	}
	
	public static void initMessageSource(MessageSource messageSource,Collection<MessageSourceAware> array) {
		Assert.notNull(messageSource,"messageSource must be not null");
		if(array != null) {
			for(MessageSourceAware ser : array) {
				ser.setMessageSource(messageSource);
			}
		}
	}
	
}
