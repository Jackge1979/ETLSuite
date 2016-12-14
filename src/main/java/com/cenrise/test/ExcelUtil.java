package com.cenrise.test;

import java.lang.reflect.Field;

/**
 * Hello world!
 *
 */
public class ExcelUtil {
	/**
	 * 为对象的属性名称设置值。
	 * 
	 * @param obj
	 *            要设置值的对象实例
	 * @param fie
	 *            字段名，与类的属性名一致
	 * @param val
	 *            设置这个字段的值
	 */
	public static void setValue(Object obj, String fie, Object val) {
		Class<?> cla = obj.getClass();
		Field field = null;
		try {
			field = cla.getDeclaredField(fie);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 类中的成员变量为private时需要设置可访问，否则会出现异常 java.lang.IllegalAccessException
		field.setAccessible(true);
		try {
			field.set((Object) obj, val);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TestEntity testInstance = new TestEntity();
		ExcelUtil.setValue(testInstance, "name", "zhangsan!");
		ExcelUtil.setValue(testInstance, "age", 20);
		System.out
				.println(testInstance.getName() + ":" + testInstance.getAge());
	}
}
