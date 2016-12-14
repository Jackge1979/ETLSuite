package com.cenrise.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * 
 * @ClassName: Context
 * @Description: Context通过key-value的形式保存系统的配置信息
 * @author dongpo.jia
 * @date 2016年12月12日
 *
 */
public class Context {

	/**
	 * 线程安全的Map,存储参数
	 */
	private Map<String, String> parameters;

	public Context() {
		parameters = Collections.synchronizedMap(new HashMap<String, String>());
	}

	public Context(Map<String, String> paramters) {
		this();
		this.putAll(paramters);
	}

	/**
	 * 获取当前Context的一个不变拷贝。
	 * 
	 * @return 当前参数对象的一个immutable copy
	 */
	public ImmutableMap<String, String> getParameters() {
		synchronized (parameters) {
			return ImmutableMap.copyOf(parameters);
		}
	}

	/**
	 * 清空map中的所有数据
	 */
	public void clear() {
		parameters.clear();
	}

	/**
	 * 获取使用指定前缀标识的属性。返回的结果是移除了前缀的key-value对。
	 * 例如：如果传输的参数为“hdfs.”,当paramters中包含如下内容时： <code>
	 * { hdfs.key = value, otherKey = otherValue }
	 * </code> 该方法将会返回： <code>
	 * { key = value}
	 * </code>
	 *
	 * <b>Note:</b> <tt>前缀</tt>英文的句号(点)结束. 否则的话将会抛出 IllegalArgumentException.
	 *
	 * @param prefix
	 *            key的前缀
	 * @return
	 * @throws IllegalArguemntException
	 *             如果前缀不是以英文句号(点)结束，则抛出该异常
	 */
	public ImmutableMap<String, String> getSubProperties(String prefix) {
		Preconditions.checkArgument(prefix.endsWith("."),
				"The given prefix does not end with a period (" + prefix + ")");
		Map<String, String> result = Maps.newHashMap();
		synchronized (parameters) {
			for (String key : parameters.keySet()) {
				if (key.startsWith(prefix)) {
					String name = key.substring(prefix.length());
					result.put(name, parameters.get(key));
				}
			}
		}
		return ImmutableMap.copyOf(result);
	}

	/**
	 * 将给定的map对象添加到Context中
	 */
	public void putAll(Map<String, String> map) {
		parameters.putAll(map);
	}

	/**
	 * 将指定的key-value添加到Context中。如果当前Context中存在使用该key标识的map，则用新值覆盖旧值 如果
	 * key所对应的value不是 null且在忽略大小写时等于 "true"，则返回的 boolean表示true值。
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, String value) {
		parameters.put(key, value);
	}

	/**
	 * 使用指定的key，返回其value，如果没有对应的key，则返回defaultValue 如果 key所对应的value不是 null
	 * 且在忽略大小写时等于 "true"，则返回的 boolean 表示 true 值。
	 * 
	 * @param key
	 * @param defaultValue
	 *            如果key不存在，则返回defaultValue
	 * @return value
	 */
	public Boolean getBoolean(String key, Boolean defaultValue) {
		String value = get(key);
		if (value != null) {
			return Boolean.parseBoolean(value.trim());
		}
		return defaultValue;
	}

	/**
	 * 使用指定的key,返回其布尔表示，如果没有对应的key,则返回null 如果 key所对应的value不是 null 且在忽略大小写时等于
	 * "true"，则返回的 boolean 表示 true 值。
	 * <p>
	 * 该方法返回Boolean类型。
	 * </p>
	 * 
	 * @param key
	 *            to be found
	 * @return value associated with key or null if unmapped
	 */
	public Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}

	/**
	 * 根据指定的key，获取value的Integer的表示。如果没有指定的key，则返回defaultValue
	 * 如果取到的值不能表示为Integer，则抛出{@link NullPointerException}
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Integer getInteger(String key, Integer defaultValue) {
		String value = get(key);
		if (value != null) {
			return Integer.parseInt(value.trim());
		}
		return defaultValue;
	}

	/**
	 * 根据指定的key获取value的Integer表示，如果不能找到，则返回null 如果取到的值不能表示为Integer，则抛出
	 * {@link NullPointerException}
	 * 
	 * @param key
	 * @return value 如果找不到值则返回false
	 */
	public Integer getInteger(String key) {
		return getInteger(key, null);
	}

	/**
	 * 根据指定的key返回value的Long表示，如果找不到key则返回defaultValue 如果取到的值不能表示为Long，则抛出
	 * {@link NullPointerException}
	 * 
	 * @param key
	 * @param defaultValue
	 * @return value的Long表示
	 */
	public Long getLong(String key, Long defaultValue) {
		String value = get(key);
		if (value != null) {
			return Long.parseLong(value.trim());
		}
		return defaultValue;
	}

	/**
	 * 根据指定的key返回value的Long表示，如果找不到key则返回null. 如果取到的值不能表示为Long，则抛出
	 * {@link NullPointerException}
	 * 
	 * @param key
	 * @return value
	 */
	public Long getLong(String key) {
		return getLong(key, null);
	}

	/**
	 * 根据指定的key返回value的String表示，如果找不到key则返回null.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return value
	 */
	public String getString(String key, String defaultValue) {
		return get(key, defaultValue);
	}

	/**
	 * 根据指定的key返回value的String表示，如果找不到key则返回null.
	 * 
	 * @param key
	 * @return value
	 */
	public String getString(String key) {
		return get(key);
	}

	private String get(String key, String defaultValue) {
		String result = parameters.get(key);
		if (result != null) {
			return result;
		}
		return defaultValue;
	}

	private String get(String key) {
		return get(key, null);
	}

	@Override
	public String toString() {
		return "{ parameters:" + parameters + " }";
	}

	/**
	 * 根据给定的前缀，获得该前缀的带序号的key
	 * 
	 * @param prefix
	 * @return
	 */
	public List<String> getNumberedKeys(String prefix) {
		List<String> numberedKeys = new ArrayList<String>();
		Vector<Integer> numbers = new Vector<Integer>();
		if (parameters != null) {
			Iterator<String> allkeys = parameters.keySet().iterator();
			while (allkeys.hasNext()) {
				String pkey = (String) allkeys.next();
				if (pkey.startsWith(prefix)) {
					String tmpKey = pkey.substring((prefix).length());
					int i = tmpKey.indexOf(".");
					String sNum = "0";
					if (i != -1) {
						sNum = tmpKey.substring(0, i);
					} else {
						sNum = tmpKey;
					}

					int num = -1;
					try {
						num = Integer.parseInt(sNum);
						if (!numbers.contains(num)) {
							numbers.add(num);
						}

					} catch (NumberFormatException e) {
						continue;
					}
				}
			}

			Collections.sort(numbers);
			for (Integer i : numbers) {
				numberedKeys.add(prefix + i);
			}
		}
		return numberedKeys;
	}

	// public static void main(String[] args){
	//
	// Context c = new Context();
	//
	// Map<String, String> map = new HashMap<String,String>();
	// map.put("a.a.x1.a", "x1");
	// map.put("a.a.x1.b", "x1");
	// map.put("a.a.x3.b", "x3");
	// map.put("a.a.x2.c", "x2");
	// map.put("a.a.x.x1", "xx1");
	// map.put("a.a.x.x3", "xx3");
	// map.put("a.a.x.x2", "xx2");
	// c.putAll(map);
	// List<String> keys = c.getNumberedKeys("a.a.x.x");
	// for (String s : keys) {
	// System.out.println(s);
	// }
	// }
}
