package com.cenrise.excelinput.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.cenrise.spreadsheet.KCell;
import com.cenrise.spreadsheet.KSheet;

public class PoiSheet implements KSheet {
	private Sheet sheet;

	public PoiSheet(Sheet sheet) {
		this.sheet = sheet;
	}

	public String getName() {
		return sheet.getSheetName();
	}

	public KCell[] getRow(int rownr) {
		if (rownr < sheet.getFirstRowNum()) {
			return new KCell[] {};
		} else if (rownr > sheet.getLastRowNum()) {
			throw new ArrayIndexOutOfBoundsException("Read beyond last row: "
					+ rownr);
		}
		Row row = sheet.getRow(rownr);
		if (row == null) { // read an empty row
			return new KCell[] {};
		}
		int cols = row.getLastCellNum();
		if (cols > 0) {// 当单元格数为-1时，不创建单元格数组
			PoiCell[] xlsCells = new PoiCell[cols];
			for (int i = 0; i < cols; i++) {
				Cell cell = row.getCell(i);
				if (cell != null) {
					xlsCells[i] = new PoiCell(cell);
				}
			}
			return xlsCells;
		}
		return new KCell[] {};
	}

	public int getRows() {
		return sheet.getLastRowNum() + 1;
	}

	public KCell getCell(int colnr, int rownr) {
		Row row = sheet.getRow(rownr);
		if (row == null)
			return null;
		Cell cell = row.getCell(colnr);
		if (cell == null)
			return null;
		return new PoiCell(cell);
	}
}
