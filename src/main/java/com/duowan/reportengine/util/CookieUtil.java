package com.duowan.reportengine.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * 
 * @author badqiu
 *
 */
public class CookieUtil {
	private static String ARRAY_SEPERATOR = "\001";
	
	public static void saveParamInotCookie(Map<String,Object> param, HttpServletResponse resp,String cookieNamePrefix) {
		Assert.notNull(cookieNamePrefix,"cookieNamePrefix must be not empty");
		
		for(String key : param.keySet()) {
			Object value = param.get(key);
//			if(value == null) {
//				continue;
//			}
			
			String strValue = null;
			if(value instanceof String) {
				strValue = (String)value;
			}else {
				strValue = StringUtils.join((Object[])value,ARRAY_SEPERATOR);
			}
			
//			if(StringUtils.isBlank(strValue)) {
//				continue;
//			}
			
			Cookie cookie;
			cookie = new Cookie(cookieNamePrefix+key,strValue);
			cookie.setPath("/");
//				cookie.setMaxAge(-1); //浏览器进程生效
			resp.addCookie(cookie);
		}
	}
	
	/**
	 * 清除所有 cookie
	 */
	public static void cleanAllCookie(HttpServletRequest resq,HttpServletResponse resp,String cookieNamePrefix) {
		Assert.notNull(cookieNamePrefix,"cookieNamePrefix must be not empty");
		
		Cookie[] cookies = resq.getCookies();
		if(cookies != null) {
			for(Cookie c : cookies) {
				if(c.getName().startsWith(cookieNamePrefix)) {
					c.setValue(null);
					c.setPath("/");
					c.setMaxAge(0);
					resp.addCookie(c);
				}
			}
		}
	}
	
	public static Map getCookieMap(HttpServletRequest req,String cookieNamePrefix) {
		Assert.notNull(cookieNamePrefix,"cookieNamePrefix must be not empty");
		
		Map map = new HashMap();
		Cookie[] cookies = req.getCookies();
		if(cookies != null) {
			for(Cookie c : cookies) {
				String cookieName = c.getName();
				if(cookieName.startsWith(cookieNamePrefix)) {
					String key = cookieName.substring(cookieNamePrefix.length());
					String value = c.getValue();
					if(StringUtils.isNotBlank(value)) {
						if(StringUtils.contains(value, ARRAY_SEPERATOR)) {
							String[] values = StringUtils.split(value,ARRAY_SEPERATOR);
							map.put(key, values);
						}else {
							map.put(key,value);
						}
					}
				}
				
			}
		}
		return map;
	}
	
}
