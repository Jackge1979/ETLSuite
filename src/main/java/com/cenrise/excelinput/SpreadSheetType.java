package com.cenrise.excelinput;

/**
 * 
 * @ClassName: SpreadSheetType
 * @Description: excel三种格式
 * @author dongpo.jia
 * @date 2016年12月12日
 *
 */
public enum SpreadSheetType {
	JXL("Excel 97-2003 XLS (JXL)"), POI("Excel 2007 XLSX (Apache POI)"), ODS(
			"Open Office ODS (ODFDOM)"), ;

	private String description;

	/**
	 * @param description
	 */
	private SpreadSheetType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	/**
	 * 
	    * @Title: getStpreadSheetTypeByDescription  
	    * @Description: 通过值内容比较  
	    * @param @param description
	    * @param @return    参数  
	    * @return SpreadSheetType    返回类型  
	    * @throws
	 */
	public static SpreadSheetType getStpreadSheetTypeByName(
			String name) {
		for (SpreadSheetType type : values()) {
			if ( type .equals( SpreadSheetType.valueOf(name))) {
				return type;
			}
		}
		return null;
	}
	
	/**
	 * 
	    * @Title: getStpreadSheetTypeByDescription  
	    * @Description: 通过值内容比较  
	    * @param @param description
	    * @param @return    参数  
	    * @return SpreadSheetType    返回类型  
	    * @throws
	 */
	public static SpreadSheetType getStpreadSheetTypeByDescription(
			String description) {
		for (SpreadSheetType type : values()) {
			if (type.getDescription().equalsIgnoreCase(description)) {
				return type;
			}
		}
		return null;
	}
	public static void main(String[] args) {
//		meta.setSpreadSheetType(spreadSheetType != null ? SpreadSheetType.values()[spreadSheetType.ordinal()]
//				: SpreadSheetType.values()[SpreadSheetType.JXL.ordinal()]);
		ExcelInputMeta meta  = new ExcelInputMeta();
		System.out.println(SpreadSheetType.JXL.ordinal());
		System.out.println(SpreadSheetType.values()[SpreadSheetType.JXL.ordinal()]);
		System.out.println(SpreadSheetType.JXL);
		meta.setSpreadSheetType(SpreadSheetType.JXL);
		SpreadSheetType spreadSheetType = 	SpreadSheetType.JXL;
		meta.setSpreadSheetType(spreadSheetType != null ? SpreadSheetType.values()[spreadSheetType.ordinal()]
				: SpreadSheetType.values()[SpreadSheetType.JXL.ordinal()]);
		
		
		 
	}
}
