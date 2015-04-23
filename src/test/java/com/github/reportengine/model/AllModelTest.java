package com.github.reportengine.model;

import org.junit.Assert;
import org.junit.Test;

import com.github.rapid.common.test.util.BeanAssert;
import com.github.rapid.common.test.util.BeanDefaultValueUtils;

public class AllModelTest extends Assert{

	@Test
	public void testAllModel() throws InstantiationException, IllegalAccessException {
		testPropertiesAndCommonMethod(Chart.class);
		testPropertiesAndCommonMethod(Chart.Ser.class);
		testPropertiesAndCommonMethod(Groovy.class);
		testPropertiesAndCommonMethod(Param.class);
		testPropertiesAndCommonMethod(Query.class,"dataSource");
		testPropertiesAndCommonMethod(Report.class);
		testPropertiesAndCommonMethod(Table.class,"paginator");
		testPropertiesAndCommonMethod(Table.Column.class);
//		testPropertiesAndCommonMethod(DataSource.class);
	}
	
	@SuppressWarnings("rawtypes")
	private static void testPropertiesAndCommonMethod(Class clazz,String... ignoreProperties)
			throws InstantiationException, IllegalAccessException {
		Object bean = clazz.newInstance();
		BeanDefaultValueUtils.setBeanProperties(bean);
		BeanAssert.assertPropertiesNotNull(bean,ignoreProperties);

		Object origian = clazz.newInstance();
		assertFalse(bean.equals(origian));
		assertFalse(bean.hashCode() == origian.hashCode());
		assertFalse(bean.toString().equals(origian.toString()));
	}
}
