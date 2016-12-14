package com.cenrise.meta;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface RowMetaInterface extends Cloneable {

	public ValueMetaInterface getValueMeta(int i);

	/**
	 * Add a metadata value, extends the array if needed. If a value with the
	 * same name already exists, it gets renamed.
	 * 
	 * @param meta
	 *            The metadata value o add
	 */
	public void addValueMeta(ValueMetaInterface meta);

	/**
	 * Compare 2 rows with each other using certain values in the rows and also
	 * considering the specified ascending clauses of the value metadata.
	 * 
	 * @param rowData1
	 *            The first row of data
	 * @param rowData2
	 *            The second row of data
	 * @param fieldnrs
	 *            the fields to compare on (in that order)
	 * @return 0 if the rows are considered equal, -1 is data1 is smaller, 1 if
	 *         data2 is smaller.
	 * @throws RuntimeException
	 */
	public int compare(Object[] rowData1, Object[] rowData2, int fieldnrs[])
			throws RuntimeException;

	/**
	 * Compare 2 rows with each other for equality using certain values in the
	 * rows and also considering the case sensitivity flag.
	 * 
	 * @param rowData1
	 *            The first row of data
	 * @param rowData2
	 *            The second row of data
	 * @param fieldnrs
	 *            the fields to compare on (in that order)
	 * @return true if the rows are considered equal, false if they are not.
	 * @throws RuntimeException
	 */
	public boolean equals(Object[] rowData1, Object[] rowData2, int fieldnrs[])
			throws RuntimeException;

	/**
	 * Compare 2 rows with each other using certain values in the rows and also
	 * considering the specified ascending clauses of the value metadata.
	 * 
	 * @param rowData1
	 *            The first row of data
	 * @param rowData2
	 *            The second row of data
	 * @param fieldnrs1
	 *            The indexes of the values to compare in the first row
	 * @param fieldnrs2
	 *            The indexes of the values to compare with in the second row
	 * @return 0 if the rows are considered equal, -1 is data1 is smaller, 1 if
	 *         data2 is smaller.
	 * @throws RuntimeException
	 */
	public int compare(Object[] rowData1, Object[] rowData2, int fieldnrs1[],
			int fieldnrs2[]) throws RuntimeException;

	/**
	 * Compare 2 rows with each other using certain values in the rows and also
	 * considering the specified ascending clauses of the value metadata.
	 * 
	 * @param rowData1
	 *            The first row of data
	 * @param rowMeta2
	 *            the metadat of the second row of data
	 * @param rowData2
	 *            The second row of data
	 * @param fieldnrs1
	 *            The indexes of the values to compare in the first row
	 * @param fieldnrs2
	 *            The indexes of the values to compare with in the second row
	 * @return 0 if the rows are considered equal, -1 is data1 is smaller, 1 if
	 *         data2 is smaller.
	 * @throws RuntimeException
	 */
	public int compare(Object[] rowData1, RowMetaInterface rowMeta2,
			Object[] rowData2, int fieldnrs1[], int fieldnrs2[])
			throws RuntimeException;

	/**
	 * Compare 2 rows with each other using all values in the rows and also
	 * considering the specified ascending clauses of the value metadata.
	 * 
	 * @param rowData1
	 *            The first row of data
	 * @param rowData2
	 *            The second row of data
	 * @return 0 if the rows are considered equal, -1 is data1 is smaller, 1 if
	 *         data2 is smaller.
	 * @throws RuntimeException
	 */
	public int compare(Object[] rowData1, Object[] rowData2)
			throws RuntimeException;

	/**
	 * @return the list of value Metadata
	 */
	public List<ValueMetaInterface> getValueMetaList();

	/**
	 * @param valueMetaList
	 *            the list of valueMeta to set
	 */
	public void setValueMetaList(List<ValueMetaInterface> valueMetaList);

	/**
	 * @return the size of the metadata row
	 */
	public int size();

	/**
	 * @return true if there are no elements in the row metadata
	 */
	public boolean isEmpty();

	/**
	 * @return a cloned Object[] object.
	 * @throws RuntimeException
	 *             in case something is not quite right with the expected data
	 */
	public Object[] cloneRow(Object[] objects, Object[] cloneTo)
			throws RuntimeException;

	/**
	 * @return a cloned Object[] object.
	 * @throws RuntimeException
	 *             in case something is not quite right with the expected data
	 */
	public Object[] cloneRow(Object[] objects) throws RuntimeException;

	/**
	 * @return a copy of this RowMetaInterface object
	 */
	public RowMetaInterface clone();

	/**
	 * Searches the index of a value meta with a given name
	 * 
	 * @param valueName
	 *            the name of the value metadata to look for
	 * @return the index or -1 in case we didn't find the value
	 */
	public int indexOfValue(String valueName);

	/**
	 * Get the string representation of the data in a row of data
	 * 
	 * @param row
	 *            the row of data to convert to string
	 * @return the row of data in string form
	 * @throws KettleValueException
	 *             in case of a conversion error
	 */
	public String getString(Object[] row) throws RuntimeException;

	/**
	 * Get a String value from a row of data. Convert data if this needed.
	 * 
	 * @param rowRow
	 *            the row of data
	 * @param index
	 *            the index
	 * @return The string found on that position in the row
	 * @throws RuntimeException
	 *             in case there was a problem converting the data.
	 */
	public String getString(Object[] dataRow, int index)
			throws RuntimeException;

	/**
	 * Get an Integer value from a row of data. Convert data if this needed.
	 * 
	 * @param rowRow
	 *            the row of data
	 * @param index
	 *            the index
	 * @return The integer found on that position in the row
	 * @throws RuntimeException
	 *             in case there was a problem converting the data.
	 */
	public Long getInteger(Object[] dataRow, int index) throws RuntimeException;

	/**
	 * Get a Number value from a row of data. Convert data if this needed.
	 * 
	 * @param rowRow
	 *            the row of data
	 * @param index
	 *            the index
	 * @return The number found on that position in the row
	 * @throws RuntimeException
	 *             in case there was a problem converting the data.
	 */
	public Double getNumber(Object[] dataRow, int index)
			throws RuntimeException;

	/**
	 * Get a Date value from a row of data. Convert data if this needed.
	 * 
	 * @param rowRow
	 *            the row of data
	 * @param index
	 *            the index
	 * @return The date found on that position in the row
	 * @throws RuntimeException
	 *             in case there was a problem converting the data.
	 */
	public Date getDate(Object[] dataRow, int index) throws RuntimeException;

	/**
	 * Get a BigNumber value from a row of data. Convert data if this needed.
	 * 
	 * @param rowRow
	 *            the row of data
	 * @param index
	 *            the index
	 * @return The bignumber found on that position in the row
	 * @throws RuntimeException
	 *             in case there was a problem converting the data.
	 */
	public BigDecimal getBigNumber(Object[] dataRow, int index)
			throws RuntimeException;

	/**
	 * Get a Boolean value from a row of data. Convert data if this needed.
	 * 
	 * @param rowRow
	 *            the row of data
	 * @param index
	 *            the index
	 * @return The boolean found on that position in the row
	 * @throws RuntimeException
	 *             in case there was a problem converting the data.
	 */
	public Boolean getBoolean(Object[] dataRow, int index)
			throws RuntimeException;

	/**
	 * Get a Binary value from a row of data. Convert data if this needed.
	 * 
	 * @param rowRow
	 *            the row of data
	 * @param index
	 *            the index
	 * @return The binary found on that position in the row
	 * @throws RuntimeException
	 *             in case there was a problem converting the data.
	 */
	public byte[] getBinary(Object[] dataRow, int index)
			throws RuntimeException;

	/**
	 * Searches for a value with a certain name in the value meta list
	 * 
	 * @param valueName
	 *            The value name to search for
	 * @return The value metadata or null if nothing was found
	 */
	public ValueMetaInterface searchValueMeta(String valueName);

}
