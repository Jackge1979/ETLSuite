package com.cenrise.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;

public class Const {

	/**
	 * DOSCR: MS-DOS specific Carriage Return
	 */
	public static final String DOSCR = "\n\r";

	/**
	 * An empty ("") String.
	 */
	public static final String EMPTY_STRING = "";
	public static final String XML_ENCODING = "UTF-8";

	/** Value type indicating that the value has no type set */
	public static final int TYPE_NONE = 0;

	/**
	 * Value type indicating that the value contains a floating point double
	 * precision number.
	 */
	public static final int TYPE_NUMBER = 1;

	/** Value type indicating that the value contains a text String. */
	public static final int TYPE_STRING = 2;

	/** Value type indicating that the value contains a Date. */
	public static final int TYPE_DATE = 3;

	/** Value type indicating that the value contains a boolean. */
	public static final int TYPE_BOOLEAN = 4;

	/** Value type indicating that the value contains a long integer. */
	public static final int TYPE_INTEGER = 5;

	/**
	 * Value type indicating that the value contains a floating point precision
	 * number with arbitrary precision.
	 */
	public static final int TYPE_BIGNUMBER = 6;

	/** Value type indicating that the value contains an Object. */
	public static final int TYPE_SERIALIZABLE = 7;

	/**
	 * Value type indicating that the value contains binary data: BLOB, CLOB,
	 * ...
	 */
	public static final int TYPE_BINARY = 8;

	public static final int TYPE_BIT = 9;
	/**
	 * What's the file systems file separator on this operating system?
	 */
	public static final String FILE_SEPARATOR = System
			.getProperty("file.separator");

	/**
	 * What's the path separator on this operating system?
	 */
	public static final String PATH_SEPARATOR = System
			.getProperty("path.separator");

	/**
	 * CR: operating systems specific Carriage Return
	 */
	public static final String CR = System.getProperty("line.separator");

	/**
	 * Fetch size in rows when querying a database
	 */
	public static final int FETCH_SIZE = 5000;

	/**
	 * The default number format
	 */
	public static final String DEFAULT_NUMBER_FORMAT = ((DecimalFormat) (NumberFormat
			.getInstance())).toPattern();

	/**
	 * Default string representing Null String values (empty)
	 */
	public static final String NULL_STRING = "";

	/**
	 * Default string representing Null Number values (empty)
	 */
	public static final String NULL_NUMBER = "";

	/**
	 * Default string representing Null Date values (empty)
	 */
	public static final String NULL_DATE = "";

	/**
	 * Default string representing Null BigNumber values (empty)
	 */
	public static final String NULL_BIGNUMBER = "";

	/**
	 * Default string representing Null Boolean values (empty)
	 */
	public static final String NULL_BOOLEAN = "";

	/**
	 * Default string representing Null Integer values (empty)
	 */
	public static final String NULL_INTEGER = "";

	/**
	 * Default string representing Null Binary values (empty)
	 */
	public static final String NULL_BINARY = "";
	/**
	 * The default locale for the kettle environment (system defined)
	 */
	public static final Locale DEFAULT_LOCALE = Locale.getDefault(); // new
																		// Locale("nl",
																		// "BE");
	/**
	 * Default string representing Null Undefined values (empty)
	 */
	public static final String NULL_NONE = "";

	/**
	 * Indicating that the string content should NOT be trimmed if conversion is
	 * to occur to another data type
	 */
	public static final int TRIM_TYPE_NONE = 0;

	/**
	 * Indicating that the string content should be LEFT trimmed if conversion
	 * is to occur to another data type
	 */
	public static final int TRIM_TYPE_LEFT = 1;

	/**
	 * Indicating that the string content should be RIGHT trimmed if conversion
	 * is to occur to another data type
	 */
	public static final int TRIM_TYPE_RIGHT = 2;

	/**
	 * Indicating that the string content should be LEFT AND RIGHT trimmed if
	 * conversion is to occur to another data type
	 */
	public static final int TRIM_TYPE_BOTH = 3;
	/**
	 * The default decimal separator . or ,
	 */
	public static final char DEFAULT_DECIMAL_SEPARATOR = (new DecimalFormatSymbols(
			DEFAULT_LOCALE)).getDecimalSeparator();

	/**
	 * The default grouping separator , or .
	 */
	public static final char DEFAULT_GROUPING_SEPARATOR = (new DecimalFormatSymbols(
			DEFAULT_LOCALE)).getGroupingSeparator();

	/**
	 * Version number
	 */
	public static final String VERSION = "1.0.0";

	/** The prefix that all internal kettle variables should have */
	public static final String INTERNAL_VARIABLE_PREFIX = "Internal";

	/**
	 * Check if the string supplied is empty. A String is empty when it is null
	 * or when the length is 0
	 * 
	 * @param string
	 *            The string to check
	 * @return true if the string supplied is empty
	 */
	public static final boolean isEmpty(String string) {
		return string == null || string.length() == 0;
	}

	/**
	 * Check if the array supplied is empty. An array is empty when it is null
	 * or when the length is 0
	 * 
	 * @param array
	 *            The array to check
	 * @return true if the array supplied is empty
	 */
	public static final boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Determines whether or not a character is considered a space. A character
	 * is considered a space in Kettle if it is a space, a tab, a newline or a
	 * cariage return.
	 * 
	 * @param c
	 *            The character to verify if it is a space.
	 * @return true if the character is a space. false otherwise.
	 */
	public static final boolean isSpace(char c) {
		return c == ' ' || c == '\t' || c == '\r' || c == '\n'
				|| Character.isWhitespace(c);
	}

	/**
	 * Checks whether or not a String consists only of spaces.
	 * 
	 * @param str
	 *            The string to check
	 * @return true if the string has nothing but spaces.
	 */
	public static final boolean onlySpaces(String str) {
		for (int i = 0; i < str.length(); i++)
			if (!isSpace(str.charAt(i)))
				return false;
		return true;
	}

	/**
	 * rounds double f to any number of places after decimal point Does
	 * arithmetic using BigDecimal class to avoid integer overflow while
	 * rounding TODO: make the rounding itself optional in the Props for
	 * performance reasons.
	 * 
	 * @param f
	 *            The value to round
	 * @param places
	 *            The number of decimal places
	 * @return The rounded floating point value
	 */
	public static double round(double f, int places) {
		java.math.BigDecimal bdtemp = java.math.BigDecimal.valueOf(f);
		bdtemp = bdtemp.setScale(places, java.math.BigDecimal.ROUND_HALF_EVEN);
		return bdtemp.doubleValue();
	}

	/**
	 * Returns a string of the stack trace of the specified exception
	 */
	public static final String getStackTracker(Throwable e) {
		return getClassicStackTrace(e);
	}

	public static final String getClassicStackTrace(Throwable e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		String string = stringWriter.toString();
		try {
			stringWriter.close();
		} catch (IOException ioe) {
		} // is this really required?
		return string;
	}

	public static String getCustomStackTrace(Throwable aThrowable) {
		final StringBuilder result = new StringBuilder();
		String errorMessage = aThrowable.toString();
		result.append(errorMessage);
		if (!errorMessage.contains(Const.CR)) {
			result.append(CR);
		}

		// add each element of the stack trace
		//
		for (StackTraceElement element : aThrowable.getStackTrace()) {
			result.append(element);
			result.append(CR);
		}
		return result.toString();
	}

	/**
	 * Search for a string in an array of strings and return the index.
	 * 
	 * @param lookup
	 *            The string to search for
	 * @param array
	 *            The array of strings to look in
	 * @return The index of a search string in an array of strings. -1 if not
	 *         found.
	 */
	public static final int indexOfString(String lookup, String array[]) {
		if (array == null)
			return -1;
		if (lookup == null)
			return -1;

		for (int i = 0; i < array.length; i++) {
			if (lookup.equalsIgnoreCase(array[i]))
				return i;
		}
		return -1;
	}

	/**
	 * Search for strings in an array of strings and return the indexes.
	 * 
	 * @param lookup
	 *            The strings to search for
	 * @param array
	 *            The array of strings to look in
	 * @return The indexes of strings in an array of strings. -1 if not found.
	 */
	public static final int[] indexsOfStrings(String lookup[], String array[]) {
		int[] indexes = new int[lookup.length];
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = indexOfString(lookup[i], array);
		return indexes;
	}

	/**
	 * Search for strings in an array of strings and return the indexes. If a
	 * string is not found, the index is not returned.
	 * 
	 * @param lookup
	 *            The strings to search for
	 * @param array
	 *            The array of strings to look in
	 * @return The indexes of strings in an array of strings. Only existing
	 *         indexes are returned (no -1)
	 */
	public static final int[] indexsOfFoundStrings(String lookup[],
			String array[]) {
		List<Integer> indexesList = new ArrayList<Integer>();
		for (int i = 0; i < lookup.length; i++) {
			int idx = indexOfString(lookup[i], array);
			if (idx >= 0)
				indexesList.add(Integer.valueOf(idx));
		}
		int[] indexes = new int[indexesList.size()];
		for (int i = 0; i < indexesList.size(); i++)
			indexes[i] = (indexesList.get(i)).intValue();
		return indexes;
	}

	/**
	 * Search for a string in a list of strings and return the index.
	 * 
	 * @param lookup
	 *            The string to search for
	 * @param list
	 *            The ArrayList of strings to look in
	 * @return The index of a search string in an array of strings. -1 if not
	 *         found.
	 */
	public static final int indexOfString(String lookup, List<String> list) {
		if (list == null)
			return -1;

		for (int i = 0; i < list.size(); i++) {
			String compare = list.get(i);
			if (lookup.equalsIgnoreCase(compare))
				return i;
		}
		return -1;
	}

	/**
	 * Sort the strings of an array in alphabetical order.
	 * 
	 * @param input
	 *            The array of strings to sort.
	 * @return The sorted array of strings.
	 */
	public static final String[] sortStrings(String input[]) {
		Arrays.sort(input);
		return input;
	}

	/**
	 * Implements Oracle style NVL function
	 * 
	 * @param source
	 *            The source argument
	 * @param def
	 *            The default value in case source is null or the length of the
	 *            string is 0
	 * @return source if source is not null, otherwise return def
	 */
	public static final String NVL(String source, String def) {
		if (source == null || source.length() == 0)
			return def;
		return source;
	}

	/**
	 * Convert strings separated by a string into an array of strings.
	 * <p>
	 * <code>
     Example: a;b;c;d    ==>  new String[] { a, b, c, d }
     * </code>
	 * 
	 * <p>
	 * <b>NOTE: this differs from String.split() in a way that the built-in
	 * method uses regular expressions and this one does not.</b>
	 * 
	 * @param string
	 *            The string to split
	 * @param separator
	 *            The separator used.
	 * @return the string split into an array of strings
	 */
	public static final String[] splitString(String string, String separator) {
		/*
		 * 0123456 Example a;b;c;d --> new String[] { a, b, c, d }
		 */
		// System.out.println("splitString ["+path+"] using ["+separator+"]");
		List<String> list = new ArrayList<String>();

		if (string == null || string.length() == 0) {
			return new String[] {};
		}

		int sepLen = separator.length();
		int from = 0;
		int end = string.length() - sepLen + 1;

		for (int i = from; i < end; i += sepLen) {
			if (string.substring(i, i + sepLen).equalsIgnoreCase(separator)) {
				// OK, we found a separator, the string to add to the list
				// is [from, i[
				list.add(NVL(string.substring(from, i), ""));
				from = i + sepLen;
			}
		}

		// Wait, if the string didn't end with a separator, we still have
		// information at the end of the string...
		// In our example that would be "d"...
		if (from + sepLen <= string.length()) {
			list.add(NVL(string.substring(from, string.length()), ""));
		}

		return list.toArray(new String[list.size()]);
	}

	/**
	 * Convert strings separated by a character into an array of strings.
	 * <p>
	 * <code>
     Example: a;b;c;d    ==  new String[] { a, b, c, d }
     * </code>
	 * 
	 * @param string
	 *            The string to split
	 * @param separator
	 *            The separator used.
	 * @return the string split into an array of strings
	 */
	public static final String[] splitString(String string, char separator) {
		/*
		 * 0123456 Example a;b;c;d --> new String[] { a, b, c, d }
		 */
		// System.out.println("splitString ["+path+"] using ["+separator+"]");
		List<String> list = new ArrayList<String>();

		if (string == null || string.length() == 0) {
			return new String[] {};
		}

		int from = 0;
		int end = string.length();

		for (int i = from; i < end; i += 1) {
			if (string.charAt(i) == separator) {
				// OK, we found a separator, the string to add to the list
				// is [from, i[
				list.add(NVL(string.substring(from, i), ""));
				from = i + 1;
			}
		}

		// Wait, if the string didn't end with a separator, we still have
		// information at the end of the string...
		// In our example that would be "d"...
		if (from + 1 <= string.length()) {
			list.add(NVL(string.substring(from, string.length()), ""));
		}

		return list.toArray(new String[list.size()]);
	}

	/**
	 * Convert strings separated by a string into an array of strings.
	 * <p>
	 * <code>
	 *   Example /a/b/c --> new String[] { a, b, c }
	 * </code>
	 * 
	 * @param path
	 *            The string to split
	 * @param separator
	 *            The separator used.
	 * @return the string split into an array of strings
	 */
	public static final String[] splitPath(String path, String separator) {
		//
		// Example /a/b/c --> new String[] { a, b, c }
		//
		// Make sure training slashes are removed
		//
		// Example /a/b/c/ --> new String[] { a, b, c }
		//

		// Check for empty paths...
		//
		if (path == null || path.length() == 0 || path.equals(separator)) {
			return new String[] {};
		}

		// lose trailing separators
		//
		while (path.endsWith(separator)) {
			path = path.substring(0, path.length() - 1);
		}

		int sepLen = separator.length();
		int nr_separators = 1;
		int from = path.startsWith(separator) ? sepLen : 0;

		for (int i = from; i < path.length(); i += sepLen) {
			if (path.substring(i, i + sepLen).equalsIgnoreCase(separator)) {
				nr_separators++;
			}
		}

		String spath[] = new String[nr_separators];
		int nr = 0;
		for (int i = from; i < path.length(); i += sepLen) {
			if (path.substring(i, i + sepLen).equalsIgnoreCase(separator)) {
				spath[nr] = path.substring(from, i);
				nr++;

				from = i + sepLen;
			}
		}
		if (nr < spath.length) {
			spath[nr] = path.substring(from);
		}

		//
		// a --> { a }
		//
		if (spath.length == 0 && path.length() > 0) {
			spath = new String[] { path };
		}

		return spath;
	}

	/**
	 * 十六进制字符串转化为byte数组
	 * 
	 * @param hexString
	 * @return
	 */
	public static byte[] hex2Byte(String hexString) {
		if (StringUtils.isEmpty(hexString)) {
			throw new IllegalArgumentException(
					"this hexString must not be empty");
		}

		hexString = hexString.toLowerCase();
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {
			// 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
			// 将hex 转换成byte "&" 操作为了防止负数的自动扩展
			// hex转换成byte 其实只占用了4位，然后把高位进行右移四位
			// 然后“|”操作 低四位 就能得到 两个 16进制数转换成一个byte.
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}

		return byteArray;
	}

	/**
	 * byte数组转化为十六进制字符串
	 * 
	 * @param b
	 * @return 十六进制字符串
	 */
	public static String byte2hex(byte[] b) {
		StringBuilder hex = new StringBuilder();
		String stmp = "";
		int len = b.length;
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1) {
				hex.append('0').append(stmp);
			} else {
				hex.append(stmp);
			}
		}

		return hex.toString();
	}

	/**
	 * Left trim: remove spaces to the left of a String.
	 * 
	 * @param str
	 *            The String to left trim
	 * @return The left trimmed String
	 */
	public static String ltrim(String source) {
		if (source == null)
			return null;
		int from = 0;
		while (from < source.length() && isSpace(source.charAt(from)))
			from++;

		return source.substring(from);
	}

	/**
	 * Right trim: remove spaces to the right of a string
	 * 
	 * @param str
	 *            The string to right trim
	 * @return The trimmed string.
	 */
	public static String rtrim(String source) {
		if (source == null)
			return null;

		int max = source.length();
		while (max > 0 && isSpace(source.charAt(max - 1)))
			max--;

		return source.substring(0, max);
	}

	/**
	 * Trims a string: removes the leading and trailing spaces of a String.
	 * 
	 * @param str
	 *            The string to trim
	 * @return The trimmed string.
	 */
	public static final String trim(String str) {
		if (str == null)
			return null;

		int max = str.length() - 1;
		int min = 0;

		while (min <= max && isSpace(str.charAt(min)))
			min++;
		while (max >= 0 && isSpace(str.charAt(max)))
			max--;

		if (max < min)
			return "";

		return str.substring(min, max + 1);
	}

	/**
	 * Right pad a string: adds spaces to a string until a certain length. If
	 * the length is smaller then the limit specified, the String is truncated.
	 * 
	 * @param ret
	 *            The string to pad
	 * @param limit
	 *            The desired length of the padded string.
	 * @return The padded String.
	 */
	public static final String rightPad(String ret, int limit) {
		if (ret == null)
			return rightPad(new StringBuffer(), limit);
		else
			return rightPad(new StringBuffer(ret), limit);
	}

	/**
	 * Right pad a StringBuffer: adds spaces to a string until a certain length.
	 * If the length is smaller then the limit specified, the String is
	 * truncated.
	 * 
	 * @param ret
	 *            The StringBuffer to pad
	 * @param limit
	 *            The desired length of the padded string.
	 * @return The padded String.
	 */
	public static final String rightPad(StringBuffer ret, int limit) {
		int len = ret.length();
		int l;

		if (len > limit) {
			ret.setLength(limit);
		} else {
			for (l = len; l < limit; l++)
				ret.append(' ');
		}
		return ret.toString();
	}

	/**
	 * 创建文件支持多级目录 如果目录不存在先创建目录再创建文件，如果目录存在直接创建目录
	 *
	 * @param filePath
	 *            需要创建的文件
	 * @return 是否成功
	 */
	public static boolean createDirFiles(String filePath) {
		File file = new File(filePath);
		File dir = file.getParentFile();
		if (!dir.exists()) {
			if (dir.mkdirs()) {
				try {
					return file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				return file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 *            包含路径的文件名
	 * @throws Exception
	 */
	public static boolean delFile(String fileName) throws Exception {
		try {
			String filePath = fileName;
			java.io.File delFile = new java.io.File(filePath);
			return delFile.delete();
		} catch (Exception e) {
			throw new Exception("删除文件操作出错: " + e.getMessage());
		}
	}

	/**
	 * 查找符合正则表达式reg的的文件
	 *
	 * @param dirPath
	 *            搜索的目录
	 * @param reg
	 *            正则表达式
	 * @return 返回文件列表
	 */
	public static List<File> searchFileReg(File dirPath, String reg) {
		List<File> list = new ArrayList();
		File[] files = dirPath.listFiles();
		if (isValid(files)) {
			for (File file : files) {
				if (file.isDirectory()) {
					list.addAll(searchFile(file, reg));
				} else {
					String Name = file.getName();
					if (isMatche(Name, reg)) {
						list.add(file);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 在指定的目录下搜寻文个文件
	 *
	 * @param dirPath
	 *            搜索的目录
	 * @param fileName
	 *            搜索的文件名
	 * @return 返回文件列表
	 */
	public static List<File> searchFile(File dirPath, String fileName) {
		List<File> list = new ArrayList();
		File[] files = dirPath.listFiles();
		if (isValid(files)) {
			for (File file : files) {
				if (file.isDirectory()) {
					list.addAll(searchFile(file, fileName));
				} else {
					String Name = file.getName();
					if (Name.equals(fileName)) {
						list.add(file);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 判断字符串有效性
	 */
	public static boolean isValid(String src) {
		return !(src == null || "".equals(src.trim()));
	}

	public static boolean isValid(String... src) {
		for (String s : src) {
			if (!isValid(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断一个对象是否为空
	 */
	public static boolean isValid(Object obj) {
		return !(null == obj);
	}

	public static boolean isValid(Object[] objs) {
		if (objs != null && objs.length != 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断集合的有效性
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isValid(Collection col) {
		return !(col == null || col.isEmpty());
	}

	public static boolean isValid(Collection... cols) {
		for (Collection c : cols) {
			if (!isValid(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Map集合是否为null或空集合
	 * 
	 * @param map
	 * @return
	 */
	public static boolean isValid(Map map) {
		return !(map == null || map.isEmpty());
	}

	/**
	 * 是否有效
	 * 
	 * @param maps
	 * @return
	 */
	public static boolean isValid(Map... maps) {
		for (Map m : maps) {
			if (!isValid(m)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串str是否符合正则表达式reg
	 *
	 * @param str
	 *            需要处理的字符串
	 * @param reg
	 *            正则
	 * @return 是否匹配
	 */
	public static boolean isMatche(String str, String reg) {
		Pattern pattern = Pattern.compile(reg);
		Matcher isNum = pattern.matcher(str);
		return isNum.matches();
	}

	/**
	 * Replace values in a String with another.
	 * 
	 * @param string
	 *            The original String.
	 * @param repl
	 *            The text to replace
	 * @param with
	 *            The new text bit
	 * @return The resulting string with the text pieces replaced.
	 */
	public static final String replace(String string, String repl, String with) {
		StringBuffer str = new StringBuffer(string);
		for (int i = str.length() - 1; i >= 0; i--) {
			if (str.substring(i).startsWith(repl)) {
				str.delete(i, i + repl.length());
				str.insert(i, with);
			}
		}
		return str.toString();
	}

	/**
	 * Return the input string trimmed as specified.
	 * 
	 * @param string
	 *            String to be trimmed
	 * @param trimType
	 *            Type of trimming
	 * 
	 * @return Trimmed string.
	 */
	public static String trimToType(String string, int trimType) {
		switch (trimType) {
		case 3:
			return trim(string);
		case 1:
			return ltrim(string);
		case 2:
			return rtrim(string);
		case 0:
		default:
			return string;
		}
	}

	/**
	 * Create an array of Strings consisting of spaces. The index of a String in
	 * the array determines the number of spaces in that string.
	 * 
	 * @return array of 'space' Strings.
	 */
	public static String[] getEmptyPaddedStrings() {
		if (emptyPaddedSpacesStrings == null) {
			emptyPaddedSpacesStrings = new String[250];
			for (int i = 0; i < emptyPaddedSpacesStrings.length; i++) {
				emptyPaddedSpacesStrings[i] = rightPad("", i);
			}
		}
		return emptyPaddedSpacesStrings;
	}

	private static String[] emptyPaddedSpacesStrings;

	/**
	 * Sets the first character of each word in upper-case.
	 * 
	 * @param string
	 *            The strings to convert to initcap
	 * @return the input string but with the first character of each word
	 *         converted to upper-case.
	 */
	public static final String initCap(String string) {
		StringBuffer change = new StringBuffer(string);
		boolean new_word;
		int i;
		char lower, upper, ch;

		new_word = true;
		for (i = 0; i < string.length(); i++) {
			lower = change.substring(i, i + 1).toLowerCase().charAt(0); // Lowercase
																		// is
																		// default.
			upper = change.substring(i, i + 1).toUpperCase().charAt(0); // Uppercase
																		// for
																		// new
																		// words.
			ch = upper;

			if (new_word) {
				change.setCharAt(i, upper);
			} else {
				change.setCharAt(i, lower);
			}

			new_word = false;

			// Cast to (int) is required for extended characters (SB)
			if (!Character.isLetterOrDigit((int) ch) && ch != '_')
				new_word = true;
		}

		return change.toString();
	}

	/**
	 * Utility class for use in JavaScript to create a new byte array. This is
	 * surprisingly difficult to do in JavaScript.
	 * 
	 * @return a new java byte array
	 */
	public static final byte[] createByteArray(int size) {
		return new byte[size];
	}

	// modified by lichao,20130618,ETL-169,改为年月日格式，同时改为24小时制
	public static final String GENERALIZED_DATE_TIME_FORMAT = "yyyyMMdd_HHmmss";
	public static final String GENERALIZED_DATE_TIME_FORMAT_MILLIS = "yyyyMMdd_HHmmssSSS";

	/**
	 * Convert a String into an integer. If the conversion fails, assign a
	 * default value.
	 * 
	 * @param str
	 *            The String to convert to an integer
	 * @param def
	 *            The default value
	 * @return The converted value or the default.
	 */
	public static final int toInt(String str, int def) {
		int retval;
		try {
			retval = Integer.parseInt(str);
		} catch (Exception e) {
			retval = def;
		}
		return retval;
	}

	/**
	 * 字符的拼音，多音字就得到第一个拼音。不是汉字，就return null。
	 */
	private static String pinyin(char c) {
		String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);
		if (pinyins == null) {
			return null;
		}
		return pinyins[0];
	}

	/**
	 * 汉字排序
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static int compareString(String o1, String o2,
			boolean caseInsensitive) {

		if (caseInsensitive) {
			o1 = o1.toLowerCase();
			o2 = o2.toLowerCase();
		}
		for (int i = 0; i < o1.length() && i < o2.length(); i++) {

			int codePoint1 = o1.charAt(i);
			int codePoint2 = o2.charAt(i);

			if (Character.isSupplementaryCodePoint(codePoint1)
					|| Character.isSupplementaryCodePoint(codePoint2)) {
				i++;
			}

			if (codePoint1 != codePoint2) {
				if (Character.isSupplementaryCodePoint(codePoint1)
						|| Character.isSupplementaryCodePoint(codePoint2)) {
					return codePoint1 - codePoint2;
				}

				String pinyin1 = pinyin((char) codePoint1);
				String pinyin2 = pinyin((char) codePoint2);

				if (pinyin1 != null && pinyin2 != null) { // 两个字符都是汉字
					if (!pinyin1.equals(pinyin2)) {
						return pinyin1.compareTo(pinyin2);
					} else { // modified by wanglh , add : check codePoint, if
								// pinyin1 equals pinyin2 ,
						return codePoint1 - codePoint2;
					}
				} else {
					return codePoint1 - codePoint2;
				}
			}
		}
		return o1.length() - o2.length();
	}

	/**
	 * 逻辑:context必须包含keys，且key的值不能为null
	 * 
	 * 1、context中不包含Key. 或者 2、context中包含key，但值为null 抛出异常
	 * 
	 * @param context
	 *            Context对象
	 * @param keys
	 */
	public static void ensureRequiredNonNull(Context context, String... keys) {
		for (String key : keys) {
			if (!context.getParameters().containsKey(key)
					|| context.getParameters().get(key) == null) {
				throw new IllegalArgumentException("Required parameter " + key
						+ " must exist and may not be null");
			}
		}
	}

}
