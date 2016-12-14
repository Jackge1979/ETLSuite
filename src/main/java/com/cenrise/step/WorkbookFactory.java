package com.cenrise.step;

import com.cenrise.excelinput.SpreadSheetType;
import com.cenrise.excelinput.jxl.XLSWorkbook;
import com.cenrise.excelinput.ods.OdfWorkbook;
import com.cenrise.excelinput.poi.PoiWorkbook;
import com.cenrise.spreadsheet.KWorkbook;

public class WorkbookFactory {

	public static KWorkbook getWorkbook(SpreadSheetType type, String filename,
			String encoding) throws RuntimeException {
		switch (type) {
		case JXL:
			return new XLSWorkbook(filename, encoding);
		case POI:
			return new PoiWorkbook(filename, encoding); // encoding is not used,
														// perhaps detected
														// automatically?
		case ODS:
			return new OdfWorkbook(filename, encoding); // encoding is not used,
														// perhaps detected
														// automatically?
		default:
			throw new RuntimeException("Sorry, spreadsheet type "
					+ type.getDescription() + " is not yet supported");
		}

	}
}
