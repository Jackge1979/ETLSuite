package com.cenrise.xml;

/**
 * Implementing classes of this interface know how to express themselves using
 * XML They also can construct themselves using XML.
 * 
 */
public interface XMLInterface {
	/**
	 * Describes the Object implementing this interface as XML
	 * 
	 * @return the XML string for this object
	 * @throws DgfException
	 *             in case there is an encoding problem.
	 */
	public String getXML() throws RuntimeException;

}
