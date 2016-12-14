package com.cenrise.excelinput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;

import com.cenrise.meta.RowMeta;
import com.cenrise.meta.RowMetaInterface;
import com.cenrise.meta.ValueMeta;
import com.cenrise.meta.ValueMetaInterface;
import com.cenrise.spreadsheet.KCell;
import com.cenrise.spreadsheet.KCellType;
import com.cenrise.spreadsheet.KSheet;
import com.cenrise.spreadsheet.KWorkbook;
import com.cenrise.step.FileInputList;
import com.cenrise.step.WorkbookFactory;
import com.cenrise.util.Const;
import com.cenrise.variables.VariableSpace;
import com.cenrise.variables.Variables;
import com.cenrise.vfs.KettleVFS;

/**
 * 
 * @ClassName: ExcelInputMeta
 * @Description: 配置信息
 * @author dongpo.jia
 * @date 2016年12月12日
 *
 */
public class ExcelInputMeta {
	private static final Log logger = LogFactory.getLog(ExcelInputMeta.class);
	/**
	 * The filenames to load or directory in case a filemask was set.
	 */
	private String fileName[];

	/**
	 * The fieldname that holds the name of the file
	 */
	private String fileField;

	/**
	 * The names of the sheets to load. Null means: all sheets...
	 */
	private String sheetName[];

	/**
	 * The row-nr where we start processing.
	 */
	private int startRow[];

	/**
	 * The column-nr where we start processing.
	 */
	private int startColumn[];

	/**
	 * The fieldname that holds the name of the sheet
	 */
	private String sheetField;

	/**
	 * The cell-range starts with a header-row
	 */
	private boolean startsWithHeader;

	/**
	 * Stop reading when you hit an empty row.
	 */
	private boolean stopOnEmpty;

	/**
	 * Avoid empty rows in the result.
	 */
	private boolean ignoreEmptyRows;

	/**
	 * The fieldname containing the row number. An empty (null) value means that
	 * no row number is included in the output. This is the rownumber of all
	 * written rows (not the row in the sheet).
	 */
	private String rowNumberField;

	/**
	 * The fieldname containing the sheet row number. An empty (null) value
	 * means that no sheet row number is included in the output. Sheet row
	 * number is the row number in the sheet.
	 */
	private String sheetRowNumberField;

	/**
	 * The maximum number of rows that this step writes to the next step.
	 */
	private long rowLimit;

	/**
	 * The fields to read in the range. Note: the number of columns in the range
	 * has to match field.length
	 */
	private ExcelInputField field[];

	/**
	 * Array of boolean values as string, indicating if we need to fetch sub
	 * folders.
	 */
	private String includeSubFolders[];
	/** Strict types : will generate erros */
	private boolean strictTypes;
	/**
	 * The encoding to use for reading: null or empty string means system
	 * default encoding
	 */
	private String encoding;
	// 类型
	private SpreadSheetType spreadSheetType;

	public FileInputList getFileList(VariableSpace space) {
		// TODO 处理文件列表，这几个参数都暂时设置为0
		return FileInputList.createFileList(space, fileName, null, null, null, null);
	}

	/**
	* Read all sheets if the sheet names are left blank. 
	* @return true if all sheets are read.
	*/
	public boolean readAllSheets() {
		return Const.isEmpty(sheetName) || (sheetName.length == 1 && Const.isEmpty(sheetName[0]));
	}

	/**
	 * 
	    * @Title: getFields  
	    * @Description:  获取excel文件指定sheet页的结构信息
	    * @param @param file
	    * @param @param sheetName
	    * @param @return    参数  
	    * @return SheetFieldMeta    返回类型  
	    * @throws
	 */
	public SheetFieldMeta getFields(FileObject file, String sheetName) {
		SheetFieldMeta meta = new SheetFieldMeta();
		try {
			KWorkbook workbook = WorkbookFactory.getWorkbook(getSpreadSheetType(), KettleVFS.getFilename(file),
					getEncoding());

			KSheet sheet = workbook.getSheet(sheetName);
			if (sheet == null)
				return null;

			int rownr = 0;
			int startcol = 0;
			boolean stop = false;

			for (int colnr = startcol; colnr < 256 && !stop; colnr++) {
				try {
					String fieldname = null;
					int fieldtype = ValueMetaInterface.TYPE_NONE;

					KCell cell = sheet.getCell(colnr, rownr);
					if (cell == null) {
						stop = true;
					} else {
						if (cell.getType() != KCellType.EMPTY) {
							fieldname = cell.getContents();
						}
						// 当字段名为空时，默认已经到了最后一列
						if (fieldname == null || fieldname.trim().length() == 0) {
							stop = true;
							continue;
						}

						KCell below = sheet.getCell(colnr, rownr + 1);
						if (below != null) {
							if (below.getType() == KCellType.BOOLEAN) {
								fieldtype = ValueMetaInterface.TYPE_BOOLEAN;
							} else if (below.getType() == KCellType.DATE) {
								fieldtype = ValueMetaInterface.TYPE_DATE;
							} else if (below.getType() == KCellType.LABEL) {
								fieldtype = ValueMetaInterface.TYPE_STRING;
							} else if (below.getType() == KCellType.NUMBER) {
								fieldtype = ValueMetaInterface.TYPE_NUMBER;
							}

							if (fieldname != null && fieldtype == ValueMetaInterface.TYPE_NONE) {
								fieldtype = ValueMetaInterface.TYPE_STRING;
							}
						}

						if (fieldname != null && fieldtype != ValueMetaInterface.TYPE_NONE) {
							meta.putFieldMeta(fieldname, colnr, fieldtype);
						} else {
							if (fieldname == null)
								stop = true;
						}
					}
				} catch (ArrayIndexOutOfBoundsException aioobe) {
					stop = true;
				}
			}
			workbook.close();
		} catch (Exception e) {
			logger.error(
					"无法读取Excel文件[{" + KettleVFS.getFilename(file) + "}].\n  请检查文件, 目录和表达式.\n{" + e.toString() + "}");
		}
		return meta;
	}

	/**
	 * Get the list of fields in the Excel workbook and put the result in the fields table view.
	 */
	public void getFields() {
		RowMetaInterface fields = new RowMeta();

		ExcelInputMeta info = new ExcelInputMeta();
		// getInfo(info);

		// 创建一个空的VariableSpace
		VariableSpace defaultVariableSpace = new Variables();
		defaultVariableSpace.initializeVariablesFrom(null);
		FileInputList fileList = info.getFileList(defaultVariableSpace);

		for (FileObject file : fileList.getFiles()) {
			try {
				KWorkbook workbook = WorkbookFactory.getWorkbook(info.getSpreadSheetType(), KettleVFS.getFilename(file),
						info.getEncoding());

				int nrSheets = workbook.getNumberOfSheets();
				for (int j = 0; j < nrSheets; j++) {
					KSheet sheet = workbook.getSheet(j);

					// See if it's a selected sheet:
					int sheetIndex;
					if (info.readAllSheets()) {
						sheetIndex = 0;
					} else {
						// sheetIndex = Const.indexOfString(sheet.getName(),
						// info.getSheetName());
						sheetIndex = -1;
						for (int m = 0; m < info.getSheetName().length; m++) {
							if (info.getSheetName()[m].equals(sheet.getName())) {
								sheetIndex = m;
								break;
							}
						}
					}
					if (sheetIndex >= 0) {
						// We suppose it's the complete range we're looking
						// for...
						//
						int rownr = 0;
						int startcol = 0;

						if (info.readAllSheets()) {
							if (info.getStartColumn().length == 1)
								startcol = info.getStartColumn()[0];
							if (info.getStartRow().length == 1)
								rownr = info.getStartRow()[0];
						} else {
							rownr = info.getStartRow()[sheetIndex];
							startcol = info.getStartColumn()[sheetIndex];
						}

						boolean stop = false;

						for (int colnr = startcol; colnr < 256 && !stop; colnr++) {
							try {
								String fieldname = null;
								int fieldtype = ValueMetaInterface.TYPE_NONE;

								KCell cell = sheet.getCell(colnr, rownr);
								if (cell == null) {
									stop = true;
								} else {
									if (cell.getType() != KCellType.EMPTY) {
										// We found a field.
										fieldname = cell.getContents();
									}
									// 2012-05-28 , added by jiayf ,
									// 当字段名为空时，默认已经到了最后一列，不再取列名了
									if (fieldname == null || fieldname.trim().length() == 0) {
										stop = true;
										continue;
									}

									// System.out.println("Fieldname =
									// "+fieldname);

									KCell below = sheet.getCell(colnr, rownr + 1);
									if (below != null) {
										if (below.getType() == KCellType.BOOLEAN) {
											fieldtype = ValueMetaInterface.TYPE_BOOLEAN;
										} else if (below.getType() == KCellType.DATE) {
											fieldtype = ValueMetaInterface.TYPE_DATE;
										} else if (below.getType() == KCellType.LABEL) {
											fieldtype = ValueMetaInterface.TYPE_STRING;
										} else if (below.getType() == KCellType.NUMBER) {
											fieldtype = ValueMetaInterface.TYPE_NUMBER;
										}

										if (fieldname != null && fieldtype == ValueMetaInterface.TYPE_NONE) {
											fieldtype = ValueMetaInterface.TYPE_STRING;
										}
									} else {// 2012-06-08 , added by jiayf ,
											// 避免空值早晨类型取不到的问题
										fieldtype = ValueMetaInterface.TYPE_STRING;
									}

									if (fieldname != null && fieldtype != ValueMetaInterface.TYPE_NONE) {
										ValueMeta field = new ValueMeta(fieldname, fieldtype);
										field.setColumnIndexExcel(colnr);
										fields.addValueMeta(field);
									} else {
										if (fieldname == null)
											stop = true;
									}
								}
							} catch (ArrayIndexOutOfBoundsException aioobe) {
								// System.out.println("index out of bounds at
								// column "+colnr+" : "+aioobe.toString());
								stop = true;
							}
						}
					}
					sheet = null;
				}

				workbook.close();
			} catch (Exception e) {
				logger.error("无法读取Excel文件[{" + KettleVFS.getFilename(file) + "}].\n  请检查文件, 目录和表达式.\n{" + e.toString()
						+ "}");
			}
			// 相同名称的sheet结构都是一致的，所以只取第一个文件后就跳出循环
			break;
		}

		if (fields.size() > 0) {
			for (int j = 0; j < fields.size(); j++) {
				ValueMetaInterface field = fields.getValueMeta(j);
			}
		} else {
			logger.error("警告：无法在Excel文件里找到任何字段！");
		}
	}

	public ExcelInputMeta() {
		super(); // allocate BaseStepMeta
	}

	/**
	 * @return Returns the fieldLength.
	 */
	public ExcelInputField[] getField() {
		return field;
	}

	/**
	 * @param fields
	 *            The excel input fields to set.
	 */
	public void setField(ExcelInputField[] fields) {
		this.field = fields;
	}

	/**
	 * @return Returns the fileField.
	 */
	public String getFileField() {
		return fileField;
	}

	/**
	 * @param fileField
	 *            The fileField to set.
	 */
	public void setFileField(String fileField) {
		this.fileField = fileField;
	}

	/**
	 * @return Returns the ignoreEmptyRows.
	 */
	public boolean ignoreEmptyRows() {
		return ignoreEmptyRows;
	}

	/**
	 * @param ignoreEmptyRows
	 *            The ignoreEmptyRows to set.
	 */
	public void setIgnoreEmptyRows(boolean ignoreEmptyRows) {
		this.ignoreEmptyRows = ignoreEmptyRows;
	}

	/**
	 * @return Returns the rowLimit.
	 */
	public long getRowLimit() {
		return rowLimit;
	}

	/**
	 * @param rowLimit
	 *            The rowLimit to set.
	 */
	public void setRowLimit(long rowLimit) {
		this.rowLimit = rowLimit;
	}

	/**
	 * @return Returns the rowNumberField.
	 */
	public String getRowNumberField() {
		return rowNumberField;
	}

	/**
	 * @param rowNumberField
	 *            The rowNumberField to set.
	 */
	public void setRowNumberField(String rowNumberField) {
		this.rowNumberField = rowNumberField;
	}

	/**
	 * @return Returns the sheetRowNumberField.
	 */
	public String getSheetRowNumberField() {
		return sheetRowNumberField;
	}

	/**
	 * @param rowNumberField
	 *            The rowNumberField to set.
	 */
	public void setSheetRowNumberField(String rowNumberField) {
		this.sheetRowNumberField = rowNumberField;
	}

	/**
	 * @return Returns the sheetField.
	 */
	public String getSheetField() {
		return sheetField;
	}

	/**
	 * @param sheetField
	 *            The sheetField to set.
	 */
	public void setSheetField(String sheetField) {
		this.sheetField = sheetField;
	}

	/**
	 * @return Returns the sheetName.
	 */
	public String[] getSheetName() {
		return sheetName;
	}

	/**
	 * @param sheetName
	 *            The sheetName to set.
	 */
	public void setSheetName(String[] sheetName) {
		this.sheetName = sheetName;
	}

	/**
	 * @return Returns the startColumn.
	 */
	public int[] getStartColumn() {
		return startColumn;
	}

	/**
	 * @param startColumn
	 *            The startColumn to set.
	 */
	public void setStartColumn(int[] startColumn) {
		this.startColumn = startColumn;
	}

	/**
	 * @return Returns the startRow.
	 */
	public int[] getStartRow() {
		return startRow;
	}

	/**
	 * @param startRow
	 *            The startRow to set.
	 */
	public void setStartRow(int[] startRow) {
		this.startRow = startRow;
	}

	/**
	 * @return Returns the startsWithHeader.
	 */
	public boolean startsWithHeader() {
		return startsWithHeader;
	}

	/**
	 * @param startsWithHeader
	 *            The startsWithHeader to set.
	 */
	public void setStartsWithHeader(boolean startsWithHeader) {
		this.startsWithHeader = startsWithHeader;
	}

	/**
	 * @return Returns the stopOnEmpty.
	 */
	public boolean stopOnEmpty() {
		return stopOnEmpty;
	}

	/**
	 * @param stopOnEmpty
	 *            The stopOnEmpty to set.
	 */
	public void setStopOnEmpty(boolean stopOnEmpty) {
		this.stopOnEmpty = stopOnEmpty;
	}

	public String[] getFileName() {
		return fileName;
	}

	public void setFileName(String[] fileName) {
		this.fileName = fileName;
	}

	public String[] getIncludeSubFolders() {
		return includeSubFolders;
	}

	public void setIncludeSubFolders(String[] includeSubFolders) {
		this.includeSubFolders = includeSubFolders;
	}

	public boolean isStrictTypes() {
		return strictTypes;
	}

	public void setStrictTypes(boolean strictTypes) {
		this.strictTypes = strictTypes;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isStartsWithHeader() {
		return startsWithHeader;
	}

	public boolean isStopOnEmpty() {
		return stopOnEmpty;
	}

	public boolean isIgnoreEmptyRows() {
		return ignoreEmptyRows;
	}

	public SpreadSheetType getSpreadSheetType() {
		return spreadSheetType;
	}

	public void setSpreadSheetType(SpreadSheetType spreadSheetType) {
		this.spreadSheetType = spreadSheetType;
	}

}