/**
 * 
 */
package com.cenrise.util;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通过传入指定的dgf.properties文件的路径或传入文件的方式，获取dgf.properties对于key的绝对路径
 * <li>dgf.properties可以是文件路径，可以是File对象。
 * <li>通过路径或文件名解析dgf.properties文件
 * <li>根据dgf.properties文件的key获取value文件对于的绝对路径
 * <li>dgf.propertes的value必须是存在的文件名
 * 
 * @author dongpo.jia
 *
 */
public class PathConfiguerUtils {
	private static final Logger logger = LoggerFactory.getLogger(PathConfiguerUtils.class);

	/**
	 * 根据传入的配置文件路径，解析配置文件，获取value文件的绝对路径
	 * 
	 * @param file
	 * @return 返回一个Map，这个map的key为文件名称，value此文件的绝对路径
	 * @throws ParseException
	 */
	public static Map<String, String> getPath(String filePath) throws Exception {
		Map<String, String> filePathMap = new HashMap<String, String>();
		if (StringUtils.isEmpty(filePath)) {
			logger.warn("filePath不能为空!");
			return null;
		}
		File file = new File(filePath);
		if (file.isDirectory()) {
			logger.warn("filePath不能是目录!");
			return null;
		}
		filePathMap = getPath(file);
		return filePathMap;
	}

	/**
	 * 根据给出的配文件解析配置文件的value对于的文件的绝对路径
	 * 
	 * @param file
	 * @return
	 */
	public static Map<String, String> getPath(File file) throws Exception {
		// 转化后的文件名称和文件路径的Map
		Map<String, String> filePathMap = new HashMap<String, String>();
		PropertiesParserUtils pp = new PropertiesParserUtils();
		// properties文件的key和value的map
		Map<String, String> tmp = new HashMap<String, String>();
		tmp = pp.parse(file);
		String fileForder = file.getParent();
		Set<?> set = tmp.entrySet();
		Iterator<?> i = set.iterator();
		while (i.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, String> entry1 = (Map.Entry<String, String>) i.next();
			filePathMap.put(entry1.getValue(), fileForder + File.separator + entry1.getValue());
		}
		return filePathMap;
	}

	/**
	 * 根据传入的文件，properties文件的key，获取value值。
	 * 
	 * @param file
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String getProperty(File file, String key) throws Exception {
		PropertiesParserUtils p = new PropertiesParserUtils();
		// 获取value
		String value = p.getProperty(file, key);
		return value;
	}
	/**
	 * 根据proKey获取对应文件的绝对路径
	 * @param proKey
	 * @return
	 */
	public static String getAbsolutePath(String proKey)throws Exception{
		if(StringUtils.isEmpty(proKey)){
			logger.warn("输入的key不能为空。");
			return null;
		}
		String absolutePath = null;
		String dgfpath = System.getProperty("dgf.path");
		File f = new File(dgfpath);
		Map<String,String> filePaths = getPath(f);
		String proValue = getProperty(f,proKey);
		if(filePaths!=null){
			absolutePath = filePaths.get(proValue);
		}
		return absolutePath;
	}
	

	public static void main(String[] args) throws Exception {
//		File f = new File("E:\\dgfsources\\dgf-server\\conf\\dgf.properties");
		System.setProperty(
				"dgf.path", "//Users//yp-tc-m-2684//workspace_all//etl//ExcelInput//src//main//resources//pros//MU_B2T_EXCEL.properties");
		String proKey = "MU_B2T.excel.tailLineNum";
		// System.out.println(p.getProperty(f, DGF_FILE_PATH_CLUSTER));
		System.out.println(getAbsolutePath(proKey));

	}
}
