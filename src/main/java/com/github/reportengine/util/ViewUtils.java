package com.github.reportengine.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.NestedNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.common.beanutils.PropertyUtils;
import com.duowan.common.util.ObjectUtils;

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
		DecimalFormat df=new DecimalFormat("0.00"); 
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
	 * @param precision
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
		if(number1==null || number2==null || number2.toString().equals("0") || number2.toString().equals("0.0")) {
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
	 * top n
	 * @param rows
	 * @param column
	 * @param n
	 * @return
	 */
	public static List<Map<String,Object>> topN(List<Map<String,Object>> rows,final String column, int n) {
		List<Map<String,Object>> temp = cloneList(rows);
		Collections.sort(temp, new Comparator<Map<String,Object>>() {
			public int compare(Map<String, Object> row1, Map<String, Object> row2) {
				try {
					Object o1 = PropertyUtils.getNestedProperty(row1, column);
					Object o2 = PropertyUtils.getNestedProperty(row2, column);
					if (o1 == null) {
						return 1;
					}
					if (o2 == null) {
						return -1;
					}
					int num1 = Integer.valueOf(o1.toString());
					int num2 = Integer.valueOf(o2.toString());
					return num2 - num1;
				} catch (NestedNullException e) {
					return -1;
				}
			}
		});
		if(rows.size()<=n) {
			return temp;
		}
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		for(int i=0;i<n;i++) {
			result.add(temp.get(i));
		}
		return result;
	}
	
	public static List<Map<String,Object>> cloneList(List<Map<String,Object>> list) {
		List<Map<String,Object>> temp = new ArrayList<Map<String,Object>>();
		for(Map map : list) {
			temp.add(map);
		}
		return temp;
	}
	
	/**
	 * 求sum
	 * sum(rows, "kcnts.user_cnt","kcnt.abc_cnt")
	 * @param rows
	 * @param columns
	 * @return
	 */
	public static Map<String,Double> sum(List<Map<String,Object>> rows,final String... columns) {
		Map<String,Double> result = new HashMap<String,Double>();
		for(Map row : rows) {
			for(int i=0;i<columns.length;i++) {
				String key = columns[i];
				try {
					Object value = PropertyUtils.getNestedProperty(row, columns[i]);
					if(value != null) {
						Double oldValue = result.get(key);
						if(oldValue == null) {
							oldValue = 0.0;
						}
						result.put(key, oldValue + Double.parseDouble(value.toString()));
					}
				}catch(NestedNullException e) {
					//ignore
				}
			}		
			
		}
		return result;
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
//			Object propertyObject = PropertyUtils.getProperty(bean, key);
//			if (propertyObject instanceof Map) {
//				 Map map = (Map) propertyObject;
//				 if (map.get(key.)) {
//					
//				}
//			}
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
	
	public String objectToString(Object obj) {
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}
	
	/**
	 * 合并两个数据集
	 * @param rows1
	 * @param rows2
	 * @param combineColumens
	 * @param alias
	 * @param innerColumns
	 * @return
	 */
	public static List<Map<String,Object>> combine(List<Map<String,Object>> rows1,List<Map<String,Object>> rows2,String[] combineColumens,String[] alias,String[] innerColumns){
		 if(!(rows1 instanceof java.util.ArrayList)) {
			 rows1 = new ArrayList<Map<String,Object>>();
		 }
	    for(Map row1:rows1) {
	    boolean flag = true;
	       for(Map row2 :rows2) {
	         if(isJoin(row1,row2,innerColumns)) {
	        	 for(int i=0;i<combineColumens.length;i++) {
	  			   row1.put(alias[i], row2.get(combineColumens[i]));
	  			  }
	           rows2.remove(row2);
	           flag = false;
	           break;
	         }
	       }
	       if(flag) {
	    	   for(int i=0;i<combineColumens.length;i++) {
	    		   copyDataStructure(row1,combineColumens[i],alias[i]);
	  		   }  
	       }
	    }
	   for(Map row : rows2) {
		 for(int i=0;i<combineColumens.length;i++) {
			 row.put(alias[i], row.get(combineColumens[i]));
			 copyDataStructure(row,alias[i],combineColumens[i]);
  		 }  
	     ((ArrayList)rows1).add(row);
	   }
	   return rows1;
	}
	/**
	 * 把第二数据集合并到第一个
	 * @param rows1
	 * @param rows2
	 * @param combineColumens
	 * @param alias
	 * @param innerColumns
	 * @return
	 */
	public static List<Map<String,Object>> mergeTo(List<Map<String,Object>> rows1,List<Map<String,Object>> rows2,String[] combineColumens,String[] alias,String[] innerColumns){
		 if(!(rows1 instanceof java.util.ArrayList)) {
			 rows1 = new ArrayList<Map<String,Object>>();
		 }
	    for(Map row1:rows1) {
	    boolean flag = true;
	       for(Map row2 :rows2) {
	         if(isJoin(row1,row2,innerColumns)) {
	        	 for(int i=0;i<combineColumens.length;i++) {
	  			   row1.put(alias[i], row2.get(combineColumens[i]));
	  			  }
	           rows2.remove(row2);
	           flag = false;
	           break;
	         }
	       }
	       if(flag) {
	    	   for(int i=0;i<combineColumens.length;i++) {
	    		   copyDataStructure(row1,combineColumens[i],alias[i]);
	  		   }  
	       }
	    }
	   return rows1;
	}
	
	private static boolean isJoin(Map row1,Map row2,String[] innerColumns) {
	   for(String column:innerColumns) {
		   Object colValue1 = row1.get(column);
		   Object colValue2 = row2.get(column);
		   if(colValue1 == null && colValue1 == null) {
			   continue;
		   }else if(colValue1 == null || colValue1 == null){
			   return false;
		   }else {
			   if(!colValue1.equals(colValue2)) {
				  return false;
			   }
		   }
	   }
	   return true;
	}
	
	private static void copyDataStructure(Map row, String column, String alias) {
		Object o = row.get(column);
		if(o instanceof Integer || o instanceof Long || o instanceof Double || o instanceof Float) {
			row.put(alias, 0);
		} else if(o instanceof String) {
			row.put(alias, "");
		} else if(o instanceof java.util.HashMap) {
			row.put(alias, new HashMap());
		}
	}
}
