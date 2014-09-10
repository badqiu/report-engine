package com.duowan.reportengine.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerUtil {

	public static String processTemplateIntoString(Configuration conf,String freemarkerTemplate,Map params) throws IOException, TemplateException {
		try {
			Template t = new Template("freemarkerTemplate", new StringReader(freemarkerTemplate),conf);
			String sql = FreeMarkerTemplateUtils.processTemplateIntoString(t,params);
			return sql;
		}catch(Exception e) {
			throw new RuntimeException("error on process freemarkerTemplateString:"+freemarkerTemplate+" params:"+params,e);
		}
	}

}
