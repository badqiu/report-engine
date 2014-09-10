package com.duowan.reportengine.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
/**
 * 
 * @author badqiu
 *
 */
public class CloneUtil {
	/**
	 * 深度克隆对象
	 */
	public static  <T> T deepClone(T obj) {
		if(obj == null) return null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(baos);
			os.writeObject(obj);
			os.close();
			
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
			return (T)ois.readObject();
		}catch(Exception e) {
			throw new RuntimeException("deepClone error,class:"+obj.getClass().getName(),e);
		}
	}
	
}
