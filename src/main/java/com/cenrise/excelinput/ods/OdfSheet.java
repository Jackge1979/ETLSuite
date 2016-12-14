package com.cenrise.excelinput.ods;

import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

import com.cenrise.spreadsheet.KCell;
import com.cenrise.spreadsheet.KSheet;

public class OdfSheet implements KSheet {
  private OdfTable table;
  private int nrOfRows;
  
  public OdfSheet(OdfTable table) {
    this.table = table;
    
    //2012-05-25 , modified by jiayf , 修改获取行数的方法
    //int size = table.getOdfElement().getChildNodes().getLength();
    int size =table.getRowCount();
    
    //2012-08-27,removed by jiayf ,此段代码会导致内存泄露，并且获取行数也没有必要以这种方式循环获取。#TIETL-1025
//    int rowNr = 0;
//    int maxIndex=0;
//    OdfTableRow row = table.getRowByIndex(rowNr);
//    row = row.getNextRow();
//    rowNr++;
//    while (rowNr<size) {
//      int cols = findNrColumns(row);
//      if (cols>0) maxIndex=rowNr;
//      row = row.getNextRow();
//      rowNr++;
//    }
   
    nrOfRows = size;
  }
  
  private int findNrColumns(OdfTableRow row) {
    return row.getOdfElement().getChildNodes().getLength();
  }
  
  public String getName() {
    return table.getTableName();
  }
  
  public KCell[] getRow(int rownr) {
    if (rownr>=nrOfRows) {
      throw new ArrayIndexOutOfBoundsException("Read beyond last row: "+rownr);
    }
    OdfTableRow row = table.getRowByIndex(rownr);
    int cols = findNrColumns(row);
    OdfCell[] xlsCells = new OdfCell[cols];
    for (int i=0;i<cols;i++) {
      OdfTableCell cell = row.getCellByIndex(i);
      if (cell!=null) {
        xlsCells[i] = new OdfCell(cell);
      }
    }
    return xlsCells;
  }
  
  public int getRows() {
    return nrOfRows;
  }
  
  public KCell getCell(int colnr, int rownr) {
    OdfTableCell cell = table.getCellByPosition(colnr, rownr);
    if (cell==null) return null;
    return new OdfCell(cell);
  }
}
