package com.cenrise.excelinput;

import java.util.Date;
import java.util.HashMap;

import javax.sql.RowSet;

import org.apache.commons.vfs2.FileObject;

import com.cenrise.meta.RowMetaInterface;
import com.cenrise.meta.ValueMeta;
import com.cenrise.meta.ValueMetaInterface;
import com.cenrise.spreadsheet.KSheet;
import com.cenrise.spreadsheet.KWorkbook;
import com.cenrise.step.FileInputList;

/**
 * 
 * @ClassName: ExcelInputData
 * @Description: 运行时数据
 * @author dongpo.jia
 * @date 2016年12月12日
 *
 */
public class ExcelInputData {
	/**
	 * The previous row in case we want to repeat values...
	 */
	public Object[] previousRow;

	/**
	 * The maximum length of all filenames...
	 */
	public int maxfilelength;

	/**
	 * The maximum length of all sheets...
	 */
	public int maxsheetlength;

	/**
	 * The Excel files to read
	 */
	public FileInputList files;

	/**
	 * The file number that's being handled...
	 */
	public int filenr;

	public String filename;

	public FileObject file;

	/**
	 * The openFile that's being processed...
	 */
	public KWorkbook workbook;

	/**
	 * The sheet number that's being processed...
	 */
	public int sheetnr;

	/**
	 * The sheet that's being processed...
	 */
	public KSheet sheet;

	/**
	 * The row where we left off the previous time...
	 */
	public int rownr;

	/**
	 * The column where we left off previous time...
	 */
	public int colnr;

	public RowMetaInterface outputRowMeta;

	ValueMetaInterface valueMetaString;
	ValueMetaInterface valueMetaNumber;
	ValueMetaInterface valueMetaDate;
	ValueMetaInterface valueMetaBoolean;

	public RowMetaInterface conversionRowMeta;

	public String[] sheetNames;
	public int[] startColumn;
	public int[] startRow;

	public int defaultStartColumn;
	public int defaultStartRow;

	public String shortFilename;
	public String path;
	public String extension;
	public boolean hidden;
	public Date lastModificationDateTime;
	public String uriName;
	public String rootUriName;
	public long size;

	public HashMap<FileObject, Object[]> passThruFields;

	public int nrPassThruFields;

	public RowSet rowSet;

	public boolean errorRow;

	public String errorDesc;

	public String[] errorRowValue;

	/**
	 * 
	 */
	public ExcelInputData() {
		super();
		workbook = null;
		filenr = 0;
		sheetnr = 0;
		rownr = -1;
		colnr = -1;
		nrPassThruFields = 0;
		valueMetaString = new ValueMeta("v", ValueMetaInterface.TYPE_STRING);
		valueMetaNumber = new ValueMeta("v", ValueMetaInterface.TYPE_NUMBER);
		valueMetaDate = new ValueMeta("v", ValueMetaInterface.TYPE_DATE);
		valueMetaBoolean = new ValueMeta("v", ValueMetaInterface.TYPE_BOOLEAN);
	}
}
