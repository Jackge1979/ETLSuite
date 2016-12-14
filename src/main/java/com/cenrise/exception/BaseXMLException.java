package com.cenrise.exception;

/**
 * This exception is throws when and error is found in an XML snippet.
 * 
 */
public class BaseXMLException extends BaseException {
	public static final long serialVersionUID = 0x8D8EA0264F7A1C19L;

	/**
	 * Constructs a new throwable with null as its detail message.
	 */
	public BaseXMLException() {
		super();
	}

	/**
	 * Constructs a new throwable with the specified detail message.
	 * 
	 * @param message
	 *            - the detail message. The detail message is saved for later
	 *            retrieval by the getMessage() method.
	 */
	public BaseXMLException(String message) {
		super(message);
	}

	/**
	 * Constructs a new throwable with the specified cause and a detail message
	 * of (cause==null ? null : cause.toString()) (which typically contains the
	 * class and detail message of cause).
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            getCause() method). (A null value is permitted, and indicates
	 *            that the cause is nonexistent or unknown.)
	 */
	public BaseXMLException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new throwable with the specified detail message and cause.
	 * 
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            getMessage() method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            getCause() method). (A null value is permitted, and indicates
	 *            that the cause is nonexistent or unknown.)
	 */
	public BaseXMLException(String message, Throwable cause) {
		super(message, cause);
	}

}
