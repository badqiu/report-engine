package com.github.reportengine.util;

import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.time.DateUtils;

import com.github.rapid.common.freemarker.directive.BlockDirective;
import com.github.rapid.common.freemarker.directive.ExtendsDirective;
import com.github.rapid.common.freemarker.directive.OverrideDirective;
import com.github.rapid.common.freemarker.directive.SuperDirective;
import com.github.rapid.common.util.DateConvertUtils;
import com.github.reportengine.util.freemarker.NewDirective;

import freemarker.template.Configuration;

public class FreeMarkerConfigurationUtil {

	public static Configuration newDefaultConfiguration() {
		try {
			Configuration conf = new Configuration();
			conf.setSharedVariable("block", new BlockDirective());  
			conf.setSharedVariable("override", new OverrideDirective());  
			conf.setSharedVariable("extends", new ExtendsDirective());  
			conf.setSharedVariable("super", new SuperDirective());
			conf.setSharedVariable("new", new NewDirective());
			
			conf.setSharedVariable("DateUtils", new DateUtils());
			conf.setSharedVariable("DateConvertUtils", new DateConvertUtils());
			conf.setSharedVariable("StringUtils", new StringUtils());
			conf.setSharedVariable("WordUtils", new WordUtils());
			conf.setSharedVariable("NumberUtils", new NumberUtils());
			conf.setSharedVariable("ViewUtils", new ViewUtils());
			conf.setSharedVariable("MiscUtil", new MiscUtil());
			conf.setSharedVariable("DataUtils", new DataUtils());
			conf.setSharedVariable("ObjectUtils", new com.github.rapid.common.util.ObjectUtils());
			conf.setSharedVariable("PDateUtils", new com.github.reportengine.util.DateUtils());
			
			conf.setNumberFormat("######.####");
			conf.setBooleanFormat("true,false");
			conf.setDateFormat("yyyy-MM-dd");
			conf.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
			return conf;
		}catch(Exception e) {
			throw new RuntimeException("error on create Configuration",e);
		}
	}
	
}
