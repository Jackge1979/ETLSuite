package com.cenrise.meta;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.cenrise.util.Const;
import com.cenrise.util.Value;

/**
 * 
 * @ClassName: ValueMeta
 * @Description: 数据值元数据类，定义数的名称和类型
 * @author dongpo.jia
 * @date 2016年12月12日
 *
 */
public class ValueMeta implements ValueMetaInterface {

	static String BIT_0 = "0";

	static String BIT_1 = "1";

	private String name;

	private int type = TYPE_NONE;

	// private int sqlType = Types.VARCHAR;

	private String sqlTypeName = "";

	private int storageType = STORAGE_TYPE_NORMAL;

	private int length;

	private int precision;

	private String stringEncoding;

	private ValueMetaInterface storageMetadata;

	private ValueMetaInterface conversionMetadata;

	private DecimalFormat decimalFormat;

	private String currencySymbol;

	private static SimpleDateFormat compatibleDateFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss.SSS");

	private String decimalSymbol;

	private SimpleDateFormat dateFormat;

	private boolean dateFormatChanged;

	private String conversionMask;

	private boolean decimalFormatChanged;

	private boolean identicalFormat;

	private int trimType;

	private String comments;

	private String groupingSymbol;

	public static final String DEFAULT_DATE_FORMAT_MASK = "yyyy/MM/dd HH:mm:ss.SSS";

	private Locale dateFormatLocale;

	private boolean dateFormatLenient;

	private boolean largeTextField;

	private String sqlType;

	// get & store original result set meta data for later use
	// @see java.sql.ResultSetMetaData
	private int originalColumnType;

	private String originalColumnTypeName;

	private int originalPrecision;

	private int originalScale;

	private boolean originalAutoIncrement;

	private int originalNullable;

	private boolean originalSigned;
	private boolean sortedDescending;
	private boolean caseInsensitive;
	private Object[] index;
	private String origin;

	/**
	 * The trim type codes
	 */
	public final static String trimTypeCode[] = { "none", "left", "right",
			"both" };

	/**
	 * The trim description
	 */
	public final static String trimTypeDesc[] = { "不去掉空格", "去掉左空格", "去掉右空格",
			"去掉左右两端空格" };

	 //用于表示字段在Excel中的序号，add by dongpo.jia
    private int columnIndexExcel;
    
	public ValueMeta() {
	}

	public ValueMeta(String name) {
		this.name = name;
	}

	public ValueMeta(String name, int type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public int getPrecision() {
		return precision;
	}

	@Override
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	@Override
	public int getStorageType() {
		return storageType;
	}

	@Override
	public void setStorageType(int storageType) {
		this.storageType = storageType;
	}

	@Override
	public boolean isStorageBinaryString() {
		return storageType == STORAGE_TYPE_BINARY_STRING;
	}

	@Override
	public boolean isStorageNormal() {
		return storageType == STORAGE_TYPE_NORMAL;
	}

	/**
	 * Return the type of a value in a textual form: "String", "Number",
	 * "Integer", "Boolean", "Date", ...
	 * 
	 * @return A String describing the type of value.
	 */
	@Override
	public String getTypeDesc() {
		return typeCodes[type];
	}

	public static String getTypeDesc(int code) {
		return typeCodes[code];
	}

	/**
	 * Return the storage type of a value in a textual form: "normal",
	 * "binary-string", "indexes"
	 * 
	 * @return A String describing the storage type of the value metadata
	 */
	@Override
	public String getStorageTypeDesc() {
		return storageTypeCodes[storageType];
	}

	/**
	 * a String text representation of this Value, optionally padded to the
	 * specified length
	 * 
	 * @return a String text representation of this Value, optionally padded to
	 *         the specified length
	 */
	@Override
	public String toStringMeta() {
		// We (Sven Boden) did explicit performance testing for this
		// part. The original version used Strings instead of StringBuffers,
		// performance between the 2 does not differ that much. A few
		// milliseconds
		// on 100000 iterations in the advantage of StringBuffers. The
		// lessened creation of objects may be worth it in the long run.
		StringBuffer retval = new StringBuffer(getTypeDesc());

		switch (getType()) {
		case TYPE_STRING:
			if (getLength() > 0)
				retval.append('(').append(getLength()).append(')');
			break;
		case TYPE_BIT:
			if (getLength() > 0)
				retval.append('(').append(getLength()).append(')');
			break;
		case TYPE_NUMBER:
		case TYPE_BIGNUMBER:
			if (getLength() > 0) {
				retval.append('(').append(getLength());
				if (getPrecision() > 0) {
					retval.append(", ").append(getPrecision());
				}
				retval.append(')');
			}
			break;
		case TYPE_INTEGER:
			if (getLength() > 0) {
				retval.append('(').append(getLength()).append(')');
			}
			break;
		default:
			break;
		}

		if (!isStorageNormal()) {
			retval.append("<").append(getStorageTypeDesc()).append(">");
		}

		return retval.toString();
	}

	@Override
	public ValueMeta clone() {
		try {
			ValueMeta valueMeta = (ValueMeta) super.clone();
			return valueMeta;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public boolean isNull(Object data) {
		return data == null;
	}

	private synchronized String convertIntegerToString(Long i)
			throws RuntimeException {
		return i.toString();
	}

	private synchronized String convertIntegerToCompatibleString(Long integer)
			throws RuntimeException {
		if (integer == null)
			return null;
		return Long.toString(integer);
	}

	public synchronized Long convertStringToInteger(String s)
			throws RuntimeException {
		if (Const.isEmpty(s))
			return null;

		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			throw new RuntimeException(toString()
					+ " : couldn't convert String to Integer", e);
		}
	}

	private synchronized String convertBigNumberToString(BigDecimal number)
			throws RuntimeException {
		if (number == null)
			return null;

		try {
			return number.toString();
		} catch (Exception e) {
			throw new RuntimeException(toString()
					+ " : couldn't convert BigNumber to String ", e);
		}
	}

	public synchronized BigDecimal convertStringToBigNumber(String s)
			throws RuntimeException {

		if (Const.isEmpty(s))
			return null;

		try {
			return new BigDecimal(s);
		} catch (NumberFormatException e) {
			throw new RuntimeException(toString()
					+ " : couldn't convert string value '" + s
					+ "' to a big number.", e);
		}
	}

	// BOOLEAN + STRING

	private String convertBooleanToString(Boolean bool) {
		if (bool == null)
			return null;
		if (length == 1) {
			return bool.booleanValue() ? "1" : "0";
		}
		if (length >= 3) {
			return bool.booleanValue() ? "true" : "false";
		} else {
			return bool.booleanValue() ? "Y" : "N";
		}
	}

	public static Boolean convertStringToBoolean(String string) {
		if (Const.isEmpty(string))
			return null;
		return Boolean.valueOf("Y".equalsIgnoreCase(string)
				|| "TRUE".equalsIgnoreCase(string)
				|| "YES".equalsIgnoreCase(string) || "1".equals(string));
	}

	// BOOLEAN + NUMBER

	private Double convertBooleanToNumber(Boolean bool) {
		if (bool == null)
			return null;
		return new Double(bool.booleanValue() ? 1.0 : 0.0);
	}

	private Boolean convertNumberToBoolean(Double number) {
		if (number == null)
			return null;
		return Boolean.valueOf(number.intValue() != 0);
	}

	// BOOLEAN + INTEGER

	private Long convertBooleanToInteger(Boolean bool) {
		if (bool == null)
			return null;
		return Long.valueOf(bool.booleanValue() ? 1L : 0L);
	}

	private Boolean convertIntegerToBoolean(Long number) {
		if (number == null)
			return null;
		return Boolean.valueOf(number.longValue() != 0);
	}

	// BOOLEAN + BIGNUMBER

	private BigDecimal convertBooleanToBigNumber(Boolean bool) {
		if (bool == null)
			return null;
		return bool.booleanValue() ? BigDecimal.ONE : BigDecimal.ZERO;
	}

	private Boolean convertBigNumberToBoolean(BigDecimal number) {
		if (number == null)
			return null;
		return Boolean.valueOf(number.intValue() != 0);
	}

	/**
	 * Converts a byte[] stored in a binary string storage type into a String;
	 * 
	 * @param binary
	 *            the binary string
	 * @return the String in the correct encoding.
	 * @throws RuntimeException
	 */
	private String convertBinaryStringToString(byte[] binary)
			throws RuntimeException {
		// OK, so we have an internal representation of the original object,
		// read from file.
		// Before we release it back, we have to see if we don't have to do a
		// String-<type>-String
		// conversion with different masks.
		// This obviously only applies to numeric data and dates.
		// We verify if this is true or false in advance for performance reasons
		//
		// if (binary==null || binary.length==0) return null;
		if (binary == null || binary.length == 0) {
			return (binary != null) ? "" : null;
		}

		String encoding = this.stringEncoding;

		if (Const.isEmpty(encoding)) {
			return new String(binary);
		} else {
			try {
				return new String(binary, encoding);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(
						toString()
								+ " : couldn't convert binary value to String with specified string encoding ["
								+ stringEncoding + "]", e);
			}
		}
	}

	/**
	 * Converts the specified data object to the normal storage type.
	 * 
	 * @param object
	 *            the data object to convert
	 * @return the data in a normal storage type
	 * @throws RuntimeException
	 *             In case there is a data conversion error.
	 */
	public Object convertToNormalStorageType(Object object)
			throws RuntimeException {
		if (object == null)
			return null;

		switch (storageType) {
		case STORAGE_TYPE_NORMAL:
			return object;
		case STORAGE_TYPE_BINARY_STRING:
			return convertBinaryStringToNativeType((byte[]) object);
		default:
			throw new RuntimeException(toStringMeta()
					+ " : Unknown storage type [" + storageType
					+ "] while converting to normal storage type");
		}
	}

	/**
	 * Convert the binary data to the actual data type.<br>
	 * - byte[] --> Long (Integer) - byte[] --> Double (Number) - byte[] -->
	 * BigDecimal (BigNumber) - byte[] --> Date (Date) - byte[] --> Boolean
	 * (Boolean) - byte[] --> byte[] (Binary)
	 * 
	 * @param binary
	 * @return
	 * @throws RuntimeException
	 */
	public Object convertBinaryStringToNativeType(byte[] binary)
			throws RuntimeException {
		if (binary == null)
			return null;

		// numberOfBinaryStringConversions++;

		// OK, so we have an internal representation of the original object,
		// read from file.
		// First we decode it in the correct encoding
		//
		String string = convertBinaryStringToString(binary);

		// In this method we always must convert the data.
		// We use the storageMetadata object to convert the binary string
		// object.
		//
		// --> Convert from the String format to the current data type...
		//
		return convertData(storageMetadata, string);
	}

	/**
	 * Convert the specified data to the data type specified in this object.
	 * 
	 * @param meta2
	 *            the metadata of the object to be converted
	 * @param data2
	 *            the data of the object to be converted
	 * @return the object in the data type of this value metadata object
	 * @throws RuntimeException
	 *             in case there is a data conversion error
	 */
	public Object convertData(ValueMetaInterface meta2, Object data2)
			throws RuntimeException {
		switch (getType()) {
		case TYPE_STRING:
			return meta2.getString(data2);
		case TYPE_NUMBER:
			return meta2.getNumber(data2);
		case TYPE_INTEGER:
			return meta2.getInteger(data2);
		case TYPE_DATE:
			return meta2.getDate(data2);
		case TYPE_BIGNUMBER:
			return meta2.getBigNumber(data2);
		case TYPE_BOOLEAN:
			return meta2.getBoolean(data2);
		case TYPE_BINARY:
			return meta2.getBinary(data2);
		case TYPE_BIT:
			return meta2.getBit(data2);
		default:
			throw new RuntimeException(toString()
					+ " : I can't convert the specified value to data type : "
					+ getTypeDesc());
		}
	}

	/**
	 * Convert the specified data to the data type specified in this object. For
	 * String conversion, be compatible with version 2.5.2.
	 * 
	 * @param meta2
	 *            the metadata of the object to be converted
	 * @param data2
	 *            the data of the object to be converted
	 * @return the object in the data type of this value metadata object
	 * @throws RuntimeException
	 *             in case there is a data conversion error
	 */
	public Object convertDataCompatible(ValueMetaInterface meta2, Object data2)
			throws RuntimeException {
		switch (getType()) {
		case TYPE_STRING:
			return meta2.getCompatibleString(data2);
		case TYPE_NUMBER:
			return meta2.getNumber(data2);
		case TYPE_INTEGER:
			return meta2.getInteger(data2);
		case TYPE_DATE:
			return meta2.getDate(data2);
		case TYPE_BIGNUMBER:
			return meta2.getBigNumber(data2);
		case TYPE_BOOLEAN:
			return meta2.getBoolean(data2);
		case TYPE_BINARY:
			return meta2.getBinary(data2);
		case TYPE_BIT:
			return meta2.getBit(data2);
		default:
			throw new RuntimeException(toString()
					+ " : I can't convert the specified value to data type : "
					+ getType());
		}
	}

	public String getCompatibleString(Object object) throws RuntimeException {
		try {
			String string;

			switch (type) {
			case TYPE_DATE:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					string = convertDateToCompatibleString((Date) object);
					break;
				case STORAGE_TYPE_BINARY_STRING:
					string = convertDateToCompatibleString((Date) convertBinaryStringToNativeType((byte[]) object));
					break;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
				break;

			case TYPE_NUMBER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					string = convertNumberToCompatibleString((Double) object);
					break;
				case STORAGE_TYPE_BINARY_STRING:
					string = convertNumberToCompatibleString((Double) convertBinaryStringToNativeType((byte[]) object));
					break;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
				break;

			case TYPE_INTEGER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					string = convertIntegerToCompatibleString((Long) object);
					break;
				case STORAGE_TYPE_BINARY_STRING:
					string = convertIntegerToCompatibleString((Long) convertBinaryStringToNativeType((byte[]) object));
					break;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
				break;

			default:
				return getString(object);
			}

			return string;
		} catch (ClassCastException e) {
			throw new RuntimeException(toString()
					+ " : There was a data type error: the data type of "
					+ object.getClass().getName() + " object [" + object
					+ "] does not correspond to value meta [" + toStringMeta()
					+ "]");
		}
	}

	private synchronized String convertNumberToCompatibleString(Double number)
			throws RuntimeException {
		if (number == null)
			return null;
		return Double.toString(number);
	}

	private synchronized String convertDateToCompatibleString(Date date) {
		if (date == null)
			return null;
		return compatibleDateFormat.format(date);
	}

	/**
	 * Converts the specified data object to the binary string storage type.
	 * 
	 * @param object
	 *            the data object to convert
	 * @return the data in a binary string storage type
	 * @throws RuntimeException
	 *             In case there is a data conversion error.
	 */
	public Object convertToBinaryStringStorageType(Object object)
			throws RuntimeException {
		if (object == null)
			return null;

		switch (storageType) {
		case STORAGE_TYPE_NORMAL:
			return convertNormalStorageTypeToBinaryString(object);
		case STORAGE_TYPE_BINARY_STRING:
			return object;
		default:
			throw new RuntimeException(toStringMeta()
					+ " : Unknown storage type [" + storageType
					+ "] while converting to normal storage type");
		}
	}

	public Object convertNormalStorageTypeToBinaryString(Object object)
			throws RuntimeException {
		if (object == null)
			return null;

		String string = getString(object);

		return convertStringToBinaryString(string);
	}

	private byte[] convertStringToBinaryString(String string)
			throws RuntimeException {
		if (string == null)
			return null;

		if (Const.isEmpty(stringEncoding)) {
			return string.getBytes();
		} else {
			try {
				return string.getBytes(stringEncoding);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(
						toString()
								+ " : couldn't convert String to Binary with specified string encoding ["
								+ stringEncoding + "]", e);
			}
		}
	}

	public synchronized Double convertStringToNumber(String string)
			throws RuntimeException {
		if (Const.isEmpty(string))
			return null;

		try {
			BigDecimal bd = new BigDecimal(string);
			return new Double(bd.doubleValue());
		} catch (Exception e) {
			throw new RuntimeException(toString()
					+ " : couldn't convert String to number ", e);
		}
	}

	private Double convertDateToNumber(Date date) {
		return new Double(date.getTime());
	}

	@Override
	public Double getNumber(Object object) {
		try {
			if (object == null) // NULL
			{
				return null;
			}
			switch (type) {
			case TYPE_NUMBER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return (Double) object;
				case STORAGE_TYPE_BINARY_STRING:
					return (Double) convertBinaryStringToNativeType((byte[]) object);
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_STRING:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertStringToNumber((String) object);
				case STORAGE_TYPE_BINARY_STRING:
					return convertStringToNumber((String) convertBinaryStringToNativeType((byte[]) object));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_BIT:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertStringToNumber((String) object);
				case STORAGE_TYPE_BINARY_STRING:
					return convertStringToNumber((String) convertBinaryStringToNativeType((byte[]) object));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_DATE:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertDateToNumber((Date) object);
				case STORAGE_TYPE_BINARY_STRING:
					return convertDateToNumber((Date) convertBinaryStringToNativeType((byte[]) object));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_INTEGER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return new Double(((Long) object).doubleValue());
				case STORAGE_TYPE_BINARY_STRING:
					return new Double(
							((Long) convertBinaryStringToNativeType((byte[]) object))
									.doubleValue());
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_BIGNUMBER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return new Double(((BigDecimal) object).doubleValue());
				case STORAGE_TYPE_BINARY_STRING:
					return new Double(
							((BigDecimal) convertBinaryStringToNativeType((byte[]) object))
									.doubleValue());
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_BOOLEAN:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertBooleanToNumber((Boolean) object);
				case STORAGE_TYPE_BINARY_STRING:
					return convertBooleanToNumber((Boolean) convertBinaryStringToNativeType((byte[]) object));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_BINARY:
				throw new RuntimeException(
						toString()
								+ " : I don't know how to convert binary values to numbers.");
			case TYPE_SERIALIZABLE:
				throw new RuntimeException(
						toString()
								+ " : I don't know how to convert serializable values to numbers.");
			default:
				throw new RuntimeException(toString() + " : Unknown type "
						+ type + " specified.");
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Unexpected conversion error while converting value ["
							+ toString() + "] to a Number", e);
		}
	}

	@Override
	public Boolean getBoolean(Object object) {
		if (object == null) {
			return null;
		}
		switch (type) {
		case TYPE_BOOLEAN:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return (Boolean) object;
			case STORAGE_TYPE_BINARY_STRING:
				return (Boolean) convertBinaryStringToNativeType((byte[]) object);
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_STRING:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return convertStringToBoolean(((String) object).trim());
			case STORAGE_TYPE_BINARY_STRING:
				return convertStringToBoolean(((String) convertBinaryStringToNativeType((byte[]) object))
						.trim());
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_INTEGER:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return convertIntegerToBoolean((Long) object);
			case STORAGE_TYPE_BINARY_STRING:
				return convertIntegerToBoolean((Long) convertBinaryStringToNativeType((byte[]) object));
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_NUMBER:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return convertNumberToBoolean((Double) object);
			case STORAGE_TYPE_BINARY_STRING:
				return convertNumberToBoolean((Double) convertBinaryStringToNativeType((byte[]) object));
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_BIGNUMBER:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return convertBigNumberToBoolean((BigDecimal) object);
			case STORAGE_TYPE_BINARY_STRING:
				return convertBigNumberToBoolean((BigDecimal) convertBinaryStringToNativeType((byte[]) object));
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");

			}
		case TYPE_BIT:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return object.toString().equals("1") ? true : false;
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_DATE:
			throw new RuntimeException(toString()
					+ " : I don't know how to convert date values to booleans.");
		case TYPE_BINARY:
			throw new RuntimeException(
					toString()
							+ " : I don't know how to convert binary values to booleans.");
		case TYPE_SERIALIZABLE:
			throw new RuntimeException(
					toString()
							+ " : I don't know how to convert serializable values to booleans.");
		default:
			throw new RuntimeException(toString() + " : Unknown type " + type
					+ " specified.");
		}
	}

	/**
	 * note : 暂时只是支持1位 的bit 类型
	 *
	 * 转换规则： 如果字符串是NULL 返回 null 如果字符串是 "1" 返回 BIT_1 如果字符串是 "0" 返回 BIT_0 如果字符串是
	 * "11111X" 返回 BIT_1 如果字符串是 "00000X" 返回 BIT_0 否则返回 BIT_0
	 *
	 *
	 * @param str
	 * @return
	 * @throws RuntimeException
	 */
	public String converStringToBit(String str) throws RuntimeException {
		if (str == null) {
			return null;
		}

		if (BIT_0.equals(str) || BIT_1.equals(str)) {
			return str;
		}

		java.util.regex.Pattern bit_1p = java.util.regex.Pattern
				.compile("[1]*");
		if (bit_1p.matcher(str).matches()) {
			return BIT_1;
		}

		return BIT_0;
	}

	@Override
	public String getBit(Object object) {
		try {
			if (object == null) {
				return null;
			}
			switch (type) {
			case TYPE_BIT:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return converStringToBit((String) object);
				case STORAGE_TYPE_BINARY_STRING:
					return converStringToBit((String) convertBinaryStringToNativeType((byte[]) object));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}

			case TYPE_INTEGER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return (((Long) object).longValue()) > 0 ? BIT_1 : BIT_0;
				case STORAGE_TYPE_BINARY_STRING:
					return (((Long) convertBinaryStringToNativeType((byte[]) object))
							.longValue()) > 0 ? BIT_1 : BIT_0;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_STRING:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return converStringToBit((String) object);
				case STORAGE_TYPE_BINARY_STRING:
					return converStringToBit((String) convertBinaryStringToNativeType((byte[]) object));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_NUMBER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return (((Double) object).doubleValue()) > 0 ? BIT_1
							: BIT_0;
				case STORAGE_TYPE_BINARY_STRING:
					return ((Double) convertBinaryStringToNativeType((byte[]) object))
							.doubleValue() > 0 ? BIT_1 : BIT_0;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_DATE:
				throw new RuntimeException(
						toString()
								+ " : I don't know how to convert date values to bit string.");
			case TYPE_BIGNUMBER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return ((BigDecimal) object).longValue() > 0 ? BIT_1
							: BIT_0;
				case STORAGE_TYPE_BINARY_STRING:
					return ((BigDecimal) convertBinaryStringToNativeType((byte[]) object))
							.longValue() > 0 ? BIT_1 : BIT_0;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_BOOLEAN:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return (Boolean) object ? BIT_1 : BIT_0;
				case STORAGE_TYPE_BINARY_STRING:
					return (Boolean) convertBinaryStringToNativeType((byte[]) object) ? BIT_1
							: BIT_0;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_BINARY:
				throw new RuntimeException(
						toString()
								+ " : I don't know how to convert binary values to integers.");
			case TYPE_SERIALIZABLE:
				throw new RuntimeException(
						toString()
								+ " : I don't know how to convert serializable values to integers.");
			default:
				throw new RuntimeException(toString() + " : Unknown type "
						+ type + " specified.");
			}
		} catch (Exception ex) {

		}
		return null;
	}

	private Long convertDateToInteger(Date date) {
		return new Long(date.getTime());
	}

	@Override
	public Long getInteger(Object object) {
		try {
			if (object == null) // NULL
			{
				return null;
			}
			switch (type) {
			case TYPE_INTEGER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return (Long) object;
				case STORAGE_TYPE_BINARY_STRING:
					return (Long) convertBinaryStringToNativeType((byte[]) object);
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_STRING:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertStringToInteger((String) object);
				case STORAGE_TYPE_BINARY_STRING:
					return convertStringToInteger((String) convertBinaryStringToNativeType((byte[]) object));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_NUMBER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return new Long(Math.round(((Double) object).doubleValue()));
				case STORAGE_TYPE_BINARY_STRING:
					return new Long(
							Math.round(((Double) convertBinaryStringToNativeType((byte[]) object))
									.doubleValue()));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_DATE:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertDateToInteger((Date) object);
				case STORAGE_TYPE_BINARY_STRING:
					return new Long(
							((Date) convertBinaryStringToNativeType((byte[]) object))
									.getTime());
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_BIGNUMBER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return new Long(((BigDecimal) object).longValue());
				case STORAGE_TYPE_BINARY_STRING:
					return new Long(
							((BigDecimal) convertBinaryStringToNativeType((byte[]) object))
									.longValue());
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_BOOLEAN:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertBooleanToInteger((Boolean) object);
				case STORAGE_TYPE_BINARY_STRING:
					return convertBooleanToInteger((Boolean) convertBinaryStringToNativeType((byte[]) object));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_BIT:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return Long.parseLong(object.toString());
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_BINARY:
				throw new RuntimeException(
						toString()
								+ " : I don't know how to convert binary values to integers.");
			case TYPE_SERIALIZABLE:
				throw new RuntimeException(
						toString()
								+ " : I don't know how to convert serializable values to integers.");
			default:
				throw new RuntimeException(toString() + " : Unknown type "
						+ type + " specified.");
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Unexpected conversion error while converting value ["
							+ toString() + "] to an Integer", e);
		}
	}

	@Override
	public boolean isNull(Object object, boolean b) {
		return object == null;
	}

	private synchronized String convertDateToString(Date date) {
		if (date == null)
			return null;

		return compatibleDateFormat.format(date);
	}

	private synchronized String convertNumberToString(Double number)
			throws RuntimeException {
		if (number == null) {
			return null;
		}

		try {
			return String.valueOf(number);
		} catch (Exception e) {
			throw new RuntimeException(toString()
					+ " : couldn't convert Number to String ", e);
		}
	}

	@Override
	public String getString(Object object) {
		try {
			String string;

			switch (type) {
			case TYPE_STRING:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					string = object == null ? null : object.toString();
					break;
				case STORAGE_TYPE_BINARY_STRING:
					string = (String) convertBinaryStringToNativeType((byte[]) object);
					break;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
				if (string != null)
					string = string.trim();
				break;
			case TYPE_BIT:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					string = object == null ? null : object.toString();
					break;
				case STORAGE_TYPE_BINARY_STRING:
					string = (String) convertBinaryStringToNativeType((byte[]) object);
					break;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
				if (string != null)
					string = string.trim();
				break;

			case TYPE_DATE:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					string = convertDateToString((Date) object);
					break;
				case STORAGE_TYPE_BINARY_STRING:
					string = convertDateToString((Date) convertBinaryStringToNativeType((byte[]) object));
					break;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
				break;

			case TYPE_NUMBER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					string = convertNumberToString((Double) object);
					break;
				case STORAGE_TYPE_BINARY_STRING:
					string = convertNumberToString((Double) convertBinaryStringToNativeType((byte[]) object));
					break;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
				break;

			case TYPE_INTEGER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					string = convertIntegerToString((Long) object);
					break;
				case STORAGE_TYPE_BINARY_STRING:
					string = convertIntegerToString((Long) convertBinaryStringToNativeType((byte[]) object));
					break;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
				break;

			case TYPE_BIGNUMBER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					string = convertBigNumberToString((BigDecimal) object);
					break;
				case STORAGE_TYPE_BINARY_STRING:
					string = convertBigNumberToString((BigDecimal) convertBinaryStringToNativeType((byte[]) object));
					break;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
				break;

			case TYPE_BOOLEAN:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					string = convertBooleanToString((Boolean) object);
					break;
				case STORAGE_TYPE_BINARY_STRING:
					string = convertBooleanToString((Boolean) convertBinaryStringToNativeType((byte[]) object));
					break;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
				break;

			case TYPE_BINARY:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					string = convertBinaryStringToString((byte[]) object);
					break;
				case STORAGE_TYPE_BINARY_STRING:
					string = convertBinaryStringToString((byte[]) object);
					break;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
				break;

			case TYPE_SERIALIZABLE:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					string = object == null ? null : object.toString();
					break; // just go for the default toString()
				case STORAGE_TYPE_BINARY_STRING:
					string = convertBinaryStringToString((byte[]) object);
					break;
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
				break;

			default:
				throw new RuntimeException(toString() + " : Unknown type "
						+ type + " specified.");
			}

			return string;
		} catch (ClassCastException e) {
			throw new RuntimeException(toString()
					+ " : There was a data type error: the data type of "
					+ object.getClass().getName() + " object [" + object
					+ "] does not correspond to value meta [" + toStringMeta()
					+ "]");
		}
	}

	private BigDecimal convertDateToBigNumber(Date date) {
		return new BigDecimal(date.getTime());
	}

	@Override
	public BigDecimal getBigNumber(Object object) {
		if (object == null) // NULL
		{
			return null;
		}
		switch (type) {
		case TYPE_BIGNUMBER:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return (BigDecimal) object;
			case STORAGE_TYPE_BINARY_STRING:
				return (BigDecimal) convertBinaryStringToNativeType((byte[]) object);
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_STRING:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return convertStringToBigNumber((String) object);
			case STORAGE_TYPE_BINARY_STRING:
				return convertStringToBigNumber((String) convertBinaryStringToNativeType((byte[]) object));
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_INTEGER:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return BigDecimal.valueOf(((Long) object).longValue());
			case STORAGE_TYPE_BINARY_STRING:
				return BigDecimal
						.valueOf(((Long) convertBinaryStringToNativeType((byte[]) object))
								.longValue());
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_NUMBER:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return BigDecimal.valueOf(((Double) object).doubleValue());
			case STORAGE_TYPE_BINARY_STRING:
				return BigDecimal
						.valueOf(((Double) convertBinaryStringToNativeType((byte[]) object))
								.doubleValue());
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_DATE:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return convertDateToBigNumber((Date) object);
			case STORAGE_TYPE_BINARY_STRING:
				return convertDateToBigNumber((Date) convertBinaryStringToNativeType((byte[]) object));
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_BOOLEAN:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return convertBooleanToBigNumber((Boolean) object);
			case STORAGE_TYPE_BINARY_STRING:
				return convertBooleanToBigNumber((Boolean) convertBinaryStringToNativeType((byte[]) object));
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_BIT:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return new BigDecimal(object.toString());
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_BINARY:
			throw new RuntimeException(
					toString()
							+ " : I don't know how to convert binary values to integers.");
		case TYPE_SERIALIZABLE:
			throw new RuntimeException(
					toString()
							+ " : I don't know how to convert serializable values to integers.");
		default:
			throw new RuntimeException(toString() + " : Unknown type " + type
					+ " specified.");
		}
	}

	@Override
	public byte[] getBinary(Object object) {
		if (object == null) {
			return null;
		}
		switch (type) {
		case TYPE_BINARY:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return (byte[]) object;
			case STORAGE_TYPE_BINARY_STRING:
				return (byte[]) object;
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_DATE:
			throw new RuntimeException(toString()
					+ " : I don't know how to convert a date to binary.");
		case TYPE_STRING:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return convertStringToBinaryString((String) object);
			case STORAGE_TYPE_BINARY_STRING:
				return (byte[]) object;
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_NUMBER:
			throw new RuntimeException(toString()
					+ " : I don't know how to convert a number to binary.");
		case TYPE_INTEGER:
			throw new RuntimeException(toString()
					+ " : I don't know how to convert an integer to binary.");
		case TYPE_BIGNUMBER:
			throw new RuntimeException(toString()
					+ " : I don't know how to convert a bignumber to binary.");
		case TYPE_BOOLEAN:
			throw new RuntimeException(toString()
					+ " : I don't know how to convert a boolean to binary.");
		case TYPE_BIT:
			throw new RuntimeException(toString()
					+ " : I don't know how to convert a bit string to binary.");
		case TYPE_SERIALIZABLE:
			throw new RuntimeException(
					toString()
							+ " : I don't know how to convert a serializable to binary.");

		default:
			throw new RuntimeException(toString() + " : Unknown type " + type
					+ " specified.");
		}
	}

	public synchronized Date convertStringToDate(String string)
			throws RuntimeException {
		if (Const.isEmpty(string))
			return null;

		try {
			return (Date) compatibleDateFormat.parse(string);
		} catch (ParseException e) {
			throw new RuntimeException(toString()
					+ " : couldn't convert string [" + string
					+ "] to a date using format ["
					+ compatibleDateFormat.toPattern() + "]", e);
		}
	}

	private Date convertNumberToDate(Double number) {
		return new Date(number.longValue());
	}

	private Date convertIntegerToDate(Long number) {
		return new Date(number.longValue());
	}

	private Date convertBigNumberToDate(BigDecimal number) {
		return new Date(number.longValue());
	}

	@Override
	public Date getDate(Object object) throws RuntimeException {
		if (object == null) // NULL
		{
			return null;
		}
		switch (type) {
		case TYPE_DATE:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return (Date) object;
			case STORAGE_TYPE_BINARY_STRING:
				return (Date) convertBinaryStringToNativeType((byte[]) object);
			case STORAGE_TYPE_INDEXED:
				return (Date) index[((Integer) object).intValue()];
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_STRING:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return convertStringToDate((String) object);
			case STORAGE_TYPE_BINARY_STRING:
				return convertStringToDate((String) convertBinaryStringToNativeType((byte[]) object));
			case STORAGE_TYPE_INDEXED:
				return convertStringToDate((String) index[((Integer) object)
						.intValue()]);
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_NUMBER:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return convertNumberToDate((Double) object);
			case STORAGE_TYPE_BINARY_STRING:
				return convertNumberToDate((Double) convertBinaryStringToNativeType((byte[]) object));
			case STORAGE_TYPE_INDEXED:
				return convertNumberToDate((Double) index[((Integer) object)
						.intValue()]);
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_INTEGER:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return convertIntegerToDate((Long) object);
			case STORAGE_TYPE_BINARY_STRING:
				return convertIntegerToDate((Long) convertBinaryStringToNativeType((byte[]) object));
			case STORAGE_TYPE_INDEXED:
				return convertIntegerToDate((Long) index[((Integer) object)
						.intValue()]);
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}
		case TYPE_BIGNUMBER:
			switch (storageType) {
			case STORAGE_TYPE_NORMAL:
				return convertBigNumberToDate((BigDecimal) object);
			case STORAGE_TYPE_BINARY_STRING:
				return convertBigNumberToDate((BigDecimal) convertBinaryStringToNativeType((byte[]) object));
			case STORAGE_TYPE_INDEXED:
				return convertBigNumberToDate((BigDecimal) index[((Integer) object)
						.intValue()]);
			default:
				throw new RuntimeException(toString()
						+ " : Unknown storage type " + storageType
						+ " specified.");
			}

		case TYPE_BOOLEAN:
			throw new RuntimeException(toString()
					+ " : I don't know how to convert a boolean to a date.");

		case TYPE_BIT:
			throw new RuntimeException(toString()
					+ " : I don't know how to convert a bit string to a date.");
		case TYPE_BINARY:
			throw new RuntimeException(toString()
					+ " : I don't know how to convert a binary value to date.");
		case TYPE_SERIALIZABLE:
			throw new RuntimeException(
					toString()
							+ " : I don't know how to convert a serializable value to date.");

		default:
			throw new RuntimeException(toString() + " : Unknown type " + type
					+ " specified.");
		}
	}

	/**
	 * @param conversionMask
	 *            the conversionMask to set
	 */
	public void setConversionMask(String conversionMask) {
		this.conversionMask = conversionMask;
		dateFormatChanged = true;
		decimalFormatChanged = true;
		compareStorageAndActualFormat();
	}

	private void compareStorageAndActualFormat() {

		if (storageMetadata == null) {
			identicalFormat = true;
		} else {

			// If a trim type is set, we need to at least try to trim the
			// strings.
			// In that case, we have to set the identical format off.
			//
			if (trimType != TRIM_TYPE_NONE) {
				identicalFormat = false;
			} else {

				// If there is a string encoding set and it's the same encoding
				// in the binary string, then we don't have to convert
				// If there are no encodings set, then we're certain we don't
				// have to convert as well.
				//
				if (getStringEncoding() != null
						&& getStringEncoding().equals(
								storageMetadata.getStringEncoding())
						|| getStringEncoding() == null
						&& storageMetadata.getStringEncoding() == null) {

					// However, perhaps the conversion mask changed since we
					// read the binary string?
					// The output can be different from the input. If the mask
					// is different, we need to do conversions.
					// Otherwise, we can just ignore it...
					//
					if (isDate()) {
						if ((getConversionMask() != null && getConversionMask()
								.equals(storageMetadata.getConversionMask()))
								|| (getConversionMask() == null && storageMetadata
										.getConversionMask() == null)) {
							identicalFormat = true;
						} else {
							identicalFormat = false;
						}
					} else if (isNumeric()) {
						// Check the lengths first
						//
						if (getLength() != storageMetadata.getLength())
							identicalFormat = false;
						else if (getPrecision() != storageMetadata
								.getPrecision())
							identicalFormat = false;
						else
						// For the same reasons as above, if the conversion
						// mask, the decimal or the grouping symbol changes
						// we need to convert from the binary strings to the
						// target data type and then back to a string in the
						// required format.
						//
						if ((getConversionMask() != null
								&& getConversionMask().equals(
										storageMetadata.getConversionMask()) || (getConversionMask() == null && storageMetadata
								.getConversionMask() == null))) {
							if ((getGroupingSymbol() != null && getGroupingSymbol()
									.equals(storageMetadata.getGroupingSymbol()))
									|| (getConversionMask() == null && storageMetadata
											.getConversionMask() == null)) {
								if ((getDecimalFormat(false) != null && getDecimalFormat(
										false)
										.equals(storageMetadata
												.getDecimalFormat(false)))
										|| (getDecimalFormat(false) == null && storageMetadata
												.getDecimalFormat(false) == null)) {
									identicalFormat = true;
								} else {
									identicalFormat = false;
								}
							} else {
								identicalFormat = false;
							}
						} else {
							identicalFormat = false;
						}
					}
				}
			}
		}
	}

	/**
	 * @return the groupingSymbol
	 */
	public String getGroupingSymbol() {
		return groupingSymbol;
	}

	/**
	 * @param groupingSymbol
	 *            the groupingSymbol to set
	 */
	public void setGroupingSymbol(String groupingSymbol) {
		this.groupingSymbol = groupingSymbol;
		decimalFormatChanged = true;
		compareStorageAndActualFormat();
	}

	public synchronized DecimalFormat getDecimalFormat() {
		return getDecimalFormat(false);
	}

	public synchronized DecimalFormat getDecimalFormat(boolean useBigDecimal) {
		// If we have an Integer that is represented as a String
		// In that case we can set the format of the original Integer on the
		// String value metadata in the form of a conversion metadata object.
		// That way, we can always convert from Integer to String and back
		// without a problem, no matter how complex the format was.
		// As such, we should return the decimal format of the conversion
		// metadata.
		//
		if (conversionMetadata != null) {
			return conversionMetadata.getDecimalFormat(useBigDecimal);
		}

		// Calculate the decimal format as few times as possible.
		// That is because creating or changing a DecimalFormat object is very
		// CPU hungry.
		//
		if (decimalFormat == null || decimalFormatChanged) {
			// // gengjie 2012-6-18 增加货币符号的处理机制
			// if(!Const.isEmpty(currencySymbol)){
			// decimalFormat =
			// (DecimalFormat)NumberFormat.getCurrencyInstance();
			// }else{
			decimalFormat = (DecimalFormat) NumberFormat.getInstance();
			// }
			decimalFormat.setParseBigDecimal(useBigDecimal);
			DecimalFormatSymbols decimalFormatSymbols = decimalFormat
					.getDecimalFormatSymbols();

			if (!Const.isEmpty(currencySymbol))
				decimalFormatSymbols.setCurrencySymbol(currencySymbol);
			if (!Const.isEmpty(groupingSymbol))
				decimalFormatSymbols.setGroupingSeparator(groupingSymbol
						.charAt(0));
			if (!Const.isEmpty(decimalSymbol))
				decimalFormatSymbols.setDecimalSeparator(decimalSymbol
						.charAt(0));
			decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

			// Apply the conversion mask if we have one...
			// if(Const.isEmpty(currencySymbol)){
			if (!Const.isEmpty(conversionMask)) {
				decimalFormat.applyPattern(conversionMask);
			} else {
				switch (type) {
				case TYPE_INTEGER: {
					if (length < 1) {
						decimalFormat
								.applyPattern("###############0;-###############0"); // Same
																						// as
																						// before
																						// version
																						// 3.0
					} else {
						StringBuffer integerPattern = new StringBuffer();

						// First the format for positive integers...
						integerPattern.append(" ");
						for (int i = 0; i < getLength(); i++)
							integerPattern.append('0'); // all zeroes.

						int pos = length + 1;
						if (groupingSymbol != null
								&& groupingSymbol.trim().length() != 0) {
							int count = 0;
							for (int i = pos; i > 0; i--) {
								if (count != 0 && count % 3 == 0) {
									integerPattern.insert(i, '0');
									integerPattern.setCharAt(i, ',');
									count = 0;
								}
								count++;
							}
						}

						StringBuffer negativePattern = new StringBuffer(
								integerPattern);
						negativePattern.setCharAt(0, '-');

						integerPattern.append(";");
						integerPattern.append(negativePattern);

						decimalFormat.applyPattern(integerPattern.toString());
					}
				}
					break;
				case TYPE_BIGNUMBER:
				case TYPE_NUMBER: {
					if (length < 1) {
						// gengjie 2012-5-22 去掉mask前面的空格
						decimalFormat
								.applyPattern("##########0.0########;-#########0.0########");
					} else {
						StringBuffer numberPattern = new StringBuffer();

						// First do the format for positive numbers...
						//
						numberPattern.append(' '); // to compensate for minus
													// sign.
						if (precision < 0) // Default: two decimals
						{
							for (int i = 0; i < length; i++)
								numberPattern.append('0');
							numberPattern.append(".00"); // for the .00
						} else // Floating point format 00001234,56 --> (12,2)
						{
							// 精度为0时，不添加小数点分隔符。
							int initLen = 0;
							if (precision == 0)
								initLen = length - 1;
							else
								initLen = length;
							for (int i = 0; i <= initLen; i++)
								numberPattern.append('0'); // all zeroes.
							int pos = length - precision + 1;
							if (precision != 0 && pos >= 0
									&& pos < numberPattern.length()) {
								numberPattern.setCharAt(length - precision + 1,
										'.'); // one 'comma'
							}
							// 处理千分位
							if (groupingSymbol != null
									&& groupingSymbol.trim().length() != 0) {
								int count = 0;
								for (int i = pos; i > 0; i--) {
									if (count != 0 && count % 3 == 0) {
										// 添加千分位符号时，需要补位，否则会出现长度和设置的长度不一致的问题。
										numberPattern.insert(i, '0');
										numberPattern.setCharAt(i, ',');
										count = 0;
									}
									count++;
								}
							}
						}

						// Now do the format for negative numbers...
						//
						StringBuffer negativePattern = new StringBuffer(
								numberPattern);
						negativePattern.setCharAt(0, '-');

						numberPattern.append(";");
						numberPattern.append(negativePattern);

						// Apply the pattern...
						// 去掉格式中的空格
						decimalFormat.applyPattern(numberPattern.toString()
								.trim());
					}
				}
				}

			}
			// }
			decimalFormatChanged = false;
		}
		return decimalFormat;
	}

	/**
	 * Checks whether or not this value is a Date
	 * 
	 * @return true if the value is a Date
	 */
	public boolean isDate() {
		return type == TYPE_DATE;
	}

	/**
	 * Checks whether or not the value is a String.
	 * 
	 * @return true if the value is a String.
	 */
	public boolean isString() {
		return type == TYPE_STRING;
	}

	/**
	 * Checks whether or not the value is a Big Number
	 * 
	 * @return true is this value is a big number
	 */
	public boolean isBigNumber() {
		return type == TYPE_BIGNUMBER;
	}

	/**
	 * Checks whether or not the value is a Number
	 * 
	 * @return true is this value is a number
	 */
	public boolean isNumber() {
		return type == TYPE_NUMBER;
	}

	/**
	 * Checks whether or not this value is a boolean
	 * 
	 * @return true if this value has type boolean.
	 */
	public boolean isBoolean() {
		return type == TYPE_BOOLEAN;
	}

	/**
	 * Checks whether or not this value is of type Serializable
	 * 
	 * @return true if this value has type Serializable
	 */
	public boolean isSerializableType() {
		return type == TYPE_SERIALIZABLE;
	}

	/**
	 * Checks whether or not this value is of type Binary
	 * 
	 * @return true if this value has type Binary
	 */
	public boolean isBinary() {
		return type == TYPE_BINARY;
	}

	/**
	 * Checks whether or not this value is an Integer
	 * 
	 * @return true if this value is an integer
	 */
	public boolean isInteger() {
		return type == TYPE_INTEGER;
	}

	public boolean isBit() {
		return type == TYPE_BIT;
	}

	/**
	 * Checks whether or not this Value is Numeric A Value is numeric if it is
	 * either of type Number or Integer
	 * 
	 * @return true if the value is either of type Number or Integer
	 */
	public boolean isNumeric() {
		return isInteger() || isNumber() || isBigNumber();
	}

	/**
	 * Checks whether or not the specified type is either Integer or Number
	 * 
	 * @param t
	 *            the type to check
	 * @return true if the type is Integer or Number
	 */
	public static final boolean isNumeric(int t) {
		return t == TYPE_INTEGER || t == TYPE_NUMBER || t == TYPE_BIGNUMBER;
	}

	public String getStringEncoding() {
		return stringEncoding;
	}

	public void setStringEncoding(String stringEncoding) {
		this.stringEncoding = stringEncoding;
	}

	public ValueMetaInterface getStorageMetadata() {
		return storageMetadata;
	}

	public void setStorageMetadata(ValueMetaInterface storageMetadata) {
		this.storageMetadata = storageMetadata;
	}

	public static SimpleDateFormat getCompatibleDateFormat() {
		return compatibleDateFormat;
	}

	public static void setCompatibleDateFormat(
			SimpleDateFormat compatibleDateFormat) {
		ValueMeta.compatibleDateFormat = compatibleDateFormat;
	}

	public boolean isDateFormatChanged() {
		return dateFormatChanged;
	}

	public void setDateFormatChanged(boolean dateFormatChanged) {
		this.dateFormatChanged = dateFormatChanged;
	}

	public boolean isDecimalFormatChanged() {
		return decimalFormatChanged;
	}

	public void setDecimalFormatChanged(boolean decimalFormatChanged) {
		this.decimalFormatChanged = decimalFormatChanged;
	}

	public boolean isIdenticalFormat() {
		return identicalFormat;
	}

	public void setIdenticalFormat(boolean identicalFormat) {
		this.identicalFormat = identicalFormat;
	}

	public int getTrimType() {
		return trimType;
	}

	public void setTrimType(int trimType) {
		this.trimType = trimType;
	}

	public String getConversionMask() {
		return conversionMask;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String getCurrencySymbol() {
		return currencySymbol;
	}

	@Override
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
		decimalFormatChanged = true;
	}

	@Override
	public synchronized SimpleDateFormat getDateFormat() {
		if (conversionMetadata != null) {
			return conversionMetadata.getDateFormat();
		}

		if (dateFormat == null || dateFormatChanged) {
			// This may not become static as the class is not thread-safe!
			dateFormat = new SimpleDateFormat();

			String mask;
			if (Const.isEmpty(conversionMask)) {
				mask = DEFAULT_DATE_FORMAT_MASK;
			} else {
				mask = conversionMask;
			}

			if (dateFormatLocale == null
					|| dateFormatLocale.equals(Locale.getDefault())) {
				dateFormat = new SimpleDateFormat(mask);
			} else {
				dateFormat = new SimpleDateFormat(mask, dateFormatLocale);
			}

			// Set the conversion leniency as well
			dateFormat.setLenient(dateFormatLenient);

			dateFormatChanged = false;
		}
		return dateFormat;
	}

	public String toString() {
		return name + " " + toStringMeta();
	}

	/**
	 * @param largeTextField
	 *            Set to true if this is to be a large text field (CLOB, TEXT)
	 *            with arbitrary length.
	 */
	public void setLargeTextField(boolean largeTextField) {
		this.largeTextField = largeTextField;
	}

	/** get set方法 */

	public ValueMetaInterface getConversionMetadata() {
		return conversionMetadata;
	}

	public void setConversionMetadata(ValueMetaInterface conversionMetadata) {
		this.conversionMetadata = conversionMetadata;
	}

	public String getDecimalSymbol() {
		return decimalSymbol;
	}

	public void setDecimalSymbol(String decimalSymbol) {
		this.decimalSymbol = decimalSymbol;
	}

	public Locale getDateFormatLocale() {
		return dateFormatLocale;
	}

	public void setDateFormatLocale(Locale dateFormatLocale) {
		this.dateFormatLocale = dateFormatLocale;
	}

	public boolean isDateFormatLenient() {
		return dateFormatLenient;
	}

	public void setDateFormatLenient(boolean dateFormatLenient) {
		this.dateFormatLenient = dateFormatLenient;
	}

	public String getComments() {
		return comments;
	}

	public void setDecimalFormat(DecimalFormat decimalFormat) {
		this.decimalFormat = decimalFormat;
	}

	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#isAutoIncrement()
	 */
	public boolean isOriginalAutoIncrement() {
		return originalAutoIncrement;
	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#setAutoIncrement(boolean)
	 */
	public void setOriginalAutoIncrement(boolean originalAutoIncrement) {
		this.originalAutoIncrement = originalAutoIncrement;
	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#getColumnType()
	 */
	public int getOriginalColumnType() {
		return originalColumnType;
	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#setColumnType(int)
	 */
	public void setOriginalColumnType(int originalColumnType) {
		this.originalColumnType = originalColumnType;
	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#getColumnTypeName()
	 */
	public String getOriginalColumnTypeName() {
		return originalColumnTypeName;
	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#setColumnTypeName(java.lang.String)
	 */
	public void setOriginalColumnTypeName(String originalColumnTypeName) {
		this.originalColumnTypeName = originalColumnTypeName;

	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#isNullable()
	 */
	public int isOriginalNullable() {
		return originalNullable;
	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#setNullable(int)
	 */
	public void setOriginalNullable(int originalNullable) {
		this.originalNullable = originalNullable;

	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#getPrecision()
	 */
	public int getOriginalPrecision() {
		return originalPrecision;
	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#setPrecision(int)
	 */
	public void setOriginalPrecision(int originalPrecision) {
		this.originalPrecision = originalPrecision;
	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#getScale()
	 */
	public int getOriginalScale() {
		return originalScale;
	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#setScale(int)
	 */
	public void setOriginalScale(int originalScale) {
		this.originalScale = originalScale;

	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#isSigned()
	 */
	public boolean isOriginalSigned() {
		return originalSigned;
	}

	/*
	 * Original JDBC RecordSetMetaData
	 * 
	 * @see java.sql.ResultSetMetaData#setOriginalSigned(boolean)
	 */
	public void setOriginalSigned(boolean originalSigned) {
		this.originalSigned = originalSigned;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}

	public String getSqlTypeDesc() {
		return sqlType;
	}

	@Override
	public void setSqlTypeDesc(String sqlType) {
		this.sqlType = sqlType;
	}

	/**
	 * Compare 2 values of the same data type
	 * 
	 * @param data1
	 *            the first value
	 * @param data2
	 *            the second value
	 * @return 0 if the values are equal, -1 if data1 is smaller than data2 and
	 *         +1 if it's larger.
	 * @throws RuntimeException
	 *             In case we get conversion errors
	 */
	public int compare(Object data1, Object data2) throws RuntimeException {
		boolean n1 = isNull(data1);
		boolean n2 = isNull(data2);

		// null is always smaller!
		if (n1 && !n2)
			return -1;
		if (!n1 && n2)
			return 1;
		if (n1 && n2)
			return 0;

		int cmp = 0;
		switch (getType()) {
		case TYPE_STRING: {
			// if (isStorageBinaryString() && identicalFormat &&
			// storageMetadata.isSingleByteEncoding()) return
			// compareBinaryStrings((byte[])data1, (byte[])data2); TODO
			String one = getString(data1);
			String two = getString(data2);
			cmp = Const.compareString(one, two, caseInsensitive);
		}
			break;
		case TYPE_BIT: {
			String one = getString(data1);
			String two = getString(data2);
			cmp = Const.compareString(one, two, caseInsensitive);
		}

		case TYPE_INTEGER: {
			// if (isStorageBinaryString() && identicalFormat) return
			// compareBinaryStrings((byte[])data1, (byte[])data2); TODO
			long compare = getInteger(data1).longValue()
					- getInteger(data2).longValue();
			if (compare < 0)
				cmp = -1;
			else if (compare > 0)
				cmp = 1;
			else
				cmp = 0;
		}
			break;

		case TYPE_NUMBER: {
			cmp = Double.compare(getNumber(data1).doubleValue(),
					getNumber(data2).doubleValue());
		}
			break;

		case TYPE_DATE: {
			long compare = getDate(data1).getTime() - getDate(data2).getTime();
			if (compare < 0)
				cmp = -1;
			else if (compare > 0)
				cmp = 1;
			else
				cmp = 0;
		}
			break;

		case TYPE_BIGNUMBER: {
			cmp = getBigNumber(data1).compareTo(getBigNumber(data2));
		}
			break;

		case TYPE_BOOLEAN: {
			if (getBoolean(data1).booleanValue() == getBoolean(data2)
					.booleanValue())
				cmp = 0; // true == true, false == false
			else if (getBoolean(data1).booleanValue()
					&& !getBoolean(data2).booleanValue())
				cmp = 1; // true > false
			else
				cmp = -1; // false < true
		}
			break;

		case TYPE_BINARY: {
			byte[] b1 = (byte[]) data1;
			byte[] b2 = (byte[]) data2;

			int length = b1.length < b2.length ? b1.length : b2.length;

			for (int i = 0; i < length; i++) {
				cmp = b1[i] - b2[i];
				if (cmp != 0) {
					cmp = cmp < 0 ? -1 : 1;
					break;
				}
			}

			cmp = b1.length - b2.length;
		}
			break;
		default:
			throw new RuntimeException(toString()
					+ " : Comparing values can not be done with data type : "
					+ getType());
		}

		if (isSortedDescending()) {
			return -cmp;
		} else {
			return cmp;
		}
	}

	/**
	 * Compare 2 values of the same data type
	 * 
	 * @param data1
	 *            the first value
	 * @param meta2
	 *            the second value's metadata
	 * @param data2
	 *            the second value
	 * @return 0 if the values are equal, -1 if data1 is smaller than data2 and
	 *         +1 if it's larger.
	 * @throws RuntimeException
	 *             In case we get conversion errors
	 */
	public int compare(Object data1, ValueMetaInterface meta2, Object data2)
			throws RuntimeException {
		if (meta2 == null) {
			throw new RuntimeException(
					toStringMeta()
							+ " : Second meta data (meta2) is null, please check one of the previous steps.");
		}

		try {
			// Before we can compare data1 to data2 we need to make sure they
			// have the same data type etc.
			//
			if (getType() == meta2.getType()) {
				if (getStorageType() == meta2.getStorageType())
					return compare(data1, data2);

				// Convert the storage type to compare the data.
				//
				switch (getStorageType()) {
				case STORAGE_TYPE_NORMAL:
					return compare(data1,
							meta2.convertToNormalStorageType(data2));

				case STORAGE_TYPE_BINARY_STRING:
					return compare(data1,
							meta2.convertToBinaryStringStorageType(data2));

				case STORAGE_TYPE_INDEXED:
					switch (meta2.getStorageType()) {
					case STORAGE_TYPE_INDEXED:
						return compare(data1, data2); // not accessible, just to
														// make sure.
					case STORAGE_TYPE_NORMAL:
						return -meta2.compare(data2,
								convertToNormalStorageType(data1));
					case STORAGE_TYPE_BINARY_STRING:
						return -meta2.compare(data2,
								convertToBinaryStringStorageType(data1));
					default:
						throw new RuntimeException(meta2.toStringMeta()
								+ " : Unknown storage type : "
								+ meta2.getStorageType());

					}
				default:
					throw new RuntimeException(toStringMeta()
							+ " : Unknown storage type : " + getStorageType());
				}
			}

			// If the data types are not the same, the first one is the
			// driver...
			// The second data type is converted to the first one.
			//
			return compare(data1, convertData(meta2, data2));
		} catch (Exception e) {
			throw new RuntimeException(toStringMeta()
					+ " : Unable to compare with value ["
					+ meta2.toStringMeta() + "]", e);
		}
	}

	@Override
	/**
	 * Convert an object to the data type specified in the conversion metadata
	 * @param data2 The data
	 * @return The data converted to the storage data type
	 * @throws RuntimeException in case there is a conversion error.
	 */
	public Object convertDataUsingConversionMetaData(Object data2)
			throws RuntimeException {
		if (conversionMetadata == null) {
			throw new RuntimeException(
					"API coding error: please specify the conversion metadata before attempting to convert value "
							+ name);
		}

		// Suppose we have an Integer 123, length 5
		// The string variation of this is " 00123"
		// To convert this back to an Integer we use the storage metadata
		// Specifically, in method convertStringToInteger() we consult the
		// storageMetaData to get the correct conversion mask
		// That way we're always sure that a conversion works both ways.
		//

		switch (conversionMetadata.getType()) {
		case TYPE_STRING:
			return getString(data2);
		case TYPE_BIT:
			return getBit(data2);
		case TYPE_INTEGER:
			return getInteger(data2);
		case TYPE_NUMBER:
			return getNumber(data2);
		case TYPE_DATE:
			return getDate(data2);
		case TYPE_BIGNUMBER:
			return getBigNumber(data2);
		case TYPE_BOOLEAN:
			return getBoolean(data2);
		case TYPE_BINARY:
			return getBinary(data2);
		default:
			throw new RuntimeException(toString()
					+ " : I can't convert the specified value to data type : "
					+ storageMetadata.getTypeDesc());
		}
	}

	@Override
	/**
	 * Convert the specified string to the data type specified in this object.
	 * @param pol the string to be converted
	 * @param convertMeta the metadata of the object (only string type) to be converted
	 * @param nullIf set the object to null if pos equals nullif (IgnoreCase)
	 * @param ifNull set the object to ifNull when pol is empty or null
	 * @param trim_type the trim type to be used (ValueMetaInterface.TRIM_TYPE_XXX)
	 * @return the object in the data type of this value metadata object
	 * @throws RuntimeException in case there is a data conversion error
	 */
	public Object convertDataFromString(String pol,
			ValueMetaInterface convertMeta, String nullIf, String ifNull,
			int trim_type) throws RuntimeException {

		// null handling and conversion of value to null
		//
		String null_value = nullIf;
		if (null_value == null) {
			switch (convertMeta.getType()) {
			case Value.VALUE_TYPE_BOOLEAN:
				null_value = Const.NULL_BOOLEAN;
				break;
			case Value.VALUE_TYPE_STRING:
				null_value = Const.NULL_STRING;
				break;
			case Value.VALUE_TYPE_BIGNUMBER:
				null_value = Const.NULL_BIGNUMBER;
				break;
			case Value.VALUE_TYPE_NUMBER:
				null_value = Const.NULL_NUMBER;
				break;
			case Value.VALUE_TYPE_INTEGER:
				null_value = Const.NULL_INTEGER;
				break;
			case Value.VALUE_TYPE_DATE:
				null_value = Const.NULL_DATE;
				break;
			case Value.VALUE_TYPE_BINARY:
				null_value = Const.NULL_BINARY;
				break;
			default:
				null_value = Const.NULL_NONE;
				break;
			}
		}

		// See if we need to convert a null value into a String
		// For example, we might want to convert null into "Empty".
		//
		if (!Const.isEmpty(ifNull)) {
			// Note that you can't pull the pad method up here as a nullComp
			// variable because you could get an NPE since you haven't checked
			// isEmpty(pol) yet!
			if (Const.isEmpty(pol)
					|| pol.equalsIgnoreCase(Const.rightPad(new StringBuffer(
							null_value), pol.length()))) {
				pol = ifNull;
			}
		}

		// See if the polled value is empty
		// In that case, we have a null value on our hands...
		// 修改记录值被转换为null的条件，
		// 1、字段值为空 2、ETL设置空字符串和Null值一样且记录值里都是空格
		if (Const.isEmpty(pol) || (Const.onlySpaces(pol))) {
			return null;
		}

		// Trimming
		switch (trim_type) {
		case ValueMetaInterface.TRIM_TYPE_LEFT: {
			StringBuffer strpol = new StringBuffer(pol);
			while (strpol.length() > 0 && strpol.charAt(0) == ' ')
				strpol.deleteCharAt(0);
			pol = strpol.toString();
		}
			break;
		case ValueMetaInterface.TRIM_TYPE_RIGHT: {
			StringBuffer strpol = new StringBuffer(pol);
			while (strpol.length() > 0
					&& strpol.charAt(strpol.length() - 1) == ' ')
				strpol.deleteCharAt(strpol.length() - 1);
			pol = strpol.toString();
		}
			break;
		case ValueMetaInterface.TRIM_TYPE_BOTH:
			StringBuffer strpol = new StringBuffer(pol);
			{
				while (strpol.length() > 0 && strpol.charAt(0) == ' ')
					strpol.deleteCharAt(0);
				while (strpol.length() > 0
						&& strpol.charAt(strpol.length() - 1) == ' ')
					strpol.deleteCharAt(strpol.length() - 1);
				pol = strpol.toString();
			}
			break;
		default:
			break;
		}

		// On with the regular program...
		// Simply call the ValueMeta routines to do the conversion
		// We need to do some effort here: copy all
		//
		Object value = convertData(convertMeta, pol);

		// “置空条件”处理，原来比较的是字符串，这种方法由于格式等问题，可能导致匹配不上。现在改成比较对象。
		if (!Const.isEmpty(null_value)) {
			Object nu = convertData(convertMeta, null_value);
			if (value.equals(nu))
				return null;

		}

		return value;
	}

	/**
	 * Convert the String description of a type to an integer type.
	 * 
	 * @param desc
	 *            The description of the type to convert
	 * @return The integer type of the given String.
	 *         (ValueMetaInterface.TYPE_...)
	 */
	public static final int getType(String desc) {
		for (int i = 1; i < typeCodes.length; i++) {
			if (typeCodes[i].equalsIgnoreCase(desc)) {
				return i;
			}
		}

		return TYPE_NONE;
	}

	/** get set */
	public String getSqlTypeName() {
		return sqlTypeName;
	}

	public void setSqlTypeName(String sqlTypeName) {
		this.sqlTypeName = sqlTypeName;
	}

	public boolean isSortedDescending() {
		return sortedDescending;
	}

	public void setSortedDescending(boolean sortedDescending) {
		this.sortedDescending = sortedDescending;
	}

	public boolean isLargeTextField() {
		return largeTextField;
	}

	public String getSqlType() {
		return sqlType;
	}

	public int getOriginalNullable() {
		return originalNullable;
	}

	/**
	 * Clones the data. Normally, we don't have to do anything here, but just
	 * for arguments and safety, we do a little extra work in case of binary
	 * blobs and Date objects. We should write a programmers manual later on to
	 * specify in all clarity that
	 * "we always overwrite/replace values in the Object[] data rows, we never modify them"
	 * .
	 * 
	 * @return a cloned data object if needed
	 */
	public Object cloneValueData(Object object) throws RuntimeException {
		if (object == null)
			return null;

		if (storageType == STORAGE_TYPE_NORMAL) {
			switch (getType()) {
			case ValueMeta.TYPE_STRING:
			case ValueMeta.TYPE_NUMBER:
			case ValueMeta.TYPE_INTEGER:
			case ValueMeta.TYPE_BOOLEAN:
			case ValueMeta.TYPE_BIT:
			case ValueMeta.TYPE_BIGNUMBER: // primitive data types: we can only
											// overwrite these, not change them
				return object;

			case ValueMeta.TYPE_DATE:
				return new Date(((Date) object).getTime()); // just to make
															// sure: very
															// inexpensive too.

			case ValueMeta.TYPE_BINARY:
				byte[] origin = (byte[]) object;
				byte[] target = new byte[origin.length];
				System.arraycopy(origin, 0, target, 0, origin.length);
				return target;

			case ValueMeta.TYPE_SERIALIZABLE:
				// Let's not create a copy but simply return the same value.
				//
				return object;

			default:
				throw new RuntimeException(toString()
						+ ": unable to make copy of value type: " + getType());
			}
		} else {

			return object;

		}
	}

	public boolean requiresRealClone() {
		return type == TYPE_BINARY || type == TYPE_SERIALIZABLE;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length, int precision) {
		this.length = length;
		this.precision = precision;
	}

	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public final static String getTrimTypeCode(int i) {
		if (i < 0 || i >= trimTypeCode.length)
			return trimTypeCode[0];
		return trimTypeCode[i];
	}

	public final static String getTrimTypeDesc(int i) {
		if (i < 0 || i >= trimTypeDesc.length)
			return trimTypeDesc[0];
		return trimTypeDesc[i];
	}

	public final static int getTrimTypeByCode(String tt) {
		if (tt == null)
			return 0;

		for (int i = 0; i < trimTypeCode.length; i++) {
			if (trimTypeCode[i].equalsIgnoreCase(tt))
				return i;
		}
		return 0;
	}

	@Override
	public byte[] getBinaryString(Object object) throws RuntimeException {
		// If the input is a binary string, we should return the exact same
		// binary object IF
		// and only IF the formatting options for the storage metadata and this
		// object are the same.
		//
		if (isStorageBinaryString() && identicalFormat) {
			return (byte[]) object; // shortcut it directly for better
									// performance.
		}

		try {
			if (object == null) // NULL
			{
				return null;
			}

			switch (type) {
			case TYPE_STRING:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertStringToBinaryString((String) object);
				case STORAGE_TYPE_BINARY_STRING:
					return convertStringToBinaryString((String) convertBinaryStringToNativeType((byte[]) object));
				case STORAGE_TYPE_INDEXED:
					return convertStringToBinaryString((String) index[((Integer) object)
							.intValue()]);
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}

			case TYPE_DATE:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertStringToBinaryString(convertDateToString((Date) object));
				case STORAGE_TYPE_BINARY_STRING:
					return convertStringToBinaryString(convertDateToString((Date) convertBinaryStringToNativeType((byte[]) object)));
				case STORAGE_TYPE_INDEXED:
					return convertStringToBinaryString(convertDateToString((Date) index[((Integer) object)
							.intValue()]));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}

			case TYPE_NUMBER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertStringToBinaryString(convertNumberToString((Double) object));
				case STORAGE_TYPE_BINARY_STRING:
					return convertStringToBinaryString(convertNumberToString((Double) convertBinaryStringToNativeType((byte[]) object)));
				case STORAGE_TYPE_INDEXED:
					return convertStringToBinaryString(convertNumberToString((Double) index[((Integer) object)
							.intValue()]));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}

			case TYPE_INTEGER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertStringToBinaryString(convertIntegerToString((Long) object));
				case STORAGE_TYPE_BINARY_STRING:
					return convertStringToBinaryString(convertIntegerToString((Long) convertBinaryStringToNativeType((byte[]) object)));
				case STORAGE_TYPE_INDEXED:
					return convertStringToBinaryString(convertIntegerToString((Long) index[((Integer) object)
							.intValue()]));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}

			case TYPE_BIGNUMBER:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertStringToBinaryString(convertBigNumberToString((BigDecimal) object));
				case STORAGE_TYPE_BINARY_STRING:
					return convertStringToBinaryString(convertBigNumberToString((BigDecimal) convertBinaryStringToNativeType((byte[]) object)));
				case STORAGE_TYPE_INDEXED:
					return convertStringToBinaryString(convertBigNumberToString((BigDecimal) index[((Integer) object)
							.intValue()]));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}

			case TYPE_BOOLEAN:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertStringToBinaryString(convertBooleanToString((Boolean) object));
				case STORAGE_TYPE_BINARY_STRING:
					return convertStringToBinaryString(convertBooleanToString((Boolean) convertBinaryStringToNativeType((byte[]) object)));
				case STORAGE_TYPE_INDEXED:
					return convertStringToBinaryString(convertBooleanToString((Boolean) index[((Integer) object)
							.intValue()]));
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}
			case TYPE_BIT:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertStringToBinaryString(object.toString());

				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}

			case TYPE_BINARY:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return (byte[]) object;
				case STORAGE_TYPE_BINARY_STRING:
					return (byte[]) object;
				case STORAGE_TYPE_INDEXED:
					return (byte[]) index[((Integer) object).intValue()];
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}

			case TYPE_SERIALIZABLE:
				switch (storageType) {
				case STORAGE_TYPE_NORMAL:
					return convertStringToBinaryString(object.toString());
				case STORAGE_TYPE_BINARY_STRING:
					return (byte[]) object;
				case STORAGE_TYPE_INDEXED:
					return convertStringToBinaryString(index[((Integer) object)
							.intValue()].toString());
				default:
					throw new RuntimeException(toString()
							+ " : Unknown storage type " + storageType
							+ " specified.");
				}

			default:
				throw new RuntimeException(toString() + " : Unknown type "
						+ type + " specified.");
			}
		} catch (ClassCastException e) {
			throw new RuntimeException(toString()
					+ " : There was a data type error: the data type of "
					+ object.getClass().getName() + " object [" + object
					+ "] does not correspond to value meta [" + toStringMeta()
					+ "]");
		}
	}

	
	/**  
	* @return columnIndexExcel  
	*/
	
	public int getColumnIndexExcel() {
		return columnIndexExcel;
	}

	
	/**  
	 * @param paramtheparamthe{bare_field_name} to set  
	 */
	
	public void setColumnIndexExcel(int columnIndexExcel) {
		this.columnIndexExcel = columnIndexExcel;
	}
	

}