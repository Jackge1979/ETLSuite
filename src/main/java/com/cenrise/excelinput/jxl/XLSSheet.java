package com.cenrise.excelinput.jxl;

import com.cenrise.spreadsheet.KCell;
import com.cenrise.spreadsheet.KSheet;

import jxl.Cell;
import jxl.Sheet;

public class XLSSheet implements KSheet {
	private Sheet sheet;

	public XLSSheet(Sheet sheet) {
		this.sheet = sheet;
	}

	public String getName() {
		return sheet.getName();
	}

	public KCell[] getRow(int rownr) {
		Cell[] cells = sheet.getRow(rownr);
		XLSCell[] xlsCells = new XLSCell[cells.length];
		for (int i = 0; i < cells.length; i++) {
			if (cells[i] != null) {
				xlsCells[i] = new XLSCell(cells[i]);
			}
		}
		return xlsCells;
	}

	public int getRows() {
		return sheet.getRows();
	}

	public KCell getCell(int colnr, int rownr) {
		Cell cell = sheet.getCell(colnr, rownr);
		if (cell == null)
			return null;
		return new XLSCell(cell);
	}
}
