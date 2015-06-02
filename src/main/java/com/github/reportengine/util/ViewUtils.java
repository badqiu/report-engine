package com.github.reportengine.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		DecimalFormat df = new DecimalFormat("0.00"); 
		return percent(number1, number2, df);
	}

	private static String percent(Object number1, Object number2,DecimalFormat df) {
		if(number1==null || number2==null || number1.toString().equals("0") || number2.toString().equals("0") || number2.toString().equals("0.0")) {
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
	 * 百分比
	 * @param number1
	 * @param number2
	 * @param precision 精度
	 * @return
	 */
	public static String percent(Object number1,Object number2, int precision) {
		StringBuffer preStr = new StringBuffer("0");
		if(precision>0) {
			preStr.append(".");
		}
		for(int i=0;i<precision;i++) {
			preStr.append(0);
		}
		
		DecimalFormat df=new DecimalFormat(preStr.toString()); 
		return percent(number1,number2,df);
	}
	
	/**
	 * 转化率
	 * @param rows
	 * @param columns
	 */
	public static void change(List<Map<String,Object>> rows,final String... columns) {
		for(int i=1;i< rows.size();i++) {
			for(String key :columns) {
				try {
					Object firstValue = getNestedProperty(rows.get(0), key);
					Object preValue = getNestedProperty(rows.get(i-1), key);
					Object curValue = getNestedProperty(rows.get(i), key);
					rows.get(i).put(key+".change", percent(curValue,preValue).equals("")?0:percent(curValue,preValue) );
					rows.get(i).put(key+".totalChange", percent(curValue,firstValue).equals("")?0:percent(curValue,firstValue));
				}catch(NestedNullException e) {
					logger.error("getNestedProperty error! key=" +key + ",rows="+rows ,e) ;
				}
			}		
		}
	}

	public static void changeForMap(List<Map<String,Object>> rows, String mapCode, String... keys){
		Object defaultValue = 0;
		for (Map<String,Object> row : rows) {
			Map<String, Object> map = (Map<String, Object>)row.get(mapCode);
			map.remove(null);
			for (String key : keys) {
				if(map.get(key) == null){
					map.put(key, defaultValue);
				}
			}
			row.putAll(map);
		}
		change(rows, initColumnName(mapCode,keys));
	}
	
	private static String[] initColumnName(String mapCode, String[] keys) {
		String[] newKeys = new String[keys.length];
		for (int i=0; i<keys.length; i++) {
			newKeys[i] = mapCode + "." + keys[i];
		}
		return newKeys;
	}

	private static Object getNestedProperty(Object bean,	String key) {
		try{
			return PropertyUtils.getNestedProperty(bean, key);
		}catch (Exception e){
			logger.error("getNestedProperty error! key=" +key + ",bean="+bean ,e) ; 
			return null;
		}
	}

	
	/**
	 * 除
	 * @param number1
	 * @param number2
	 * @return
	 */
	public static long div(Object number1,Object number2) {
		if(number1==null || number2==null) {
			return 0;
		}
		long result = Long.valueOf(number1.toString())/Long.valueOf(number2.toString());
		return result;
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
		Double result = Double.valueOf(number1.toString())/Double.valueOf(number2.toString());
		return Double.valueOf(NUM_FORMAT.format(result));
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
