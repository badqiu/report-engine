package com.github.reportengine.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.reflect.FieldUtils;

import com.duowan.common.util.ObjectUtils;

public class FieldUtil {

	/**
	 * 将child的field从parent继承
	 * @param parent
	 * @param child
	 */
	public static void inheritanceFields(Object parent, Object child) {
		for (Field parentField : getAllFields(parent.getClass())) {
			try {
				Object parentValue = getFieldValue(parent, parentField);
				if (ObjectUtils.isEmpty(parentValue)
						|| isZeroNumber(parentValue)) {
					continue;
				}
				
				Field childField = getField(child.getClass(),parentField.getName());
				if(childField == null) {
					continue;
				}
				
				Object childValue = getFieldValue(child, childField);
				
				if((ObjectUtils.isEmpty(childValue) || isZeroNumber(childValue)) ) {
					setFieldValue(child,childField, parentValue);
				}
			} catch (Exception e) {
				throw new RuntimeException("override field:"
						+ parentField.getName() + " error,cuase:" + e, e);
			}
		}
	}

	private static Object getFieldValue(Object source, Field field)
			throws IllegalAccessException {
		field.setAccessible(true);
		return field.get(source);
	}

	private static void setFieldValue(Object source, Field field,Object value)
			throws IllegalAccessException {
		field.setAccessible(true);
		field.set(source, value);
	}
	
    public static Field getField(final Class cls,String name) {
        if (cls == null) {
            throw new IllegalArgumentException("The class must not be null");
        }
        for(Field f : getAllFields(cls)) {
        	if(f.getName().equalsIgnoreCase(name)) {
        		return f;
        	}
        }
        return null;
    }
    
    public static List<Field> getAllFields(final Class cls) {
        if (cls == null) {
            throw new IllegalArgumentException("The class must not be null");
        }
        List<Field> allFields = new ArrayList<Field>();
        // check up the superclass hierarchy
        for (Class acls = cls; acls != null; acls = acls.getSuperclass()) {
            Field[] fields = acls.getDeclaredFields();
            allFields.addAll(Arrays.asList(fields));
        }
        return allFields;
    }
    

	private static boolean isZeroNumber(Object targetValue) {
		if (targetValue instanceof Number) {
			return ((Number) targetValue).longValue() == 0;
		}
		return false;
	}

}
