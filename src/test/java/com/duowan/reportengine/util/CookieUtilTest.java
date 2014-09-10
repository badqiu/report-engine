package com.duowan.reportengine.util;

import java.util.Map;

import javax.servlet.http.Cookie;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class CookieUtilTest extends Assert{

	MockHttpServletRequest req = new MockHttpServletRequest();
	@Test
	public void test_getCookieMap() {
		req.setCookies(new Cookie("ra_param_username","badqiu"),new Cookie("test_key","test_value"));
		Map map = CookieUtil.getCookieMap(req, "ra_param_");
		assertEquals("{username=badqiu}",map.toString());
	}
	
}
