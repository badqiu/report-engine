package com.github.reportengine.util;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Test;

import com.github.rapid.common.util.DateConvertUtil;
import com.github.reportengine.model.DemoStime;
import com.thoughtworks.xstream.converters.basic.DateConverter;

public class JsonUtilTest {
	static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	@Test
	public void test() {
		assertEquals("null",JsonUtil.toJson(null));
	}
	
	@Test
	public void parseJson() throws Exception {
		String json = "{\"AppId\":\"198311\",\"Stime\":\"1509957298952\",\"MsgType\":\"role.login\",\"DataSource\":\"server\",\"Channel\":\"xiaomi\"}";
		Map map = (Map)mapper.readValue(json, Object.class);
		System.out.println(map);
		
		System.out.println(map.get("Stime"));
		
		DemoStime ds = (DemoStime)mapper.readValue(json.toLowerCase(), DemoStime.class);
		System.out.println(DateConvertUtil.format(ds.getStime(),"yyyy-MM-dd HH:mm:ss"));
	}

}
