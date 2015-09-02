package com.github.reportengine.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class JsonUtil {

	static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
	}
	public static String toJson(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e){
			throw new RuntimeException("json error,ojb:"+obj,e);
		}
	}
	
}
