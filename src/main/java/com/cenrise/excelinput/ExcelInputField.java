package com.cenrise.excelinput;

import com.cenrise.meta.ValueMeta;
import com.cenrise.meta.ValueMetaInterface;

/**
 * 
 * @ClassName: ExcelInputField
 * @Description:Describes a single field in an excel file
 * @author dongpo.jia
 * @date 2016年12月12日
 *
 */
public class ExcelInputField implements Cloneable {
	private String name;
	private int type;
	private int length;
	private int precision;
	private int trimtype;
	private String format;
	private String currencySymbol;
	private String decimalSymbol;
	private String groupSymbol;
	private boolean repeat;
	// 该属性用于存储字段在excel文件中的位置序号
	private int index;

	public ExcelInputField(String fieldname, int position, int length, int index) {
		this.name = fieldname;
		this.length = length;
		this.type = ValueMetaInterface.TYPE_STRING;
		this.format = "";
		this.groupSymbol = "";
		this.decimalSymbol = "";
		this.currencySymbol = "";
		this.precision = -1;
		this.repeat = false;
		this.index = index;
	}

	public ExcelInputField() {
		this(null, -1, -1, -1);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
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

	public String getName() {
		return name;
	}

	public void setName(String fieldname) {
		this.name = fieldname;
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

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getTrimType() {
		return trimtype;
	}

	public void setTrimType(int trimtype) {
		this.trimtype = trimtype;
	}

	public String getGroupSymbol() {
		return groupSymbol;
	}

	public void setGroupSymbol(String group_symbol) {
		this.groupSymbol = group_symbol;
	}

	public String getDecimalSymbol() {
		return decimalSymbol;
	}

	public void setDecimalSymbol(String decimal_symbol) {
		this.decimalSymbol = decimal_symbol;
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

	public boolean isRepeated() {
		return repeat;
	}

	public void setRepeated(boolean repeat) {
		this.repeat = repeat;
	}

	public void flipRepeated() {
		repeat = !repeat;
	}

	public String toString() {
		return name + ":" + getTypeDesc() + "(" + length + "," + precision
				+ ")";
	}
}
