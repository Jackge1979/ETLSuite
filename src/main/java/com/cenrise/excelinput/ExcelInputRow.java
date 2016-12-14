package com.cenrise.excelinput;

import com.cenrise.spreadsheet.KCell;

/**
 * 
 * @ClassName: ExcelInputRow
 * @Description: Represent 1 row in a an Excel sheet.
 * @author dongpo.jia
 * @date 2016年12月12日
 *
 */
public class ExcelInputRow {

	public final String sheetName;
	public final int rownr;
	public final KCell[] cells;

	public ExcelInputRow(String sheetName, int rownr, KCell[] cells) {
		this.sheetName = sheetName;
		this.rownr = rownr;
		this.cells = cells;
	}
}