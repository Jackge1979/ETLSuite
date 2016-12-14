package com.cenrise.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解析配置文件的工具类
 * 
 * @author dongpo.jia
 *
 */
public class PropertiesParserUtils {

	private static final Logger LOG = LoggerFactory
			.getLogger(PropertiesParserUtils.class);

	private Properties fProperties = new Properties();

	public Map<String, String> parse(File file) throws ParseException {
		Map<String, String> filePaths = new HashMap<String, String>();
		if (file == null) {
			LOG.warn("配置文件为空!");
			return null;
		}
		if (!file.exists()) {
			LOG.warn(String.format("指定了不存在的配置文件!", file.getAbsolutePath()));
			return null;
		}
		try {
			FileReader reader = new FileReader(file);
			filePaths = parse(reader);
		} catch (FileNotFoundException fex) {
			LOG.warn(String.format("指定了不存在的配置文件!", file.getAbsolutePath()), fex);
			return null;
		}
		return filePaths;
	}

	/**
	 * 解析配置文件。
	 * 
	 * @throws ParseException
	 *             如果定义的配置与要求的规则不一致。
	 */
	Map<String, String> parse(Reader reader) throws ParseException {
		Map<String, String> m = new HashMap<String, String>();
		if (reader == null) {
			// just log and return default properties
			LOG.warn("文件读取器为空!");
			return null;
		}

		BufferedReader br = new BufferedReader(reader);
		String line;
		try {
			while ((line = br.readLine()) != null) {
				int commentMarker = line.indexOf('#');
				if (commentMarker != -1) {
					if (commentMarker == 0) {
						// 跳过注释行
						continue;
					} else {
						// 不合规的注释行
						throw new ParseException(line, commentMarker);
					}
				} else {
					if (line.isEmpty() || line.matches("^\\s*$")) {
						// 跳过空行或者无实际意义的特殊符号行
						continue;
					}

					// 读取用空格分割的key-value值
					int deilimiterIdx = line.indexOf('=');
					String key = line.substring(0, deilimiterIdx).trim();
					String value = line.substring(deilimiterIdx + 1).trim();
					m.put(key, value);
					fProperties.put(key, value);
				}
			}
		} catch (IOException ex) {
			throw new ParseException("Failed to read", 1);
		}
		return m;
	}

	public Properties getProperties() {

		return fProperties;
	}

	public String getProperty(File f, String key) throws Exception {
		parse(f);
		Properties properties = getProperties();
		// 获取value
		String value = properties.getProperty(key);
		return value;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		PropertiesParserUtils ppu = new PropertiesParserUtils();

		// PathConfiguerUtils pcu = new PathConfiguerUtils();
		File file = new File(
				"D:\\workspace_all\\workspace_TI-ETL\\ExcelInput\\src\\main\\resources\\MU_B2T_EXCEL.properties");
		Map<String, String> maps = ppu.parse(file);
		System.out.println(maps.get("encoding"));

	}

}
