package com.github.reportengine.util;

import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.time.DateUtils;

import com.github.rapid.common.freemarker.directive.BlockDirective;
import com.github.rapid.common.freemarker.directive.ExtendsDirective;
import com.github.rapid.common.freemarker.directive.OverrideDirective;
import com.github.rapid.common.freemarker.directive.SuperDirective;
import com.github.rapid.common.util.DateConvertUtil;
import com.github.reportengine.util.freemarker.NewDirective;

import freemarker.template.Configuration;

public class FreeMarkerConfigurationUtil {

	public static Configuration newDefaultConfiguration() {
		try {
			Configuration conf = new Configuration();
			conf.setLocale(Locale.CHINA);
			conf.setDefaultEncoding("UTF-8");
			conf.setSharedVariable("block", new BlockDirective());  
			conf.setSharedVariable("override", new OverrideDirective());  
			conf.setSharedVariable("extends", new ExtendsDirective());  
			conf.setSharedVariable("super", new SuperDirective());
			conf.setSharedVariable("new", new NewDirective());
			
			conf.setSharedVariable("DateUtils", new DateUtils());
			conf.setSharedVariable("DateConvertUtil", new DateConvertUtil());
			conf.setSharedVariable("StringUtils", new StringUtils());
			conf.setSharedVariable("CollectionUtils", new CollectionUtils());
			conf.setSharedVariable("WordUtils", new WordUtils());
			conf.setSharedVariable("NumberUtils", new org.apache.commons.lang.math.NumberUtils());
			conf.setSharedVariable("MomUtil", new MomUtil());
			conf.setSharedVariable("MapUtil", new MapUtil());
			conf.setSharedVariable("ViewUtils", new ViewUtils());
//			conf.setSharedVariable("MiscUtil", new MiscUtil());
//			conf.setSharedVariable("DataUtils", new DataUtils());
			conf.setSharedVariable("ObjectUtil", new com.github.rapid.common.util.ObjectUtil());
			conf.setSharedVariable("JsonUtil", new JsonUtil());
			conf.setSharedVariable("PDateUtils", new com.github.reportengine.util.DateUtil());
			
			conf.setNumberFormat("###,###,###,###.##");
			conf.setTimeFormat("HH:mm:ss");
			conf.setBooleanFormat("true,false");
			conf.setDateFormat("yyyy-MM-dd");
			conf.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
			return conf;
		}catch(Exception e) {
			throw new RuntimeException("error on create Configuration",e);
		}
	}
	
}
