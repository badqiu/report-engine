package com.github.reportengine.util.freemarker;

import java.io.IOException;
import java.util.Map;

import com.github.reportengine.util.ClassUtil;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 创建新对象,并放入freemarker 共享变量的指令
 * 
 * 使用示例:
 * <@new class="com.github.reportengine.util.MiscUtil"/>
 * 
 * @author badqiu
 *
 */
public class NewDirective  implements TemplateDirectiveModel{

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Object name = params.get("name");
		Object clazz = params.get("class");
		if(clazz == null) {
			throw new RuntimeException("not found required param: class");
		}
		 
		Object instance = ClassUtil.newInstance(String.valueOf(clazz));
		if(name == null) {
			name= instance.getClass().getSimpleName();
		}
		env.getConfiguration().setSharedVariable(String.valueOf(name), instance);
	}

}
