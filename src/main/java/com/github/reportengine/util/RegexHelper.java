package com.github.reportengine.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class RegexHelper {

	public static List<String> findMatchs(String input,String regex,int regexGroup) {
		if(StringUtils.isBlank(input))
			return Collections.EMPTY_LIST;
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		List<String> result = new ArrayList<String>();
		while(m.find()) {
			result.add(m.group(regexGroup));
		}
		return result;
	}
	
}
