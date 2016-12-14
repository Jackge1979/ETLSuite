package com.cenrise.excelinput.poi;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.cenrise.spreadsheet.KSheet;
import com.cenrise.spreadsheet.KWorkbook;
import com.cenrise.vfs.KettleVFS;
/**
 * 
    * @ClassName: PoiWorkbook  
    * @Description: Excel 2007 XLSX (Apache POI)
    * @author dongpo.jia  
    * @date 2016年12月12日  
    *
 */
public class PoiWorkbook implements KWorkbook {

	private Workbook workbook;
	private String filename;
	private String encoding;

	public PoiWorkbook(String filename, String encoding)
			throws RuntimeException {
		this.filename = filename;
		this.encoding = encoding;

		try {
			workbook = org.apache.poi.ss.usermodel.WorkbookFactory
					.create(KettleVFS.getInputStream(filename));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		// not needed here
	}

	@Override
	public KSheet getSheet(String sheetName) {
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null)
			return null;
		return new PoiSheet(sheet);
	}

	public String[] getSheetNames() {
		int nrSheets = workbook.getNumberOfSheets();
		String[] names = new String[nrSheets];
		for (int i = 0; i < nrSheets; i++) {
			names[i] = workbook.getSheetName(i);
		}
		return names;
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
		Sheet sheet = workbook.getSheetAt(sheetNr);
		if (sheet == null)
			return null;
		return new PoiSheet(sheet);
	}
}
