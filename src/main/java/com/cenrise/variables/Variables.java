package com.cenrise.variables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cenrise.meta.ValueMeta;
import com.cenrise.util.Const;

/**
 * This class is an implementation of VariableSpace
 * 
 */
public class Variables implements VariableSpace {
	private Map<String, String> properties;
	private VariableSpace parent;
	private Map<String, String> injection;
	private boolean initialized;

	public static final String UNIX_OPEN = "${";

	public static final String UNIX_CLOSE = "}";

	public static final String WINDOWS_OPEN = "%%";

	public static final String WINDOWS_CLOSE = "%%";

	public static final String HEX_OPEN = "$[";
	public static final String HEX_CLOSE = "]";

	public static final String CRLF = "\r\n"; //$NON-NLS-1$

	public Variables() {
		properties = new ConcurrentHashMap<String, String>();
		parent = null;
		injection = null;
		initialized = false;
	}

	public void copyVariablesFrom(VariableSpace space) {
		if (space != null && this != space) {
			// If space is not null and this variable is not already
			// the same object as the argument.
			String[] variableNames = space.listVariables();
			for (int idx = 0; idx < variableNames.length; idx++) {
				properties.put(variableNames[idx],
						space.getVariable(variableNames[idx]));
			}
		}
	}

	public VariableSpace getParentVariableSpace() {
		return parent;
	}

	public void setParentVariableSpace(VariableSpace parent) {
		this.parent = parent;
	}

	public String getVariable(String variableName, String defaultValue) {
		String var = properties.get(variableName);
		if (var == null)
			return defaultValue;
		return var;
	}

	public String getVariable(String variableName) {
		return properties.get(variableName);
	}

	public boolean getBooleanValueOfVariable(String variableName,
			boolean defaultValue) {
		if (!Const.isEmpty(variableName)) {
			String value = environmentSubstitute(variableName);
			if (!Const.isEmpty(value)) {
				return ValueMeta.convertStringToBoolean(value);
			}
		}
		return defaultValue;
	}

	public void initializeVariablesFrom(VariableSpace parent) {
		this.parent = parent;

		// Add all the system properties...
		for (Object key : System.getProperties().keySet()) {
			properties.put((String) key,
					System.getProperties().getProperty((String) key));
		}

		if (parent != null) {
			copyVariablesFrom(parent);
		}
		if (injection != null) {
			properties.putAll(injection);
			injection = null;
		}
		initialized = true;
	}

	public String[] listVariables() {
		List<String> list = new ArrayList<String>();
		for (String name : properties.keySet()) {
			list.add(name);
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	public void setVariable(String variableName, String variableValue) {
		if (variableValue != null) {
			properties.put(variableName, variableValue);
		} else {
			properties.remove(variableName);
		}
	}

	public String environmentSubstitute(String aString) {
		if (aString == null || aString.length() == 0)
			return aString;

		return environmentSubstitute(aString, properties);
	}

	public String[] environmentSubstitute(String string[]) {
		String retval[] = new String[string.length];
		for (int i = 0; i < string.length; i++) {
			retval[i] = environmentSubstitute(string[i]);
		}
		return retval;
	}

	public void shareVariablesWith(VariableSpace space) {
		// not implemented in here... done by pointing to the same VariableSpace
		// implementation
	}

	public void injectVariables(Map<String, String> prop) {
		if (initialized) {
			// variables are already initialized
			if (prop != null) {
				properties.putAll(prop);
				injection = null;
			}
		} else {
			// We have our own personal copy, so changes afterwards
			// to the input properties don't affect us.
			injection = new Hashtable<String, String>();
			injection.putAll(prop);
		}
	}

	/**
	 * Get a default variable space as a placeholder. Everytime you will get a
	 * new instance.
	 * 
	 * @return a default variable space.
	 */
	synchronized public static VariableSpace getADefaultVariableSpace() {
		VariableSpace space = new Variables();

		space.initializeVariablesFrom(null);

		return space;
	}

	/**
	 * Substitutes variables in <code>aString</code> with the environment values
	 * in the system properties
	 * 
	 * @param aString
	 *            the string on which to apply the substitution.
	 * @param systemProperties
	 *            the system properties to use
	 * @return the string with the substitution applied.
	 */
	public synchronized static final String environmentSubstitute(
			String aString, Map<String, String> systemProperties) {
		Map<String, String> sysMap = new ConcurrentHashMap<String, String>();
		synchronized (sysMap) {
			sysMap.putAll(Collections.synchronizedMap(systemProperties));

			aString = substituteWindows(aString, sysMap);
			aString = substituteUnix(aString, sysMap);
			aString = substituteHex(aString);
			return aString;
		}
	}

	/**
	 * Substitutes variables in <code>aString</code>. Variables are of the form
	 * "%%<variable name>%%", following the windows convention. The values are
	 * retrieved from the given map.
	 * 
	 * @param aString
	 *            the string on which to apply the substitution.
	 * @param variables
	 *            a map containg the variable values. The keys are the variable
	 *            names, the values are the variable values.
	 * @return the string with the substitution applied.
	 */
	public static String substituteWindows(String aString,
			Map<String, String> variables) {
		return substitute(aString, variables, WINDOWS_OPEN, WINDOWS_CLOSE);
	}

	/**
	 * Substitutes variables in <code>aString</code>. Variables are of the form
	 * "${<variable name>}", following the usin convention. The values are
	 * retrieved from the given map.
	 * 
	 * @param aString
	 *            the string on which to apply the substitution.
	 * @param variables
	 *            a map containg the variable values. The keys are the variable
	 *            names, the values are the variable values.
	 * @return the string with the substitution applied.
	 */
	public static String substituteUnix(String aString,
			Map<String, String> variables) {
		return substitute(aString, variables, UNIX_OPEN, UNIX_CLOSE);
	}

	/**
	 * Substitutes hex values in <code>aString</code> and convert them to
	 * operating system char equivalents in the return string. Format is $[01]
	 * or $[6F,FF,00,1F] Example:
	 * "This is a hex encoded six digits number 123456 in this string: $[31,32,33,34,35,36]"
	 * 
	 * @param aString
	 *            the string on which to apply the substitution.
	 * @return the string with the substitution applied.
	 */
	public static String substituteHex(String aString) {
		if (aString == null)
			return null;

		StringBuffer buffer = new StringBuffer();

		String rest = aString;

		// search for opening string
		int i = rest.indexOf(HEX_OPEN);
		while (i > -1) {
			int j = rest.indexOf(HEX_CLOSE, i + HEX_OPEN.length());
			// search for closing string
			if (j > -1) {
				buffer.append(rest.substring(0, i));
				String hexString = rest.substring(i + HEX_OPEN.length(), j);
				String[] hexStringArray = hexString.split(",");
				int hexInt;
				byte[] hexByte = new byte[1];
				for (int pos = 0; pos < hexStringArray.length; pos++) {
					try {
						hexInt = Integer.parseInt(hexStringArray[pos], 16);
					} catch (NumberFormatException e) {
						hexInt = 0; // in case we get an invalid hex value,
									// ignore: we can not log here
					}
					hexByte[0] = (byte) hexInt;
					buffer.append(new String(hexByte));
				}
				rest = rest.substring(j + HEX_CLOSE.length());
			} else {
				// no closing tag found; end the search
				buffer.append(rest);
				rest = "";
			}
			// keep searching
			i = rest.indexOf(HEX_OPEN);
		}
		buffer.append(rest);
		return buffer.toString();
	}

	/**
	 * Substitutes variables in <code>aString</code>. Variable names are
	 * delimited by open and close strings. The values are retrieved from the
	 * given map.
	 * 
	 * @param aString
	 *            the string on which to apply the substitution.
	 * @param variablesValues
	 *            a map containing the variable values. The keys are the
	 *            variable names, the values are the variable values.
	 * @param open
	 *            the open delimiter for variables.
	 * @param close
	 *            the close delimiter for variables.
	 * @return the string with the substitution applied.
	 */
	public static String substitute(String aString,
			Map<String, String> variablesValues, String open, String close) {
		return substitute(aString, variablesValues, open, close, 0);
	}

	/**
	 * Substitutes variables in <code>aString</code>. Variable names are
	 * delimited by open and close strings. The values are retrieved from the
	 * given map.
	 * 
	 * @param aString
	 *            the string on which to apply the substitution.
	 * @param variablesValues
	 *            a map containg the variable values. The keys are the variable
	 *            names, the values are the variable values.
	 * @param open
	 *            the open delimiter for variables.
	 * @param close
	 *            the close delimiter for variables.
	 * @param recursion
	 *            the number of recursion (internal counter to avoid endless
	 *            loops)
	 * @return the string with the substitution applied.
	 */
	public static String substitute(String aString,
			Map<String, String> variablesValues, String open, String close,
			int recursion) {
		if (aString == null)
			return null;

		StringBuffer buffer = new StringBuffer();

		String rest = aString;

		// search for opening string
		int i = rest.indexOf(open);
		while (i > -1) {
			int j = rest.indexOf(close, i + open.length());
			// search for closing string
			if (j > -1) {
				String varName = rest.substring(i + open.length(), j);
				Object value = variablesValues.get(varName);
				if (value == null) {
					value = open + varName + close;
				}
				// gengjie 2012-9-3 不让其循环调用
				// else
				// {
				// // check for another variable inside this value
				// int another = ((String) value).indexOf(open); // check
				// // here
				// // first for
				// // speed
				// if (another > -1)
				// {
				// if (recursion > 50) // for safety: avoid recursive
				// // endless loops with stack overflow
				// {
				// throw new
				// RuntimeException("Endless loop detected for substitution of variable: "
				// + (String) value);
				// }
				// value = substitute((String) value, variablesValues, open,
				// close, ++recursion);
				// }
				// }
				buffer.append(rest.substring(0, i));
				buffer.append(value);
				rest = rest.substring(j + close.length());
			} else {
				// no closing tag found; end the search
				buffer.append(rest);
				rest = "";
			}
			// keep searching
			i = rest.indexOf(open);
		}
		buffer.append(rest);
		return buffer.toString();
	}

}