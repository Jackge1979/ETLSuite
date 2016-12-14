package com.cenrise.excelinput.jxl;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

import com.cenrise.spreadsheet.KSheet;
import com.cenrise.spreadsheet.KWorkbook;
import com.cenrise.util.Const;
import com.cenrise.vfs.KettleVFS;

/**
 * 
 * @ClassName: XLSWorkbook
 * @Description: Excel 97-2003 XLS (JXL)
 * @author dongpo.jia
 * @date 2016年12月12日
 *
 */
public class XLSWorkbook implements KWorkbook {

	private Workbook workbook;
	private String filename;
	private String encoding;

	public XLSWorkbook(String filename, String encoding)
			throws RuntimeException {
		this.filename = filename;
		this.encoding = encoding;

		WorkbookSettings ws = new WorkbookSettings();
		if (!Const.isEmpty(encoding)) {
			ws.setEncoding(encoding);
		}
		try {
			workbook = Workbook.getWorkbook(KettleVFS.getInputStream(filename),
					ws);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		workbook.close();
	}

	@Override
	public KSheet getSheet(String sheetName) {
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null)
			return null;
		return new XLSSheet(sheet);
	}

	public String[] getSheetNames() {
		return workbook.getSheetNames();
	}

	public String getFilename() {
		return filename;
	}

	public String getEncoding() {
		return encoding;
	}

	public int getNumberOfSheets() {
		return workbook.getNumberOfSheets();
	}

	public KSheet getSheet(int sheetNr) {
		Sheet sheet = workbook.getSheet(sheetNr);
		if (sheet == null)
			return null;
		return new XLSSheet(sheet);
	}
}
