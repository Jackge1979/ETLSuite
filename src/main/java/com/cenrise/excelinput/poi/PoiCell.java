package com.cenrise.excelinput.poi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.cenrise.spreadsheet.KCell;
import com.cenrise.spreadsheet.KCellType;

public class PoiCell implements KCell {
	// excel2007日期单元格的类型值
	private static List<String> date_format_list = new ArrayList<String>();
	private static List<String> time_format_list = new ArrayList<String>();
	static {
		date_format_list.add("[DBNum1][$-804]yyyy\"年\"m\"月\"d\"日\";@");
		date_format_list.add("m/d/yy");
		date_format_list.add("[$-F800]dddd\\,\\ mmmm\\ dd\\,\\ yyyy");
		date_format_list.add("[DBNum1][$-804]yyyy\"年\"m\"月\";@");
		date_format_list.add("[DBNum1][$-804]m\"月\"d\"日\";@");
		date_format_list.add("yyyy\"年\"m\"月\"d\"日\";@");
		date_format_list.add("yyyy\"年\"m\"月\";@");
		date_format_list.add("yyyy\"年\"m\"月\";@");
		date_format_list.add("m\"月\"d\"日\";@");
		date_format_list.add("[$-804]aaaa;@");
		date_format_list.add("[$-804]aaa;@");
		date_format_list.add("yyyy/m/d;@");
		date_format_list.add("[$-409]yyyy/m/d\\ h:mm\\ AM/PM;@");
		date_format_list.add("yyyy/m/d\\ h:mm;@");
		date_format_list.add("yy/m/d;@");
		date_format_list.add("m/d;@");
		date_format_list.add("m/d/yy;@");
		date_format_list.add("mm/dd/yy;@");
		date_format_list.add("[$-409]d/mmm;@");
		date_format_list.add("[$-409]d/mmm/yy;@");
		date_format_list.add("[$-409]dd/mmm/yy;@");
		date_format_list.add("[$-409]mmm/yy;@");
		date_format_list.add("[$-409]mmmm/yy;@");
		date_format_list.add("[$-409]mmmmm;@");
		date_format_list.add("[$-409]mmmmm/yy;@");

		time_format_list.add("[$-F400]h:mm:ss\\ AM/PM");
		time_format_list.add("h:mm;@");
		time_format_list.add("[$-409]h:mm\\ AM/PM;@");
		time_format_list.add("h:mm:ss;@");
		time_format_list.add("[$-409]h:mm:ss\\ AM/PM;@");
		time_format_list.add("h\"时\"mm\"分\";@");
		time_format_list.add("h\"时\"mm\"分\"ss\"秒\";@");
		time_format_list.add("AM/PMh\"时\"mm\"分\";@");
		time_format_list.add("AM/PMh\"时\"mm\"分\"ss\"秒\";@");
		time_format_list.add("[DBNum1][$-804]上午/下午h\"时\"mm\"分\";@");
		time_format_list.add("[DBNum1][$-804]h\"时\"mm\"分\";@");
		time_format_list.add("[DBNum1][$-804]AM/PMh\"时\"mm\"分\";@");
		time_format_list.add("[$-F400]h:mm:ss\\ AM/PM");
		time_format_list.add("[$-F400]h:mm:ss\\ AM/PM");
		time_format_list.add("[$-F400]h:mm:ss\\ AM/PM");
		time_format_list.add("[$-F400]h:mm:ss\\ AM/PM");
		time_format_list.add("[$-F400]h:mm:ss\\ AM/PM");
		time_format_list.add("[$-F400]h:mm:ss\\ AM/PM");
	}

	private Cell cell;

	public PoiCell(Cell cell) {
		this.cell = cell;
	}

	public KCellType getType() {
		int type = cell.getCellType();
		if (type == Cell.CELL_TYPE_BOOLEAN) {
			return KCellType.BOOLEAN;
		} else if (type == Cell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				return KCellType.DATE;
			} else if (isDateFormatted(cell)) {
				return KCellType.DATE;
			} else {
				return KCellType.NUMBER;
			}
		} else if (type == Cell.CELL_TYPE_STRING) {
			return KCellType.LABEL;
		} else if (type == Cell.CELL_TYPE_BLANK || type == Cell.CELL_TYPE_ERROR) {
			return KCellType.EMPTY;
		} else if (type == Cell.CELL_TYPE_FORMULA) {
			switch (cell.getCachedFormulaResultType()) {
			case Cell.CELL_TYPE_BLANK:
			case Cell.CELL_TYPE_ERROR:
				return KCellType.EMPTY;
			case Cell.CELL_TYPE_BOOLEAN:
				return KCellType.BOOLEAN_FORMULA;
			case Cell.CELL_TYPE_STRING:
				return KCellType.STRING_FORMULA;
			case Cell.CELL_TYPE_NUMERIC:
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					return KCellType.DATE_FORMULA;
				} else {
					return KCellType.NUMBER_FORMULA;
				}
			}
		}
		return null;
	}

	/*
	 * 
	 * excel2007日期类型的单元格，使用HSSFDateUtil.isCellDateFormatted(cell)只能判断出来一部分，
	 * 所以会导致部分日期类型的不能被正确识别
	 * 其余的日期类型单元格，只能通过cell.getCellStyle().getDataFormat()进行比较进行硬编码识别
	 */
	private boolean isDateFormatted(Cell cell) {
		String dataFormat = cell.getCellStyle().getDataFormatString();
		// System.out.println(dataFormat);
		if (date_format_list.contains(dataFormat)
				|| time_format_list.contains(dataFormat))
			return true;
		return false;
	}

	public Object getValue() {
		try {
			switch (getType()) {
			case BOOLEAN_FORMULA:
			case BOOLEAN:
				return Boolean.valueOf(cell.getBooleanCellValue());
			case DATE_FORMULA:
			case DATE:
				// Timezone conversion needed since POI doesn't support this
				// apparently
				//
				long time = cell.getDateCellValue().getTime();
				long tzOffset = TimeZone.getDefault().getOffset(time);
				return new Date(time + tzOffset);
			case NUMBER_FORMULA:
			case NUMBER:
				return Double.valueOf(cell.getNumericCellValue());
			case STRING_FORMULA:
			case LABEL:
				return cell.getStringCellValue();
			case EMPTY:
			default:
				return null;
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to get value of cell ("
					+ cell.getColumnIndex() + ", " + cell.getRowIndex() + ")",
					e);
		}
	}

	public String getContents() {
		try {
			Object value = getValue();
			if (value == null)
				return null;
			return value.toString();
		} catch (Exception e) {
			throw new RuntimeException("Unable to get string content of cell ("
					+ cell.getColumnIndex() + ", " + cell.getRowIndex() + ")",
					e);
		}
	}

	public int getRow() {
		Row row = cell.getRow();
		return row.getRowNum();
	}
}
