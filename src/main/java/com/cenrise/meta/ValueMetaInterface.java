/**
 * 
 */
package com.cenrise.meta;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public interface ValueMetaInterface extends Cloneable {

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

	public static final int TYPE_TIETLFUNC = 10;// 增加函数标示符

	public static final String[] typeCodes = new String[] { "-", "Number",
			"String", "Date", "Boolean", "Integer", "BigNumber",
			"Serializable", "Binary", "Bit" };

	/** The storage type is the same as the indicated value type */
	public static final int STORAGE_TYPE_NORMAL = 0;

	/**
	 * The storage type is binary: read from text but not yet converted to the
	 * requested target data type, for lazy conversions.
	 */
	public static final int STORAGE_TYPE_BINARY_STRING = 1;

	/**
	 * The storage type is indexed. This means that the value is a simple
	 * integer index referencing the values in getIndex()
	 */
	public static final int STORAGE_TYPE_INDEXED = 2;

	public static final String[] storageTypeCodes = new String[] { "normal",
			"binary-string", "indexed", };

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

	/** Default integer length for hardcoded metadata integers */
	public static final int DEFAULT_INTEGER_LENGTH = 10;

	public abstract ValueMeta clone();

	public abstract String toStringMeta();

	public abstract String getStorageTypeDesc();

	public abstract String getTypeDesc();

	public abstract boolean isStorageNormal();

	public abstract boolean isStorageBinaryString();

	public abstract void setStorageType(int storageType);

	public abstract int getStorageType();

	public abstract void setPrecision(int precision);

	public abstract int getPrecision();

	public abstract void setLength(int length);

	public abstract int getLength();

	public void setLength(int length, int precision);

	public String getOrigin();

	public void setOrigin(String origin);

	public abstract void setSqlType(String sqlType);

	// public abstract int getSqlType();

	// public abstract void setSqlTypeName(String sqlTypeName);

	// public abstract String getSqlTypeName();

	public abstract void setType(int type);

	public int getType();

	public abstract void setName(String name);

	public abstract String getName();

	public abstract boolean isNull(Object data);

	public abstract Double getNumber(Object object);

	public abstract Boolean getBoolean(Object object);

	public abstract String getBit(Object object);

	public abstract Long getInteger(Object object);

	public abstract boolean isNull(Object object, boolean b);

	public abstract String getString(Object object);

	public abstract BigDecimal getBigNumber(Object object);

	public abstract byte[] getBinary(Object object);

	public abstract Object getDate(Object data2);

	public abstract Object getCompatibleString(Object data2);

	public void setConversionMask(String conversionMask);

	public void setComments(String comments);

	public String getConversionMask();

	public String getDecimalSymbol();

	public void setDecimalSymbol(String decimalSymbol);

	public String getStringEncoding();

	public void setStringEncoding(String stringEncoding);

	public String getGroupingSymbol();

	public void setGroupingSymbol(String groupingSymbol);

	public String getCurrencySymbol();

	public void setCurrencySymbol(String currencySymbol);

	public SimpleDateFormat getDateFormat();

	public DecimalFormat getDecimalFormat();

	public DecimalFormat getDecimalFormat(boolean useBigDecimal);

	/**
	 * @param largeTextField
	 *            Set to true if this is to be a large text field (CLOB, TEXT)
	 *            with arbitrary length.
	 */
	public void setLargeTextField(boolean largeTextField);

	/**
	 * store original JDBC RecordSetMetaData for later use
	 * 
	 * @see java.sql.ResultSetMetaData
	 */

	public int getOriginalColumnType();

	public void setOriginalColumnType(int originalColumnType);

	public String getOriginalColumnTypeName();

	public void setOriginalColumnTypeName(String originalColumnTypeName);

	public int getOriginalPrecision();

	public void setOriginalPrecision(int originalPrecision);

	public int getOriginalScale();

	public void setOriginalScale(int originalScale);

	public boolean isOriginalAutoIncrement();

	public void setOriginalAutoIncrement(boolean originalAutoIncrement);

	public int isOriginalNullable();

	public void setOriginalNullable(int originalNullable);

	public boolean isOriginalSigned();

	public void setOriginalSigned(boolean originalSigned);

	public String getSqlTypeDesc();

	public void setSqlTypeDesc(String sqlType);

	/**
	 * @param storageMetadata
	 *            the storage Meta data that is needed for internal conversion
	 *            from BinaryString or String to the specified type. This
	 *            storage Meta data object survives cloning and should travel
	 *            through the transformation unchanged as long as the data type
	 *            remains the same.
	 */
	public void setStorageMetadata(ValueMetaInterface storageMetadata);

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
	public int compare(Object data1, Object data2) throws RuntimeException;

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
			throws RuntimeException;

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
			throws RuntimeException;

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
			throws RuntimeException;

	/**
	 * Convert an object to the data type specified in the conversion metadata
	 * 
	 * @param data
	 *            The data
	 * @return The data converted to the conversion data type
	 * @throws RuntimeException
	 *             in case there is a conversion error.
	 */
	public Object convertDataUsingConversionMetaData(Object data)
			throws RuntimeException;

	/**
	 * Convert the specified string to the data type specified in this object.
	 * 
	 * @param pol
	 *            the string to be converted
	 * @param convertMeta
	 *            the metadata of the object (only string type) to be converted
	 * @param nullif
	 *            set the object to null if pos equals nullif (IgnoreCase)
	 * @param ifNull
	 *            set the object to ifNull when pol is empty or null
	 * @param trim_type
	 *            the trim type to be used (ValueMetaInterface.TRIM_TYPE_XXX)
	 * @return the object in the data type of this value metadata object
	 * @throws RuntimeException
	 *             in case there is a data conversion error
	 */
	public Object convertDataFromString(String pol,
			ValueMetaInterface convertMeta, String nullif, String ifNull,
			int trim_type) throws RuntimeException;

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
			throws RuntimeException;

	/**
	 * Convert the given binary data to the actual data type.<br>
	 * - byte[] --> Long (Integer)<br>
	 * - byte[] --> Double (Number)<br>
	 * - byte[] --> BigDecimal (BigNumber)<br>
	 * - byte[] --> Date (Date)<br>
	 * - byte[] --> Boolean (Boolean)<br>
	 * - byte[] --> byte[] (Binary)<br>
	 * <br>
	 * 
	 * @param binary
	 *            the binary data read from file or database
	 * @return the native data type after conversion
	 * @throws RuntimeException
	 *             in case there is a data conversion error
	 */
	public Object convertBinaryStringToNativeType(byte[] binary)
			throws RuntimeException;

	/**
	 * Convert a normal storage type to a binary string object. (for comparison
	 * reasons)
	 * 
	 * @param object
	 *            The object expressed in a normal storage type
	 * @return a binary string
	 * @throws RuntimeException
	 *             in case there is a data conversion error
	 */
	public Object convertNormalStorageTypeToBinaryString(Object object)
			throws RuntimeException;

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
			throws RuntimeException;

	public boolean requiresRealClone();

	public Object cloneValueData(Object object) throws RuntimeException;

	/**
	 * Checks wheter or not the value is a String.
	 * 
	 * @return true if the value is a String.
	 */
	public boolean isString();

	public int getTrimType();

	public void setTrimType(int trimType);

	/**
	 * convert the supplied data to a binary string representation (for writing
	 * text)
	 */
	public byte[] getBinaryString(Object object) throws RuntimeException;

	public String getComments();

}
