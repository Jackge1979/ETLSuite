
package com.cenrise.excelinput;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**  
    * @ClassName: SheetFieldMeta  
    * @Description: sheet的元数据
    * @author dongpo.jia  
    * @date 2016年12月12日  
    *    
    */

public class SheetFieldMeta {
	private Map<String, Integer[]> fieldMetaMap = new HashMap<String, Integer[]>();

	public void putFieldMeta(String name, Integer seq, Integer type) {
		fieldMetaMap.put(name, new Integer[] { seq, type });
	}

	public int getSeq(String name) {
		return fieldMetaMap.get(name)[0];
	}

	public int getType(String name) {
		return fieldMetaMap.get(name)[1];
	}

	public int getSize() {
		return fieldMetaMap.size();
	}

	public boolean containsName(String name) {
		return fieldMetaMap.containsKey(name);
	}

	public boolean compare(SheetFieldMeta meta) {
		if (getSize() != meta.getSize())
			return false;
		Iterator<String> iter = fieldMetaMap.keySet().iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			if (!meta.containsName(name))
				return false;
			if (getSeq(name) != meta.getSeq(name) || getType(name) != meta.getType(name))
				return false;
		}

		return true;
	}

}
