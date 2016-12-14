package com.cenrise.test;

import java.lang.reflect.Field;

/**
 * Java反射：根据属性名得到其Set方法设置它的值
 * 
 * @author ThinkPad
 *
 */
public class UpdateFieldUseSet {
	private String x = "0";

	public static void main(String[] ag) {
		UpdateFieldUseSet a = new UpdateFieldUseSet();
		Field field = null;
		try {
			field = a.getClass().getDeclaredField("x");
			try {
				System.out.println("xiugaiqiandezhi-----------------"
						+ field.get(a));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//类中的成员变量为private时需要设置可访问，否则会出现异常 java.lang.IllegalAccessException
		field.setAccessible(true);
		try {
			field.set((Object) a, "1");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 读取
		Field f = null;
		try {
			f = a.getClass().getDeclaredField("x");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		f.setAccessible(true);
		try {
			System.out.println("xiugaihoudezhi-----------------" + f.get(a));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}