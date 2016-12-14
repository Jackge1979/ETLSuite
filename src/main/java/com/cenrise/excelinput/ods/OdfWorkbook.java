package com.cenrise.excelinput.ods;

import java.util.List;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;

import com.cenrise.spreadsheet.KSheet;
import com.cenrise.spreadsheet.KWorkbook;
import com.cenrise.vfs.KettleVFS;

/**
 * 
 * @ClassName: OdfWorkbook
 * @Description: Open Office ODS (ODFDOM)
 * @author dongpo.jia
 * @date 2016年12月12日
 *
 */
public class OdfWorkbook implements KWorkbook {

	private String filename;
	private String encoding;
	private OdfDocument document;

	public OdfWorkbook(String filename, String encoding)
			throws RuntimeException {
		this.filename = filename;
		this.encoding = encoding;

		try {
			document = OdfSpreadsheetDocument.loadDocument(KettleVFS
					.getInputStream(filename));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		// not needed here
	}

	@Override
	public KSheet getSheet(String sheetName) {
		OdfTable table = document.getTableByName(sheetName);
		if (table == null)
			return null;
		return new OdfSheet(table);
	}

	public String[] getSheetNames() {
		List<OdfTable> list = document.getTableList();
		int nrSheets = list.size();
		String[] names = new String[nrSheets];
		for (int i = 0; i < nrSheets; i++) {
			names[i] = list.get(i).getTableName();
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
		return document.getTableList().size();
	}

	public KSheet getSheet(int sheetNr) {
		OdfTable table = document.getTableList().get(sheetNr);
		if (table == null)
			return null;
		return new OdfSheet(table);
	}
}
