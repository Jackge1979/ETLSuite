package com.cenrise.meta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @ClassName: RowMeta
 * @Description: 对行的元数据定义
 * @author dongpo.jia
 * @date 2016年12月12日
 *
 */
public class RowMeta implements RowMetaInterface {
	public static final String XML_META_TAG = "row-meta"; //$NON-NLS-1$

	public static final String XML_DATA_TAG = "row-data"; //$NON-NLS-1$

	private List<ValueMetaInterface> valueMetaList;

	private List<Integer> valuesThatNeedRealClone;

	/**
     * 
     */
	public RowMeta() {
		valueMetaList = new ArrayList<ValueMetaInterface>();
	}

	public RowMeta clone() {
		RowMeta rowMeta = new RowMeta();
		for (int i = 0; i < size(); i++) {
			ValueMetaInterface valueMeta = getValueMeta(i);
			rowMeta.addValueMeta(valueMeta);
		}
		return rowMeta;
	}

	/**
	 * @return true if there are no elements in the row metadata
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Searches for a value with a certain name in the value meta list
	 * 
	 * @param valueName
	 *            The value name to search for
	 * @return The value metadata or null if nothing was found
	 */
	public ValueMetaInterface searchValueMeta(String valueName) {
		for (int i = 0; i < valueMetaList.size(); i++) {
			ValueMetaInterface valueMeta = getValueMeta(i);
			if (valueMeta.getName().equalsIgnoreCase(valueName))
				return valueMeta;
		}
		return null;
	}

	/**
	 * Searches the index of a value meta with a given name
	 * 
	 * @param valueName
	 *            the name of the value metadata to look for
	 * @return the index or -1 in case we didn't find the value
	 */
	public int indexOfValue(String valueName) {
		for (int i = 0; i < valueMetaList.size(); i++) {
			if (getValueMeta(i).getName().equalsIgnoreCase(valueName))
				return i;
		}
		return -1;
	}

	public boolean exists(ValueMetaInterface meta) {
		return searchValueMeta(meta.getName()) != null;
	}

	private ValueMetaInterface renameValueMetaIfInRow(
			ValueMetaInterface valueMeta) {
		// We want to rename the field to Name[2], Name[3], ...
		//
		int index = 1;
		String name = valueMeta.getName() + "_" + index;
		while (searchValueMeta(name) != null) {
			index++;
			name = valueMeta.getName() + "_" + index;
		}

		// Create a copy of the valueMeta object to make sure we don't rename
		// any other value meta objects.
		// It's only being renamed because of the addition to THIS row metadata
		// object, not another.
		//
		ValueMetaInterface copy = valueMeta.clone();

		// OK, this is the new name to pick
		//
		copy.setName(name);

		return copy;
	}

	/**
	 * Add a metadata value. If a value with the same name already exists, it
	 * gets renamed.
	 * 
	 * @param meta
	 *            The metadata value to add
	 */
	public void addValueMeta(ValueMetaInterface meta) {
		if (!exists(meta)) {
			valueMetaList.add(meta);
		} else {
			valueMetaList.add(renameValueMetaIfInRow(meta));
		}
	}

	/**
	 * Add a metadata value on a certain location in the row. If a value with
	 * the same name already exists, it gets renamed. Remember to change the
	 * data row according to this.
	 *
	 * @param index
	 *            The index where the metadata value needs to be put in the row
	 * @param meta
	 *            The metadata value to add to the row
	 */
	public void addValueMeta(int index, ValueMetaInterface meta) {
		if (!exists(meta)) {
			valueMetaList.add(index, meta);
		} else {
			valueMetaList.add(index, renameValueMetaIfInRow(meta));
		}
	}

	/**
	 * Get the value metadata on the specified index.
	 * 
	 * @param index
	 *            The index to get the value metadata from
	 * @return The value metadata specified by the index.
	 */
	public ValueMetaInterface getValueMeta(int index) {
		return valueMetaList.get(index);
	}

	/**
	 * Replaces a value meta entry in the row metadata with another one
	 * 
	 * @param index
	 *            The index in the row to replace at
	 * @param valueMeta
	 *            the metadata to replace with
	 */
	public void setValueMeta(int index, ValueMetaInterface valueMeta) {
		valueMetaList.set(index, valueMeta);
	}

	/**
	 * @return the number of values in the row
	 */
	public int size() {
		return valueMetaList.size();
	}

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
			throws RuntimeException {
		for (int i = 0; i < fieldnrs.length; i++) {
			ValueMetaInterface valueMeta = getValueMeta(fieldnrs[i]);

			int cmp = valueMeta.compare(rowData1[fieldnrs[i]],
					rowData2[fieldnrs[i]]);
			if (cmp != 0)
				return cmp;
		}

		return 0;
	}

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
	public boolean equals(Object[] rowData1, Object[] rowData2, int[] fieldnrs)
			throws RuntimeException {
		for (int i = 0; i < fieldnrs.length; i++) {
			ValueMetaInterface valueMeta = getValueMeta(fieldnrs[i]);

			int cmp = valueMeta.compare(rowData1[fieldnrs[i]],
					rowData2[fieldnrs[i]]);
			if (cmp != 0)
				return false;
		}

		return true;
	}

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
			int fieldnrs2[]) throws RuntimeException {
		int len = (fieldnrs1.length < fieldnrs2.length) ? fieldnrs1.length
				: fieldnrs2.length;
		for (int i = 0; i < len; i++) {
			ValueMetaInterface valueMeta = getValueMeta(fieldnrs1[i]);

			int cmp = valueMeta.compare(rowData1[fieldnrs1[i]],
					rowData2[fieldnrs2[i]]);
			if (cmp != 0)
				return cmp;
		}

		return 0;
	}

	/**
	 * Compare 2 rows with each other using certain values in the rows and also
	 * considering the specified ascending clauses of the value metadata.
	 * 
	 * @param rowData1
	 *            The first row of data
	 * @param rowMeta2
	 *            the metadata of the second row of data
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
			throws RuntimeException {
		int len = (fieldnrs1.length < fieldnrs2.length) ? fieldnrs1.length
				: fieldnrs2.length;
		for (int i = 0; i < len; i++) {
			ValueMetaInterface valueMeta1 = getValueMeta(fieldnrs1[i]);
			ValueMetaInterface valueMeta2 = rowMeta2.getValueMeta(fieldnrs2[i]);

			int cmp = valueMeta1.compare(rowData1[fieldnrs1[i]], valueMeta2,
					rowData2[fieldnrs2[i]]);
			if (cmp != 0)
				return cmp;
		}

		return 0;
	}

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
			throws RuntimeException {
		for (int i = 0; i < size(); i++) {
			ValueMetaInterface valueMeta = getValueMeta(i);

			int cmp = valueMeta.compare(rowData1[i], rowData2[i]);
			if (cmp != 0)
				return cmp;
		}

		return 0;
	}

	/**
	 * @return the list of value metadata
	 */
	public List<ValueMetaInterface> getValueMetaList() {
		return valueMetaList;
	}

	/**
	 * @return a cloned Object[] object.
	 * @throws RuntimeException
	 *             in case something is not quite right with the expected data
	 */
	public Object[] cloneRow(Object[] objects) throws RuntimeException {
		return cloneRow(objects, objects.clone());
	}

	/**
	 * @return a cloned Object[] object.
	 * @throws RuntimeException
	 *             in case something is not quite right with the expected data
	 */
	public Object[] cloneRow(Object[] objects, Object[] newObjects)
			throws RuntimeException {
		if (valuesThatNeedRealClone == null) {
			valuesThatNeedRealClone = new ArrayList<Integer>();
			for (int i = 0; i < size(); i++) {
				ValueMetaInterface valueMeta = getValueMeta(i);
				if (valueMeta.requiresRealClone()) {
					valuesThatNeedRealClone.add(i);
				}
			}
		}
		for (Integer i : valuesThatNeedRealClone) {
			ValueMetaInterface valueMeta = getValueMeta(i);
			newObjects[i] = valueMeta.cloneValueData(objects[i]);
		}
		return newObjects;
	}

	/**
	 * Get the string representation of the data in a row of data
	 * 
	 * @param row
	 *            the row of data to convert to string
	 * @return the row of data in string form
	 * @throws RuntimeException
	 *             in case of a conversion error
	 */
	public String getString(Object[] row) throws RuntimeException {
		StringBuffer buffer = new StringBuffer();
		String logRow = null;
		for (int i = 0; i < size(); i++) {
			// gengjie 2013-3-13 当字段类型为Binary时，在日志中不打印其内容，仅打印“Binary Data”
			// TIETL-1391 TIETL-826
			ValueMetaInterface meta = valueMetaList.get(i);
			if (ValueMetaInterface.TYPE_BINARY == meta.getType()) {
				logRow = "Binary Data";
			} else if (meta.getType() == ValueMetaInterface.TYPE_TIETLFUNC) {
				logRow = meta.getComments();
			} else {
				logRow = getString(row, i);
			}

			if (i > 0)
				buffer.append(", ");
			buffer.append("[");
			// buffer.append( getString(row, i) );
			buffer.append(logRow);
			buffer.append("]");
		}
		return buffer.toString();
	}

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
			throws RuntimeException {
		if (dataRow == null) {
			return null;
		}
		ValueMetaInterface meta = valueMetaList.get(index);
		// gengjie 2013-3-13 不应该在这里修改binary类型的值 TIETL-1391
		// // gengjie 2012-7-24 当字段类型为Binary时，在日志中不打印其内容，仅打印“Binary Data”
		// if(ValueMetaInterface.TYPE_BINARY == meta.getType()){
		// return "Binary Data";
		// }
		return meta.getString(dataRow[index]);
	}

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
	public Long getInteger(Object[] dataRow, int index) throws RuntimeException {
		if (dataRow == null) {
			return null;
		}
		ValueMetaInterface meta = valueMetaList.get(index);
		return meta.getInteger(dataRow[index]);
	}

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
			throws RuntimeException {
		if (dataRow == null) {
			return null;
		}
		ValueMetaInterface meta = valueMetaList.get(index);
		return meta.getNumber(dataRow[index]);
	}

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
	public Date getDate(Object[] dataRow, int index) throws RuntimeException {
		if (dataRow == null) {
			return null;
		}
		ValueMetaInterface meta = valueMetaList.get(index);
		return (Date) meta.getDate(dataRow[index]);
	}

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
			throws RuntimeException {
		if (dataRow == null) {
			return null;
		}
		ValueMetaInterface meta = valueMetaList.get(index);
		return meta.getBigNumber(dataRow[index]);
	}

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
			throws RuntimeException {
		if (dataRow == null) {
			return null;
		}
		ValueMetaInterface meta = valueMetaList.get(index);
		return meta.getBoolean(dataRow[index]);
	}

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
			throws RuntimeException {
		if (dataRow == null) {
			return null;
		}
		ValueMetaInterface meta = valueMetaList.get(index);
		return meta.getBinary(dataRow[index]);
	}

	/**
	 * Determines whether a value in a row is null. A value is null when the
	 * object is null or when it's an empty String
	 * 
	 * @param dataRow
	 *            The row of data
	 * @param index
	 *            the index to reference
	 * @return true if the value on the index is null.
	 * @throws RuntimeException
	 *             in case there is a conversion error (only thrown in case of
	 *             lazy conversion)
	 */
	public boolean isNull(Object[] dataRow, int index) throws RuntimeException {
		if (dataRow == null) {
			// I guess so...
			return true;
		}
		return getValueMeta(index).isNull(dataRow[index]);
	}

	public String getString(Object[] dataRow, String valueName,
			String defaultValue) throws RuntimeException {
		int index = indexOfValue(valueName);
		if (index < 0)
			return defaultValue;
		return getString(dataRow, index);
	}

	public Long getInteger(Object[] dataRow, String valueName, Long defaultValue)
			throws RuntimeException {
		int index = indexOfValue(valueName);
		if (index < 0)
			return defaultValue;
		return getInteger(dataRow, index);
	}

	public Date getDate(Object[] dataRow, String valueName, Date defaultValue)
			throws RuntimeException {
		int index = indexOfValue(valueName);
		if (index < 0)
			return defaultValue;
		return getDate(dataRow, index);
	}

	/**
	 * @param valueMetaList
	 *            the list of valueMeta to set
	 */
	public void setValueMetaList(List<ValueMetaInterface> valueMetaList) {
		this.valueMetaList = valueMetaList;
	}

}
