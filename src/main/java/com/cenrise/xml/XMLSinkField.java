package com.cenrise.xml;

import com.cenrise.meta.ValueMeta;

/**
 * Describes a single field in an XML output file
 */
public class XMLSinkField implements Cloneable {
	private String fieldName;
	private String elementName;
	private int type;
	private String format;
	private int length;
	private int precision;
	private String currencySymbol;
	private String decimalPointSymbol;
	private String thousandSeparator;
	private int trimType;
	private String nullString;

	public int getTrimType() {
		return trimType;
	}

	public void setTrimType(int trimType) {
		this.trimType = trimType;
	}

	public String getTrimTypeCode() {
		return ValueMeta.getTrimTypeCode(trimType);
	}

	public String getTrimTypeDesc() {
		return ValueMeta.getTrimTypeDesc(trimType);
	}

	public XMLSinkField() {
	}

	public int compare(Object obj) {
		XMLSinkField field = (XMLSinkField) obj;

		return fieldName.compareTo(field.getFieldName());
	}

	public boolean equal(Object obj) {
		XMLSinkField field = (XMLSinkField) obj;

		return fieldName.equals(field.getFieldName());
	}

	public Object clone() {
		try {
			Object retval = super.clone();
			return retval;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldname) {
		this.fieldName = fieldname;
	}

	public int getType() {
		return type;
	}

	public String getTypeDesc() {
		return ValueMeta.getTypeDesc(type);
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setType(String typeDesc) {
		this.type = ValueMeta.getType(typeDesc);
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currency_symbol) {
		this.currencySymbol = currency_symbol;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public String getDecimalPointSymbol() {
		return decimalPointSymbol;
	}

	public void setDecimalPointSymbol(String decimalPointSymbol) {
		this.decimalPointSymbol = decimalPointSymbol;
	}

	public String getThousandSeparator() {
		return thousandSeparator;
	}

	public void setThousandSeparator(String thousandSeparator) {
		this.thousandSeparator = thousandSeparator;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getNullString() {
		return nullString;
	}

	public void setNullString(String nullString) {
		this.nullString = nullString;
	}

}
