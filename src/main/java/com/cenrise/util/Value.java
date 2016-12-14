package com.cenrise.util;

public class Value {

	/**
	 * Value type indicating that the value has no type set.
	 */
	public static final int VALUE_TYPE_NONE = 0;

	/**
	 * Value type indicating that the value contains a floating point double
	 * precision number.
	 */
	public static final int VALUE_TYPE_NUMBER = 1;

	/**
	 * Value type indicating that the value contains a text String.
	 */
	public static final int VALUE_TYPE_STRING = 2;

	/**
	 * Value type indicating that the value contains a Date.
	 */
	public static final int VALUE_TYPE_DATE = 3;

	/**
	 * Value type indicating that the value contains a boolean.
	 */
	public static final int VALUE_TYPE_BOOLEAN = 4;

	/**
	 * Value type indicating that the value contains a long integer.
	 */
	public static final int VALUE_TYPE_INTEGER = 5;

	/**
	 * Value type indicating that the value contains a floating point precision
	 * number with arbitrary precision.
	 */
	public static final int VALUE_TYPE_BIGNUMBER = 6;

	/**
	 * Value type indicating that the value contains an Object.
	 */
	public static final int VALUE_TYPE_SERIALIZABLE = 7;

	/**
	 * Value type indicating that the value contains binary data: BLOB, CLOB,
	 * ...
	 */
	public static final int VALUE_TYPE_BINARY = 8;

	/**
	 * The descriptions of the value types.
	 */
	private static final String valueTypeCode[] = {
			"-", // $NON-NLS-1$
			"Number", "String", "Date", "Boolean", "Integer", "BigNumber",
			"Serializable", "Binary" // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
										// $NON-NLS-4$ $NON-NLS-5$ $NON-NLS-6$
										// $NON-NLS-7$
	};

}
