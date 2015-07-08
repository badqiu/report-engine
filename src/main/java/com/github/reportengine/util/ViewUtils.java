package com.github.reportengine.util;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rapid.common.beanutils.PropertyUtils;

/**
 * 页面工具类
 * @author andy
 *
 */
public class ViewUtils {
	private static Map<String,Object> NULL_MAP = new HashMap<String,Object>();
	
	private final static DecimalFormat NUM_FORMAT = new DecimalFormat("0.00");
	
	private static Logger logger = LoggerFactory.getLogger(ViewUtils.class);
	
	/**
	 * 百分比
	 * @param number1
	 * @param number2
	 * @return
	 */
	public static String percent(Object number1,Object number2) {
		return percent(number1, number2, NUM_FORMAT);
	}

	private static String percent(Object number1, Object number2,DecimalFormat df) {
		if(number1==null || number2==null || 
				StringUtils.isBlank(number1.toString()) || 
				StringUtils.isBlank(number2.toString()) || 
				number1.toString().equals("0") || number2.toString().equals("0") || number2.toString().equals("0.0")) {
			return "";
		}
		double percent = (double)Double.valueOf(number1.toString())/Double.valueOf(number2.toString())*100;
		if(percent > 99.99) {
			return "100%";
		}
		if(percent < 0.01) {
			return "0%";
		}
		return df.format(percent)+"%";
	}
	
	
	/**
	 * 除
	 * @param number1
	 * @param number2
	 * @return
	 */
	public static double div(Object number1,Object number2) {
		if(number1==null || number2==null) {
			return 0;
		}
		Double num1 = Double.valueOf(number1.toString());
		Double num2 = Double.valueOf(number2.toString());
		if(num2 == 0) return 0;
		
		return num1 / num2;
	}
	
	/**
	 * 占比
	 * @param number1
	 * @param number2
	 * @return
	 */
	public static double rate(Object number1,Object number2) {
		if(number1==null || number2==null) {
			return 0;
		}
		return Double.valueOf(NUM_FORMAT.format(div(number1,number2)));
	}
	
	public static String parseRate(Object number1,Object number2){
		String result =  NUM_FORMAT.format(rate(number1, number2) * 100);
		return result+"%";
	}
	

	public static String html2text(String html) {
		if(StringUtils.isBlank(html)) {
			return html;
		}
		return Jsoup.parse(html).text();
	}

}
