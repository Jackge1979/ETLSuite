package com.cenrise.exception;

import com.cenrise.util.Const;

/**
 * This is an exception thrown by file handling (I/O) when an End Of File marker
 * has been reached.
 * 
 */
public class BaseFileException extends BaseException {
	public static final long serialVersionUID = 0x8D8EA0264F7A1C13L;

	/**
	 * Constructs a new throwable with null as its detail message.
	 */
	public BaseFileException() {
		super();
	}

	/**
	 * Constructs a new throwable with the specified detail message.
	 * 
	 * @param message
	 *            - the detail message. The detail message is saved for later
	 *            retrieval by the getMessage() method.
	 */
	public BaseFileException(String message) {
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
	public BaseFileException(Throwable cause) {
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
	public BaseFileException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * get the messages back to it's origin cause.
	 */
	public String getMessage() {
		String retval = Const.CR;
		retval += super.getMessage() + Const.CR;

		Throwable cause = getCause();
		if (cause != null) {
			String message = cause.getMessage();
			if (message != null) {
				retval += message + Const.CR;
			} else {
				// Add with stack trace elements of cause...
				StackTraceElement ste[] = cause.getStackTrace();
				for (int i = ste.length - 1; i >= 0; i--) {
					retval += "	at " + ste[i].getClassName() + "."
							+ ste[i].getMethodName() + " ("
							+ ste[i].getFileName() + ":"
							+ ste[i].getLineNumber() + ")" + Const.CR;
				}
			}
		}

		return retval;
	}

}
