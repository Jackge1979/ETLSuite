package com.cenrise.excelinput;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import com.cenrise.exception.BaseException;
import com.cenrise.meta.RowMeta;
import com.cenrise.meta.RowMetaInterface;
import com.cenrise.meta.ValueMeta;
import com.cenrise.meta.ValueMetaInterface;
import com.cenrise.spreadsheet.KCell;
import com.cenrise.spreadsheet.KCellType;
import com.cenrise.spreadsheet.KSheet;
import com.cenrise.spreadsheet.KWorkbook;
import com.cenrise.step.FileInputList;
import com.cenrise.step.StepMetaInjectionInterface;
import com.cenrise.step.WorkbookFactory;
import com.cenrise.util.Const;
import com.cenrise.util.Context;
import com.cenrise.util.PropertiesParserUtils;
import com.cenrise.variables.VariableSpace;
import com.cenrise.variables.Variables;
import com.cenrise.vfs.KettleVFS;

/**
 * 
 * @ClassName: ExcelInput
 * @Description: Excel文件解析核心类
 * @author dongpo.jia
 * @date 2016年12月12日
 *
 */
public class ExcelInput {
	private static final Log logger = LogFactory.getLog(ExcelInput.class);
	private ExcelInputMeta meta;
	private ExcelInputData data;
	private int size = 0;
	// 添加空行计数器，因为openoffice提供的api获取行数信息不准确，所以只能通过连续的空行数量来判断是否到文件尾部。
	private int emptyRowCount;
	// 最大空行数量
	private static final int MAX_EMPTY_ROW_COUNT = 100;
	// 首次处理，用于一些初始数据
	public boolean first = true;
	Context context = new Context();

	private Object[] fillRow(int startcolumn, ExcelInputRow excelInputRow) throws Exception {
		Object[] r = new Object[data.outputRowMeta.size()];
		data.errorRowValue = new String[data.outputRowMeta.size()];

		// Set values in the row...
		KCell cell = null;
		// excelInputRow存的是excel文件一行的值，需要从这里面取需要的字段值。ExcelInputField增加了一属性index，就是存储字段在excel中的序号,#TIETL-1026
		ExcelInputField[] vmiList = meta.getField();
		int size = vmiList.length;
		for (int i = 0; i < size; i++) {
			int rowcolumn = i + data.nrPassThruFields;
			ExcelInputField vmi = vmiList[i];

			ValueMetaInterface targetMeta = data.outputRowMeta.getValueMeta(rowcolumn);
			ValueMetaInterface sourceMeta = null;

			// 处理空行及有空值的单元格。
			if (excelInputRow.cells.length == 0 || vmi.getIndex() >= excelInputRow.cells.length)
				cell = null;
			else
				cell = excelInputRow.cells[vmi.getIndex()];
			if (cell == null) {
				r[rowcolumn] = null;
				continue;
			}

			try {
				checkType(cell, targetMeta);
			} catch (Exception ex) {
				data.errorRow = true;
				data.errorDesc = data.errorDesc + ex.getMessage() + "||";
			}
			data.errorRowValue[rowcolumn] = cell.getContents();

			KCellType cellType = cell.getType();
			if (KCellType.BOOLEAN == cellType || KCellType.BOOLEAN_FORMULA == cellType) {
				r[rowcolumn] = (Boolean) cell.getValue();
				sourceMeta = data.valueMetaBoolean;
			} else {
				if (KCellType.DATE.equals(cellType) || KCellType.DATE_FORMULA.equals(cellType)) {
					Date date = (Date) cell.getValue();
					long time = date.getTime();
					int offset = TimeZone.getDefault().getOffset(time);
					r[rowcolumn] = new Date(time - offset);
					sourceMeta = data.valueMetaDate;
				} else {
					if (KCellType.LABEL == cellType || KCellType.STRING_FORMULA == cellType) {
						String string = (String) cell.getValue();
						string = excelHandler(string);

						r[rowcolumn] = string;
						sourceMeta = data.valueMetaString;
					} else {
						if (KCellType.NUMBER == cellType || KCellType.NUMBER_FORMULA == cellType) {
							r[rowcolumn] = (Double) cell.getValue();
							sourceMeta = data.valueMetaNumber;
						} else {
							logger.info("未知类型 : " + cell.getType().toString() + " : [" + cell.getContents() + "]");
							r[rowcolumn] = null;
						}
					}
				}
			}

			ExcelInputField field = meta.getField()[i];

			// Change to the appropriate type if needed...
			try {
				// Null stays null folks.
				if (sourceMeta != null && sourceMeta.getType() != targetMeta.getType() && r[rowcolumn] != null) {
					ValueMetaInterface sourceMetaCopy = sourceMeta.clone();
					sourceMetaCopy.setConversionMask(field.getFormat());
					sourceMetaCopy.setGroupingSymbol(field.getGroupSymbol());
					sourceMetaCopy.setDecimalSymbol(field.getDecimalSymbol());
					sourceMetaCopy.setCurrencySymbol(field.getCurrencySymbol());

					switch (targetMeta.getType()) {
					// Use case: we find a numeric value: convert it using the
					// supplied format to the desired data type...
					//
					case ValueMetaInterface.TYPE_NUMBER:
					case ValueMetaInterface.TYPE_INTEGER:
						switch (field.getType()) {
						case ValueMetaInterface.TYPE_DATE:
							// number to string conversion (20070522.00 -->
							// "20070522")
							ValueMetaInterface valueMetaNumber = new ValueMeta("num", ValueMetaInterface.TYPE_NUMBER);
							valueMetaNumber.setConversionMask("#");
							Object string = sourceMetaCopy.convertData(valueMetaNumber, r[rowcolumn]);

							// String to date with mask...
							//
							r[rowcolumn] = targetMeta.convertData(sourceMetaCopy, string);
							break;
						default:
							r[rowcolumn] = targetMeta.convertData(sourceMetaCopy, r[rowcolumn]);
							break;
						}
						break;
					// Use case: we find a date: convert it using the supplied
					// format to String...
					//
					default:
						r[rowcolumn] = targetMeta.convertData(sourceMetaCopy, r[rowcolumn]);
					}
				}
			} catch (Exception ex) {
				// TODO
			}
		}

		int rowIndex = meta.getField().length + data.nrPassThruFields;

		// Do we need to include the filename?
		if (!Const.isEmpty(meta.getFileField())) {
			r[rowIndex] = data.filename;
			data.errorRowValue[rowIndex] = data.filename;
			rowIndex++;
		}

		// Do we need to include the sheetname?
		if (!Const.isEmpty(meta.getSheetField())) {
			r[rowIndex] = excelInputRow.sheetName;
			data.errorRowValue[rowIndex] = excelInputRow.sheetName;
			rowIndex++;
		}

		// Do we need to include the sheet rownumber?
		if (!Const.isEmpty(meta.getSheetRowNumberField())) {
			r[rowIndex] = new Long(data.rownr);
			rowIndex++;
		}

		return r;
	}

	private static int byte2int(byte n) {
		int m = n;
		if (n < 0) {
			m = n + 256;
			return m;
		}
		return m;
	}

	private static byte int2byte(int n) {
		byte m = (byte) n;
		if (n > 127) {
			m = (byte) (n - 256);
			return m;
		}
		return m;
	}

	private static byte[] getOutputList(byte[] m) { // 过滤byte数组中为0的元素
		int len = m.length;
		if (len > 0) {
			StringBuffer temp = new StringBuffer(10240);
			for (int i = 0; i < len; i++) {
				if (m[i] != 0) {
					temp.append(m[i]);
					temp.append(",");
				}
			}

			String[] stings = temp.toString().split(",");
			int total = stings.length;
			byte[] newList = new byte[total];
			for (int i = 0; i < total; i++) {
				newList[i] = (byte) Integer.parseInt(stings[i]);
			}
			return newList;
		}

		return null;

	}

	private String excelHandler(String originalString) {
		// 将ascii码值为160的空格替换为常见的ascii值为32的空格
		if (originalString != null) {
			return originalString.replace(String.valueOf((char) 160), " ");
		} else {
			return originalString;
		}
	}

	private void checkType(KCell cell, ValueMetaInterface v) throws Exception {
		if (!meta.isStrictTypes())
			return;
		switch (cell.getType()) {
		case BOOLEAN: {
			if (!(v.getType() == ValueMetaInterface.TYPE_STRING || v.getType() == ValueMetaInterface.TYPE_NONE
					|| v.getType() == ValueMetaInterface.TYPE_BOOLEAN))
				throw new Exception("无效的类型 Boolean, 期望的类型 " + v.getTypeDesc());
		}
			break;
		case DATE: {
			if (!(v.getType() == ValueMetaInterface.TYPE_STRING || v.getType() == ValueMetaInterface.TYPE_NONE
					|| v.getType() == ValueMetaInterface.TYPE_DATE))
				throw new Exception("无效的类型 Date: " + cell.getContents() + ", 期望的类型" + v.getTypeDesc());
		}
			break;
		case LABEL: {
			if (v.getType() == ValueMetaInterface.TYPE_BOOLEAN || v.getType() == ValueMetaInterface.TYPE_DATE
					|| v.getType() == ValueMetaInterface.TYPE_INTEGER || v.getType() == ValueMetaInterface.TYPE_NUMBER)
				throw new Exception("无效的类型 Label: " + cell.getContents() + ", 期望的类型 " + v.getTypeDesc());
		}
			break;
		case EMPTY: {
			// ok
		}
			break;
		case NUMBER: {
			if (!(v.getType() == ValueMetaInterface.TYPE_STRING || v.getType() == ValueMetaInterface.TYPE_NONE
					|| v.getType() == ValueMetaInterface.TYPE_INTEGER
					|| v.getType() == ValueMetaInterface.TYPE_BIGNUMBER
					|| v.getType() == ValueMetaInterface.TYPE_NUMBER))
				throw new Exception("无效的类型 Number: " + cell.getContents() + ", 期望的类型 " + v.getTypeDesc());
		}
			break;
		default:
			throw new Exception("不支持带有值 " + cell.getType().getDescription() + " 的类型 " + cell.getContents());
		}
	}

	/**
	 * 
	    * @Title: processRow  
	    * @Description:解析入口
	    * @param @return
	    * @param @throws Exception    参数  
	    * @return boolean    返回类型  
	    * @throws
	 */
	public boolean processRow() throws Exception {

		if (first) {
			first = false;
			data.outputRowMeta = new RowMeta(); // start from scratch!
			// handleMissingFiles();
			size = 0;
		} else {
			size++;
		}

		// 修复“最大解析行数”数量不准确的bug
		if ((meta.getRowLimit() > 0 && data.rownr > meta.getRowLimit())
				|| (meta.getRowLimit() > 0 && (size + 1) > meta.getRowLimit()) || (emptyRowCount >= MAX_EMPTY_ROW_COUNT
						&& meta.getSpreadSheetType() == SpreadSheetType.ODS && data.filenr >= data.files.nrOfFiles())) {
			// The close of the openFile is in dispose()
			logger.info("已达到[" + meta.getRowLimit() + "]的记录限制: 停止处理");
			return false;
		}

		Object[] r = getRowFromWorkbooks();
		if (r != null) {
			// OK, see if we need to repeat values.
			if (data.previousRow != null) {
				for (int i = 0; i < meta.getField().length; i++) {
					int rownum = i + data.nrPassThruFields;
					ValueMetaInterface valueMeta = data.outputRowMeta.getValueMeta(rownum);
					Object valueData = r[rownum];

					if (valueMeta.isNull(valueData) && meta.getField()[i].isRepeated()) {
						// Take the value from the previous row.
						r[rownum] = data.previousRow[rownum];
					}
				}
			}

			// Remember this row for the next time around!
			data.previousRow = data.outputRowMeta.cloneRow(r);

			return true;
		} else {
			size--;
			emptyRowCount++;
			return true;
		}
	}

	private String getStringFromRow(RowMetaInterface outputRowMeta, String[] errorRowValue) {
		StringBuffer buffer = new StringBuffer();
		String logRow = null;
		for (int i = 0; i < errorRowValue.length; i++) {
			// 当字段类型为Binary时，在日志中不打印其内容，仅打印“Binary Data”
			ValueMetaInterface meta = outputRowMeta.getValueMeta(i);
			if (ValueMetaInterface.TYPE_BINARY == meta.getType()) {
				logRow = "Binary Data";
			} else {
				logRow = errorRowValue[i];
			}

			if (i > 0)
				buffer.append(",");
			buffer.append(logRow);
		}
		if (data.errorDesc != null) {
			buffer.append(",\"");
			String error = data.errorDesc.replaceAll("\r|\n|\r\n", "");
			buffer.append(error);
			buffer.append("\"");
		}
		return buffer.toString();

	}

	/**
	 * 
	* @Title: handleMissingFiles  
	* @Description: 指定文件缺失的处理
	* @param @throws Exception    参数  
	* @return void    返回类型  
	* @throws
	 */
	private void handleMissingFiles() throws Exception {
		List<FileObject> nonExistantFiles = data.files.getNonExistantFiles();

		if (nonExistantFiles.size() != 0) {
			String message = FileInputList.getRequiredFilesDescription(nonExistantFiles);
			logger.info("需要的文件" + "警告:没有 " + message);
		}

		List<FileObject> nonAccessibleFiles = data.files.getNonAccessibleFiles();
		if (nonAccessibleFiles.size() != 0) {
			String message = FileInputList.getRequiredFilesDescription(nonAccessibleFiles);
			logger.info("需要的文件" + "警告: 无法访问 " + message);
			throw new Exception("下列文件不可以访问: " + message + "\r\n");

		}
	}

	/**
	 * 
	    * @Title: getRowFromWorkbooks  
	    * @Description: 一条数据行
	    * @param @return
	    * @param @throws Exception    参数  
	    * @return Object[]    返回类型  
	    * @throws
	 */
	public Object[] getRowFromWorkbooks() throws Exception {
		Object[] retval = null;
		data.errorRow = false;
		data.errorDesc = "";
		try {
			// First, see if a file has been opened?
			if (data.workbook == null) {
				// Open a new openFile..
				// data.file = data.files.getFile(data.filenr);
				data.filename = KettleVFS.getFilename(data.file);
				// Add additional fields?

				logger.info("打开文件 #" + data.filenr + " : " + data.filename);
				try {
					data.workbook = WorkbookFactory.getWorkbook(meta.getSpreadSheetType(), data.filename,
							meta.getEncoding());

				} catch (Exception e) {// 添加格式错误的文件的处理方式
					logger.error("错误处理 Excel 文件 [" + data.filename + "] 中的行 : " + e.toString(), e);
					dispose();
					return null;
				}
				// See if we have sheet names to retrieve, otherwise we'll have
				// to get all sheets...
				data.sheetNames = data.workbook.getSheetNames();
				data.startColumn = new int[data.sheetNames.length];
				data.startRow = new int[data.sheetNames.length];
				for (int i = 0; i < data.sheetNames.length; i++) {
					data.startColumn[i] = data.defaultStartColumn;
					data.startRow[i] = data.defaultStartRow;
				}
			}

			boolean nextsheet = false;

			// What sheet were we handling?
			logger.debug("获取表单 #" + data.filenr + "." + data.sheetnr);

			String sheetName = data.sheetNames[data.sheetnr];
			KSheet sheet = data.workbook.getSheet(data.sheetNames[data.sheetnr]);
			if (sheet != null) {
				// at what row do we continue reading?
				if (data.rownr < 0) {
					data.rownr = data.startRow[data.sheetnr];

					// Add an extra row if we have a header row to skip...
					if (meta.startsWithHeader()) {
						data.rownr++;
					}
				}
				// Start at the specified column
				data.colnr = data.startColumn[data.sheetnr];

				// Build a new row and fill in the data from the sheet...
				try {
					KCell line[] = sheet.getRow(data.rownr);
					// Already increase cursor 1 row
					int lineNr = ++data.rownr;
					// Excel starts counting at 0
					logger.info("从表单 #" + lineNr + " 获取行 # " + data.filenr + "." + data.sheetnr);
					logger.info("读取" + line.length + "个单元格的行");

					ExcelInputRow excelInputRow = new ExcelInputRow(sheet.getName(), lineNr, line);
					Object[] r = fillRow(data.colnr, excelInputRow);
					logger.info("将Excel中的行转换成记录 #" + lineNr + " : " + data.outputRowMeta.getString(r));

					boolean isEmpty = isLineEmpty(line);
					if (!isEmpty || !meta.ignoreEmptyRows()) {
						// Put the row
						retval = r;
					} else {

						if (data.rownr > sheet.getRows()) {
							nextsheet = true;
						}
					}

					if (isEmpty && meta.stopOnEmpty()) {
						nextsheet = true;
					}

				} catch (ArrayIndexOutOfBoundsException e) {
					logger.error("索引越界: 移到下一个表单!");

					// We tried to read below the last line in the sheet.
					// Go to the next sheet...
					nextsheet = true;
				}
			} else {
				nextsheet = true;
			}

			if (nextsheet) {
				// Go to the next sheet
				data.sheetnr++;

				// Reset the start-row:
				data.rownr = -1;

				// no previous row yet, don't take it from the previous sheet!
				// (that whould be plain wrong!)
				data.previousRow = null;

				// Perhaps it was the last sheet?
				if (data.sheetnr >= data.sheetNames.length) {
					jumpToNextFile();
				}
			}
		} catch (Exception e) {
			logger.error("错误处理 Excel 文件 [" + data.filename + "" + "] 中的行 : " + e.toString(), e);
			dispose();
			return null;
		}
		if (retval != null && data.passThruFields != null) {
			// Simply add all fields from source files step
			for (int i = 0; i < data.nrPassThruFields; i++) {
				retval[i] = data.passThruFields.get(data.file)[i];
			}
		}
		return retval;
	}

	private boolean isLineEmpty(KCell[] line) {
		if (line.length == 0)
			return true;

		boolean isEmpty = true;
		for (int i = 0; i < line.length && isEmpty; i++) {
			if (line[i] != null && !Const.isEmpty(line[i].getContents()))
				isEmpty = false;
		}
		return isEmpty;
	}

	private void jumpToNextFile() throws Exception {
		data.sheetnr = 0;

		// Reset the start-row:
		data.rownr = -1;

		// no previous row yet, don't take it from the previous sheet! (that
		// whould be plain wrong!)
		data.previousRow = null;

		// Close the openFile!
		data.workbook.close();
		data.workbook = null; // marker to open again.

		// advance to the next file!
		data.filenr++;
	}

	/**
	 * 
	    * @Title: dispose  
	    * @Description: 最好资源清理
	    * @param     参数  
	    * @return void    返回类型  
	    * @throws
	 */
	public void dispose() {
		if (data.workbook != null)
			data.workbook.close();
		if (data.file != null) {
			try {
				data.file.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	    * @Title: init  
	    * @Description: 初始化变量、读取配置
	    * @param @return    参数  
	    * @return boolean    返回类型  
	    * @throws
	 */
	public boolean init() {

		meta = new ExcelInputMeta();
		data = new ExcelInputData();

		PropertiesParserUtils ppu = new PropertiesParserUtils();
		
		File file = new File(
				"//Users//yp-tc-m-2684//workspace_all//etlNeon//ExcelInput//src//main//resources//pros//MU_B2T.properties");
		Map<String, String> maps;
		try {
			maps = ppu.parse(file);
			context.putAll(maps);
		} catch (ParseException e) {
			logger.error("文件解析失败！" + file == null ? "" : file.getName());
			e.printStackTrace();
		}
		// System.out.println(maps.get("encoding"));

		String files = "/Users/yp-tc-m-2684/Downloads/B2T/20160905142026sale.xls";
		// logger.info("停止处理,没有指定文件!");
		FileObject directoryFileObject = KettleVFS.getFileObject(files);

		try {
			if (!directoryFileObject.exists()) {
				logger.info("停止处理,没有指定文件!");
				return false;
			} else {
				if (!directoryFileObject.isReadable()) {
					logger.info("不能读取指定的文件！" + "文件名：" + directoryFileObject.getName());
					return false;
				}
			}
		} catch (FileSystemException e) {
			logger.error("文件处理处理失败！");
			e.printStackTrace();
			return false;
		}

		data.file = directoryFileObject;
		String name = KettleVFS.getFilename(directoryFileObject);

		// -------------------meta配置赋值开始！---------------------
		
		try {
			// 注入
			StepMetaInjectionInterface injectionInterface = new ExcelInputMetaInjection(meta);
			List<StepInjectionMetaEntry> metadataEntries = injectionInterface.getStepInjectionMetadataEntries();

			// 手动设置
			for (StepInjectionMetaEntry lookFields : metadataEntries) {
				for (StepInjectionMetaEntry lookField : lookFields.getDetails()) {
					List<StepInjectionMetaEntry> entries = lookField.getDetails();
					for (StepInjectionMetaEntry entry : entries) {
						// TODO 字段的信息设置
						// entry.setKey(key);
						// entry.setValue(value);
					}
				}
			}

			injectionInterface.injectStepMetadataEntries(metadataEntries);
		} catch (BaseException e) {
			logger.error("元数据信息注入错误！", e);
			e.printStackTrace();
		}
		// -------------------meta配置赋值结束！---------------------

		// -------------------data运行时数据赋值开始！---------------------
		if (meta.getStartRow().length == 1) {
			data.defaultStartRow = meta.getStartRow()[0];
		} else {
			data.defaultStartRow = 0;
		}
		if (meta.getStartColumn().length == 1) {
			data.defaultStartColumn = meta.getStartColumn()[0];
		} else {
			data.defaultStartColumn = 0;
		}
		// -------------------data运行时数据赋值开始！---------------------

		return true;
	}
	
/**
 * 
    * @Title: getInfo  
    * @Description: 各个配置的数据内容
    * @param @param meta    参数  
    * @return void    返回类型  
    * @throws
 */
	private void getInfo(ExcelInputMeta meta) {

		Context encryptionContext = new Context(context.getSubProperties("MU_B2T.EXCEL."));

		Const.ensureRequiredNonNull(encryptionContext, "startRow");
		int[] startRows = new int[1];
		// TODO 默认只处理一个文件
		startRows[0] = encryptionContext.getInteger("startRow", 0);
		meta.setStartRow(startRows);

		Const.ensureRequiredNonNull(encryptionContext, "startColumn");
		int[] startColumns = new int[1];
		startColumns[0] = encryptionContext.getInteger("startColumn", 0);
		meta.setStartColumn(startColumns);
		
		List<ExcelInputField> excelInputFields = new ArrayList<ExcelInputField>();
		meta.setField(excelInputFields.toArray(new ExcelInputField[excelInputFields.size()]));

		Const.ensureRequiredNonNull(encryptionContext, "rowLimit");
		meta.setRowLimit(encryptionContext.getInteger("rowLimit"));
		Const.ensureRequiredNonNull(encryptionContext, "encoding");
		meta.setEncoding(encryptionContext.getString("encoding"));
		Const.ensureRequiredNonNull(encryptionContext, "spreadSheetType");
		SpreadSheetType spreadSheetType = SpreadSheetType
				.getStpreadSheetTypeByName(encryptionContext.getString("spreadSheetType"));
		meta.setSpreadSheetType(spreadSheetType != null ? SpreadSheetType.values()[spreadSheetType.ordinal()]
				: SpreadSheetType.values()[SpreadSheetType.JXL.ordinal()]);
		
//		meta.setFileField(wInclFilenameField.getText());
//		meta.setSheetField(wInclSheetnameField.getText());
//		meta.setSheetRowNumberField(wInclSheetRownumField.getText());
//		meta.setRowNumberField(wInclRownumField.getText());

//		meta.setAddResultFile(wAddResult.getSelection());
		
		Const.ensureRequiredNonNull(encryptionContext, "startsWithHeader");
		meta.setStartsWithHeader(encryptionContext.getBoolean("startsWithHeader"));
		Const.ensureRequiredNonNull(encryptionContext, "ignoreEmptyRows");
		meta.setIgnoreEmptyRows(encryptionContext.getBoolean("ignoreEmptyRows"));
		Const.ensureRequiredNonNull(encryptionContext, "stopOnEmpty");
		meta.setStopOnEmpty(encryptionContext.getBoolean("stopOnEmpty"));
		

//		meta.setAcceptingFilenames(wAccFilenames.getSelection());
//		meta.setPassingThruFields(wPassThruFields.getSelection());
//		meta.setAcceptingField(wAccField.getText());

//		int nrfiles = wFilenameList.nrNonEmpty();
//		int nrsheets = wSheetnameList.nrNonEmpty();
//		int nrfields = wFields.nrNonEmpty();
//		meta.allocate(nrfiles, nrsheets, nrfields);

		
		/* TODO
		 Const.ensureRequiredNonNull(encryptionContext, "fileName");
		meta.setFileName(encryptionContext.getString("fileName"));
		Const.ensureRequiredNonNull(encryptionContext, "fileMask");
		meta.setFileMask(encryptionContext.getBoolean("fileMask"));
		Const.ensureRequiredNonNull(encryptionContext, "excludeFileMask");
		meta.setExcludeFileMask(encryptionContext.getBoolean("excludeFileMask"));
		meta.setFileName(wFilenameList.getItems(0));
		meta.setFileMask(wFilenameList.getItems(1));
		meta.setExcludeFileMask(wFilenameList.getItems(2));
		meta.setFileRequired(wFilenameList.getItems(3));
		meta.setIncludeSubFolders(wFilenameList.getItems(4));

		for (int i = 0; i < nrsheets; i++) {
			TableItem item = wSheetnameList.getNonEmpty(i);
			meta.getSheetName()[i] = item.getText(1);
			meta.getStartRow()[i] = Const.toInt(item.getText(2), 0);
			meta.getStartColumn()[i] = Const.toInt(item.getText(3), 0);
		}

		for (int i = 0; i < nrfields; i++) {
			TableItem item = wFields.getNonEmpty(i);
			meta.getField()[i] = new ExcelInputField();

			meta.getField()[i].setName(item.getText(1));
			meta.getField()[i].setType(ValueMeta.getType(item.getText(2)));
			String slength = item.getText(3);
			String sprec = item.getText(4);
			meta.getField()[i].setTrimType(ExcelInputMeta.getTrimTypeByDesc(item.getText(5)));
			meta.getField()[i]
					.setRepeated(BaseMessages.getString(PKG, "System.Combo.Yes").equalsIgnoreCase(item.getText(6)));

			meta.getField()[i].setLength(Const.toInt(slength, -1));
			meta.getField()[i].setPrecision(Const.toInt(sprec, -1));

			meta.getField()[i].setFormat(item.getText(7));
			meta.getField()[i].setCurrencySymbol(item.getText(8));
			meta.getField()[i].setDecimalSymbol(item.getText(9));
			meta.getField()[i].setGroupSymbol(item.getText(10));
			meta.getField()[i].setIndex(Const.toInt(item.getText(11), -1));
		}

		// Error handling fields...
		meta.setStrictTypes(wStrictTypes.getSelection());
		meta.setErrorIgnored(wErrorIgnored.getSelection());
		meta.setErrorLineSkipped(wSkipErrorLines.getSelection());
		// 2012-08-30, removed by jiayf , 去掉输出告警文件的组件。#TIETL-1069
		// meta.setWarningFilesDestinationDirectory( wWarningDestDir.getText()
		// );
		// meta.setBadLineFilesExtension( wWarningExt.getText() );
		meta.setErrorFilesDestinationDirectory(wErrorDestDir.getText());
		meta.setErrorFilesExtension(wErrorExt.getText());
		meta.setLineNumberFilesDestinationDirectory(wLineNrDestDir.getText());
		meta.setLineNumberFilesExtension(wLineNrExt.getText());
		meta.setShortFileNameField(wShortFileFieldName.getText());
		meta.setPathField(wPathFieldName.getText());
		meta.setIsHiddenField(wIsHiddenName.getText());
		meta.setLastModificationDateField(wLastModificationTimeName.getText());
		meta.setUriField(wUriName.getText());
		meta.setRootUriField(wRootUriName.getText());
		meta.setExtensionField(wExtensionFieldName.getText());
		meta.setSizeField(wSizeFieldName.getText());*/
	}


	public static void main(String args[]) throws Exception {
		// 1. 初始化配置
		ExcelInput excelInput = new ExcelInput();
		if (excelInput.init()) {
			excelInput.processRow();
		}

		// 2. 处理excel

		// 3. 销毁
	}

}